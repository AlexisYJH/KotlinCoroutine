package com.example.coroutine01

import kotlinx.coroutines.*
import org.junit.Test
import java.lang.Exception

/**
 * @author AlexisYin
 */
class CoroutineCancel {
    //1. 取消作用域会取消它的子协程
    @Test
    fun `test scope cancel`() = runBlocking<Unit> {
        // 定义一个协程作用域
        // 子协程没有继承父协程的上下文，也就没有继承其作用域，所以父协程不会等待子协程结束，要等待使用Job的join/await
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            delay(1000)
            println("job 1")
        }
        scope.launch {
            delay(1000)
            println("job 2")
        }
        // 延迟100毫秒，防止还没执行到delay就被取消
        delay(100)
        //两个协程全部被取消
        scope.cancel()
        //父协程不会等待子协程结束：要有输出，让父协程挂起，或使用等待join/await
        delay(2000)
    }

    //2. 被取消的子协程并不会影响其余兄弟协程
    @Test
    fun `test bother cancel`() = runBlocking<Unit> {
        val scope = CoroutineScope(Dispatchers.Default)
        val job1 = scope.launch {
            delay(1000)
            println("job 1")
        }
        val job2 = scope.launch {
            delay(1000)
            println("job 2")
        }
        delay(100)
        //job1被取消，job2 没有被取消。
        job1.cancel()
        delay(2000)
    }

    //3. 协程通过抛出一个特殊的异常 CancellationException 来处理取消操作
    @Test
    fun `test CancellationException`() = runBlocking<Unit> {
        val job1 = GlobalScope.launch {
            try {
                delay(1000)
                println("job 1")
            } catch (e: Exception) {
                //JobCancellationException
                e.printStackTrace()
            }
        }
        delay(100)
        //job1.cancel(CancellationException("取消"))
        //job1.join()
        job1.cancelAndJoin()
    }

    //CPU密集型任务取消-0. 使用了while循环，执行时CPU高度运作，当协程执行密集型任务时，协程无法被取消。
    @Test
    fun `test cancel cpu task`() = runBlocking<Unit> {

        val job = launch(Dispatchers.Default) {
            var nextPrintTime = System.currentTimeMillis()
            var i = 0
            while (i < 5) {
                if (System.currentTimeMillis() >= nextPrintTime) {
                    println("job: I'm sleeping ${i++} ...")
                    nextPrintTime += 500
                }
            }
        }
        delay(1000)
        println("main: I'm tired of waiting!")
        job.cancelAndJoin()
        println("main: Now I can quit.")
    }

    //CPU密集型任务取消-1. isActive： 是一个可以被使用在 CoroutineScope 中的扩展属性，检查 Job 是否处于活跃状态
    @Test
    fun `test cancel cpu task by isActive`() = runBlocking<Unit> {
        val job = launch(Dispatchers.Default) {
            var nextPrintTime = System.currentTimeMillis()
            var i = 0
            while (i < 5 && isActive) {
                if (System.currentTimeMillis() >= nextPrintTime) {
                    println("job: I'm sleeping ${i++} ...")
                    nextPrintTime += 500
                }
            }
        }
        delay(1000)
        println("main: I'm tired of waiting!")
        job.cancelAndJoin()
        println("main: Now I can quit.")
    }


    //CPU密集型任务取消-2. ensureActive()：如果Job处于非活跃状态，则抛出异常。(利用isActive实现)
    @Test
    fun `test cancel cpu task by ensureActive`() = runBlocking<Unit> {
        val job = launch(Dispatchers.Default) {
            var nextPrintTime = System.currentTimeMillis()
            var i = 0
            while (i < 5) {
                //CancellationException，被静默处理
                ensureActive()
                if (System.currentTimeMillis() >= nextPrintTime) {
                    println("job: I'm sleeping ${i++} ...")
                    nextPrintTime += 500
                }
            }
        }
        delay(1000)
        println("main: I'm tired of waiting!")
        job.cancelAndJoin()
        println("main: Now I can quit.")
    }

    //CPU密集型任务取消-3. yield 函数会检查所在协程的状态，如果已经取消，则抛出异常。次外，它还会尝试让出线程的执行权，给其他协程协程提供执行机会。（当程序非常密集的时候使用）
    @Test
    fun `test cancel cpu task by yield`() = runBlocking<Unit> {
        val job = launch(Dispatchers.Default) {
            var nextPrintTime = System.currentTimeMillis()
            var i = 0
            while (i < 5) {
                //CancellationException，被静默处理
                yield()
                if (System.currentTimeMillis() >= nextPrintTime) {
                    println("job: I'm sleeping ${i++} ...")
                    nextPrintTime += 500
                }
            }
        }
        delay(1000)
        println("main: I'm tired of waiting!")
        job.cancelAndJoin()
        println("main: Now I can quit.")
    }
}