package ru.netology.nmedia.fireBaseModule

import com.google.firebase.messaging.FirebaseMessaging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
object FireBaseModule {
    @Provides
    @ActivityScoped
    fun provideFireBaseMessaging():FirebaseMessaging{
        return FirebaseMessaging.getInstance()
    }
}