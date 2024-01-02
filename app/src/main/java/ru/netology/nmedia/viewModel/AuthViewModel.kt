package ru.netology.nmedia.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import ru.netology.nmedia.auth.AppAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.FormBody
import ru.netology.nmedia.AuthApiService
import ru.netology.nmedia.FeedModelState
import ru.netology.nmedia.OnRetryListener
import ru.netology.nmedia.exceptions.AuthException
import ru.netology.nmedia.requests.AuthenticationRequest
import ru.netology.nmedia.responses.Error
import ru.netology.nmedia.responses.ErrorResponse
import javax.inject.Inject
import kotlin.Exception

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: AppAuth,
    private val apiService: AuthApiService
) : ViewModel() {
    val gson = Gson()
    val _dataState = MutableLiveData(FeedModelState())

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
    private fun onError(reason:String?,onRetryListener: OnRetryListener){
        _dataState.value = FeedModelState(error= Error(reason,onRetryListener))
    }
}