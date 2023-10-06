package com.example.netologyandroidhomework1.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ListAdapter
import com.example.netologyandroidhomework1.OnButtonTouchListener
import com.example.netologyandroidhomework1.databinding.PostBinding
import com.example.netologyandroidhomework1.diffutill.PostDiffCallback
import com.example.netologyandroidhomework1.dto.Post
import com.example.netologyandroidhomework1.viewHolder.PostHolder

class PostAdapter(val context:Context,
    private val  listener:OnButtonTouchListener
) : PagingDataAdapter<Post, PostHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val binding = PostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostHolder(parent.context,binding,listener)
    }
    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        // FIXME: students will do in HW
        getItem(position)?.let {
            holder.bind(it)
        }
    }
}