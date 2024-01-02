package ru.netology.nmedia.repository

import androidx.room.withTransaction
import com.google.gson.Gson
import ru.netology.nmedia.PostApiService
import ru.netology.nmedia.dao.postDao.AllPostDao
import ru.netology.nmedia.dao.postDao.PostDao
import ru.netology.nmedia.dao.postDao.insert
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.AllPostEntity
import ru.netology.nmedia.entity.MyPostEntity
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.responses.ErrorResponse

abstract class PostRepository constructor(
    val appDb: AppDb? = null,
    val retrofitService: PostApiService
) : Repository<Post> {
    override suspend fun like(likedPost: Post, token: String?) {
        val response = retrofitService.likePostById(likedPost.id, token)
        if (!response.isSuccessful) {
            val error: ErrorResponse? =
                Gson().fromJson(
                    response.errorBody()!!.charStream(),
                    ErrorResponse::class.java
                )
            throw Exception(error?.reason)
        } else
            response.body()?.let { post ->
                appDb?.withTransaction {
                    appDb.allPostDao().update(AllPostEntity(post))
                    appDb.myPostDao().update(MyPostEntity(post))
                }

            }
    }

    override suspend fun dislike(dislikedPost: Post, token: String?) {
        val response = retrofitService.dislikePostById(dislikedPost.id, token)
        if (!response.isSuccessful) {
            val error: ErrorResponse? =
                Gson().fromJson(
                    response.errorBody()!!.charStream(),
                    ErrorResponse::class.java
                )
            throw Exception(error?.reason)
        } else
            response.body()?.let { post ->
                appDb?.withTransaction {
                    appDb.allPostDao().update(AllPostEntity(post))
                    appDb.myPostDao().update(MyPostEntity(post))
                }

            }
    }

    fun share(id: Int) {
    }

    override suspend fun remove(post: Post, token: String?) {
        appDb?.withTransaction {
            appDb.allPostDao().remove(AllPostEntity(post))
            appDb.myPostDao().remove(MyPostEntity(post))
        }
        val response = retrofitService.removePostById(post.id, token)
        if (!response.isSuccessful)
            throw Exception("Request is not successful")
    }

    override suspend fun createPost(content: String) {
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
        response.body()?.let {post->
            appDb?.withTransaction {
                appDb.allPostDao().insert(AllPostEntity(post))
                appDb.myPostDao().insert(MyPostEntity(post))
            }

        }
    }

    override suspend fun update(post: Post) {
        val response = retrofitService.savePost(post)
        if (!response.isSuccessful)
            throw Exception("Request is not successful")
        response.body()?.let {post->
            appDb?.withTransaction {
                appDb.allPostDao().update(AllPostEntity(post))
                appDb.myPostDao().update(MyPostEntity(post))
            }

        }
    }
}