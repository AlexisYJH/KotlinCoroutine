package com.example.jetpackpaging.model

import com.google.gson.annotations.SerializedName

/**
 * @author AlexisYin
 */
data class Movies(
    @SerializedName("subjects")
    val movieList: List<Movie>,
    @SerializedName("has_more")
    val hasMore: Boolean
)
