package com.univeloued.rico.ui.screens.family

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univeloued.rico.domain.usecase.GetFamilyMembersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FamilyViewModel @Inject constructor(
    private val getFamilyMembersUseCase: GetFamilyMembersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FamilyUiState(isLoading = true))
    val uiState: StateFlow<FamilyUiState> = _uiState.asStateFlow()

    init {
        loadFamilyMembers()
    }

    private fun loadFamilyMembers() {
        viewModelScope.launch {
            getFamilyMembersUseCase()
                .onStart { _uiState.update { it.copy(isLoading = true) } }
                .collect { members ->
                    _uiState.update { it.copy(familyMembers = members, isLoading = false) }
                }
        }
    }

    fun onAction(action: FamilyUiAction) {
        when (action) {
            is FamilyUiAction.Refresh -> loadFamilyMembers()
        }
    }
}
