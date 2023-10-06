package com.example.netologyandroidhomework1.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.example.netologyandroidhomework1.databinding.ItemLoadingBinding
import com.example.netologyandroidhomework1.databinding.PostBinding
import com.example.netologyandroidhomework1.viewHolder.PostHolder
import com.example.netologyandroidhomework1.viewHolder.PostLoadingStateViewHolder

class PostLoadingStateAdapter(val retry:()->Unit): LoadStateAdapter<PostLoadingStateViewHolder>() {
    override fun onBindViewHolder(holder: PostLoadingStateViewHolder, loadState: LoadState) {
        holder.bind(loadState,retry)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): PostLoadingStateViewHolder {
        val binding = ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostLoadingStateViewHolder(binding,)
    }
}