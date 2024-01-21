package ru.netology.nmedia.auth

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import ru.netology.nmedia.dao.ProfileDao
import ru.netology.nmedia.dto.Profile
import ru.netology.nmedia.entity.ProfileData
import ru.netology.nmedia.entity.ProfileEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAuth @Inject constructor(
    private val profileDao: ProfileDao
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    val authStateFlow= profileDao.getProfileData().mapLatest {
        if (it.isNotEmpty()) {
            it.first().toDto()
        }
        else
            Profile()
    }.stateIn(MainScope(), SharingStarted.Eagerly, Profile())
    suspend fun setAuth(id:Int = 0,token:String? = null) {
        profileDao.insert(ProfileEntity(ProfileData.PROFILE,id=id,token=token))
    }
    suspend fun updateUserData(id: Int,login:String?,name:String?,avatar:String?){
        profileDao.updateProfile(id,login,name,avatar)
    }
}