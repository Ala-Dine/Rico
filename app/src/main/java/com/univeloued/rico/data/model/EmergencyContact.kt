package com.univeloued.rico.data.model

data class EmergencyContact(
    val id: String,
    val name: String,
    val phone: String,
    val email: String,
    val photoUri: String? = null
)
