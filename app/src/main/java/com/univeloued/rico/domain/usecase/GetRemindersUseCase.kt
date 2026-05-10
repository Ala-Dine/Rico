package com.univeloued.rico.domain.usecase

import com.univeloued.rico.domain.model.Reminder
import com.univeloued.rico.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRemindersUseCase @Inject constructor(
    private val repository: ReminderRepository
) {
    operator fun invoke(): Flow<List<Reminder>> {
        return repository.getReminders()
    }
}
