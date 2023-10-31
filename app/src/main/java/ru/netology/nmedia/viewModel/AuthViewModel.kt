package ru.netology.nmedia.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import ru.netology.nmedia.auth.AppAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.nmedia.ApiService
import ru.netology.nmedia.dto.Profile
import ru.netology.nmedia.exceptions.AuthException
import ru.netology.nmedia.requests.AuthenticationRequest
import ru.netology.nmedia.responses.ErrorResponse
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: AppAuth,
    private val apiService: ApiService
) : ViewModel() {
    val gson = Gson()


    val data: LiveData<Profile> = auth.authStateFlow
        .asLiveData(Dispatchers.Default)
    val authenticated: Boolean
        get() = auth.authStateFlow.value.id != 0

    fun signUp(login: String, password: String, name: String) {
        viewModelScope.launch {
            val response = apiService.signUp(login, password, name)
            if (!response.isSuccessful) {
                val error: ErrorResponse? =
                    gson.fromJson(response.errorBody()!!.charStream(), ErrorResponse::class.java)
                throw AuthException(error?.reason)
            } else
                response.body()?.let {
                    auth.setAuth(it.id,it.token)
                }
        }
    }
    fun signIn(login:String,password: String){
        viewModelScope.launch {
            try {
                val response = apiService.signIn(AuthenticationRequest(login,password))
                if (!response.isSuccessful) {
                    val error: ErrorResponse? =
                        gson.fromJson(
                            response.errorBody()!!.charStream(),
                            ErrorResponse::class.java
                        )
                    throw AuthException(error?.reason)
                } else
                    response.body()?.let {
                        auth.setAuth(it.id,it.token)
                    }
            }
            catch (_:Exception){

            }
        }
    }
}