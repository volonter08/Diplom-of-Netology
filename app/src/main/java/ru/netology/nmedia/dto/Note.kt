package ru.netology.nmedia.dto

import ru.netology.nmedia.model.Attachment

interface Note {
    val id: Int
    val authorId: Int
    val author: String
    val authorAvatar: String?
    val authorJob: String?
    val content: String
    val published: String
    val coords: Coordinates?
    val link: String?
    val likeOwnerIds: List<Int>
    val likedByMe: Boolean
    val attachment: Attachment?
    val ownedByMe: Boolean
    val users: Map<String, UserPreview>
}