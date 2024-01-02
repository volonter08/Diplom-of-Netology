package ru.netology.nmedia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.UserPreview

@Entity
class AllPostEntity(
    @PrimaryKey(autoGenerate = true)
    override val id: Int,
    authorId: Int,
    author: String?,
    authorAvatar: String?,
    authorJob:String?,
    content: String?,
    published: String?,
    @Embedded
    override val coords: CoordinatesEmbeddable?=null,
    link:String?,
    likeOwnerIds:List<Int>,
    mentionIds:List<Int>,
    mentionedMe:Boolean,
    likedByMe: Boolean,
    @Embedded
    override val attachment: AttachmentEmbeddable? = null,
    ownedByMe: Boolean = false,
    users: Map<String, UserPreview>
):PostEntity(id, authorId, author, authorAvatar, authorJob, content, published, coords, link, likeOwnerIds, mentionIds, mentionedMe, likedByMe, attachment, ownedByMe, users){
    constructor(post:Post):this(post.id, post.authorId, post.author, post.authorAvatar, post.authorJob, post.content, post.published, CoordinatesEmbeddable.fromDto(post.coords), post.link, post.likeOwnerIds, post.mentionIds, post.mentionedMe, post.likedByMe, AttachmentEmbeddable.fromDto(post.attachment), post.ownedByMe, post.users)
    fun toDto() = Post(id, authorId, author?:"", authorAvatar?:"", authorJob?:"", content?:"", published?:"", coords?.toDto(), link?:"", likeOwnerIds, mentionIds, mentionedMe, likedByMe, attachment?.toDto(), ownedByMe, users )
}

fun List<Post>.toAllEntity(): List<AllPostEntity> =  map{
    AllPostEntity(it)
}