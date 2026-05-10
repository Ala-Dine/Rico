package com.univeloued.rico.data.repository

import com.univeloued.rico.data.local.dao.MedicalRecordDao
import com.univeloued.rico.data.mapper.toDomain
import com.univeloued.rico.data.mapper.toEntity
import com.univeloued.rico.data.util.FileHelper
import com.univeloued.rico.domain.model.MedicalRecord
import com.univeloued.rico.domain.repository.MedicalRecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MedicalRecordRepositoryImpl @Inject constructor(
    private val medicalRecordDao: MedicalRecordDao,
    private val fileHelper: FileHelper
) : MedicalRecordRepository {

    override fun getMedicalRecords(): Flow<List<MedicalRecord>> {
        return medicalRecordDao.getAllMedicalRecords().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addMedicalRecord(record: MedicalRecord) {
        val internalFileUri = record.fileUri?.let { uriString ->
            if (uriString.startsWith("content://")) {
                fileHelper.saveFileToInternalStorage(android.net.Uri.parse(uriString), "medical_records")
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
        medicalRecordDao.insertMedicalRecord(recordToInsert.toEntity())
    }

    override suspend fun deleteMedicalRecord(record: MedicalRecord) {
        medicalRecordDao.deleteMedicalRecord(record.toEntity())
    }
}
