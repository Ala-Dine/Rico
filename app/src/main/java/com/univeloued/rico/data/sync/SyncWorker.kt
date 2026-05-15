package com.univeloued.rico.data.sync

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.univeloued.rico.data.local.dao.*
import com.univeloued.rico.data.local.entity.*
import com.univeloued.rico.data.remote.SupabaseRemoteDataSource
import com.univeloued.rico.data.remote.dto.*
import com.univeloued.rico.data.security.DatabasePassphraseManager
import com.univeloued.rico.data.security.MasterKeyManager
import com.univeloued.rico.domain.repository.AuthRepository
import com.univeloued.rico.domain.security.EncryptionService
import com.univeloued.rico.util.Constants
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Provider

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val medicalRecordDaoProvider: Provider<MedicalRecordDao>,
    private val emergencyContactDaoProvider: Provider<EmergencyContactDao>,
    private val reminderDaoProvider: Provider<ReminderDao>,
    private val familyMemberDaoProvider: Provider<FamilyMemberDao>,
    private val userProfileDaoProvider: Provider<UserProfileDao>,
    private val remoteDataSource: SupabaseRemoteDataSource,
    private val encryptionService: EncryptionService,
    private val authRepository: AuthRepository,
    private val passphraseManager: DatabasePassphraseManager,
    private val masterKeyManager: MasterKeyManager
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        Log.d("SyncWorker", "Starting sync work...")
        
        // We need the Master Password to decrypt/encrypt data during sync
        if (passphraseManager.getMasterPassword() == null) {
            Log.w("SyncWorker", "App is locked (Master Password missing). Retrying later.")
            return@withContext Result.retry()
        }

        try {
            val userId = authRepository.getCurrentUserId() ?: run {
                Log.e("SyncWorker", "No userId found, failing sync.")
                return@withContext Result.failure()
            }
            
            Log.d("SyncWorker", "Syncing for user: $userId")

            // 1. PUSH local changes first (to avoid overwriting new local data with old cloud data)
            syncUserProfile(userId)
            syncMedicalRecords(userId)
            syncEmergencyContacts(userId)
            syncReminders(userId)
            syncFamilyMembers(userId)

            // 2. PULL from remote (Pull)
            pullUserProfile(userId)
            pullMedicalRecords(userId)
            pullEmergencyContacts(userId)
            pullReminders(userId)
            pullFamilyMembers(userId)
            
            Log.d("SyncWorker", "Sync work completed successfully.")
            Result.success()
        } catch (e: Exception) {
            Log.e("SyncWorker", "Sync work failed with exception", e)
            Result.retry()
        }
    }

    private suspend fun pullUserProfile(userId: String) {
        try {
            Log.d("SyncWorker", "Pulling user profile from remote for user: $userId")
            
            // If we have unsynced local changes, don't pull yet to avoid clobbering
            if (userProfileDaoProvider.get().getUnsyncedProfile(userId) != null) {
                Log.d("SyncWorker", "Local profile has unsynced changes. Skipping pull for now.")
                return
            }

            val remoteProfile = remoteDataSource.getUserProfile(userId) ?: return
            
            // Sync encryption salt if available
            remoteProfile.encryptionSalt?.let { saltString ->
                val salt = masterKeyManager.stringToSalt(saltString)
                passphraseManager.setEncryptionSalt(salt)
            }

            val entity = UserProfileEntity(
                id = remoteProfile.id,
                name = decryptSafely(remoteProfile.name, "Decryption Error"),
                birthdate = remoteProfile.birthdate,
                gender = remoteProfile.gender,
                bloodType = remoteProfile.bloodType,
                insuranceNumber = decryptSafely(remoteProfile.insuranceNumber, ""),
                address = decryptSafely(remoteProfile.address, ""),
                phone = decryptSafely(remoteProfile.phone, ""),
                email = remoteProfile.email,
                notes = decryptSafely(remoteProfile.notes, ""),
                photoUri = remoteProfile.photoUrl,
                isSynced = true,
                lastUpdated = System.currentTimeMillis()
            )
            userProfileDaoProvider.get().insertOrUpdateProfile(entity)
            Log.d("SyncWorker", "Successfully pulled and inserted user profile.")
        } catch (e: Exception) {
            Log.e("SyncWorker", "Error in pullUserProfile: ${e.message}", e)
        }
    }

    private fun decryptSafely(encryptedValue: String?, fallback: String): String {
        if (encryptedValue.isNullOrBlank()) return ""
        return try {
            encryptionService.decrypt(encryptedValue)
        } catch (e: Exception) {
            Log.e("SyncWorker", "Decryption failed for value starting with: ${encryptedValue.take(10)}")
            fallback
        }
    }

    private suspend fun pullMedicalRecords(userId: String) {
        try {
            Log.d("SyncWorker", "Pulling medical records from remote for user: $userId")
            val remoteRecords = remoteDataSource.getMedicalRecords(userId)
            Log.d("SyncWorker", "Found ${remoteRecords.size} medical records on remote.")
            var successCount = 0
            for (dto in remoteRecords) {
                try {
                    val entity = MedicalRecordEntity(
                        id = dto.id,
                        userId = userId,
                        fileName = try { encryptionService.decrypt(dto.fileName) } catch(e: Exception) { "Decryption Error" },
                        recordFor = try { encryptionService.decrypt(dto.recordFor) } catch(e: Exception) { "Decryption Error" },
                        recordType = dto.recordType,
                        createdOn = dto.createdOn,
                        fileUri = dto.fileUrl?.let { android.net.Uri.parse(it) },
                        isSynced = true
                    )
                    Log.d("SyncWorker", "Pulled record: ${entity.fileName}")
                    medicalRecordDaoProvider.get().insertMedicalRecord(entity)
                    successCount++
                } catch (e: Exception) {
                    Log.e("SyncWorker", "Failed to decrypt or insert record ${dto.id}: ${e.message}", e)
                }
            }
            Log.d("SyncWorker", "Successfully pulled and inserted $successCount medical records.")
        } catch (e: Exception) {
            Log.e("SyncWorker", "Error in pullMedicalRecords: ${e.message}", e)
        }
    }

    private suspend fun pullEmergencyContacts(userId: String) {
        try {
            Log.d("SyncWorker", "Pulling emergency contacts from remote for user: $userId")
            val remoteContacts = remoteDataSource.getEmergencyContacts(userId)
            Log.d("SyncWorker", "Found ${remoteContacts.size} contacts on remote.")
            var successCount = 0
            for (dto in remoteContacts) {
                try {
                    val entity = EmergencyContactEntity(
                        id = dto.id,
                        userId = userId,
                        name = try { encryptionService.decrypt(dto.name) } catch(e: Exception) { "Decryption Error" },
                        phone = try { encryptionService.decrypt(dto.phone) } catch(e: Exception) { "" },
                        email = try { encryptionService.decrypt(dto.email) } catch(e: Exception) { "" },
                        photoUri = dto.photoUrl,
                        isSynced = true
                    )
                    emergencyContactDaoProvider.get().insertContact(entity)
                    successCount++
                } catch (e: Exception) {
                    Log.e("SyncWorker", "Failed to insert contact ${dto.id}: ${e.message}", e)
                }
            }
            Log.d("SyncWorker", "Successfully pulled and inserted $successCount contacts.")
        } catch (e: Exception) {
            Log.e("SyncWorker", "Error in pullEmergencyContacts: ${e.message}", e)
        }
    }

    private suspend fun pullReminders(userId: String) {
        try {
            Log.d("SyncWorker", "Pulling reminders from remote for user: $userId")
            val remoteReminders = remoteDataSource.getReminders(userId)
            Log.d("SyncWorker", "Found ${remoteReminders.size} reminders on remote.")
            var successCount = 0
            for (dto in remoteReminders) {
                try {
                    val entity = ReminderEntity(
                        id = dto.id,
                        userId = userId,
                        medicineName = try { encryptionService.decrypt(dto.medicineName) } catch(e: Exception) { "Decryption Error" },
                        unit = dto.unit,
                        frequency = dto.frequency,
                        time = dto.time,
                        duration = dto.duration,
                        intakeMethod = dto.intakeMethod,
                        isActive = dto.isActive,
                        isSynced = true
                    )
                    reminderDaoProvider.get().insertReminder(entity)
                    successCount++
                } catch (e: Exception) {
                    Log.e("SyncWorker", "Failed to insert reminder ${dto.id}: ${e.message}", e)
                }
            }
            Log.d("SyncWorker", "Successfully pulled and inserted $successCount reminders.")
        } catch (e: Exception) {
            Log.e("SyncWorker", "Error in pullReminders: ${e.message}", e)
        }
    }

    private suspend fun pullFamilyMembers(userId: String) {
        try {
            Log.d("SyncWorker", "Pulling family members from remote for user: $userId")
            val remoteMembers = remoteDataSource.getFamilyMembers(userId)
            Log.d("SyncWorker", "Found ${remoteMembers.size} family members on remote.")
            var successCount = 0
            for (dto in remoteMembers) {
                try {
                    val entity = FamilyMemberEntity(
                        id = dto.id,
                        userId = userId,
                        name = try { encryptionService.decrypt(dto.name) } catch(e: Exception) { "Decryption Error" },
                        relationship = dto.relationship,
                        birthdate = dto.birthdate,
                        gender = dto.gender,
                        photoUri = dto.photoUrl,
                        isSynced = true
                    )
                    familyMemberDaoProvider.get().insertFamilyMember(entity)
                    successCount++
                } catch (e: Exception) {
                    Log.e("SyncWorker", "Failed to insert family member ${dto.id}: ${e.message}", e)
                }
            }
            Log.d("SyncWorker", "Successfully pulled and inserted $successCount family members.")
        } catch (e: Exception) {
            Log.e("SyncWorker", "Error in pullFamilyMembers: ${e.message}", e)
        }
    }

    private suspend fun syncUserProfile(userId: String) {
        try {
            val entity = userProfileDaoProvider.get().getUnsyncedProfile(userId) ?: return
            
            // Ensure local salt is stored in DatabasePassphraseManager
            entity.encryptionSalt?.let { saltString ->
                passphraseManager.setEncryptionSalt(masterKeyManager.stringToSalt(saltString))
            }

            val dto = EncryptedUserProfileDto(
                id = entity.id,
                name = encryptionService.encrypt(entity.name),
                birthdate = entity.birthdate,
                gender = entity.gender,
                bloodType = entity.bloodType,
                insuranceNumber = encryptionService.encrypt(entity.insuranceNumber),
                address = encryptionService.encrypt(entity.address),
                phone = encryptionService.encrypt(entity.phone),
                email = entity.email,
                notes = encryptionService.encrypt(entity.notes),
                photoUrl = entity.photoUri,
                encryptionSalt = entity.encryptionSalt
            )
            remoteDataSource.uploadUserProfile(dto)
            userProfileDaoProvider.get().markAsSynced(entity.id)
            Log.d("SyncWorker", "Successfully pushed user profile.")
        } catch (e: Exception) {
            Log.e("SyncWorker", "Error in syncUserProfile", e)
        }
    }

    private suspend fun syncMedicalRecords(userId: String) {
        try {
            val unsyncedRecords = medicalRecordDaoProvider.get().getUnsyncedMedicalRecords(userId)
            Log.d("SyncWorker", "Found ${unsyncedRecords.size} unsynced medical records for push.")
            for (entity in unsyncedRecords) {
                try {
                    var remoteFileUrl: String? = null
                    entity.fileUri?.let { uri ->
                        try {
                            val bytes = if (uri.scheme == "content") {
                                applicationContext.contentResolver.openInputStream(uri)?.readBytes()
                            } else {
                                // For file:// or internal paths
                                val file = java.io.File(uri.path ?: "")
                                if (file.exists()) file.readBytes() else null
                            }
                            
                            if (bytes != null) {
                                val cloudPath = "$userId/${entity.id}_file"
                                remoteFileUrl = remoteDataSource.uploadFile(cloudPath, bytes)
                                Log.d("SyncWorker", "File uploaded for record ${entity.id}: $remoteFileUrl")
                            } else {
                                Log.e("SyncWorker", "Could not read bytes for file: $uri")
                            }
                        } catch (e: Exception) {
                            Log.e("SyncWorker", "Failed to upload file for record ${entity.id}: ${errorMsg(e)}")
                        }
                    }

                    val dto = EncryptedMedicalRecordDto(
                        id = entity.id,
                        fileName = encryptionService.encrypt(entity.fileName),
                        recordFor = encryptionService.encrypt(entity.recordFor),
                        recordType = entity.recordType,
                        createdOn = entity.createdOn,
                        fileUrl = remoteFileUrl,
                        userId = userId
                    )
                    remoteDataSource.uploadMedicalRecord(dto)
                    medicalRecordDaoProvider.get().markAsSynced(entity.id)
                    Log.d("SyncWorker", "Successfully pushed medical record: ${entity.id}")
                } catch (e: Exception) {
                    Log.e("SyncWorker", "Failed to push medical record ${entity.id}: ${errorMsg(e)}")
                }
            }
        } catch (e: Exception) {
            Log.e("SyncWorker", "Error in syncMedicalRecords loop: ${errorMsg(e)}")
        }
    }

    private fun errorMsg(e: Exception): String = e.message ?: e.toString()

    private suspend fun syncEmergencyContacts(userId: String) {
        try {
            val unsyncedContacts = emergencyContactDaoProvider.get().getUnsyncedContacts(userId)
            Log.d("SyncWorker", "Found ${unsyncedContacts.size} unsynced contacts for push.")
            for (entity in unsyncedContacts) {
                try {
                    val dto = EncryptedEmergencyContactDto(
                        id = entity.id,
                        name = encryptionService.encrypt(entity.name),
                        phone = encryptionService.encrypt(entity.phone),
                        email = encryptionService.encrypt(entity.email),
                        photoUrl = entity.photoUri,
                        userId = userId
                    )
                    remoteDataSource.uploadEmergencyContact(dto)
                    emergencyContactDaoProvider.get().markAsSynced(entity.id)
                    Log.d("SyncWorker", "Successfully pushed contact ${entity.id}")
                } catch (e: Exception) {
                    Log.e("SyncWorker", "Failed to push contact ${entity.id}: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("SyncWorker", "Error in syncEmergencyContacts loop: ${e.message}")
        }
    }

    private suspend fun syncReminders(userId: String) {
        try {
            val unsyncedReminders = reminderDaoProvider.get().getUnsyncedReminders(userId)
            for (entity in unsyncedReminders) {
                val dto = EncryptedReminderDto(
                    id = entity.id,
                    medicineName = encryptionService.encrypt(entity.medicineName),
                    unit = entity.unit,
                    frequency = entity.frequency,
                    time = entity.time,
                    duration = entity.duration,
                    intakeMethod = entity.intakeMethod,
                    isActive = entity.isActive,
                    userId = userId
                )
                remoteDataSource.uploadReminder(dto)
                reminderDaoProvider.get().markAsSynced(entity.id)
            }
        } catch (e: Exception) {
            Log.e("SyncWorker", "Error in syncReminders", e)
        }
    }

    private suspend fun syncFamilyMembers(userId: String) {
        try {
            val unsyncedMembers = familyMemberDaoProvider.get().getUnsyncedFamilyMembers(userId)
            for (entity in unsyncedMembers) {
                val dto = EncryptedFamilyMemberDto(
                    id = entity.id,
                    name = encryptionService.encrypt(entity.name),
                    relationship = entity.relationship,
                    birthdate = entity.birthdate,
                    gender = entity.gender,
                    photoUrl = entity.photoUri,
                    userId = userId
                )
                remoteDataSource.uploadFamilyMember(dto)
                familyMemberDaoProvider.get().markAsSynced(entity.id)
            }
        } catch (e: Exception) {
            Log.e("SyncWorker", "Error in syncFamilyMembers", e)
        }
    }
}
