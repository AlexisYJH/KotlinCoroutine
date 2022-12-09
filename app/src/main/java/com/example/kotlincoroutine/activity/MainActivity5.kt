package com.example.kotlincoroutine.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlincoroutine.R
import kotlin.coroutines.*

/**
 * @author AlexisYin
 */
class MainActivity5 : AppCompatActivity() {

    @SuppressLint("StaticFieldLeak")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //使用基础设施层创建协程：
        val continuation = suspend { // 协程体
            5
        }.createCoroutine(object : Continuation<Int> {
            override val context: CoroutineContext = EmptyCoroutineContext
            override fun resumeWith(result: Result<Int>) {
                println("Coroutine End: $result")
            }
        })
        continuation.resume(Unit)
    }

}