package com.example.kotlincoroutine.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kotlincoroutine.api.User
import com.example.kotlincoroutine.repository.UserRepository
import kotlinx.coroutines.launch

/**
 * @author AlexisYin
 */
class MainViewModel() : ViewModel() {
    val userLiveData = MutableLiveData<User>()

    private val userRepository = UserRepository()

    fun getUser(name: String) {
        viewModelScope.launch {
            userLiveData.value = userRepository.getUser(name)
        }
    }

}