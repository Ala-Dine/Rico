package com.univeloued.rico.domain.repository

import com.univeloued.rico.data.model.UserProfile
import kotlinx.coroutines.flow.StateFlow

interface UserProfileRepository {
    val userProfile: StateFlow<UserProfile>
    fun updateUserProfile(profile: UserProfile)
}
