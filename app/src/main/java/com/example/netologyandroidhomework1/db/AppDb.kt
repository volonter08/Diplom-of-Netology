package com.example.netologyandroidhomework1.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.netologyandroidhomework1.dao.PostDao
import com.example.netologyandroidhomework1.dao.PostRemoteKeyDao
import com.example.netologyandroidhomework1.entity.PostEntity
import com.example.netologyandroidhomework1.entity.PostRemoteKeyEntity

@Database(entities = [PostEntity::class,PostRemoteKeyEntity::class], version = 1, exportSchema = false)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
}