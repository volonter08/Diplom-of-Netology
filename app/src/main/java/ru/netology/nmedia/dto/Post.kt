package ru.netology.nmedia.dto

import ru.netology.nmedia.model.Attachment

data class Post(
    val id: Int,
    val authorId: Int = 0,
    val author: String = "",
    val authorAvatar: String = "",
    val authorJob:String = "",
    val content: String,
    val published: String,
    val coords:Coordinates?=null,
    val link:String = "",
    val likeOwnerIds:List<Int> = emptyList(),
    val mentionIds:List<Int> = emptyList(),
    val mentionedMe:Boolean = false,
    val likedByMe: Boolean = false,
    val attachment: Attachment? = null,
    val ownedByMe: Boolean = false,
    val users: Map<String,UserPreview> = emptyMap(),
):java.io.Serializable