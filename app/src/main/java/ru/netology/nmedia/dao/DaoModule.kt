package ru.netology.nmedia.dao
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dao.jobDao.MyJobDao
import ru.netology.nmedia.dao.postDao.AllPostDao
import ru.netology.nmedia.dao.postDao.MyPostDao
import ru.netology.nmedia.dao.postDao.PostDao
import ru.netology.nmedia.dao.postRemoteKeyDao.AllPostRemoteKeyDao
import ru.netology.nmedia.dao.postRemoteKeyDao.MyPostRemoteKeyDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Profile
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DaoModule {
    @Provides
    @Singleton
    fun provideAllPostDao(db: AppDb): AllPostDao = db.allPostDao()
    @Provides
    @Singleton
    fun provideMyPostDao(db: AppDb): MyPostDao = db.myPostDao()
    @Provides
    @Singleton
    fun provideAllPostRemoteKeyDao(db: AppDb): AllPostRemoteKeyDao = db.allPostRemoteKeyDao()
    @Provides
    @Singleton
    fun provideMyPostRemoteKeyDao(db: AppDb): MyPostRemoteKeyDao = db.myPostRemoteKeyDao()
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
    fun provideMyJobDao(db:AppDb):MyJobDao = db.myJobDao()
    @Provides
    @Singleton
    fun provideLiveDataProfile(auth: AppAuth):LiveData<Profile> = auth.authStateFlow.asLiveData()
}