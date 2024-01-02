package ru.netology.nmedia.dao.postDao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ru.netology.nmedia.entity.AllPostEntity
import ru.netology.nmedia.entity.MyPostEntity

@Dao
interface MyPostDao:PostDao<MyPostEntity> {
    @Query("SELECT * FROM MyPostEntity ORDER BY id DESC")
    override fun pagingSource(): PagingSource<Int, MyPostEntity>
    @Query("SELECT * FROM MyPostEntity ORDER BY id DESC LIMIT :count")
    override suspend fun getLatest(count:Int): List<AllPostEntity>
    @Query("SELECT COUNT(*) == 0 FROM MyPostEntity")
    override suspend fun isEmpty(): Boolean
    @Query("DELETE FROM MyPostEntity")
    override suspend fun removeAll()
}