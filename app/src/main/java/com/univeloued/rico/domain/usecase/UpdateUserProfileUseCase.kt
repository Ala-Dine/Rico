package com.univeloued.rico.domain.usecase

import com.univeloued.rico.data.model.UserProfile
import com.univeloued.rico.domain.repository.UserProfileRepository
import javax.inject.Inject

class UpdateUserProfileUseCase @Inject constructor(
    private val repository: UserProfileRepository
) {
    operator fun invoke(profile: UserProfile) {
        repository.updateUserProfile(profile)
    }
}
