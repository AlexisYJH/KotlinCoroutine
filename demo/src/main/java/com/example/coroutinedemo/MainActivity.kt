package com.example.coroutinedemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.example.coroutinedemo.adapter.MovieAdapter
import com.example.coroutinedemo.adapter.FooterAdapter
import com.example.coroutinedemo.databinding.ActivityMainBinding
import com.example.coroutinedemo.viewmodel.MovieViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

/**
 * 0. 引入依赖，准备MainActivity和布局，清单文件
 *
 * 1. Hilt注入网络相关对象
 * 1）init包，新建DemoApp注解@HiltAndroidApp，配置到清单文件
 * 2）MainActivity注解@AndroidEntryPoint
 * 3) di包，NetWorkModule注解@Module
 * 4） model包，Movie
 * 5）remote包，新建MovieService接口，在NetWorkModule提供对应实例方法
 *
 * 2. Hilt注入Room相关对象
 * 1）entity包，MovieEntity
 * 2）db包, MovieDao, AppDatabase
 * 3）di包，RoomModule
 *
 * 3. Pager配置
 * 1）remote包，MovieRemoteMediator继承RemoteMediator
 * 2）repository包，Repository接口（获取Movie作为PagingData的Flow），MovieRepositoryImpl
 * 3）mapper包，Mapper接口，Entity2ModelMapper实现MovieEntity到Movie转换
 * MovieRepositoryImpl中完成以下三个步骤连接
 * - 请求网络数据，放入数据库：RemoteMediator
 * - 从数据库拿到数据：Dao
 * - 对数据进行转换，给到UI显示: Mapper
 *
 * 4. ViewModel
 * 1）viewmodel包，MovieViewModel，持有Repository实例，借Repository拿到PagingData的Flow，调用asLiveData()完成收集和转换LiveData
 * 2）di包，RepositoryModule，注入到MovieViewModel
 * 3）MainActivity中处理MovieViewModel，观察livedata，刷新列表
 *
 * 5. BindingAdapter与Coil
 * 准备item布局，binding包，新建ViewBinding，实现绑定
 *
 * 6. PagingDataAdapter
 * 1）adapter包，BindingViewHolder，MovieAdapter
 * 2）MainActivity中处理MovieAdapter
 *
 * 7. LoadStateFooter上拉刷新
 *  准备item布局，adapter包，NetworkStateItemViewHolder, FooterAdapter，添加到MovieAdapter
 *
 * 8. SwipeRefreshLayout下拉刷新
 *
 * 9. App Startup
 *  init包，AppInitializer，清单文件中配置Provider
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val viewModel : MovieViewModel by viewModels()

    private val adapter: MovieAdapter by lazy {
        MovieAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.recyclerView.adapter = adapter.withLoadStateFooter(FooterAdapter(adapter))
        viewModel.data.observe(this) {
            //submitData是suspend，需要协程，传入lifecycle会使用协程处理
            adapter.submitData(lifecycle, it)
            binding.swipeRefreshLayout.isEnabled = false
        }
        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest { state->
                binding.swipeRefreshLayout.isRefreshing = state.refresh is LoadState.Loading
            }
        }
    }
}