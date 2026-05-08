package com.univeloued.rico.ui.screens.history.add

import android.net.Uri

sealed interface AddRecordUiAction {
    data class UpdateFileName(val name: String) : AddRecordUiAction
    data class UpdateRecordFor(val person: String) : AddRecordUiAction
    data class UpdateRecordType(val type: String) : AddRecordUiAction
    data class UpdateCreatedOn(val date: String) : AddRecordUiAction
    data class UpdateFileUri(val uri: Uri?) : AddRecordUiAction
    data object SaveRecord : AddRecordUiAction
}
