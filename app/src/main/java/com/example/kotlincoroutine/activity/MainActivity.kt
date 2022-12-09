package com.example.kotlincoroutine.activity

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.example.kotlincoroutine.R
import com.example.kotlincoroutine.api.User
import com.example.kotlincoroutine.api.userServiceApi

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val textView = findViewById<TextView>(R.id.textview)
        textView.text = "Jack"
        val button = findViewById<Button>(R.id.button)
        //SAM: Single Abstract Method
        button.setOnClickListener {
            Log.d("TAG", "button clicked!")
            //对象表达式相当于匿名内部类
            object : AsyncTask<Void, Void, User>() {
                override fun doInBackground(vararg p0: Void?): User? {
                    return userServiceApi.loadUser("Jack").execute().body()
                }

                override fun onPostExecute(result: User?) {
                    textView.text = "name: ${result?.name}, address: ${result?.address}"
                }
            }.execute()
        }
    }


}