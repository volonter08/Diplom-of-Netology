package ru.netology.nmedia.model

import ru.netology.nmedia.OnRetryListener

interface ErrorCallback {
    fun onError(reason:String?,onRetryListener: OnRetryListener)
}