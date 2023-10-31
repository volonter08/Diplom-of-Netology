package ru.netology.nmedia.model

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import ru.netology.nmedia.ApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dao.PostRemoteKeyDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepository @Inject constructor(
    appDb: AppDb,
    private val dao: PostDao,
    private val daoRemoteKey:PostRemoteKeyDao,
    val retrofitService: ApiService
) : Repository<List<Post>> {

    @OptIn(ExperimentalPagingApi::class)
    val data: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false, initialLoadSize = 10),
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
        dao.insert(listPosts.map {
            PostEntity.fromDto(it)
        })
    }

    suspend fun like(id: Int) {
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

    suspend fun dislike(id: Int) {
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

    fun getNewerCount(id: Int): Flow<Int> = flow {
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

    suspend fun remove(id: Int) {
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
                published = " ",
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