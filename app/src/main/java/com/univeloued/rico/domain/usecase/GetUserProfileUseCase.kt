package com.univeloued.rico.domain.usecase

import com.univeloued.rico.data.model.UserProfile
import com.univeloued.rico.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val repository: UserProfileRepository
) {
    operator fun invoke(): StateFlow<UserProfile> {
        return repository.userProfile
    }
}
