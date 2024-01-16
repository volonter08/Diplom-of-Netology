package ru.netology.nmedia.repository

import androidx.room.withTransaction
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.JobApiService
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Job
import ru.netology.nmedia.entity.MyJobEntity
import ru.netology.nmedia.responses.ErrorResponse

abstract class JobRepository constructor(
    protected val appDb: AppDb? = null,
    protected val jobApiService: JobApiService,
    protected val userId: Int
):Repository<Job>{

    abstract val data: Flow<List<Job>>

    abstract suspend fun loadJobs(token: String?)
    override suspend fun like(liked: Job, token: String?) {
    }
    override suspend fun dislike(disliked: Job, token: String?) {
    }
    override suspend fun save(saved: Job, token: String?) {
        val response = jobApiService.saveJobs(
            token,saved
        )
        if (!response.isSuccessful) {
            val error: ErrorResponse? =
                Gson().fromJson(
                    response.errorBody()!!.charStream(),
                    ErrorResponse::class.java
                )
            throw Exception(error?.reason)
        } else {
            response.body()?.let { job ->
                appDb?.withTransaction {
                    appDb.myJobDao().insert(MyJobEntity(job))
                }
            }
        }
    }

    override suspend fun remove(removed: Job, token: String?) {
        val response = jobApiService.removeJobs( token,removed.id)
        if (!response.isSuccessful) {
            val error: ErrorResponse? =
                Gson().fromJson(
                    response.errorBody()!!.charStream(),
                    ErrorResponse::class.java
                )
            throw Exception(error?.reason)
        } else
            appDb?.withTransaction {
                appDb.myJobDao().remove(MyJobEntity(removed))
            }
    }

}
