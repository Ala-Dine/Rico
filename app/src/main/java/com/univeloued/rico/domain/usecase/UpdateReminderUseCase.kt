package com.univeloued.rico.domain.usecase

import android.util.Log
import com.univeloued.rico.domain.model.Reminder
import com.univeloued.rico.domain.repository.AuthRepository
import com.univeloued.rico.domain.util.ValidationResult
import com.univeloued.rico.domain.repository.ReminderRepository
import javax.inject.Inject

class UpdateReminderUseCase @Inject constructor(
    private val repository: ReminderRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(reminder: Reminder): ValidationResult {
        try {
            if (authRepository.getCurrentUserId() == null) {
                return ValidationResult.Error("User not authenticated")
            }
            repository.updateReminder(reminder)
            return ValidationResult.Success
        } catch (e: Exception) {
            Log.e("UpdateReminderUseCase", "Failed to update reminder", e)
            return ValidationResult.Error("An unexpected error occurred: ${e.localizedMessage}")
        }
    }
}
