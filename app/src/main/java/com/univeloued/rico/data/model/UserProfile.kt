package com.univeloued.rico.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey
    val id: Int = 0, // Single user profile, constant ID
    val name: String = "",
    val birthdate: String = "",
    val gender: String = "",
    val bloodType: String = "",
    val insuranceNumber: String = "",
    val address: String = "",
    val phone: String = "",
    val email: String = "",
    val notes: String = "",
    val photoUri: String? = null
)
