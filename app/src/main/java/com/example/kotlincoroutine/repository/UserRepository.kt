package com.example.kotlincoroutine.repository

import com.example.kotlincoroutine.api.User
import com.example.kotlincoroutine.api.userServiceApi

/**
 * @author AlexisYin
 */
class UserRepository {
    suspend fun getUser(name: String) : User {
        return userServiceApi.getUser(name)
    }
}