package com.univeloued.rico.ui.screens.reminders.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univeloued.rico.data.model.Reminder
import com.univeloued.rico.domain.usecase.AddReminderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddReminderViewModel @Inject constructor(
    private val addReminderUseCase: AddReminderUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddReminderUiState())
    val uiState: StateFlow<AddReminderUiState> = _uiState.asStateFlow()

    fun onAction(action: AddReminderUiAction) {
        when (action) {
            is AddReminderUiAction.UpdateMedicineName -> {
                _uiState.update { it.copy(medicineName = action.name) }
            }
            is AddReminderUiAction.UpdateUnit -> {
                _uiState.update { it.copy(unit = action.unit) }
            }
            is AddReminderUiAction.UpdateFrequency -> {
                _uiState.update { it.copy(frequency = action.frequency) }
            }
            is AddReminderUiAction.UpdateTime -> {
                _uiState.update { it.copy(time = action.time) }
            }
            is AddReminderUiAction.UpdateDuration -> {
                _uiState.update { it.copy(duration = action.duration) }
            }
            is AddReminderUiAction.UpdateIntakeMethod -> {
                _uiState.update { it.copy(intakeMethod = action.method) }
            }
            is AddReminderUiAction.SaveReminder -> {
                saveReminder()
            }
        }
    }

    private fun saveReminder() {
        val state = _uiState.value
        if (state.medicineName.isBlank() || state.unit.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val reminder = Reminder(
                id = "",
                medicineName = state.medicineName,
                unit = state.unit,
                frequency = state.frequency,
                time = state.time,
                duration = state.duration,
                intakeMethod = state.intakeMethod
            )
            addReminderUseCase(reminder)
            _uiState.update { it.copy(isSaving = false, isSaved = true) }
        }
    }
}
