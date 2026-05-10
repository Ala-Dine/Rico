package com.univeloued.rico.data.mapper

import com.univeloued.rico.data.local.entity.MedicalRecordEntity
import com.univeloued.rico.domain.model.MedicalRecord

fun MedicalRecordEntity.toDomain() = MedicalRecord(
    id = id,
    fileName = fileName,
    recordFor = recordFor,
    recordType = recordType,
    createdOn = createdOn,
    fileUri = fileUri
)

fun MedicalRecord.toEntity() = MedicalRecordEntity(
    id = id,
    fileName = fileName,
    recordFor = recordFor,
    recordType = recordType,
    createdOn = createdOn,
    fileUri = fileUri
)
