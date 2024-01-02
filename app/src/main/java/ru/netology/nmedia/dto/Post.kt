package ru.netology.nmedia.dto

import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.model.Attachment

data class Post(
    override val id: Int,
    override val authorId: Int = 0,
    override val author: String = "",
    override val authorAvatar: String = "",
    override val authorJob:String = "",
    override  val content: String,
    override  val published: String,
    override val coords:Coordinates?=null,
    override val link:String = "",
    override val likeOwnerIds:List<Int> = emptyList(),
    val mentionIds:List<Int> = emptyList(),
    val mentionedMe:Boolean = false,
    override val likedByMe: Boolean = false,
    override val attachment: Attachment? = null,
    override val ownedByMe: Boolean = false,
    override val users: Map<String,UserPreview> = emptyMap(),
):java.io.Serializable, Note{

}