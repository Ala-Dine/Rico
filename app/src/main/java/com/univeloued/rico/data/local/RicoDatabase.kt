package com.univeloued.rico.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.univeloued.rico.data.local.dao.*
import com.univeloued.rico.data.local.entity.*

@Database(
    entities = [
        EmergencyContactEntity::class,
        FamilyMemberEntity::class,
        MedicalRecordEntity::class,
        ReminderEntity::class,
        UserProfileEntity::class
    ],
    version = 2, // Increment version because of entity name changes/structure
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class RicoDatabase : RoomDatabase() {
    abstract fun emergencyContactDao(): EmergencyContactDao
    abstract fun familyMemberDao(): FamilyMemberDao
    abstract fun medicalRecordDao(): MedicalRecordDao
    abstract fun reminderDao(): ReminderDao
    abstract fun userProfileDao(): UserProfileDao
}
