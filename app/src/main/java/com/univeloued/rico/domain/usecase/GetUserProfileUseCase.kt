package com.univeloued.rico.domain.usecase

import com.univeloued.rico.domain.model.UserProfile
import com.univeloued.rico.domain.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val repository: UserProfileRepository
) {
    operator fun invoke(): Flow<UserProfile?> {
        return repository.getUserProfile()
    }
}
