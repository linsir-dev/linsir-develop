# 第12章 Java内存模型与线程

## 12.1 概述

Java内存模型（Java Memory Model，JMM）是Java虚拟机规范中定义的一套内存访问规则，用于规范多线程环境下共享变量的访问方式，确保程序在不同硬件和操作系统平台上具有一致的内存访问语义。

**为什么需要Java内存模型？**

在现代计算机系统中，多核CPU、高速缓存、指令重排序等硬件优化技术虽然提升了性能，但也带来了多线程编程中的三大问题：

1. **可见性（Visibility）**：一个线程对共享变量的修改，其他线程不一定能立即看到
2. **原子性（Atomicity）**：复合操作（如i++）在多线程环境下可能被拆分为多个步骤执行
3. **有序性（Ordering）**：编译器和处理器可能对指令进行重排序，影响程序执行顺序

Java内存模型通过定义主内存与工作内存的抽象、内存间交互操作规则以及happens-before原则，为开发者提供了一套统一的并发编程语义规范。

**本章内容结构：**
- 硬件层面的内存模型与一致性协议
- Java内存模型的核心抽象与规则
- 主内存与工作内存的交互操作
- volatile关键字的内存语义
- happens-before原则
- 原子性、可见性与有序性保证
- Java线程的实现与调度

## 12.2 硬件的效率与一致性

### 12.2.1 硬件内存架构

现代计算机硬件采用分层存储架构，不同存储介质的访问速度存在数量级差异：

| 存储层次 | 访问速度 | 容量 | 位置 |
|----------|----------|------|------|
| CPU寄存器 | ~1ns | 几十字节 | CPU内部 |
| L1缓存 | ~2-4ns | 32-64KB | CPU内部 |
| L2缓存 | ~10ns | 256KB-1MB | CPU内部 |
| L3缓存 | ~20-30ns | 4-64MB | 多核共享 |
| 主内存（RAM） | ~100ns | GB级 | 主板 |
| 磁盘/SSD | ~1-10ms | TB级 | 外部存储 |

**缓存一致性问题：**

在多核处理器中，每个核心都有自己的高速缓存。当多个核心同时访问同一内存位置时，可能出现缓存数据不一致的问题：

```
核心A缓存: x = 1          核心B缓存: x = 1
      ↓                         ↓
   修改x=2                   读取x=?
      ↓                         ↓
   写回主内存                从缓存读取x=1（过期值）
```

### 12.2.2 缓存一致性协议

为了解决缓存一致性问题，硬件层面实现了多种缓存一致性协议，最著名的是MESI协议：

**MESI协议四种状态：**

| 状态 | 名称 | 含义 |
|------|------|------|
| M (Modified) | 修改 | 数据被修改，与主内存不一致，独占该数据 |
| E (Exclusive) | 独占 | 数据与主内存一致，独占该数据 |
| S (Shared) | 共享 | 数据与主内存一致，多个缓存共享该数据 |
| I (Invalid) | 无效 | 数据已过期，不能使用 |

**状态转换示例：**

```
初始状态: 核心A和核心B都没有缓存x

核心A读取x:
  核心A: I → E (从主内存加载，独占)

核心B读取x:
  核心A: E → S (变为共享)
  核心B: I → S (从主内存加载，共享)

核心A修改x:
  核心A: S → M (修改，通知核心B失效)
  核心B: S → I (收到通知，变为无效)

核心B再次读取x:
  核心B: I → S (从核心A的缓存获取最新值)
  核心A: M → S (写回主内存，变为共享)
```

### 12.2.3 指令重排序

为了提升执行效率，编译器和处理器会对指令进行重排序，主要包括三种类型：

**1. 编译器重排序**

编译器在不改变单线程程序语义的前提下，重新安排语句的执行顺序。

```java
// 源代码
int a = 1;      // 语句1
int b = 2;      // 语句2
int c = a + b;  // 语句3

// 编译器可能重排序为：语句1、语句3、语句2（如果b未被使用）
// 或保持原顺序
```

**2. 指令级并行重排序**

现代处理器采用指令级并行技术（ILP），将多条指令重叠执行。如果不存在数据依赖，处理器可以改变语句对应机器指令的执行顺序。

```java
// 无数据依赖的语句可以并行执行
int a = 1;      // 指令1
int b = 2;      // 指令2（与指令1无依赖，可并行）
int c = a + b;  // 指令3（依赖指令1和2，必须等待）
```

**3. 内存系统重排序**

由于处理器使用缓存和读写缓冲区，加载和存储操作看起来可能是在乱序执行。

```java
// 处理器A执行:
store x = 1;    // 写入缓冲区，未立即刷入主内存
store y = 2;    // 可能先完成

// 处理器B执行:
load y;         // 可能读到2
load x;         // 可能读到0（x还未刷入主内存）
```

