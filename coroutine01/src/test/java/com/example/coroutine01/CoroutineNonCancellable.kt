package com.example.coroutine01

import kotlinx.coroutines.*
import org.junit.Test

/**
 * @author AlexisYin
 */
class CoroutineNonCancellable {
    @Test
    fun `test cancel with NonCancellable`() = runBlocking<Unit> {
        val job = launch {
            try {
                repeat(1000) {
                    println("job: I'm sleeping $it ...")
                    delay(500)
                }
            } finally {
                withContext(NonCancellable) {
                    println("job: I'm running finally")
                    delay(1000)
                    println("job: And I've just delayed for 1 sec because I'm non-cancellable")
                }
            }
        }
        delay(1000)
        println("main: I'm tired of waiting!")
        job.cancelAndJoin()
        println("main: Now I can quit.")
    }

}