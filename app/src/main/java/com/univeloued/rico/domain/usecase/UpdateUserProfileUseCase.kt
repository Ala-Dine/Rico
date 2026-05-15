package com.univeloued.rico.domain.usecase

import android.util.Log
import com.univeloued.rico.domain.model.UserProfile
import com.univeloued.rico.domain.repository.AuthRepository
import com.univeloued.rico.domain.util.ValidationResult
import com.univeloued.rico.domain.repository.UserProfileRepository
import javax.inject.Inject

class UpdateUserProfileUseCase @Inject constructor(
    private val repository: UserProfileRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(profile: UserProfile): ValidationResult {
        try {
            if (authRepository.getCurrentUserId() == null) {
                return ValidationResult.Error("User not authenticated")
            }
            repository.updateUserProfile(profile)
            return ValidationResult.Success
        } catch (e: Exception) {
            Log.e("UpdateUserProfileUC", "Failed to update profile", e)
            return ValidationResult.Error("An unexpected error occurred: ${e.localizedMessage}")
        }
    }
}
