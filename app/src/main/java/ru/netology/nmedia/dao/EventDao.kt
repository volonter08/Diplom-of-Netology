package ru.netology.nmedia.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.netology.nmedia.entity.EventEntity
import ru.netology.nmedia.entity.PostEntity
@Dao
interface EventDao {
    @Query("SELECT * FROM EventEntity ORDER BY id DESC")
    fun pagingSource(): PagingSource<Int, EventEntity>
    @Query("SELECT * FROM EventEntity ORDER BY id DESC")
    fun getAll(): LiveData<List<EventEntity>>
    @Query("SELECT * FROM EventEntity ORDER BY id DESC LIMIT :count")
    suspend fun getLatest(count:Int): List<EventEntity>
    @Query("SELECT COUNT(*) == 0 FROM EventEntity")
    suspend fun isEmpty(): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: EventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: List<EventEntity>)

    @Query("DELETE FROM EventEntity WHERE id = :id")
    suspend fun removeById(id: Int)
    @Query("DELETE FROM PostEntity")
    suspend fun removeAll()
}