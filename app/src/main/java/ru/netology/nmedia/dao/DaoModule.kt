package ru.netology.nmedia.dao
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Profile
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DaoModule {
    @Provides
    @Singleton
    fun providePostDao(db: AppDb): PostDao = db.postDao()
    @Provides
    @Singleton
    fun providePostRemoteKeyDao(db: AppDb): PostRemoteKeyDao = db.postRemoteKeyDao()
    @Provides
    @Singleton
    fun provideProfileDao(db:AppDb):ProfileDao = db.profileDao()
    @Provides
    @Singleton
    fun provideEventDao(db:AppDb):EventDao = db.eventDao()
    @Provides
    @Singleton
    fun provideEventRemoteKeyDao(db: AppDb): EventRemoteKeyDao = db.eventRemoteKeyDao()
    @Provides
    @Singleton
    fun provideLiveDataProfile(auth: AppAuth):LiveData<Profile> = auth.authStateFlow.asLiveData(Dispatchers.Default)
}