package com.example.kotlincoroutine.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlincoroutine.R
import com.example.kotlincoroutine.api.userServiceApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * @author AlexisYin
 *
 * 0. 添加协程依赖
 * 1. api接口中添加suspend函数
 * 2. 使用GlobalScope.launch
 */
class MainActivity2 : AppCompatActivity() {

    @SuppressLint("StaticFieldLeak")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val textView = findViewById<TextView>(R.id.textview)
        textView.text = "Jack"
        val button = findViewById<Button>(R.id.button)
        //SAM: Single Abstract Method
        button.setOnClickListener {
            Log.d("TAG", "button clicked!")
            GlobalScope.launch(Dispatchers.Main) {
                val user = withContext(Dispatchers.IO) {
                    userServiceApi.getUser("JakeWharton")
                }
                textView.text = "name: ${user.name}, address: ${user.address}"
            }
        }
    }

}