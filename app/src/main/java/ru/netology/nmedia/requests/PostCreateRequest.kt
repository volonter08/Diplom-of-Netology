package ru.netology.nmedia.requests

import ru.netology.nmedia.dto.Coordinates
import ru.netology.nmedia.dto.Note
import ru.netology.nmedia.dto.UserPreview
import ru.netology.nmedia.model.Attachment

data class PostCreateRequest(
    override val id: Int = 0,
    override val content: String,
    override val coords: Coordinates? = null,
    override val link: String? = null,
    override val attachment: Attachment? = null,
    override val mentionIds: List<Int> = emptyList()
) : NoteCreateRequest(id, content, coords, link, attachment, mentionIds)