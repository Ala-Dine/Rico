package com.univeloued.rico.data.di

import com.univeloued.rico.data.repository.*
import com.univeloued.rico.data.security.AESEncryptionService
import com.univeloued.rico.data.sync.SyncManagerImpl
import com.univeloued.rico.domain.repository.*
import com.univeloued.rico.domain.security.EncryptionService
import com.univeloued.rico.domain.sync.SyncManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindMedicalRecordRepository(
        medicalRecordRepositoryImpl: MedicalRecordRepositoryImpl
    ): MedicalRecordRepository

    @Binds
    @Singleton
    abstract fun bindReminderRepository(
        reminderRepositoryImpl: ReminderRepositoryImpl
    ): ReminderRepository

    @Binds
    @Singleton
    abstract fun bindFamilyRepository(
        familyRepositoryImpl: FamilyRepositoryImpl
    ): FamilyRepository

    @Binds
    @Singleton
    abstract fun bindEmergencyContactRepository(
        emergencyContactRepositoryImpl: EmergencyContactRepositoryImpl
    ): EmergencyContactRepository

    @Binds
    @Singleton
    abstract fun bindUserProfileRepository(
        userProfileRepositoryImpl: UserProfileRepositoryImpl
    ): UserProfileRepository

    @Binds
    @Singleton
    abstract fun bindEncryptionService(
        aesEncryptionService: AESEncryptionService
    ): EncryptionService

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindSyncManager(
        syncManagerImpl: SyncManagerImpl
    ): SyncManager
}
