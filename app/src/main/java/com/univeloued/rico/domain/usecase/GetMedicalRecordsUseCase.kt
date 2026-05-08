package com.univeloued.rico.domain.usecase

import com.univeloued.rico.data.model.MedicalRecord
import com.univeloued.rico.domain.repository.MedicalRecordRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetMedicalRecordsUseCase @Inject constructor(
    private val repository: MedicalRecordRepository
) {
    operator fun invoke(): StateFlow<List<MedicalRecord>> {
        return repository.records
    }
}
