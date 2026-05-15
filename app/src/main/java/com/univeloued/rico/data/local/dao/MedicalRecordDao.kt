package com.univeloued.rico.data.local.dao

import androidx.room.*
import com.univeloued.rico.data.local.entity.MedicalRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicalRecordDao {
    @Query("SELECT * FROM medical_records WHERE userId = :userId")
    fun getAllMedicalRecords(userId: String): Flow<List<MedicalRecordEntity>>

    @Query("SELECT * FROM medical_records WHERE id = :id AND userId = :userId")
    suspend fun getMedicalRecordById(id: String, userId: String): MedicalRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicalRecord(medicalRecord: MedicalRecordEntity)

    @Update
    suspend fun updateMedicalRecord(medicalRecord: MedicalRecordEntity)

    @Delete
    suspend fun deleteMedicalRecord(medicalRecord: MedicalRecordEntity)

    @Query("SELECT * FROM medical_records WHERE isSynced = 0 AND userId = :userId")
    suspend fun getUnsyncedMedicalRecords(userId: String): List<MedicalRecordEntity>

    @Query("UPDATE medical_records SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)
}
