package com.example.coroutinedemo.repository

import androidx.paging.PagingData
import com.example.coroutinedemo.model.Movie
import kotlinx.coroutines.flow.Flow

/**
 * @author AlexisYin
 */
interface Repository {
    fun fetchMovieList() : Flow<PagingData<Movie>>
}