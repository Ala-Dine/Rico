package com.univeloued.rico.data.model

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medical_records")
data class MedicalRecord(
    @PrimaryKey
    val id: String,
    val fileName: String,
    val recordFor: String,
    val recordType: String,
    val createdOn: String,
    val fileUri: Uri? = null
)
