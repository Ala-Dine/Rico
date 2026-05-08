package com.univeloued.rico.domain.usecase

import com.univeloued.rico.data.model.FamilyMember
import com.univeloued.rico.domain.repository.FamilyRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetFamilyMembersUseCase @Inject constructor(
    private val repository: FamilyRepository
) {
    operator fun invoke(): StateFlow<List<FamilyMember>> {
        return repository.familyMembers
    }
}
