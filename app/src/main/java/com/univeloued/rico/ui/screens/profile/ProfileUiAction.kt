package com.univeloued.rico.ui.screens.profile

sealed interface ProfileUiAction {
    data object Refresh : ProfileUiAction
    data class UpdatePhoto(val uri: String?) : ProfileUiAction
}
