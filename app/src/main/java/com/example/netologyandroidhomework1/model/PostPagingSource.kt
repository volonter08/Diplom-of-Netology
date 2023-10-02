package com.example.netologyandroidhomework1.model

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.netologyandroidhomework1.PostsApiService
import com.example.netologyandroidhomework1.dto.Post

class PostPagingSource (
    private val service: PostsApiService,
) : PagingSource<Long, Post>() {
    override fun getRefreshKey(state: PagingState<Long, Post>): Long? {
        return null
    }

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Post> {
        try {
            val response = when (params) {
                is LoadParams.Refresh -> service.getLatest(params.loadSize)
                is LoadParams.Prepend -> return LoadResult.Page(
                    data = emptyList(),
                    prevKey = params.key,
                    nextKey = null
                )
                is LoadParams.Append -> service.getBefore(params.key, params.loadSize)
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