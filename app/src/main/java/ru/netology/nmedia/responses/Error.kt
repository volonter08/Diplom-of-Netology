package ru.netology.nmedia.responses

import ru.netology.nmedia.OnRetryListener

data class ErrorResponse(val reason: String?)
data class Error(val reason: String?,val onRetryListener: OnRetryListener)