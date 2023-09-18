package com.example.netologyandroidhomework1.application

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NMediaApplication:Application() {
    override fun onCreate() {
        super.onCreate()
    }
}