package ru.netology.nmedia.dto

import ru.netology.nmedia.model.Attachment
import java.util.Date

data class Post(
    override val id: Int = 0,
    val authorId: Int = 0,
    val author: String = "",
    val authorAvatar: String? = null,
    val authorJob:String? = null,
    val content: String,
    val published: Date?=null,
    val coords:Coordinates?=null,
    val link:String? = null,
    val likeOwnerIds:List<Int> = emptyList(),
    val mentionIds:List<Int> = emptyList(),
    val mentionedMe:Boolean = false,
    val likedByMe: Boolean = false,
    val attachment: Attachment? = null,
    val ownedByMe: Boolean = false,
    val users: Map<String,UserPreview> = emptyMap(),
): Note()