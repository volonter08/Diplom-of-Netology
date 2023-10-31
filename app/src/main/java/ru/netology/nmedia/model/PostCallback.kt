package ru.netology.nmedia.model

import ru.netology.nmedia.OnRetryListener

interface PostCallback {
    fun onError(onRetryListener: OnRetryListener)
}