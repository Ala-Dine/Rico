package com.univeloued.rico.ui.screens.emergency

import com.univeloued.rico.data.model.EmergencyContact

data class EmergencyUiState(
    val contacts: List<EmergencyContact> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
