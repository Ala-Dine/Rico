package com.univeloued.rico.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univeloued.rico.domain.usecase.GetMedicalRecordsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getMedicalRecordsUseCase: GetMedicalRecordsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState(isLoading = true))
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadRecords()
    }

    private fun loadRecords() {
        viewModelScope.launch {
            getMedicalRecordsUseCase()
                .onStart { _uiState.update { it.copy(isLoading = true) } }
                .collect { records ->
                    _uiState.update { 
                        it.copy(
                            records = records, 
                            filteredRecords = filterRecords(records, it.selectedFilter, it.searchQuery),
                            isLoading = false 
                        ) 
                    }
                }
        }
    }

    fun onAction(action: HistoryUiAction) {
        when (action) {
            is HistoryUiAction.Search -> {
                _uiState.update { 
                    it.copy(
                        searchQuery = action.query,
                        filteredRecords = filterRecords(it.records, it.selectedFilter, action.query)
                    )
                }
            }
            is HistoryUiAction.Filter -> {
                _uiState.update { 
                    it.copy(
                        selectedFilter = action.filter,
                        filteredRecords = filterRecords(it.records, action.filter, it.searchQuery)
                    )
                }
            }
            HistoryUiAction.Refresh -> loadRecords()
        }
    }

    private fun filterRecords(records: List<com.univeloued.rico.data.model.MedicalRecord>, filter: String, query: String): List<com.univeloued.rico.data.model.MedicalRecord> {
        return records.filter { record ->
            val matchesSearch = record.fileName.contains(query, ignoreCase = true) || 
                               record.recordFor.contains(query, ignoreCase = true)
            val matchesFilter = if (filter == "All") true else record.recordType == filter
            matchesSearch && matchesFilter
        }
    }
}
