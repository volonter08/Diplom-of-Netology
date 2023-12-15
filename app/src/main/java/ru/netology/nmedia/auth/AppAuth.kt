package ru.netology.nmedia.auth

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.netology.nmedia.ApiService
import ru.netology.nmedia.ErrorRequest
import ru.netology.nmedia.dao.ProfileDao
import ru.netology.nmedia.dto.Profile
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.ProfileData
import ru.netology.nmedia.entity.ProfileEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAuth @Inject constructor(
    @ApplicationContext private val context: Context,
    private val profileDao: ProfileDao
) {
    val authStateFlow= profileDao.getProfileData().mapLatest {
        if (it.isNotEmpty())
            it.first().toDto()
        else
            Profile()
    }

    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface AppAuthEntryPoint {
        fun apiService(): ApiService
    }
    suspend fun setAuth(id:Int = 0,token:String? = null) {
        println("Auth")
        profileDao.insert(ProfileEntity(ProfileData.PROFILE,id=id,token=token))
    }
    suspend fun updateUserData(id: Int,login:String?,name:String?,avatar:String?){
        profileDao.updateProfile(id,login,name,avatar)
    }
    @Synchronized
    private fun getApiService(context: Context): ApiService {
        val hiltEntryPoint = EntryPointAccessors.fromApplication(
            context,
            AppAuthEntryPoint::class.java
        )
        return hiltEntryPoint.apiService()
    }
}