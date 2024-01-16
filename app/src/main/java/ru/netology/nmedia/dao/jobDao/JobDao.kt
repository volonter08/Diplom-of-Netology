package ru.netology.nmedia.dao.jobDao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.entity.JobEntity

interface JobDao<T:JobEntity> {

    fun getAll(): Flow<List<T>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(job: T)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(listJob: List<T>)
    @Update
    suspend fun update(job:T)
    @Delete
    suspend fun remove(job: T)
}