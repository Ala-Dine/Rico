package com.univeloued.rico.domain.usecase

import com.univeloued.rico.domain.model.MedicalRecord
import com.univeloued.rico.domain.repository.MedicalRecordRepository
import com.univeloued.rico.domain.util.ValidationResult
import com.univeloued.rico.domain.util.Validator
import javax.inject.Inject

class AddMedicalRecordUseCase @Inject constructor(
    private val repository: MedicalRecordRepository
) {
    suspend operator fun invoke(record: MedicalRecord): ValidationResult {
        Validator.validateRequired(record.fileName, "File Name").let { if (it is ValidationResult.Error) return it }
        Validator.validateRequired(record.recordFor, "Patient Name").let { if (it is ValidationResult.Error) return it }
        Validator.validateRequired(record.recordType, "Record Type").let { if (it is ValidationResult.Error) return it }
        Validator.validateRequired(record.fileUri, "File").let { if (it is ValidationResult.Error) return it }

        repository.addMedicalRecord(record)
        return ValidationResult.Success
    }
}
