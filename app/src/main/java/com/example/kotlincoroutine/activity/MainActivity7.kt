package com.example.kotlincoroutine.activity

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.kotlincoroutine.R
import com.example.kotlincoroutine.databinding.ActivityMainBinding
import com.example.kotlincoroutine.viewmodel.MainViewModel
import kotlinx.coroutines.*
import kotlin.coroutines.*

/**
 * @author AlexisYin
 *
 * 协程+Retrofit+ViewModel+Livedata+DataBinding
 * 1. 添加DataBinding支持，修改布局文件
 * 2. 创建MainViewModel，数据和布局绑定
 * 3. 添加依赖activity-ktx，在Activity中使用 by viewModels()初始化ViewModel
 * 4. 创建UserRepository
 * 5. MainViewModel持有UserRepository，在MainViewModel的协程中通过UserRepository拿到数据交给MutableLiveData
 */
class MainActivity7 : AppCompatActivity(){
    private var textView: TextView? = null

    //添加依赖activity-ktx 或fragment-ktx就可以在Activity或Fragment中使用 by viewModels()初始化ViewModel。
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.viewModel = mainViewModel
        binding.lifecycleOwner = this
        binding.button.setOnClickListener {
            mainViewModel.getUser("xxx")
        }
    }

}