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
        val nameResult = Validator.validateRequiredField(record.fileName, "File Name")
        if (nameResult is ValidationResult.Error) return nameResult

        val forResult = Validator.validateRequiredField(record.recordFor, "Patient Name")
        if (forResult is ValidationResult.Error) return forResult

        val typeResult = Validator.validateRequiredField(record.recordType, "Record Type")
        if (typeResult is ValidationResult.Error) return typeResult

        if (record.fileUri.isNullOrBlank()) {
            return ValidationResult.Error("Please select a file")
        }

        repository.addMedicalRecord(record)
        return ValidationResult.Success
    }
}
