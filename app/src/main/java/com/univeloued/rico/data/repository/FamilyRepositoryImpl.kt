package com.univeloued.rico.data.repository

import com.univeloued.rico.data.local.dao.FamilyMemberDao
import com.univeloued.rico.data.model.FamilyMember
import com.univeloued.rico.domain.repository.FamilyRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FamilyRepositoryImpl @Inject constructor(
    private val familyMemberDao: FamilyMemberDao
) : FamilyRepository {

    override fun getFamilyMembers(): Flow<List<FamilyMember>> {
        return familyMemberDao.getAllFamilyMembers()
    }

    override suspend fun addFamilyMember(member: FamilyMember) {
        val memberToInsert = if (member.id.isEmpty()) {
            member.copy(id = UUID.randomUUID().toString())
        } else {
            member
        }
        familyMemberDao.insertFamilyMember(memberToInsert)
    }

    override suspend fun deleteFamilyMember(member: FamilyMember) {
        familyMemberDao.deleteFamilyMember(member)
    }
}
