package com.univeloued.rico.domain.repository

import com.univeloued.rico.data.model.Reminder
import kotlinx.coroutines.flow.StateFlow

interface ReminderRepository {
    val reminders: StateFlow<List<Reminder>>
    fun addReminder(reminder: Reminder)
    fun updateReminder(reminder: Reminder)
}
