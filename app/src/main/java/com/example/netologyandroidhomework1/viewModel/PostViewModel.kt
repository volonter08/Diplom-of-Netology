package com.example.netologyandroidhomework1.viewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.netologyandroidhomework1.FeedModel
import com.example.netologyandroidhomework1.FeedModelState
import com.example.netologyandroidhomework1.OnRetryListener
import com.example.netologyandroidhomework1.db.AppDb
import com.example.netologyandroidhomework1.dto.Post
import com.example.netologyandroidhomework1.model.PostCallback
import com.example.netologyandroidhomework1.model.PostRepository
import kotlinx.coroutines.launch
import com.example.netologyandroidhomework1.utills.SingleLiveEvent
import com.google.gson.JsonSyntaxException
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(@ApplicationContext context: Context,val repository: PostRepository) : ViewModel() {
    private val postCallback = object : PostCallback {
        override fun onError(onRetryListener: OnRetryListener) {
            _dataState.postValue(
                FeedModelState(error = true, errorRetryListener = onRetryListener)
            )
        }
    }
    val data: LiveData<FeedModel> = repository.data.map(::FeedModel)
    val _dataState: MutableLiveData<FeedModelState> = MutableLiveData()
    val dataState:LiveData<FeedModelState>
    get() = _dataState
    init {
        loadPosts()
    }

    fun loadPosts() {
        viewModelScope.launch {
            try {
                _dataState.postValue(FeedModelState(loading = true))
                repository.getAll()
                _dataState.postValue(FeedModelState())
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
                _dataState.postValue(FeedModelState(isRefreshed = true))
                repository.getAll()
                _dataState.postValue(FeedModelState())
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