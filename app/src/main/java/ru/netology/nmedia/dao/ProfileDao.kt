package ru.netology.nmedia.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.entity.ProfileData
import ru.netology.nmedia.entity.ProfileEntity

@Dao
interface ProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: ProfileEntity)
    @Query("SELECT * FROM ProfileEntity")
    fun getProfileData(): Flow<List<ProfileEntity>>
    @Query("SELECT token FROM ProfileEntity WHERE type = :profileData")
    suspend fun getAccessToken(profileData: ProfileData = ProfileData.PROFILE):String?

    @Query("UPDATE ProfileEntity SET login = :login,name=:name,avatar=:avatar WHERE id = :id")
    suspend fun updateProfile(id: Int, login:String?,name:String?,avatar:String?): Int
}