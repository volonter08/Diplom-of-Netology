package ru.netology.nmedia.requests

import android.icu.text.SimpleDateFormat
import ru.netology.nmedia.dto.Job
import java.util.Date

data class JobCreateRequest(
    override val id: Int,
    val name: String,
    val position: String,
    val start: String,
    val finish: String?,
    val link: String?,
) :NoteCreateRequest(){
    constructor(job: Job):this(job.id,job.name,job.position,
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(job.start),SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(job.finish),job.link)
}