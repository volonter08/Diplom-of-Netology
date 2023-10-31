package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import ru.netology.nmedia.databinding.ItemLoadingBinding
import ru.netology.nmedia.viewHolder.PostLoadingStateViewHolder

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