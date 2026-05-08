package com.univeloued.rico.ui.screens.emergency.add

sealed interface AddEmergencyContactUiAction {
    data class UpdateName(val name: String) : AddEmergencyContactUiAction
    data class UpdatePhone(val phone: String) : AddEmergencyContactUiAction
    data class UpdateEmail(val email: String) : AddEmergencyContactUiAction
    data class UpdatePhotoUri(val uri: String?) : AddEmergencyContactUiAction
    data object SaveContact : AddEmergencyContactUiAction
}
