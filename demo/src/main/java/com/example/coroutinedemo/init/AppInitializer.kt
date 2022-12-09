package com.example.coroutinedemo.init

import android.content.Context
import androidx.startup.Initializer

/**
 * @author AlexisYin
 */
class AppInitializer: Initializer<Unit> {
    override fun create(context: Context) {
        AppHelper.init(context)
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> = mutableListOf()
}