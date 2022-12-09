package com.example.flowpractice.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.flowpractice.db.AppDataBase
import com.example.flowpractice.db.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

/**
 * @author AlexisYin
 */
class UserViewModel(app: Application): AndroidViewModel(app) {
    fun insert(uid: String, name: String) {
        viewModelScope.launch {
            AppDataBase.getInstance(getApplication()).userDao().insert(User(uid.toInt(), name))
            Log.d("TAG", "insert: $uid")
        }
    }

    fun getAll() : Flow<List<User>> {
        return AppDataBase.getInstance(getApplication()).userDao().getAll()
            .catch {
                Log.e("TAG","getAll", it) }
            .flowOn(Dispatchers.IO)
    }
}