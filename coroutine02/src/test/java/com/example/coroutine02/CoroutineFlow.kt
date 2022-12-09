package com.example.coroutine02

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.junit.Test
import kotlin.system.measureTimeMillis

/**
 * @author AlexisYin
 */
class CoroutineFlow {

    // 返回了多个值，但不是异步
    fun simpleList(): List<Int> = listOf<Int>(1, 2, 3)

    // 返回了多个值，是同步。
    // 序列与集合的区别：集合长度是固定的，序列是不固定的。
    // SequenceScope只能使用自己已有的挂起函数
    fun simpleSequence(): Sequence<Int> = sequence {
        for (i in 1..3) {
            Thread.sleep(1000)  //阻塞，线程被占用，不能干其它的事情。这里我们假装在计算
            //delay(1000)//只能使用已有的挂起函数
            yield(i)
        }
    }

    //集合+挂起函数方案：返回了多个值，是异步，但是一次性返回了多个值
    //我们要的是像Sequence一次给一个值
    suspend fun simpleList2(): List<Int> {
        delay(1000)
        return listOf<Int>(1, 2, 3)
    }

    //返回多个值，是异步的
    //flow构建器，会构建一个flow对象出来
    fun simpleFlow() = flow<Int> {
        for (i in 1..3) {
            delay(1000)//假装在一些重要的事情
            emit(i)//发射，产生一个元素用emit
        }
    }


    @Test
    fun `test multiple values`() {
        //simpleList().forEach { println(it) }
        simpleSequence().forEach { println(it) }
    }

    @Test
    fun `test multiple values2`() = runBlocking<Unit> {
        simpleList2().forEach { println(it) }
    }

    //运行效果与Sequence一模一样，但Sequence是阻塞主线程的，flow是不阻塞主线程的。
    @Test
    fun `test multiple values3`() = runBlocking<Unit> {
        //运行效果与Sequence同，但后者是阻塞的，两个任务来回切换，证明flow是不阻塞的。
        launch {//为了证明flow不是被阻塞的，我们再起一个任务
            for (k in 1..3) {
                println("I'm not blocked $k")
                delay(1500)
            }
        }
        simpleFlow().collect { println(it) }
    }


    fun simpleFlow2() = flow<Int> {
        println("Flow started")
        for (i in 1..3) {
            delay(1000)
            emit(i)
        }
    }

    @Test
    fun `test flow is cold`() = runBlocking<Unit> {
        val flow = simpleFlow2()
        println("Calling collect...")
        flow.collect { println(it) }
        println("Calling collect again...")
        flow.collect { println(it) }
    }

    @Test
    fun `test flow continuation`() = runBlocking<Unit> {
        //asFlow()是IntRange的扩展属性，构建流的构建器。
        (1..5).asFlow().filter {
            it % 2 == 0//过滤偶数
        }.map {
            "string $it"
        }.collect {
            println("Collect $it")
        }
    }

    @Test
    fun `test flow builder`() = runBlocking<Unit> {
        flowOf("one", "two", "three")
            .onEach { delay(1000) }//每隔一秒钟发射个元素
            .collect { value ->
                println(value)//每隔一秒钟打印输出one two three
            }

        (1..3).asFlow().collect { value ->
            println(value)
        }
    }

    fun simpleFlow3() = flow<Int> {
        println("Flow started ${Thread.currentThread().name}")
        for (i in 1..3) {
            delay(1000)
            emit(i)
        }
    }

    //这种方法无法解决上下文保存
    fun simpleFlow4() = flow<Int> {
        withContext(Dispatchers.IO) {//withContext切换，后台线程发射元素
            println("Flow started ${Thread.currentThread().name}")
            for (i in 1..3) {
                delay(1000)
                emit(i)
            }
        }
    }

    @Test
    fun `test flow context`() = runBlocking<Unit> {
        //构建流和收集流会在同一个协程上下文里面
        simpleFlow3().collect { println("Collected $it ${Thread.currentThread().name}") }
        //这里会报错，IllegalStateException
        simpleFlow4().collect { value -> println("Collected $value ${Thread.currentThread().name}") }
    }

    fun simpleFlow5() = flow<Int> {
        println("Flow started ${Thread.currentThread().name}")
        for (i in 1..3) {
            delay(1000)
            emit(i)
        }
    }.flowOn(Dispatchers.Default)
    //flowOn操作符，该函数用于更改流发射的上下文（改变上游的上下文）

    @Test
    fun `test flow on`() = runBlocking<Unit> {
        simpleFlow5().collect { value -> println("Collected $value ${Thread.currentThread().name}") }
    }

