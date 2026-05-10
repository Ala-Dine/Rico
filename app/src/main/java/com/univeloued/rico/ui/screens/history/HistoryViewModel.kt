package com.univeloued.rico.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univeloued.rico.domain.model.MedicalRecord
import com.univeloued.rico.domain.model.RecordType
import com.univeloued.rico.domain.usecase.GetMedicalRecordsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getMedicalRecordsUseCase: GetMedicalRecordsUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedFilter = MutableStateFlow(RecordType.ALL)
    private val _refreshTrigger = MutableStateFlow(0)

    val uiState: StateFlow<HistoryUiState> = combine(
        getMedicalRecordsUseCase(),
        _searchQuery,
        _selectedFilter,
        _refreshTrigger
    ) { records, query, filter, _ ->
        HistoryUiState(
            records = records,
            filteredRecords = filterRecords(records, filter, query),
            selectedFilter = filter,
            searchQuery = query,
            isLoading = false
        )
    }.onStart {
        emit(HistoryUiState(isLoading = true))
    }.catch { e ->
        emit(HistoryUiState(error = e.message ?: "An unexpected error occurred", isLoading = false))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HistoryUiState(isLoading = true)
    )

    fun onAction(action: HistoryUiAction) {
        when (action) {
            is HistoryUiAction.Search -> _searchQuery.update { action.query }
            is HistoryUiAction.Filter -> _selectedFilter.update { action.filter }
            HistoryUiAction.Refresh -> _refreshTrigger.update { it + 1 }
        }
    }

    private fun filterRecords(
        records: List<MedicalRecord>,
        filter: RecordType,
        query: String
    ): List<MedicalRecord> {
        return records.filter { record ->
            val matchesSearch = query.isEmpty() ||
                    record.fileName.contains(query, ignoreCase = true) ||
                    record.recordFor.contains(query, ignoreCase = true)

            val matchesFilter = filter == RecordType.ALL || record.recordType == filter

            matchesSearch && matchesFilter
        }
    }
}
