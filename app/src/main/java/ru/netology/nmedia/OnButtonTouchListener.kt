package ru.netology.nmedia

import ru.netology.nmedia.dto.Note
import ru.netology.nmedia.dto.Post

interface OnButtonTouchListener {
    fun onLikeCLick(likedNote: Note)
    fun onDislikeCLick(dislikedNote:Note)
    fun onShareCLick(note: Note)
    fun onRemoveClick(removedNote:Note)
    fun onUpdateCLick(note: Note)
    fun onCreateClick()
    fun onPostAuthorClick(authorId:Int)
}