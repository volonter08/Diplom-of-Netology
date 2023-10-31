package ru.netology.nmedia.model

interface  Repository<T> {
    suspend fun getAll()
}