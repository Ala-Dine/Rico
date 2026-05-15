package com.univeloued.rico.data.local.dao

import androidx.room.*
import com.univeloued.rico.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = :userId")
    fun getUserProfile(userId: String): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(userProfile: UserProfileEntity)

    @Query("SELECT * FROM user_profile WHERE isSynced = 0 AND id = :userId")
    suspend fun getUnsyncedProfile(userId: String): UserProfileEntity?

    @Query("UPDATE user_profile SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: String)
}
