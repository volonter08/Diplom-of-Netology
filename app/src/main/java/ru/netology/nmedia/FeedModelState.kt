package ru.netology.nmedia

data class FeedModelState (
    val loading: Boolean = false,
    val isRefreshed:Boolean = false,
    val error: Boolean = false,
    val errorRetryListener: ru.netology.nmedia.OnRetryListener?= null
)