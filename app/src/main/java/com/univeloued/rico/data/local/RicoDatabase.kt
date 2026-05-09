package com.univeloued.rico.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.univeloued.rico.data.local.dao.*
import com.univeloued.rico.data.model.*

@Database(
    entities = [
        EmergencyContact::class,
        FamilyMember::class,
        MedicalRecord::class,
        Reminder::class,
        UserProfile::class
    ],
    version = 1,
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
