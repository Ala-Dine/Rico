package com.univeloued.rico.ui.screens.emergency.add

data class AddEmergencyContactUiState(
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val photoUri: String? = null,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false
)
