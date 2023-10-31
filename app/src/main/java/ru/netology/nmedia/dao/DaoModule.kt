package ru.netology.nmedia.dao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.nmedia.db.AppDb
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
}