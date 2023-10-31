package ru.netology.nmedia.entity

import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.model.Attachment

data class AttachmentEmbeddable(
    var url: String?,
    var type: AttachmentType
) {
    fun toDto() = Attachment(url?:"", type)

    companion object {
        fun fromDto(dto: Attachment?) = dto?.run {
            AttachmentEmbeddable(url,type)
        }
    }
}