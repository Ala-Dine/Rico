package com.univeloued.rico.data.repository

import com.univeloued.rico.data.model.Reminder
import com.univeloued.rico.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderRepositoryImpl @Inject constructor() : ReminderRepository {
    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())
    override val reminders: StateFlow<List<Reminder>> = _reminders.asStateFlow()

    override fun addReminder(reminder: Reminder) {
        _reminders.value += reminder.copy(id = UUID.randomUUID().toString())
    }

    override fun updateReminder(reminder: Reminder) {
        _reminders.value = _reminders.value.map {
            if (it.id == reminder.id) reminder else it
        }
    }
}
