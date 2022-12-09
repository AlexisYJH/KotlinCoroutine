package com.example.coroutine03

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import org.junit.Test

/**
 * @author AlexisYin
 */
class CoroutineChannel {
    @Test
    fun `test know channel`() = runBlocking {
        val channel = Channel<Int>()
        //生产者
        val producer = GlobalScope.launch {
            var i = 0
            while(true) {
                delay(1000)
                channel.send(++i)
                println("send $i")
            }
        }
        //消费者
        val consumer = GlobalScope.launch {
            while(true) {
                val element = channel.receive()
                println("receive $element")
            }
        }
        joinAll(producer, consumer)
    }

    //生产效率大于消费效率，缓冲区(默认大小是0)满的时候会挂起，可以发现，消费完了才会生产。
    @Test
    fun `test know channel2`() = runBlocking {
        val channel = Channel<Int>()
        //生产者，发完后会等着，消费完后再生产，在继续发
        val producer = GlobalScope.launch {
            var i = 0
            while(true) {
                delay(100)
                channel.send(++i)
                println("send $i")
            }
        }
        val consumer = GlobalScope.launch {
            while(true) {
                delay(200)
                val element = channel.receive()
                println("receive $element")
            }
        }
        joinAll(producer, consumer)
    }

    @Test
    fun `test iterate channel`() = runBlocking<Unit> {
        //缓冲区大小为Channel.UNLIMITED，大小为Int.MAX_VALUE
        val channel = Channel<Int>(Channel.UNLIMITED)
        //生产者，快速发，发到后会放到缓存队列里
        val producer = GlobalScope.launch {
            for (x in 1..5) {
                channel.send(x * x)//发数字的平方
                println("send ${x * x}")
            }
        }

        //消费者，慢慢消费
        val consumer = GlobalScope.launch {
            /*val iterator = channel.iterator()
            while (iterator.hasNext()){
                val element = iterator.next()
                println("receive $element")
                delay(2000)//每隔两秒取出一个元素
            }*/

            for (element in channel) {
                println("receive $element")
                delay(2000)
            }
        }
        joinAll(producer, consumer)
    }

    @Test
    fun `test fast producer channel`() = runBlocking<Unit> {
        val receiveChannel: ReceiveChannel<Int> = GlobalScope.produce<Int> {
            repeat(100) {
                delay(1000)
                send(it)
            }
        }

        val consumer = GlobalScope.launch {
            for (i in receiveChannel) {
                //每隔一秒钟打印一次
                println("received: $i")
            }
        }
        consumer.join()
    }

    @Test
    fun `test fast consumer channel`() = runBlocking<Unit> {
        val sendChannel: SendChannel<Int> = GlobalScope.actor<Int> {
            while (true) {
                val element = receive()
                println(element)
            }
        }

        val producer = GlobalScope.launch {
            for (i in 0..3) {
                sendChannel.send(i)
            }
        }
        producer.join()
    }

    @Test
    fun `test close channel`() = runBlocking<Unit> {
        val channel = Channel<Int>(3)//通道缓冲区大小是3
        //生产者
        val producer = GlobalScope.launch {
            List(3) {
                channel.send(it)
                println("send $it")
            }
            //发完就关闭掉
            channel.close()
            //trimMargin()去掉空格和换行
            println("""close channel. 
                |  - ClosedForSend: ${channel.isClosedForSend}
                |  - ClosedForReceive: ${channel.isClosedForReceive}""".trimMargin())
        }

        //消费者
        val consumer = GlobalScope.launch {
            for (element in channel){
                println("receive $element")
                delay(1000)//每隔一秒消费一次
            }
            println("""After Consuming. 
                |   - ClosedForSend: ${channel.isClosedForSend} 
                |   - ClosedForReceive: ${channel.isClosedForReceive}""".trimMargin())
        }
        joinAll(producer, consumer)
        //每隔三秒钟取一次，但是发送瞬间完成
    }

    @Test
    fun `test broadcast`() = runBlocking<Unit> {
        //capacity是Channel.BUFFERED，0和不限制大小都会崩溃。
        //val broadcastChannel = BroadcastChannel<Int>(Channel.BUFFERED)
        //channel 与 broadcastChannel 的转换
        val channel = Channel<Int>()
        //3是可以缓存的大小
        val broadcastChannel = channel.broadcast(3)
        val producer = GlobalScope.launch {
            List(3){
                delay(100)
                broadcastChannel.send(it)
            }
            broadcastChannel.close()
        }

        List(3){ index ->//启动三个协程
            GlobalScope.launch {
                val receiveChannel = broadcastChannel.openSubscription()//接收者要订阅的
                for (i in receiveChannel){
                    println("[#$index] received: $i")
                }
            }
        }.joinAll()
    }

}