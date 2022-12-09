package com.example.coroutine01

import kotlinx.coroutines.*
import org.junit.Test
import java.lang.ArithmeticException
import java.lang.Exception
import java.lang.IndexOutOfBoundsException

/**
 * @author AlexisYin
 */
class CoroutineExceptionPropagation {
    //协程构建器有两种形式：
    //自动传播异常（launch与actor）, 异常会在第一时间被抛出
    //向用户暴露异常（async与produce），依赖用户最终消费异常，例如通过await或receive。
    @Test
    fun `test exception propagation`() = runBlocking<Unit> {
        val job = GlobalScope.launch {
            try {
                throw IndexOutOfBoundsException()
            } catch (e : Exception) {
                println("Caught IndexOutOfBoundsException")
            }
        }
        job.join()

        val deferred = GlobalScope.async {
            throw ArithmeticException()
        }
        //不调用await，不消费不会出现异常
        try {
            deferred.await()
        } catch (e : Exception) {
            println("Caught ArithmeticException")
        }
    }


    //非根协程的异常: 其他协程所创建的协程中，产生的异常总是会被传播
    @Test
    fun `test exception propagation2`() = runBlocking<Unit> {
        val scope = CoroutineScope(Job())
        val job = scope.launch {
            //非根协程，不需要消费，自动传播
            async {
                try {
                    throw IllegalAccessException()
                } catch (e : Exception) {
                    println("Caught IllegalAccessException")
                }
            }
        }
        job.join()
    }

}