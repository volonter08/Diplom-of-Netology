package ru.netology.nmedia.entity


import ru.netology.nmedia.dto.UserPreview
import java.util.Date

abstract class PostEntity(
    open val id: Int,
    val authorId: Int,
    val author: String?,
    val authorAvatar: String?,
    val authorJob: String?,
    val content: String?,
    val published: Date?,
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