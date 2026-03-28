# 第13章 线程安全面试题总结

## 一、基础概念类

### 1. 什么是线程安全？

**答案**：
当多个线程访问一个对象时，如果不用考虑这些线程在运行时环境下的调度和交替执行，也不需要进行额外的同步，或者在调用方进行任何其他的协调操作，调用这个对象的行为都可以获得正确的结果，那这个对象是线程安全的。

**线程安全的实现方式**：
1. **互斥同步（阻塞同步）**：synchronized、ReentrantLock
2. **非阻塞同步**：CAS、Atomic类
3. **无同步方案**：不可变对象、ThreadLocal

---

### 2. Java中的线程安全等级有哪些？

**答案**：

| 安全等级 | 说明 | 示例 |
|----------|------|------|
| **不可变** | 绝对线程安全 | String、Integer、final类 |
| **绝对线程安全** | 完全满足线程安全定义 | Vector、Hashtable（不推荐） |
| **相对线程安全** | 通常线程安全，特定操作需额外同步 | ArrayList、HashMap |
| **线程兼容** | 对象本身非线程安全，但可通过同步正确使用 | 大部分类 |
| **线程对立** | 无法在多线程环境正确使用 | Thread.stop()等废弃方法 |

---

### 3. 什么是竞态条件（Race Condition）？

**答案**：
竞态条件是指多个线程对同一共享数据进行读写操作时，由于执行顺序的不确定性，导致最终结果依赖于线程执行的时序。

**常见竞态条件**：
- **check-then-act**：先检查后执行，检查到执行之间状态可能改变
- **read-modify-write**：读取-修改-写入操作不是原子的

**示例**：
```java
// check-then-act竞态条件
if (instance == null) {  // 检查
    instance = new Instance();  // 执行
}
```

---

## 二、synchronized类

### 4. synchronized的底层原理是什么？

**答案**：

**字节码层面**：
- 同步代码块：`monitorenter` 和 `monitorexit` 指令
- 同步方法：`ACC_SYNCHRONIZED` 访问标志

**对象头（Mark Word）**：
```
| 锁状态   | 25bit          | 4bit     | 1bit(偏向锁位) | 2bit(锁标志位) |
|----------|----------------|----------|----------------|----------------|
| 无锁     | 对象HashCode   | 分代年龄 | 0              | 01             |
| 偏向锁   | 线程ID|Epoch  | 分代年龄 | 1              | 01             |
| 轻量级锁 | 指向栈中锁记录指针 | -     | -              | 00             |
| 重量级锁 | 指向互斥量指针   | -       | -              | 10             |
| GC标记   | 空              | -       | -              | 11             |
```

**Monitor机制**：
- 每个Java对象都有一个关联的Monitor
- Monitor包含：Owner（持有线程）、EntryList（等待锁）、WaitSet（等待通知）

---

### 5. synchronized和ReentrantLock的区别？

**答案**：

| 特性 | synchronized | ReentrantLock |
|------|--------------|---------------|
| **实现方式** | JVM层面（字节码） | API层面（JDK） |
| **锁获取方式** | 自动获取/释放 | 手动lock/unlock |
| **可中断** | 不支持 | 支持lockInterruptibly() |
| **超时获取** | 不支持 | 支持tryLock(timeout) |
| **公平锁** | 非公平 | 支持公平/非公平 |
| **条件变量** | 一个（wait/notify） | 多个Condition |
| **性能** | JDK6后优化，接近 | 相当 |
| **适用场景** | 简单同步场景 | 需要高级功能时 |

**选择建议**：
- 简单场景：优先使用synchronized
- 需要高级功能：使用ReentrantLock

---

### 6. synchronized的可重入性是如何实现的？

**答案**：

**可重入性**：同一个线程可以多次获取同一把锁，而不会导致死锁。

**实现原理**：
1. **锁计数器**：Monitor中维护一个计数器
2. **持有线程记录**：记录当前持有锁的线程
3. **重入逻辑**：
   - 首次获取锁：计数器=1，记录线程
   - 同线程再次获取：计数器+1
   - 释放锁：计数器-1，为0时真正释放

