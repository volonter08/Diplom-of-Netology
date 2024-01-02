package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

open class PostRemoteKeyEntity(
    open val type: KeyType,
    val id: Int,
)