package ru.netology.nmedia.auth

import android.content.Context
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import ru.netology.nmedia.AuthApiService
import ru.netology.nmedia.dao.ProfileDao
import ru.netology.nmedia.dto.Profile
import ru.netology.nmedia.entity.ProfileData
import ru.netology.nmedia.entity.ProfileEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAuth @Inject constructor(
    @ApplicationContext private val context: Context,
    private val profileDao: ProfileDao
) {
    val authFlow= profileDao.getProfileData().mapLatest {
        if (it.isNotEmpty()) {
            it.first().toDto()
        }
        else
            Profile()
    }
    val authStateFlow = authFlow.stateIn(MainScope(), SharingStarted.Eagerly, Profile())
    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface AppAuthEntryPoint {
        fun authApiService(): AuthApiService
    }
    suspend fun setAuth(id:Int = 0,token:String? = null) {
        profileDao.insert(ProfileEntity(ProfileData.PROFILE,id=id,token=token))
    }
    suspend fun updateUserData(id: Int,login:String?,name:String?,avatar:String?){
        profileDao.updateProfile(id,login,name,avatar)
    }
    @Synchronized
    private fun getApiService(context: Context): AuthApiService {
        val hiltEntryPoint = EntryPointAccessors.fromApplication(
            context,
            AppAuthEntryPoint::class.java
        )
        return hiltEntryPoint.authApiService()
    }
}