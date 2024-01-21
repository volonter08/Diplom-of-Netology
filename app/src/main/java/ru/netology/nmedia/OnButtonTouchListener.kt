package ru.netology.nmedia

import android.graphics.Point
import ru.netology.nmedia.dto.Note

interface OnButtonTouchListener {
    fun onLikeCLick(likedNote: Note)
    fun onDislikeCLick(dislikedNote:Note)
    fun onRemoveClick(removedNote:Note)
    fun onUpdateCLick(note: Note,point: Point)
    fun onPostAuthorClick(authorId:Int)
    fun onParticipate(eventId:Int)
    fun onUnparticipate(eventId: Int)
}