package com.univeloued.rico.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EncryptedUserProfileDto(
    @SerialName("id")
    val id: String, // This is the userId
    @SerialName("name")
    val name: String, // Encrypted
    @SerialName("birthdate")
    val birthdate: String,
    @SerialName("gender")
    val gender: String,
    @SerialName("bloodType")
    val bloodType: String,
    @SerialName("insuranceNumber")
    val insuranceNumber: String, // Encrypted
    @SerialName("address")
    val address: String, // Encrypted
    @SerialName("phone")
    val phone: String, // Encrypted
    @SerialName("email")
    val email: String,
    @SerialName("notes")
    val notes: String, // Encrypted
    @SerialName("photoUrl")
    val photoUrl: String? = null,
    @SerialName("encryptionSalt")
    val encryptionSalt: String? = null
)
