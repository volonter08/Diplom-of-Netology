package ru.netology.nmedia.dao.postRemoteKeyDao

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import ru.netology.nmedia.entity.PostRemoteKeyEntity

interface PostRemoteKeyDao<T:PostRemoteKeyEntity> {
    suspend fun isEmpty(): Boolean

    suspend fun max(): Int?

    suspend fun min(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(key: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(keys: List<T>)
    suspend fun removeAll()
}