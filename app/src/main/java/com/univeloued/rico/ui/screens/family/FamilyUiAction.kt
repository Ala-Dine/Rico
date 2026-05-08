package com.univeloued.rico.ui.screens.family

sealed interface FamilyUiAction {
    data object Refresh : FamilyUiAction
}
