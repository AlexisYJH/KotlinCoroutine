package com.example.coroutinedemo.repository

import androidx.paging.*
import com.example.coroutinedemo.db.AppDatabase
import com.example.coroutinedemo.entity.MovieEntity
import com.example.coroutinedemo.mapper.Entity2ModelMapper
import com.example.coroutinedemo.mapper.Mapper
import com.example.coroutinedemo.model.Movie
import com.example.coroutinedemo.remote.MovieRemoteMediator
import com.example.coroutinedemo.remote.MovieService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

/**
 * @author AlexisYin
 */
class MovieRepositoryImpl(
    private val api: MovieService,
    private val database: AppDatabase,
    private val mapper: Mapper<MovieEntity, Movie>
) : Repository{

    @OptIn(ExperimentalPagingApi::class)
    override fun fetchMovieList(): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                pageSize = 8,
                prefetchDistance = 1,
                initialLoadSize = 16
            ),
            //请求网络数据，放入数据库
            remoteMediator = MovieRemoteMediator(api, database)
        ) {
            //从数据库拿到数据
            database.MovieDao().getMovie()
        }.flow.flowOn(Dispatchers.IO).map { pagingData ->
            //对数据进行转换，给到UI显示
            pagingData.map { mapper.map(it) }
        }
    }
}