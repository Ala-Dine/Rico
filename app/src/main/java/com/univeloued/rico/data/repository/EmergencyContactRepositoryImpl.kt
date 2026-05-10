package com.univeloued.rico.data.repository

import com.univeloued.rico.data.local.dao.EmergencyContactDao
import com.univeloued.rico.data.mapper.toDomain
import com.univeloued.rico.data.mapper.toEntity
import com.univeloued.rico.domain.model.EmergencyContact
import com.univeloued.rico.domain.repository.EmergencyContactRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmergencyContactRepositoryImpl @Inject constructor(
    private val emergencyContactDao: EmergencyContactDao
) : EmergencyContactRepository {

    override fun getEmergencyContacts(): Flow<List<EmergencyContact>> {
        return emergencyContactDao.getAllContacts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addEmergencyContact(contact: EmergencyContact) {
        val contactToInsert = if (contact.id.isEmpty()) {
            contact.copy(id = UUID.randomUUID().toString())
        } else {
            contact
        }
        emergencyContactDao.insertContact(contactToInsert.toEntity())
    }

    override suspend fun deleteEmergencyContact(contact: EmergencyContact) {
        emergencyContactDao.deleteContact(contact.toEntity())
    }
}
