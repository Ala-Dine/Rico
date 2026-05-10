package com.univeloued.rico.domain.usecase

import com.univeloued.rico.domain.model.Reminder
import com.univeloued.rico.domain.repository.ReminderRepository
import javax.inject.Inject

class UpdateReminderUseCase @Inject constructor(
    private val repository: ReminderRepository
) {
    suspend operator fun invoke(reminder: Reminder) {
        repository.updateReminder(reminder)
    }
}