**代码示例**：
```java
public synchronized void methodA() {
    methodB();  // 同一线程可以再次获取锁
}

public synchronized void methodB() {
    // 执行代码
}
```

---

### 7. synchronized锁的是什么？

**答案**：

**三种用法对应的锁对象**：

| 用法 | 锁对象 | 代码示例 |
|------|--------|----------|
| **实例方法** | 当前实例（this） | `public synchronized void method()` |
| **静态方法** | 类的Class对象 | `public static synchronized void method()` |
| **代码块** | 指定对象 | `synchronized(obj) { ... }` |

**注意事项**：
- 不要用String常量、Integer等作为锁对象（可能被缓存/复用）
- 锁对象建议使用private final修饰

---

## 三、锁优化类

### 8. 讲一讲Java的锁升级过程？

**答案**：

**锁升级路径**：
```
无锁 → 偏向锁 → 轻量级锁 → 重量级锁
```

**各阶段说明**：

| 阶段 | 触发条件 | 特点 |
|------|----------|------|
| **无锁** | 初始状态 | 对象刚创建 |
| **偏向锁** | 只有一个线程访问 | 记录线程ID，无CAS |
| **轻量级锁** | 多个线程交替访问 | CAS获取锁，自旋等待 |
| **重量级锁** | 多个线程同时竞争 | 操作系统Mutex，线程阻塞 |

**升级过程**：
1. **偏向锁→轻量级锁**：其他线程尝试获取锁
2. **轻量级锁→重量级锁**：自旋超过阈值或竞争激烈

**JDK版本变化**：
- JDK 6：引入偏向锁、轻量级锁
- JDK 15：默认禁用偏向锁（-XX:+UseBiasedLocking）
- JDK 18：移除偏向锁支持

---

### 9. 什么是自旋锁？优缺点是什么？

**答案**：

**自旋锁**：线程不放弃CPU，通过忙等待（循环检查）的方式尝试获取锁。

**优点**：
- 避免线程切换开销（用户态↔内核态）
- 适用于锁持有时间短的场景

**缺点**：
- 占用CPU资源
- 锁持有时间长时浪费CPU

**自适应自旋**（JDK 6+）：
- 根据历史成功率动态调整自旋时间
- 上次成功→增加自旋次数
- 上次失败→减少自旋次数

**JVM参数**：
```bash
-XX:+UseSpinning          # 启用自旋（JDK6默认开启）
-XX:PreBlockSpin=10       # 默认自旋次数
```

---

### 10. 什么是锁消除和锁粗化？

**答案**：

**锁消除（Lock Elimination）**：
- **原理**：JIT编译器通过逃逸分析，判断某段代码不可能存在竞争，消除不必要的锁
- **触发条件**：锁对象不会逃逸出当前线程/方法
- **示例**：StringBuffer的append方法（局部变量）

```java
public String concat(String s1, String s2) {
    StringBuffer sb = new StringBuffer();  // 局部变量，不会逃逸
    sb.append(s1);  // 锁可被消除
    sb.append(s2);  // 锁可被消除
    return sb.toString();
}
```

**锁粗化（Lock Coarsening）**：
- **原理**：将连续的加锁解锁操作合并为一个更大的同步块
- **目的**：减少加解锁次数，提高性能

```java
// 优化前
for (int i = 0; i < 100; i++) {
    synchronized(lock) { count++; }
}

// 优化后（锁粗化）
synchronized(lock) {
    for (int i = 0; i < 100; i++) { count++; }
}
```

---

### 11. 什么是偏向锁？为什么JDK 15后要禁用？

**答案**：

**偏向锁原理**：
- 记录第一个获取锁的线程ID
- 同一线程再次获取时，只需检查线程ID，无需CAS
- 适用于单线程重复获取锁的场景

**优点**：
- 无竞争时性能最优（无需CAS）

**缺点**：
- 撤销偏向锁有开销（需要Stop The World）
- 多线程竞争时反而降低性能

