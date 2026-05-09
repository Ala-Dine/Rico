package com.univeloued.rico.ui.screens.reminders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univeloued.rico.domain.usecase.GetRemindersUseCase
import com.univeloued.rico.domain.usecase.UpdateReminderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RemindersViewModel @Inject constructor(
    private val getRemindersUseCase: GetRemindersUseCase,
    private val updateReminderUseCase: UpdateReminderUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RemindersUiState(isLoading = true))
    val uiState: StateFlow<RemindersUiState> = _uiState.asStateFlow()

    init {
        loadReminders()
    }

    private fun loadReminders() {
        viewModelScope.launch {
            getRemindersUseCase()
                .onStart { _uiState.update { it.copy(isLoading = true) } }
                .collect { reminders ->
                    _uiState.update { it.copy(reminders = reminders, isLoading = false) }
                }
        }
    }

    fun onAction(action: RemindersUiAction) {
        when (action) {
            is RemindersUiAction.Refresh -> loadReminders()
            is RemindersUiAction.DeleteReminder -> {
                // Implement delete use case if available
            }
            is RemindersUiAction.ToggleReminder -> {
                viewModelScope.launch {
                    val updatedReminder = action.reminder.copy(isActive = !action.reminder.isActive)
                    updateReminderUseCase(updatedReminder)
                }
            }
        }
    }
}
