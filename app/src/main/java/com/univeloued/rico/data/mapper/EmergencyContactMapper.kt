package com.univeloued.rico.data.mapper

import com.univeloued.rico.data.local.entity.EmergencyContactEntity
import com.univeloued.rico.domain.model.EmergencyContact

fun EmergencyContactEntity.toDomain() = EmergencyContact(
    id = id,
    name = name,
    phone = phone,
    email = email,
    photoUri = photoUri
)

fun EmergencyContact.toEntity() = EmergencyContactEntity(
    id = id,
    name = name,
    phone = phone,
    email = email,
    photoUri = photoUri
)
