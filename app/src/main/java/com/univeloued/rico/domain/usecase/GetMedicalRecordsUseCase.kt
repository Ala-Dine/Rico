package com.univeloued.rico.domain.usecase

import com.univeloued.rico.domain.model.MedicalRecord
import com.univeloued.rico.domain.repository.MedicalRecordRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMedicalRecordsUseCase @Inject constructor(
    private val repository: MedicalRecordRepository
) {
    operator fun invoke(): Flow<List<MedicalRecord>> {
        return repository.getMedicalRecords()
    }
}
