package ru.netology.nmedia.googleApiModule

import com.google.android.gms.common.GoogleApiAvailability
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
object GoogleApiModule {
    @Provides
    @ActivityScoped
    fun provideGoogleApiAvailable(): GoogleApiAvailability{
        return GoogleApiAvailability()
    }
}