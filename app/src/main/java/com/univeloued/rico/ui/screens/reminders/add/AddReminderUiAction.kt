package com.univeloued.rico.ui.screens.reminders.add

sealed interface AddReminderUiAction {
    data class UpdateMedicineName(val name: String) : AddReminderUiAction
    data class UpdateUnit(val unit: String) : AddReminderUiAction
    data class UpdateFrequency(val frequency: String) : AddReminderUiAction
    data class UpdateTime(val time: String) : AddReminderUiAction
    data class UpdateDuration(val duration: String) : AddReminderUiAction
    data class UpdateIntakeMethod(val method: String) : AddReminderUiAction
    data object SaveReminder : AddReminderUiAction
}
