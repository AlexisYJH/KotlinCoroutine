package com.example.jetpackpaging.net

import com.example.jetpackpaging.model.Movies
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @author AlexisYin
 */
interface MovieApi {

    @GET("pkds.do")
    suspend fun getMovies(
        @Query("page") page: Int,
        @Query("pagesize") pageSize: Int
    ) : Movies
}