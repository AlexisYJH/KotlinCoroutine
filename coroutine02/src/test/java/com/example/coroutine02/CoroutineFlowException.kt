package com.example.coroutine02

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.junit.Test

/**
 * @author AlexisYin
 */
class CoroutineFlowException {
    fun simpleFlow() = flow<Int> {
        for (i in 1..3) {
            println("Emitting $i")
            emit(i)
        }
    }

    //收集元素的时候抛出一个异常，通过try 与 catch块捕获了下游的异常。
    @Test
    fun `test flow exception`() = runBlocking<Unit> {
        try {
            simpleFlow().collect { value ->
                println(value)
                //抛一个异常,"Collected $value"是异常信息
                check(value <= 1) { "Collected $value" }
            }
        } catch (e: Throwable) {
            println("Caught $e")
        }
    }

    //构建时候抛出的异常，通过catch函数捕获了上游的异常。
    @Test
    fun `test flow exception2`() = runBlocking<Unit> {
        flow {
            emit(1)
            throw ArithmeticException("Div 0")
        }.catch { e: Throwable ->
            println("Caught $e")
            emit(10)//在异常中恢复
        }.flowOn(Dispatchers.IO).collect { println(it) }
    }


    fun simpleFlow2() = (1..3).asFlow()
    @Test
    fun `test flow complete in finally`() = runBlocking<Unit> {
        try {
            simpleFlow2().collect { println(it) }
        } finally {
            println("Done")
        }
    }

    fun simpleFlow3() = flow<Int> {
        emit(1)
        throw RuntimeException()
    }

    @Test
    fun `test flow complete in onCompletion`() = runBlocking<Unit> {
        simpleFlow2()
            .onCompletion { println("Done") }
            .collect { println(it) }
    }

    @Test
    fun `test flow complete in onCompletion2`() = runBlocking<Unit> {
        simpleFlow3()
            .onCompletion { exception ->
                if (exception != null) println("Flow completed exceptionally: $exception")
            }
            .catch { exception -> println("上游：Caught $exception") }
            .collect { value -> println(value) }
    }

    @Test
    fun `test flow complete in onCompletion3`() = runBlocking<Unit> {
        try {
            simpleFlow2()
                .onCompletion { exception ->
                    if (exception != null) println("Flow completed exceptionally: $exception")
                }
                .catch { exception -> println("上游：Caught $exception") }
                .collect { value ->
                    println(value)
                    //抛一个异常
                    check(value <= 1) { "Collected $value" }
                }
        } catch (e: Throwable) {
            println("下游：Caught $e")
        }
    }

}