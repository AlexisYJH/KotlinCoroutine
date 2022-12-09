package com.example.coroutine01

import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.io.BufferedReader
import java.io.FileReader

/**
 * @author AlexisYin
 */
class CoroutineCancelSideEffect {
    //在finnally释放资源
    @Test
    fun `test finally`() = runBlocking<Unit> {
        val job = launch {
            try {
                repeat(1000) {
                    println("job: I'm sleeping $it ...")
                    delay(500)
                }
            } finally {
                println("job: I'm running finally")
            }
        }
        delay(1000)
        println("main: I'm tired of waiting!")
        job.cancelAndJoin()
        println("main: Now I can quit.")
    }

    //use函数：只能被实现了Closeable的对象使用，程序结束时会自动调用close方法，适合文件对象。
    @Test
    fun `test use`() = runBlocking<Unit> {
        println("close in finally-->")
        BufferedReader(FileReader("D:\\I have a dream.txt")).apply {
            try {
                var line: String? = null
                while (true) {
                    line = readLine() ?: break
                    println(line)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                close()
            }
        }


        println("use-->程序结束时会自动调用close方法")
        BufferedReader(FileReader("D:\\I have a dream.txt")).use {
            var line: String?
            while (true) {
                line = it.readLine() ?: break
                println(line)
            }
        }
    }
}