package com.example.netologyandroidhomework1.fragments

import android.animation.ObjectAnimator
import android.animation.TypeEvaluator
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.result.launch
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.example.netologyandroidhomework1.OnButtonTouchListener
import com.example.netologyandroidhomework1.R
import com.example.netologyandroidhomework1.adapter.PostAdapter
import com.example.netologyandroidhomework1.databinding.FragmentPostDisplayBinding
import com.example.netologyandroidhomework1.dto.Post
import com.example.netologyandroidhomework1.viewModel.PostViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PostDisplayFragment : Fragment() {
    private val viewModel: PostViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewBinding = FragmentPostDisplayBinding.inflate(layoutInflater,container,false)
        val postOnButtonTouchListener = object : OnButtonTouchListener {
            override fun onLikeCLick(id:Long) {
                viewModel.like(id)
            }
            override fun onDislikeCLick(id: Long) {
                viewModel.dislike(id)
            }
            override fun onShareCLick(post: Post){
                val intentSend = Intent().apply {
                    action= Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT,post.content)
                    type = "text/plain"
                }
                val chooserIntentSend = Intent.createChooser(intentSend, "getString(R.string.)")
                startActivity(chooserIntentSend)
            }
            override fun onRemoveClick(id: Long) {
                viewModel.remove(id)
            }
            override fun onUpdateCLick(post: Post) {
                findNavController().navigate(R.id.action_postDisplayFragment_to_savePostFragment, args = bundleOf("post" to post))
            }
            override fun onCreateClick() {
                findNavController().navigate(R.id.action_postDisplayFragment_to_savePostFragment, args = bundleOf("post" to null))
            }
        }
        val postAdapter = PostAdapter(context = requireContext(),postOnButtonTouchListener)
        viewBinding.recycleView.adapter = postAdapter
        viewBinding.swipeRefreshLayout.setOnRefreshListener(postAdapter::refresh)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.data.collectLatest{
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
                when{
                    error -> {
                        MaterialAlertDialogBuilder(requireContext()).setTitle(R.string.request_is_not_successful).setPositiveButton("OK"){ dialog, which->
                            feedModel.errorRetryListener?.onRetry()
                        }.create().apply {
                            window?.setGravity(Gravity.TOP)
                            ObjectAnimator.ofObject(this.window,"attributes",object :
                                TypeEvaluator<WindowManager.LayoutParams> {
                                override fun evaluate(
                                    fraction: Float,
                                    startValue: WindowManager.LayoutParams,
                                    endValue: WindowManager.LayoutParams
                                ): WindowManager.LayoutParams {
                                    val attr=  WindowManager.LayoutParams()
                                    attr.copyFrom(window?.attributes)
                                    return attr.apply {
                                        y = (startValue.y + (endValue.y - startValue.y)*fraction).toInt()
                                    }
                                }
                            }, WindowManager.LayoutParams().apply { y = 2000}).apply {
                                duration = 40000
                                start()
                            }
                        }.show()
                    }

                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                postAdapter.loadStateFlow.collectLatest { state ->
                    viewBinding.swipeRefreshLayout.isRefreshing =
                        state.refresh is LoadState.Loading ||
                                state.prepend is LoadState.Loading ||
                                state.append is LoadState.Loading
                }
            }
        }
        viewBinding.createButton.setOnClickListener {
            postOnButtonTouchListener.onCreateClick()
        }
        return viewBinding.root
    }
}