package com.univeloued.rico.domain.usecase

import com.univeloued.rico.domain.model.EmergencyContact
import com.univeloued.rico.domain.repository.EmergencyContactRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEmergencyContactsUseCase @Inject constructor(
    private val repository: EmergencyContactRepository
) {
    operator fun invoke(): Flow<List<EmergencyContact>> {
        return repository.getEmergencyContacts()
    }
}
