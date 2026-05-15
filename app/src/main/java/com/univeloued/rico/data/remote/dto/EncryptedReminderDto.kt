package com.univeloued.rico.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EncryptedReminderDto(
    @SerialName("id")
    val id: String,
    @SerialName("medicineName")
    val medicineName: String, // Encrypted
    @SerialName("unit")
    val unit: String,
    @SerialName("frequency")
    val frequency: String,
    @SerialName("time")
    val time: String,
    @SerialName("duration")
    val duration: String,
    @SerialName("intakeMethod")
    val intakeMethod: String,
    @SerialName("isActive")
    val isActive: Boolean,
    @SerialName("userId")
    val userId: String
)
