package com.example.flowpractice.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flowpractice.common.Event
import com.example.flowpractice.common.LocalEventBus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * @author AlexisYin
 */
class SharedFlowViewModel: ViewModel(){
    private lateinit var job: Job

    fun startRefresh() {
        if (::job.isInitialized) {
            job.cancel()
        }
        job = viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                LocalEventBus.postEvent(Event(System.currentTimeMillis()))
            }
        }
    }

    fun stopRefresh() {
        job.cancel()
    }
}