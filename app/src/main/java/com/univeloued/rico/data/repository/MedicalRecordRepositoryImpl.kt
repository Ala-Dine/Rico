package com.univeloued.rico.data.repository

import android.util.Log
import com.univeloued.rico.data.local.dao.MedicalRecordDao
import com.univeloued.rico.data.mapper.toDomain
import com.univeloued.rico.data.mapper.toEntity
import com.univeloued.rico.data.util.FileHelper
import com.univeloued.rico.domain.model.MedicalRecord
import com.univeloued.rico.domain.repository.AuthRepository
import com.univeloued.rico.domain.repository.MedicalRecordRepository
import com.univeloued.rico.domain.sync.SyncManager
import com.univeloued.rico.util.Constants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class MedicalRecordRepositoryImpl @Inject constructor(
    private val medicalRecordDaoProvider: Provider<MedicalRecordDao>,
    private val fileHelper: FileHelper,
    private val authRepository: AuthRepository,
    private val syncManager: SyncManager
) : MedicalRecordRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getMedicalRecords(): Flow<List<MedicalRecord>> {
        return authRepository.currentUserFlow.flatMapLatest { userId ->
            if (userId != null) {
                medicalRecordDaoProvider.get().getAllMedicalRecords(userId).map { entities ->
                    entities.map { it.toDomain() }
                }
            } else {
                flowOf(emptyList())
            }
        }
    }

    override suspend fun addMedicalRecord(record: MedicalRecord) {
        val userId = authRepository.getCurrentUserId() ?: run {
            Log.e("MedicalRecordRepo", "Cannot add record: userId is null")
            return
        }
        
        val internalFileUri = record.fileUri?.let { uriString ->
            if (uriString.startsWith("content://")) {
                fileHelper.saveFileToInternalStorage(android.net.Uri.parse(uriString), Constants.DIR_MEDICAL_RECORDS)
            } else {
                uriString
            }
        }

        val recordToInsert = if (record.id.isEmpty()) {
            record.copy(
                id = UUID.randomUUID().toString(),
                fileUri = internalFileUri
            )
        } else {
            record.copy(fileUri = internalFileUri ?: record.fileUri)
        }
        
        medicalRecordDaoProvider.get().insertMedicalRecord(recordToInsert.toEntity().copy(userId = userId))
        syncManager.scheduleRecordsSync()
    }

    override suspend fun deleteMedicalRecord(record: MedicalRecord) {
        medicalRecordDaoProvider.get().deleteMedicalRecord(record.toEntity())
        syncManager.scheduleRecordsSync()
    }
}
