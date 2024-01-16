package ru.netology.nmedia.requests

import ru.netology.nmedia.dto.Coordinates
import ru.netology.nmedia.model.Attachment
import ru.netology.nmedia.model.TypeOnline
import java.util.Date

abstract class NoteCreateRequest(
    open val id: Int = 0,
    open val content: String,
    open val coords: Coordinates? = null,
    open val link: String? = null,
    open val attachment: Attachment? = null,
    open val mentionIds: List<Int> = emptyList()
)