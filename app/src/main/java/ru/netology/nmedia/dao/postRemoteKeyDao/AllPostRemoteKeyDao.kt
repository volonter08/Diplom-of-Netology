package ru.netology.nmedia.dao.postRemoteKeyDao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.netology.nmedia.entity.AllPostRemoteKeyEntity

@Dao
interface AllPostRemoteKeyDao: PostRemoteKeyDao<AllPostRemoteKeyEntity>{
    @Query("SELECT COUNT(*) == 0 FROM AllPostRemoteKeyEntity")
    override suspend fun isEmpty(): Boolean

    @Query("SELECT MAX(id) FROM AllPostRemoteKeyEntity")
    override suspend fun max(): Int?

    @Query("SELECT MIN(id) FROM AllPostRemoteKeyEntity")
    override suspend fun min(): Int?

    @Query("DELETE FROM AllPostRemoteKeyEntity")
    override suspend fun removeAll()
}