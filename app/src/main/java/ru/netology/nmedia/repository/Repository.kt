package ru.netology.nmedia.repository

interface  Repository<T> {
    suspend fun getAll()
}