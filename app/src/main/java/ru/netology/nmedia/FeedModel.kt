package ru.netology.nmedia

import ru.netology.nmedia.dto.Post
data class FeedModel(
    val posts: List<Post> = emptyList(),
)