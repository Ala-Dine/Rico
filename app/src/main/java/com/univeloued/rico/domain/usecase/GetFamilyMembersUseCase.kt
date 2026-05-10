package com.univeloued.rico.domain.usecase

import com.univeloued.rico.domain.model.FamilyMember
import com.univeloued.rico.domain.repository.FamilyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFamilyMembersUseCase @Inject constructor(
    private val repository: FamilyRepository
) {
    operator fun invoke(): Flow<List<FamilyMember>> {
        return repository.getFamilyMembers()
    }
}
