package ru.netology.nmedia.dao.jobDao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.entity.JobEntity
import ru.netology.nmedia.entity.MyJobEntity
@Dao
interface MyJobDao: JobDao<MyJobEntity>{
    @Query("SELECT * FROM MyJobEntity ORDER BY id DESC")
    override fun getAll(): Flow<List<MyJobEntity>>
}