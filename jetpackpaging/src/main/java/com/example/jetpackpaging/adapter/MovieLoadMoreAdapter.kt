package com.example.jetpackpaging.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.example.jetpackpaging.databinding.LoadmoreBinding

/**
 * @author AlexisYin
 */
class MovieLoadMoreAdapter: LoadStateAdapter<BindingViewHolder>() {
    override fun onBindViewHolder(holder: BindingViewHolder, loadState: LoadState) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): BindingViewHolder {
        val binding = LoadmoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BindingViewHolder(binding)
    }
}