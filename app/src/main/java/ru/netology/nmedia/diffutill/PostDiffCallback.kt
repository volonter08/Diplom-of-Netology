package ru.netology.nmedia.diffutill

import androidx.recyclerview.widget.DiffUtil
import ru.netology.nmedia.dto.Post

class PostDiffCallback: DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean=
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean{
        println(oldItem==newItem)
        return oldItem==newItem
    }
}