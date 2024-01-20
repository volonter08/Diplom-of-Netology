package ru.netology.nmedia.dto

import ru.netology.nmedia.model.Attachment
import ru.netology.nmedia.model.TypeOnline
import java.util.Date

data class Event(
    override val id: Int,
    val authorId: Int,
    val author: String,
    val authorAvatar: String?= null,
    val authorJob: String? = null,
    val content: String,
    val datetime: Date,
    val published: Date,
    val coords: Coordinates? = null,
    val type: TypeOnline,
    val link: String? = null,
    val likeOwnerIds: List<Int> = emptyList(),
    val likedByMe: Boolean = false,
    val speakerIds: List<Int>,
    val participantsIds: List<Int>,
    val participatedByMe:Boolean,
    val attachment: Attachment? = null,
    val ownedByMe: Boolean = false,
    val users: Map<String, UserPreview> = emptyMap()
):Note()