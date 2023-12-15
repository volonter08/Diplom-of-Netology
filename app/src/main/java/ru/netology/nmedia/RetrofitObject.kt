package ru.netology.nmedia

import okhttp3.FormBody
import okhttp3.RequestBody
import ru.netology.nmedia.dto.Post
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import ru.netology.nmedia.dto.Event
import ru.netology.nmedia.dto.User
import ru.netology.nmedia.requests.AuthenticationRequest
import ru.netology.nmedia.responses.Token

interface ApiService {
    //Authorization
    @POST("api/users/authentication")
    suspend fun signIn(@Body authenticationRequest: AuthenticationRequest):Response<Token>
    @POST("api/users/registration")
    suspend fun signUp(@Body formBody: FormBody):Response<Token>
    //Posts
    @GET("api/posts")
    suspend fun getAllPosts(): Response<List<Post>>

    @GET("api/posts/{id}/newer")
    suspend fun getNewerPosts(@Path("id") id: Int, @Header("Authorization") token:String?): Response<List<Post>>

    @GET("api/posts/{id}/before")
    suspend fun getBeforePost(
        @Path("id") id: Int,
        @Query("count") count: Int,
        @Header("Authorization") token:String?
    ): Response<List<Post>>

    @GET("api/posts/{id}/after")
    suspend fun getAfterPost(
        @Path("id") id: Int,
        @Query("count") count: Int,
        @Header("Authorization") token:String?
    ): Response<List<Post>>

    @GET("api/posts/latest")
    suspend fun getLatestPosts(@Query("count",) count: Int, @Header("Authorization") token:String?): Response<List<Post>>

    @GET("api/posts/{id}")
    suspend fun getPostById(@Path("id") id: Long): Response<Post>

    @POST("api/posts")
    suspend fun savePost(@Body post: Post): Response<Post>

    @DELETE("api/posts/{id}")
    suspend fun removePostById(@Path("id") id: Int, @Header("Authorization") token:String?): Response<Unit>

    @POST("api/posts/{id}/likes")
    suspend fun likePostById(@Path("id") id: Int, @Header("Authorization") token:String?): Response<Post>

    @DELETE("api/posts/{id}/likes")
    suspend fun dislikePostById(@Path("id") id: Int, @Header("Authorization") token:String?): Response<Post>
    //Events
    @GET("api/events")
    suspend fun getAllEvents(): Response<List<Event>>

    @GET("api/events/{id}/newer")
    suspend fun getNewerEvents(@Path("id") id: Int, @Header("Authorization") token:String?): Response<List<Event>>
    @GET("api/events/{id}/before")
    suspend fun getBeforeEvent(
        @Path("id") id: Int,
        @Query("count") count: Int,
        @Header("Authorization") token:String?
    ): Response<List<Event>>

    @GET("api/events/{id}/after")
    suspend fun getAfterEvent(
        @Path("id") id: Int,
        @Query("count") count: Int,
        @Header("Authorization") token:String?
    ): Response<List<Event>>

    @GET("api/events/latest")
    suspend fun getLatestEvents(@Query("count",) count: Int, @Header("Authorization") token:String?): Response<List<Event>>

    @GET("api/posts/{id}")
    suspend fun getEventById(@Path("id") id: Long): Response<Event>

    @POST("api/posts")
    suspend fun saveEvent(@Body event: Post): Response<Event>

    @DELETE("api/posts/{id}")
    suspend fun removeEventById(@Path("id") id: Int, @Header("Authorization") token:String?): Response<Unit>

    @POST("api/posts/{id}/likes")
    suspend fun likeEventById(@Path("id") id: Int, @Header("Authorization") token:String?): Response<Event>

    @DELETE("api/posts/{id}/likes")
    suspend fun dislikeEventById(@Path("id") id: Int, @Header("Authorization") token:String?): Response<Event>
    //User
    @GET("api/users/{userId}")
    suspend fun getUserById(@Path("userId") userId:String):Response<User>
}