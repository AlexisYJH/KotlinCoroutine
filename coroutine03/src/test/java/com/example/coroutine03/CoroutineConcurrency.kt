package com.example.coroutine03

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author AlexisYin
 */
class CoroutineConcurrency {
    @Test
    fun `test not safe concurrent`() = runBlocking<Unit> {
        var count = 0
        List(1000) {//启动1000个协程
            GlobalScope.launch { count++ }
        }.joinAll()
        //count++不是原子性操作，所以count最后的值不是1000，而且多次运行每次打印的数也不相同
        println(count)
    }

    //可以使用java自己的API解决
    @Test
    fun `test safe concurrent`() = runBlocking<Unit> {
        var count = AtomicInteger(0)//变成原子性操作
        List(1000) {
            //这里不再是count++
            GlobalScope.launch { count.incrementAndGet() }
        }.joinAll()
        //每次输出都是1000
        println(count.get())
    }

    @Test
    fun `test safe concurrent tools`() = runBlocking<Unit> {
        var count = 0
        val mutex = Mutex()
        List(1000) {
            GlobalScope.launch {
                //加锁，是一个挂起函数
                mutex.withLock {
                    count++
                }
            }
        }.joinAll()
        println(count)
    }

    @Test
    fun `test safe concurrent tools2`() = runBlocking<Unit> {
        var count = 0
        val semaphore = Semaphore(1)//信号
        List(1000) {
            GlobalScope.launch {
                semaphore.withPermit {
                    count++
                }
            }
        }.joinAll()
        println(count)
    }

    @Test
    fun `test avoid access outer variable`() = runBlocking<Unit> {
        var count = 0
        //count在协程外部，就不存在并发安全的操作了。
        val result = count + List(1000){
            GlobalScope.async { 1 }
        }.map { it.await() }.sum()
        println(result)
    }
}