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
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import ru.netology.nmedia.FeedModelState
import ru.netology.nmedia.responses.Error
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    @ApplicationContext context: Context, val repository: PostRepository,
    val auth: AppAuth
) : ViewModel() {
    private val cached = repository
        .data
        .cachedIn(viewModelScope)

    var tokenAccess:String? = null
    val data: Flow<PagingData<Post>> = auth.authStateFlow
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

    init {
        loadPosts()
    }

    fun loadPosts() {
        viewModelScope.launch {
            try {
                _dataState.value = ru.netology.nmedia.FeedModelState(loading = true)
                //repository.getAllPosts()
                _dataState.value = ru.netology.nmedia.FeedModelState()
            } catch (e: Exception) {
                e.printStackTrace()
                onError("Request is not successful") {
                    loadPosts()
                }
            }
        }
    }
    fun like(likedPost: Post) {
        viewModelScope.launch {
            try {
                repository.like(likedPost,tokenAccess)
                _dataState.setValue(FeedModelState())
            } catch (e: Exception) {
                onError(e.message?:"") {
                    like(likedPost)
                }
            }
        }
    }

    fun dislike(disLikedPost: Post) {
        viewModelScope.launch {
            try {
                repository.dislike(dislikedPost = disLikedPost, tokenAccess )
                _dataState.setValue(FeedModelState())
            } catch (e: Exception) {
                onError(e.message?:"") {
                    dislike(disLikedPost)
                }
            }
        }
    }

    fun share(id: Int) {
        repository.share(id)
    }

    fun remove(id: Int) {
        viewModelScope.launch {
            try {
                repository.remove(id,tokenAccess)
            } catch (_: Exception) {
                onError("Request is not succesfull") {
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
                onError("Request is not succesfull") {
                    createPost(content)
                }
            }
        }
    }

    fun update(post: Post) {
        viewModelScope.launch {
            try {
                repository.update(post)
            } catch (_: Exception) {
                onError("Request is not succesfull") {
                    update(post)
                }
            }
        }
    }

    private fun onError(reason: String, onRetryListener: OnRetryListener) {
        _dataState.value = FeedModelState(error = Error(reason, onRetryListener))
        _dataState.value = FeedModelState()
    }
}