package com.example.netologyandroidhomework1.fireBaseModule

import com.google.firebase.messaging.FirebaseMessaging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
object FireBaseModule {
    @Provides
    @ActivityScoped
    fun provideFireBaseMessaging():FirebaseMessaging{
        return FirebaseMessaging.getInstance()
    }
}