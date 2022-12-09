package com.example.jetpackpaging.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.example.jetpackpaging.adapter.MovieAdapter
import com.example.jetpackpaging.adapter.MovieLoadMoreAdapter
import com.example.jetpackpaging.databinding.ActivityMainBinding
import com.example.jetpackpaging.viewmodel.MovieViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * 0. 添加依赖及配置
 * 1. model包，Movie，Movies
 * 2. net包，RetrofitClient，MovieApi
 * 3. paging包，MoviePagingSource
 * 4. viewmodel包，MovieViewModel，Pager配置（确认PagingConfig正确），构建Flow
 * 5. adapter包，BindingViewHolder，MovieAdapter继承PagingDataAdapter
 * 6. ImageViewBindingAdapter，自定义BindingAdapter加载网络图片
 * 7. MovieLoadMoreAdapter，LoadStateFooter上拉刷新
 * 8. SwipeRefreshLayout下拉刷新
 * 9. 上游数据缓存cachedIn
 */
class MainActivity : AppCompatActivity() {
    private val mBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<MovieViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        val adapter = MovieAdapter()
        mBinding.apply {
            recycleView.adapter = adapter.withLoadStateFooter(MovieLoadMoreAdapter())
            swipeRefreshLayout.setOnRefreshListener {
                adapter.refresh()
            }
        }
        lifecycleScope.launchWhenCreated {
            viewModel.loadMovie().collectLatest {
                adapter.submitData(it)
            }
        }

        lifecycleScope.launchWhenCreated {
            //监听刷新。数据拿到了就停止刷新
            //加载的状态信息被存放在一个Flow当中
            adapter.loadStateFlow.collectLatest {state->
                mBinding.swipeRefreshLayout.isRefreshing = state.refresh is LoadState.Loading
            }
        }
    }
}