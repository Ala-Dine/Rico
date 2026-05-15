package com.univeloued.rico.data.mapper

import android.net.Uri
import com.univeloued.rico.data.local.entity.MedicalRecordEntity
import com.univeloued.rico.domain.model.MedicalRecord
import com.univeloued.rico.domain.model.RecordType

fun MedicalRecordEntity.toDomain(): MedicalRecord {
    return MedicalRecord(
        id = id,
        fileName = fileName,
        recordFor = recordFor,
        recordType = RecordType.fromString(recordType),
        createdOn = createdOn,
        fileUri = fileUri?.toString(),
        isSynced = isSynced
    )
}

fun MedicalRecord.toEntity(): MedicalRecordEntity {
    return MedicalRecordEntity(
        id = id,
        fileName = fileName,
        recordFor = recordFor,
        recordType = recordType.displayName,
        createdOn = createdOn,
        fileUri = fileUri?.let { Uri.parse(it) },
        isSynced = isSynced
    )
}
