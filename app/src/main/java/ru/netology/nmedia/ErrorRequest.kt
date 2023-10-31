package ru.netology.nmedia

data class ErrorRequest (
    val reason:String? = null,val onRetry:()->Unit)