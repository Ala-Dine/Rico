package com.univeloued.rico.ui.screens.history.add

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univeloued.rico.domain.model.MedicalRecord
import com.univeloued.rico.domain.model.RecordType
import com.univeloued.rico.domain.usecase.AddMedicalRecordUseCase
import com.univeloued.rico.util.Resource
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
        try {
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
        } catch (e: Exception) {
            Log.e("AddRecordVM", "Error in onAction", e)
            _uiState.update { it.copy(error = "Action failed: ${e.localizedMessage}") }
        }
    }

    private fun saveRecord() {
        val state = _uiState.value
        val recordType = state.recordType ?: return
        
        if (state.fileName.isBlank() || 
            state.recordFor.isBlank() || 
            state.createdOn.isBlank() || 
            state.selectedFileUri == null
        ) return

        Log.d("AddRecordVM", "Saving record: ${state.fileName}")
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isSaving = true, error = null) }
                val record = MedicalRecord(
                    id = "",
                    fileName = state.fileName,
                    recordFor = state.recordFor,
                    recordType = recordType,
                    createdOn = state.createdOn,
                    fileUri = state.selectedFileUri.toString()
                )
                val result = addMedicalRecordUseCase(record)
                when (result) {
                    is Resource.Error -> {
                        Log.e("AddRecordVM", "Error: ${result.message}")
                        _uiState.update { it.copy(isSaving = false, error = result.message) }
                    }
                    is Resource.Success -> {
                        Log.d("AddRecordVM", "Save successful")
                        _uiState.update { it.copy(isSaving = false, isSaved = true) }
                    }
                    is Resource.Loading -> { /* Handled by isSaving */ }
                }
            } catch (e: Exception) {
                Log.e("AddRecordVM", "Error saving record", e)
                _uiState.update { it.copy(isSaving = false, error = "An unexpected error occurred: ${e.localizedMessage}") }
            }
        }
    }
}
