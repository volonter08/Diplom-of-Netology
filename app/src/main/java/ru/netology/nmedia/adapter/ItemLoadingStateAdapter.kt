package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import ru.netology.nmedia.databinding.ItemLoadingBinding
import ru.netology.nmedia.viewHolder.ItemLoadingStateViewHolder

class ItemLoadingStateAdapter(val retry:()->Unit): LoadStateAdapter<ItemLoadingStateViewHolder>() {
    override fun onBindViewHolder(holder: ItemLoadingStateViewHolder, loadState: LoadState) {
        holder.bind(loadState,retry)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): ItemLoadingStateViewHolder {
        val binding = ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemLoadingStateViewHolder(binding,)
    }
}