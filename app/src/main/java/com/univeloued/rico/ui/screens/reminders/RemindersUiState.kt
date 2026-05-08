package com.univeloued.rico.ui.screens.reminders

import com.univeloued.rico.data.model.Reminder

data class RemindersUiState(
    val reminders: List<Reminder> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
