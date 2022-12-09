package com.example.coroutine01

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * 报错“Cannot access androidx.lifecycle.HasDefaultViewModelProviderFactory” 可正常运行
 * 添加库：implementation 'androidx.lifecycle:lifecycle-extensions:2.2.0'
 * 或者更新androidx.appcompat:appcompat: implementation 'androidx.appcompat:appcompat:1.3.0-alpha02'
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val handler = CoroutineExceptionHandler { _, exception ->
            println("Caught $exception")
        }

        findViewById<Button>(R.id.button).setOnClickListener {
            //不指定CoroutineExceptionHandler时，报错闪退
            //异常捕获阻止应用闪退
            GlobalScope.launch(handler) {
                Log.d("TAG", "click button")
                "abc".substring(10)
            }
        }
    }
}