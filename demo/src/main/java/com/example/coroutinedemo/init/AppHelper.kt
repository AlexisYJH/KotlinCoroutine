package com.example.coroutinedemo.init

import android.content.Context

/**
 * @author AlexisYin
 */
const val SERVER_URL = "http://192.168.0.104:8080/pagingserver/"

const val LOG_TAG = "COROUTINE_DEMO"

object AppHelper{
    lateinit var mContext: Context

    fun init(context: Context) {
        this.mContext = context
    }
}