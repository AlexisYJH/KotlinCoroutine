> $$app
# 协程
## 什么是协程？
协程基于线程，它是**轻量级**线程。

协程让**异步逻辑同步化**，杜绝回调地狱。
协程最核心的点就是，函数或一段程序能被**挂起**，稍后再在挂起的位置**恢复**
> MainActivity2.kt

## Android中协程用来解决什么问题？
1. 处理耗时任务：这种任务常常会阻塞主线程
2. 保证主线程安全：即确保安全地从主线程调用任何suspend函数

## 异步任务（过时）
异步任务的回调函数不利于天然地思考问题
可能存在回调地狱：一个异步请求嵌套另一个异步请求，另一个异步请求依赖于另一个的执行结果，使用回调的方式相互嵌套，会造成代码可读性非常差，后期不好维护
> MainActivity.kt

## 协程的挂起和恢复
suspend：用于暂停执行当前协程，并保存所有局部变量
resume：用于让已暂停的协程从暂停处继续执行

挂起函数：使用suspend修饰的函数，只能在协程体内或其他挂起函数内调用

堆栈帧中的函数调用流程
> MainActivity3.kt

## 挂起和阻塞
delay 是挂起函数，可以让delay函数暂时挂起，挂起函数不会阻塞线程（主线程或其它线程），另外，协程体本身就是一个挂起函数。
Thread.sleep函数是真正的阻塞函数，它会让当前线程阻塞。
> MainActivity4.kt

## 协程的两部分
Kotlin的协程实现分为两个层次：
基础设施层，标准的协程API，主要对协程提供了概念和语义上最基本的支持
业务框架层，协程的上层框架支持
> MainActivity5.kt

## 协程调度器
Dispatchers.Default：表示会使用一种默认低并发的线程策略，当你要执行的代码属于计算密集型任务时，开启过高的并发反而可能会影响任务的运行效率，此时就可以使用Dispatchers.Default。
场景：数组排序，JSON数据解析，处理差异判断。

Dispatchers.IO：表示会使用一种较高并发的线程策略，当你要执行的代码大多数时间是在阻塞和等待中，比如说执行网络请求时，为了能够支持更高的并发数量，此时就可以使用Dispatchers.IO。
场景：数据库、文件读写、网络处理

Dispatchers.Main：则表示不会开启子线程，而是在Android主线程中执行代码，但是这个值只能在Android 项目中使用。
场景：调用suspend函数、调用UI函数、更新LiveData

## 任务泄露
当某个协程任务丢失，会导致内存、CPU、磁盘等资源浪费，甚至发送一个无用的网络请求，这种情况称为**任务泄露**。
为了能够避免协程泄露，Kotlin引入了**结构化并发机制**。
使用结构化并发可以做到：
- **取消任务**，当某项不再需要时取消它。
- **追踪任务**，当任务正在执行时，追踪它。
- **发出错误信号**，当协程失败时，发出错误信号表明有错误发生。

## CoroutinesScope
定义协程必须指定其CoroutinesScope，它会跟踪所有协程，同样它还可以**取消由它所启动的所有协程**。
常用的相关API有：
- GlobalScope，生命周期是process级别的，即使Activity或Fragment已经被销毁，协程仍然在执行。
- MainScope，在Activity中使用，可以在onDestory中取消协程。
`private val mainScope = MainScope()`
一般使用委托的方式：`class MainActivity: AppCompatActivity(), CoroutineScope by MainScope(){`
- viewModelScope，只能在ViewModel中使用，绑定ViewModel的生命周期。
依赖：`implementation"androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1"`
- lifecycleScope，只能在Activity、Fragment中使用，会绑定Activity和Fragment的生命周期。
依赖： `implementation"androidx.lifecycle:lifecycle-runtime-ktx:2.2.0"`
> MainActivity6.kt

## 协程上手
协程+Retrofit+ViewModel+Livedata+DataBinding
> MainActivity7.kt MainViewModel.kt UserRespository.kt activity_main.xml

> $$coroutine01
# 协程的启动和取消
## 协程构建器
launch与async构建器都用来启动新协程：
- launch，返回一个Job，并且不附带任何结果值
- async，返回一个Deferred，Deferred也是一个Job，可以使用.await()在一个延期的值上得到它的最终结果。

