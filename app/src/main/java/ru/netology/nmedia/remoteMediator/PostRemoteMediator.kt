package ru.netology.nmedia.remoteMediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import ru.netology.nmedia.ApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dao.PostRemoteKeyDao
import ru.netology.nmedia.dao.ProfileDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.entity.KeyType
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.PostRemoteKeyEntity
import ru.netology.nmedia.entity.toEntity
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator @Inject constructor(
    private val service: ApiService,
    private val db: AppDb,
    private val postDao: PostDao,
    private val postRemoteKeyDao: PostRemoteKeyDao,
    private val profileDao: ProfileDao
) : RemoteMediator<Int, PostEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>
    ): MediatorResult {
        try {
            val minId = postRemoteKeyDao.min() ?: -1
            val maxId = postRemoteKeyDao.max() ?: -1
            val response = when (loadType) {
                LoadType.REFRESH -> {
                    if (maxId == -1) {
                        service.getLatestPosts(state.config.initialLoadSize,profileDao.getAccessToken())
                    } else {
                        service.getNewerPosts(maxId,profileDao.getAccessToken())
                    }
                }

                LoadType.PREPEND -> {
                    val id = postRemoteKeyDao.max() ?: return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                    service.getAfterPost(id, state.config.pageSize,profileDao.getAccessToken())
                }

                LoadType.APPEND -> {
                    val id = postRemoteKeyDao.min() ?: return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                    service.getBeforePost(id, state.config.pageSize,profileDao.getAccessToken())
                }
            }
            if (!response.isSuccessful) {
                throw Exception()
            }
            val body = (response.body() ?: throw Exception())
            if (body.isNotEmpty()) {
                db.withTransaction {
                    when (loadType) {
                        LoadType.REFRESH -> {
                            postRemoteKeyDao.insert(
                                listOf(

                                    PostRemoteKeyEntity(
                                        type = KeyType.AFTER,
                                        id = body.first().id,
                                    ),
                                    PostRemoteKeyEntity(
                                        type = KeyType.BEFORE,
                                        id = if (minId == -1) body.last().id else kotlin.math.min(
                                            minId,
                                            body.last().id
                                        )
                                    )
                                )
                            )
                        }

                        LoadType.APPEND -> {
                            postRemoteKeyDao.insert(
                                PostRemoteKeyEntity(
                                    type = KeyType.BEFORE,
                                    id = body.last().id
                                )
                            )
                        }

                        LoadType.PREPEND -> {
                            postRemoteKeyDao.insert(
                                PostRemoteKeyEntity(
                                    type = KeyType.AFTER,
                                    id = body.first().id,
                                )
                            )
                        }
                    }
                    postDao.insert(body.toEntity())
                }
            }
            return MediatorResult.Success(endOfPaginationReached = false)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }
}