package com.example.flowpractice.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.flowpractice.R
import com.example.flowpractice.databinding.FragmentNumberBinding
import com.example.flowpractice.databinding.FragmentSharedFlowBinding
import com.example.flowpractice.viewmodel.NumberViewModel
import com.example.flowpractice.viewmodel.SharedFlowViewModel

/**
 * 0. 新建Fragment和处理layout
 * 1. 新建LocalEventBus类
 * 2. 新建SharedFlowViewModel
 * 3. 在SharedFlowFragment中，借SharedFlowViewModel在发射和取消行程
 * 4. 在TextFragment中，收集MutableSharedFlow更新UI
 */
class SharedFlowFragment : Fragment() {
    private val mBinding : FragmentSharedFlowBinding by lazy {
        FragmentSharedFlowBinding.inflate(layoutInflater)
    }

    private val viewModel by viewModels<SharedFlowViewModel>()

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
            btnStart.setOnClickListener {
                viewModel.startRefresh()
            }
            btnStop.setOnClickListener {
                viewModel.stopRefresh()
            }
        }
    }
}