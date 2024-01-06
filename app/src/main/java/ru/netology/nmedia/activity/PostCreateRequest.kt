package ru.netology.nmedia.activity

import ru.netology.nmedia.dto.Coordinates
import ru.netology.nmedia.dto.UserPreview
import ru.netology.nmedia.model.Attachment

data class PostCreateRequest (
    val id: Int,
    val content: String,
    val coords: Coordinates?=null,
    val link:String? = null,
    val attachment: Attachment? = null,
    val mentionIds:List<Int> = emptyList()
)