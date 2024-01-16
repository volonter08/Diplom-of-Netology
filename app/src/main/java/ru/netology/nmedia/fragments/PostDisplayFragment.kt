package ru.netology.nmedia.fragments

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.example.netologyandroidhomework1.AndroidUtils
import com.example.netologyandroidhomework1.Dismissible
import com.example.netologyandroidhomework1.RevealAnimationSetting
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.withCreationCallback
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.nmedia.OnButtonTouchListener
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.EventAdapter
import ru.netology.nmedia.adapter.ItemLoadingStateAdapter
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.databinding.FragmentPostDisplayBinding
import ru.netology.nmedia.dto.Event
import ru.netology.nmedia.dto.Note
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.ErrorCallback
import ru.netology.nmedia.viewModel.EventViewModel
import ru.netology.nmedia.viewModel.PostViewModel
import ru.netology.nmedia.viewModel.PostViewModelFactory
import javax.inject.Inject
import kotlin.concurrent.thread


@AndroidEntryPoint
class PostDisplayFragment : Fragment() {
    @Inject
    lateinit var errorCallback: ErrorCallback

    lateinit var viewBinding:FragmentPostDisplayBinding
    private val postViewModel: PostViewModel by viewModels<PostViewModel>(
        extrasProducer = {
            defaultViewModelCreationExtras.withCreationCallback<
                    PostViewModelFactory> { factory ->
                factory.create(-1)
            }
        }
    )

    private val eventViewModel: EventViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentPostDisplayBinding.inflate(layoutInflater, container, false)
        val onButtonTouchListener = object : OnButtonTouchListener {
            override fun onLikeCLick(likedNote: Note) {
                likedNote.run {
                    when (this) {
                        is Post -> postViewModel.like(this)
                        is Event -> eventViewModel.like(this)
                    }
                }
            }

            override fun onDislikeCLick(dislikedNote: Note) {
                dislikedNote.run {
                    when (this) {
                        is Post -> postViewModel.dislike(this)
                        is Event -> eventViewModel.dislike(this)
                    }
                }
            }

            override fun onShareCLick(note: Note) {

            }

            override fun onRemoveClick(removedNote: Note) {
                removedNote.run {
                    when (this) {
                        is Post -> postViewModel.remove(removedNote as Post)
                        is Event -> eventViewModel.remove(removedNote as Event)
                    }
                }
            }

            override fun onUpdateCLick(note: Note) {

            }
            override fun onPostAuthorClick(authorId:Int) {
                findNavController().navigate(R.id.action_postDisplayFragment_to_userFragment,
                    bundleOf("user_id" to authorId)
                )
            }
        }
        val postAdapter = PostAdapter(context = requireContext(), onButtonTouchListener)
        val eventAdapter = EventAdapter(context = requireContext(), onButtonTouchListener)
        viewBinding.eventRecycleView.adapter = eventAdapter.withLoadStateHeaderAndFooter(
            header = ItemLoadingStateAdapter {
                eventAdapter.retry()
            },
            footer = ItemLoadingStateAdapter {
                eventAdapter.retry()
            }
        )
        viewBinding.postRecycleView.adapter = postAdapter.withLoadStateHeaderAndFooter(
            header = ItemLoadingStateAdapter {
                postAdapter.retry()
            },
            footer = ItemLoadingStateAdapter {
                postAdapter.retry()
            }
        )
        viewBinding.eventSwipeRefreshLayout.setOnRefreshListener(eventAdapter::refresh)
        viewBinding.postSwipeRefreshLayout.setOnRefreshListener(postAdapter::refresh)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                eventViewModel.data.collectLatest {
                    eventAdapter.submitData(it)
                }

            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                postViewModel.data.collectLatest {
                    postAdapter.submitData(it)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                eventViewModel.auth.authStateFlow.collectLatest {
                    eventAdapter.refresh()
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                postViewModel.auth.authStateFlow.collectLatest {
                    postAdapter.refresh()
                }
            }
        }
        eventViewModel.dataState.observe(viewLifecycleOwner) { feedModel ->
            feedModel.run {
                viewBinding.progressBar.isVisible = loading
                viewBinding.eventSwipeRefreshLayout.isRefreshing = isRefreshed
                error?.let {
                    errorCallback.onError(it.reason, it.onRetryListener)
                }
            }
        }
        postViewModel.dataState.observe(viewLifecycleOwner) { feedModel ->
            feedModel.run {
                viewBinding.progressBar.isVisible = loading
                viewBinding.postSwipeRefreshLayout.isRefreshing = isRefreshed
                error?.let {
                    errorCallback.onError(it.reason, it.onRetryListener)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                eventAdapter.loadStateFlow.collectLatest { state ->
                    viewBinding.eventSwipeRefreshLayout.isRefreshing =
                        state.refresh is LoadState.Loading
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                postAdapter.loadStateFlow.collectLatest { state ->
                    viewBinding.postSwipeRefreshLayout.isRefreshing =
                        state.refresh is LoadState.Loading
                }
            }
        }
        viewBinding.createPostButton.setOnClickListener {
            showSavePostFragment()
        }
        viewBinding.createEventButton.setOnClickListener {
            showSaveEventFragment()
        }
        viewBinding.buttonSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                viewBinding.relativeEventLayout.isVisible = isChecked
                ObjectAnimator.ofPropertyValuesHolder(
                    viewBinding.relativeEventLayout,
                    PropertyValuesHolder.ofFloat(View.ALPHA, 0F, 1F)
                ).apply {
                    duration = 2000
                    interpolator = LinearInterpolator()
                }.start()
                viewBinding.relativePostLayout.visibility = View.GONE
            } else {
                viewBinding.relativePostLayout.isVisible = !isChecked
                ObjectAnimator.ofPropertyValuesHolder(
                    viewBinding.relativePostLayout,
                    PropertyValuesHolder.ofFloat(View.ALPHA, 0F, 1F)
                ).apply {
                    duration = 2000
                    interpolator = LinearInterpolator()
                }.start()
                viewBinding.relativeEventLayout.visibility = View.GONE
            }
        }
        return viewBinding.root
    }
    fun showSavePostFragment() {
        findNavController().navigate(
            R.id.action_postDisplayFragment_to_savePostFragment,
            bundleOf("revealAnimationSetting" to constructRevealSettings())
        )
    }
    fun showSaveEventFragment() {
        findNavController().navigate(
            R.id.action_postDisplayFragment_to_saveEventFragment,
            bundleOf("revealAnimationSetting" to constructRevealSettings())
        )
    }
    private fun constructRevealSettings(): RevealAnimationSetting? {
        val addFab = viewBinding.createPostButton
        val containerView = viewBinding.root
        return RevealAnimationSetting(
            (addFab.x + addFab.width / 2).toInt(),
            (addFab.y + addFab.height / 2).toInt(),
            containerView.width,
            containerView.height
        )
    }

}