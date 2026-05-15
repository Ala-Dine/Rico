package com.univeloued.rico.domain.repository

import com.univeloued.rico.domain.util.ValidationResult
import com.univeloued.rico.util.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUserFlow: Flow<String?>
    val sessionStatusFlow: Flow<io.github.jan.supabase.auth.status.SessionStatus>
    suspend fun signUp(email: String, password: String): ValidationResult
    suspend fun signIn(email: String, password: String): ValidationResult
    suspend fun signOut()
    suspend fun resetPassword(email: String): ValidationResult
    suspend fun updatePassword(newPassword: String): ValidationResult
    fun handleDeepLink(intent: android.content.Intent)
    fun isResetPasswordFlow(intentData: android.net.Uri?): Boolean
    fun getCurrentUserId(): String?
    suspend fun clearLocalData()
    suspend fun fetchAndSaveSalt(): Resource<Unit>
}
