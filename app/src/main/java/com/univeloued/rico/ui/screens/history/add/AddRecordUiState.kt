package com.univeloued.rico.ui.screens.history.add

import android.net.Uri

data class AddRecordUiState(
    val fileName: String = "",
    val recordFor: String = "",
    val recordType: String = "",
    val createdOn: String = "",
    val selectedFileUri: Uri? = null,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false
)
