package com.univeloued.rico.ui.screens.profile

import com.univeloued.rico.domain.model.UserProfile

data class ProfileUiState(
    val userProfile: UserProfile = UserProfile(),
    val isLoading: Boolean = false,
    val error: String? = null
)
