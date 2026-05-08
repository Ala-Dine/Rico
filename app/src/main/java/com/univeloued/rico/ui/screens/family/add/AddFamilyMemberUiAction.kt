package com.univeloued.rico.ui.screens.family.add

sealed interface AddFamilyMemberUiAction {
    data class UpdateName(val name: String) : AddFamilyMemberUiAction
    data class UpdateRelationship(val relationship: String) : AddFamilyMemberUiAction
    data class UpdateBirthdate(val birthdate: String) : AddFamilyMemberUiAction
    data class UpdateGender(val gender: String) : AddFamilyMemberUiAction
    data class UpdatePhotoUri(val uri: String?) : AddFamilyMemberUiAction
    data object SaveMember : AddFamilyMemberUiAction
}
