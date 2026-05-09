package com.univeloued.rico.data.local.dao

import androidx.room.*
import com.univeloued.rico.data.model.EmergencyContact
import kotlinx.coroutines.flow.Flow

@Dao
interface EmergencyContactDao {
    @Query("SELECT * FROM emergency_contacts")
    fun getAllContacts(): Flow<List<EmergencyContact>>

    @Query("SELECT * FROM emergency_contacts WHERE id = :id")
    suspend fun getContactById(id: String): EmergencyContact?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: EmergencyContact)

    @Update
    suspend fun updateContact(contact: EmergencyContact)

    @Delete
    suspend fun deleteContact(contact: EmergencyContact)
}
