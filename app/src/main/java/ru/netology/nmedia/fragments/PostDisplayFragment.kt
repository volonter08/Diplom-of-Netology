package ru.netology.nmedia.fragments

import android.animation.ObjectAnimator
import android.animation.TypeEvaluator
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import ru.netology.nmedia.OnButtonTouchListener
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.adapter.PostLoadingStateAdapter
import ru.netology.nmedia.databinding.FragmentPostDisplayBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewModel.PostViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.nmedia.ErrorWindow

@AndroidEntryPoint
class PostDisplayFragment : Fragment() {
    private val viewModel: PostViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewBinding = FragmentPostDisplayBinding.inflate(layoutInflater, container, false)
        val postOnButtonTouchListener = object : OnButtonTouchListener {
            override fun onLikeCLick(id: Int) {
                viewModel.like(id)
            }

            override fun onDislikeCLick(id: Int) {
                viewModel.dislike(id)
            }

            override fun onShareCLick(post: Post) {
                val intentSend = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }
                val chooserIntentSend = Intent.createChooser(intentSend, "getString(R.string.)")
                startActivity(chooserIntentSend)
            }

            override fun onRemoveClick(id: Int) {
                viewModel.remove(id)
            }

            override fun onUpdateCLick(post: Post) {
                findNavController().navigate(
                    R.id.action_postDisplayFragment_to_savePostFragment,
                    args = bundleOf("post" to post)
                )
            }

            override fun onCreateClick() {
                findNavController().navigate(
                    R.id.action_postDisplayFragment_to_savePostFragment,
                    args = bundleOf("post" to null)
                )
            }
        }
        val postAdapter = PostAdapter(context = requireContext(), postOnButtonTouchListener)
        viewBinding.recycleView.adapter = postAdapter.withLoadStateHeaderAndFooter(
            header = PostLoadingStateAdapter {
                postAdapter.retry()
            },
            footer = PostLoadingStateAdapter {
                postAdapter.retry()
            }
        )
        viewBinding.swipeRefreshLayout.setOnRefreshListener(postAdapter::refresh)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.data.collectLatest {
                    postAdapter.submitData(it)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.auth.authStateFlow.collectLatest {
                    postAdapter.refresh()
                }
            }
        }
        viewModel.dataState.observe(viewLifecycleOwner) { feedModel ->
            feedModel.run {
                viewBinding.progressBar.isVisible = loading
                viewBinding.swipeRefreshLayout.isRefreshing = isRefreshed
                when {
                    error -> {
                        ErrorWindow.show(
                            requireContext(), "",
                            feedModel.errorRetryListener!!::onRetry
                        )
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                postAdapter.loadStateFlow.collectLatest { state ->
                    viewBinding.swipeRefreshLayout.isRefreshing =
                        state.refresh is LoadState.Loading
                }
            }
        }
        viewBinding.createButton.setOnClickListener {
            postOnButtonTouchListener.onCreateClick()
        }
        return viewBinding.root
    }
}