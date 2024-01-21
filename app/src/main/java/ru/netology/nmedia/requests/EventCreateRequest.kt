package ru.netology.nmedia.requests

import android.icu.text.SimpleDateFormat
import ru.netology.nmedia.dto.Coordinates
import ru.netology.nmedia.dto.Event
import ru.netology.nmedia.model.Attachment
import ru.netology.nmedia.model.TypeOnline

data class EventCreateRequest(
    override val id: Int = 0,
    val content: String,
    val datetime:String,
    val coords: Coordinates? = null,
    val type:TypeOnline = TypeOnline.ONLINE,
    val link: String? = null,
    val attachment: Attachment? = null,
    val speakerIds: List<Int> = emptyList()
):NoteCreateRequest(){
    constructor(event: Event):this(event.id,event.content,
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(event.datetime),event.coords,event.type,event.link,event.attachment,event.speakerIds)
}