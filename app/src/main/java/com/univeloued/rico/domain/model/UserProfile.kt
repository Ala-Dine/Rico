package com.univeloued.rico.domain.model

data class UserProfile(
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
    val encryptionSalt: String? = null
)
