package ru.netology.nmedia.dto

import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.model.Attachment
import java.io.Serializable

data class Post(
    override val id: Int,
    override val authorId: Int,
    override val author: String,
    override val authorAvatar: String? = null,
    override val authorJob:String? = null,
    override val content: String,
    override val published: String,
    override val coords:Coordinates?=null,
    override val link:String? = null,
    override val likeOwnerIds:List<Int> = emptyList(),
    val mentionIds:List<Int> = emptyList(),
    val mentionedMe:Boolean = false,
    override val likedByMe: Boolean = false,
    override val attachment: Attachment? = null,
    override val ownedByMe: Boolean = false,
    override val users: Map<String,UserPreview> = emptyMap(),
): Serializable,Note