package com.univeloued.rico.data.repository

import com.univeloued.rico.data.local.dao.ReminderDao
import com.univeloued.rico.data.mapper.toDomain
import com.univeloued.rico.data.mapper.toEntity
import com.univeloued.rico.domain.model.Reminder
import com.univeloued.rico.domain.repository.AuthRepository
import com.univeloued.rico.domain.repository.ReminderRepository
import com.univeloued.rico.domain.sync.SyncManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class ReminderRepositoryImpl @Inject constructor(
    private val reminderDaoProvider: Provider<ReminderDao>,
    private val authRepository: AuthRepository,
    private val syncManager: SyncManager
) : ReminderRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getReminders(): Flow<List<Reminder>> {
        return authRepository.currentUserFlow.flatMapLatest { userId ->
            if (userId != null) {
                reminderDaoProvider.get().getAllReminders(userId).map { entities ->
                    entities.map { it.toDomain() }
                }
            } else {
                flowOf(emptyList())
            }
        }
    }

    override suspend fun addReminder(reminder: Reminder) {
        val userId = authRepository.getCurrentUserId() ?: return
        val reminderToInsert = if (reminder.id.isEmpty()) {
            reminder.copy(id = UUID.randomUUID().toString())
        } else {
            reminder
        }
        reminderDaoProvider.get().insertReminder(reminderToInsert.toEntity().copy(userId = userId))
        syncManager.scheduleRemindersSync()
    }

    override suspend fun updateReminder(reminder: Reminder) {
        val userId = authRepository.getCurrentUserId() ?: return
        reminderDaoProvider.get().updateReminder(reminder.toEntity().copy(userId = userId))
        syncManager.scheduleRemindersSync()
    }

    override suspend fun deleteReminder(reminder: Reminder) {
        reminderDaoProvider.get().deleteReminder(reminder.toEntity())
        syncManager.scheduleRemindersSync()
    }
}
