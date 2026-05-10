package com.univeloued.rico.ui.screens.profile.edit

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
class EditProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            getUserProfileUseCase().take(1).collect { profileOrNull ->
                val profile = profileOrNull ?: UserProfile()
                _uiState.update { it.copy(
                    name = profile.name,
                    birthdate = profile.birthdate,
                    gender = profile.gender,
                    bloodType = profile.bloodType,
                    insuranceNumber = profile.insuranceNumber,
                    address = profile.address,
                    phone = profile.phone,
                    email = profile.email,
                    notes = profile.notes,
                    photoUri = profile.photoUri
                ) }
            }
        }
    }

    fun onAction(action: EditProfileUiAction) {
        when (action) {
            is EditProfileUiAction.UpdateName -> _uiState.update { it.copy(name = action.name) }
            is EditProfileUiAction.UpdateBirthdate -> _uiState.update { it.copy(birthdate = action.birthdate) }
            is EditProfileUiAction.UpdateGender -> _uiState.update { it.copy(gender = action.gender) }
            is EditProfileUiAction.UpdateBloodType -> _uiState.update { it.copy(bloodType = action.bloodType) }
            is EditProfileUiAction.UpdateInsuranceNumber -> _uiState.update { it.copy(insuranceNumber = action.insuranceNumber) }
            is EditProfileUiAction.UpdateAddress -> _uiState.update { it.copy(address = action.address) }
            is EditProfileUiAction.UpdatePhone -> _uiState.update { it.copy(phone = action.phone) }
            is EditProfileUiAction.UpdateEmail -> _uiState.update { it.copy(email = action.email) }
            is EditProfileUiAction.UpdateNotes -> _uiState.update { it.copy(notes = action.notes) }
            is EditProfileUiAction.UpdatePhotoUri -> _uiState.update { it.copy(photoUri = action.uri) }
            EditProfileUiAction.SaveProfile -> saveProfile()
        }
    }

    private fun saveProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val state = _uiState.value
            val profile = UserProfile(
                name = state.name,
                birthdate = state.birthdate,
                gender = state.gender,
                bloodType = state.bloodType,
                insuranceNumber = state.insuranceNumber,
                address = state.address,
                phone = state.phone,
                email = state.email,
                notes = state.notes,
                photoUri = state.photoUri
            )
            updateUserProfileUseCase(profile)
            _uiState.update { it.copy(isSaving = false, isSaved = true) }
        }
    }
}
