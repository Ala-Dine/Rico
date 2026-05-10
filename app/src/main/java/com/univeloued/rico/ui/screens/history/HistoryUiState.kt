package com.univeloued.rico.ui.screens.history

import com.univeloued.rico.domain.model.MedicalRecord

data class HistoryUiState(
    val records: List<MedicalRecord> = emptyList(),
    val filteredRecords: List<MedicalRecord> = emptyList(),
    val selectedFilter: String = "All",
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)
