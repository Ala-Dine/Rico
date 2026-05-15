package com.univeloued.rico.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EncryptedMedicalRecordDto(
    @SerialName("id")
    val id: String,
    @SerialName("fileName")
    val fileName: String, // Encrypted
    @SerialName("recordFor")
    val recordFor: String, // Encrypted
    @SerialName("recordType")
    val recordType: String,
    @SerialName("createdOn")
    val createdOn: String,
    @SerialName("fileUrl")
    val fileUrl: String? = null, // Path in Supabase Storage
    @SerialName("userId")
    val userId: String
)
