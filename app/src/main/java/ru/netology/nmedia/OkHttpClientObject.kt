package ru.netology.nmedia

import okhttp3.OkHttpClient

object OkHttpClientObject {
    private val client = OkHttpClient.Builder().build()
        fun get(): OkHttpClient {
            return client
        }
}