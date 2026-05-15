package com.univeloued.rico.domain.usecase

import android.util.Log
import com.univeloued.rico.domain.model.MedicalRecord
import com.univeloued.rico.domain.repository.MedicalRecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

class GetMedicalRecordsUseCase @Inject constructor(
    private val repository: MedicalRecordRepository
) {
    operator fun invoke(): Flow<List<MedicalRecord>> {
        return repository.getMedicalRecords()
            .catch { e ->
                Log.e("GetMedicalRecordsUC", "Error fetching records", e)
                emit(emptyList())
            }
    }
}
