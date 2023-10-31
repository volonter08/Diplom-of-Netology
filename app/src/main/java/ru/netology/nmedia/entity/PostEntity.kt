package ru.netology.nmedia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import ru.netology.nmedia.converters.ListConverter
import ru.netology.nmedia.converters.MapConverter
import ru.netology.nmedia.dto.Coordinates
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.UserPreview
import ru.netology.nmedia.model.Attachment

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val authorId: Int,
    val author: String?,
    val authorAvatar: String?,
    val authorJob:String?,
    val content: String?,
    val published: String?,
    @Embedded
    val coords: CoordinatesEmbeddable?=null,
    val link:String?,
    val likeOwnerIds:List<Int>,
    val mentionIds:List<Int>,
    val mentionedMe:Boolean,
    val likedByMe: Boolean,
    @Embedded
    val attachment: AttachmentEmbeddable? = null,
    val ownedByMe: Boolean = false,
    val users: Map<String, UserPreview>
) {
    fun toDto() = Post(id, authorId, author?:"", authorAvatar?:"", authorJob?:"", content?:"", published?:"", coords?.toDto(), link?:"", likeOwnerIds, mentionIds, mentionedMe, likedByMe, attachment?.toDto(), ownedByMe, users )

    companion object {
        fun fromDto(dto: Post) =dto.run{
            PostEntity(id, authorId, author, authorAvatar, authorJob, content, published, CoordinatesEmbeddable.fromDto(coords), link, likeOwnerIds, mentionIds, mentionedMe, likedByMe, AttachmentEmbeddable.fromDto(attachment), ownedByMe, users)
        }

    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)
