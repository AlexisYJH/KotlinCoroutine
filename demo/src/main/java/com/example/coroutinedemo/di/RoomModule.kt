package com.example.coroutinedemo.di

import android.app.Application
import androidx.room.Room
import com.example.coroutinedemo.db.AppDatabase
import com.example.coroutinedemo.db.MovieDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

/**
 * @author AlexisYin
 */
@InstallIn(ApplicationComponent::class)
@Module
object RoomModule {
    @Singleton
    @Provides
    fun providerAppDatabase(application: Application) : AppDatabase {
        return Room.databaseBuilder(application, AppDatabase::class.java, "movie.db").build()
    }

    @Singleton
    @Provides
    fun providerMovieDao(appDatabase: AppDatabase) : MovieDao {
        return appDatabase.MovieDao()
    }
}