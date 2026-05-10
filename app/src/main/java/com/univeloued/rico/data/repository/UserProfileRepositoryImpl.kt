package com.univeloued.rico.data.repository

import com.univeloued.rico.data.local.dao.UserProfileDao
import com.univeloued.rico.data.mapper.toDomain
import com.univeloued.rico.data.mapper.toEntity
import com.univeloued.rico.domain.model.UserProfile
import com.univeloued.rico.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileRepositoryImpl @Inject constructor(
    private val userProfileDao: UserProfileDao
) : UserProfileRepository {

    override fun getUserProfile(): Flow<UserProfile?> {
        return userProfileDao.getUserProfile().map { it?.toDomain() }
    }

    override suspend fun updateUserProfile(profile: UserProfile) {
        userProfileDao.insertOrUpdateProfile(profile.toEntity())
    }
}
