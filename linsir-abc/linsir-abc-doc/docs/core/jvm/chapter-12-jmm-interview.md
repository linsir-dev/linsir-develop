# 第12章 Java内存模型面试题总结

## 一、Java内存模型基础

### 1. 什么是Java内存模型（JMM）？

**答案：**
Java内存模型（Java Memory Model，JMM）是Java虚拟机规范中定义的一套内存访问规则，用于规范多线程环境下共享变量的访问方式，确保程序在不同硬件和操作系统平台上具有一致的内存访问语义。

JMM主要解决三个问题：
- **可见性**：一个线程对共享变量的修改，其他线程能否立即看到
- **原子性**：一个或多个操作要么全部执行，要么完全不执行
- **有序性**：程序执行的顺序是否按照代码的先后顺序

### 2. JMM中主内存和工作内存的关系是什么？

**答案：**
JMM定义了主内存（Main Memory）和工作内存（Working Memory）的抽象：

- **主内存**：所有线程共享的内存区域，存储所有共享变量的实例
- **工作内存**：每个线程私有的内存区域，存储主内存中变量的副本

**交互规则**：
1. 线程对共享变量的所有操作必须在工作内存中进行
2. 线程不能直接读写主内存中的变量
3. 线程间无法直接访问彼此的工作内存
4. 变量传递必须通过主内存完成

**8种原子操作**：lock、unlock、read、load、use、assign、store、write

### 3. 为什么需要Java内存模型？

**答案：**
1. **硬件差异**：不同CPU架构（x86、ARM等）的内存模型不同
2. **编译优化**：编译器会进行指令重排序优化
3. **缓存一致性**：多核CPU的缓存一致性协议不同
4. **跨平台**：确保Java程序"一次编写，到处运行"

JMM为开发者提供了一套统一的并发编程语义规范，屏蔽底层硬件差异。

---

## 二、volatile关键字

### 1. volatile关键字的作用是什么？

**答案：**
volatile是Java提供的最轻量级同步机制，有两个核心作用：

**1. 保证可见性**
- 写volatile变量：立即将工作内存中的值刷新到主内存
- 读volatile变量：直接从主内存读取最新值

**2. 保证有序性（禁止指令重排序）**
- 写volatile变量前的代码不会被重排序到写操作之后
- 读volatile变量后的代码不会被重排序到读操作之前

**内存屏障**：
- 写volatile：StoreStore屏障 + StoreLoad屏障
- 读volatile：LoadLoad屏障 + LoadStore屏障

### 2. volatile能保证原子性吗？为什么？

**答案：**
**不能**。volatile只能保证单次读/写操作的原子性，不能保证复合操作的原子性。

**示例**：
```java
private volatile int count = 0;

public void increment() {
    count++;  // 不是原子操作！
}
```

`count++`实际上包含三个步骤：
1. 读取count的值
2. 将值加1
3. 写回count

在多线程环境下，这三个步骤可能被其他线程打断，导致数据竞争。

**解决方案**：
- 使用`synchronized`
- 使用`AtomicInteger`等原子类
- 使用`ReentrantLock`

### 3. volatile的使用场景有哪些？

**答案：**

**场景1：状态标志位**
```java
private volatile boolean running = true;

public void stop() {
    running = false;
}

public void doWork() {
    while (running) {
        // 执行任务
    }
}
```

**场景2：双重检查锁定（DCL）单例模式**
```java
public class Singleton {
    private volatile static Singleton instance;
    
    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();  // 防止指令重排序
                }
            }
        }
        return instance;
    }
}
```

**场景3：读多写少的场景**
```java
private volatile int value;

public int getValue() {  // 读操作，无需加锁
    return value;
}

public synchronized void setValue(int v) {  // 写操作加锁
    value = v;
}
```

### 4. DCL单例模式为什么要用volatile？

**答案：**
`instance = new Singleton()`实际上包含三个步骤：
1. 分配内存空间
2. 初始化对象（构造函数执行）
3. 将引用指向分配的内存地址

**指令重排序问题**：
步骤2和步骤3可能被重排序，导致：
- 线程A执行到步骤3但步骤2未完成
- 线程B判断`instance != null`，返回未完全初始化的对象
- 线程B使用对象时出错

**volatile的作用**：
插入内存屏障，禁止步骤2和步骤3的重排序，确保对象完全初始化后才将引用暴露出去。

---

## 三、synchronized关键字

### 1. synchronized的三种用法是什么？

**答案：**

