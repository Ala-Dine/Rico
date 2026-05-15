package com.univeloued.rico.data.remote

import com.univeloued.rico.data.remote.dto.EncryptedEmergencyContactDto
import com.univeloued.rico.data.remote.dto.EncryptedFamilyMemberDto
import com.univeloued.rico.data.remote.dto.EncryptedMedicalRecordDto
import com.univeloued.rico.data.remote.dto.EncryptedReminderDto
import com.univeloued.rico.data.remote.dto.EncryptedUserProfileDto
import com.univeloued.rico.util.Constants
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupabaseRemoteDataSource @Inject constructor(
    private val supabaseClient: SupabaseClient
) {
    private val postgrest = supabaseClient.postgrest
    private val storage = supabaseClient.storage

    suspend fun uploadMedicalRecord(record: EncryptedMedicalRecordDto) {
        postgrest["medical_records"].upsert(record)
    }

    suspend fun uploadEmergencyContact(contact: EncryptedEmergencyContactDto) {
        postgrest["emergency_contacts"].upsert(contact)
    }

    suspend fun uploadReminder(reminder: EncryptedReminderDto) {
        postgrest["reminders"].upsert(reminder)
    }

    suspend fun uploadFamilyMember(member: EncryptedFamilyMemberDto) {
        postgrest["family_members"].upsert(member)
    }

    suspend fun uploadUserProfile(profile: EncryptedUserProfileDto) {
        android.util.Log.d("SupabaseRemote", "Uploading user profile: ${profile.id}")
        postgrest["user_profiles"].upsert(profile)
    }

    suspend fun uploadFile(fileName: String, bytes: ByteArray): String {
        val bucket = storage.from(Constants.BUCKET_MEDICAL_RECORDS)
        bucket.upload(fileName, bytes) {
            upsert = true
        }
        return bucket.publicUrl(fileName)
    }

    suspend fun getMedicalRecords(userId: String): List<EncryptedMedicalRecordDto> {
        android.util.Log.d("SupabaseRemote", "Fetching records for userId: $userId")
        return postgrest["medical_records"]
            .select {
                filter {
                    eq("userId", userId)
                }
            }
            .decodeList<EncryptedMedicalRecordDto>()
    }

    suspend fun getEmergencyContacts(userId: String): List<EncryptedEmergencyContactDto> {
        return postgrest["emergency_contacts"]
            .select {
                filter {
                    eq("userId", userId)
                }
            }
            .decodeList<EncryptedEmergencyContactDto>()
    }

    suspend fun getReminders(userId: String): List<EncryptedReminderDto> {
        return postgrest["reminders"]
            .select {
                filter {
                    eq("userId", userId)
                }
            }
            .decodeList<EncryptedReminderDto>()
    }

    suspend fun getFamilyMembers(userId: String): List<EncryptedFamilyMemberDto> {
        return postgrest["family_members"]
            .select {
                filter {
                    eq("userId", userId)
                }
            }
            .decodeList<EncryptedFamilyMemberDto>()
    }

    suspend fun getUserProfile(userId: String): EncryptedUserProfileDto? {
        return postgrest["user_profiles"]
            .select {
                filter {
                    eq("id", userId)
                }
            }
            .decodeSingleOrNull<EncryptedUserProfileDto>()
    }
}
