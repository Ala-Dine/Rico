package com.univeloued.rico.data.local.dao

import androidx.room.*
import com.univeloued.rico.data.model.MedicalRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicalRecordDao {
    @Query("SELECT * FROM medical_records")
    fun getAllMedicalRecords(): Flow<List<MedicalRecord>>

    @Query("SELECT * FROM medical_records WHERE id = :id")
    suspend fun getMedicalRecordById(id: String): MedicalRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicalRecord(medicalRecord: MedicalRecord)

    @Update
    suspend fun updateMedicalRecord(medicalRecord: MedicalRecord)

    @Delete
    suspend fun deleteMedicalRecord(medicalRecord: MedicalRecord)
}
