package com.univeloued.rico.domain.usecase

import com.univeloued.rico.domain.model.EmergencyContact
import com.univeloued.rico.domain.repository.EmergencyContactRepository
import com.univeloued.rico.domain.util.ValidationResult
import com.univeloued.rico.domain.util.Validator
import javax.inject.Inject

class AddEmergencyContactUseCase @Inject constructor(
    private val repository: EmergencyContactRepository
) {
    suspend operator fun invoke(contact: EmergencyContact): ValidationResult {
        val nameResult = Validator.validateRequiredField(contact.name, "Name")
        if (nameResult is ValidationResult.Error) return nameResult

        val phoneResult = Validator.validatePhone(contact.phone)
        if (phoneResult is ValidationResult.Error) return phoneResult

        val emailResult = Validator.validateEmail(contact.email)
        if (emailResult is ValidationResult.Error) return emailResult

        repository.addEmergencyContact(contact)
        return ValidationResult.Success
    }
}