**1. 同步实例方法**
```java
public synchronized void method() {
    // 锁对象：当前实例（this）
}
```

**2. 同步静态方法**
```java
public static synchronized void method() {
    // 锁对象：类的Class对象（XXX.class）
}
```

**3. 同步代码块**
```java
public void method() {
    synchronized (obj) {  // 可指定任意对象作为锁
        // 同步代码
    }
}
```

### 2. synchronized的底层实现原理？

**答案：**

**JVM层面**：
- 同步方法：使用`ACC_SYNCHRONIZED`访问标志
- 同步代码块：使用`monitorenter`和`monitorexit`指令

**对象头（Object Header）**：
对象在内存中的布局包含：
- Mark Word（标记字段）：存储对象的hashCode、分代年龄、锁状态标志、偏向线程ID等
- Class Metadata Address（类型指针）：指向对象所属类的元数据
- Array Length（数组长度）：仅数组对象有

**锁升级过程**：
无锁 → 偏向锁 → 轻量级锁 → 重量级锁

### 3. synchronized的锁升级过程是怎样的？

**答案：**

**1. 无锁状态**
- 对象刚创建时的状态
- Mark Word记录hashCode、分代年龄等信息

**2. 偏向锁（Biased Locking）**
- 只有一个线程访问同步块时，Mark Word记录线程ID
- 该线程再次进入时无需CAS操作，直接获取锁
- 适用于只有一个线程访问同步块的场景

**3. 轻量级锁（Lightweight Locking）**
- 有第二个线程尝试获取锁时，偏向锁升级为轻量级锁
- 线程在栈帧中创建Lock Record，通过CAS尝试将Mark Word替换为指向Lock Record的指针
- 适用于线程交替执行同步块的场景

**4. 重量级锁（Heavyweight Locking）**
- 自旋一定次数后仍无法获取锁，升级为重量级锁
- 使用操作系统的互斥量（mutex），线程阻塞和唤醒需要用户态和内核态切换
- 适用于多个线程同时竞争锁的场景

**锁升级的目的**：
在大多数情况下，锁不仅不存在多线程竞争，而且总是由同一线程多次获得，因此引入偏向锁和轻量级锁减少获取锁的性能消耗。

### 4. synchronized和volatile的区别？

**答案：**

| 特性 | synchronized | volatile |
|------|--------------|----------|
| 原子性 | ✅ 保证 | ❌ 不保证复合操作 |
| 可见性 | ✅ 保证 | ✅ 保证 |
| 有序性 | ✅ 保证 | ✅ 保证 |
| 阻塞 | ✅ 会阻塞线程 | ❌ 不会阻塞 |
| 适用场景 | 复合操作、临界区 | 状态标志、单次读写 |
| 性能 | 较重 | 轻量 |

**选择原则**：
- 需要原子性：使用synchronized或Atomic类
- 只需要可见性：使用volatile
- 读多写少：考虑volatile + synchronized组合

---

## 四、原子性、可见性、有序性

### 1. 什么是原子性？如何保证？

**答案：**
**原子性**：一个或多个操作要么全部执行完成且不被中断，要么完全不执行。

**JMM保证的原子性操作**：
- 基本类型的读取和赋值（除long/double在32位JVM上）
- volatile变量的单次读写

**不能保证原子性的操作**：
- i++、i--等复合操作
- long/double的读写（32位JVM）

**保证原子性的方法**：
```java
// 1. synchronized
public synchronized void increment() {
    count++;
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
    count.incrementAndGet();
}
```

### 2. 什么是可见性？如何保证？

**答案：**
**可见性**：一个线程对共享变量的修改，其他线程能够立即看到。

**为什么会出现不可见问题**：
- 每个线程有自己的工作内存（缓存）
- 线程修改变量后可能不会立即刷新到主内存
- 其他线程读取时可能从工作内存读取旧值

**保证可见性的方法**：

| 机制 | 原理 |
|------|------|
| volatile | 强制刷新到主内存/从主内存读取 |
| synchronized | unlock时刷新到主内存，lock时清空工作内存 |
| final | 构造函数中写入对其他线程可见 |
| Thread.start() | start前的修改对新线程可见 |
| Thread.join() | 子线程的所有修改对join线程可见 |

### 3. 什么是有序性？为什么会出现有序性问题？

**答案：**
**有序性**：程序执行的顺序按照代码的先后顺序执行。

**指令重排序**：
编译器和处理器为了提高性能，可能会对指令进行重排序：
1. **编译器重排序**：编译器优化时的重排序
2. **指令级并行重排序**：处理器指令级并行的重排序
3. **内存系统重排序**：处理器使用缓存和读写缓冲区的重排序

