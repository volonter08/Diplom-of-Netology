package ru.netology.nmedia.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ru.netology.nmedia.FeedModelState
import ru.netology.nmedia.OnRetryListener
import ru.netology.nmedia.JobRepositoryEntryPoint
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dao.ProfileDao
import ru.netology.nmedia.dto.Job
import ru.netology.nmedia.repository.JobRepository
import ru.netology.nmedia.responses.Error

@HiltViewModel(assistedFactory = JobViewModelFactory::class)
class JobViewModel @AssistedInject constructor(
    @Assisted
    val userId: Int,
    @ApplicationContext context: Context,
    val auth: AppAuth,
    val profileDao: ProfileDao
) : ViewModel() {

    val entryPoint: JobRepositoryEntryPoint =
        EntryPointAccessors.fromApplication(context, JobRepositoryEntryPoint::class.java)
    val repository: JobRepository = when (userId) {
        0 -> entryPoint.myJobRepository()
        else -> entryPoint.userJobRepositoryFactory().create(userId)
    }

    val data: Flow<List<Job>> = repository.data
    val _dataState: MutableLiveData<FeedModelState> = MutableLiveData()
    val dataState: LiveData<FeedModelState>
        get() = _dataState
    init {
        loadJobs()
    }
    fun loadJobs() {
        viewModelScope.launch {
            try {
                _dataState.value = FeedModelState(isRefreshed = true)
                repository.loadJobs(profileDao.getAccessToken())
                _dataState.value = FeedModelState()
            }
            catch (e:Exception){
                onError(e.message ?: "") {
                    loadJobs()
                }
            }
        }
    }

    fun remove(job: Job) {
        viewModelScope.launch {
            try {
                repository.remove(job, profileDao.getAccessToken())
            } catch (e: Exception) {
                onError(e.message ?: "") {
                    remove(job)
                }
            }
        }
    }

    fun save(job:Job) {
        viewModelScope.launch {
            try {
                _dataState.value = FeedModelState(loading = true)
                repository.save(job, token = profileDao.getAccessToken())
                _dataState.value = FeedModelState(isSaved = true)
            } catch (e: Exception) {
                onError(e.message ?: "") {
                   save(job)
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
interface JobViewModelFactory {
    fun create(authorId: Int): JobViewModel
}