# 第12章 Java内存模型代码说明文档

## 1. 代码结构

```
com.linsir.abc.core.jvm.jmm/
├── volatileexample/                    # volatile关键字示例包
│   ├── VolatileFlag.java              # 状态标志位示例
│   ├── VolatileSingleton.java         # DCL单例模式示例
│   ├── ReadWriteCounter.java          # 读写锁读操作示例
│   └── VolatileCounter.java           # 原子性对比测试
├── synchronizedexample/               # synchronized关键字示例包
│   ├── SynchronizedCounter.java       # 线程安全计数器
│   └── SynchronizedMemoryVisibility.java  # 内存可见性示例
├── thread/                            # 线程相关示例包
│   ├── ThreadStateDemo.java           # 线程状态转换演示
│   └── ThreadImplementation.java      # 线程实现方式
├── happensbefore/                     # happens-before规则示例包
│   └── HappensBeforeRules.java        # 8条规则演示
└── test/
    └── JMMTest.java                   # 单元测试类（12个测试用例）
```

## 2. 代码作用

### 2.1 volatileexample包

#### VolatileFlag.java
**作用**：演示volatile作为状态标志位的正确使用场景
- 展示volatile保证可见性的特性
- 实现线程安全的状态控制（启动/停止）
- 适用于单一写线程、多读线程的场景

**核心代码**：
```java
private volatile boolean running = true;

public void stop() {
    running = false;  // 写操作，立即刷新到主内存
}

public void doWork() {
    while (running) {  // 读操作，从主内存获取最新值
        // 执行任务
    }
}
```

#### VolatileSingleton.java
**作用**：演示volatile在双重检查锁定（DCL）单例模式中的关键作用
- 防止new操作的指令重排序问题
- 保证单例对象的正确初始化
- 实现线程安全的懒加载

**核心原理**：
- new操作分为三步：1)分配内存 2)初始化对象 3)引用指向内存地址
- volatile禁止步骤2和3的重排序
- 避免其他线程获取到未完全初始化的对象

#### ReadWriteCounter.java
**作用**：演示volatile在读多写少场景下的性能优化
- 读操作使用volatile，无需加锁，性能高
- 写操作使用synchronized，保证原子性
- 适用于读多写少的并发场景

#### VolatileCounter.java
**作用**：对比演示volatile不能保证原子性
- 展示volatile在复合操作（i++）中的局限性
- 对比volatile与AtomicInteger的线程安全性
- 验证原子类的正确性

### 2.2 synchronizedexample包

#### SynchronizedCounter.java
**作用**：演示synchronized的三种用法和线程安全保证
- **同步实例方法**：锁对象为当前实例（this）
- **同步静态方法**：锁对象为类的Class对象
- **同步代码块**：可指定锁对象，精确控制同步范围

**内存语义**：
- 进入synchronized块：清空工作内存，从主内存重新读取变量值
- 退出synchronized块：将工作内存中的变量值刷新到主内存

#### SynchronizedMemoryVisibility.java
**作用**：演示synchronized保证内存可见性
- 展示synchronized的内存屏障效果
- 验证读写操作的可见性保证
- 对比同步与非同步的可见性差异

### 2.3 thread包

#### ThreadStateDemo.java
**作用**：演示Java线程的6种状态及其转换
- **NEW**：新建状态
- **RUNNABLE**：可运行状态（包含Ready和Running）
- **BLOCKED**：阻塞状态（等待监视器锁）
- **WAITING**：等待状态（无限期等待）
- **TIMED_WAITING**：限时等待状态
- **TERMINATED**：终止状态

#### ThreadImplementation.java
**作用**：演示3种创建线程的方式
- **继承Thread类**：简单直接，但无法继承其他类
- **实现Runnable接口**：可继承其他类，支持资源共享
- **实现Callable接口**：有返回值，可抛出异常

### 2.4 happensbefore包

#### HappensBeforeRules.java
**作用**：演示Java内存模型的8条happens-before规则
1. **程序次序规则**：单线程内，前面的操作happens-before后面的操作
2. **监视器锁规则**：unlock操作happens-before后续lock操作
3. **volatile变量规则**：volatile写happens-before后续volatile读
4. **线程启动规则**：start()方法happens-before线程内所有操作
5. **线程终止规则**：线程内所有操作happens-before终止检测
6. **线程中断规则**：interrupt()调用happens-before中断检测
7. **对象终结规则**：构造函数结束happens-before finalize()
8. **传递性**：A happens-before B，B happens-before C，则A happens-before C

## 3. 测试方法

### 3.1 运行单个示例

每个Java类都包含main方法，可直接运行：

