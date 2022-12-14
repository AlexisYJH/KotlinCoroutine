package com.example.flowpractice.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * @author AlexisYin
 */
@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class AppDataBase : RoomDatabase(){
    abstract fun userDao() : UserDao
    companion object{
        private var instance: AppDataBase? = null
        fun getInstance(context: Context) : AppDataBase{
            return instance?: synchronized(this) {
                Room.databaseBuilder(context, AppDataBase::class.java, "my_db.db")
                    .build().also { instance = it }
            }
        }
    }
}