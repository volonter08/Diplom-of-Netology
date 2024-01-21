package ru.netology.nmedia.fragments

import android.graphics.Point
import android.graphics.drawable.AnimatedImageDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.withCreationCallback
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.nmedia.OnButtonTouchListener
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.JobAdapter
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.databinding.FragmentUserBinding
import ru.netology.nmedia.dto.Event
import ru.netology.nmedia.dto.Note
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.ErrorCallback
import ru.netology.nmedia.viewModel.JobViewModel
import ru.netology.nmedia.viewModel.JobViewModelFactory
import ru.netology.nmedia.viewModel.PostViewModel
import ru.netology.nmedia.viewModel.PostViewModelFactory
import ru.netology.nmedia.viewModel.ProfileViewModel
import javax.inject.Inject
import kotlin.properties.Delegates

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 * Use the [UserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class UserFragment : Fragment() {
    @Inject
    lateinit var errorCallback: ErrorCallback

    // TODO: Rename and change types of parameters
    private var userId by Delegates.notNull<Int>()
    private lateinit var userFragmentBinding: FragmentUserBinding
    private val postViewModel: PostViewModel by viewModels(
        extrasProducer = {
            defaultViewModelCreationExtras.withCreationCallback<
                    PostViewModelFactory> { factory ->
                factory.create(userId)
            }
        }
    )
    private val jobViewModel by viewModels<JobViewModel>(
        extrasProducer = {
            defaultViewModelCreationExtras.withCreationCallback<JobViewModelFactory> { factory->
                factory.create(userId)

            }
        }
    )
    private val profileViewModel: ProfileViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getInt("user_id")
        }
        profileViewModel.initUserData(userId.toString())
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        userFragmentBinding = FragmentUserBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
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
                    }
                }
            }

            override fun onUpdateCLick(note: Note, point: Point) {
                TODO("Not yet implemented")
            }

            override fun onPostAuthorClick(authorId: Int) {
            }

            override fun onParticipate(eventId: Int) {
                TODO("Not yet implemented")
            }

            override fun onUnparticipate(eventId: Int) {
                TODO("Not yet implemented")
            }
        }
        val postAdapter = PostAdapter(context = requireContext(), onButtonTouchListener)
        val jobAdapter = JobAdapter(onButtonTouchListener)
        val userFragmentBinding = FragmentUserBinding.inflate(layoutInflater, container, false)
        userFragmentBinding.postRecycleView.adapter = postAdapter
        userFragmentBinding.jobRecycleView.adapter = jobAdapter
        profileViewModel.dataProfile.observe(viewLifecycleOwner) {
            userFragmentBinding.login.text = String.format(getString(R.string.login), it.login)
            userFragmentBinding.name.text = String.format(getString(R.string.name), it.name)
            val animPlaceHolder =
                requireContext().getDrawable(R.drawable.loading_avatar) as AnimatedImageDrawable
            animPlaceHolder.start()// probably needed
            Glide.with(requireContext()).load(it.avatar).circleCrop()
                .placeholder(animPlaceHolder)
                .timeout(10_000).error(R.drawable.null_avatar)
                .into(userFragmentBinding.profileAvatar)
        }
        profileViewModel.dataState.observe(viewLifecycleOwner) { feedModelState ->
            userFragmentBinding.progressBarLayout.isVisible = feedModelState.loading
            feedModelState.error?.let {
                errorCallback.onError(it.reason, it.onRetryListener)
            }
        }
        postViewModel.dataState.observe(viewLifecycleOwner) { feedModel ->
            feedModel.run {
                userFragmentBinding.postSwipeRefreshLayout.isRefreshing = isRefreshing
                error?.let {
                    errorCallback.onError(it.reason, it.onRetryListener)
                }
            }
        }
        jobViewModel.dataState.observe(viewLifecycleOwner){feedModel->
            userFragmentBinding.jobsSwipeRefreshLayout.isRefreshing = feedModel.isRefreshing
            feedModel.error?.let {
                errorCallback.onError(it.reason, it.onRetryListener)
            }

        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                postViewModel.data.collectLatest {
                    postAdapter.submitData(it)
                    userFragmentBinding.emptyMyPostListText.isVisible = postAdapter.itemCount == 0
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
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                postAdapter.loadStateFlow.collectLatest { state ->
                    userFragmentBinding.postSwipeRefreshLayout.isRefreshing =
                        state.refresh is LoadState.Loading
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                jobViewModel.data.collectLatest {
                    jobAdapter.submitList(it)
                    userFragmentBinding.emptyMyJobsListText.isVisible = it.isEmpty()
                }
            }
        }
        userFragmentBinding.postSwipeRefreshLayout.setOnRefreshListener(postAdapter::refresh)
        userFragmentBinding.jobsSwipeRefreshLayout.setOnRefreshListener(jobViewModel::loadJobs)
        return userFragmentBinding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UserFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}