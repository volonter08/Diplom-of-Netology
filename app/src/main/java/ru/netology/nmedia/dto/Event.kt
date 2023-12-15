package ru.netology.nmedia.dto

import ru.netology.nmedia.model.Attachment
import ru.netology.nmedia.model.TypeOnline

data class Event(
    override val id: Int,
    override val authorId: Int = 0,
    override val author: String = "",
    override val authorAvatar: String = "",
    override val authorJob: String = "",
    override val content: String,
    val datetime: String,
    override val published: String,
    override val coords: Coordinates? = null,
    val type: TypeOnline,
    override val link: String = "",
    override val likeOwnerIds: List<Int> = emptyList(),
    override val likedByMe: Boolean = false,
    val speakerIds: List<Int>,
    val participantsIds: List<Int>,
    val participatedByMe:Boolean,
    override val attachment: Attachment? = null,
    override val ownedByMe: Boolean = false,
    override val users: Map<String, UserPreview> = emptyMap()
):Note