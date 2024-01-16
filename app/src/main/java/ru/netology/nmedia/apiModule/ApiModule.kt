package ru.netology.nmedia.apiModule

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.netology.nmedia.AllEventsApiService
import ru.netology.nmedia.AllPostApiService
import ru.netology.nmedia.AuthApiService
import ru.netology.nmedia.JobApiService
import ru.netology.nmedia.MyJobApiService
import ru.netology.nmedia.MyPostApiService
import ru.netology.nmedia.UserJobApiService
import ru.netology.nmedia.UserPostApiService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    const val BASE_URL = "https://netomedia.ru/"
    @Provides
    @Singleton
    fun provideAuthService(): AuthApiService {
        return Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL).client(
            OkHttpClient.Builder().addInterceptor( HttpLoggingInterceptor()
                .apply {
                        level = HttpLoggingInterceptor.Level.BODY
                }).build()
        ).build().create(AuthApiService::class.java)
    }
    @Provides
    @Singleton
    fun provideAllPostsService(): AllPostApiService {
        return Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL).client(
                OkHttpClient.Builder().addInterceptor( HttpLoggingInterceptor()
                    .apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    }).build()
            ).build().create(AllPostApiService::class.java)
    }
    @Provides
    @Singleton
    fun provideMyPostService(): MyPostApiService {
        return Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL).client(
                OkHttpClient.Builder().addInterceptor( HttpLoggingInterceptor()
                    .apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    }).build()
            ).build().create(MyPostApiService::class.java)
    }
    @Provides
    @Singleton
    fun provideAllEventService(): AllEventsApiService {
        return Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL).client(
                OkHttpClient.Builder().addInterceptor( HttpLoggingInterceptor()
                    .apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    }).build()
            ).build().create(AllEventsApiService::class.java)
    }
    @Provides
    @Singleton
    fun provideUserService(): UserPostApiService {
        return Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL).client(
                OkHttpClient.Builder().addInterceptor( HttpLoggingInterceptor()
                    .apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    }).build()
            ).build().create(UserPostApiService::class.java)
    }
    @Provides
    @Singleton
    fun provideMyJobApiService(): MyJobApiService {
        return Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL).client(
                OkHttpClient.Builder().addInterceptor( HttpLoggingInterceptor()
                    .apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    }).build()
            ).build().create(MyJobApiService::class.java)
    }
    @Provides
    @Singleton
    fun provideUserJobApiService(): UserJobApiService {
        return Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL).client(
                OkHttpClient.Builder().addInterceptor( HttpLoggingInterceptor()
                    .apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    }).build()
            ).build().create(UserJobApiService::class.java)
    }
}