**数据依赖性：**

如果两个操作访问同一个变量，且其中一个为写操作，则这两个操作存在数据依赖性：

| 类型 | 示例 | 说明 |
|------|------|------|
| 写后读（WAR） | a=1; b=a; | 必须先写后读 |
| 写后写（WAW） | a=1; a=2; | 必须先写后写 |
| 读后写（RAW） | b=a; a=1; | 必须先读后写 |

编译器和处理器不会改变存在数据依赖关系的两个操作的执行顺序。

### 12.2.4 内存屏障

为了保证内存操作的顺序性，处理器提供了内存屏障（Memory Barrier）指令，也称为内存栅栏：

| 屏障类型 | 示例指令 | 作用 |
|----------|----------|------|
| LoadLoad | Load1; LoadLoad; Load2 | 确保Load1在Load2之前完成 |
| StoreStore | Store1; StoreStore; Store2 | 确保Store1在Store2之前完成并刷入内存 |
| LoadStore | Load1; LoadStore; Store2 | 确保Load1在Store2之前完成 |
| StoreLoad | Store1; StoreLoad; Load2 | 确保Store1在Load2之前完成并刷入内存（开销最大） |

Java内存模型通过内存屏障实现了volatile和synchronized的内存语义。

## 12.3 Java内存模型

### 12.3.1 主内存与工作内存

Java内存模型定义了主内存（Main Memory）与工作内存（Working Memory）的抽象概念：

**主内存：**
- 所有线程共享的内存区域
- 存储所有共享变量的实例
- 对应物理内存或硬件缓存

**工作内存：**
- 每个线程私有的内存区域
- 存储主内存中变量的副本
- 对应CPU寄存器、高速缓存等

**内存模型示意图：**

```
┌─────────────────────────────────────────────────────────────┐
│                        主内存（共享）                         │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐    │
│  │ 变量x=1  │  │ 变量y=2  │  │ 变量z=3  │  │   ...    │    │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘    │
└──────────────────────────┬──────────────────────────────────┘
                           │
        ┌──────────────────┼──────────────────┐
        │                  │                  │
        ↓                  ↓                  ↓
┌───────────────┐  ┌───────────────┐  ┌───────────────┐
│  线程A工作内存  │  │  线程B工作内存  │  │  线程C工作内存  │
│  ┌──────────┐ │  │  ┌──────────┐ │  │  ┌──────────┐ │
│  │ x副本=1  │ │  │  │ x副本=1  │ │  │  │ x副本=1  │ │
│  │ y副本=2  │ │  │  │ y副本=2  │ │  │  │ y副本=2  │ │
│  └──────────┘ │  │  └──────────┘ │  │  └──────────┘ │
└───────────────┘  └───────────────┘  └───────────────┘
```

**线程对变量的操作规则：**

1. 线程对共享变量的所有操作必须在工作内存中进行
2. 线程不能直接读写主内存中的变量
3. 线程间无法直接访问彼此的工作内存
4. 变量传递必须通过主内存完成

### 12.3.2 内存间交互操作

Java内存模型定义了8种原子操作来完成主内存与工作内存之间的交互：

| 操作 | 作用范围 | 说明 |
|------|----------|------|
| lock（锁定） | 主内存 | 将变量标识为线程独占状态 |
| unlock（解锁） | 主内存 | 释放变量的锁定状态 |
| read（读取） | 主内存→工作内存 | 从主内存读取变量值 |
| load（载入） | 工作内存 | 将read的值放入工作内存变量副本 |
| use（使用） | 工作内存 | 将变量值传递给执行引擎 |
| assign（赋值） | 工作内存 | 将执行引擎的值赋给变量 |
| store（存储） | 工作内存→主内存 | 从工作内存读取变量值 |
| write（写入） | 主内存 | 将store的值写入主内存变量 |

**操作规则：**

1. **read和load、store和write必须成对出现**，但允许跨操作（如read后先执行其他操作再load）
2. **不允许线程丢弃assign操作**，变量改变后必须同步回主内存
3. **不允许线程无原因地将数据从工作内存同步到主内存**
4. **新变量只能在主内存中诞生**，工作内存中的变量必须经过load或assign初始化
5. **同一时刻只允许一个线程对变量执行lock**，但lock可被同一线程重复执行（可重入）
6. **lock操作会清空工作内存中该变量的值**，需要重新读取
7. **unlock前必须将变量同步回主内存**
8. **unlock只能解锁本线程锁定的变量**

**变量读写流程：**

