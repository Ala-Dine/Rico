package com.univeloued.rico.data.mapper

import com.univeloued.rico.data.local.entity.FamilyMemberEntity
import com.univeloued.rico.domain.model.FamilyMember

fun FamilyMemberEntity.toDomain(): FamilyMember {
    return FamilyMember(
        id = id,
        name = name,
        relationship = relationship,
        birthdate = birthdate,
        gender = gender,
        photoUri = photoUri
    )
}

fun FamilyMember.toEntity(): FamilyMemberEntity {
    return FamilyMemberEntity(
        id = id,
        name = name,
        relationship = relationship,
        birthdate = birthdate,
        gender = gender,
        photoUri = photoUri
    )
}
