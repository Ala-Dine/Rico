package com.univeloued.rico.data.repository

import com.univeloued.rico.data.model.MedicalRecord
import com.univeloued.rico.domain.repository.MedicalRecordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MedicalRecordRepositoryImpl @Inject constructor() : MedicalRecordRepository {
    private val _records = MutableStateFlow<List<MedicalRecord>>(emptyList())
    override val records: StateFlow<List<MedicalRecord>> = _records.asStateFlow()

    override fun addRecord(record: MedicalRecord) {
        _records.value += record.copy(id = UUID.randomUUID().toString())
    }
}
