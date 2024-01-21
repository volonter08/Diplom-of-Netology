package ru.netology.nmedia.dao.postDao

import androidx.paging.PagingSource
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import ru.netology.nmedia.entity.AllPostEntity
import ru.netology.nmedia.entity.PostEntity

interface PostDao<T: PostEntity> {
    fun pagingSource(): PagingSource<Int, T>
    suspend fun getLatest(count:Int): List<AllPostEntity>

    suspend fun isEmpty(): Boolean
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: T)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(listPost: List<T>)
    @Update
    suspend fun update(post:T)
    @Delete
    suspend fun remove(post: T)

    suspend fun removeAll()
}