**JDK 15+禁用原因**：
1. 现代应用多线程竞争更普遍
2. 偏向锁撤销开销大
3. 性能测试显示禁用后整体性能更好

**相关参数**：
```bash
-XX:+UseBiasedLocking     # JDK 15前默认开启
-XX:-UseBiasedLocking     # 禁用偏向锁
```

---

## 四、CAS和Atomic类

### 12. 什么是CAS？有什么优缺点？

**答案**：

**CAS（Compare And Swap）**：
比较并交换，是一种原子操作，包含三个操作数：
- **V**：内存位置
- **A**：预期原值
- **B**：新值

**执行逻辑**：
```
if V == A:
    V = B
    return true
else:
    return false
```

**优点**：
- 非阻塞，不会引起线程切换
- 轻量级，性能好

**缺点**：
- **ABA问题**：值从A→B→A，CAS认为未改变
- **循环开销**：高竞争时不断重试，消耗CPU
- **只能保证单个变量原子性**

**底层实现**：
- 使用`Unsafe`类的`compareAndSwapInt`等方法
- CPU指令支持（如x86的`cmpxchg`）

---

### 13. 什么是ABA问题？如何解决？

**答案**：

**ABA问题**：
值从A变为B，再变回A，CAS操作会认为值没有变化，但实际上已经经历过变化。

**示例场景**：
```
线程1：读取值为A
线程2：将A改为B，又将B改回A
线程1：CAS(A→C) 成功，但值已被修改过
```

**解决方案**：

| 方案 | 实现类 | 原理 |
|------|--------|------|
| **版本号** | AtomicStampedReference | 增加stamp版本号 |
| **时间戳** | AtomicMarkableReference | 增加boolean标记 |

**AtomicStampedReference示例**：
```java
AtomicStampedReference<Integer> ref = 
    new AtomicStampedReference<>(100, 0);

// 更新时需要同时匹配值和版本号
ref.compareAndSet(100, 101, 0, 1);
```

---

### 14. AtomicInteger和synchronized的区别？

**答案**：

| 特性 | AtomicInteger | synchronized |
|------|---------------|--------------|
| **实现机制** | CAS（无锁） | 互斥锁（阻塞） |
| **线程状态** | 非阻塞 | 可能阻塞 |
| **适用场景** | 简单原子操作（++、--） | 复杂操作、多个变量 |
| **性能** | 低竞争时更好 | 高竞争时稳定 |
| **ABA问题** | 存在 | 不存在 |

**选择建议**：
- 简单计数：AtomicInteger
- 复杂逻辑：synchronized或ReentrantLock

---

### 15. LongAdder和AtomicLong的区别？

**答案**：

**AtomicLong**：
- 基于CAS
- 高竞争时大量线程自旋，性能下降

**LongAdder**（JDK 8+）：
- **分段思想**：内部维护多个Cell（槽）
- 不同线程操作不同Cell，减少竞争
- 获取结果时汇总所有Cell

**性能对比**：
- 低竞争：AtomicLong略好
- 高竞争：LongAdder性能优势明显（10倍以上）

**使用场景**：
- 高并发计数器：优先使用LongAdder
- 需要compareAndSet：使用AtomicLong

---

## 五、ThreadLocal类

### 16. ThreadLocal的原理是什么？

**答案**：

**核心结构**：
```
Thread → ThreadLocalMap → Entry[] → Entry{ThreadLocal, Value}
```

**原理说明**：
1. 每个Thread对象有一个`ThreadLocalMap`成员
2. `ThreadLocalMap`是ThreadLocal的定制HashMap
3. Key是ThreadLocal实例（弱引用），Value是线程本地值

**源码核心**：
```java
// Thread类中的成员
ThreadLocal.ThreadLocalMap threadLocals = null;

// ThreadLocal.get()
public T get() {
    Thread t = Thread.currentThread();
    ThreadLocalMap map = getMap(t);  // 获取当前线程的Map
    if (map != null) {
        ThreadLocalMap.Entry e = map.getEntry(this);
        if (e != null) {
            return (T)e.value;
        }
    }
    return setInitialValue();
}
```

---

### 17. ThreadLocal内存泄漏问题及解决方案？

