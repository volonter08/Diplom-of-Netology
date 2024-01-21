package ru.netology.nmedia.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ListConverter {
   @TypeConverter
   fun stringToList(value: String): List<Int>? {
       return Gson().fromJson(value,  object : TypeToken<List<Int>>() {}.type)
   }

    @TypeConverter
    fun listToString(value: List<Int>?): String {
        return if(value == null) "" else Gson().toJson(value)
    }
}