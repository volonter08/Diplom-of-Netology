package ru.netology.nmedia.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.FormBody
import ru.netology.nmedia.AuthApiService
import ru.netology.nmedia.FeedModelState
import ru.netology.nmedia.OnRetryListener
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.User
import ru.netology.nmedia.exceptions.AuthException
import ru.netology.nmedia.requests.AuthenticationRequest
import ru.netology.nmedia.responses.Error
import ru.netology.nmedia.responses.ErrorResponse
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: AppAuth,
    private val apiService: AuthApiService
) : ViewModel() {
    val gson = Gson()
    private val _dataState = MutableLiveData(FeedModelState())

    val dataState:LiveData<FeedModelState>
        get() {
            return _dataState
        }
    fun signUp(login: String, password: String, name: String) {
        viewModelScope.launch {
            try {
                _dataState.value = FeedModelState(true)
                val formBody = FormBody.Builder().add("login",login).add("password",password).add("name",name).build()
                val response = apiService.signUp(formBody)
                _dataState.value = FeedModelState()
                if (!response.isSuccessful) {
                    val error: ErrorResponse? =
                        gson.fromJson(
                            response.errorBody()!!.charStream(),
                            ErrorResponse::class.java
                        )
                    throw AuthException(error?.reason)
                } else
                    response.body()?.let {
                        auth.setAuth(it.id, it.token)
                    }
            }
            catch (e:Exception) {
                onError(e.message) {
                    signIn(login, password)
                }
            }
        }
    }
    fun signIn(login:String,password: String){
        viewModelScope.launch {
            try {
                _dataState.value = FeedModelState(true)
                val response = apiService.signIn(AuthenticationRequest(login,password))
                _dataState.value = FeedModelState()
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
            catch (e:Exception){
                onError(e.message){
                    signIn(login,password)
                }
            }
        }
    }
    fun updateUserData(user: User) {
        viewModelScope.launch {
            try {
                _dataState.value = FeedModelState(true)
                auth.updateUserData(user.id, user.login, user.name, user.avatar)
                _dataState.value = FeedModelState()
            } catch (e: java.lang.Exception) {
                onError(e.message) {
                    updateUserData(user)
                }
            }
        }
    }
    fun exit(onSuccessfulExit: () -> Unit) {
        viewModelScope.launch {
            try {
                _dataState.value = FeedModelState(true)
                auth.setAuth()
                _dataState.value = FeedModelState()
            } catch (e: java.lang.Exception) {
                onError(e.message) {
                    exit(onSuccessfulExit)
                }
            }
        }

    }
    private fun onError(reason:String?,onRetryListener: OnRetryListener){
        _dataState.value = FeedModelState(error= Error(reason,onRetryListener))
    }
}