package ru.netology.nmedia.viewHolder

import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.databinding.ItemLoadingBinding

class PostLoadingStateViewHolder (private val binding: ItemLoadingBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(loadState: LoadState, retry:()->Unit){
        binding.retryButton.isVisible = loadState is LoadState.Error
        binding.retryButton.setOnClickListener {
            retry()
        }
        binding.loadStateProgressBar.isVisible = loadState is LoadState.Loading
    }
}