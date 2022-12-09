package com.example.flowpractice.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.flowpractice.model.Article
import com.example.flowpractice.net.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

/**
 * @author AlexisYin
 */
class ArticleViewModel(app: Application) : AndroidViewModel(app) {
    //防止flow嵌套flow
    val articles = MutableLiveData<List<Article>>()

    fun searchArticles(key: String) {
        viewModelScope.launch {
            flow {
                val list = RetrofitClient.articleApi.searchArticles(key)
                emit(list)
            }.flowOn(Dispatchers.IO)
                .catch { Log.e("TAG","searchArticles", it) }
                .collect {
                    articles.setValue(it)
                }
        }
    }
}