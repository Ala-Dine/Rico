package com.univeloued.rico.ui.security

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univeloued.rico.data.security.DatabasePassphraseManager
import com.univeloued.rico.data.security.MasterKeyManager
import com.univeloued.rico.domain.model.UserProfile
import com.univeloued.rico.domain.repository.AuthRepository
import com.univeloued.rico.domain.repository.UserProfileRepository
import com.univeloued.rico.domain.sync.SyncManager
import com.univeloued.rico.domain.util.ValidationResult
import com.univeloued.rico.domain.util.Validator
import io.github.jan.supabase.auth.status.SessionStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SupabaseAuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userProfileRepository: UserProfileRepository,
    private val masterKeyManager: MasterKeyManager,
    private val databasePassphraseManager: DatabasePassphraseManager,
    private val syncManager: SyncManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SupabaseAuthUiState())
    val uiState: StateFlow<SupabaseAuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.sessionStatusFlow.collect { status ->
                when (status) {
                    is SessionStatus.Initializing -> {
                        _uiState.update { it.copy(isSessionLoaded = false) }
                    }
                    is SessionStatus.Authenticated -> {
                        authRepository.fetchAndSaveSalt()
                        syncManager.triggerManualSync()
                        _uiState.update { it.copy(
                            isAuthenticated = true,
                            isSessionLoaded = true,
                            isLoading = false
                        ) }
                    }
                    is SessionStatus.NotAuthenticated, is SessionStatus.RefreshFailure -> {
                        _uiState.update { it.copy(
                            isAuthenticated = false,
                            isSessionLoaded = true,
                            isLoading = false
                        ) }
                    }
                }
            }
        }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun onMasterPasswordChange(password: String) {
        _uiState.update { it.copy(masterPassword = password) }
    }

    fun onSignIn() {
        val emailResult = Validator.validateEmail(_uiState.value.email)
        if (emailResult is ValidationResult.Error) {
            _uiState.update { it.copy(error = emailResult.message) }
            return
        }
        if (_uiState.value.password.length < 6) {
            _uiState.update { it.copy(error = "Login password must be at least 6 characters") }
            return
        }
        if (_uiState.value.masterPassword.isEmpty()) {
            _uiState.update { it.copy(error = "Master Password is required to decrypt your data") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = authRepository.signIn(_uiState.value.email, _uiState.value.password)
            if (result is ValidationResult.Error) {
                _uiState.update { it.copy(isLoading = false, error = result.message) }
            } else {
                // Save Master Password temporarily for key derivation
                databasePassphraseManager.setMasterPassword(_uiState.value.masterPassword.toByteArray(Charsets.UTF_8))

                // Successful sign in, now fetch salt
                var saltFetched = false
                repeat(3) {
                    if (!saltFetched) {
                        val saltResult = authRepository.fetchAndSaveSalt()
                        if (saltResult is com.univeloued.rico.util.Resource.Success) {
                            saltFetched = true
                        } else {
                            kotlinx.coroutines.delay(1000)
                        }
                    }
                }
                
                // Trigger immediate sync to pull data
                syncManager.triggerManualSync()

                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onSignUp() {
        val emailResult = Validator.validateEmail(_uiState.value.email)
        if (emailResult is ValidationResult.Error) {
            _uiState.update { it.copy(error = emailResult.message) }
            return
        }
        if (_uiState.value.password.length < 6) {
            _uiState.update { it.copy(error = "Login password must be at least 6 characters") }
            return
        }
        if (_uiState.value.masterPassword.length < 6) {
            _uiState.update { it.copy(error = "Master Password must be at least 6 characters") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, successMessage = null) }
            val result = authRepository.signUp(_uiState.value.email, _uiState.value.password)
            if (result is ValidationResult.Error) {
                _uiState.update { it.copy(isLoading = false, error = result.message) }
            } else {
                // Check if user is authenticated immediately
                val currentUserId = authRepository.getCurrentUserId()
                if (currentUserId != null) {
                    // Create initial profile with encryption salt
                    val newSalt = masterKeyManager.generateSalt()
                    val saltString = masterKeyManager.saltToString(newSalt)
                    
                    // Set local salt and password for immediate use
                    databasePassphraseManager.setEncryptionSalt(newSalt)
                    databasePassphraseManager.setMasterPassword(_uiState.value.masterPassword.toByteArray(Charsets.UTF_8))

                    userProfileRepository.updateUserProfile(
                        UserProfile(
                            id = currentUserId, 
                            email = _uiState.value.email,
                            encryptionSalt = saltString
                        )
                    )
                    
                    // Trigger initial sync
                    syncManager.triggerManualSync()

                    _uiState.update { it.copy(isLoading = false) }
                } else {
                    _uiState.update { it.copy(
                        isLoading = false, 
                        successMessage = "Sign up successful! Please check your email to confirm your account." 
                    ) }
                }
            }
        }
    }

    fun onSignOut() {
        viewModelScope.launch {
            // We no longer clear local data on sign out so that records persist
            // for when the user logs back in. Data is already partitioned by userId.
            authRepository.signOut()
            _uiState.value = SupabaseAuthUiState()
        }
    }

    fun onNewPasswordChange(password: String) {
        _uiState.update { it.copy(newPassword = password) }
    }

    fun onUpdatePassword() {
        if (_uiState.value.newPassword.length < 6) {
            _uiState.update { it.copy(error = "Password must be at least 6 characters") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = authRepository.updatePassword(_uiState.value.newPassword)
            if (result is ValidationResult.Error) {
                _uiState.update { it.copy(isLoading = false, error = result.message) }
            } else {
                _uiState.update { it.copy(isLoading = false, isPasswordReset = false, successMessage = "Password updated successfully!") }
            }
        }
    }

    fun checkDeepLink(uri: android.net.Uri?) {
        if (authRepository.isResetPasswordFlow(uri)) {
            _uiState.update { it.copy(isPasswordReset = true) }
        }
    }

    fun onResetPassword() {
        // ... existing code ...
    }
}

data class SupabaseAuthUiState(
    val email: String = "",
    val password: String = "",
    val masterPassword: String = "",
    val newPassword: String = "",
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val isSessionLoaded: Boolean = false,
    val isPasswordReset: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)
