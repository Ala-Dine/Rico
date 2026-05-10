package com.univeloued.rico.domain.model

data class FamilyMember(
    val id: String,
    val name: String,
    val relationship: String,
    val birthdate: String,
    val gender: String,
    val photoUri: String? = null
)
