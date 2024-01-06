package ru.netology.nmedia.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Post

interface  Repository<T : Any> {
    val data: Flow<PagingData<T>>
    suspend fun like(likedPost: T, token: String?)
    suspend fun dislike(dislikedPost: T, token: String?)
    suspend fun remove(removedPost: T, token: String?)
    suspend fun createPost(content: String,link: String?,token: String?)
    suspend fun update(post: Post,token: String?)
}