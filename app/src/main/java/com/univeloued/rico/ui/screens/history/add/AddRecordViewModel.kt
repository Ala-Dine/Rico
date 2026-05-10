package com.univeloued.rico.ui.screens.history.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univeloued.rico.domain.model.MedicalRecord
import com.univeloued.rico.domain.usecase.AddMedicalRecordUseCase
import com.univeloued.rico.domain.util.ValidationResult
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
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            val record = MedicalRecord(
                id = "",
                fileName = state.fileName,
                recordFor = state.recordFor,
                recordType = state.recordType,
                createdOn = state.createdOn,
                fileUri = state.selectedFileUri.toString()
            )
            
            when (val result = addMedicalRecordUseCase(record)) {
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
