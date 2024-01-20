package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Note
import ru.netology.nmedia.requests.NoteCreateRequest

interface  Repository<T : Note, V:NoteCreateRequest> {
    suspend fun like(liked: T, token: String?)
    suspend fun dislike(disliked: T, token: String?)
    suspend fun remove(removed: T, token: String?)
    suspend fun save(createRequest: V, token: String?)
}