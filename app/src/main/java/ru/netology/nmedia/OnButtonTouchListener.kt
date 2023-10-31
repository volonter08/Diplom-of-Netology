package ru.netology.nmedia

import ru.netology.nmedia.dto.Post

interface OnButtonTouchListener {
    fun onLikeCLick(id:Int)
    fun onDislikeCLick(id:Int)
    fun onShareCLick(post: Post)
    fun onRemoveClick(id:Int)
    fun onUpdateCLick(post: Post)
    fun onCreateClick()
}