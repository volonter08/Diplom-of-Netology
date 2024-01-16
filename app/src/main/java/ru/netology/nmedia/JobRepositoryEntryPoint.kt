package ru.netology.nmedia

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.nmedia.repository.MyJobRepository
import ru.netology.nmedia.repository.UserJobRepositoryFactory

@InstallIn(SingletonComponent::class)
@EntryPoint
interface JobRepositoryEntryPoint {
    fun myJobRepository(): MyJobRepository
    fun userJobRepositoryFactory(): UserJobRepositoryFactory
}