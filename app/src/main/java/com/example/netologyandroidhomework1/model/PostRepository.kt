package com.example.netologyandroidhomework1.model

import androidx.lifecycle.map
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.netologyandroidhomework1.PostsApiService
import com.example.netologyandroidhomework1.dao.PostDao
import com.example.netologyandroidhomework1.dto.Post
import com.example.netologyandroidhomework1.entity.PostEntity
import com.example.netologyandroidhomework1.entity.toDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class PostRepository @Inject constructor(
    private val dao: PostDao,
    val retrofitService: PostsApiService
) : Repository<List<Post>> {

    val data: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize =2, enablePlaceholders = false, initialLoadSize = 2),
        pagingSourceFactory = { PostPagingSource(retrofitService) },
    ).flow
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

    fun share(id: Int) {
    }

    suspend fun remove(id: Long) {
        dao.removeById(id)
        val response = retrofitService.removeById(id)
        if (!response.isSuccessful)
            throw Exception("Request is not successful")
    }

    suspend fun createPost(content: String) {
        val response = retrofitService.save(Post(id = 0,
            content = content,
            authorId = 0,
            author = "",
            authorAvatar = "",
            likedByMe = false,
            likes = 0,
            published = 0,))
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