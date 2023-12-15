package ru.netology.nmedia.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.google.gson.Gson
import ru.netology.nmedia.ApiService
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.netology.nmedia.dao.EventDao
import ru.netology.nmedia.dao.EventRemoteKeyDao
import ru.netology.nmedia.dao.ProfileDao
import ru.netology.nmedia.dto.Event
import ru.netology.nmedia.entity.EventEntity
import ru.netology.nmedia.remoteMediator.EventRemoteMediator
import ru.netology.nmedia.responses.ErrorResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(
    appDb: AppDb,
    private val dao: EventDao,
    daoRemoteKey: EventRemoteKeyDao,
    profileDao: ProfileDao,
    val retrofitService: ApiService
) : Repository<List<Event>> {

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

    override suspend fun getAll() {
        val response = retrofitService.getAllEvents()
        if (!response.isSuccessful)
            throw Exception("Request is not successfu")
        val listPosts = response.body() ?: emptyList()
        dao.insert(listPosts.map {
            EventEntity.fromDto(it)
        })
    }

    suspend fun like(likedPost: Event, token: String?) {
        val response = retrofitService.likeEventById(likedPost.id, token)
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

    suspend fun dislike(dislikedPost: Event, token: String?) {
        val response = retrofitService.dislikeEventById(dislikedPost.id, token)
        if (!response.isSuccessful) {
            val error: ErrorResponse? =
                Gson().fromJson(
                    response.errorBody()!!.charStream(),
                    ErrorResponse::class.java
                )
            throw Exception(error?.reason)
        }
        else
            response.body()?.let {
                dao.insert(EventEntity.fromDto(it))
            }
    }

    fun share(id: Int) {
    }

    suspend fun remove(id: Int, token: String?) {
        dao.removeById(id)
        val response = retrofitService.removeEventById(id, token)
        if (!response.isSuccessful)
            throw Exception("Request is not successful")
    }
    /*
    suspend fun createPost(content: String) {
        val response = retrofitService.savePost(
            Post(
                id = 0,
                content = content,
                authorId = 0,
                author = "",
                authorAvatar = "",
                likedByMe = false,
                published = " ",
            )
        )
        if (!response.isSuccessful)
            throw Exception("Request is not successful")
        val createdPost = response.body()
        dao.insert(PostEntity.fromDto(createdPost!!))
    }

    suspend fun update(post: Post) {
        val response = retrofitService.savePost(post)
        if (!response.isSuccessful)
            throw Exception("Request is not successful")
        val updatedPost = response.body()
        dao.insert(PostEntity.fromDto(updatedPost!!))
    }

     */
}