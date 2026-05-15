package com.univeloued.rico.domain.usecase

import android.util.Log
import com.univeloued.rico.domain.model.Reminder
import com.univeloued.rico.domain.repository.AuthRepository
import com.univeloued.rico.domain.repository.ReminderRepository
import com.univeloued.rico.util.Resource
import com.univeloued.rico.domain.util.Validator
import javax.inject.Inject

class AddReminderUseCase @Inject constructor(
    private val repository: ReminderRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(reminder: Reminder): Resource<Unit> {
        return try {
            if (authRepository.getCurrentUserId() == null) {
                return Resource.Error("User not authenticated. Please log in again.")
            }

            Validator.validateRequiredField(reminder.medicineName, "Medicine Name").let { if (it is com.univeloued.rico.domain.util.ValidationResult.Error) return Resource.Error(it.message) }
            Validator.validateRequiredField(reminder.unit, "Unit").let { if (it is com.univeloued.rico.domain.util.ValidationResult.Error) return Resource.Error(it.message) }
            Validator.validateRequiredField(reminder.frequency, "Frequency").let { if (it is com.univeloued.rico.domain.util.ValidationResult.Error) return Resource.Error(it.message) }
            Validator.validateRequiredField(reminder.time, "Time").let { if (it is com.univeloued.rico.domain.util.ValidationResult.Error) return Resource.Error(it.message) }

            repository.addReminder(reminder)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Log.e("AddReminderUseCase", "Failed to add reminder", e)
            Resource.Error("An unexpected error occurred: ${e.localizedMessage}")
        }
    }
}
