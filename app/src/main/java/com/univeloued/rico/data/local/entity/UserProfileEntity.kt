package com.univeloued.rico.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: String = "",
    val name: String = "",
    val birthdate: String = "",
    val gender: String = "",
    val bloodType: String = "",
    val insuranceNumber: String = "",
    val address: String = "",
    val phone: String = "",
    val email: String = "",
    val notes: String = "",
    val photoUri: String? = null,
    val encryptionSalt: String? = null,
    val isSynced: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
)