等待一个作业：
- join与await
- async组合并发
> CoroutineTest01.kt

## 启动模式
- DEFAULT：协程创建后，立即开始调度，在调度前如果协程被取消，其将直接进入取消响应的状态；（立即调度不代表立即执行，可能需要等待）
- ATOMIC（原子）：协程创建后，立即开始调度，协程执行到第一个挂起点之前不响应取消；（适用于第一个挂起点前需要执行必要操作）
- LAZY：只有协程被需要时，包括主动调用协程的start、join或者await等函数时才会开始调度，如果调度前就被取消，那么该协程将直接进入异常结束状态。
- UNDIPATCHED：协程创建后**立即**在**当前函数调用栈**中执行，直到遇到一个真正被挂起的点。

取消的时机：
- DEFAULT：调用cacel后，直接取消调度
- ATOMIC：调用cacel后，当协程执行到第一个挂起函数时才会取消调度
- LAZY：当协程被使用的时候才开始调度，可以在调度前取消协程
- UNDIPATCHED：不分发，即使指定了调度器，也会立即在当前函数调用栈中执行，调用cacel后，当协程执行到第一个挂起函数时才会取消调度
```kotlin
private fun test() = runBlocking {
    launch (context = Dispatchers.IO, start = CoroutineStart.UNDISPATCHED) {
        println(Thread.currentThread().name)
    }
}
```
runBlocking 在主线程中执行，在协程中，即使指定了 Dispatchers.IO，依然在主线程中执行。
> CoroutineTest02.kt

## 作用域构建器
runBlocking 和 coroutineScope：
- runBlocking 是常规函数，而 coroutineScope 是挂起函数
- 它们都会等待其协程体以及所有子协程结束，主要区别在于 runBlocking 方法会**阻塞**当前线程来等待，
    而 coroutineScope 只是**挂起**，会释放底层线程用于其他用途

coroutineScope 和 supervisorScope：
- coroutineScope 一个协程失败了，所有其它兄弟协程也会被取消
- supervisorScope 一个协程失败了，不会影响其它兄弟协程

## Job的生命周期
对于每一个创建的协程，会返回一个Job实例，该实例是协程的唯一标示，并负责管理协程的生命周期
一个任务包括的状态是：新建（New）、激活（Active）、完成中（Completing）、已完成（Completed）、取消中（Cacelling）、已取消（Cacelled）。可以访问Job的属性：isActive、isCancelled和isCompleted

