package com.example.coroutinedemo.mapper

/**
 * @author AlexisYin
 */
interface Mapper<I, O> {
    fun map(input: I) : O
}