```
读取变量x:
  lock x（可选，用于同步）
    ↓
  read x（从主内存读取）
    ↓
  load x（载入工作内存）
    ↓
  use x（使用变量值）

写入变量x:
  assign x（赋值给工作内存副本）
    ↓
  store x（从工作内存读取）
    ↓
  write x（写入主内存）
    ↓
  unlock x（可选，释放锁）
```

### 12.3.3 对于volatile型变量的特殊规则

volatile是Java提供的最轻量级同步机制，它保证变量的可见性和有序性。

**volatile的内存语义：**

1. **可见性保证**
   - 写volatile变量：立即将工作内存中的值刷新到主内存
   - 读volatile变量：直接从主内存读取最新值，不使用工作内存副本

2. **有序性保证（禁止指令重排序）**
   - 写volatile变量前的代码不会被重排序到写操作之后
   - 读volatile变量后的代码不会被重排序到读操作之前

**volatile读写插入的内存屏障：**

```
写volatile变量:
  普通写操作
    ↓
  [StoreStore屏障]  // 防止普通写与volatile写重排序
    ↓
  volatile写
    ↓
  [StoreLoad屏障]   // 防止volatile写与后续读写重排序

读volatile变量:
  [LoadLoad屏障]    // 防止volatile读与普通读重排序
    ↓
  volatile读
    ↓
  [LoadStore屏障]   // 防止volatile读与普通写重排序
    ↓
  普通读/写操作
```

**volatile不保证原子性：**

```java
public class VolatileTest {
    private volatile int count = 0;
    
    public void increment() {
        count++;  // 不是原子操作！
        // 实际执行: read → load → use → increment → assign → store → write
    }
}
```

`count++`操作实际上包含多个步骤，即使使用volatile修饰，多线程环境下仍可能出现竞态条件。

**volatile使用场景：**

1. **状态标志位**
```java
private volatile boolean running = true;

public void stop() {
    running = false;  // 所有线程立即可见
}

public void doWork() {
    while (running) {  // 读取最新状态
        // 执行任务
    }
}
```

2. **双重检查锁定（DCL）**
```java
public class Singleton {
    private volatile static Singleton instance;
    
    public static Singleton getInstance() {
        if (instance == null) {                    // 第一次检查
            synchronized (Singleton.class) {
                if (instance == null) {            // 第二次检查
                    instance = new Singleton();    // volatile防止重排序
                }
            }
        }
        return instance;
    }
}
```

3. **读写锁的读操作**
```java
private volatile int value;

public int getValue() {
    return value;  // 无需加锁，直接读取
}

public synchronized void setValue(int v) {
    value = v;     // 写操作加锁保证原子性
}
```

### 12.3.4 针对long和double型变量的特殊规则

Java内存模型允许虚拟机将没有被volatile修饰的64位数据（long和double）的读写操作划分为两次32位操作进行，即不保证其原子性。

**非原子性long/double：**

```java
public class LongTest {
    private long value = 0;
    
    // 在32位JVM上，可能分两次写入（高32位和低32位）
    public void set(long l) {
        value = l;
    }
    
    // 在32位JVM上，可能分两次读取
    public long get() {
        return value;
    }
}
```

**可能出现的问题：**

```
线程A写入0x00000000FFFFFFFF（高32位先写，低32位后写）
  写入高32位: 0x00000000
  （线程B读取）→ 读到0x0000000000000000（错误值）
  写入低32位: 0xFFFFFFFF
```

**解决方案：**

1. **使用volatile修饰**
```java
private volatile long value;  // 保证原子性
```

2. **使用AtomicLong**
```java
private AtomicLong value = new AtomicLong(0);
```

3. **使用synchronized**
```java
public synchronized void set(long l) {
    value = l;
}
```

**注意：** 在64位JVM上，long和double的读写通常是原子的，但为了代码可移植性，建议对共享的long/double变量使用volatile或同步机制。

### 12.3.5 原子性、可见性与有序性

Java内存模型围绕并发编程的三大特性建立：

#### 1. 原子性（Atomicity）

**定义：** 一个或多个操作要么全部执行完成且不被中断，要么完全不执行。

**JMM保证的原子性：**

| 操作类型 | 原子性保证 | 说明 |
|----------|------------|------|
| 基本类型读写 | 是（除long/double） | int、short、byte、boolean、char、float |
| long/double读写 | 否（32位JVM） | 可能分两次32位操作 |
| volatile变量读写 | 是（单次） | 仅保证单次读写的原子性 |
| 复合操作 | 否 | i++、i = i + 1等 |

**保证原子性的方法：**

```java
// 1. synchronized关键字
public synchronized void increment() {
    count++;  // 复合操作变为原子操作
}

// 2. ReentrantLock
private final ReentrantLock lock = new ReentrantLock();

public void increment() {
    lock.lock();
    try {
        count++;
    } finally {
        lock.unlock();
    }
}

// 3. 原子类
private AtomicInteger count = new AtomicInteger(0);

public void increment() {
    count.incrementAndGet();  // 原子自增
}
```

