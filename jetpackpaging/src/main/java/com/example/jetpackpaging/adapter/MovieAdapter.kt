package com.example.jetpackpaging.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.example.jetpackpaging.databinding.ItemBinding
import com.example.jetpackpaging.model.Movie

/**
 * @author AlexisYin
 */
class MovieAdapter : PagingDataAdapter<Movie, BindingViewHolder>(object : DiffUtil.ItemCallback<Movie>(){
    override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        //Movie是的data数据类，会重写比较内容的方法。比较的是Movie的属性值是否都相等
        // kotlin  === 引用 ， == 内容
        // Java == 引用， equals 内容
        return oldItem == newItem
    }

}){
    override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {
        val movie = getItem(position)
        movie?.let {
            val binding = holder.binding as ItemBinding
            binding.movie = it
            binding.networkImage = it.cover
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder {
        val binding = ItemBinding.inflate(LayoutInflater.from(parent.context), parent, false);
        return BindingViewHolder(binding)
    }
}