**as-if-serial语义**：
单线程程序的重排序不会改变程序的执行结果，但多线程程序中可能受影响。

**保证有序性的方法**：
- volatile：禁止指令重排序（插入内存屏障）
- synchronized：同一时刻只有一个线程执行，天然有序
- happens-before规则：定义操作之间的顺序关系

### 4. happens-before原则是什么？有哪些规则？

**答案：**
**happens-before**：如果操作A happens-before操作B，那么A的操作结果对B可见，且A在B之前执行。

**8条happens-before规则**：

1. **程序次序规则**：单线程内，前面的操作happens-before后面的操作
2. **监视器锁规则**：unlock操作happens-before后续lock操作
3. **volatile变量规则**：volatile写happens-before后续volatile读
4. **线程启动规则**：start()方法happens-before线程内所有操作
5. **线程终止规则**：线程内所有操作happens-before终止检测
6. **线程中断规则**：interrupt()调用happens-before中断检测
7. **对象终结规则**：构造函数结束happens-before finalize()
8. **传递性**：A happens-before B，B happens-before C，则A happens-before C

---

## 五、CAS与原子类

### 1. 什么是CAS？有什么优缺点？

**答案：**
**CAS（Compare-And-Swap）**：比较并交换，是一种乐观锁实现。

**原理**：
```java
// 伪代码
boolean compareAndSwap(V, A, B) {
    if (V == A) {  // 当前值等于预期值
        V = B;     // 更新为新值
        return true;
    }
    return false;  // 更新失败
}
```

**执行过程**：
1. 读取内存值V
2. 计算新值B
3. 比较内存当前值是否等于V
4. 如果相等，更新为B；如果不相等，重试

**优点**：
- 无锁，不会阻塞线程
- 性能优于synchronized（低竞争时）

**缺点**：
- **ABA问题**：值从A→B→A，CAS无法感知中间变化
- **自旋开销**：高竞争时CPU空转严重
- **只能保证单个变量原子性**

**ABA问题解决**：
使用`AtomicStampedReference`，增加版本号标识。

### 2. AtomicInteger的实现原理？

**答案：**
`AtomicInteger`基于CAS实现，核心方法是`compareAndSet()`。

**源码核心**：
```java
public final int incrementAndGet() {
    return unsafe.getAndAddInt(this, valueOffset, 1) + 1;
}

// Unsafe类中的方法
public final int getAndAddInt(Object o, long offset, int delta) {
    int v;
    do {
        v = getIntVolatile(o, offset);  // 读取当前值
    } while (!compareAndSwapInt(o, offset, v, v + delta));  // CAS更新
    return v;
}
```

**关键点**：
- 使用`Unsafe`类直接操作内存
- 通过`volatile`保证可见性
- 通过CAS保证原子性
- 失败时自旋重试

### 3. CAS和synchronized的区别？

**答案：**

| 特性 | CAS | synchronized |
|------|-----|--------------|
| 锁类型 | 乐观锁 | 悲观锁 |
| 实现方式 | 硬件原语 | JVM监视器锁 |
| 线程阻塞 | 不会阻塞（自旋） | 会阻塞 |
| 适用场景 | 低竞争、短操作 | 高竞争、长操作 |
| 性能 | 低竞争时更优 | 高竞争时更优 |
| 问题 | ABA问题、自旋开销 | 线程切换开销 |

---

## 六、线程基础

### 1. 创建线程的几种方式？

**答案：**

**方式1：继承Thread类**
```java
class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("Thread running");
    }
}
MyThread t = new MyThread();
t.start();
```

**方式2：实现Runnable接口**
```java
class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println("Runnable running");
    }
}
Thread t = new Thread(new MyRunnable());
t.start();
```

**方式3：实现Callable接口**
```java
class MyCallable implements Callable<String> {
    @Override
    public String call() throws Exception {
        return "Callable result";
    }
}
FutureTask<String> task = new FutureTask<>(new MyCallable());
new Thread(task).start();
String result = task.get();  // 阻塞获取结果
```

**方式4：线程池**
```java
ExecutorService executor = Executors.newFixedThreadPool(10);
executor.execute(() -> System.out.println("Pool thread"));
Future<Integer> future = executor.submit(() -> 42);
```

**推荐**：优先使用线程池，其次Runnable（避免单继承限制）。

### 2. 线程的6种状态及转换？

**答案：**

**6种状态**：

