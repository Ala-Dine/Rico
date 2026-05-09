package com.univeloued.rico.data.local.dao

import androidx.room.*
import com.univeloued.rico.data.model.FamilyMember
import kotlinx.coroutines.flow.Flow

@Dao
interface FamilyMemberDao {
    @Query("SELECT * FROM family_members")
    fun getAllFamilyMembers(): Flow<List<FamilyMember>>

    @Query("SELECT * FROM family_members WHERE id = :id")
    suspend fun getFamilyMemberById(id: String): FamilyMember?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFamilyMember(familyMember: FamilyMember)

    @Update
    suspend fun updateFamilyMember(familyMember: FamilyMember)

    @Delete
    suspend fun deleteFamilyMember(familyMember: FamilyMember)
}
