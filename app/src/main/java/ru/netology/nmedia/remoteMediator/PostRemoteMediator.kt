package ru.netology.nmedia.remoteMediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import ru.netology.nmedia.PostApiService
import ru.netology.nmedia.dao.postDao.PostDao
import ru.netology.nmedia.dao.postRemoteKeyDao.PostRemoteKeyDao
import ru.netology.nmedia.dao.ProfileDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.entity.AllPostEntity
import ru.netology.nmedia.entity.AllPostRemoteKeyEntity
import ru.netology.nmedia.entity.KeyType
import ru.netology.nmedia.entity.MyPostEntity
import ru.netology.nmedia.entity.MyPostRemoteKeyEntity
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.PostRemoteKeyEntity
import ru.netology.nmedia.entity.toAllEntity
import ru.netology.nmedia.entity.toMyEntity
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator<T : PostEntity, V : PostRemoteKeyEntity> @Inject constructor(
    private val t: Class<T>,
    private val v: Class<V>,
    private val service: PostApiService,
    private val db: AppDb,
    private val postDao: PostDao<T>,
    private val postRemoteKeyDao: PostRemoteKeyDao<V>,
    private val profileDao: ProfileDao
) : RemoteMediator<Int, T>() {

    companion object {
        inline operator fun <reified T : PostEntity, reified V : PostRemoteKeyEntity> invoke(
            service: PostApiService,
            db: AppDb,
            postDao: PostDao<T>, postRemoteKeyDao: PostRemoteKeyDao<V>,
            profileDao: ProfileDao
        ) = PostRemoteMediator(
            T::class.java,
            V::class.java,
            service,
            db,
            postDao,
            postRemoteKeyDao,
            profileDao
        )
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, T>
    ): MediatorResult {
        try {
            if(t.isAssignableFrom(MyPostEntity::class.java))
                println("")
            val response = when (loadType) {
                LoadType.REFRESH -> {
                    service.getLatestPosts(
                        state.config.initialLoadSize,
                        profileDao.getAccessToken()
                    )
                }

                LoadType.PREPEND -> {
                    val id = postRemoteKeyDao.max() ?: return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                    service.getAfterPost(id, state.config.pageSize, profileDao.getAccessToken())
                }

                LoadType.APPEND -> {
                    val id = postRemoteKeyDao.min() ?: return MediatorResult.Success(
                        endOfPaginationReached = false
                    )
                    service.getBeforePost(id, state.config.pageSize, profileDao.getAccessToken())
                }
            }
            if (!response.isSuccessful) {
                throw Exception()
            }
            val body = (response.body() ?: throw Exception())
            if(t.isAssignableFrom(MyPostEntity::class.java))
                println("")
            if (body.isNotEmpty()) {
                db.withTransaction {
                    when (loadType) {
                        LoadType.REFRESH -> {
                            postRemoteKeyDao.insert(
                                when {
                                    v.isAssignableFrom(AllPostRemoteKeyEntity::class.java) -> {
                                        listOf(
                                            AllPostRemoteKeyEntity(
                                                type = KeyType.AFTER,
                                                id = body.first().id,
                                            ),
                                            AllPostRemoteKeyEntity(
                                                type = KeyType.BEFORE,
                                                body.last().id
                                            )
                                        ) as List<V>
                                    }

                                    v.isAssignableFrom(MyPostRemoteKeyEntity::class.java) -> {

                                        listOf(
                                            MyPostRemoteKeyEntity(
                                                type = KeyType.AFTER,
                                                id = body.first().id,
                                            ),
                                            MyPostRemoteKeyEntity(
                                                type = KeyType.BEFORE,
                                                body.last().id
                                            )
                                        ) as List<V>

                                    }

                                    else -> {
                                        emptyList()
                                    }
                                }
                            )
                            postDao.removeAll()
                        }

                        LoadType.APPEND -> {
                            postRemoteKeyDao.insert(
                                when{
                                    v.isAssignableFrom(AllPostRemoteKeyEntity::class.java)->{
                                        AllPostRemoteKeyEntity(
                                            type = KeyType.BEFORE,
                                            id = body.last().id
                                        ) as V
                                    }
                                    v.isAssignableFrom(MyPostRemoteKeyEntity::class.java)->{
                                        MyPostRemoteKeyEntity(
                                            type = KeyType.BEFORE,
                                            id = body.last().id
                                        ) as V
                                    }
                                    else -> {
                                        MyPostRemoteKeyEntity(
                                            type = KeyType.BEFORE,
                                            id = body.last().id
                                        ) as V
                                    }
                                }

                            )
                        }

                        LoadType.PREPEND -> {
                            postRemoteKeyDao.insert(
                                when{
                                    v.isAssignableFrom(AllPostRemoteKeyEntity::class.java)->{
                                        AllPostRemoteKeyEntity(
                                            type = KeyType.AFTER,
                                            id = body.first().id
                                        ) as V
                                    }
                                    v.isAssignableFrom(MyPostRemoteKeyEntity::class.java)->{
                                        MyPostRemoteKeyEntity(
                                            type = KeyType.AFTER,
                                            id = body.first().id
                                        ) as V
                                    }
                                    else -> {
                                        MyPostRemoteKeyEntity(
                                            type = KeyType.AFTER,
                                            id = body.first().id
                                        ) as V
                                    }
                                }

                            )
                        }
                    }
                    when {
                        t.isAssignableFrom(MyPostEntity::class.java) ->
                            postDao.insert(body.toMyEntity() as List<T>)

                        t.isAssignableFrom(AllPostEntity::class.java) ->
                            postDao.insert(body.toAllEntity() as List<T>)
                    }
                }
                return MediatorResult.Success(endOfPaginationReached = false)
            }
            else{
                return MediatorResult.Success(endOfPaginationReached = true)
            }
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }
}