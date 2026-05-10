package com.univeloued.rico.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medical_records")
data class MedicalRecordEntity(
    @PrimaryKey
    val id: String,
    val fileName: String,
    val recordFor: String,
    val recordType: String,
    val createdOn: String,
    val fileUri: String? = null
)
