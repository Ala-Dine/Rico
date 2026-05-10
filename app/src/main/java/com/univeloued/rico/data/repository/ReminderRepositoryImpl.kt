package com.univeloued.rico.data.repository

import com.univeloued.rico.data.local.dao.ReminderDao
import com.univeloued.rico.data.mapper.toDomain
import com.univeloued.rico.data.mapper.toEntity
import com.univeloued.rico.domain.model.Reminder
import com.univeloued.rico.domain.repository.ReminderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderRepositoryImpl @Inject constructor(
    private val reminderDao: ReminderDao
) : ReminderRepository {

    override fun getReminders(): Flow<List<Reminder>> {
        return reminderDao.getAllReminders().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addReminder(reminder: Reminder) {
        val reminderToInsert = if (reminder.id.isEmpty()) {
            reminder.copy(id = UUID.randomUUID().toString())
        } else {
            reminder
        }
        reminderDao.insertReminder(reminderToInsert.toEntity())
    }

    override suspend fun updateReminder(reminder: Reminder) {
        reminderDao.updateReminder(reminder.toEntity())
    }

    override suspend fun deleteReminder(reminder: Reminder) {
        reminderDao.deleteReminder(reminder.toEntity())
    }
}
