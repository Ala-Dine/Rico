package com.univeloued.rico.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EncryptedEmergencyContactDto(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String, // Encrypted
    @SerialName("phone")
    val phone: String, // Encrypted
    @SerialName("email")
    val email: String, // Encrypted
    @SerialName("photoUrl")
    val photoUrl: String? = null,
    @SerialName("userId")
    val userId: String
)
