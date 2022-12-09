package com.example.flowpractice.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.flowpractice.R
import com.example.flowpractice.databinding.FragmentDownloadBinding
import com.example.flowpractice.databinding.FragmentHomeBinding
import com.example.flowpractice.download.DownloadManager
import com.example.flowpractice.download.DownloadStatus
import java.io.File

/**
 * 0. 添加依赖，新建Fragment和处理layout
 * 1. 新建密封类DownloadStatus
 * 2. 新建单例类DownloadManager，用于构建Flow
 * 3. 在DownloadFragment中收集Flow，更新UI
 */
class DownloadFragment : Fragment() {
    val URl ="http://192.168.0.104:8080/kotlinstudyserver/pic.jpg"
    private val mBinding : FragmentDownloadBinding by lazy {
        FragmentDownloadBinding.inflate(layoutInflater)
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
            //context可能为空，使用apply操作符
            context?.apply {
                //sdcard/Android/data/pkg/files/pic.jpg
                var file = File(getExternalFilesDir(null)?.path, "pic.jpg")

                //collect会引发下载，collect是挂起函数，必须写在协程里面
                DownloadManager.download(URl, file).collect{ status ->
                    when(status) {
                        is DownloadStatus.Progress -> {
                            mBinding.apply {
                                progressBar.progress = status.value
                                tvProgress.text = "${status.value}%"
                            }
                        }
                        is DownloadStatus.Error -> {
                            Toast.makeText(context, "下载错误", Toast.LENGTH_SHORT).show()
                        }
                        is DownloadStatus.Done -> {
                            mBinding.apply {
                                progressBar.progress = 100
                                tvProgress.text = "100%"
                            }
                            Toast.makeText(context, "下载完成", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Log.d("TAG", "下载失败")
                        }
                    }
                }
            }
        }


    }
}