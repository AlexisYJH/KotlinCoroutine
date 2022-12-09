package com.example.jetpackpaging.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.example.jetpackpaging.model.Movie
import com.example.jetpackpaging.paging.INITIAL_LOAD_SIZE
import com.example.jetpackpaging.paging.MoviePagingSource
import com.example.jetpackpaging.paging.PAGE_SIZE
import kotlinx.coroutines.flow.Flow

/**
 * @author AlexisYin
 */
class MovieViewModel : ViewModel(){
    fun loadMovie(): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = 2, //距离最后一个Item多远时候加载数据，默认为PageSize，一定要大于一。太小的话会有Bug.给LoadMore预留足够的位置
                initialLoadSize = INITIAL_LOAD_SIZE //默认是3 X pageSize
            ),
            pagingSourceFactory = {MoviePagingSource()}
        ).flow.cachedIn(viewModelScope) //上游数据缓存
        //流的数据要缓存必须使用cachedIn，流会一直是活跃的只要我们给定的作用域是活跃的，activity没有挂掉，viewModelScope就会一直在
    }
}