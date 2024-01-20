package ru.netology.nmedia.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import ru.netology.nmedia.OnRetryListener
import ru.netology.nmedia.auth.AppAuth
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import ru.netology.nmedia.FeedModelState
import ru.netology.nmedia.dao.ProfileDao
import ru.netology.nmedia.dto.Event
import ru.netology.nmedia.repository.EventRepository
import ru.netology.nmedia.requests.EventCreateRequest
import ru.netology.nmedia.requests.PostCreateRequest
import ru.netology.nmedia.responses.Error
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val repository: EventRepository,
    val auth: AppAuth,
    val profileDao: ProfileDao
) : ViewModel() {
    private val cached = repository
        .data
        .cachedIn(viewModelScope)

    var tokenAccess:String? = null
    val data: Flow<PagingData<Event>> = auth.authStateFlow
        .flatMapLatest { (myId, token, _, _, _) ->
            tokenAccess= token
            cached.map { pagingData ->
                pagingData.map { post ->
                    post.copy(ownedByMe = post.authorId == myId,likedByMe = post.likeOwnerIds.contains(myId))
                }
            }
        }
    val _dataState: MutableLiveData<ru.netology.nmedia.FeedModelState> = MutableLiveData()
    val dataState: LiveData<ru.netology.nmedia.FeedModelState>
        get() = _dataState

    fun like(likedEvent: Event) {
        viewModelScope.launch {
            try {
                repository.like(likedEvent,tokenAccess)
                _dataState.setValue(FeedModelState())
            } catch (e: Exception) {
                onError(e.message?:"") {
                    like(likedEvent)
                }
            }
        }
    }

    fun dislike(disLikedEvent: Event) {
        viewModelScope.launch {
            try {
                repository.dislike(disliked = disLikedEvent, tokenAccess )
                _dataState.setValue(FeedModelState())
            } catch (e: Exception) {
                onError(e.message?:"") {
                    dislike(disLikedEvent)
                }
            }
        }
    }
    fun remove(event: Event) {
        viewModelScope.launch {
            try {
                repository.remove(event, tokenAccess)
            } catch (_: Exception) {
                onError("Request is not succesfull") {
                    remove(event)
                }
            }
        }
    }
    fun saveEvent(eventCreateRequest: EventCreateRequest) {
        viewModelScope.launch {
            try {
                _dataState.value = FeedModelState(loading = true)
                repository.save(eventCreateRequest,tokenAccess)
                _dataState.value = FeedModelState(isSaved = true)
            } catch (e: Exception) {
                onError(e.message?:"") {
                    saveEvent(eventCreateRequest)
                }
            }
        }
    }
    fun participate(eventId:Int){
        viewModelScope.launch {
            try {
                repository.participate(eventId,profileDao.getAccessToken())
            } catch (e: Exception) {
                onError(e.message?:"") {
                    participate(eventId)
                }
            }
        }
    }
    fun unparticipate(eventId:Int){
        viewModelScope.launch {
            try {
                repository.unparticipate(eventId,profileDao.getAccessToken())
            } catch (e: Exception) {
                onError(e.message?:"") {
                    participate(eventId)
                }
            }
        }
    }
    private fun onError(reason: String, onRetryListener: OnRetryListener) {
        _dataState.value = FeedModelState(error = Error(reason, onRetryListener))
        _dataState.value = FeedModelState()
    }

}