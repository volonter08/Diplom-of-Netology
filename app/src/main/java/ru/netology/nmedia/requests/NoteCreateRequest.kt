package ru.netology.nmedia.requests

import ru.netology.nmedia.dto.Coordinates
import ru.netology.nmedia.model.Attachment
import ru.netology.nmedia.model.TypeOnline
import java.util.Date

abstract class NoteCreateRequest(
){
    abstract val id:Int
}