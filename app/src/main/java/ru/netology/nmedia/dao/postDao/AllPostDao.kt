package ru.netology.nmedia.dao.postDao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import ru.netology.nmedia.entity.AllPostEntity
@Dao
interface AllPostDao:PostDao<AllPostEntity> {
    @Query("SELECT * FROM AllPostEntity ORDER BY id DESC")
    override fun pagingSource(): PagingSource<Int, AllPostEntity>
    @Query("SELECT * FROM AllPostEntity ORDER BY id DESC LIMIT :count")
    override suspend fun getLatest(count:Int): List<AllPostEntity>
    @Query("SELECT COUNT(*) == 0 FROM AllPostEntity")
    override suspend fun isEmpty(): Boolean
    @Query("DELETE FROM AllPostEntity")
    override suspend fun removeAll()
}