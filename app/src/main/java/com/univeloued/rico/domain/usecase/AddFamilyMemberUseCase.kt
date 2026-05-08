package com.univeloued.rico.domain.usecase

import com.univeloued.rico.data.model.FamilyMember
import com.univeloued.rico.domain.repository.FamilyRepository
import javax.inject.Inject

class AddFamilyMemberUseCase @Inject constructor(
    private val repository: FamilyRepository
) {
    operator fun invoke(member: FamilyMember) {
        repository.addFamilyMember(member)
    }
}
