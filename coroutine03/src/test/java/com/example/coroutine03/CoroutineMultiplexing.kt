package com.example.coroutine03

import com.example.kotlincoroutine.api.User
import com.example.kotlincoroutine.api.userServiceApi
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.selects.select
import org.junit.Test
import java.io.File

/**
 * @author AlexisYin
 */
private val cachePath = "D://coroutine.cache"
private val gson = Gson()

data class Response<T>(val value: T, val isLocal: Boolean)

//因为要获取协程的返回结果，所以使用async
//但async要在协程里执行。当然可以使用GlobalScope，但我们不推荐使用
//推荐使用CoroutineScope的扩展函数，扩展函数里面有隐式调用，this对象，this对象指向当前调用的CoroutineScope这一协程作用域

//从本地读取
fun CoroutineScope.getUserFromLocal(name: String) = async(Dispatchers.IO) {
    //delay(1000) //故意的延迟
    //读取json文本，转成User对象
    File(cachePath).readText().let { gson.fromJson(it, User::class.java) }
}

//从网络获取数据
fun CoroutineScope.getUserFromRemote(name: String) = async(Dispatchers.IO) {
    userServiceApi.getUser(name)
}

class CoroutineMultiplexing {
    @Test
    fun `test select await`() = runBlocking<Unit> {
        GlobalScope.launch {
            val localRequest = getUserFromLocal("xxx")
            val remoteRequest = getUserFromRemote("yyy")
            //谁更快返回select就用谁。Response<User>返回值的类型
            val userResponse = select<Response<User>> {
                localRequest.onAwait{ Response(it, true) }
                remoteRequest.onAwait{ Response(it, false) }
            }
            userResponse.value?.let { println(it) }
        }.join()
    }


    @Test
    fun `test select channel`() = runBlocking<Unit> {
        //创建两个channel
        val channels = listOf(Channel<Int>(), Channel<Int>())
        GlobalScope.launch {
            delay(100)
            //第0个通道隔100毫秒发消息
            channels[0].send(200)
        }

        GlobalScope.launch {
            delay(50)
            //第1个通道隔50毫秒发消息
            channels[1].send(100)
        }
        //可能两个都没收到消息，所以泛型我们使用Int?
        val result = select<Int?> {
            channels.forEach { channel ->
                channel.onReceive { it }
            }
        }
        println(result)
    }

    @Test
    fun `test SelectClause0`() = runBlocking<Unit> {
        val job1 = GlobalScope.launch {
            delay(100)
            println("job 1")
        }

        val job2 = GlobalScope.launch {
            delay(10)
            println("job 2")
        }

        select<Unit> {//job1和job2没有返回值，所以泛型类型是Unit
            job1.onJoin { println("job 1 onJoin") }
            job2.onJoin { println("job 2 onJoin") }
        }

        delay(1000)
    }

    @Test
    fun `test SelectClause2`() = runBlocking<Unit> {
        val channels = listOf(Channel<Int>(), Channel<Int>())
        println(channels)

        launch(Dispatchers.IO) {
            select<Unit?> { //没有返回值

                launch {
                    delay(10)
                    //onSend()要传入两个参数,一个是要发送的数据，一个是发送成功的回调
                    channels[1].onSend(200) { sentChannel ->
                        println("sent on $sentChannel")
                    }
                }

                launch {
                    delay(100)
                    channels[0].onSend(100) { sentChannel ->
                        println("sent on $sentChannel")
                    }
                }
            }
        }
        //再开两个协程
        GlobalScope.launch {
            println(channels[0].receive())
        }

        GlobalScope.launch {
            println(channels[1].receive())
        }

        delay(1000)
    }


    //用到了kotlin反射，需要添加反射的依赖
    // implementation "org.jetbrains.kotlin:kotlin-reflect:$kotlin_version"
    @Test
    fun `test select flow`() = runBlocking<Unit> {
        // 函数 -> 协程 -> Flow -> Flow合并
        val name = "guest"
        coroutineScope {
            //::是函数的引用
            listOf(::getUserFromLocal, ::getUserFromRemote)
                .map { function ->
                    //函数引用，是调用kotlin反射,name是需要传入的参数
                    function.call(name)
                }.map { deferred ->
                    flow { emit(deferred.await()) }
                    //merge合并两个协程
                }.merge().collect { user -> println(user) }
        }
    }
}