package com.univeloued.rico.ui.screens.emergency.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univeloued.rico.domain.model.EmergencyContact
import com.univeloued.rico.domain.usecase.AddEmergencyContactUseCase
import com.univeloued.rico.domain.util.ValidationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEmergencyContactViewModel @Inject constructor(
    private val addEmergencyContactUseCase: AddEmergencyContactUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEmergencyContactUiState())
    val uiState: StateFlow<AddEmergencyContactUiState> = _uiState.asStateFlow()

    fun onAction(action: AddEmergencyContactUiAction) {
        when (action) {
            is AddEmergencyContactUiAction.UpdateName -> {
                _uiState.update { it.copy(name = action.name) }
            }
            is AddEmergencyContactUiAction.UpdatePhone -> {
                _uiState.update { it.copy(phone = action.phone) }
            }
            is AddEmergencyContactUiAction.UpdateEmail -> {
                _uiState.update { it.copy(email = action.email) }
            }
            is AddEmergencyContactUiAction.UpdatePhotoUri -> {
                _uiState.update { it.copy(photoUri = action.uri) }
            }
            is AddEmergencyContactUiAction.SaveContact -> {
                saveContact()
            }
        }
    }

    private fun saveContact() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            val contact = EmergencyContact(
                id = "",
                name = state.name,
                phone = state.phone,
                email = state.email,
                photoUri = state.photoUri
            )
            when (val result = addEmergencyContactUseCase(contact)) {
                is ValidationResult.Error -> {
                    _uiState.update { it.copy(isSaving = false, error = result.message) }
                }
                ValidationResult.Success -> {
                    _uiState.update { it.copy(isSaving = false, isSaved = true) }
                }
            }
        }
    }
}
