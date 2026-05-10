package com.univeloued.rico.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emergency_contacts")
data class EmergencyContactEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val phone: String,
    val email: String,
    val photoUri: String? = null
)
