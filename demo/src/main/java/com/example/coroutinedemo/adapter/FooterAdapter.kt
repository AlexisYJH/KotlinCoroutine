package com.example.coroutinedemo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.coroutinedemo.databinding.NetworkStateItemBinding

/**
 * @author AlexisYin
 */
class FooterAdapter(
    private val adapter: MovieAdapter
): LoadStateAdapter<NetworkStateItemViewHolder>() {
    override fun onBindViewHolder(holder: NetworkStateItemViewHolder, loadState: LoadState) {
        val params = holder.itemView.layoutParams
        if (params is StaggeredGridLayoutManager.LayoutParams) {
            params.isFullSpan = true
        }
        holder.bindData(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): NetworkStateItemViewHolder {
        val binding = NetworkStateItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NetworkStateItemViewHolder(binding){adapter.retry()}
    }

}