package ru.netology.nmedia

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import ru.netology.nmedia.repository.AllPostsRepository
import ru.netology.nmedia.repository.MyPostsRepository
import ru.netology.nmedia.repository.UserPostRepositoryFactory
import ru.netology.nmedia.repository.UserPostsRepository

@InstallIn(SingletonComponent::class)
@EntryPoint
interface PostRepositoryEntryPoint {
    fun allPostRepository():AllPostsRepository
    fun myPostRepository():MyPostsRepository
}