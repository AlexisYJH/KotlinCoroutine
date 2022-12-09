package com.example.kotlincoroutine.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlincoroutine.R
import com.example.kotlincoroutine.api.userServiceApi
import kotlinx.coroutines.*
import java.lang.Exception
import kotlin.coroutines.*

/**
 * @author AlexisYin
 */
class MainActivity6 : AppCompatActivity(), CoroutineScope by MainScope(){
    //private val mainScope = MainScope()
    private var textView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById<TextView>(R.id.textview)
        textView?.text = "Jack"
        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            /*mainScope.launch {
                //val user = userServiceApi.getUser("MainScope")
                //textView?.text = "name: ${user.name}, address: ${user.address}"
                try {
                    delay(10000)
                } catch (e: Exception) {
                    //kotlinx.coroutines.JobCancellationException: Job was cancelled;
                    println(e)
                }
            }*/
            launch {
                val user = userServiceApi.getUser("MainScope")
                textView?.text = "name: ${user.name}, address: ${user.address}"
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //mainScope.cancel()
        cancel()
    }

}