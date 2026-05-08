package com.univeloued.rico.domain.repository

import com.univeloued.rico.data.model.EmergencyContact
import kotlinx.coroutines.flow.StateFlow

interface EmergencyContactRepository {
    val emergencyContacts: StateFlow<List<EmergencyContact>>
    fun addEmergencyContact(contact: EmergencyContact)
}