**答案**：

**内存泄漏原因**：

```
Thread → ThreadLocalMap → Entry{ThreadLocal(弱引用), Value(强引用)}
                                      ↓
                                 如果ThreadLocal被回收
                                 Value无法被访问但无法回收
```

**泄漏场景**：
- 使用线程池时，线程长期存活
- ThreadLocal使用完未remove()

**解决方案**：

| 方案 | 说明 |
|------|------|
| **及时remove()** | 使用完调用`threadLocal.remove()` |
| **try-finally** | 确保一定执行remove() |
| **避免大对象** | 不要存放大量数据到ThreadLocal |

**最佳实践**：
```java
public void process() {
    threadLocal.set(value);
    try {
        // 业务逻辑
    } finally {
        threadLocal.remove();  // 必须清理
    }
}
```

---

### 18. ThreadLocalMap的Key为什么使用弱引用？

**答案**：

**使用弱引用的原因**：

| 引用类型 | 回收时机 | 问题 |
|----------|----------|------|
| **强引用** | 永不回收 | ThreadLocal无法回收，导致Value一直存在 |
| **弱引用** | GC时回收 | ThreadLocal可回收，但Value可能泄漏 |

**弱引用设计目的**：
1. 允许ThreadLocal在不再使用时被回收
2. 下次访问时，发现Key为null，可以清理Value

**源码清理逻辑**：
```java
// ThreadLocalMap.expungeStaleEntry()
private int expungeStaleEntry(int staleSlot) {
    Entry[] tab = table;
    int len = tab.length;
    
    // 清理指定位置的Entry
    tab[staleSlot].value = null;
    tab[staleSlot] = null;
    size--;
    
    // 继续清理后续过期的Entry
    // ...
}
```

**注意**：弱引用只是辅助，仍需手动remove()

---

## 六、实战场景类

### 19. 如何设计一个线程安全的单例模式？

**答案**：

**方案1：饿汉式（推荐）**
```java
public class Singleton {
    private static final Singleton INSTANCE = new Singleton();
    private Singleton() {}
    public static Singleton getInstance() {
        return INSTANCE;
    }
}
```
- 优点：简单，线程安全
- 缺点：类加载时就创建

**方案2：双重检查锁定（DCL）**
```java
public class Singleton {
    private volatile static Singleton instance;
    private Singleton() {}
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
- volatile防止指令重排序
- 延迟加载，性能较好

**方案3：静态内部类**
```java
public class Singleton {
    private Singleton() {}
    private static class Holder {
        private static final Singleton INSTANCE = new Singleton();
    }
    public static Singleton getInstance() {
        return Holder.INSTANCE;
    }
}
```
- 延迟加载，线程安全，推荐

**方案4：枚举（最佳）**
```java
public enum Singleton {
    INSTANCE;
    // 方法
}
```
- 绝对安全，防止反射和序列化攻击

---

### 20. 如何实现一个生产者-消费者模式？

**答案**：

**方案1：wait/notify**
```java
public class Buffer {
    private List<Integer> list = new ArrayList<>();
    private final int capacity;
    
    public synchronized void produce(int value) throws InterruptedException {
        while (list.size() == capacity) {
            wait();  // 满了，等待消费
        }
        list.add(value);
        notifyAll();  // 通知消费者
    }
    
    public synchronized int consume() throws InterruptedException {
        while (list.isEmpty()) {
            wait();  // 空了，等待生产
        }
        int value = list.remove(0);
        notifyAll();  // 通知生产者
        return value;
    }
}
```

**方案2：ReentrantLock + Condition**
```java
public class Buffer {
    private final List<Integer> list = new ArrayList<>();
    private final int capacity;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();
    
    public void produce(int value) throws InterruptedException {
        lock.lock();
        try {
            while (list.size() == capacity) {
                notFull.await();
            }
            list.add(value);
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }
    
    public int consume() throws InterruptedException {
        lock.lock();
        try {
            while (list.isEmpty()) {
                notEmpty.await();
            }
            int value = list.remove(0);
            notFull.signal();
            return value;
        } finally {
            lock.unlock();
        }
    }
}
```

**方案3：BlockingQueue（推荐）**
```java
public class Buffer {
    private final BlockingQueue<Integer> queue = 
        new ArrayBlockingQueue<>(100);
    
