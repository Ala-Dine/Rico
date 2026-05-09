package com.univeloued.rico.domain.repository

import com.univeloued.rico.data.model.EmergencyContact
import kotlinx.coroutines.flow.Flow

interface EmergencyContactRepository {
    fun getEmergencyContacts(): Flow<List<EmergencyContact>>
    suspend fun addEmergencyContact(contact: EmergencyContact)
    suspend fun deleteEmergencyContact(contact: EmergencyContact)
}
