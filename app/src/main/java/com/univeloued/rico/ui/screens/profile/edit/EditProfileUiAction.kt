package com.univeloued.rico.ui.screens.profile.edit

sealed interface EditProfileUiAction {
    data class UpdateName(val name: String) : EditProfileUiAction
    data class UpdateBirthdate(val birthdate: String) : EditProfileUiAction
    data class UpdateGender(val gender: String) : EditProfileUiAction
    data class UpdateBloodType(val bloodType: String) : EditProfileUiAction
    data class UpdateInsuranceNumber(val insuranceNumber: String) : EditProfileUiAction
    data class UpdateAddress(val address: String) : EditProfileUiAction
    data class UpdatePhone(val phone: String) : EditProfileUiAction
    data class UpdateEmail(val email: String) : EditProfileUiAction
    data class UpdateNotes(val notes: String) : EditProfileUiAction
    data class UpdatePhotoUri(val uri: String?) : EditProfileUiAction
    data object SaveProfile : EditProfileUiAction
}