    public void produce(int value) throws InterruptedException {
        queue.put(value);  // 满了自动阻塞
    }
    
    public int consume() throws InterruptedException {
        return queue.take();  // 空了自动阻塞
    }
}
```

---

### 21. 如何排查死锁问题？

**答案**：

**死锁产生的四个条件**：
1. **互斥条件**：资源一次只能被一个线程占用
2. **请求与保持**：持有资源的同时请求新资源
3. **不剥夺条件**：已获得的资源不能被强制剥夺
4. **循环等待条件**：形成资源请求的循环链

**排查方法**：

| 方法 | 命令/工具 | 说明 |
|------|-----------|------|
| **jstack** | `jstack -l <pid>` | 查看线程堆栈，找死锁 |
| **JConsole** | 图形界面 | "线程"标签页检测死锁 |
| **VisualVM** | 图形界面 | 线程Dump分析 |

**jstack示例输出**：
```
Found one Java-level deadlock:
=============================
"Thread-1":
  waiting to lock monitor 0x000000001d0d5b88 (object 0x000000076b5c7d58)
  which is held by "Thread-2"
"Thread-2":
  waiting to lock monitor 0x000000001d0d5b28 (object 0x000000076b5c7d48)
  which is held by "Thread-1"
```

**预防死锁**：
1. 按固定顺序获取锁
2. 使用tryLock()设置超时
3. 尽量减少锁的持有时间
4. 使用并发工具类（ConcurrentHashMap等）

---

### 22. ConcurrentHashMap如何保证线程安全？

**答案**：

**JDK 7：分段锁（Segment）**
- 将数据分成16个Segment
- 每个Segment是一个独立的HashMap
- 不同Segment的操作可以并行

**JDK 8：CAS + synchronized**
- 取消Segment，使用Node数组
- **put操作**：
  1. 计算hash，定位桶
  2. 桶为空：CAS插入
  3. 桶不为空：synchronized锁定头节点
- **get操作**：无锁，volatile保证可见性

**核心优化**：
- 锁粒度更细（只锁链表头节点）
- 读操作无锁
- 扩容时支持并发转移

**与Hashtable对比**：
| 特性 | ConcurrentHashMap | Hashtable |
|------|-------------------|-----------|
| 锁粒度 | 桶级别 | 整个表 |
| 读操作 | 无锁 | 加锁 |
| 性能 | 高 | 低 |
| null键值 | 不支持 | 不支持 |

---

### 23. 如何优雅地停止一个线程？

**答案**：

**错误方式**：
- `Thread.stop()`：不安全，已废弃
- `Thread.suspend()`：容易造成死锁，已废弃

**正确方式**：

**方案1：使用中断标志**
```java
public class Worker implements Runnable {
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            // 工作逻辑
            try {
                doWork();
            } catch (InterruptedException e) {
                // 收到中断，清理资源后退出
                Thread.currentThread().interrupt();  // 重新设置标志
                break;
            }
        }
    }
}

// 停止线程
thread.interrupt();
```

**方案2：使用volatile标志**
```java
public class Worker implements Runnable {
    private volatile boolean running = true;
    
    @Override
    public void run() {
        while (running) {
            doWork();
        }
    }
    
    public void shutdown() {
        running = false;
    }
}
```

**方案3：使用线程池的shutdown()**
```java
ExecutorService executor = Executors.newFixedThreadPool(4);
// 提交任务...

