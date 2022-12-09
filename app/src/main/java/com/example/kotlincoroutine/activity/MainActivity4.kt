package com.example.kotlincoroutine.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlincoroutine.R

/**
 * @author AlexisYin
 */
class MainActivity4 : AppCompatActivity() {

    private var textView: TextView? = null

    @SuppressLint("StaticFieldLeak")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById<TextView>(R.id.textview)
        textView?.text = "Jack"
        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            Log.d("TAG", "button clicked!")
            /*GlobalScope.launch(Dispatchers.Main) {
                //挂起
                //按钮点击后弹起
                delay(10000)
                Log.d("TAG", "${Thread.currentThread().name} after delay")

            }*/
            //阻塞
            //按钮点击后没有弹起
            //Choreographer: Skipped 600 frames!  The application may be doing too much work on its main thread.
            Thread.sleep(10000)
            Log.d("TAG", "${Thread.currentThread().name} after sleep")
        }
    }
}