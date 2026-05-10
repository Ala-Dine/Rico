package com.univeloued.rico.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "family_members")
data class FamilyMemberEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val relationship: String,
    val birthdate: String,
    val gender: String,
    val photoUri: String? = null
)