#### 2. 可见性（Visibility）

**定义：** 一个线程对共享变量的修改，其他线程能够立即看到。

**JMM保证可见性的机制：**

| 机制 | 可见性保证 | 原理 |
|------|------------|------|
| volatile | 是 | 强制刷新到主内存/从主内存读取 |
| synchronized | 是 | unlock时刷新到主内存，lock时清空工作内存 |
| final | 是 | 构造函数中写入对其他线程可见 |
| Thread.join() | 是 | 子线程的所有修改对join线程可见 |
| Thread.start() | 是 | start前的修改对新线程可见 |

**可见性示例：**

```java
// 无可见性保证（可能无限循环）
public class NoVisibility {
    private boolean ready = false;
    private int number = 0;
    
    public void writer() {
        number = 42;      // 语句1
        ready = true;     // 语句2（可能被重排序到语句1之前）
    }
    
    public void reader() {
        while (!ready) {  // 可能永远看不到ready=true
            Thread.yield();
        }
        System.out.println(number);  // 可能输出0
    }
}

// 使用volatile保证可见性
public class WithVisibility {
    private volatile boolean ready = false;
    private volatile int number = 0;
    
    public void writer() {
        number = 42;      // volatile写之前的操作不会被重排序到后面
        ready = true;     // volatile写，立即刷新到主内存
    }
    
    public void reader() {
        while (!ready) {  // volatile读，从主内存获取最新值
            Thread.yield();
        }
        System.out.println(number);  // 保证输出42
    }
}
```

#### 3. 有序性（Ordering）

**定义：** 程序执行的顺序按照代码的先后顺序执行。

**指令重排序：**

编译器和处理器为了提高性能，可能会对指令进行重排序，但会遵守as-if-serial语义：
- 单线程程序的重排序不会改变程序的执行结果
- 多线程程序中，重排序可能影响执行结果

**JMM保证有序性的机制：**

| 机制 | 有序性保证 | 说明 |
|------|------------|------|
| volatile | 是 | 禁止指令重排序（插入内存屏障） |
| synchronized | 是 | 同一时刻只有一个线程执行，天然有序 |
| happens-before | 是 | 定义操作之间的顺序关系 |

**重排序示例：**

```java
public class ReorderingDemo {
    private int x = 0, y = 0;
    private int a = 0, b = 0;
    
    public void thread1() {
        a = 1;    // 语句1
        x = b;    // 语句2（可能与语句1重排序）
    }
    
    public void thread2() {
        b = 1;    // 语句3
        y = a;    // 语句4（可能与语句3重排序）
    }
    
    // 可能的结果：
    // x=0, y=1（正常执行）
    // x=1, y=0（正常执行）
    // x=1, y=1（正常执行）
    // x=0, y=0（重排序导致，语句2在语句1前，语句4在语句3前）
}
```

### 12.3.6 先行发生原则

happens-before（先行发生）是Java内存模型中定义的两项操作之间的偏序关系。如果操作A happens-before操作B，那么A的操作结果对B可见，且A在B之前执行。

**happens-before规则（JLS §17.4.5）：**

#### 1. 程序次序规则（Program Order Rule）

在一个线程内，按照程序代码顺序，前面的操作happens-before于后面的操作。

```java
int a = 1;      // 操作A
int b = 2;      // 操作B
// A happens-before B（单线程内）
```

#### 2. 监视器锁规则（Monitor Lock Rule）

对一个锁的unlock操作happens-before于后续对这个锁的lock操作。

```java
synchronized (lock) {
    x = 10;     // 操作A
}               // unlock

synchronized (lock) {
    // 操作B能看到A的结果（A happens-before B）
    System.out.println(x);  // 输出10
}
```

#### 3. volatile变量规则（Volatile Variable Rule）

对一个volatile变量的写操作happens-before于后续对这个变量的读操作。

```java
// 线程A
volatile int flag = 0;
...
flag = 1;       // 写操作A

// 线程B
if (flag == 1) {    // 读操作B
    // A happens-before B，能看到flag=1
}
```

#### 4. 线程启动规则（Thread Start Rule）

Thread对象的start()方法happens-before于此线程的每一个动作。

```java
int x = 10;             // 操作A
Thread t = new Thread(() -> {
    // 操作B能看到x=10（A happens-before B）
    System.out.println(x);
});
t.start();              // start() happens-before线程内所有操作
```

#### 5. 线程终止规则（Thread Termination Rule）

线程中的所有操作都happens-before于对此线程的终止检测。

```java
Thread t = new Thread(() -> {
    x = 10;             // 操作A
});
t.start();
t.join();               // 等待线程结束
// A happens-before join返回，x一定等于10
```

