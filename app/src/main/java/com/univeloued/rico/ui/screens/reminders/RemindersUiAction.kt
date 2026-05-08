package com.univeloued.rico.ui.screens.reminders

import com.univeloued.rico.data.model.Reminder

sealed interface RemindersUiAction {
    data object Refresh : RemindersUiAction
    data class DeleteReminder(val id: String) : RemindersUiAction
    data class ToggleReminder(val reminder: Reminder) : RemindersUiAction
}
