package com.univeloued.rico.ui.screens.emergency

sealed interface EmergencyUiAction {
    data object Refresh : EmergencyUiAction
}
