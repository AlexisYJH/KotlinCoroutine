package com.example.coroutinedemo.mapper

import com.example.coroutinedemo.entity.MovieEntity
import com.example.coroutinedemo.model.Movie

/**
 * @author AlexisYin
 */
class Entity2ModelMapper: Mapper<MovieEntity, Movie> {
    override fun map(input: MovieEntity): Movie =
        Movie(no = input.no, id= input.id, title = input.title, rate = input.rate, cover = input.cover)
}