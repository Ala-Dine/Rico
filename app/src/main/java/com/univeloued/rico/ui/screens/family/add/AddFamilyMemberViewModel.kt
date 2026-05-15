package com.univeloued.rico.ui.screens.family.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univeloued.rico.domain.model.FamilyMember
import com.univeloued.rico.domain.usecase.AddFamilyMemberUseCase
import com.univeloued.rico.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddFamilyMemberViewModel @Inject constructor(
    private val addFamilyMemberUseCase: AddFamilyMemberUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddFamilyMemberUiState())
    val uiState: StateFlow<AddFamilyMemberUiState> = _uiState.asStateFlow()

    fun onAction(action: AddFamilyMemberUiAction) {
        when (action) {
            is AddFamilyMemberUiAction.UpdateName -> {
                _uiState.update { it.copy(name = action.name) }
            }
            is AddFamilyMemberUiAction.UpdateRelationship -> {
                _uiState.update { it.copy(relationship = action.relationship) }
            }
            is AddFamilyMemberUiAction.UpdateBirthdate -> {
                _uiState.update { it.copy(birthdate = action.birthdate) }
            }
            is AddFamilyMemberUiAction.UpdateGender -> {
                _uiState.update { it.copy(gender = action.gender) }
            }
            is AddFamilyMemberUiAction.UpdatePhotoUri -> {
                _uiState.update { it.copy(photoUri = action.uri) }
            }
            is AddFamilyMemberUiAction.SaveMember -> {
                saveMember()
            }
        }
    }

    private fun saveMember() {
        val state = _uiState.value
        if (state.name.isBlank() || state.relationship.isBlank() || state.birthdate.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            val member = FamilyMember(
                id = "",
                name = state.name,
                relationship = state.relationship,
                birthdate = state.birthdate,
                gender = state.gender,
                photoUri = state.photoUri
            )
            val result = addFamilyMemberUseCase(member)
            when (result) {
                is Resource.Error -> {
                    _uiState.update { it.copy(isSaving = false, error = result.message) }
                }
                is Resource.Success -> {
                    _uiState.update { it.copy(isSaving = false, isSaved = true) }
                }
                is Resource.Loading -> {}
            }
        }
    }
}
