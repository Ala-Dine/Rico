package com.univeloued.rico.data.local.dao

import androidx.room.*
import com.univeloued.rico.data.local.entity.FamilyMemberEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FamilyMemberDao {
    @Query("SELECT * FROM family_members")
    fun getAllFamilyMembers(): Flow<List<FamilyMemberEntity>>

    @Query("SELECT * FROM family_members WHERE id = :id")
    suspend fun getFamilyMemberById(id: String): FamilyMemberEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFamilyMember(familyMember: FamilyMemberEntity)

    @Update
    suspend fun updateFamilyMember(familyMember: FamilyMemberEntity)

    @Delete
    suspend fun deleteFamilyMember(familyMember: FamilyMemberEntity)
}
