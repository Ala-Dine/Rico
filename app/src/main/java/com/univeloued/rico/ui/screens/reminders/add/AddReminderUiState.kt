package com.univeloued.rico.ui.screens.reminders.add

data class AddReminderUiState(
    val medicineName: String = "",
    val unit: String = "",
    val frequency: String = "Once daily",
    val time: String = "8:00 AM",
    val duration: String = "5 days",
    val intakeMethod: String = "Before meal",
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)
