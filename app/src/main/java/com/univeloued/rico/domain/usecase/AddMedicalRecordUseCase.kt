package com.univeloued.rico.domain.usecase

import android.util.Log
import com.univeloued.rico.domain.model.MedicalRecord
import com.univeloued.rico.domain.repository.AuthRepository
import com.univeloued.rico.domain.repository.MedicalRecordRepository
import com.univeloued.rico.util.Resource
import com.univeloued.rico.domain.util.Validator
import javax.inject.Inject

class AddMedicalRecordUseCase @Inject constructor(
    private val repository: MedicalRecordRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(record: MedicalRecord): Resource<Unit> {
        return try {
            if (authRepository.getCurrentUserId() == null) {
                return Resource.Error("User not authenticated. Please log in again.")
            }

            Validator.validateRequired(record.fileName, "File Name").let { if (it is com.univeloued.rico.domain.util.ValidationResult.Error) return Resource.Error(it.message) }
            Validator.validateRequired(record.recordFor, "Patient Name").let { if (it is com.univeloued.rico.domain.util.ValidationResult.Error) return Resource.Error(it.message) }
            Validator.validateRequired(record.recordType, "Record Type").let { if (it is com.univeloued.rico.domain.util.ValidationResult.Error) return Resource.Error(it.message) }
            Validator.validateRequired(record.fileUri, "File").let { if (it is com.univeloued.rico.domain.util.ValidationResult.Error) return Resource.Error(it.message) }

            repository.addMedicalRecord(record)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Log.e("AddMedicalRecordUseCase", "Failed to add medical record", e)
            Resource.Error("An unexpected error occurred: ${e.localizedMessage}")
        }
    }
}
