package com.example.coroutine01

import kotlinx.coroutines.*
import org.junit.Test
import java.lang.ArithmeticException

/**
 * @author AlexisYin
 */
class CoroutineCancelAndException {
    //协程内部使用 CancellationException 来进行取消，这个异常会被忽略。
    //当子协程被取消时，不会取消它的父协程
    @Test
    fun `test cancel and exception`() = runBlocking {
        val job = launch {
            val child = launch {
                try {
                    try {
                        delay(Long.MAX_VALUE)
                    } catch (e: Exception) {
                        //Caught kotlinx.coroutines.JobCancellationException
                        println("Caught $e")
                    }
                } finally {
                    println("child is cancelled")
                }
            }
            yield()//使用yield来给我们的子作业一个机会去执行
            println("Cancelling child")
            child.cancelAndJoin()
            yield()
            println("Parent is not cancelled")
        }
        job.join()
    }


    // 如果一个协程遇到了 CancellationException 以外的异常，它将使用该异常取消它的父协程。
    // 当父协程的所有子协程都结束后，异常才会被父协程处理。
    @Test
    fun `test cancel and exception2`() = runBlocking {
        val handler = CoroutineExceptionHandler { _, exception ->
            println("Caught $exception")
        }
        val job = GlobalScope.launch(handler) {
            launch {
                try {
                    delay(Long.MAX_VALUE)
                } finally {
                    withContext(NonCancellable) {
                        println("Children are cancelled, but exception s not handled until all children terminate")
                        delay(100)
                        println("The first child finished in non cancellable block")
                    }
                }
            }
            launch {
                delay(10)
                println("The second child throw an exception")
                throw ArithmeticException()
            }
        }
        job.join()
    }
}