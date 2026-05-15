package com.univeloued.rico.data.repository

import com.univeloued.rico.data.local.dao.EmergencyContactDao
import com.univeloued.rico.data.mapper.toDomain
import com.univeloued.rico.data.mapper.toEntity
import com.univeloued.rico.domain.model.EmergencyContact
import com.univeloued.rico.domain.repository.AuthRepository
import com.univeloued.rico.domain.repository.EmergencyContactRepository
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
class EmergencyContactRepositoryImpl @Inject constructor(
    private val emergencyContactDaoProvider: Provider<EmergencyContactDao>,
    private val authRepository: AuthRepository,
    private val syncManager: SyncManager
) : EmergencyContactRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getEmergencyContacts(): Flow<List<EmergencyContact>> {
        return authRepository.currentUserFlow.flatMapLatest { userId ->
            if (userId != null) {
                emergencyContactDaoProvider.get().getAllContacts(userId).map { entities ->
                    entities.map { it.toDomain() }
                }
            } else {
                flowOf(emptyList())
            }
        }
    }

    override suspend fun addEmergencyContact(contact: EmergencyContact) {
        val userId = authRepository.getCurrentUserId() ?: return
        val contactToInsert = if (contact.id.isEmpty()) {
            contact.copy(id = UUID.randomUUID().toString())
        } else {
            contact
        }
        emergencyContactDaoProvider.get().insertContact(contactToInsert.toEntity().copy(userId = userId))
        syncManager.scheduleContactsSync()
    }

    override suspend fun deleteEmergencyContact(contact: EmergencyContact) {
        emergencyContactDaoProvider.get().deleteContact(contact.toEntity())
        syncManager.scheduleContactsSync()
    }
}
