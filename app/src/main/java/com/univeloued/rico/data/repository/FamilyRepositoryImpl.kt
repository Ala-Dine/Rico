package com.univeloued.rico.data.repository

import com.univeloued.rico.data.local.dao.FamilyMemberDao
import com.univeloued.rico.data.mapper.toDomain
import com.univeloued.rico.data.mapper.toEntity
import com.univeloued.rico.domain.model.FamilyMember
import com.univeloued.rico.domain.repository.FamilyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FamilyRepositoryImpl @Inject constructor(
    private val familyMemberDao: FamilyMemberDao
) : FamilyRepository {

    override fun getFamilyMembers(): Flow<List<FamilyMember>> {
        return familyMemberDao.getAllFamilyMembers().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addFamilyMember(member: FamilyMember) {
        val memberToInsert = if (member.id.isEmpty()) {
            member.copy(id = UUID.randomUUID().toString())
        } else {
            member
        }
        familyMemberDao.insertFamilyMember(memberToInsert.toEntity())
    }

    override suspend fun deleteFamilyMember(member: FamilyMember) {
        familyMemberDao.deleteFamilyMember(member.toEntity())
    }
}
