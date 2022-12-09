package com.example.coroutinedemo.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.coroutinedemo.model.Movie
import com.example.coroutinedemo.repository.Repository

/**
 * @author AlexisYin
 */

class MovieViewModel @ViewModelInject constructor(
    private val repository: Repository
    ) : ViewModel(){
    val data : LiveData<PagingData<Movie>> =
        repository.fetchMovieList().cachedIn(viewModelScope).asLiveData()
}