package com.univeloued.rico.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EncryptedFamilyMemberDto(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String, // Encrypted
    @SerialName("relationship")
    val relationship: String,
    @SerialName("birthdate")
    val birthdate: String,
    @SerialName("gender")
    val gender: String,
    @SerialName("photoUrl")
    val photoUrl: String? = null,
    @SerialName("userId")
    val userId: String
)
