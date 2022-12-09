package com.example.flowpractice.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.flowpractice.adapter.UserAdapter
import com.example.flowpractice.databinding.FragmentUserBinding
import com.example.flowpractice.viewmodel.UserViewModel

/**
 * 错误1
 * Caused by: java.lang.RuntimeException: cannot find implementation for
 * https://blog.csdn.net/s_nshine/article/details/122241119
 * room编译依赖添加异常，无法自动生成java代码
 * 添加插件id 'kotlin-kapt'，将annotationProcessor改为kapt
 *
 * 错误2:
 * Type of the parameter must be a class annotated with @Entity or a collection/array of it.
 * Not sure how to handle insert method's return type.
 * 更新Room到2.4.0
 *
 * 0. 新建Fragment和处理layout
 * 1. db包，新建User, AppDataBase, UserDao
 * 2. viewmodel包，新建UserViewModel
 * 3. adapter包，新建BindingViewHolder，UserAdapter
 * 4. 在RoomFragment中，收集通过UserViewModel拿到的Flow，交给UserAdapter；
 * 点击后借由UserViewModel将输入数据存入数据库
 */
class UserFragment : Fragment() {

    private val mBinding : FragmentUserBinding by lazy {
        FragmentUserBinding.inflate(layoutInflater)
    }
    private val viewModel by viewModels<UserViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mBinding.apply {
            btnAddUser.setOnClickListener{
                viewModel.insert(etUserId.text.toString(), etUserName.text.toString())
            }
        }

        val adapter = UserAdapter()
        mBinding.recyclerView.adapter = adapter
        lifecycleScope.launchWhenCreated {
            viewModel.getAll().collect {
                adapter.setData(it)
            }
        }
    }

}