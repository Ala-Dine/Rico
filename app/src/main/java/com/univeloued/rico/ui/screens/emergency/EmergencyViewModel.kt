package com.univeloued.rico.ui.screens.emergency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univeloued.rico.domain.usecase.GetEmergencyContactsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmergencyViewModel @Inject constructor(
    private val getEmergencyContactsUseCase: GetEmergencyContactsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmergencyUiState(isLoading = true))
    val uiState: StateFlow<EmergencyUiState> = _uiState.asStateFlow()

    init {
        loadContacts()
    }

    private fun loadContacts() {
        viewModelScope.launch {
            getEmergencyContactsUseCase()
                .onStart { _uiState.update { it.copy(isLoading = true) } }
                .collect { contacts ->
                    _uiState.update { it.copy(contacts = contacts, isLoading = false) }
                }
        }
    }

    fun onAction(action: EmergencyUiAction) {
        when (action) {
            is EmergencyUiAction.Refresh -> loadContacts()
        }
    }
}
