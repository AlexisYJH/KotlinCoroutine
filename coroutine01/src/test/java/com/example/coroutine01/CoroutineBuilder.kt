package com.example.coroutine01

import kotlinx.coroutines.*
import org.junit.Test
import kotlin.system.measureTimeMillis
import kotlin.time.measureTime

/**
 * @author AlexisYin
 *
 * 0. 添加协程依赖
 *
 *
 */
class CoroutineBuilder {
    @Test
    fun `test coroutine builder`() = runBlocking {
        val job1 = launch {
            delay(400)
            println("job1 finished")
        }

        val job2 = async {
            delay(200)
            println("job2 finished")
            "job2 result"
        }
        println(job2.await())
    }

    @Test
    fun `test coroutine join`() = runBlocking {
        val job1 = launch {
            delay(200)
            println("1")
        }
        job1.join()

        val job2 = launch {
            delay(200)
            println("2")
        }

        val job3 = launch {
            delay(200)
            println("3")
        }
    }

    @Test
    fun `test coroutine await`() = runBlocking {
        val job1 = async {
            delay(200)
            println("1")
        }
        job1.await()

        val job2 = async {
            delay(200)
            println("2")
        }

        val job3 = async {
            delay(200)
            println("3")
        }
    }

    @Test
    fun `test sync`() = runBlocking {
        val time = measureTimeMillis {
            val one = one()
            val two = two()
            println("result: ${one + two}")
        }
        println(time) //2033
    }

    @Test
    fun `test combine async`() = runBlocking {
        val time = measureTimeMillis {
            val one = async { one() }
            val two = async { two() }
            println("result: ${one.await() + two.await()}")
        }
        println(time) //1026
    }

    @Test
    fun `test combine async error`() = runBlocking {
        val time = measureTimeMillis {
            val one = async { one() }.await()
            val two = async { two() }.await()
            println("result: ${one + two}")
        }
        println(time) //2026
    }

    suspend fun one() :Int {
        delay(1000)
        return 1
    }

    suspend fun two():Int {
        delay(1000)
        return 2
    }
}