#### 6. 线程中断规则（Thread Interruption Rule）

对线程interrupt()方法的调用happens-before于被中断线程检测到中断事件（通过Thread.interrupted()或Thread.isInterrupted()）。

```java
// 线程A
threadB.interrupt();    // 操作A

// 线程B
if (Thread.interrupted()) {  // 操作B
    // A happens-before B
}
```

#### 7. 对象终结规则（Finalizer Rule）

一个对象的初始化完成（构造函数执行结束）happens-before于它的finalize()方法的开始。

```java
public class MyClass {
    private int value;
    
    public MyClass() {
        value = 42;     // 初始化完成
    }
    
    @Override
    protected void finalize() throws Throwable {
        // 能看到value=42
        System.out.println(value);
    }
}
```

#### 8. 传递性（Transitivity）

如果A happens-before B，且B happens-before C，那么A happens-before C。

```java
// 线程A
volatile int x = 0;
int y = 0;
y = 10;         // 操作A
x = 1;          // 操作B（volatile写）

// 线程B
if (x == 1) {   // 操作C（volatile读）
    // B happens-before C
    // A happens-before B（程序次序规则）
    // 因此A happens-before C，能看到y=10
    System.out.println(y);  // 输出10
}
```

**happens-before与JMM的关系：**

happens-before规则是判断数据是否存在竞争、线程是否安全的主要依据。如果两个操作之间不存在happens-before关系，那么它们的执行顺序和可见性就没有保证，可能存在线程安全问题。

## 12.4 Java与线程

### 12.4.1 线程的实现

Java语言提供了多线程支持，线程的实现主要有三种方式：

#### 1. 使用Thread类

```java
public class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("Thread running: " + Thread.currentThread().getName());
    }
    
    public static void main(String[] args) {
        MyThread thread = new MyThread();
        thread.start();  // 启动线程
    }
}
```

#### 2. 实现Runnable接口

```java
public class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println("Runnable running: " + Thread.currentThread().getName());
    }
    
    public static void main(String[] args) {
        Thread thread = new Thread(new MyRunnable());
        thread.start();
    }
}
```

#### 3. 实现Callable接口（有返回值）

```java
public class MyCallable implements Callable<String> {
    @Override
    public String call() throws Exception {
        return "Callable result from " + Thread.currentThread().getName();
    }
    
    public static void main(String[] args) throws Exception {
        FutureTask<String> futureTask = new FutureTask<>(new MyCallable());
        new Thread(futureTask).start();
        String result = futureTask.get();  // 阻塞等待结果
        System.out.println(result);
    }
}
```

**三种方式对比：**

| 方式 | 优点 | 缺点 |
|------|------|------|
| 继承Thread | 简单直接 | 无法继承其他类 |
| 实现Runnable | 可继承其他类，资源共享 | 无返回值 |
| 实现Callable | 有返回值，可抛异常 | 使用较复杂 |

### 12.4.2 Java线程调度

Java线程调度由操作系统和JVM共同完成，主要分为两种方式：

#### 1. 协同式线程调度（Cooperative Scheduling）

- 线程执行时间由线程本身控制
- 线程执行完毕后主动通知系统切换到其他线程
- 优点：实现简单，切换操作可知
- 缺点：线程可能一直不释放执行权，导致系统阻塞

#### 2. 抢占式线程调度（Preemptive Scheduling）

- 由系统分配执行时间，线程切换不由线程本身决定
- 每个线程有优先级，优先级高的线程获得更多执行时间
- 优点：不会因为单个线程阻塞导致整个系统阻塞
- 缺点：线程同步复杂

**Java采用抢占式调度**，但可以通过以下方式影响调度：

```java
// 设置线程优先级（1-10，默认5）
thread.setPriority(Thread.MAX_PRIORITY);  // 10
thread.setPriority(Thread.NORM_PRIORITY); // 5
thread.setPriority(Thread.MIN_PRIORITY);  // 1

// 提示调度器当前线程愿意让出CPU（不保证一定让出）
Thread.yield();

// 等待指定时间（让出CPU）
Thread.sleep(1000);

// 等待其他线程执行完毕
thread.join();
```

**注意：** 线程优先级只是提示，实际执行顺序由操作系统决定，不同平台表现可能不同。

### 12.4.3 状态转换

Java线程在生命周期中有六种状态：

| 状态 | 说明 | 触发条件 |
|------|------|----------|
| NEW | 新建 | 创建Thread对象，未调用start() |
| RUNNABLE | 可运行 | 调用start()，等待或正在执行 |
| BLOCKED | 阻塞 | 等待监视器锁 |
| WAITING | 等待 | 等待其他线程通知（wait/join） |
| TIMED_WAITING | 限时等待 | 指定时间的等待（sleep/wait(timeout)） |
| TERMINATED | 终止 | run()执行完毕或异常退出 |

