package com.univeloued.rico.domain.repository

import com.univeloued.rico.domain.model.MedicalRecord
import kotlinx.coroutines.flow.Flow

interface MedicalRecordRepository {
    fun getMedicalRecords(): Flow<List<MedicalRecord>>
    suspend fun addMedicalRecord(record: MedicalRecord)
    suspend fun deleteMedicalRecord(record: MedicalRecord)
}
