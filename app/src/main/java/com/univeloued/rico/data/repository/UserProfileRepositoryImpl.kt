package com.univeloued.rico.data.repository

import com.univeloued.rico.data.local.dao.UserProfileDao
import com.univeloued.rico.data.mapper.toDomain
import com.univeloued.rico.data.mapper.toEntity
import com.univeloued.rico.data.util.FileHelper
import com.univeloued.rico.data.security.MasterKeyManager
import com.univeloued.rico.domain.model.UserProfile
import com.univeloued.rico.domain.repository.AuthRepository
import com.univeloued.rico.domain.repository.UserProfileRepository
import com.univeloued.rico.domain.sync.SyncManager
import com.univeloued.rico.util.Constants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class UserProfileRepositoryImpl @Inject constructor(
    private val userProfileDaoProvider: Provider<UserProfileDao>,
    private val fileHelper: FileHelper,
    private val authRepository: AuthRepository
) : UserProfileRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getUserProfile(): Flow<UserProfile?> {
        return authRepository.currentUserFlow.flatMapLatest { userId ->
            if (userId != null) {
                userProfileDaoProvider.get().getUserProfile(userId).map { 
                    it?.toDomain()
                }
            } else {
                flowOf(null)
            }
        }
    }

    override suspend fun updateUserProfile(profile: UserProfile) {
        val userId = authRepository.getCurrentUserId() ?: return
        val internalPhotoUri = profile.photoUri?.let { uriString ->
            if (uriString.startsWith("content://")) {
                fileHelper.saveFileToInternalStorage(android.net.Uri.parse(uriString), Constants.DIR_PROFILE_PHOTOS)
            } else {
                uriString
            }
        }
        userProfileDaoProvider.get().insertOrUpdateProfile(
            profile.copy(id = userId, photoUri = internalPhotoUri).toEntity()
        )
        // Profile sync is usually triggered automatically or by manual refresh in this app's logic
    }
}
