package com.univeloued.rico.data.repository

import com.univeloued.rico.data.model.UserProfile
import com.univeloued.rico.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileRepositoryImpl @Inject constructor() : UserProfileRepository {
    private val _userProfile = MutableStateFlow(UserProfile())
    override val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    override fun updateUserProfile(profile: UserProfile) {
        _userProfile.value = profile
    }
}
