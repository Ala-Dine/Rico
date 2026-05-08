package com.univeloued.rico.domain.usecase

import com.univeloued.rico.data.model.Reminder
import com.univeloued.rico.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetRemindersUseCase @Inject constructor(
    private val repository: ReminderRepository
) {
    operator fun invoke(): StateFlow<List<Reminder>> {
        return repository.reminders
    }
}
