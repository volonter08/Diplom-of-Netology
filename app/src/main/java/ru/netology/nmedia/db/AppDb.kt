package ru.netology.nmedia.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.netology.nmedia.converters.DateConverter
import ru.netology.nmedia.converters.ListConverter
import ru.netology.nmedia.converters.MapConverter
import ru.netology.nmedia.dao.EventDao
import ru.netology.nmedia.dao.EventRemoteKeyDao
import ru.netology.nmedia.dao.ProfileDao
import ru.netology.nmedia.dao.jobDao.MyJobDao
import ru.netology.nmedia.dao.postDao.AllPostDao
import ru.netology.nmedia.dao.postDao.MyPostDao
import ru.netology.nmedia.dao.postRemoteKeyDao.AllPostRemoteKeyDao
import ru.netology.nmedia.dao.postRemoteKeyDao.MyPostRemoteKeyDao
import ru.netology.nmedia.entity.AllPostEntity
import ru.netology.nmedia.entity.AllPostRemoteKeyEntity
import ru.netology.nmedia.entity.EventEntity
import ru.netology.nmedia.entity.EventRemoteKeyEntity
import ru.netology.nmedia.entity.MyJobEntity
import ru.netology.nmedia.entity.MyPostEntity
import ru.netology.nmedia.entity.MyPostRemoteKeyEntity
import ru.netology.nmedia.entity.ProfileEntity

@Database(entities = [AllPostEntity::class,MyPostEntity::class,EventEntity::class, AllPostRemoteKeyEntity::class,MyPostRemoteKeyEntity::class,EventRemoteKeyEntity::class,ProfileEntity::class,MyJobEntity::class], version = 2, exportSchema = false)
@TypeConverters(MapConverter::class,ListConverter::class,DateConverter::class)
abstract class AppDb : RoomDatabase() {

    abstract fun allPostDao(): AllPostDao
    abstract fun myPostDao(): MyPostDao
    abstract fun allPostRemoteKeyDao(): AllPostRemoteKeyDao
    abstract fun myPostRemoteKeyDao(): MyPostRemoteKeyDao
    abstract fun profileDao(): ProfileDao
    abstract fun eventDao(): EventDao
    abstract fun eventRemoteKeyDao():EventRemoteKeyDao
    abstract fun myJobDao():MyJobDao
}