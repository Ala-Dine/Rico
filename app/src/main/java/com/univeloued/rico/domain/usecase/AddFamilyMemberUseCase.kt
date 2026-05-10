package com.univeloued.rico.domain.usecase

import com.univeloued.rico.domain.model.FamilyMember
import com.univeloued.rico.domain.repository.FamilyRepository
import javax.inject.Inject

class AddFamilyMemberUseCase @Inject constructor(
    private val repository: FamilyRepository
) {
    suspend operator fun invoke(member: FamilyMember) {
        repository.addFamilyMember(member)
    }
}
