package com.example.netologyandroidhomework1.model

import androidx.lifecycle.map
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.netologyandroidhomework1.PostsApiService
import com.example.netologyandroidhomework1.dao.PostDao
import com.example.netologyandroidhomework1.dao.PostRemoteKeyDao
import com.example.netologyandroidhomework1.db.AppDb
import com.example.netologyandroidhomework1.dto.Post
import com.example.netologyandroidhomework1.entity.PostEntity
import com.example.netologyandroidhomework1.entity.toDto
import com.example.netologyandroidhomework1.entity.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class PostRepository @Inject constructor(
    appDb: AppDb,
    private val dao: PostDao,
    private val daoRemoteKey:PostRemoteKeyDao,
    val retrofitService: PostsApiService
) : Repository<List<Post>> {

    @OptIn(ExperimentalPagingApi::class)
    val data: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 4, enablePlaceholders = false, initialLoadSize = 4),
        remoteMediator = PostRemoteMediator(retrofitService, appDb,dao,daoRemoteKey),
        pagingSourceFactory = dao::pagingSource,
    ).flow.map {pagingData->
        pagingData.map {
            it.toDto()
        }
    }
    override suspend fun getAll() {
        val response = retrofitService.getAll()
        if (!response.isSuccessful)
            throw Exception("Request is not successfu")
        val listPosts = response.body() ?: emptyList()
        println(listPosts)
        dao.insert(listPosts.map {
            PostEntity.fromDto(it)
        })
    }

    suspend fun like(id: Long) {
        /*
        val likedPost = data.value?.find {
            it.id == id
        }!!.let {
            it.copy(likedByMe = true, likes = it.likes + 1)
        }
        dao.insert(PostEntity.fromDto(likedPost))
        val response = retrofitService.likeById(id)
        if (!response.isSuccessful)
            throw Exception("Request is not successful")

         */
    }

    suspend fun dislike(id: Long) {
        /*
        val dislikedPost = data.value?.find {
            it.id == id
        }!!.let {
            it.copy(likedByMe = false, likes = if (it.likes == 0) 0 else it.likes - 1)
        }
        dao.insert(PostEntity.fromDto(dislikedPost))
        val response = retrofitService.dislikeById(id)
        if (!response.isSuccessful)
            throw Exception("Request is not successful")

         */
    }

    fun getNewerCount(id: Long): Flow<Int> = flow {
        while (true) {
            delay(120_000L)
            val response = retrofitService.getNewer(id)
            if (!response.isSuccessful) {
                throw Exception()
            }

            val body = response.body() ?: throw Exception()
            dao.insert(body.toEntity())
            emit(body.size)
        }
    }
        .catch { e -> throw Exception() }
        .flowOn(Dispatchers.Default)

    fun share(id: Int) {
    }

    suspend fun remove(id: Long) {
        dao.removeById(id)
        val response = retrofitService.removeById(id)
        if (!response.isSuccessful)
            throw Exception("Request is not successful")
    }

    suspend fun createPost(content: String) {
        val response = retrofitService.save(
            Post(
                id = 0,
                content = content,
                authorId = 0,
                author = "",
                authorAvatar = "",
                likedByMe = false,
                likes = 0,
                published = 0,
            )
        )
        if (!response.isSuccessful)
            throw Exception("Request is not successful")
        val createdPost = response.body()
        dao.insert(PostEntity.fromDto(createdPost!!))
    }

    suspend fun update(post: Post) {
        val response = retrofitService.save(post)
        if (!response.isSuccessful)
            throw Exception("Request is not successful")
        val updatedPost = response.body()
        dao.insert(PostEntity.fromDto(updatedPost!!))
    }
}