**线程状态转换图：**

```
                    NEW
                     │
                     │ start()
                     ↓
    ┌──────────────────────────────────┐
    │           RUNNABLE               │
    │  （包含Ready和Running状态）       │
    └──────────────────────────────────┘
         │         │          │
         │         │          │
    wait()    sleep()    获取锁失败
         │         │          │
         ↓         ↓          ↓
    WAITING  TIMED_WAITING  BLOCKED
         │         │          │
    notify()  时间到      获取锁成功
         │         │          │
         └─────────┴──────────┘
                     │
              run()结束/异常
                     ↓
                 TERMINATED
```

**状态转换示例：**

```java
public class ThreadStateDemo {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            try {
                System.out.println("Running...");
                Thread.sleep(1000);  // TIMED_WAITING
                synchronized (ThreadStateDemo.class) {
                    ThreadStateDemo.class.wait();  // WAITING
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        
        System.out.println("NEW: " + thread.getState());  // NEW
        
        thread.start();
        Thread.sleep(100);
        System.out.println("RUNNABLE: " + thread.getState());  // RUNNABLE
        
        Thread.sleep(1100);
        System.out.println("TIMED_WAITING: " + thread.getState());  // TIMED_WAITING
        
        Thread.sleep(100);
        System.out.println("WAITING: " + thread.getState());  // WAITING
        
        synchronized (ThreadStateDemo.class) {
            ThreadStateDemo.class.notify();
        }
        
        thread.join();
        System.out.println("TERMINATED: " + thread.getState());  // TERMINATED
    }
}
```

## 12.5 Java与协程

### 12.5.1 内核线程的局限

Java线程模型基于操作系统内核线程（Kernel Thread），每个Java线程都对应一个内核线程。这种1:1的线程模型虽然简化了线程管理，但也带来了一些局限性：

**1. 线程创建和切换开销大**

内核线程的创建、销毁和上下文切换都需要操作系统介入，涉及用户态和内核态的切换，开销较大。

```
用户态 → 内核态 → 用户态
  ↓        ↓        ↓
 准备    执行切换   恢复
```

**2. 内存占用高**

每个线程都需要独立的栈空间（默认1MB），大量线程会消耗大量内存。

```java
// 默认栈大小
-Xss1m  // 每个线程1MB栈空间

// 创建10000个线程需要约10GB内存（仅栈空间）
for (int i = 0; i < 10000; i++) {
    new Thread(() -> {
        // 线程执行逻辑
    }).start();
}
```

**3. 线程数量受限**

操作系统能同时管理的线程数量有限，通常几千到几万个，难以支持百万级并发。

**4. 阻塞影响性能**

当线程执行阻塞I/O操作时，内核线程被阻塞，CPU资源被浪费。

```java
// 线程阻塞示例
public void blockingIO() {
    // 阻塞操作导致线程挂起
    Socket socket = serverSocket.accept();  // 阻塞
    InputStream in = socket.getInputStream();
    int data = in.read();  // 可能阻塞
}
```

### 12.5.2 协程的复苏

协程（Coroutine）是一种用户态的轻量级线程，由程序自己调度，具有以下特点：

**协程 vs 线程：**

| 特性 | 协程（Coroutine） | 线程（Thread） |
|------|------------------|----------------|
| 调度方式 | 用户态调度 | 内核态调度 |
| 切换开销 | 极小（无需内核介入） | 较大（需要系统调用） |
| 内存占用 | 极小（KB级栈空间） | 较大（MB级栈空间） |
| 创建数量 | 百万级 | 千级到万级 |
| 阻塞影响 | 协程阻塞不影响其他协程 | 线程阻塞会挂起 |
| 编程复杂度 | 需要显式切换 | 自动调度 |

**协程的优势：**

1. **高并发能力**：可以创建数百万个协程，轻松应对高并发场景
2. **低开销**：协程切换只需保存/恢复寄存器状态，无需内核介入
3. **协作式调度**：协程主动让出CPU，避免频繁的上下文切换

**协程的工作原理：**

```
┌─────────────────────────────────────────┐
│              用户空间                    │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐   │
│  │ 协程A   │ │ 协程B   │ │ 协程C   │   │
│  │ (运行)  │ │ (就绪)  │ │ (阻塞)  │   │
│  └────┬────┘ └─────────┘ └─────────┘   │
│       │                                 │
│       ↓ yield() / 阻塞                  │
│  ┌─────────────────────────────────┐   │
│  │        协程调度器（用户态）       │   │
│  └─────────────────────────────────┘   │
└─────────────────────────────────────────┘
                   │
                   ↓ 系统调用
┌─────────────────────────────────────────┐
│              内核空间                    │
│        ┌─────────────────┐              │
│        │   少量内核线程   │              │
│        └─────────────────┘              │
└─────────────────────────────────────────┘
```

