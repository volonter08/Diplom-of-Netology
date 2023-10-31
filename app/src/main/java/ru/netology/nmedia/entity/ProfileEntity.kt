package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.Profile
import ru.netology.nmedia.dto.User

@Entity
data class ProfileEntity(
    @PrimaryKey
    val type:ProfileData,
    val id:Int = 0,
    val token:String? = null,
    val login:String? = null,
    val name:String? = null,
    val avatar:String? = null

){
    fun toDto() = Profile(id,token,login,name,avatar)

    companion object {
        fun fromDto(dto:Profile) =dto.run{
            ProfileEntity(ProfileData.PROFILE,id,token,login,name,avatar)
        }
    }
}
enum class ProfileData{
    PROFILE
}