// 优雅关闭
executor.shutdown();           // 停止接收新任务，等待完成
executor.awaitTermination(60, TimeUnit.SECONDS);  // 等待60秒
executor.shutdownNow();        // 强制中断
```

---

### 24. 什么是Fork/Join框架？

**答案**：

**Fork/Join**：JDK 7引入的并行计算框架，基于分治思想。

**核心组件**：

| 组件 | 说明 |
|------|------|
| **ForkJoinPool** | 线程池，使用工作窃取算法 |
| **ForkJoinTask** | 任务基类 |
| **RecursiveTask** | 有返回值的任务 |
| **RecursiveAction** | 无返回值的任务 |

**工作窃取算法**：
- 每个线程维护自己的任务队列
- 空闲线程从其他线程队列"窃取"任务
- 减少线程等待，提高CPU利用率

**示例代码**：
```java
public class SumTask extends RecursiveTask<Long> {
    private final long[] array;
    private final int start, end;
    private static final int THRESHOLD = 10000;
    
    @Override
    protected Long compute() {
        if (end - start <= THRESHOLD) {
            // 直接计算
            long sum = 0;
            for (int i = start; i < end; i++) {
                sum += array[i];
            }
            return sum;
        }
        
        // 拆分任务
        int mid = (start + end) / 2;
        SumTask left = new SumTask(array, start, mid);
        SumTask right = new SumTask(array, mid, end);
        
        left.fork();   // 异步执行
        right.fork();
        
        return left.join() + right.join();  // 等待结果
    }
}

// 使用
ForkJoinPool pool = new ForkJoinPool();
long result = pool.invoke(new SumTask(array, 0, array.length));
```

**适用场景**：
- 大数据量递归计算
- 数组排序、归并
- MapReduce风格计算

---

## 七、高级问题

### 25. 什么是伪共享（False Sharing）？如何解决？

**答案**：

**CPU缓存结构**：
- 缓存行（Cache Line）：64字节
- 多个变量可能位于同一缓存行

**伪共享问题**：
- 线程A修改变量X
- 线程B修改变量Y
- X和Y在同一缓存行
- 导致缓存行在CPU间频繁失效，性能下降

**解决方案**：

| 方案 | JDK版本 | 实现 |
|------|---------|------|
| **填充（Padding）** | JDK 7 | 在变量前后填充无用字段 |
| **@Contended** | JDK 8 | 注解自动填充 |

**示例**：
```java
// JDK 8+ 使用@Contended
public class PaddedLong {
    @Contended
    private volatile long value;
}

// JVM参数启用
-XX:-RestrictContended
```

**LongAdder中的使用**：
- Cell类使用@Contended注解
- 避免多个Cell之间的伪共享

---

### 26. 什么是 happens-before 原则？

**答案**：

**happens-before**：JMM定义的内存可见性保证，如果A happens-before B，则A的操作结果对B可见。

**八大规则**：

| 规则 | 说明 |
|------|------|
| **程序次序规则** | 同一线程内，按代码顺序执行 |
| **锁规则** | unlock happens-before 后续的lock |
| **volatile规则** | volatile写 happens-before 后续的读 |
| **传递性** | A→B，B→C，则A→C |
| **start规则** | Thread.start() happens-before 线程内所有操作 |
| **join规则** | 线程内所有操作 happens-before Thread.join()返回 |
| **中断规则** | interrupt() happens-before 检测到中断 |
| **finalize规则** | 对象构造 happens-before finalize() |

**意义**：
- 程序员只需关注happens-before关系
- 无需关心底层内存屏障细节
- 保证多线程程序的正确性

---

## 八、总结

### 线程安全技术选型指南

| 场景 | 推荐方案 | 说明 |
|------|----------|------|
| 简单计数 | AtomicInteger/LongAdder | 性能最好 |
| 复杂业务逻辑 | synchronized/ReentrantLock | 灵活可控 |
| 高并发读、少写 | ReadWriteLock | 读不互斥 |
| 批量数据处理 | Fork/Join | 并行计算 |
| 线程隔离数据 | ThreadLocal | 避免同步 |
| 不可变数据 | final + 不可变类 | 绝对安全 |

### 面试要点

1. **理解原理**：不仅要知道怎么用，还要知道底层原理
2. **对比分析**：能够对比不同方案的优缺点
3. **场景应用**：根据场景选择合适的技术
4. **问题解决**：能够排查死锁、内存泄漏等问题
5. **JDK演进**：了解各版本的变化（如偏向锁移除）
