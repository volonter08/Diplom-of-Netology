package ru.netology.nmedia.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.dto.UserPreview

class MapConverter {
    @TypeConverter
    fun stringToMap(value: String): Map<String, UserPreview>? {
        return Gson().fromJson(value,  object : TypeToken<Map<String, UserPreview>>() {}.type)
    }

    @TypeConverter
    fun mapToString(value: Map<String, UserPreview>?): String {
        return if(value == null) "" else Gson().toJson(value)
    }
}