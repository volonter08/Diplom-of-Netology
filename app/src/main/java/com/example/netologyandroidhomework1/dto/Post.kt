package com.example.netologyandroidhomework1.dto

import com.example.netologyandroidhomework1.model.Attachment

data class Post(
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: Long,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val attachment: Attachment? = null,
    val ownedByMe: Boolean = false,

):java.io.Serializable