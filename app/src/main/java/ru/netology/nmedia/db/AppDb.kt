package ru.netology.nmedia.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.netology.nmedia.converters.ListConverter
import ru.netology.nmedia.converters.MapConverter
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dao.PostRemoteKeyDao
import ru.netology.nmedia.dao.ProfileDao
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.PostRemoteKeyEntity
import ru.netology.nmedia.entity.ProfileEntity

@Database(entities = [PostEntity::class,PostRemoteKeyEntity::class,ProfileEntity::class], version = 1, exportSchema = false)
@TypeConverters(MapConverter::class,ListConverter::class)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
    abstract fun profileDao():ProfileDao
}