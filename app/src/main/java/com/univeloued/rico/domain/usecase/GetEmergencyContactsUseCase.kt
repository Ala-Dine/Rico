package com.univeloued.rico.domain.usecase

import com.univeloued.rico.data.model.EmergencyContact
import com.univeloued.rico.domain.repository.EmergencyContactRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetEmergencyContactsUseCase @Inject constructor(
    private val repository: EmergencyContactRepository
) {
    operator fun invoke(): StateFlow<List<EmergencyContact>> {
        return repository.emergencyContacts
    }
}
