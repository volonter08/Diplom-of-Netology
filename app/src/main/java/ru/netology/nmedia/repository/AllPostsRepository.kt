package ru.netology.nmedia.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.netology.nmedia.AllPostApiService
import ru.netology.nmedia.dao.ProfileDao
import ru.netology.nmedia.dao.postDao.AllPostDao
import ru.netology.nmedia.dao.postRemoteKeyDao.AllPostRemoteKeyDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.remoteMediator.PostRemoteMediator
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AllPostsRepository @Inject constructor(
    appDb: AppDb,
    dao: AllPostDao,
    daoRemoteKey: AllPostRemoteKeyDao,
    profileDao: ProfileDao,
    retrofitService: AllPostApiService
) : PostRepository( appDb,retrofitService) {
    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false, initialLoadSize = 10),
        remoteMediator = PostRemoteMediator(retrofitService, appDb, dao, daoRemoteKey, profileDao),
        pagingSourceFactory = dao::pagingSource
    ).flow.map { pagingData ->
        pagingData.map {
            it.toDto()
        }
    }
}