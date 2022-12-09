package com.example.kotlincoroutine.api

import android.util.Log
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @author AlexisYin
 */
data class User(val name: String, val address: String)

val userServiceApi : UserServiceApi by lazy {
    val retrofit = Retrofit.Builder()
        .client(OkHttpClient.Builder().addInterceptor(){
            it.proceed(it.request()).apply {
                Log.d("TAG", "request: ${code()}")
            }
        }.build())
        .baseUrl("http://192.168.0.104:8080/kotlinstudyserver/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    retrofit.create(UserServiceApi::class.java)
}

interface UserServiceApi {
    @GET("user")
    fun loadUser(@Query("name") name: String) : Call<User>

    @GET("user")
    suspend fun getUser(@Query("name") name: String) : User
}

