package com.univeloued.rico.data.repository

import com.univeloued.rico.data.model.EmergencyContact
import com.univeloued.rico.domain.repository.EmergencyContactRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmergencyContactRepositoryImpl @Inject constructor() : EmergencyContactRepository {
    private val _emergencyContacts = MutableStateFlow<List<EmergencyContact>>(emptyList())
    override val emergencyContacts: StateFlow<List<EmergencyContact>> = _emergencyContacts.asStateFlow()

    override fun addEmergencyContact(contact: EmergencyContact) {
        _emergencyContacts.value += contact.copy(id = UUID.randomUUID().toString())
    }
}
