package com.univeloued.rico.domain.usecase

import com.univeloued.rico.data.model.MedicalRecord
import com.univeloued.rico.domain.repository.MedicalRecordRepository
import javax.inject.Inject

class AddMedicalRecordUseCase @Inject constructor(
    private val repository: MedicalRecordRepository
) {
    suspend operator fun invoke(record: MedicalRecord) {
        repository.addMedicalRecord(record)
    }
}
