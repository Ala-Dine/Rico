package com.univeloued.rico.domain.repository

import com.univeloued.rico.data.model.MedicalRecord
import kotlinx.coroutines.flow.StateFlow

interface MedicalRecordRepository {
    val records: StateFlow<List<MedicalRecord>>
    fun addRecord(record: MedicalRecord)
}
