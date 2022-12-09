package com.example.kotlincoroutine.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlincoroutine.R
import com.example.kotlincoroutine.api.User
import com.example.kotlincoroutine.api.userServiceApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * @author AlexisYin
 */
class MainActivity3 : AppCompatActivity() {

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
            GlobalScope.launch(Dispatchers.Main) {
                getUser()
            }
        }
    }

    /**
     * 堆栈帧中的函数调用流程
     * 1. getUser()进入主线程的堆栈帧中
     * 2. getUser()挂起，get()进入主线程的堆栈帧中
     * 3. get()挂起
     * 4. 耗时任务结束，get()恢复，再次回到主线程的堆栈帧中
     * 5. 对user进行赋值，赋值完成，get执行结束，从主线程的堆栈帧中移除
     * 6. 执行show(user)，由于show是getUser()的一部分，getUser()重新回到主线程的堆栈帧中
     */
    private suspend fun getUser() {
        val user = get()
        show(user)
    }

    private fun show(user: User) {
        textView?.text = "name: ${user.name}, address: ${user.address}"
    }

    private suspend fun get(): User = withContext(Dispatchers.IO) {
        userServiceApi.getUser("JakeWharton")
    }

}