```bash
# 编译
javac -d target/classes src/main/java/com/linsir/abc/core/jvm/jmm/volatileexample/VolatileFlag.java

# 运行
java -cp target/classes com.linsir.abc.core.jvm.jmm.volatileexample.VolatileFlag
```

### 3.2 运行单元测试

使用Maven运行所有测试：

```bash
# 运行所有JMM测试
cd linsir-abc-core
mvn test -Dtest=JMMTest

# 运行单个测试方法
mvn test -Dtest=JMMTest#testVolatileFlag

# 安静模式运行
mvn test -Dtest=JMMTest -q
```

### 3.3 测试类说明

[JMMTest.java](file:///d:/dev/2026/1.3%20code/develop/linsir-develop/linsir-abc/linsir-abc-core/src/test/java/com/linsir/abc/core/jvm/jmm/JMMTest.java) 包含12个测试用例：

| 测试方法 | 测试内容 |
|----------|----------|
| testVolatileFlag | volatile状态标志位可见性 |
| testVolatileSingleton | DCL单例模式正确性 |
| testVolatileNotAtomic | volatile非原子性验证 |
| testAtomicCounter | AtomicInteger原子性 |
| testSynchronizedCounter | synchronized线程安全 |
| testSynchronizedVisibility | synchronized内存可见性 |
| testThreadState | 线程状态转换 |
| testThreadImplementation | 线程实现方式 |
| testHappensBeforeMonitorLock | happens-before监视器锁规则 |
| testHappensBeforeVolatile | happens-before volatile规则 |
| testReadWriteCounter | ReadWriteCounter性能 |
| testConcurrentReadWrite | 并发读写综合测试 |

## 4. 代码执行预期结果

### 4.1 VolatileFlag

**预期输出**：
```
Worker-Thread is working...
Worker-Thread is working...
Main thread calling stop...
Worker-Thread stopped.
Main thread finished.
```

**验证点**：
- 工作线程能够正确读取running标志位的变化
- 主线程调用stop()后，工作线程能够立即看到并停止

### 4.2 VolatileSingleton

**预期输出**：
```
Thread-0 got instance with ID: 1234567890
Thread-1 got instance with ID: 1234567890
...
All threads finished. Instance ID: 1234567890
```

**验证点**：
- 所有线程获取的实例ID相同
- 只有一个实例被创建
- 无并发问题

### 4.3 VolatileCounter

**预期输出**：
```
=== Volatile Counter (Unsafe) ===
Expected: 50000
Actual: 49823  // 小于期望值，证明volatile不能保证原子性
Time: 45ms

=== Atomic Counter (Safe) ===
Expected: 50000
Actual: 50000  // 等于期望值，证明AtomicInteger保证原子性
Time: 120ms
```

**验证点**：
- volatile计数器的实际值小于期望值
- AtomicInteger计数器的实际值等于期望值

### 4.4 SynchronizedCounter

**预期输出**：
```
=== Synchronized Counter Test ===
Thread count: 50
Increment per thread: 1000
Expected: 50000
Actual: 50000
Time: 85ms
Correct: true
```

**验证点**：
- 最终计数等于期望值
- 无并发问题

### 4.5 ThreadStateDemo

**预期输出**：
```
=== Java Thread States Demo ===

NEW State: NEW
RUNNABLE State: RUNNABLE
BLOCKED State: BLOCKED
WAITING State: WAITING
TIMED_WAITING State: TIMED_WAITING
TERMINATED State: TERMINATED
```

**验证点**：
- 能够正确演示6种线程状态
- 状态转换符合预期

### 4.6 HappensBeforeRules

**预期输出**：
```
=== Happens-Before Rules Demo ===

=== Rule 1: Program Order Rule ===
a = 1, b = 2, c = 3
In single thread, operations follow program order.

=== Rule 2: Monitor Lock Rule ===
Writer: set value to 42
Reader: read value = 42
...
=== All rules demonstrated ===
```

**验证点**：
- 每条规则的演示都能正确执行
- 读写操作的可见性得到保证

### 4.7 单元测试结果

**预期结果**：
```
Tests run: 12, Failures: 0, Errors: 0, Skipped: 0

Results :

Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

**验证点**：
- 所有12个测试用例全部通过
- 无失败、无错误

## 5. 注意事项

1. **volatile使用场景**：
   - 适用于状态标志位、DCL单例、读多写少场景
   - 不适用于需要原子性的复合操作（如i++）

2. **synchronized使用场景**：
   - 适用于需要原子性的复合操作
   - 注意锁的粒度，避免过度同步

3. **线程安全选择**：
   - 单一写多读：volatile
   - 复合操作：synchronized或Atomic类
   - 高并发读：ReadWriteLock

4. **测试执行**：
   - 并发测试可能因机器性能不同而耗时不同
   - 建议多次运行验证稳定性
