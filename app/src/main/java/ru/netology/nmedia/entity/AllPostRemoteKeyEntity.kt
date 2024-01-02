package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class AllPostRemoteKeyEntity(
    @PrimaryKey
    override val type: KeyType,
    id: Int

) : PostRemoteKeyEntity(type,id)