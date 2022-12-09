package com.example.coroutine01

import kotlinx.coroutines.*
import org.junit.Test

/**
 * @author AlexisYin
 */
class CoroutineContextTest {
    @Test
    fun `test CoroutineContext`() = runBlocking<Unit> {
        //public operator fun plus(context: CoroutineContext): CoroutineContext
        launch (Dispatchers.Default + CoroutineName("test")){
            //DefaultDispatcher-worker-1 @test#2
            println("I'm working in thread ${Thread.currentThread().name}")
        }
    }

    //每次创建协程都会有一个新的Job实例
    //剩下的元素会从CoroutineContext的父类继承
    @Test
    fun `test CoroutineContext extend`() = runBlocking<Unit> {
        val scope = CoroutineScope(Job() + Dispatchers.IO + CoroutineName("test"))
        val job = scope.launch {
            //新的协程会将CoroutineScope作为父级
            println("${coroutineContext[Job]} ${Thread.currentThread().name}")
            val result = async {
                //通过async创建的新协程会将当前协程作为父级
                println("${coroutineContext[Job]} ${Thread.currentThread().name}")
                "OK"
            }.await()
        }
        job.join()
    }

    //协程的上下文 = **默认值 + 继承的CoroutineContext  + 参数**
    @Test
    fun `test CoroutineContext extend2`() = runBlocking<Unit> {
        val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
            println("Caught $exception")
        }
        val scope = CoroutineScope(Job() + Dispatchers.Main + coroutineExceptionHandler)
        scope.launch(Dispatchers.IO) {
            //协程的调度器是：Dispatchers.IO
            //每次创建协程都会有一个新的Job实例
            //协程的名字来自于默认值为coroutine
            //协程的异常处理器来自于CoroutineScope：coroutineExceptionHandler
        }
    }
}