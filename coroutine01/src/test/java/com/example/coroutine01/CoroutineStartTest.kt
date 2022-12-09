package com.example.coroutine01

import kotlinx.coroutines.*
import org.junit.Test

/**
 * @author AlexisYin
 */
class CoroutineStartTest {
    //DEFAULT和ATOMIC：协程创建后，立即开始调度（立即调度不代表立即执行，可能需要等待）
    @Test
    fun `test start mode default`() = runBlocking {
        val job = launch(start = CoroutineStart.DEFAULT) {
            print("不一定会打印-->在调度前如果协程被取消，其将直接进入取消响应的状态")//执行了就有输出
            delay(3000)
            println("Job Finished.")//没有输出
        }
        delay(1000)
        job.cancel()
    }

    @Test
    fun `test start mode atomic`() = runBlocking {
        val job = launch(start = CoroutineStart.ATOMIC) {
            println("一定会打印-->协程执行到第一个挂起点之前不响应取消")//有输出
            //如果没有挂起，则取消失效，也没有必要使用协程
            delay(3000)
            println("Job Finished.")//没有输出
        }
        job.cancel()
    }

    @Test
    fun `test start mode lazy`() = runBlocking {
        val job = launch(start = CoroutineStart.LAZY) {
            println("不一定会打印-->主动调用协程的start、join或者await等函数时才会开始调度，如果调度前就被取消，那么该协程将直接进入异常结束状态")//满足条件输出
            delay(3000)
            println("Job Finished.")//有输出
        }
        println("执行其他操作")
        //只能在调度前就被取消，开始调用后无法取消
        job.cancel()
        job.join()
    }

    @Test
    fun `test start mode undispatched`() = runBlocking {
        //在使用Dispatchers.IO时，如果让协程任务仍然运行在当前线程：指定启动模式为不分发UNDISPATCHED
        val job = async (context = Dispatchers.IO, start = CoroutineStart.UNDISPATCHED) {
            //DEFAULT -> DefaultDispatcher-worker-1 @coroutine#2 从线程池中取的一个线程
            //UNDISPATCHED（不分发） -> Test worker @coroutine#2 当前线程
            println("thread: ${Thread.currentThread().name}")
            println("一定会打印-->协程创建后立即在当前函数调用栈中执行，直到遇到遇到一个真正被挂起的点")//有输出
            delay(3000)
            println("Job Finished.")//没有输出
        }
        job.cancel()
    }
}