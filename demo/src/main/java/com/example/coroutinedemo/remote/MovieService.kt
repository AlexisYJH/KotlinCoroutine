package com.example.coroutinedemo.remote

import com.example.coroutinedemo.model.Movie
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @author AlexisYin
 */
interface MovieService {
    @GET("cds.do")
    suspend fun getMovies(
        @Query("since") since: Int,
        @Query("pagesize") pageSize: Int
    ) : List<Movie>
}