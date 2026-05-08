package com.univeloued.rico.data.model

data class Reminder(
    val id: String,
    val medicineName: String,
    val unit: String,
    val frequency: String,
    val time: String,
    val duration: String,
    val intakeMethod: String,
    val isActive: Boolean = true
)
