package ru.netology.nmedia.requests

import ru.netology.nmedia.dto.Coordinates
import ru.netology.nmedia.model.Attachment
import ru.netology.nmedia.model.TypeOnline
import java.util.Date

data class EventCreateRequest(
    override val id: Int = 0,
    override val content: String,
    val dateTime:Date,
    override val coords: Coordinates? = null,
    val type:TypeOnline,
    override val link: String? = null,
    override val attachment: Attachment? = null,
    override val mentionIds: List<Int> = emptyList()
):NoteCreateRequest(id, content, coords, link, attachment, mentionIds)