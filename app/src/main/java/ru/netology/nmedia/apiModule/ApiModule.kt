package ru.netology.nmedia.apiModule

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.netology.nmedia.ApiService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    const val BASE_URL = "https://netomedia.ru/"
    @Provides
    @Singleton
    fun provideService(): ApiService {
        return Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL).client(
            OkHttpClient.Builder().addInterceptor( HttpLoggingInterceptor()
                .apply {
                        level = HttpLoggingInterceptor.Level.BODY
                }).build()
        ).build().create(ApiService::class.java)
    }
}