| 状态 | 说明 |
|------|------|
| NEW | 新建状态，未调用start() |
| RUNNABLE | 可运行状态（包含Ready和Running） |
| BLOCKED | 阻塞状态，等待监视器锁 |
| WAITING | 等待状态，无限期等待 |
| TIMED_WAITING | 限时等待状态，超时自动唤醒 |
| TERMINATED | 终止状态，执行完成或异常退出 |

**状态转换**：
```
NEW → start() → RUNNABLE

RUNNABLE ←→ BLOCKED（等待锁）
RUNNABLE ←→ WAITING（wait/join）
RUNNABLE ←→ TIMED_WAITING（sleep/wait(timeout)/join(timeout)）

RUNNABLE → 执行完成 → TERMINATED
```

### 3. sleep()和wait()的区别？

**答案：**

| 特性 | sleep() | wait() |
|------|---------|--------|
| 所属类 | Thread | Object |
| 锁释放 | 不释放 | 释放 |
| 唤醒方式 | 时间到自动唤醒 | 需要notify()/notifyAll() |
| 使用场景 | 暂停执行 | 线程间通信/协作 |
| 调用位置 | 任意位置 | 必须在同步块内 |

**注意**：
- `sleep()`是静态方法，不会释放锁
- `wait()`必须在同步块内调用，会释放锁
- `wait()`通常配合`notify()`/`notifyAll()`使用

### 4. notify()和notifyAll()的区别？

**答案：**

**notify()**：
- 随机唤醒一个正在等待该对象监视器的线程
- 被唤醒的线程参与锁竞争

**notifyAll()**：
- 唤醒所有正在等待该对象监视器的线程
- 所有被唤醒的线程竞争锁

**使用建议**：
- 除非确定只有一个线程等待，否则优先使用`notifyAll()`
- 使用`notifyAll()`可以避免信号丢失问题

---

## 七、线程安全与锁优化

### 1. 什么是线程安全？如何保证？

**答案：**
**线程安全**：多个线程同时访问一个对象时，如果不用考虑这些线程在运行时环境下的调度和交替执行，也不需要进行额外的同步，或者在调用方进行任何其他的协调操作，调用这个对象的行为都可以获得正确的结果。

**保证线程安全的方法**：

1. **互斥同步（阻塞同步）**
   - synchronized
   - ReentrantLock

2. **非阻塞同步**
   - CAS原子操作
   - Atomic类

3. **无同步方案**
   - 可重入代码（纯函数）
   - 线程本地存储（ThreadLocal）
   - 不可变对象（final）

### 2. ThreadLocal的原理和使用场景？

**答案：**
**ThreadLocal**：线程本地变量，每个线程拥有独立的变量副本，互不干扰。

**原理**：
- 每个Thread对象有一个`ThreadLocalMap`
- `ThreadLocal`对象作为key，变量值作为value
- 操作的都是线程自己的Map，无需同步

**使用场景**：
1. **数据库连接**：每个线程有自己的连接
2. **Session管理**：Web应用中存储用户会话
3. **日期格式化**：SimpleDateFormat非线程安全
4. **事务管理**：存储事务上下文

**注意**：
- 使用完需要调用`remove()`，防止内存泄漏
- 线程池场景下特别注意清理

### 3. 什么是死锁？如何避免？

**答案：**
**死锁**：两个或多个线程互相等待对方释放资源，导致永久阻塞。

**死锁的四个必要条件**：
1. **互斥条件**：资源一次只能被一个线程占用
2. **请求与保持**：线程持有资源同时请求新资源
3. **不剥夺条件**：已获得的资源不能被强制剥夺
4. **循环等待**：线程之间形成资源请求的循环链

**避免死锁的方法**：

1. **破坏请求与保持**：一次性申请所有资源
2. **破坏循环等待**：按固定顺序申请资源
3. **使用定时锁**：`tryLock(timeout)`，超时放弃
4. **死锁检测**：使用工具检测死锁

**示例（按固定顺序加锁）**：
```java
// 错误：可能死锁
void transfer(Account a, Account b, int amount) {
    synchronized (a) {
        synchronized (b) {  // 可能等待b的锁
            // 转账
        }
    }
}

// 正确：按id顺序加锁
void transfer(Account a, Account b, int amount) {
    Account first = a.id < b.id ? a : b;
    Account second = a.id < b.id ? b : a;
    synchronized (first) {
        synchronized (second) {
            // 转账
        }
    }
}
```

### 4. 什么是活锁和饥饿？

**答案：**

