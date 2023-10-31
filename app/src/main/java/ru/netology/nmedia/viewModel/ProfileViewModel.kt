package ru.netology.nmedia.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import ru.netology.nmedia.ApiService
import ru.netology.nmedia.ErrorWindow
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.User
import ru.netology.nmedia.model.ProfileRepository
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val auth: AppAuth,
    val profileRepository: ProfileRepository
) : ViewModel() {
    val _dataState = MutableLiveData<ProfileState>()
    val dataAuth = auth.authStateFlow.asLiveData(Dispatchers.Default)
    val dataProfile = MutableLiveData<User>()
    val dataState: LiveData<ProfileState>
        get() {
            return _dataState
        }

    fun initialUserData(id: String) {
        viewModelScope.launch {
            try {
                _dataState.value = ProfileState(true)
                dataProfile.value = profileRepository.getUserData(id).also {

                }
                _dataState.value = ProfileState()
            } catch (e: Exception) {
                ErrorWindow.show(context = context, reason = e.message) {
                    initialUserData(id)
                }
            }
        }
    }
}

data class ProfileState(val loading: Boolean = false)