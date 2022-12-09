package com.example.coroutine01

import kotlinx.coroutines.*
import org.junit.Test
import java.lang.ArithmeticException
import java.lang.AssertionError

/**
 * @author AlexisYin
 */
class CoroutineExceptionCatch {
    //时机：异常是被自动抛出异常的协程所抛出，使用 launch，而不是 async 时；
    @Test
    fun `test CoroutineExceptionHandler time`() = runBlocking<Unit> {
        val handler = CoroutineExceptionHandler { _, exception ->
            println("Caught $exception")
        }
        //此处位置是根协程
        //launch可捕获
        val job = GlobalScope.launch(handler) {
            throw AssertionError()
        }
        //async无法捕获
        val deferred = GlobalScope.async(handler) {
            throw ArithmeticException()
        }
        job.join()
        deferred.await()
    }

    //位置： 在CoroutineScope的CoroutineContext中或在一个根协程（CoroutineScope 或者 supervisorScope 的直接子协程）中。
    //GlobalScope和SupervisorCoroutine都是CoroutineScope的子类
    //位置-1.handler在CoroutineScope的CoroutineContext中，可捕获
    @Test
    fun `test CoroutineExceptionHandler position`() = runBlocking<Unit> {
        val handler = CoroutineExceptionHandler { _, exception ->
            println("Caught $exception")
        }
        val scope = CoroutineScope(Job() + handler)
        val job = scope.launch() {
            launch {
                throw AssertionError()
            }
        }
        job.join()
    }

    //位置-2.根协程，handler在CoroutineScope的直接子协程中，可捕获
    @Test
    fun `test CoroutineExceptionHandler position2`() = runBlocking<Unit> {
        val handler = CoroutineExceptionHandler { _, exception ->
            println("Caught $exception")
        }
        val scope = CoroutineScope(Job())
        val job = scope.launch(handler) {
            //异常抛给父协程，被handler捕获
            launch {
                throw AssertionError()
            }
        }
        job.join()
    }

    //位置-2.根协程，handler在supervisorScope的直接子协程中，可捕获
    @Test
    fun `test CoroutineExceptionHandler position3`() = runBlocking<Unit> {
        val handler = CoroutineExceptionHandler { _, exception ->
            println("Caught $exception")
        }
        val job = supervisorScope {
            launch(handler) {
                throw AssertionError()
            }
        }
        job.join()
    }

    //异常处理器要安装到外部协程上，不能安装在内部协程
    //错误位置：非直接子协程（直接子协程的子协程），无法捕获
    @Test
    fun `test CoroutineExceptionHandler wrong position`() = runBlocking<Unit> {
        val handler = CoroutineExceptionHandler { _, exception ->
            println("Caught $exception")
        }
        val scope = CoroutineScope(Job())
        //无法捕获
        val job = scope.launch{
            //异常处理器要安装到外部协程上，不能安装在内部协程，只能在外部才能被捕获
            launch(handler)  {
                throw AssertionError()
            }
        }
        job.join()
    }
}