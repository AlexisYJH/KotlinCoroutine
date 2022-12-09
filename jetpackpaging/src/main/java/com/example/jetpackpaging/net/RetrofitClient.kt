package com.example.jetpackpaging.net

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * @author AlexisYin
 */
object RetrofitClient {
    private val instance: Retrofit by lazy{
        val interceptor = HttpLoggingInterceptor{
            Log.d("TAG", it)
        }
        Retrofit.Builder()
            .client(OkHttpClient.Builder().addInterceptor(interceptor).build())
            .baseUrl("http://192.168.0.104:8080/pagingserver/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <T> createApi(clazz: Class<T>) : T {
        return instance.create(clazz) as T
    }
}