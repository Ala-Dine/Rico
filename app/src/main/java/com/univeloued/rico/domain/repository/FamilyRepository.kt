package com.univeloued.rico.domain.repository

import com.univeloued.rico.domain.model.FamilyMember
import kotlinx.coroutines.flow.Flow

interface FamilyRepository {
    fun getFamilyMembers(): Flow<List<FamilyMember>>
    suspend fun addFamilyMember(member: FamilyMember)
    suspend fun deleteFamilyMember(member: FamilyMember)
}
