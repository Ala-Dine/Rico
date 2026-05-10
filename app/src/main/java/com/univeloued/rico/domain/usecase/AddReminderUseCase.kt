package com.univeloued.rico.domain.usecase

import com.univeloued.rico.domain.model.Reminder
import com.univeloued.rico.domain.repository.ReminderRepository
import com.univeloued.rico.domain.util.ValidationResult
import com.univeloued.rico.domain.util.Validator
import javax.inject.Inject

class AddReminderUseCase @Inject constructor(
    private val repository: ReminderRepository
) {
    suspend operator fun invoke(reminder: Reminder): ValidationResult {
        val nameResult = Validator.validateRequiredField(reminder.medicineName, "Medicine Name")
        if (nameResult is ValidationResult.Error) return nameResult

        val unitResult = Validator.validateRequiredField(reminder.unit, "Unit")
        if (unitResult is ValidationResult.Error) return unitResult

        val frequencyResult = Validator.validateRequiredField(reminder.frequency, "Frequency")
        if (frequencyResult is ValidationResult.Error) return frequencyResult

        val timeResult = Validator.validateRequiredField(reminder.time, "Time")
        if (timeResult is ValidationResult.Error) return timeResult

        repository.addReminder(reminder)
        return ValidationResult.Success
    }
}
