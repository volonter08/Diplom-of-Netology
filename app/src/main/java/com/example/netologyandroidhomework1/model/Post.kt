package com.example.netologyandroidhomework1.model

data class Post(
    val id: Long = 0,
    val authorId:Long = 1,
    val content: String,
    val published: String = "",
    val likedByMe: Boolean = false,
    val likes: Int = 0,
    val attachment:Attachment? = null
):java.io.Serializable