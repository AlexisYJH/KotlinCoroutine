package com.example.coroutinedemo.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.coroutinedemo.entity.MovieEntity

/**
 * @author AlexisYin
 */
@Database(entities = [MovieEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun MovieDao() : MovieDao
}