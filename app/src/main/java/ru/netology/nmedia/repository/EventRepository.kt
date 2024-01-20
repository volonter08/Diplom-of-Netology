package ru.netology.nmedia.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.google.gson.Gson
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.netology.nmedia.AllEventsApiService
import ru.netology.nmedia.dao.EventDao
import ru.netology.nmedia.dao.EventRemoteKeyDao
import ru.netology.nmedia.dao.ProfileDao
import ru.netology.nmedia.dto.Event
import ru.netology.nmedia.entity.AllPostEntity
import ru.netology.nmedia.entity.EventEntity
import ru.netology.nmedia.entity.MyPostEntity
import ru.netology.nmedia.remoteMediator.EventRemoteMediator
import ru.netology.nmedia.requests.EventCreateRequest
import ru.netology.nmedia.responses.ErrorResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(
    appDb: AppDb,
    private val dao: EventDao,
    daoRemoteKey: EventRemoteKeyDao,
    profileDao: ProfileDao,
    val retrofitService: AllEventsApiService
) : Repository<Event, EventCreateRequest> {

    @OptIn(ExperimentalPagingApi::class)
    val data: Flow<PagingData<Event>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false, initialLoadSize = 10),
        remoteMediator = EventRemoteMediator(retrofitService, appDb, dao, daoRemoteKey, profileDao),
        pagingSourceFactory = dao::pagingSource
    ).flow.map { pagingData ->
        pagingData.map {
            it.toDto()
        }
    }

    override suspend fun like(liked: Event, token: String?) {
        val response = retrofitService.likeEventById(liked.id, token)
        if (!response.isSuccessful) {
            val error: ErrorResponse? =
                Gson().fromJson(
                    response.errorBody()!!.charStream(),
                    ErrorResponse::class.java
                )
            throw Exception(error?.reason)
        } else
            response.body()?.let {
                dao.insert(EventEntity.fromDto(it))
            }
    }

    override suspend fun dislike(disliked: Event, token: String?) {
        val response = retrofitService.dislikeEventById(disliked.id, token)
        if (!response.isSuccessful) {
            val error: ErrorResponse? =
                Gson().fromJson(
                    response.errorBody()!!.charStream(),
                    ErrorResponse::class.java
                )
            throw Exception(error?.reason)
        } else
            response.body()?.let {
                dao.insert(EventEntity.fromDto(it))
            }
    }

    override suspend fun remove(removed: Event, token: String?) {
        dao.removeById(removed.id)
        val response = retrofitService.removeEventById(removed.id, token)
        if (!response.isSuccessful)
            throw Exception("Request is not successful")
    }

    override suspend fun save(createRequest: EventCreateRequest, token: String?) {
        val response = retrofitService.saveEvent(
            createRequest, token
        )
        if (!response.isSuccessful) {
            val error: ErrorResponse? =
                Gson().fromJson(
                    response.errorBody()!!.charStream(),
                    ErrorResponse::class.java
                )
            throw Exception(error?.reason)
        } else {
            response.body()?.let { event ->
                dao.insert(EventEntity.fromDto(event))
            }
        }
    }

    suspend fun participate(eventId: Int, token: String?) {
        val response = retrofitService.participate(
            token, eventId
        )
        if (!response.isSuccessful) {
            val error: ErrorResponse? =
                Gson().fromJson(
                    response.errorBody()!!.charStream(),
                    ErrorResponse::class.java
                )
            throw Exception(error?.reason)
        } else {
            response.body()?.let { event ->
                dao.insert(EventEntity.fromDto(event))
            }
        }
    }
    suspend fun unparticipate(eventId: Int, token: String?) {
        val response = retrofitService.unparticipate(
            token, eventId
        )
        if (!response.isSuccessful) {
            val error: ErrorResponse? =
                Gson().fromJson(
                    response.errorBody()!!.charStream(),
                    ErrorResponse::class.java
                )
            throw Exception(error?.reason)
        } else {
            response.body()?.let { event ->
                dao.insert(EventEntity.fromDto(event))
            }
        }
    }
}