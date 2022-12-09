package com.example.coroutinedemo.binding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load
import com.example.coroutinedemo.R

/**
 * @author AlexisYin
 */
//写在文件中，函数名和注解值一致，编译后是静态方法
@BindingAdapter("image")
fun image(imageView: ImageView, url: String) {
    //Coil
    imageView.load(url){
        crossfade(true)
        placeholder(R.drawable.ic_photo)
    }
}
