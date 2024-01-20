package ru.netology.nmedia.repository

import androidx.paging.PagingData
import androidx.room.withTransaction
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.PostApiService
import ru.netology.nmedia.requests.PostCreateRequest
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.AllPostEntity
import ru.netology.nmedia.entity.MyPostEntity
import ru.netology.nmedia.responses.ErrorResponse

abstract class PostRepository constructor(
    val appDb: AppDb? = null,
    val retrofitService: PostApiService
) : Repository<Post,PostCreateRequest> {
    abstract val data: Flow<PagingData<Post>>
    override suspend fun like(liked: Post, token: String?) {
        val response = retrofitService.likePostById(liked.id, token)
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

    override suspend fun dislike(disliked: Post, token: String?) {
        val response = retrofitService.dislikePostById(disliked.id, token)
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
    override suspend fun remove(removed: Post, token: String?) {
        val response = retrofitService.removePostById(removed.id, token)
        if (!response.isSuccessful) {
            val error: ErrorResponse? =
                Gson().fromJson(
                    response.errorBody()!!.charStream(),
                    ErrorResponse::class.java
                )
            throw Exception(error?.reason)
        } else
            appDb?.withTransaction {
                appDb.allPostDao().remove(AllPostEntity(removed))
                appDb.myPostDao().remove(MyPostEntity(removed))
            }
    }

    override suspend fun save(createRequest:PostCreateRequest, token:String?) {
        val response = retrofitService.savePost(
            createRequest,token
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
}