package com.univeloued.rico.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univeloued.rico.domain.model.UserProfile
import com.univeloued.rico.domain.usecase.GetUserProfileUseCase
import com.univeloued.rico.domain.usecase.UpdateUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState(isLoading = true))
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            getUserProfileUseCase()
                .onStart { _uiState.update { it.copy(isLoading = true) } }
                .collect { profile ->
                    _uiState.update { it.copy(userProfile = profile ?: UserProfile(), isLoading = false) }
                }
        }
    }

    fun onAction(action: ProfileUiAction) {
        when (action) {
            is ProfileUiAction.Refresh -> loadProfile()
            is ProfileUiAction.UpdatePhoto -> {
                viewModelScope.launch {
                    val currentProfile = _uiState.value.userProfile
                    val updatedProfile = currentProfile.copy(photoUri = action.uri)
                    updateUserProfileUseCase(updatedProfile)
                }
            }
        }
    }
}
