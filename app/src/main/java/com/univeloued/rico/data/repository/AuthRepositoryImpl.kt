package com.univeloued.rico.data.repository

import android.util.Log
import com.univeloued.rico.data.local.RicoDatabase
import com.univeloued.rico.data.remote.SupabaseRemoteDataSource
import com.univeloued.rico.data.security.DatabasePassphraseManager
import com.univeloued.rico.data.security.MasterKeyManager
import com.univeloued.rico.domain.repository.AuthRepository
import com.univeloued.rico.domain.util.ValidationResult
import com.univeloued.rico.util.Resource
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.handleDeeplinks
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.exception.AuthWeakPasswordException
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val databaseProvider: Provider<RicoDatabase>,
    private val remoteDataSource: SupabaseRemoteDataSource,
    private val passphraseManager: DatabasePassphraseManager,
    private val masterKeyManager: MasterKeyManager
) : AuthRepository {

    private val auth = supabaseClient.auth

    override val currentUserFlow: Flow<String?> = auth.sessionStatus.map { status ->
        Log.d("AuthRepository", "Session status changed: $status")
        when (status) {
            is SessionStatus.Authenticated -> status.session.user?.id
            else -> null
        }
    }

    override val sessionStatusFlow: Flow<SessionStatus> = auth.sessionStatus

    override suspend fun signUp(email: String, password: String): ValidationResult {
        return try {
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            ValidationResult.Success
        } catch (e: AuthWeakPasswordException) {
            ValidationResult.Error("Password is too weak: ${e.reasons.joinToString(", ")}")
        } catch (e: AuthRestException) {
            Log.e("AuthRepository", "Supabase Auth Error: ${e.message}", e)
            val message = when {
                e.message?.contains("invalid_credentials", ignoreCase = true) == true -> 
                    "Invalid email or password"
                e.message?.contains("user_not_found", ignoreCase = true) == true -> 
                    "No account found with this email"
                e.message?.contains("email_exists", ignoreCase = true) == true ->
                    "An account with this email already exists"
                e.message?.contains("over_email_send_rate_limit", ignoreCase = true) == true ->
                    "Too many requests. Please wait a while before trying again."
                e.message?.contains("unexpected_failure", ignoreCase = true) == true ->
                    "Server error. Please verify your Supabase API keys."
                else -> e.message?.lineSequence()?.firstOrNull() ?: "An error occurred"
            }
            ValidationResult.Error(message)
        } catch (e: Exception) {
            Log.e("AuthRepository", "General Auth Error", e)
            ValidationResult.Error(e.message ?: "Auth failed")
        }
    }

    override suspend fun signIn(email: String, password: String): ValidationResult {
        return try {
            auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            ValidationResult.Success
        } catch (e: AuthRestException) {
            Log.e("AuthRepository", "Supabase Auth Error: ${e.message}", e)
            val message = when {
                e.message?.contains("invalid_credentials", ignoreCase = true) == true -> 
                    "Invalid email or password"
                e.message?.contains("user_not_found", ignoreCase = true) == true -> 
                    "No account found with this email"
                e.message?.contains("over_email_send_rate_limit", ignoreCase = true) == true ->
                    "Too many requests. Please wait a while before trying again."
                e.message?.contains("unexpected_failure", ignoreCase = true) == true ->
                    "Server error. Please verify your Supabase API keys."
                else -> e.message?.lineSequence()?.firstOrNull() ?: "An error occurred"
            }
            ValidationResult.Error(message)
        } catch (e: Exception) {
            Log.e("AuthRepository", "General Auth Error", e)
            ValidationResult.Error(e.message ?: "Auth failed")
        }
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun resetPassword(email: String): ValidationResult {
        return try {
            auth.resetPasswordForEmail(email, redirectUrl = "rico://reset-password")
            ValidationResult.Success
        } catch (e: AuthRestException) {
            ValidationResult.Error(e.message ?: "Reset password failed")
        } catch (e: Exception) {
            ValidationResult.Error(e.message ?: "Reset password failed")
        }
    }

    override suspend fun updatePassword(newPassword: String): ValidationResult {
        return try {
            auth.updateUser {
                password = newPassword
            }
            ValidationResult.Success
        } catch (e: AuthRestException) {
            ValidationResult.Error(e.message ?: "Failed to update password")
        } catch (e: Exception) {
            ValidationResult.Error(e.message ?: "Failed to update password")
        }
    }

    override fun isResetPasswordFlow(intentData: android.net.Uri?): Boolean {
        if (intentData == null) return false
        val fragment = intentData.fragment ?: ""
        return fragment.contains("type=recovery") || intentData.toString().contains("type=recovery")
    }

    override fun handleDeepLink(intent: android.content.Intent) {
        supabaseClient.handleDeeplinks(intent)
    }

    override fun getCurrentUserId(): String? {
        return auth.currentUserOrNull()?.id
    }

    override suspend fun clearLocalData() {
        withContext(Dispatchers.IO) {
            databaseProvider.get().clearAllTables()
        }
    }

    override suspend fun fetchAndSaveSalt(): Resource<Unit> {
        return try {
            val userId = getCurrentUserId() ?: return Resource.Error("Not logged in")
            val remoteProfile = remoteDataSource.getUserProfile(userId)
            remoteProfile?.encryptionSalt?.let { saltString ->
                val salt = masterKeyManager.stringToSalt(saltString)
                passphraseManager.setEncryptionSalt(salt)
                Resource.Success(Unit)
            } ?: Resource.Error("No encryption settings found for this account")
        } catch (e: Exception) {
            Log.e("AuthRepository", "Failed to fetch salt", e)
            Resource.Error("Failed to fetch security settings")
        }
    }
}
