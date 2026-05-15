package com.univeloued.rico.domain.usecase

import android.util.Log
import com.univeloued.rico.domain.model.EmergencyContact
import com.univeloued.rico.domain.repository.AuthRepository
import com.univeloued.rico.domain.repository.EmergencyContactRepository
import com.univeloued.rico.util.Resource
import com.univeloued.rico.domain.util.Validator
import javax.inject.Inject

class AddEmergencyContactUseCase @Inject constructor(
    private val repository: EmergencyContactRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(contact: EmergencyContact): Resource<Unit> {
        return try {
            if (authRepository.getCurrentUserId() == null) {
                return Resource.Error("User not authenticated. Please log in again.")
            }

            Validator.validateRequiredField(contact.name, "Name").let { if (it is com.univeloued.rico.domain.util.ValidationResult.Error) return Resource.Error(it.message) }
            Validator.validatePhone(contact.phone).let { if (it is com.univeloued.rico.domain.util.ValidationResult.Error) return Resource.Error(it.message) }
            Validator.validateEmail(contact.email).let { if (it is com.univeloued.rico.domain.util.ValidationResult.Error) return Resource.Error(it.message) }

            repository.addEmergencyContact(contact)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Log.e("AddEmergencyContactUC", "Failed to add contact", e)
            Resource.Error("An unexpected error occurred: ${e.localizedMessage}")
        }
    }
}
