package ru.netology.nmedia.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.InvalidatingPagingSourceFactory
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.room.withTransaction
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.flow
import ru.netology.nmedia.UserJobApiService
import ru.netology.nmedia.dao.ProfileDao
import ru.netology.nmedia.dto.Job
import ru.netology.nmedia.entity.toMyEntity
import ru.netology.nmedia.responses.ErrorResponse


class UserJobsRepository @AssistedInject constructor(
    @Assisted
    userId: Int,
    jobApiService: UserJobApiService,
    profileDao: ProfileDao
) : JobRepository(jobApiService = jobApiService, userId = userId ) {

    override val data: Flow<List<Job>> = MutableSharedFlow()

    override suspend fun loadJobs(token: String?) {
        val response = (jobApiService as UserJobApiService).getUserJobs(token, userId)
        if (!response.isSuccessful) {
            val error: ErrorResponse? =
               Gson().fromJson(
                    response.errorBody()!!.charStream(),
                    ErrorResponse::class.java
                )
            throw Exception(error?.reason)
        } else
            response.body()?.let { listJob ->
                (data as MutableSharedFlow).emit(listJob)
            }
    }
}

@AssistedFactory
interface UserJobRepositoryFactory {
    fun create(userId: Int): UserJobsRepository
}