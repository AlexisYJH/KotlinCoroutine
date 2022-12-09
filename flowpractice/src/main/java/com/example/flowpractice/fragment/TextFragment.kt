package com.example.flowpractice.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.flowpractice.R
import com.example.flowpractice.common.LocalEventBus
import com.example.flowpractice.databinding.FragmentDownloadBinding
import com.example.flowpractice.databinding.FragmentTextBinding

class TextFragment : Fragment() {
    private val mBinding : FragmentTextBinding by lazy {
        FragmentTextBinding.inflate(layoutInflater)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        lifecycleScope.launchWhenCreated {
            LocalEventBus.events.collect{
                mBinding.tvTime.text = it.timestamp.toString()
            }
        }
    }
}