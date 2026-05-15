package com.univeloued.rico.domain.model

data class MedicalRecord(
    val id: String,
    val fileName: String,
    val recordFor: String,
    val recordType: RecordType,
    val createdOn: String,
    val fileUri: String? = null,
    val isSynced: Boolean = false
)
