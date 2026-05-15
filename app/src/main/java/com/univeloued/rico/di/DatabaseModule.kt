package com.univeloued.rico.di

import android.content.Context
import androidx.room.Room
import com.univeloued.rico.data.local.RicoDatabase
import com.univeloued.rico.data.local.dao.*
import com.univeloued.rico.data.security.DatabasePassphraseManager
import com.univeloued.rico.util.Constants
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

        // Try to open the database to check for corruption/wrong key
        // We only delete if we are SURE it's not an authentication error
        try {
            val dbFile = context.getDatabasePath(Constants.DATABASE_NAME)
            if (dbFile.exists()) {
                net.zetetic.database.sqlcipher.SQLiteDatabase.openDatabase(
                    dbFile.absolutePath,
                    passphrase,
                    null,
                    net.zetetic.database.sqlcipher.SQLiteDatabase.OPEN_READONLY,
                    hook
                ).close()
            }
        } catch (e: Exception) {
            val errorMsg = e.message ?: ""
            // Code 7 is "Out of Memory", which in SQLCipher usually means "Wrong Password"
            if (errorMsg.contains("file is not a database") || 
                errorMsg.contains("corrupt") || 
                errorMsg.contains("out of memory") ||
                errorMsg.contains("code 7")
            ) {
                android.util.Log.e("DatabaseModule", "Database key mismatch or corruption. Resetting local cache.", e)
                context.deleteDatabase(Constants.DATABASE_NAME)
            } else {
                android.util.Log.w("DatabaseModule", "Database busy or auth required. Skipping validation.")
            }
        }

        return Room.databaseBuilder(
            context,
            RicoDatabase::class.java,
            Constants.DATABASE_NAME
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
