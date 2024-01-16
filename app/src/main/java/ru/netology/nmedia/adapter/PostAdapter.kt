package ru.netology.nmedia.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.nmedia.OnButtonTouchListener
import ru.netology.nmedia.databinding.PostBinding
import ru.netology.nmedia.diffutill.PostDiffCallback
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewHolder.PostHolder

class PostAdapter(val context:Context,
    private val  listener: OnButtonTouchListener
) : PagingDataAdapter<Post, PostHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val binding = PostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostHolder(parent.context, binding, listener)
    }
    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        // FIXME: students will do in HW
        getItem(position)?.let {
            CoroutineScope(Dispatchers.Main).launch {
                holder.bind(it)
            }
        }
    }
}