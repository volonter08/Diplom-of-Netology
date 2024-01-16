package ru.netology.nmedia.repository

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.netology.nmedia.JobApiService
import ru.netology.nmedia.MyJobApiService
import ru.netology.nmedia.dao.jobDao.MyJobDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Job
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toMyEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyJobRepository @Inject constructor(
    appDb: AppDb,
    myJobDao: MyJobDao,
    jobApiService: MyJobApiService,
) : JobRepository(appDb, jobApiService, 0) {
    override val data: Flow<List<Job>> = myJobDao.getAll().map {
        it.toDto().map {
            it.copy(ownedMe = true)
        }
    }
    override suspend fun loadJobs(token: String?) {
        val response = (jobApiService as MyJobApiService).getMyJobs(token)
        if (!response.isSuccessful) {
            val error: ru.netology.nmedia.responses.ErrorResponse? =
                com.google.gson.Gson().fromJson(
                    response.errorBody()!!.charStream(),
                    ru.netology.nmedia.responses.ErrorResponse::class.java
                )
            throw Exception(error?.reason)
        } else
            response.body()?.let { listJob ->
                appDb?.withTransaction {
                    appDb.myJobDao().insert(listJob.toMyEntity())
                }
            }
    }
}