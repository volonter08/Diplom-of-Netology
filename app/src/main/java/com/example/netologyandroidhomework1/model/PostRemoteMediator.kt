package com.example.netologyandroidhomework1.model

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.netologyandroidhomework1.PostsApiService
import com.example.netologyandroidhomework1.dao.PostDao
import com.example.netologyandroidhomework1.dao.PostRemoteKeyDao
import com.example.netologyandroidhomework1.db.AppDb
import com.example.netologyandroidhomework1.entity.PostEntity
import com.example.netologyandroidhomework1.entity.PostRemoteKeyEntity
import com.example.netologyandroidhomework1.entity.toEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Long.min
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator @Inject constructor(
    private val service: PostsApiService,
    private val db: AppDb,
    private val postDao: PostDao,
    private val postRemoteKeyDao: PostRemoteKeyDao,
) : RemoteMediator<Int, PostEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>
    ): MediatorResult {
        try {
            println(loadType)
            val minId = postRemoteKeyDao.min()?:-1
            val maxId = postRemoteKeyDao.max()?:-1
            val response = when (loadType) {
                LoadType.REFRESH -> service.getNewer(maxId)
                LoadType.PREPEND -> {
                    return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                }
                LoadType.APPEND -> {
                    val id = postRemoteKeyDao.min() ?: return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                    service.getBefore(id, state.config.pageSize)
                }
            }
            if (!response.isSuccessful) {
                throw Exception()
            }
            val body = (response.body()?.reversed()?: throw Exception())
            db.withTransaction {
                when (loadType) {
                    LoadType.REFRESH -> {
                        /*if(lastId > maxId) {
                            CoroutineScope(Dispatchers.IO).launch{
                                    while (lastId > maxId && maxId!= -1L) {
                                        val response =
                                            service.getBefore(lastId, state.config.pageSize)
                                        if (!response.isSuccessful) {
                                            throw Exception()
                                        }
                                        val body = (response.body()?.filter {
                                            it.id !in (minId..maxId)
                                        } ?: throw Exception())
                                        if(body.isEmpty()){
                                            break
                                        }
                                        postDao.insert(body.toEntity())
                                        lastId = body.last().id
                                    }
                            }
                        }

                         */
                        postRemoteKeyDao.insert(
                            listOf(
                                PostRemoteKeyEntity(
                                    type = PostRemoteKeyEntity.KeyType.AFTER,
                                    id = body.first().id,
                                ),
                                PostRemoteKeyEntity(
                                    type = PostRemoteKeyEntity.KeyType.BEFORE,
                                    id = if(minId==-1L) body.last().id else min(minId,body.last().id)
                                ),
                            )
                        )
                    }
                    LoadType.APPEND -> {
                        postRemoteKeyDao.insert(
                            PostRemoteKeyEntity(
                                type = PostRemoteKeyEntity.KeyType.BEFORE,
                                id = body.last().id,
                            )
                        )
                    }
                    else -> {
                    }
                }
                postDao.insert(body.toEntity())
            }
            return MediatorResult.Success(endOfPaginationReached = body.isEmpty())
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }
}