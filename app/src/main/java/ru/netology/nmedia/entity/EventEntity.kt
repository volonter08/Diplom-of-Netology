package ru.netology.nmedia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Event
import ru.netology.nmedia.dto.UserPreview
import ru.netology.nmedia.model.TypeOnline
import java.util.Date

@Entity
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val authorId: Int,
    val author: String?,
    val authorAvatar: String?,
    val authorJob:String?,
    val content: String?,
    val datetime: Date,
    val published: Date,
    @Embedded
    val coords: CoordinatesEmbeddable?=null,
    val typev: TypeOnline,
    val link:String?,
    val likeOwnerIds:List<Int>,
    val likedByMe: Boolean,
    val speakerIds: List<Int>,
    val participantsIds: List<Int>,
    val participatedByMe:Boolean,
    @Embedded
    val attachment: AttachmentEmbeddable? = null,
    val ownedByMe: Boolean = false,
    val users: Map<String, UserPreview>
){
    fun toDto() = Event(id, authorId, author?:"", authorAvatar?:"", authorJob?:"", content?:"", datetime ,published,coords?.toDto(),
        typev, link?:"",likeOwnerIds, likedByMe,speakerIds,participantsIds,participatedByMe, attachment?.toDto(), ownedByMe, users )

    companion object {
        fun fromDto(dto: Event) =dto.run{
            EventEntity(id, authorId, author, authorAvatar, authorJob, content, datetime, published =  published, CoordinatesEmbeddable.fromDto(coords),type, link, likeOwnerIds,likedByMe,speakerIds,participantsIds,participatedByMe, AttachmentEmbeddable.fromDto(attachment), ownedByMe, users)
        }

    }
}

fun List<EventEntity>.toDto(): List<Event> = map(EventEntity::toDto)
fun List<Event>.toEntity(): List<EventEntity> = map(EventEntity::fromDto)
