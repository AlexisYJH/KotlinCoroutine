package com.example.jetpackpaging.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.jetpackpaging.model.Movie
import com.example.jetpackpaging.net.MovieApi
import com.example.jetpackpaging.net.RetrofitClient
import kotlinx.coroutines.delay

/**
 * @author AlexisYin
 */
const val PAGE_SIZE = 8
const val INITIAL_LOAD_SIZE = 16
class MoviePagingSource : PagingSource<Int, Movie>(){

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        //方便验证加载更多的脚布局
        delay(2000)
        //如果params.key为空，就是第一次加载，我们让currentPage等于1
        val currentPage = params.key ?: 1
        //一页有多少数据
        val pageSize = params.loadSize
        val movies = RetrofitClient.createApi(MovieApi::class.java).getMovies(currentPage, pageSize)
        Log.d("TAG", "currentPage:$currentPage, pageSize:$pageSize")

        var prevKey: Int?
        var nextKey: Int?

        if (currentPage == 1) {
            prevKey = null
            //nextKey为一次性加载的页数+1。比如我们一次性加载两页，那么下一页就是2+1
            nextKey = INITIAL_LOAD_SIZE/ PAGE_SIZE + 1
        } else {
            prevKey = currentPage - 1
            //如果有下一页就+1，否则为null。
            nextKey = if (movies.hasMore) currentPage+1 else null
        }
        Log.d("TAG", "prevKey:$prevKey, nextKey:$nextKey")

        return try {
            LoadResult.Page(data = movies.movieList, prevKey = prevKey, nextKey = nextKey)
        } catch (e: Exception) {
            e.printStackTrace()
            return LoadResult.Error(e)
        }
    }

    /**
     * getRefreshKey方法意思是 refresh时,从最后请求的页面开始请求,null则请求第一页。
     * 举个例子，请求出错时会调用refresh方法加载 ，如果当前已经请求了第一页到第四页的数据，
     * 可以通过设置在refresh 后会加载第5 - 8页的数据，并且前四页的数据都没了。
     * 如果getRefreshKey返回null，refresh后 会重新加载第一到第四页的数据，这里我们直接返回null即可。
     */
    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return null
    }
}