package ru.netology.nmedia.repository

import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import ru.netology.nmedia.UserJobApiService
import ru.netology.nmedia.dto.Job
import ru.netology.nmedia.responses.ErrorResponse


class UserJobsRepository @AssistedInject constructor(
    @Assisted
    userId: Int,
    jobApiService: UserJobApiService,
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