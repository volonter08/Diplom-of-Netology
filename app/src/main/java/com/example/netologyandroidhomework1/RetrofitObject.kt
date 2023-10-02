package com.example.netologyandroidhomework1

import com.example.netologyandroidhomework1.dto.Post
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface PostsApiService {

    @GET("api/slow/posts")
    suspend fun getAll(): Response<List<Post>>

    @GET("api/slow/posts/{id}/newer")
    suspend fun getNewer(@Path("id") id: Long): Response<List<Post>>

    @GET("api/slow/posts/{id}/before")
    suspend fun getBefore(
        @Path("id") id: Long,
        @Query("count") count: Int
    ): Response<List<Post>>

    @GET("api/slow/posts/{id}/after")
    suspend fun getAfter(
        @Path("id") id: Long,
        @Query("count") count: Int
    ): Response<List<Post>>

    @GET("api/slow/posts/latest")
    suspend fun getLatest(@Query("count") count: Int): Response<List<Post>>

    @GET("api/slow/posts/{id}")
    suspend fun getById(@Path("id") id: Long): Response<Post>

    @POST("api/slow/posts")
    suspend fun save(@Body post: Post): Response<Post>

    @DELETE("api/slow/posts/{id}")
    suspend fun removeById(@Path("id") id: Long): Response<Unit>

    @POST("api/slow/posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Response<Post>

    @DELETE("api/slow/posts/{id}/likes")
    suspend fun dislikeById(@Path("id") id: Long): Response<Post>
}