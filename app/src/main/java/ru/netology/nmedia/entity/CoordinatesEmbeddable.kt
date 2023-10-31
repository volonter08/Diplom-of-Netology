package ru.netology.nmedia.entity

import ru.netology.nmedia.dto.Coordinates
import ru.netology.nmedia.model.Attachment

data class CoordinatesEmbeddable (
    val lat:String?,
    val longl:String?
){
    fun toDto() = Coordinates(lat,longl)

    companion object {
        fun fromDto(dto: Coordinates?) = dto?.run {
            CoordinatesEmbeddable(lat,longl)
        }
    }
}