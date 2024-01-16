package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Job
import ru.netology.nmedia.dto.Post

abstract class JobEntity(
    open val id:Int,
    val name:String,
    val position:String,
    val start:String,
    val finish:String?,
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
    start:String,
    finish:String?,
    link:String?
):JobEntity(id,name,position,start,finish,link){
    constructor(job:Job) : this(job.id,job.name,job.position,job.start,job.finish,job.link)
    companion object{
        fun fromDto(dto:Job) = dto.run {
            MyJobEntity(id, name, position, start, finish, link)
        }
    }
}
fun List<Job>.toMyEntity(): List<MyJobEntity> = map{
    MyJobEntity(it)
}
fun List<JobEntity>.toDto(): List<Job> = map{
    it.toDto()
}

