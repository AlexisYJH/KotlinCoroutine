package com.example.jetpackpaging.adapter

import android.graphics.Color
import android.text.TextUtils
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.example.jetpackpaging.R
import com.squareup.picasso.Picasso

/**
 * @author AlexisYin
 */
class ImageViewBindingAdapter {
    //必须是静态方法才能完成绑定，java调用Kotlin静态方法
    //需要两步，第一步使用companion object，第二步使用@JvmStatic注解
    companion object{
        @JvmStatic
        @BindingAdapter("image")
        fun setImage(imageView: ImageView, url: String) {
            if (TextUtils.isEmpty(url)) {
                imageView.setBackgroundColor(Color.GRAY)
            } else {
                Picasso.get().load(url).placeholder(R.drawable.ic_launcher_background).into(imageView)
            }
        }
    }
}