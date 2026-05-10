package com.univeloued.rico.data.mapper

import com.univeloued.rico.data.local.entity.ReminderEntity
import com.univeloued.rico.domain.model.Reminder

fun ReminderEntity.toDomain(): Reminder {
    return Reminder(
        id = id,
        medicineName = medicineName,
        unit = unit,
        frequency = frequency,
        time = time,
        duration = duration,
        intakeMethod = intakeMethod,
        isActive = isActive
    )
}

fun Reminder.toEntity(): ReminderEntity {
    return ReminderEntity(
        id = id,
        medicineName = medicineName,
        unit = unit,
        frequency = frequency,
        time = time,
        duration = duration,
        intakeMethod = intakeMethod,
        isActive = isActive
    )
}
