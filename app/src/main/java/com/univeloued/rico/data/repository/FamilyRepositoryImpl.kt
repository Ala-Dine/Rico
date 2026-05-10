package com.univeloued.rico.data.repository

import com.univeloued.rico.data.local.dao.FamilyMemberDao
import com.univeloued.rico.data.mapper.toDomain
import com.univeloued.rico.data.mapper.toEntity
import com.univeloued.rico.data.util.FileHelper
import com.univeloued.rico.domain.model.FamilyMember
import com.univeloued.rico.domain.repository.FamilyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FamilyRepositoryImpl @Inject constructor(
    private val familyMemberDao: FamilyMemberDao,
    private val fileHelper: FileHelper
) : FamilyRepository {

    override fun getFamilyMembers(): Flow<List<FamilyMember>> {
        return familyMemberDao.getAllFamilyMembers().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addFamilyMember(member: FamilyMember) {
        val internalPhotoUri = member.photoUri?.let { uriString ->
            if (uriString.startsWith("content://")) {
                fileHelper.saveFileToInternalStorage(android.net.Uri.parse(uriString), "family_photos")
            } else {
                uriString
            }
        }

        val memberToInsert = if (member.id.isEmpty()) {
            member.copy(
                id = UUID.randomUUID().toString(),
                photoUri = internalPhotoUri
            )
        } else {
            member.copy(photoUri = internalPhotoUri ?: member.photoUri)
        }
        familyMemberDao.insertFamilyMember(memberToInsert.toEntity())
    }

    override suspend fun deleteFamilyMember(member: FamilyMember) {
        familyMemberDao.deleteFamilyMember(member.toEntity())
    }
}
