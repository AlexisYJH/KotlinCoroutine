package com.example.coroutinedemo.ext

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

/**
 * @author AlexisYin
 */
fun Context.isConnectedNetwork() : Boolean = run {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
    activeNetwork?.isConnectedOrConnecting == true
}