package com.univeloued.rico.di

import android.content.Context
import androidx.room.Room
import com.univeloued.rico.data.local.RicoDatabase
import com.univeloued.rico.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRicoDatabase(@ApplicationContext context: Context): RicoDatabase {
        return Room.databaseBuilder(
            context,
            RicoDatabase::class.java,
            "rico_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideEmergencyContactDao(database: RicoDatabase): EmergencyContactDao {
        return database.emergencyContactDao()
    }

    @Provides
    fun provideFamilyMemberDao(database: RicoDatabase): FamilyMemberDao {
        return database.familyMemberDao()
    }

    @Provides
    fun provideMedicalRecordDao(database: RicoDatabase): MedicalRecordDao {
        return database.medicalRecordDao()
    }

    @Provides
    fun provideReminderDao(database: RicoDatabase): ReminderDao {
        return database.reminderDao()
    }

    @Provides
    fun provideUserProfileDao(database: RicoDatabase): UserProfileDao {
        return database.userProfileDao()
    }
}
