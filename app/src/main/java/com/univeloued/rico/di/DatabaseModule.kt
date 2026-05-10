package com.univeloued.rico.di

import android.content.Context
import androidx.room.Room
import com.univeloued.rico.data.local.RicoDatabase
import com.univeloued.rico.data.local.dao.*
import com.univeloued.rico.data.security.DatabasePassphraseManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.zetetic.database.sqlcipher.SQLiteConnection
import net.zetetic.database.sqlcipher.SQLiteDatabaseHook
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideRicoDatabase(
        @ApplicationContext context: Context,
        databasePassphraseManager: DatabasePassphraseManager
    ): RicoDatabase {
        System.loadLibrary("sqlcipher")

        val passphrase = databasePassphraseManager.getDatabasePassphrase()

        val hook = object : SQLiteDatabaseHook {
            override fun preKey(connection: SQLiteConnection?) {}
            override fun postKey(connection: SQLiteConnection?) {
                connection?.executeRaw("PRAGMA cipher_migrate;", null, null)
            }
        }

        val factory = SupportOpenHelperFactory(passphrase, hook, false)

        return Room.databaseBuilder(
            context,
            RicoDatabase::class.java,
            "rico_database"
        )
            .openHelperFactory(factory)
            .fallbackToDestructiveMigration()
            .build()
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
