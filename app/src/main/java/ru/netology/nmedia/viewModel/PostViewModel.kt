package ru.netology.nmedia.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import ru.netology.nmedia.FeedModelState
import ru.netology.nmedia.PostRepositoryEntryPoint
import ru.netology.nmedia.dao.ProfileDao
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
    private val cached = repository
        .data
        .cachedIn(viewModelScope)

    val data: Flow<PagingData<Post>> = auth.authStateFlow
        .flatMapLatest { (myId, token, _, _, _) ->
            cached.map { pagingData ->
                pagingData.map { post ->
                    post.copy(
                        ownedByMe = post.authorId == myId,
                        likedByMe = post.likeOwnerIds.contains(myId),
                    )
                }
            }
        }
    val _dataState: MutableLiveData<ru.netology.nmedia.FeedModelState> = MutableLiveData()
    val dataState: LiveData<ru.netology.nmedia.FeedModelState>
        get() = _dataState

    fun like(likedPost: Post) {
        viewModelScope.launch {
            try {
                repository.like(likedPost, profileDao.getAccessToken())
                _dataState.setValue(FeedModelState())
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
                repository.dislike(dislikedPost = disLikedPost, profileDao.getAccessToken())
                _dataState.setValue(FeedModelState())
            } catch (e: Exception) {
                onError(e.message ?: "") {
                    dislike(disLikedPost)
                }
            }
        }
    }

    fun share(id: Int) {
        repository.share(id)
    }

    fun remove(post: Post) {
        viewModelScope.launch {
            try {
                repository.remove(post, profileDao.getAccessToken())
            } catch (e: Exception) {
                onError(e.message?:"") {
                    remove(post)
                }
            }
        }
    }

    fun createPost(content: String,link:String?=null) {
        viewModelScope.launch {
            try {
                _dataState.value = FeedModelState(loading = true)
                repository.createPost(content, link,profileDao.getAccessToken())
                _dataState.value = FeedModelState(isPostCreated = true)
            } catch (e: Exception) {
                onError(e.message?:"") {
                    createPost(content,link)
                }
            }
        }
    }

    fun update(post: Post) {
        viewModelScope.launch {
            try {
                repository.update(post,profileDao.getAccessToken())
            } catch (e: Exception) {
                onError(e.message?:"") {
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

@AssistedFactory
interface PostViewModelFactory {
    fun create(authorId: Int): PostViewModel
}