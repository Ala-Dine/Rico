package com.univeloued.rico.data.repository

import com.univeloued.rico.data.local.dao.FamilyMemberDao
import com.univeloued.rico.data.mapper.toDomain
import com.univeloued.rico.data.mapper.toEntity
import com.univeloued.rico.data.util.FileHelper
import com.univeloued.rico.domain.model.FamilyMember
import com.univeloued.rico.domain.repository.AuthRepository
import com.univeloued.rico.domain.repository.FamilyRepository
import com.univeloued.rico.domain.sync.SyncManager
import com.univeloued.rico.util.Constants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class FamilyRepositoryImpl @Inject constructor(
    private val familyMemberDaoProvider: Provider<FamilyMemberDao>,
    private val fileHelper: FileHelper,
    private val authRepository: AuthRepository,
    private val syncManager: SyncManager
) : FamilyRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getFamilyMembers(): Flow<List<FamilyMember>> {
        return authRepository.currentUserFlow.flatMapLatest { userId ->
            if (userId != null) {
                familyMemberDaoProvider.get().getAllFamilyMembers(userId).map { entities ->
                    entities.map { it.toDomain() }
                }
            } else {
                flowOf(emptyList())
            }
        }
    }

    override suspend fun addFamilyMember(member: FamilyMember) {
        val userId = authRepository.getCurrentUserId() ?: return
        val internalPhotoUri = member.photoUri?.let { uriString ->
            if (uriString.startsWith("content://")) {
                fileHelper.saveFileToInternalStorage(android.net.Uri.parse(uriString), Constants.DIR_FAMILY_PHOTOS)
            } else {
                uriString
            }
        }

        val memberToInsert = if (member.id.isEmpty()) {
            member.copy(id = UUID.randomUUID().toString(), photoUri = internalPhotoUri)
        } else {
            member.copy(photoUri = internalPhotoUri ?: member.photoUri)
        }
        familyMemberDaoProvider.get().insertFamilyMember(memberToInsert.toEntity().copy(userId = userId))
        syncManager.scheduleFamilySync()
    }

    override suspend fun deleteFamilyMember(member: FamilyMember) {
        familyMemberDaoProvider.get().deleteFamilyMember(member.toEntity())
        syncManager.scheduleFamilySync()
    }
}
