package com.example.coroutinedemo.adapter

import android.view.View
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.example.coroutinedemo.databinding.NetworkStateItemBinding

/**
 * @author AlexisYin
 */
class NetworkStateItemViewHolder(
    private val binding: NetworkStateItemBinding,
    val retryCallback: () -> Unit
): RecyclerView.ViewHolder(binding.root) {

    fun bindData(loadState: LoadState) {
        binding.apply {
            //正在加载，显示进程条

            progress.isVisible = loadState is LoadState.Loading
            //加载失败，显示并点击重试按钮
            btnRetry.isVisible = loadState is LoadState.Error
            btnRetry.setOnClickListener {
                retryCallback()
            }
            //加载失败显示错误原因
            tvErrorMsg.isVisible = !(loadState as? LoadState.Error)?.error?.message.isNullOrBlank()
            tvErrorMsg.text = (loadState as? LoadState.Error)?.error?.message
        }
    }
}

inline var View.isVisible: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }