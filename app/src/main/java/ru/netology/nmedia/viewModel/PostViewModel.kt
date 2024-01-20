package ru.netology.nmedia.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors
import ru.netology.nmedia.OnRetryListener
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.collectLatest
import ru.netology.nmedia.FeedModelState
import ru.netology.nmedia.PostRepositoryEntryPoint
import ru.netology.nmedia.dao.ProfileDao
import ru.netology.nmedia.repository.AllPostsRepository
import ru.netology.nmedia.requests.PostCreateRequest
import ru.netology.nmedia.responses.Error

@HiltViewModel(assistedFactory = PostViewModelFactory::class)
class PostViewModel @AssistedInject constructor(
    @Assisted
    val authorId: Int,
    @ApplicationContext context: Context,
    val auth: AppAuth,
    val profileDao: ProfileDao
) : ViewModel() {

    val entryPoint: PostRepositoryEntryPoint =
        EntryPointAccessors.fromApplication(context, PostRepositoryEntryPoint::class.java)
    val repository: PostRepository = when (authorId) {
        0 -> entryPoint.myPostRepository()
        -1 -> entryPoint.allPostRepository()
        else -> entryPoint.userPostRepositoryFactory().create(authorId)
    }
    val data = repository
        .data
        .cachedIn(viewModelScope)
    val _dataState: MutableLiveData<ru.netology.nmedia.FeedModelState> = MutableLiveData()
    val dataState: LiveData<ru.netology.nmedia.FeedModelState>
        get() = _dataState

    init {
        viewModelScope.launch {
            auth.authStateFlow.collectLatest {
                _dataState.value = FeedModelState(isRefreshing = true)
            }
        }
    }

    fun like(likedPost: Post) {
        viewModelScope.launch {
            try {
                repository.like(likedPost, profileDao.getAccessToken())
            } catch (e: Exception) {
                onError(e.message ?: "") {
                    like(likedPost)
                }
            }
        }
    }

    fun dislike(disLikedPost: Post) {
        viewModelScope.launch {
            try {
                repository.dislike(disliked = disLikedPost, profileDao.getAccessToken())
            } catch (e: Exception) {
                onError(e.message ?: "") {
                    dislike(disLikedPost)
                }
            }
        }
    }

    fun remove(post: Post) {
        viewModelScope.launch {
            try {
                repository.remove(post, profileDao.getAccessToken())
            } catch (e: Exception) {
                onError(e.message ?: "") {
                    remove(post)
                }
            }
        }
    }

    fun savePost(postCreateRequest: PostCreateRequest) {
        viewModelScope.launch {
            try {
                _dataState.value = FeedModelState(loading = true)
                repository.save(postCreateRequest, profileDao.getAccessToken())
                _dataState.value = FeedModelState(isSaved = true)
            } catch (e: Exception) {
                onError(e.message ?: "") {
                    savePost(postCreateRequest)
                }
            }
        }
    }
    fun invalidateDataState(){
        _dataState.value = FeedModelState()
    }

    private fun onError(reason: String, onRetryListener: OnRetryListener) {
        _dataState.value = FeedModelState(error = Error(reason, onRetryListener))
        _dataState.value = FeedModelState()
    }
}

@AssistedFactory
interface PostViewModelFactory {
    fun create(authorId: Int): PostViewModel
}