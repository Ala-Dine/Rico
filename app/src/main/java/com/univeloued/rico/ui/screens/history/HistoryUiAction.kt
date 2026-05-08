package com.univeloued.rico.ui.screens.history

sealed interface HistoryUiAction {
    data class Search(val query: String) : HistoryUiAction
    data class Filter(val filter: String) : HistoryUiAction
    data object Refresh : HistoryUiAction
}