**其他语言的协程实现：**

- **Go语言**：Goroutine + Channel，轻松支持百万级并发
- **Kotlin**：协程（Coroutines）支持挂起和恢复
- **Python**：asyncio库提供协程支持
- **C++20**：co_await、co_yield关键字支持协程

### 12.5.3 Java的解决方案

Java标准库长期缺乏对协程的原生支持，但社区和JDK团队一直在探索解决方案：

**1. 第三方库：Quasar**

Quasar是一个Java协程库，通过字节码增强实现协程：

```java
// Quasar协程示例
import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;

public class QuasarExample {
    public static void main(String[] args) {
        // 创建100万个协程
        for (int i = 0; i < 1_000_000; i++) {
            new Fiber<>(() -> {
                // 协程执行逻辑
                Fiber.sleep(1000);  // 挂起，不阻塞线程
            }).start();
        }
    }
}
```

**2. 响应式编程：Project Reactor / RxJava**

通过异步非阻塞编程模型解决高并发问题：

```java
// Project Reactor示例
import reactor.core.publisher.Mono;

public class ReactiveExample {
    public Mono<String> fetchData() {
        return Mono.fromCallable(() -> {
            // 异步执行
            return fetchFromDatabase();
        }).subscribeOn(Schedulers.boundedElastic());
    }
}
```

**3. JDK 19+：虚拟线程（Virtual Threads）**

JDK 19引入的Project Loom提供了官方协程实现——虚拟线程：

```java
// 虚拟线程示例（JDK 19+）
public class VirtualThreadExample {
    public static void main(String[] args) throws Exception {
        // 方式1：使用Thread.ofVirtual()
        Thread.startVirtualThread(() -> {
            System.out.println("Running in virtual thread: " + 
                Thread.currentThread());
        });
        
        // 方式2：使用ExecutorService
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 1_000_000; i++) {
                executor.submit(() -> {
                    // 任务逻辑
                    Thread.sleep(1000);  // 虚拟线程挂起
                    return "done";
                });
            }
        }
    }
}
```

**虚拟线程的特点：**

1. **轻量级**：栈空间可以自动伸缩，从几百字节到几MB
2. **海量并发**：可以轻松创建数百万个虚拟线程
3. **自动调度**：由JVM自动调度到平台线程（内核线程）上执行
4. **兼容性好**：与现有Thread API兼容，迁移成本低

**虚拟线程的工作原理：**

```
┌─────────────────────────────────────────────────────────┐
│                     虚拟线程（数百万）                     │
│  ┌─────┐ ┌─────┐ ┌─────┐ ┌─────┐ ┌─────┐ ┌─────┐       │
│  │ VT1 │ │ VT2 │ │ VT3 │ │ VT4 │ │ VT5 │ │ ... │       │
│  └──┬──┘ └──┬──┘ └──┬──┘ └──┬──┘ └──┬──┘       │       │
│     │       │       │       │       │           │       │
│     └───────┴───────┴───────┴───────┴───────────┘       │
│                         │                               │
│                         ↓ 调度器                         │
│              ┌─────────────────────┐                    │
│              │    ForkJoinPool     │                    │
│              │  (工作窃取算法)      │                    │
│              └─────────────────────┘                    │
└─────────────────────────────────────────────────────────┘
                           │
                           ↓ 挂载（mount）/ 卸载（unmount）
┌─────────────────────────────────────────────────────────┐
│                   平台线程（少量）                        │
│              ┌─────────────────────┐                    │
│              │   OS Kernel Thread  │                    │
│              └─────────────────────┘                    │
└─────────────────────────────────────────────────────────┘
```

**虚拟线程 vs 传统线程：**

```java
// 传统线程：创建1万个线程可能导致OOM
for (int i = 0; i < 10_000; i++) {
    new Thread(() -> {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }).start();
}

// 虚拟线程：创建100万个虚拟线程轻松应对
for (int i = 0; i < 1_000_000; i++) {
    Thread.startVirtualThread(() -> {
        try {
            Thread.sleep(1000);  // 虚拟线程挂起，不占用平台线程
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    });
}
```

**使用虚拟线程的注意事项：**

1. **不要池化虚拟线程**：虚拟线程创建成本极低，不需要池化
2. **避免同步阻塞**：虚拟线程遇到同步阻塞（如synchronized、ReentrantLock）会阻塞平台线程
3. **使用ThreadLocal需谨慎**：大量虚拟线程可能导致ThreadLocal内存占用过高
4. **适合I/O密集型**：虚拟线程特别适合I/O密集型应用，不适合纯计算密集型

