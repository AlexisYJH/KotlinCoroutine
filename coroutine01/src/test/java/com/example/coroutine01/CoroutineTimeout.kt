package com.example.coroutine01

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.Test

/**
 * @author AlexisYin
 */
class CoroutineTimeout {
    @Test
    fun `test deal with timeout`() = runBlocking<Unit> {
        //超时抛出异常TimeoutCancellationException
        withTimeout(1000) {
            repeat(1000) {
                println("job: I'm sleeping $it ...")
                delay(500)
            }
        }
    }

    @Test
    fun `test deal with timeout return null`() = runBlocking<Unit> {
        //超时返回null
        val result = withTimeoutOrNull(1000) {
            repeat(1000) {
                println("job: I'm sleeping $it ...")
                delay(500)
            }
            "Done"
        } ?: "Timeout"
        println("Result is $result")
    }
}