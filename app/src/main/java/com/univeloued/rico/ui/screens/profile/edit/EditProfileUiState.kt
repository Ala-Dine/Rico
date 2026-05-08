package com.univeloued.rico.ui.screens.profile.edit

data class EditProfileUiState(
    val name: String = "",
    val birthdate: String = "",
    val gender: String = "",
    val bloodType: String = "",
    val insuranceNumber: String = "",
    val address: String = "",
    val phone: String = "",
    val email: String = "",
    val notes: String = "",
    val photoUri: String? = null,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false
)
