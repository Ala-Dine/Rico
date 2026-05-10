package com.univeloued.rico.data.local.dao

import androidx.room.*
import com.univeloued.rico.data.local.entity.MedicalRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicalRecordDao {
    @Query("SELECT * FROM medical_records")
    fun getAllMedicalRecords(): Flow<List<MedicalRecordEntity>>

    @Query("SELECT * FROM medical_records WHERE id = :id")
    suspend fun getMedicalRecordById(id: String): MedicalRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicalRecord(medicalRecord: MedicalRecordEntity)

    @Update
    suspend fun updateMedicalRecord(medicalRecord: MedicalRecordEntity)

    @Delete
    suspend fun deleteMedicalRecord(medicalRecord: MedicalRecordEntity)
}
