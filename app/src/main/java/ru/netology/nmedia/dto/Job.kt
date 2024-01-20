package ru.netology.nmedia.dto

import java.util.Date

data class Job(
    override val id: Int,
    val name: String,
    val position: String,
    val start: Date,
    val finish: Date?,
    val link: String?,
    val ownedMe:Boolean = false
):Note()