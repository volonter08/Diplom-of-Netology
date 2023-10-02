package com.example.netologyandroidhomework1.viewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.netologyandroidhomework1.FeedModel
import com.example.netologyandroidhomework1.FeedModelState
import com.example.netologyandroidhomework1.OnRetryListener
import com.example.netologyandroidhomework1.auth.AppAuth
import com.example.netologyandroidhomework1.db.AppDb
import com.example.netologyandroidhomework1.dto.Post
import com.example.netologyandroidhomework1.model.PostCallback
import com.example.netologyandroidhomework1.model.PostRepository
import kotlinx.coroutines.launch
import com.example.netologyandroidhomework1.utills.SingleLiveEvent
import com.google.gson.JsonSyntaxException
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(@ApplicationContext context: Context, val repository: PostRepository,
                                        val auth: AppAuth,) : ViewModel() {
    private val postCallback = object : PostCallback {
        override fun onError(onRetryListener: OnRetryListener) {
            _dataState.postValue(
                FeedModelState(error = true, errorRetryListener = onRetryListener)
            )
        }
    }
    private val cached = repository
        .data
        .cachedIn(viewModelScope)

    val data: Flow<PagingData<Post>> = auth.authStateFlow
        .flatMapLatest { (myId, _) ->
            cached.map { pagingData ->
                pagingData.map { post ->
                    post.copy(ownedByMe = post.authorId == myId)
                }
            }
        }
    val _dataState: MutableLiveData<FeedModelState> = MutableLiveData()
    val dataState:LiveData<FeedModelState>
    get() = _dataState
    init {
        loadPosts()
    }

    fun loadPosts() {
        viewModelScope.launch {
            try {
                _dataState.value= FeedModelState(loading = true)
                //repository.getAll()
                _dataState.value = FeedModelState()
            } catch (e: Exception) {
                e.printStackTrace()
                postCallback.onError {
                    loadPosts()
                }
            }
        }
    }
    fun refreshPosts(){
        viewModelScope.launch {
            try {
                _dataState.value = FeedModelState(isRefreshed = true)
                repository.getAll()
                _dataState.setValue(FeedModelState())
            } catch (_: Exception) {
                postCallback.onError {
                    refreshPosts()
                }
            }
        }
    }

    fun like(id: Long) {
        viewModelScope.launch {
            try {
               repository.like(id)
            } catch (_: Exception) {
                postCallback.onError {
                    like(id)
                }
            }
        }
    }

    fun dislike(id: Long)  {
        viewModelScope.launch {
            try {
                repository.dislike(id)
            } catch (_: Exception) {
                postCallback.onError {
                    dislike(id)
                }
            }
        }
    }

    fun share(id: Int) {
        repository.share(id)
    }

    fun remove(id: Long)  {
        viewModelScope.launch {
            try {
                repository.remove(id)
            } catch (_: Exception) {
                postCallback.onError {
                    remove(id)
                }
            }
        }
    }
    fun createPost(content: String) {
        viewModelScope.launch {
            try {
                repository.createPost(content)
            } catch (_: Exception) {
                postCallback.onError {
                    createPost(content)
                }
            }
        }
    }

    fun update(post: Post)  {
        viewModelScope.launch {
            try {
                repository.update(post)
            } catch (_: Exception) {
                postCallback.onError {
                    update(post)
                }
            }
        }
    }
}