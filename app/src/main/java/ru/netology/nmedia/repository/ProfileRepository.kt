package ru.netology.nmedia.repository

import com.google.gson.Gson
import ru.netology.nmedia.AuthApiService
import ru.netology.nmedia.dao.ProfileDao
import ru.netology.nmedia.dto.Profile
import ru.netology.nmedia.dto.User
import ru.netology.nmedia.entity.ProfileEntity
import ru.netology.nmedia.exceptions.UserResponseException
import ru.netology.nmedia.responses.ErrorResponse
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class ProfileRepository @Inject constructor(val authApiService: AuthApiService, private val profileDao: ProfileDao){
    suspend fun getUserData(id: String):User? {
        val response = authApiService.getUserById(id)
        if (!response.isSuccessful) {
            val error: ErrorResponse? =
                Gson().fromJson(response.errorBody()!!.charStream(), ErrorResponse::class.java)
            throw UserResponseException(error?.reason)
        } else
            return response.body()?.let {
                it
            }
    }
    suspend fun setUserData(profile: Profile){
        profileDao.insert(ProfileEntity.fromDto(profile))
    }
}