package com.example.netologyandroidhomework1.apiModule

import com.example.netologyandroidhomework1.OkHttpClientObject
import com.example.netologyandroidhomework1.PostsApiService
import com.example.netologyandroidhomework1.model.PostRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    const val BASE_URL = "http://10.0.2.2:9999/"
    @Provides
    @Singleton
    fun provideService(): PostsApiService {
        return Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL).client(
            OkHttpClientObject.get()
        ).build().create(PostsApiService::class.java)
    }
}