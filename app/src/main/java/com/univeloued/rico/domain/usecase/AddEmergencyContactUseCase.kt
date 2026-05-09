package com.univeloued.rico.domain.usecase

import com.univeloued.rico.data.model.EmergencyContact
import com.univeloued.rico.domain.repository.EmergencyContactRepository
import javax.inject.Inject

class AddEmergencyContactUseCase @Inject constructor(
    private val repository: EmergencyContactRepository
) {
    suspend operator fun invoke(contact: EmergencyContact) {
        repository.addEmergencyContact(contact)
    }
}
