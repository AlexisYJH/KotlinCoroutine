package com.example.coroutine01

import kotlinx.coroutines.*
import org.junit.Test
import java.io.IOException
import java.lang.ArithmeticException
import java.lang.IndexOutOfBoundsException

/**
 * @author AlexisYin
 */
class CoroutineExceptionAggregation {
    // 当协程的多个子协程因为异常而失败时，一般情况下取第一个异常进行处理。
    // 在第一个异常之后发生的所有其他异常，都将**绑定到第一个异常之上**。
    @Test
    fun `test exception aggregation`() = runBlocking {
        val handler = CoroutineExceptionHandler { _, exception ->
            //Caught java.io.IOException [java.lang.ArithmeticException, java.lang.IndexOutOfBoundsException]
            //数组：public final synchronized Throwable[] getSuppressed() {
            println("Caught $exception ${exception.suppressed.contentToString()}")
        }
        val job = GlobalScope.launch(handler) {
            launch {
                try {
                    delay(Long.MAX_VALUE)
                } finally {
                    throw ArithmeticException()//2
                }
            }
            launch {
                try {
                    delay(Long.MAX_VALUE)
                } finally {
                    throw IndexOutOfBoundsException()//3
                }
            }
            launch {
                delay(100)
                throw IOException()//1
            }
        }
        job.join()
    }
}