package ru.netology.nmedia

import okhttp3.RequestBody
import ru.netology.nmedia.dto.Post
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import ru.netology.nmedia.dto.User
import ru.netology.nmedia.requests.AuthenticationRequest
import ru.netology.nmedia.responses.Token
import java.io.File

interface ApiService {

    @GET("api/posts")
    suspend fun getAll(): Response<List<Post>>

    @GET("api/posts/{id}/newer")
    suspend fun getNewer(@Path("id") id: Int): Response<List<Post>>
    @POST("api/users/authentication")
    suspend fun signIn(@Body authenticationRequest: AuthenticationRequest):Response<Token>
    @Multipart
    @POST("api/users/registration")
    suspend fun signUp(@Part("login") login:String,@Part("password") password:String,@Part("name") name:String,@Part("file") file: RequestBody?=null):Response<Token>
    @GET("api/posts/{id}/before")
    suspend fun getBefore(
        @Path("id") id: Int,
        @Query("count") count: Int
    ): Response<List<Post>>

    @GET("api/posts/{id}/after")
    suspend fun getAfter(
        @Path("id") id: Int,
        @Query("count") count: Int
    ): Response<List<Post>>

    @GET("api/posts/latest")
    suspend fun getLatest(@Query("count") count: Int): Response<List<Post>>

    @GET("api/posts/{id}")
    suspend fun getById(@Path("id") id: Long): Response<Post>

    @POST("api/posts")
    suspend fun save(@Body post: Post): Response<Post>

    @DELETE("api/posts/{id}")
    suspend fun removeById(@Path("id") id: Int): Response<Unit>

    @POST("api/posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Int): Response<Post>

    @DELETE("api/posts/{id}/likes")
    suspend fun dislikeById(@Path("id") id: Int): Response<Post>

    @GET("api/users/{userId}")
    suspend fun getUserById(@Path("userId") userId:String):Response<User>
}