package com.example.coroutinedemo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.example.coroutinedemo.databinding.MovieItemBinding
import com.example.coroutinedemo.model.Movie

/**
 * @author AlexisYin
 */
class MovieAdapter : PagingDataAdapter<Movie, BindingViewHolder>(object : DiffUtil.ItemCallback<Movie>(){
    override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem == newItem
    }
}) {
    override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {
        val movie = getItem(position)
        movie?.let {
            val binding = holder.binding as MovieItemBinding
            //使用了DataBinding，只需要赋值variable对象
            binding.movie = it
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder {
        val binding = MovieItemBinding.inflate(LayoutInflater.from(parent.context), parent, false);
        return BindingViewHolder(binding)
    }
}