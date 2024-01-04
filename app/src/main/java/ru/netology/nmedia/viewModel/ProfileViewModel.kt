package ru.netology.nmedia.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.FeedModelState
import ru.netology.nmedia.OnRetryListener
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.User
import ru.netology.nmedia.repository.ProfileRepository
import ru.netology.nmedia.responses.Error
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth: AppAuth,
    val profileRepository: ProfileRepository,
) : ViewModel() {
    private val _dataState = MutableLiveData(FeedModelState())
    private val _dataProfile = MutableLiveData<User>()
    val dataState: LiveData<FeedModelState>
        get() {
            return _dataState
        }
    val dataProfile: LiveData<User>
        get() {
            return _dataProfile
        }

    fun initUserData(id: String) {
        viewModelScope.launch {
            try {
                _dataState.value = FeedModelState(true)
                _dataProfile.value = profileRepository.getUserData(id)
                _dataState.value = FeedModelState()
            } catch (e: Exception) {
                onError(e.message) {
                    initUserData(id)
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
            } catch (e: Exception) {
                onError(e.message) {
                    exit(onSuccessfulExit)
                }
            }
        }

    }

    private fun onError(reason: String?, onRetryListener: OnRetryListener) {
        _dataState.value = FeedModelState(error = Error(reason, onRetryListener))
    }
}

data class ProfileState(val loading: Boolean = false, val error: Error? = null)