**活锁（Livelock）**：
- 线程不断改变状态以响应对方，但无法继续执行
- 类似于两个人在走廊互相礼让，都让对方先走，结果都走不了
- **解决**：引入随机等待

**饥饿（Starvation）**：
- 某些线程长时间得不到执行机会
- 常见于优先级调度或锁竞争激烈时
- **解决**：公平锁（Fair Lock）

---

## 八、高级主题

### 1. AQS（AbstractQueuedSynchronizer）是什么？

**答案：**
**AQS**：抽象队列同步器，是Java并发包（JUC）的核心框架，用于实现依赖先进先出（FIFO）等待队列的阻塞锁和相关同步器。

**核心思想**：
- 使用一个int类型的state表示同步状态
- 使用CLH队列（FIFO双向队列）管理等待线程
- 子类通过继承AQS并实现特定方法来定义同步器

**主要方法**：
- `acquire()`：获取锁（阻塞）
- `release()`：释放锁
- `tryAcquire()`：尝试获取锁（子类实现）
- `tryRelease()`：尝试释放锁（子类实现）

**基于AQS的实现**：
- ReentrantLock
- ReentrantReadWriteLock
- CountDownLatch
- Semaphore
- CyclicBarrier

### 2. ReentrantLock和synchronized的区别？

**答案：**

| 特性 | ReentrantLock | synchronized |
|------|---------------|--------------|
| 实现方式 | API层面 | JVM层面 |
| 锁获取方式 | 手动lock/unlock | 自动获取/释放 |
| 可中断 | ✅ 支持 | ❌ 不支持 |
| 超时获取 | ✅ 支持tryLock(timeout) | ❌ 不支持 |
| 公平锁 | ✅ 支持 | ❌ 非公平 |
| 条件变量 | ✅ 多个Condition | ❌ 一个wait/notify |
| 性能 | JDK6后差距不大 | 优化后性能接近 |

**选择建议**：
- 简单同步场景：synchronized（简洁、自动释放）
- 需要高级功能：ReentrantLock

### 3. 什么是内存屏障？有哪些类型？

**答案：**
**内存屏障（Memory Barrier）**：一组处理器指令，用于确保指令的执行顺序，防止指令重排序。

**四种类型**：

| 屏障类型 | 指令序列 | 作用 |
|----------|----------|------|
| LoadLoad | Load1; LoadLoad; Load2 | Load1在Load2之前完成 |
| StoreStore | Store1; StoreStore; Store2 | Store1在Store2之前完成并刷入内存 |
| LoadStore | Load1; LoadStore; Store2 | Load1在Store2之前完成 |
| StoreLoad | Store1; StoreLoad; Load2 | Store1在Load2之前完成并刷入内存（开销最大） |

**volatile的内存屏障**：
```
写volatile：
  [StoreStore] → volatile写 → [StoreLoad]

读volatile：
  [LoadLoad] → volatile读 → [LoadStore]
```

### 4. final关键字在JMM中的语义？

**答案：**
**final的内存语义**：
- 构造函数中对final域的写入，与随后把这个被构造对象的引用赋值给一个引用变量，这两个操作不能重排序
- 初次读一个包含final域的对象的引用，与随后初次读这个final域，这两个操作不能重排序

**作用**：
- 确保final域在对象构造完成后对其他线程可见
- 实现不可变对象的线程安全

**示例**：
```java
public class FinalExample {
    private final int x;  // final域
    private int y;        // 普通域
    
    public FinalExample() {
        x = 1;            // 写final域
        y = 2;            // 写普通域
    }
}

// 其他线程读取
FinalExample obj = new FinalExample();  // 读对象引用
int a = obj.x;   // 读final域，保证看到x=1
int b = obj.y;   // 读普通域，可能看到y=0
```

---

## 九、面试技巧总结

### 高频考点

1. **volatile**：可见性、有序性、不保证原子性、使用场景
2. **synchronized**：三种用法、锁升级、底层实现
3. **CAS**：原理、优缺点、ABA问题
4. **JMM**：主内存/工作内存、8种原子操作、happens-before
5. **线程状态**：6种状态及转换

### 答题技巧

1. **先讲概念，再讲原理，最后举例**
2. **对比类问题用表格呈现**
3. **涉及底层实现时，结合代码或图示**
4. **强调实际应用和注意事项**

### 常见陷阱

- volatile不能保证原子性（i++问题）
- synchronized锁的是对象，不是代码
- CAS的ABA问题
- ThreadLocal的内存泄漏问题
- 死锁的四个必要条件
