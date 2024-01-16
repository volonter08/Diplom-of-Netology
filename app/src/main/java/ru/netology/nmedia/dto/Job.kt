package ru.netology.nmedia.dto

data class Job(
    override val id: Int,
    val name: String,
    val position: String,
    val start: String,
    val finish: String?,
    val link: String?,
    val ownedMe:Boolean = false
):Note