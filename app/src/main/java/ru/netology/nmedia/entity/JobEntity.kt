package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Job
import java.util.Date

abstract class JobEntity(
    open val id:Int,
    val name:String,
    val position:String,
    val start: Date,
    val finish:Date?,
    val link:String?
) {
    fun toDto() = Job(id = id,name  = name,position = position,start = start, finish = finish, link = link)
}
@Entity
class MyJobEntity (
    @PrimaryKey(autoGenerate = true)
    override val id:Int,
    name:String,
    position:String,
    start:Date,
    finish:Date?,
    link:String?
):JobEntity(id,name,position,start,finish,link){
    constructor(job:Job) : this(job.id,job.name,job.position,job.start,job.finish,job.link)
}
fun List<Job>.toMyEntity(): List<MyJobEntity> = map{
    MyJobEntity(it)
}
fun List<JobEntity>.toDto(): List<Job> = map{
    it.toDto()
}

