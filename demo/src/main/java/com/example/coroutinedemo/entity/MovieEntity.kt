package com.example.coroutinedemo.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author AlexisYin
 */
@Entity
data class MovieEntity(
    @PrimaryKey
    val no: Int,
    //！！！多花半天找问题
    //不要用作主键，若为主键查询时会默认按照id的升序或降序查找，那么就会和存放顺序不一样，会导致报错，另外增加主键
    val id: Int,
    val title: String,
    val rate: String,
    val cover: String,
    val page: Int = 0 //页码
)
