package ru.netology.nmedia

import okhttp3.FormBody
import ru.netology.nmedia.dto.Post
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import ru.netology.nmedia.requests.PostCreateRequest
import ru.netology.nmedia.dto.Event
import ru.netology.nmedia.dto.Job
import ru.netology.nmedia.dto.User
import ru.netology.nmedia.requests.AuthenticationRequest
import ru.netology.nmedia.requests.EventCreateRequest
import ru.netology.nmedia.requests.JobCreateRequest
import ru.netology.nmedia.responses.Token

interface AuthApiService {
    //Authorization
    @POST("api/users/authentication")
    suspend fun signIn(@Body authenticationRequest: AuthenticationRequest): Response<Token>

    @POST("api/users/registration")
    suspend fun signUp(@Body formBody: FormBody): Response<Token>
    @GET("api/users/{userId}")
    suspend fun getUserById(@Path("userId") userId: String): Response<User>

}
interface JobApiService{

    suspend fun saveJobs(token: String?,job: JobCreateRequest):Response<Job>
    suspend fun removeJobs( token: String?, jobId:Int):Response<Unit>

}
interface MyJobApiService:JobApiService{
    @GET("api/my/jobs")
    suspend fun getMyJobs(@Header("Authorization") token: String?): Response<List<Job>>
    @POST("api/my/jobs")
    override suspend fun saveJobs(@Header("Authorization") token: String?,@Body job: JobCreateRequest):Response<Job>
    @DELETE("api/my/jobs/{job_id}")
    override suspend fun removeJobs(@Header("Authorization") token: String?,@Path("job_id") jobId:Int):Response<Unit>

}
interface UserJobApiService:JobApiService{
    @GET("api/{user_id}/jobs")
    suspend fun getUserJobs(@Header("Authorization") token: String?,@Path("user_id") userId: Int): Response<List<Job>>

}
interface PostApiService {

    suspend fun getNewerPosts(id: Int, token: String?): Response<List<Post>>

    suspend fun getBeforePost(
        id: Int,
        count: Int,
        token: String?
    ): Response<List<Post>>

    suspend fun getAfterPost(
        id: Int,
        count: Int,
        token: String?
    ): Response<List<Post>>


    suspend fun getLatestPosts(count: Int, token: String?): Response<List<Post>>

    //CRUD posts
    @POST("api/posts")
    suspend fun savePost(@Body post: PostCreateRequest, @Header("Authorization") token: String?): Response<Post>

    @DELETE("api/posts/{id}")
    suspend fun removePostById(
        @Path("id") id: Int,
        @Header("Authorization") token: String?
    ): Response<Unit>

    @POST("api/posts/{id}/likes")
    suspend fun likePostById(
        @Path("id") id: Int,
        @Header("Authorization") token: String?
    ): Response<Post>

    @DELETE("api/posts/{id}/likes")
    suspend fun dislikePostById(
        @Path("id") id: Int,
        @Header("Authorization") token: String?
    ): Response<Post>


}

interface AllPostApiService : PostApiService {
    @GET("api/posts/{id}/newer")
    override suspend fun getNewerPosts(
        @Path("id") id: Int,
        @Header("Authorization") token: String?
    ): Response<List<Post>>

    @GET("api/posts/{id}/before")
    override suspend fun getBeforePost(
        @Path("id") id: Int,
        @Query("count") count: Int,
        @Header("Authorization") token: String?
    ): Response<List<Post>>

    @GET("api/posts/{id}/after")
    override suspend fun getAfterPost(
        @Path("id") id: Int,
        @Query("count") count: Int,
        @Header("Authorization") token: String?
    ): Response<List<Post>>

    @GET("api/posts/latest")
    override suspend fun getLatestPosts(
        @Query("count") count: Int,
        @Header("Authorization") token: String?
    ): Response<List<Post>>
}

interface MyPostApiService : PostApiService {
    @GET("api/my/wall/{id}/newer")
    override suspend fun getNewerPosts(
        @Path("id") id: Int,
        @Header("Authorization") token: String?
    ): Response<List<Post>>

    @GET("api/my/wall/{id}/before")
    override suspend fun getBeforePost(
        @Path("id") id: Int,
        @Query("count") count: Int,
        @Header("Authorization") token: String?
    ): Response<List<Post>>

    @GET("api/my/wall/{id}/after")
    override suspend fun getAfterPost(
        @Path("id") id: Int,
        @Query("count") count: Int,
        @Header("Authorization") token: String?
    ): Response<List<Post>>

    @GET("api/my/wall/latest")
    override suspend fun getLatestPosts(
        @Query("count") count: Int,
        @Header("Authorization") token: String?
    ): Response<List<Post>>
}

interface UserPostApiService : PostApiService {

    @GET("api/{author_id}/wall/latest")
    suspend fun getLatestPost(
        @Path("author_id") authorId: Int,
        @Query("count") count: Int,
        @Header("Authorization") token: String?
    ): Response<List<Post>>

    @GET("api/{author_id}/wall/{post_id}/before")
    suspend fun getBeforePost(
        @Path("author_id") authorId: Int,
        @Path("id") id: Int,
        @Query("count") count: Int,
        @Header("Authorization") token: String?
    ): Response<List<Post>>

}

interface EventApiService {

    //CRUD events
    @POST("api/events")
    suspend fun saveEvent(@Body event: EventCreateRequest,@Header("Authorization") token: String?): Response<Event>

    @DELETE("api/events/{id}")
    suspend fun removeEventById(
        @Path("id") id: Int,
        @Header("Authorization") token: String?
    ): Response<Unit>

    @POST("api/events/{id}/likes")
    suspend fun likeEventById(
        @Path("id") id: Int,
        @Header("Authorization") token: String?
    ): Response<Event>

    @DELETE("api/events/{id}/likes")
    suspend fun dislikeEventById(
        @Path("id") id: Int,
        @Header("Authorization") token: String?
    ): Response<Event>
    @POST("api/events/{id}/participants")
    suspend fun participate(
        @Header("Authorization") token: String?,
        @Path("id") id: Int
    ):Response<Event>
    @DELETE("api/events/{id}/participants")
    suspend fun unparticipate(
        @Header("Authorization") token: String?,
        @Path("id") id: Int
    ):Response<Event>
    suspend fun getNewerEvents(id: Int, token: String?): Response<List<Event>>
    suspend fun getBeforeEvent(
        id: Int,
        count: Int,
        token: String?
    ): Response<List<Event>>


    suspend fun getAfterEvent(
        id: Int,
        count: Int,
        token: String?
    ): Response<List<Event>>


    suspend fun getLatestEvents(count: Int, token: String?): Response<List<Event>>
}

interface AllEventsApiService : EventApiService {
    @GET("api/events/{id}/newer")
    override suspend fun getNewerEvents(
        @Path("id") id: Int,
        @Header("Authorization") token: String?
    ): Response<List<Event>>

    @GET("api/events/{id}/before")
    override suspend fun getBeforeEvent(
        @Path("id") id: Int,
        @Query("count") count: Int,
        @Header("Authorization") token: String?
    ): Response<List<Event>>

    @GET("api/events/{id}/after")
    override suspend fun getAfterEvent(
        @Path("id") id: Int,
        @Query("count") count: Int,
        @Header("Authorization") token: String?
    ): Response<List<Event>>

    @GET("api/events/latest")
    override suspend fun getLatestEvents(
        @Query("count") count: Int,
        @Header("Authorization") token: String?
    ): Response<List<Event>>
}