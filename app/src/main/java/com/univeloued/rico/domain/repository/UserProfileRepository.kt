package com.univeloued.rico.domain.repository

import com.univeloued.rico.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    fun getUserProfile(): Flow<UserProfile?>
    suspend fun updateUserProfile(profile: UserProfile)
}
