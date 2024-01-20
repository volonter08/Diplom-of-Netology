package ru.netology.nmedia.requests

import ru.netology.nmedia.dto.Coordinates
import ru.netology.nmedia.dto.Note
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.UserPreview
import ru.netology.nmedia.model.Attachment

data class PostCreateRequest(
    override val id: Int = 0,
    val content: String,
    val coords: Coordinates? = null,
    val link: String? = null,
    val attachment: Attachment? = null,
    val mentionIds: List<Int> = emptyList()
) : NoteCreateRequest(){
    constructor(post: Post):this(post.id,post.content,post.coords,post.link,post.attachment,post.mentionIds)
}