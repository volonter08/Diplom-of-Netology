package ru.netology.nmedia.repository

import androidx.room.withTransaction
import com.google.gson.Gson
import ru.netology.nmedia.PostApiService
import ru.netology.nmedia.activity.PostCreateRequest
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.AllPostEntity
import ru.netology.nmedia.entity.MyPostEntity
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

    override suspend fun remove(removedPost: Post, token: String?) {
        val response = retrofitService.removePostById(removedPost.id, token)
        if (!response.isSuccessful) {
            val error: ErrorResponse? =
                Gson().fromJson(
                    response.errorBody()!!.charStream(),
                    ErrorResponse::class.java
                )
            throw Exception(error?.reason)
        } else
            appDb?.withTransaction {
                appDb.allPostDao().remove(AllPostEntity(removedPost))
                appDb.myPostDao().remove(MyPostEntity(removedPost))
            }
    }

    override suspend fun createPost(content: String, link: String?,token: String?) {
        val response = retrofitService.savePost(
            PostCreateRequest(
                id = 0,
                content = content,
                link = link
            ),token
        )
        if (!response.isSuccessful) {
            val error: ErrorResponse? =
                Gson().fromJson(
                    response.errorBody()!!.charStream(),
                    ErrorResponse::class.java
                )
            throw Exception(error?.reason)
        } else {
            response.body()?.let { post ->
                appDb?.withTransaction {
                    appDb.allPostDao().insert(AllPostEntity(post))
                    appDb.myPostDao().insert(MyPostEntity(post))
                }
            }
        }
    }

    override suspend fun update(post: Post,token:String?) {
        val response = retrofitService.savePost(
            PostCreateRequest(
                id = post.id,
                content = post.content,
                coords = post.coords,
                link = post.link,
                attachment = post.attachment,
                mentionIds = post.mentionIds
            ),token
        )
        if (!response.isSuccessful) {
            val error: ErrorResponse? =
                Gson().fromJson(
                    response.errorBody()!!.charStream(),
                    ErrorResponse::class.java
                )
            throw Exception(error?.reason)
        } else {
            response.body()?.let { post ->
                appDb?.withTransaction {
                    appDb.allPostDao().update(AllPostEntity(post))
                    appDb.myPostDao().update(MyPostEntity(post))
                }
            }
        }
    }
}