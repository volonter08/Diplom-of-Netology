package ru.netology.nmedia.entryPoints

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.nmedia.repository.AllPostsRepository
import ru.netology.nmedia.repository.MyPostsRepository
import ru.netology.nmedia.repository.UserPostRepositoryFactory

@InstallIn(SingletonComponent::class)
@EntryPoint
interface PostRepositoryEntryPoint {
    fun allPostRepository():AllPostsRepository
    fun myPostRepository():MyPostsRepository
    fun userPostRepositoryFactory():UserPostRepositoryFactory
}