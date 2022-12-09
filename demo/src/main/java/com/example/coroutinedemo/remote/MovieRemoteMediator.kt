package com.example.coroutinedemo.remote

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.coroutinedemo.db.AppDatabase
import com.example.coroutinedemo.entity.MovieEntity
import com.example.coroutinedemo.ext.isConnectedNetwork
import com.example.coroutinedemo.init.AppHelper
import com.example.coroutinedemo.init.LOG_TAG
import java.lang.Exception
import kotlin.math.sin

/**
 * @author AlexisYin
 */

/**
 * MediatorResult
 * - 请求错误，返回MediatorResult.Error(e)
 * - 请求成功且有数据，返回MediatorResult.Success(true)
 * - 请求成功但没有数据，返回MediatorResult.Success(false)
 */

@OptIn(ExperimentalPagingApi::class)
class MovieRemoteMediator(
    private val api: MovieService,
    private val database: AppDatabase
) : RemoteMediator<Int, MovieEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MovieEntity>
    ): MediatorResult {
        //1. 判断LoadType
        //2. 请求网络分页数据
        //3. 插入数据库
        try {
            //1. 根据LoadType计算page(当前页)
            Log.d(LOG_TAG, "loadType: $loadType")
            val pageKey =
                when(loadType) {
                //首次访问，或者调用PagingDataAdapter.refresh()
                LoadType.REFRESH -> null
                //在当前列表头部添加数据的时候使用
                LoadType.PREPEND -> return MediatorResult.Success(true)
                //在加载更多的时候使用
                LoadType.APPEND -> {
                    val lastItem : MovieEntity = state.lastItemOrNull() ?: return MediatorResult.Success(true)
                    Log.d(LOG_TAG, "lastItem: $lastItem")
                    lastItem.page
                }
            }
            //无网络，加载本地数据
            if (!AppHelper.mContext.isConnectedNetwork()) {
                return MediatorResult.Success(true)
            }

            //2. 请求网络分页数据
            val page = pageKey ?: 0
            val result = api.getMovies(page * state.config.pageSize, state.config.pageSize)
            //List<Movie> -> List<MovieEntity>
            val item = result.map {
                MovieEntity(
                    no = it.no,
                    id = it.id,
                    title = it.title,
                    rate = it.rate,
                    cover = it.cover,
                    page = page+1
                )
            }
            Log.d(LOG_TAG, "load page: $page, result: ${result.size}")

            //3. 插入数据库
            val endOfPaginationReached = result.isEmpty()
            val movieDao = database.MovieDao()
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    movieDao.clearMovie()
                }
                movieDao.insertMovie(item)
            }
            return MediatorResult.Success(endOfPaginationReached)
        } catch (e : Exception) {
            e.printStackTrace()
            return MediatorResult.Error(e)
        }
    }
}