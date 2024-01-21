package ru.netology.nmedia.fragments

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import ru.netology.nmedia.entity.utills.RevealAnimationSetting
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.withCreationCallback
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.nmedia.OnButtonTouchListener
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.EventAdapter
import ru.netology.nmedia.adapter.ItemLoadingStateAdapter
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.databinding.FragmentAllNotesBinding
import ru.netology.nmedia.dto.Event
import ru.netology.nmedia.dto.Note
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.ErrorCallback
import ru.netology.nmedia.viewModel.EventViewModel
import ru.netology.nmedia.viewModel.PostViewModel
import ru.netology.nmedia.viewModel.PostViewModelFactory
import javax.inject.Inject


@AndroidEntryPoint
class AllNotesFragment : Fragment() {
    @Inject
    lateinit var errorCallback: ErrorCallback

    private lateinit var viewBinding: FragmentAllNotesBinding
    private val postViewModel: PostViewModel by viewModels(
        extrasProducer = {
            defaultViewModelCreationExtras.withCreationCallback<
                    PostViewModelFactory> { factory ->
                factory.create(-1)
            }
        }
    )

    private val eventViewModel: EventViewModel by viewModels()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentAllNotesBinding.inflate(layoutInflater, container, false)
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

            override fun onRemoveClick(removedNote: Note) {
                removedNote.run {
                    when (this) {
                        is Post -> postViewModel.remove(removedNote as Post)
                        is Event -> eventViewModel.remove(removedNote as Event)
                    }
                }
            }

            override fun onUpdateCLick(note: Note, point: Point) {
                note.run {
                    when (this) {
                        is Post -> showSavePostFragment(point,note as Post)
                        is Event -> showSaveEventFragment(point, note as Event)
                    }
                }
            }

            override fun onPostAuthorClick(authorId: Int) {
                findNavController().navigate(
                    R.id.action_postDisplayFragment_to_userFragment,
                    bundleOf("user_id" to authorId)
                )
            }

            override fun onParticipate(eventId: Int) {
                eventViewModel.participate(eventId)
            }

            override fun onUnparticipate(eventId: Int) {
                eventViewModel.unparticipate(eventId)
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
        eventViewModel.dataState.observe(viewLifecycleOwner) { feedModel ->
            feedModel.run {
                viewBinding.progressBar.isVisible = loading
                viewBinding.eventSwipeRefreshLayout.isRefreshing = isRefreshing
                error?.let {
                    errorCallback.onError(it.reason, it.onRetryListener)
                }
            }
        }
        postViewModel.dataState.observe(viewLifecycleOwner) { feedModel ->
            feedModel.run {
                viewBinding.progressBar.isVisible = loading
                if (isRefreshing)
                    postAdapter.refresh()
                viewBinding.postSwipeRefreshLayout.isRefreshing = isRefreshing
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
                    if (state.refresh != LoadState.Loading)
                        postViewModel.invalidateDataState()
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
        viewBinding.createPostButton.setOnTouchListener { _, motionEvent ->
            return@setOnTouchListener if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                showSavePostFragment(Point(motionEvent.rawX.toInt(), motionEvent.rawY.toInt()))
                true
            } else false
        }
        viewBinding.createEventButton.setOnTouchListener { _, motionEvent ->
            return@setOnTouchListener if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                showSaveEventFragment(Point(motionEvent.rawX.toInt(), motionEvent.rawY.toInt()))
                true
            } else false
        }
        viewBinding.buttonSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewBinding.relativeEventLayout.isVisible = true
                ObjectAnimator.ofPropertyValuesHolder(
                    viewBinding.relativeEventLayout,
                    PropertyValuesHolder.ofFloat(View.ALPHA, 0F, 1F)
                ).apply {
                    duration = 2000
                    interpolator = LinearInterpolator()
                }.start()
                viewBinding.relativePostLayout.visibility = View.GONE
            } else {
                viewBinding.relativePostLayout.isVisible = true
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

    fun showSavePostFragment(point: Point,post:Post? = null) {
        findNavController().navigate(
            R.id.action_postDisplayFragment_to_savePostFragment,
            bundleOf("revealAnimationSetting" to RevealAnimationSetting.create(point,viewBinding.root.width,viewBinding.root.height),
            "post" to post
                )
        )
    }

    fun showSaveEventFragment(point: Point,event:Event? = null) {
        findNavController().navigate(
            R.id.action_postDisplayFragment_to_saveEventFragment,
            bundleOf("revealAnimationSetting" to RevealAnimationSetting.create(point,viewBinding.root.width,viewBinding.root.height),
            "event" to event)
        )
    }
}