如果协程处于活跃状态，协程运行出错或调用job.cancel()都会将当前任务置为取消中（Cacelling）状态(isActive=false, isCancelled=true)。当所有的子协程都完成时，协程会进入已取消（Cacelled）状态，此时isCompleted=true。
![Job的生命周期](https://img-blog.csdnimg.cn/6ed9c69c8ad542088b6757c2202c37c1.png#pic_center)
## 协程的取消
- 取消作用域会取消它的子协程。
- 被取消的子协程并不会影响其余兄弟协程
- 协程通过抛出一个特殊的异常 CancellationException 来处理取消操作
- 所有kotlinx.coroutines中的挂起函数（withContext、delay等）都是可取消的
> CoroutineCancel

## CPU密集型任务取消
- isActive： 是一个可以被使用在 CoroutineScope 中的扩展属性，检查 Job 是否处于活跃状态。
- ensureActive()：如果Job处于非活跃状态，则抛出异常。
- yield 函数会检查所在协程的状态，如果已经取消，则抛出异常。次外，它还会尝试让出线程的执行权，给其他协程协程提供执行机会。（当程序非常密集的时候使用）
> CoroutineCancel

## 协程取消的副作用
协程被取消时，会抛出异常，导致下面代码无法执行到，下面的代码可能是必须执行的逻辑，比如释放资源。
- 在finnally释放资源
- use函数：只能被实现了Closeable的对象使用，程序结束时会自动调用close方法，适合文件对象。（use 函数中已经封装了try...catch，以及释放资源。）
> CoroutineCancelSideEffect

## 不能取消的任务
使用 `withContext(NonCancellable)` ，任务可以不被取消。
处于取消中状态的协程不能够挂起（运行不能取消的代码），当协程被取消后需要调用挂起函数，需要即清理任务的代码放置于NoCancallable CoroutineContext中。这样会挂起运行中的代码，并保持协程的取消中状态直到任务处理完成 。
> CoroutineNonCancellable

## 超时任务
withTimeout，超时抛出异常
withTimeoutOrNull 通过返回null来进行超时操作，从而代替抛出一个异常。
> CoroutineTimeout

# 协程的异常处理
## 协程的上下文
CoroutineContext 是一组用于定义协程行为的元素。它由如下几项组成：
- Job：控制协程的生命周期
- CoroutineDispatcher：向合适的线程分发任务
- CoroutineName：协程的名称
- CoroutineExceptionHandler：处理未被捕获的异常

可以使用+操作符，在协程上下文中定义多个元素（CoroutineContext运算符重载`public operator fun plus(context: CoroutineContext): CoroutineContext`）
> CoroutineContextTest

## 协程上下文的继承
对于新建的协程，它的 CoroutineContext 会包含一个**全新的Job实例**，它会帮助我们控制协程生命周期。而**剩下的元素会从CoroutineContext的父类继承**，该父类可能是另外一个协程或者创建该协程的CoroutineSocpe。

协程的上下文 = **默认值 + 继承的CoroutineContext  + 参数**
- 一些元素包含默认值：Dispatchers.Default是默认的CoroutineDispatcher，"coroutine"是默认的CoroutineName；
- 继承的CoroutineContext是CoroutineScope或者父协程的CoroutineContext；
- 传入协程构建器的参数的优先级高于继承的上下文参数，因此会覆盖对应的参数值。
> CoroutineContextTest

## 异常处理的必要性
当应用出现一些意外情况时，给用户提供合适的体验非常重要，一方面，目睹应用崩溃是个很糟糕的体验，另一方面，在用户操作失败时，也必须给出正确的提示信息。

## 异常的传播
协程构建器有两种形式： **自动传播异常**（launch与actor），**向用户暴露异常**（async与produce）当这些构建器用于创建一个**根协程**时，前者构建器，异常会在第一时间被抛出，后者构建器，依赖用户最终消费异常，例如通过await或receive。

非根协程的异常：
其他协程所创建的协程中，产生的异常总是会被传播
> CoroutineExceptionPropagation

## 异常的传播特性
当一个协程由于一个异常而运行失败时，它会传播这个异常并传递给它的父级。接下来，父级会进行下面几个操作：
- 取消它自己的子级
- 取消它自己
- 将异常传播给它的父级

## SupervisorJob
使用 SupervisorJob 时，一个子协程的运行失败不会影响其他子协程。SupervisorJob不会传播异常给它的父级，它会让子协程自己处理异常。
这种需求常见于在作用域内定义作业的UI组件，如果任何一个UI的子作业执行失败了，它并不总是有必要取消整个UI组件，但是如果UI组件被销毁了，由于它的结果不再被需要了，它就有必要使所有的子作业执行失败。

## supervisorScope
当作业自身执行失败时，所有子作业会被全部取消。
一个子协程的运行失败不会影响其他子协程。

## 异常的捕获
使用 CoroutineExceptionHandler 对协程的异常进行捕获。
当然，并不是所有的异常都会被 CoroutineExceptionHandler 捕获，需要满足以下条件：
- 时机：异常是被自动抛出异常的协程所抛出，使用 launch，而不是 async 时；
- 位置：在CoroutineScope的CoroutineContext中或在一个根协程（CoroutineScope 或者 supervisorScope 的直接子协程）中。
CoroutineExceptionHandler 不能安装在内部协程，要安装到外部协程上
> CoroutineExceptionCatch

## Android全局异常处理
全局异常处理器可以获取到所有协程未处理的未捕获异常，不过它并不能对异常进行捕获，虽然**不能阻止程序崩溃**，全局异常处理器在程序调试和异常上报等场景中仍然有非常大的用处。

我们需要在classpath下面创建META-INF/services目录，并在其中创建一个名为kotlinx.coroutines.CoroutineExceptionHandler 的文件，文件内容就是我们的全局异常处理器的全类名。
（在模块下的main目录下新建 resources/META-INF/services 目录）
> GlobalCoroutineExceptionHandler
> kotlinx.coroutines.CoroutineExceptionHandler

## 取消与异常
- 取消与异常常紧密相关，协程内部使用 CancellationException 来进行取消，这个异常会被忽略。
- 当子协程被取消时，不会取消它的父协程。
- 如果一个协程遇到了 CancellationException 以外的异常，它将使用该异常取消它的父协程。当父协程的所有子协程都结束后，异常才会被父协程处理。
> CoroutineCancelAndException

## 异常聚合
当协程的多个子协程因为异常而失败时，一般情况下取第一个异常进行处理。在第一个异常之后发生的所有其他异常，都将**绑定到第一个异常之上**。
```kotlin
        val handler = CoroutineExceptionHandler { _, exception ->
            //Caught java.io.IOException [java.lang.ArithmeticException, java.lang.IndexOutOfBoundsException]
            //数组：public final synchronized Throwable[] getSuppressed() {
            println("Caught $exception ${exception.suppressed.contentToString()}")
        }
```
> CoroutineExceptionAggregation

> $$coroutine02
# Flow异步流
> CoroutineFlow
## 如何表示多个值？
挂起函数可以异步返回单个值，但是如果要异步返回多个计算好的值呢？

## 异步返回值的多个方案
- 集合：同步返回了多个值。
- 序列：同步返回了多个值。
序列与集合的区别：集合长度是固定的，序列是不固定的。
- 挂起函数：异步返回了多个值，但是一次性返回了多个值，要的是像Sequence一次给一个值
- Flow：异步返回多个值
> fun `test multiple values`()
> fun `test multiple values2`()
> fun `test multiple values3`()

## Flow与其他方式的区别
- 名为flow的Flow类型构建器函数
- flow{...}构建块中的代码可以挂起
- 函数simpleFlow不再标有suspend修饰符
- 流用emit函数发射值
- 流用collect函数收集值

## Flow应用
在Android当中，文件下载是Flow的一个非常典型的应用。
UI界面点击按钮下载文件，下载文件是通过后台线程(如dispature.IO)，通过emit发射数据（下载进度，异常，下载完成等）给主线程，主线程通过collect拿到数据，在UI中更新。

## 冷流
Flow是一种类似于序列的**冷流**，flow构建器中的代码直到流被收集的时候才运行。
只有调用collect的时候构建器代码才会执行，才会发射元素，再次调用collect就会再次执行构建器代码，再次发射元素
> fun `test flow is cold`()

## 流的连续性
- 流的每次单独收集都是按顺序执行的，除非使用特殊操作符
- 从上游到下游每个过滤操作符都会处理每个发射出的值，然后交给末端操作符。
注：按顺序放进去按顺序拿出来
> fun `test flow continuation`()

## 流的构建器
- flowOf构建器定义了一个发射固定值集的流
- 使用.asFlow()扩展函数，可以将各种集合和序列转换为流
> fun `test flow builder`()

## 流上下文
- 流的收集总是在调用协程的上下文中发生，流的该属性称为**上下文保存**。
- flow{...}构建器中的代码必须遵循上下文保存属性，并且不允许从其他上下文中发射（emit）。
构建流和收集流会在同一个协程上下文里面，但这很不实用，比如我们需要后台下载文件，并更新UI。要使用flowOn操作符来打破上下文保存的特性。
- flowOn操作符，该函数用于更改流发射的上下文（改变上游的上下文）
> fun `test flow context`()
> fun `test flow on`()

## 启动流
使用launchIn替换Collect我们可以在单独的协程中启动流的收集。（改变下游的上下文）
> fun `test flow launch`()

## 流的取消
流采用与协程同样的协作取消(在协程中启动流，协程取消了，流也就取消了)，像往常一样，流的收集可以是当流在一个可取消的挂起函数(如delay)中挂起的时候取消。
> fun `test cancel flow`()

## 流的取消检测
- 为方便起见，流构建器对每个发射至执行附加的ensureActive检测以进行取消，这意味着从 flow{...}发出的繁忙循环是可以取消的。
- 处于性能原因，大多数其他流操作不会自行执行其他取消检测，在协程处于繁忙循环的情况下，必须明确检测是否取消。
- 通过cancellable操作符来执行此操作
> fun `test cancel flow check`()
> fun `test flow cancellable`()

## 背压
生产者生产效率大于消费者消费效率
背压解决方案：降低生产效率；提高消费效率

- buffer()，并发运行流中发射元素的代码
- conflate()，合并发射项，不对每个值进行处理。跳过中间值
- collectLatest()，取消并重新发射最后一个值
- 当必须更改ConroutineDspatcher时，flowOn操作符使用了相同的缓存机制，但是buffer函数显示地请求缓冲而**不改变执行上下文**。
> fun `test flow back pressure`()
> fun `test flow back pressure buffer`()
> fun `test flow back pressure flowOn`()
> fun `test flow back pressure conflate`()
> fun `test flow back pressure collectLatest`()

> CoroutineFlowOperator
## 过渡流操作符
- 可以使用操作符转换流，就像使用集合与序列一样。
- 过渡操作符应用于上游流，并返回下游流。
- 这些操作符也是冷操作符，就像流一样。这类操作符本身不是挂起函数。
- 它运行的速度很快，返回新的转换流的定义。
> fun `test transform flow operator`()
> fun `test limit length operator`()

## 末端流操作符
末端操作符是在流上用于**启动流收集的挂起函数**。 collect 是最基础的末端操作符，但是还有另外一些更方便使用的末端操作符：
- 转化为各种集合，例如 toList 与 toSet。
- 获取第一个（first）值与确保流发射单个（single）值的操作符。
- 使用 reduce 与 fold 将流规约到单个值。
> fun `test terminal operator`()

## 组合多个流
就像 Kotlin 标准库中的 Sequence.zip 扩展函数一样， 流拥有一个 zip 操作符用于组合两个流中的相关值
> fun `test zip`()

## 展平流
流表示异步接收的值序列，所以很容易遇到这样的情况： 每个值都会触发对另一个值序列的请求。然而，由于流具有异步的性质，因此需要不同的展平模式， 为此，存在一系列的流展平操作符。
-  flatMapConcat 连接模式
- flatMapMerge 合并模式
- flatMapLatest 最新展平模式

> CoroutineFlowException
## 流异常
当运算符中的发射器或代码抛出异常时，流收集可以带有异常的完成。 有几种处理异常的方法。
-  try 与 catch块（下游）
- catch函数 （上游）
## 流完成
当流收集完成时（普通情况或异常情况），它可能需要执行一个动作。
- 命令式 finally 块
- onCompletion 声明式处理
onCompletion相比finally的优势在于，发生异常时会获取到异常信息，但不会捕获

> $$coroutine03
# 通道
## 认识Channel
Channel 实际上是一个**并发安全的队列**，它可以用来连接协程，实现不同协程的通信。
![在这里插入图片描述](https://img-blog.csdnimg.cn/bb0529a75b6943eb91b528300915e1ea.png)> CoroutineChannel
## Channel的容量
Channel的容量或者说缓冲区大小，默认为0，当消费者消费慢了，那么生产者会等待，反之生产者生产慢了，消费者会等待。如果想要指定缓冲区大小，可以在构建时传入。

> fun `test know channel`()
> fun `test know channel2`()
## Channel迭代器
除了使用receive函数外，Channel还提供了迭代器用来接收数据
- iterator()函数
- for in
> fun `test iterate channel`()
## produce与actor
在协程中，可以使用produce启动一个生产者协程，并返回ReceiveChannel。反之使用actor启动一个消费者协程
> fun `test fast producer channel`()
> fun `test fast consumer channel`()
## Channel的关闭
produce和actor返回的channel都会随着对应协程执行结束后自动关闭，channel才被称为**热数据流**
我们也可以使用close方法手动关闭，它会立即停止发送元素，此时**isClosedForSend**会立即返回true，而由于缓冲区的存在，所有元素读取完毕后，**isClosedForReceive**才会返回true
channel的生命周期最好由主导方来维护，建议由**主导的一方实现关闭**
> fun `test close channel`()
## BroadcastChannel
kotlin还提供了发送端一对多接收端的方式，使用BroadcastChannel需要指定其缓冲区大小，或使用Channel.BUFFERED。还可以使用channel对象的broadcast函数来获取BroadcastChannel对象
> fun `test broadcast`()

# 多路复用
数据通信系统或计算机网络系统中，传输媒体的带宽或容量往往会大于传输单一信号的需求，为了有效利用通道线路，希望**一个信道同时传输多路信号**，这就是多路复用技术（Multiplexing）。
只有一个管道，不同的水都经过这个管道。音视频中传输就是这样。

> CoroutineMultiplexing.kt
## 多个await复用--select
获取数据可能有多个方法，希望哪个方法先返回就使用它的返回值
> fun `test select await`()
## 多个Channel复用
和await类似，会接收到最快的那个channel消息
> fun `test select channel`()
## SelectCause
并不是所有的事件可以使用select的，只有SelectCauseN类型的事件
- SelectCause0：对应事件没有返回值，例如join，那么onJoin就是SelectCauseN，使用时，onJoin的参数是一个无参函数
- SelectCause1：对应事件有返回值，例如onAwait，onReceive
- SelectCause2：对应事件有返回值，此外还要一个额外参数，例如Channel.onSend，一个参数为Channel数据类型的值，一个为发送成功时的回调参数

如果想要确认挂起函数是否支持select，只要查看其**是否存在对应的SelectCauseN类型**可回调即可。
> fun `test SelectClause0`()
> fun `test SelectClause2`()
## 使用Flow实现多路复用
多数情况下，可以通过构造合适的Flow来实现多路复用的效果
> fun `test select flow`()

# 并发安全
## 不安全的并发访问
在Java平台上的kotlin协程实现避免不了并发调度的问题，因此线程安全值得留意
> fun `test not safe concurrent`()
> fun `test safe concurrent`()

## 协程的并发工具
除了我们在线程中常用的解决并发问题的手段之外，协程框架也提供了一些并发工具
- Channel：并发安全的消息通道
- Mutex：轻量级锁，用法和Java的锁类似，获取不到锁时，不会阻塞线程，而是挂起等待锁的释放
- Semaphore：轻量级信号量，在linux中是进程间通讯的一种方式，协程获取到信号量后即可执行并发操作。当Semaphore为1时，效果等价于Mutex
> fun `test safe concurrent tools`()
> fun `test safe concurrent tools2`()

## 避免访问外部可变状态
避免访问外部变量，基于参数作运算，通过返回值提供运算结果
> fun `test avoid access outer variable`()

> $$flowpractice
# 协程Flow的综合应用
## 文件下载
> DownloadFragment
## Room
> UserFragment
## Retrofit
> ArticleFragment
## 热流（StateFlow&SharedFlow）
Flow是冷流，有了订阅者Collector以后，发射出来的值才会实实在在的存在于内存之中，类似懒加载。
StateFlow和SharedFlow是热流，在垃圾回收之前，都是存在内存之中，并且处于活跃状态。
- StateFlow
StateFlow是一个状态容器式**可观察数据流**，可以向其收集器发出当前状态更新和新状态更新。还可通过其value属性读取当前状态值
StateFlow 与LiveData 非常相似。
> NumberFragment
- SharedFlow
SharedFlow会向其中收集值的所有使用方发出数据。
> SharedFlowFragment TextFragment

> $$jetpackpaging
# Flow与Jetpack Paging3
![加载数据的流程](https://img-blog.csdnimg.cn/ea70f26581604014b13b5d0cb79d337d.png)
数据从PagingSource来，Pager里设置PageConfig，加载完后会得到Flow，最后交给PagingDataAdapter更新UI

# 项目实战（hilt + room + paging3 + coil + startup）
![在这里插入图片描述](https://img-blog.csdnimg.cn/8b31501a4f68455eb73a3c52cbc352e9.png)
- Hilt注入网络相关对象
- Hilt注入Room相关对象
- Pager配置
- ViewModel
- BindingAdapter与Coil
- PagingDataAdapter
- LoadStateFooter上拉刷新
- 下拉刷新
- App Startup