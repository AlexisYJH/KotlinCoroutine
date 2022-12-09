package com.example.flowpractice.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author AlexisYin
 */
@Entity
data class User(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "name") val name : String
)
