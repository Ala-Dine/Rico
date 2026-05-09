package com.univeloued.rico.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey
    val id: String,
    val medicineName: String,
    val unit: String,
    val frequency: String,
    val time: String,
    val duration: String,
    val intakeMethod: String,
    val isActive: Boolean = true
)
