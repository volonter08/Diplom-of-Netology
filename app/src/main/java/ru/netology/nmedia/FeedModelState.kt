package ru.netology.nmedia

import ru.netology.nmedia.responses.Error

data class FeedModelState (
    val loading: Boolean = false,
    val isRefreshed:Boolean = false,
    val isSaved:Boolean  = false,
    val error: Error?=null
)