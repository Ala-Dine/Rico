package com.univeloued.rico.domain.usecase

import android.util.Log
import com.univeloued.rico.domain.model.FamilyMember
import com.univeloued.rico.domain.repository.AuthRepository
import com.univeloued.rico.domain.repository.FamilyRepository
import com.univeloued.rico.util.Resource
import com.univeloued.rico.domain.util.Validator
import javax.inject.Inject

class AddFamilyMemberUseCase @Inject constructor(
    private val repository: FamilyRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(member: FamilyMember): Resource<Unit> {
        return try {
            if (authRepository.getCurrentUserId() == null) {
                return Resource.Error("User not authenticated. Please log in again.")
            }

            Validator.validateRequiredField(member.name, "Name").let { if (it is com.univeloued.rico.domain.util.ValidationResult.Error) return Resource.Error(it.message) }
            Validator.validateRequiredField(member.relationship, "Relationship").let { if (it is com.univeloued.rico.domain.util.ValidationResult.Error) return Resource.Error(it.message) }

            repository.addFamilyMember(member)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Log.e("AddFamilyMemberUseCase", "Failed to add family member", e)
            Resource.Error("An unexpected error occurred: ${e.localizedMessage}")
        }
    }
}
