package com.univeloued.rico.data.mapper

import com.univeloued.rico.data.local.entity.UserProfileEntity
import com.univeloued.rico.domain.model.UserProfile

fun UserProfileEntity.toDomain(): UserProfile {
    return UserProfile(
        id = id,
        name = name,
        birthdate = birthdate,
        gender = gender,
        bloodType = bloodType,
        insuranceNumber = insuranceNumber,
        address = address,
        phone = phone,
        email = email,
        notes = notes,
        photoUri = photoUri,
        encryptionSalt = encryptionSalt
    )
}

fun UserProfile.toEntity(): UserProfileEntity {
    return UserProfileEntity(
        id = id,
        name = name,
        birthdate = birthdate,
        gender = gender,
        bloodType = bloodType,
        insuranceNumber = insuranceNumber,
        address = address,
        phone = phone,
        email = email,
        notes = notes,
        photoUri = photoUri,
        encryptionSalt = encryptionSalt
    )
}
