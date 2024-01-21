package ru.netology.nmedia.fragments

import android.annotation.SuppressLint
import android.graphics.Point
import android.graphics.drawable.AnimatedImageDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.bumptech.glide.Glide
import ru.netology.nmedia.entity.utills.RevealAnimationSetting
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.withCreationCallback
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.nmedia.OnButtonTouchListener
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.JobAdapter
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.databinding.FragmentProfileBinding
import ru.netology.nmedia.dto.Event
import ru.netology.nmedia.dto.Job
import ru.netology.nmedia.dto.Note
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.Profile
import ru.netology.nmedia.model.ErrorCallback
import ru.netology.nmedia.viewModel.AuthViewModel
import ru.netology.nmedia.viewModel.JobViewModel
import ru.netology.nmedia.viewModel.JobViewModelFactory
import ru.netology.nmedia.viewModel.PostViewModel
import ru.netology.nmedia.viewModel.PostViewModelFactory
import ru.netology.nmedia.viewModel.ProfileViewModel
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    @Inject
    lateinit var errorCallback: ErrorCallback

    @Inject
    lateinit var dataMyProfile: LiveData<Profile>
    private lateinit var profileFragmentBinding: FragmentProfileBinding
    private lateinit var postAdapter: PostAdapter
    private val profileViewModel: ProfileViewModel by viewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    private val postViewModel: PostViewModel by viewModels(
        extrasProducer = {
            defaultViewModelCreationExtras.withCreationCallback<
                    PostViewModelFactory> { factory ->
                factory.create(0)
            }
        }
    )
    private val jobViewModel by viewModels<JobViewModel>(
        extrasProducer = {
            defaultViewModelCreationExtras.withCreationCallback<JobViewModelFactory> { factory ->
                factory.create(0)
            }
        }
    )
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val onButtonTouchListener = object : OnButtonTouchListener {
            override fun onLikeCLick(likedNote: Note) {
                likedNote.run {
                    when (this) {
                        is Post -> postViewModel.like(this)
                        is Event -> {}
                    }
                }
            }

            override fun onDislikeCLick(dislikedNote: Note) {
                dislikedNote.run {
                    when (this) {
                        is Post -> postViewModel.dislike(this)
                        is Event -> {}
                    }
                }
            }

            override fun onRemoveClick(removedNote: Note) {
                removedNote.run {
                    when (this) {
                        is Post -> postViewModel.remove(removedNote as Post)
                        is Event -> {}
                        is Job -> jobViewModel.remove(removedNote as Job)
                    }
                }
            }

            override fun onUpdateCLick(note: Note, point: Point) {
                note.run {
                    when (this) {
                        is Job ->
                            showSaveJobFragment(point, note as Job)
                    }
                }
            }

            override fun onPostAuthorClick(authorId: Int) {
            }

            override fun onParticipate(eventId: Int) {
            }

            override fun onUnparticipate(eventId: Int) {
            }
        }

        profileFragmentBinding =
            FragmentProfileBinding.inflate(layoutInflater, container, false)
        //Initialise adapters
        postAdapter = PostAdapter(context = requireContext(), onButtonTouchListener)
        val jobAdapter = JobAdapter(onButtonTouchListener)
        dataMyProfile.observe(viewLifecycleOwner) {
            if (it.id == 0 && it.token == null) {
                findNavController().navigate(R.id.action_profileFragment_to_signInFragment)
            } else if (it.login == null && it.name == null && it.avatar == null) {
                profileViewModel.initUserData(it.id.toString())
            } else {
                profileFragmentBinding.login.text =
                    String.format(getString(R.string.login_format), it.login)
                profileFragmentBinding.name.text = String.format(getString(R.string.name_format), it.name)
                val animPlaceHolder =
                    requireContext().getDrawable(R.drawable.loading_avatar) as AnimatedImageDrawable
                animPlaceHolder.start()// probably needed
                Glide.with(requireContext()).load(it.avatar).circleCrop()
                    .placeholder(animPlaceHolder).timeout(10_000)
                    .error(R.drawable.avatar_svgrepo_com)
                    .into(profileFragmentBinding.profileAvatar)
            }
        }
        profileFragmentBinding.postRecycleView.adapter = postAdapter
        profileFragmentBinding.jobRecycleView.adapter = jobAdapter
        profileViewModel.dataProfile.observe(viewLifecycleOwner) {
            authViewModel.updateUserData(it)
        }
        postAdapter.addLoadStateListener { loadStates ->
            println(postAdapter.itemCount)
            if (loadStates.refresh is LoadState.NotLoading) {
                profileFragmentBinding.emptyMyPostListText.isVisible = postAdapter.itemCount < 1
            }
        }
        profileViewModel.dataState.observe(viewLifecycleOwner) { feedModelState ->
            profileFragmentBinding.progressBarLayout.isVisible = feedModelState.loading
            feedModelState.error?.let {
                errorCallback.onError(it.reason, it.onRetryListener)
            }
        }
        jobViewModel.dataState.observe(viewLifecycleOwner) { feedModelState ->
            profileFragmentBinding.progressBarLayout.isVisible = feedModelState.loading
            profileFragmentBinding.jobsSwipeRefreshLayout.isRefreshing = feedModelState.isRefreshing
            feedModelState.error?.let {
                errorCallback.onError(it.reason, it.onRetryListener)
            }
        }
        profileFragmentBinding.exit.setOnClickListener {
            authViewModel.exit {
                findNavController().navigate(R.id.action_profileFragment_to_signInFragment)
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
                postAdapter.onPagesUpdatedFlow.collect {
                    profileFragmentBinding.emptyMyPostListText.isVisible =
                        postAdapter.itemCount == 0
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                postAdapter.loadStateFlow.collectLatest { state ->
                    profileFragmentBinding.postSwipeRefreshLayout.isRefreshing =
                        state.refresh is LoadState.Loading
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                jobViewModel.data.collectLatest {
                    jobAdapter.submitList(it)
                    profileFragmentBinding.emptyMyJobsListText.isVisible = it.isEmpty()
                }
            }
        }
        profileFragmentBinding.createJobButton.setOnTouchListener { _, motionEvent ->
            return@setOnTouchListener if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                showSaveJobFragment(Point(motionEvent.rawX.toInt(), motionEvent.rawY.toInt()))
                true
            } else false
        }
        profileFragmentBinding.postSwipeRefreshLayout.setOnRefreshListener(postAdapter::refresh)
        profileFragmentBinding.jobsSwipeRefreshLayout.setOnRefreshListener(jobViewModel::loadJobs)
        return profileFragmentBinding.root
    }

    private fun showSaveJobFragment(point: Point, job: Job? = null) {
        findNavController().navigate(
            R.id.action_profileFragment_to_saveJobFragment,
            bundleOf(
                "revealAnimationSetting" to RevealAnimationSetting.create(
                    point,
                    profileFragmentBinding.root.width,
                    profileFragmentBinding.root.height
                ), "job" to job
            )
        )
    }
}