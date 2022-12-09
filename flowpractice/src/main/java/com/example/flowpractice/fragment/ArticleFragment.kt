package com.example.flowpractice.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.flowpractice.adapter.ArticleAdapter
import com.example.flowpractice.databinding.FragmentArticleBinding
import com.example.flowpractice.viewmodel.ArticleViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect

/**
 * 0. 新建Fragment和处理layout
 * 1. model包，新建Article
 * 2. net包，新建ArticleApi接口，RetrofitClient类
 * 3. 新建ArticleViewModel
 * 4. 新建ArticleAdapter
 * 5. 在ArticleFragment中，将输入的文字构建为Flow, 收集转交给ArticleViewModel发起网络请求，
 * ArticleViewModel收集网络响应构建的Flow，将结果转给LiveData, 继而通知ArticleAdapter更新数据
 */
class ArticleFragment : Fragment() {

    private val mBinding : FragmentArticleBinding by lazy {
        FragmentArticleBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<ArticleViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return mBinding.root
    }

    //获取关键字，TextView增加一个扩展函数。
    //callbackFlow会返回一个Flow
    private fun TextView.textWatcherFlow(): Flow<String> = callbackFlow {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {
                //往flow里面添加元素
                trySend(s.toString())
            }

        }
        addTextChangedListener(textWatcher)
        //flow关闭的时候移除监听
        awaitClose { removeTextChangedListener(textWatcher) }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        lifecycleScope.launchWhenCreated {
            mBinding.etSearch.textWatcherFlow().collect{
                Log.d("TAG", "collect keywords: $it")
                viewModel.searchArticles(it)
            }
        }
        val adapter = ArticleAdapter()
        mBinding.recyclerView.adapter = adapter
        viewModel.articles.observe(viewLifecycleOwner){ articles ->
            adapter.setData(articles)
        }
    }
}