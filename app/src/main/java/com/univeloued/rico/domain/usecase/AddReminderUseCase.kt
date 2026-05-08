package com.univeloued.rico.domain.usecase

import com.univeloued.rico.data.model.Reminder
import com.univeloued.rico.domain.repository.ReminderRepository
import javax.inject.Inject

class AddReminderUseCase @Inject constructor(
    private val repository: ReminderRepository
) {
    operator fun invoke(reminder: Reminder) {
        repository.addReminder(reminder)
    }
}
