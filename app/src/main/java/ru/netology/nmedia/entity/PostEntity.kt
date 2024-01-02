package ru.netology.nmedia.entity

import androidx.room.Embedded
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.UserPreview

open class PostEntity(
    open val id: Int,
    val authorId: Int,
    val author: String?,
    val authorAvatar: String?,
    val authorJob: String?,
    val content: String?,
    val published: String?,
    open val coords: CoordinatesEmbeddable? = null,
    val link: String?,
    val likeOwnerIds: List<Int>,
    val mentionIds: List<Int>,
    val mentionedMe: Boolean,
    val likedByMe: Boolean,
    open val attachment: AttachmentEmbeddable? = null,
    val ownedByMe: Boolean = false,
    val users: Map<String, UserPreview>
)