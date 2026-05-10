package com.univeloued.rico.ui.screens.family

import com.univeloued.rico.domain.model.FamilyMember

data class FamilyUiState(
    val familyMembers: List<FamilyMember> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
