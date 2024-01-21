package ru.netology.nmedia.dao.postRemoteKeyDao

import androidx.room.Dao
import androidx.room.Query
import ru.netology.nmedia.entity.MyPostRemoteKeyEntity
@Dao
interface MyPostRemoteKeyDao :PostRemoteKeyDao<MyPostRemoteKeyEntity> {
    @Query("SELECT COUNT(*) == 0 FROM MyPostRemoteKeyEntity")
    override suspend fun isEmpty(): Boolean

    @Query("SELECT MAX(id) FROM MyPostRemoteKeyEntity")
    override suspend fun max(): Int?

    @Query("SELECT MIN(id) FROM MyPostRemoteKeyEntity")
    override suspend fun min(): Int?

    @Query("DELETE FROM MyPostRemoteKeyEntity")
    override suspend fun removeAll()
}