package ru.netology.nmedia.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.google.gson.Gson
import ru.netology.nmedia.ApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dao.PostRemoteKeyDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.netology.nmedia.dao.ProfileDao
import ru.netology.nmedia.remoteMediator.PostRemoteMediator
import ru.netology.nmedia.responses.ErrorResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepository @Inject constructor(
    appDb: AppDb,
    private val dao: PostDao,
    daoRemoteKey: PostRemoteKeyDao,
    profileDao: ProfileDao,
    val retrofitService: ApiService
) : Repository<List<Post>> {

    @OptIn(ExperimentalPagingApi::class)
    val data: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false, initialLoadSize = 10),
        remoteMediator = PostRemoteMediator(retrofitService, appDb, dao, daoRemoteKey, profileDao),
        pagingSourceFactory = dao::pagingSource,
    ).flow.map { pagingData ->
        pagingData.map {
            it.toDto()
        }
    }

    override suspend fun getAll() {
        val response = retrofitService.getAllPosts()
        if (!response.isSuccessful)
            throw Exception("Request is not successfu")
        val listPosts = response.body() ?: emptyList()
        dao.insert(listPosts.map {
            PostEntity.fromDto(it)
        })
    }

    suspend fun like(likedPost: Post, token: String?) {
        val response = retrofitService.likePostById(likedPost.id, token)
        if (!response.isSuccessful) {
            val error: ErrorResponse? =
                Gson().fromJson(
                    response.errorBody()!!.charStream(),
                    ErrorResponse::class.java
                )
            throw Exception(error?.reason)
        } else
            response.body()?.let {
                dao.insert(PostEntity.fromDto(it))
            }
    }

    suspend fun dislike(dislikedPost: Post, token: String?) {
        val response = retrofitService.dislikePostById(dislikedPost.id, token)
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
                dao.insert(PostEntity.fromDto(it))
            }
    }

    fun share(id: Int) {
    }

    suspend fun remove(id: Int, token: String?) {
        dao.removeById(id)
        val response = retrofitService.removePostById(id, token)
        if (!response.isSuccessful)
            throw Exception("Request is not successful")
    }

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
}