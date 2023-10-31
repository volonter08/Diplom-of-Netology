package ru.netology.nmedia.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.launch
import ru.netology.nmedia.ApiService
import ru.netology.nmedia.dao.ProfileDao
import ru.netology.nmedia.dto.Profile
import ru.netology.nmedia.dto.User
import ru.netology.nmedia.entity.ProfileEntity
import ru.netology.nmedia.exceptions.UserResponseException
import ru.netology.nmedia.responses.ErrorResponse
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class ProfileRepository @Inject constructor(val apiService: ApiService,private val profileDao: ProfileDao){


    suspend fun getUserData(id: String):User? {
        val response = apiService.getUserById(id)
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