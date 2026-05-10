package com.univeloued.rico.ui.screens.history.add

import android.net.Uri
import com.univeloued.rico.domain.model.RecordType

data class AddRecordUiState(
    val fileName: String = "",
    val recordFor: String = "",
    val recordType: RecordType? = null,
    val createdOn: String = "",
    val selectedFileUri: Uri? = null,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)
