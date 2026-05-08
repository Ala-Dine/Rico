package com.univeloued.rico.ui.screens.family

import com.univeloued.rico.data.model.FamilyMember

data class FamilyUiState(
    val familyMembers: List<FamilyMember> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
