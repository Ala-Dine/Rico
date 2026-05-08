package com.univeloued.rico.ui.screens.family.add

data class AddFamilyMemberUiState(
    val name: String = "",
    val relationship: String = "",
    val birthdate: String = "",
    val gender: String = "Male",
    val photoUri: String? = null,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false
)
