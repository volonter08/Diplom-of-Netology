package ru.netology.nmedia.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.InvalidatingPagingSourceFactory
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.room.withTransaction
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.UserPostApiService
import ru.netology.nmedia.dao.ProfileDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.AllPostEntity
import ru.netology.nmedia.entity.MyPostEntity
import ru.netology.nmedia.responses.ErrorResponse


class UserPostsRepository @AssistedInject constructor(
    @Assisted
    authorId: Int,
    retrofitService: UserPostApiService,
    profileDao: ProfileDao
) : PostRepository( retrofitService= retrofitService) {
    @OptIn(ExperimentalPagingApi::class)
    val invalidatingPagingSourceFactory = InvalidatingPagingSourceFactory{
        UserPostPagingSource(retrofitService,authorId,profileDao)
    }
    override val data: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false, initialLoadSize = 10),
        pagingSourceFactory =  invalidatingPagingSourceFactory
    ).flow

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
            invalidatingPagingSourceFactory.invalidate()
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
            invalidatingPagingSourceFactory.invalidate()
    }

}

class UserPostPagingSource(
    private val service: UserPostApiService, private val authorId: Int, private val profileDao: ProfileDao
) : PagingSource<Int, Post>() {
    override fun getRefreshKey(state: PagingState<Int, Post>): Int? {
        return null
    }
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
        try {
            val response = when (params) {
                is LoadParams.Refresh -> service.getLatestPost(
                    authorId,
                    params.loadSize,
                    profileDao.getAccessToken()
                )

                is LoadParams.Prepend -> return LoadResult.Page(
                    data = emptyList(),
                    prevKey = params.key,
                    nextKey = null
                )

                is LoadParams.Append -> service.getBeforePost(
                    authorId,
                    params.key,
                    params.loadSize,
                    profileDao.getAccessToken()
                )
            }
            if (!response.isSuccessful) {
                throw Exception("Request is not successful")
            }
            val body = response.body() ?: throw Exception("Request is not successful")

            val nextKey = if (body.isEmpty()) null else body.last().id
            return LoadResult.Page(
                data = body,
                prevKey = params.key,
                nextKey = nextKey,
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }
}

@AssistedFactory
interface UserPostRepositoryFactory {
    fun create(authorId: Int): UserPostsRepository
}