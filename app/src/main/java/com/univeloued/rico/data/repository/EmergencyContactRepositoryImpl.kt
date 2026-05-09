package com.univeloued.rico.data.repository

import com.univeloued.rico.data.local.dao.EmergencyContactDao
import com.univeloued.rico.data.model.EmergencyContact
import com.univeloued.rico.domain.repository.EmergencyContactRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmergencyContactRepositoryImpl @Inject constructor(
    private val emergencyContactDao: EmergencyContactDao
) : EmergencyContactRepository {

    override fun getEmergencyContacts(): Flow<List<EmergencyContact>> {
        return emergencyContactDao.getAllContacts()
    }

    override suspend fun addEmergencyContact(contact: EmergencyContact) {
        val contactToInsert = if (contact.id.isEmpty()) {
            contact.copy(id = UUID.randomUUID().toString())
        } else {
            contact
        }
        emergencyContactDao.insertContact(contactToInsert)
    }

    override suspend fun deleteEmergencyContact(contact: EmergencyContact) {
        emergencyContactDao.deleteContact(contact)
    }
}