    //事件源，三个事件源源不断发送过来
    fun events() = (1..3)
        .asFlow()
        .onEach {
            println("Event:$it ${Thread.currentThread().name}")
            delay(100)
        }
        .flowOn(Dispatchers.Default)

    @Test
    fun `test flow launch`() = runBlocking<Unit> {
        //onEach是个过渡操作符，此时没有末端操作符，不会发送。我们加上collect末端操作符
        events().onEach { value -> println("Event $value ${Thread.currentThread().name}") }
            //.collect()
            //launchIn需要一个作用域，在作用域中指定协程上下文，launchIn返回的是一个Job对象。
            .launchIn(CoroutineScope(Dispatchers.IO))
            //因为有隐式调用，this就是主线程，所以在主线程中调用也可以这么写,
            //.launchIn(this)
            //调用join才会等待子协成执行完。
            .join()
    }

    fun simpleFlow6() = flow<Int> {
        for (i in 1..3) {
            delay(1000)
            emit(i)
            println("Emitting $i")
        }
    }

    @Test
    fun `test cancel flow`() = runBlocking<Unit> {
        //2500则超时，超时取消协程，协程被取消了，流自然也被取消了
        withTimeoutOrNull(2500) {
            simpleFlow6().collect { value -> println(value) }
        }
        println("Done")
    }

    fun simpleFlow7() = flow<Int> {
        for (i in 1..5) {
            emit(i)//这里会检测ensureActive，发完3发送4的时候这里检测到active为false。就不会继续发送了。
            println("Emitting $i")
        }
    }

    @Test
    fun `test cancel flow check`() = runBlocking<Unit> {
        simpleFlow7().collect { value ->
            println(value)
            if (value == 3) cancel()//执行cancel的时候active为false
        }

    }

    @Test
    fun `test flow cancellable`() = runBlocking<Unit> {
        /*(1..5).asFlow().collect { value ->
            println(value)
            if (value == 3) cancel()
        }*/
        // 可以发现没有取消成功。
        // 如果需要取消，必须明确检测是否取消，需增加cancellable(会影响性能)
        (1..5).asFlow().cancellable().collect { value ->
            println(value)
            if (value == 3) cancel()
        }
    }

    fun simpleFlow8() = flow<Int> {
        for (i in 1..3) {
            delay(100)//生产这个元素需要100
            emit(i)
            println("Emitting $i ${Thread.currentThread().name}")
        }
    }

    @Test
    fun `test flow back pressure`() = runBlocking<Unit> {
        val time = measureTimeMillis {
            simpleFlow8()
                .collect { value ->
                    delay(300)   //处理这个元素消耗300ms，生产效率小于消费效率
                    println("Collected $value ${Thread.currentThread().name}")
                }
        }
        println("Collected in $time ms")//总计耗时1200
    }

    @Test
    fun `test flow back pressure buffer`() = runBlocking<Unit> {
        val time = measureTimeMillis {
            simpleFlow8()
                .buffer(50)//指定50个缓存大小。
                .collect { value ->
                    delay(300)   //处理这个元素消耗300ms
                    println("Collected $value ${Thread.currentThread().name}")
                }
        }
        //一次性发送出123，然后分开三次去收集
        println("Collected in $time ms")//总计耗时1000
    }

    @Test
    fun `test flow back pressure flowOn`() = runBlocking<Unit> {
        //前面的代码都在主线程，我们可以并行发送(切换线程)
        val time = measureTimeMillis {
            simpleFlow8()
                .flowOn(Dispatchers.Default)
                .collect { value ->
                    delay(300)   //处理这个元素消耗300ms
                    println("Collected $value ${Thread.currentThread().name}")
                }
        }
        //发送在后台线程，搜集在主线程
        println("Collected in $time ms")//总计耗时1000
    }

    @Test
    fun `test flow back pressure conflate`() = runBlocking<Unit> {
        val time = measureTimeMillis {
            simpleFlow8()
                .conflate()
                .collect { value ->
                    delay(300)   //处理这个元素消耗300ms
                    println("Collected $value ${Thread.currentThread().name}")
                }
        }
        //跳过了中间值，输出1，3
        println("Collected in $time ms")//总计耗时700
    }

    @Test
    fun `test flow back pressure collectLatest`() = runBlocking<Unit> {
        val time = measureTimeMillis {
            simpleFlow8()
                .collectLatest { value -> //取消并重新发射最后一个值
                    delay(300)   //处理这个元素消耗300ms
                    println("Collected $value ${Thread.currentThread().name}")
                }
        }
        //输出3
        println("Collected in $time ms")//总计耗时700
    }

}