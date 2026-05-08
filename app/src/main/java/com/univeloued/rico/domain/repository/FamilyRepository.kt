package com.univeloued.rico.domain.repository

import com.univeloued.rico.data.model.FamilyMember
import kotlinx.coroutines.flow.StateFlow

interface FamilyRepository {
    val familyMembers: StateFlow<List<FamilyMember>>
    fun addFamilyMember(member: FamilyMember)
}
