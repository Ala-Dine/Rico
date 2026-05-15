package com.univeloued.rico.data.local.dao

import androidx.room.*
import com.univeloued.rico.data.local.entity.EmergencyContactEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmergencyContactDao {
    @Query("SELECT * FROM emergency_contacts WHERE userId = :userId")
    fun getAllContacts(userId: String): Flow<List<EmergencyContactEntity>>

    @Query("SELECT * FROM emergency_contacts WHERE id = :id AND userId = :userId")
    suspend fun getContactById(id: String, userId: String): EmergencyContactEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: EmergencyContactEntity)

    @Update
    suspend fun updateContact(contact: EmergencyContactEntity)

    @Delete
    suspend fun deleteContact(contact: EmergencyContactEntity)

    @Query("SELECT * FROM emergency_contacts WHERE isSynced = 0 AND userId = :userId")
    suspend fun getUnsyncedContacts(userId: String): List<EmergencyContactEntity>

    @Query("UPDATE emergency_contacts SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)
}
