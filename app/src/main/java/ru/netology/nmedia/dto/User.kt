package ru.netology.nmedia.dto

import java.io.Serializable

data class User(
    val id:Int,
    val login:String?,
    val name:String?,
    val avatar:String?
):Serializable