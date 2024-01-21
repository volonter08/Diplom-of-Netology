package ru.netology.nmedia.remoteMediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import ru.netology.nmedia.AllEventsApiService
import ru.netology.nmedia.dao.EventDao
import ru.netology.nmedia.dao.EventRemoteKeyDao
import ru.netology.nmedia.dao.ProfileDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.entity.EventEntity
import ru.netology.nmedia.entity.EventRemoteKeyEntity
import ru.netology.nmedia.entity.KeyType
import ru.netology.nmedia.entity.toEntity
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class EventRemoteMediator @Inject constructor(
    private val service: AllEventsApiService,
    private val db: AppDb,
    private val eventDao: EventDao,
    private val eventRemoteKeyDao: EventRemoteKeyDao,
    private val profileDao: ProfileDao
) : RemoteMediator<Int, EventEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, EventEntity>
    ): MediatorResult {
        try {
            val response = when (loadType) {
                LoadType.REFRESH -> {
                        service.getLatestEvents(state.config.initialLoadSize,profileDao.getAccessToken())
                }

                LoadType.PREPEND -> {
                    val id = eventRemoteKeyDao.max() ?: return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                    service.getAfterEvent(id, state.config.pageSize,profileDao.getAccessToken())
                }

                LoadType.APPEND -> {
                    val id = eventRemoteKeyDao.min() ?: return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                    service.getBeforeEvent(id, state.config.pageSize,profileDao.getAccessToken())
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
                            eventDao.removeAll()
                            eventRemoteKeyDao.insert(
                                listOf(
                                    EventRemoteKeyEntity(
                                        type = KeyType.AFTER,
                                        id = body.first().id,
                                    ),
                                    EventRemoteKeyEntity(
                                        type = KeyType.BEFORE,
                                        id = body.last().id
                                    )
                                )
                            )
                        }

                        LoadType.APPEND -> {
                            eventRemoteKeyDao.insert(
                                EventRemoteKeyEntity(
                                    type = KeyType.BEFORE,
                                    id = body.last().id
                                )
                            )
                        }

                        LoadType.PREPEND -> {
                            eventRemoteKeyDao.insert(
                                EventRemoteKeyEntity(
                                    type = KeyType.AFTER,
                                    id = body.first().id,
                                )
                            )
                        }
                    }
                    eventDao.insert(body.toEntity())
                }
            }
            return MediatorResult.Success(endOfPaginationReached = false)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }
}