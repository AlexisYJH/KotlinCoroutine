package com.example.coroutine01

import kotlinx.coroutines.*
import org.junit.Test
import java.lang.Exception
import java.lang.IllegalArgumentException

/**
 * @author AlexisYin
 */
class `CoroutineSupervisorJobOrScope` {
    // 使用 SupervisorJob 时，一个子协程的运行失败不会影响其他子协程。
    // SupervisorJob不会传播异常给它的父级，它会让子协程自己处理异常。
    @Test
    fun `test SupervisorJob`() = runBlocking<Unit> {
        val supervisor = CoroutineScope(SupervisorJob())
        val job1 = supervisor.launch {
            delay(100)
            println("child 1")
            throw IllegalArgumentException()
        }
        val job2 = supervisor.launch {
            try {
                delay(1000)
            } finally {
                println("child 2 finished")
            }
        }
        delay(200)
        supervisor.cancel()
        joinAll(job1, job2)
        //job1 抛出异常，job2 正常执行
    }

    //job1 抛出异常，job2 正常执行
    @Test
    fun `test supervisorScope`() = runBlocking<Unit> {
        supervisorScope {
            val job1 = launch {
                delay(100)
                println("child 1")
                throw IllegalArgumentException()
            }
            val job2 = launch {
                try {
                    delay(1000)
                } finally {
                    println("child 2 finished")
                }
            }
            joinAll(job1, job2)
        }
    }

    //当作业自身执行失败时，所有子作业会被全部取消
    @Test
    fun `test supervisorScope2`() = runBlocking<Unit> {
        try {
            supervisorScope {
                launch {
                    try {
                        println("child is sleeping")
                        delay(Long.MAX_VALUE)
                    } finally {
                        println("child is cancelled")
                    }
                }
                yield() //使用yield来给我们的子作业一个机会执行打印
                println("Throwing ann exception from the scope")
                throw IllegalArgumentException()
            }
        } catch (e :Exception) {
            println("Caught IllegalArgumentException")
        }
    }
}