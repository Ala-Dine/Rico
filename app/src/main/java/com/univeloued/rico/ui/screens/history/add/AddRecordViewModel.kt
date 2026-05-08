package com.univeloued.rico.ui.screens.history.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univeloued.rico.data.model.MedicalRecord
import com.univeloued.rico.domain.usecase.AddMedicalRecordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddRecordViewModel @Inject constructor(
    private val addMedicalRecordUseCase: AddMedicalRecordUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddRecordUiState())
    val uiState: StateFlow<AddRecordUiState> = _uiState.asStateFlow()

    fun onAction(action: AddRecordUiAction) {
        when (action) {
            is AddRecordUiAction.UpdateFileName -> {
                _uiState.update { it.copy(fileName = action.name) }
            }
            is AddRecordUiAction.UpdateRecordFor -> {
                _uiState.update { it.copy(recordFor = action.person) }
            }
            is AddRecordUiAction.UpdateRecordType -> {
                _uiState.update { it.copy(recordType = action.type) }
            }
            is AddRecordUiAction.UpdateCreatedOn -> {
                _uiState.update { it.copy(createdOn = action.date) }
            }
            is AddRecordUiAction.UpdateFileUri -> {
                _uiState.update { it.copy(selectedFileUri = action.uri) }
            }
            is AddRecordUiAction.SaveRecord -> {
                saveRecord()
            }
        }
    }

    private fun saveRecord() {
        val state = _uiState.value
        if (state.fileName.isBlank() || 
            state.recordFor.isBlank() || 
            state.recordType.isBlank() ||
            state.createdOn.isBlank() || 
            state.selectedFileUri == null
        ) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val record = MedicalRecord(
                id = "",
                fileName = state.fileName,
                recordFor = state.recordFor,
                recordType = state.recordType,
                createdOn = state.createdOn,
                fileUri = state.selectedFileUri
            )
            addMedicalRecordUseCase(record)
            _uiState.update { it.copy(isSaving = false, isSaved = true) }
        }
    }
}
