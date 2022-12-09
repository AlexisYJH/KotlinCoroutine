package com.example.coroutine01

import kotlinx.coroutines.*
import org.junit.Test

/**
 * @author AlexisYin
 */
class CoroutineScopeBuilder {

    @Test
    fun `test coroutine scope builder`() = runBlocking {
        //runBlocking 是常规函数，而 coroutineScope 是挂起函数
        //coroutineScope 一个协程失败了，所有其它兄弟协程也会被取消
        coroutineScope {
            val job1 = launch {
                delay(400)
                println("job1 finished")
            }

            val job2 = async {
                delay(200)
                println("job2 finished")
                "job2 result"
                throw IllegalAccessException()
            }
        }
    }

    @Test
    fun `test supervisor scope builder`() = runBlocking {
        // supervisorScope 一个协程失败了，不会影响其它兄弟协程
        supervisorScope {
            val job1 = launch {
                delay(400)
                println("job1 finished")
            }

            val job2 = async {
                delay(200)
                println("job2 finished")
                "job2 result"
                throw IllegalAccessException()
            }
        }
    }
}