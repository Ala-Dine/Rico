package com.univeloued.rico.ui.screens.history

import com.univeloued.rico.domain.model.RecordType

sealed interface HistoryUiAction {
    data class Search(val query: String) : HistoryUiAction
    data class Filter(val filter: RecordType) : HistoryUiAction
    data object Refresh : HistoryUiAction
}
