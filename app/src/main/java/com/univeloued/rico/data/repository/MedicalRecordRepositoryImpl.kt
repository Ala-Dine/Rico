package com.univeloued.rico.data.repository

import com.univeloued.rico.data.local.dao.MedicalRecordDao
import com.univeloued.rico.data.model.MedicalRecord
import com.univeloued.rico.domain.repository.MedicalRecordRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MedicalRecordRepositoryImpl @Inject constructor(
    private val medicalRecordDao: MedicalRecordDao
) : MedicalRecordRepository {

    override fun getMedicalRecords(): Flow<List<MedicalRecord>> {
        return medicalRecordDao.getAllMedicalRecords()
    }

    override suspend fun addMedicalRecord(record: MedicalRecord) {
        val recordToInsert = if (record.id.isEmpty()) {
            record.copy(id = UUID.randomUUID().toString())
        } else {
            record
        }
        medicalRecordDao.insertMedicalRecord(recordToInsert)
    }

    override suspend fun deleteMedicalRecord(record: MedicalRecord) {
        medicalRecordDao.deleteMedicalRecord(record)
    }
}
