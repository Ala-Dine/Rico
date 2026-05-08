package com.univeloued.rico.data.model

import android.net.Uri

data class MedicalRecord(
    val id: String,
    val fileName: String,
    val recordFor: String,
    val recordType: String,
    val createdOn: String,
    val fileUri: Uri? = null
)
