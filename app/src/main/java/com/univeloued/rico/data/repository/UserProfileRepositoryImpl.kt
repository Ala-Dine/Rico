package com.univeloued.rico.data.repository

import com.univeloued.rico.data.local.dao.UserProfileDao
import com.univeloued.rico.data.model.UserProfile
import com.univeloued.rico.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileRepositoryImpl @Inject constructor(
    private val userProfileDao: UserProfileDao
) : UserProfileRepository {

    override fun getUserProfile(): Flow<UserProfile?> {
        return userProfileDao.getUserProfile()
    }

    override suspend fun updateUserProfile(profile: UserProfile) {
        userProfileDao.insertOrUpdateProfile(profile)
    }
}
