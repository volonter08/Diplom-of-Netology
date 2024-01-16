package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Note
import ru.netology.nmedia.requests.NoteCreateRequest

interface  Repository<T : Note> {
    suspend fun like(liked: T, token: String?)
    suspend fun dislike(disliked: T, token: String?)
    suspend fun remove(removed: T, token: String?)
    suspend fun save(saved: T, token: String?)
}