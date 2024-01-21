package ru.netology.nmedia.entity

import ru.netology.nmedia.dto.Coordinates

data class CoordinatesEmbeddable (
    val lat:String?,
    val longv:String?
){
    fun toDto() = Coordinates(lat,longv)

    companion object {
        fun fromDto(dto: Coordinates?) = dto?.run {
            CoordinatesEmbeddable(lat,long)
        }
    }
}