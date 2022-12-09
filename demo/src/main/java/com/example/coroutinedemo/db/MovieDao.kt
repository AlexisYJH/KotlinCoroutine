package com.example.coroutinedemo.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.coroutinedemo.entity.MovieEntity

/**
 * @author AlexisYin
 */
@Dao
interface MovieDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movieList : List<MovieEntity>)

    @Query("SELECT * FROM MovieEntity")
    fun getMovie() : PagingSource<Int, MovieEntity>

    @Query("DELETE FROM MovieEntity")
    suspend fun clearMovie(): Int
}