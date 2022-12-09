package com.example.coroutinedemo.di

import com.example.coroutinedemo.db.AppDatabase
import com.example.coroutinedemo.mapper.Entity2ModelMapper
import com.example.coroutinedemo.remote.MovieService
import com.example.coroutinedemo.repository.MovieRepositoryImpl
import com.example.coroutinedemo.repository.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

/**
 * @author AlexisYin
 */
@InstallIn(ActivityComponent::class)
@Module
object RepositoryModule {
    @ActivityScoped
    @Provides
    fun providerRepository(api: MovieService, database: AppDatabase) : Repository {
        return MovieRepositoryImpl(api, database, Entity2ModelMapper())
    }
}