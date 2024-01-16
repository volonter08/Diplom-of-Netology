package ru.netology.nmedia.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

class DateConverter {
    @TypeConverter
    fun stringToDate(value: String): Date? {
        return Gson().fromJson(value,  object : TypeToken<Date>() {}.type)
    }

    @TypeConverter
    fun dateToString(value: Date?): String {
        return if(value == null) "null" else Gson().toJson(value)
    }
}