**Java协程的发展前景：**

虚拟线程的引入标志着Java正式迈入协程时代，它将：
- 简化高并发编程模型
- 提升应用的可扩展性
- 降低服务器资源消耗
- 与现有代码库保持兼容

## 12.6 实战：volatile与synchronized的正确使用

### 12.6.1 正确使用volatile

**适用场景：**

1. **状态标志位**
```java
public class VolatileFlag {
    private volatile boolean running = true;
    
    public void stop() {
        running = false;
    }
    
    public void doWork() {
        while (running) {
            // 执行任务
        }
    }
}
```

2. **双重检查锁定（DCL）**
```java
public class Singleton {
    private volatile static Singleton instance;
    
    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}
```

3. **读写锁的读操作**
```java
public class ReadWriteCounter {
    private volatile int value;
    
    public int getValue() {
        return value;  // 无需加锁
    }
    
    public synchronized void increment() {
        value++;
    }
}
```

**不适用场景：**

```java
// 错误：volatile不能保证复合操作的原子性
public class VolatileCounter {
    private volatile int count = 0;
    
    // 线程不安全！i++不是原子操作
    public void increment() {
        count++;
    }
}

// 正确：使用AtomicInteger
public class AtomicCounter {
    private AtomicInteger count = new AtomicInteger(0);
    
    public void increment() {
        count.incrementAndGet();
    }
}
```

### 12.6.2 正确使用synchronized

**synchronized的三种用法：**

1. **同步实例方法**
```java
public synchronized void method() {
    // 锁对象是当前实例（this）
}
```

2. **同步静态方法**
```java
public static synchronized void method() {
    // 锁对象是类的Class对象
}
```

3. **同步代码块**
```java
public void method() {
    synchronized (lock) {
        // 锁对象是指定的lock对象
    }
}
```

**synchronized的内存语义：**

- **进入** synchronized块：清空工作内存，从主内存重新读取变量值
- **退出** synchronized块：将工作内存中的变量值刷新到主内存

**示例：线程安全的计数器**

```java
public class SynchronizedCounter {
    private int count = 0;
    
    public synchronized void increment() {
        count++;
    }
    
    public synchronized void decrement() {
        count--;
    }
    
    public synchronized int getCount() {
        return count;
    }
}
```

### 12.6.3 volatile与synchronized对比

| 特性 | volatile | synchronized |
|------|----------|--------------|
| 可见性 | 保证 | 保证 |
| 原子性 | 不保证（单次读写除外） | 保证 |
| 有序性 | 保证 | 保证 |
| 阻塞 | 不会阻塞线程 | 会阻塞线程 |
| 适用场景 | 单一写、多读的状态标志 | 需要原子性的复合操作 |
| 性能 | 轻量级 | 较重（涉及线程上下文切换） |

## 12.7 本章小结

本章详细介绍了Java内存模型（JMM）的核心概念、线程相关知识以及协程的发展：

**Java内存模型：**
- JMM定义了主内存和工作内存的抽象，规范了多线程环境下共享变量的访问规则
- 线程对变量的操作必须在工作内存中进行，通过主内存进行线程间通信
- 定义了8种原子操作（lock/unlock/read/load/use/assign/store/write）来完成内存交互

**三大特性：**
- **原子性**：操作不可中断，可通过synchronized、Lock、原子类保证
- **可见性**：一个线程的修改对其他线程可见，可通过volatile、synchronized、final保证
- **有序性**：程序执行顺序符合代码逻辑，可通过volatile、synchronized、happens-before保证

**volatile关键字：**
- 保证变量的可见性和有序性
- 通过内存屏障禁止指令重排序
- 不保证复合操作的原子性

**happens-before原则：**
- 定义了操作之间的偏序关系
- 包括程序次序规则、监视器锁规则、volatile变量规则等8条规则
- 是判断线程安全的主要依据

**Java线程：**
- 实现方式：继承Thread、实现Runnable/Callable
- 调度方式：抢占式调度
- 六种状态：NEW、RUNNABLE、BLOCKED、WAITING、TIMED_WAITING、TERMINATED

**Java与协程：**
- 内核线程存在创建开销大、内存占用高、数量受限等局限
- 协程是用户态轻量级线程，具有低开销、高并发能力等优势
- JDK 19+引入虚拟线程（Virtual Threads），为Java提供官方协程支持
- 虚拟线程可以创建数百万个，特别适合I/O密集型应用场景

理解Java内存模型是编写正确并发程序的基础，只有深入理解JMM的规则和原理，才能在实际开发中避免并发问题，编写出高效、安全的并发代码。随着虚拟线程的引入，Java在协程领域也迎来了新的发展机遇。
