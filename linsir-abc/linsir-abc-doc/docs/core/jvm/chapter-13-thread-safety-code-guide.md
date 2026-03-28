# 第13章 线程安全代码说明文档

## 1. 代码结构

```
com.linsir.abc.core.jvm.threadsafety/
├── immutable/                          # 不可变对象包
│   └── ImmutablePerson.java           # 不可变类示例
├── sync/                              # synchronized包
│   └── SynchronizedCounter.java       # 同步计数器
├── lock/                              # ReentrantLock包
│   └── ReentrantLockCounter.java      # 可重入锁计数器
├── cas/                               # CAS/Atomic包
│   └── AtomicCounter.java             # 原子计数器
├── threadlocal/                       # ThreadLocal包
│   └── ThreadLocalExample.java        # 线程本地存储
├── lockoptimization/                  # 锁优化包
│   └── LockOptimizationDemo.java      # 锁优化演示
└── test/
    └── ThreadSafetyTest.java          # 单元测试类（16个测试用例）
```

## 2. 代码作用

### 2.1 immutable包

#### ImmutablePerson.java
**作用**：演示如何创建线程安全的不可变对象

**核心特性**：
- 类声明为final，防止子类修改
- 所有字段为private final
- 无setter方法
- 对可变对象进行防御性拷贝
- 返回不可修改的视图

**关键代码**：
```java
public final class ImmutablePerson {
    private final String name;
    private final int age;
    private final List<String> hobbies;
    
    public ImmutablePerson(String name, int age, List<String> hobbies) {
        this.name = name;
        this.age = age;
        // 防御性拷贝
        this.hobbies = new ArrayList<>(hobbies);
    }
    
    public List<String> getHobbies() {
        return Collections.unmodifiableList(hobbies);
    }
}
```

### 2.2 sync包

#### SynchronizedCounter.java
**作用**：演示synchronized关键字的三种用法

**三种用法**：
1. **同步实例方法**：锁对象为当前实例（this）
2. **同步静态方法**：锁对象为类的Class对象
3. **同步代码块**：可指定任意对象作为锁

**核心代码**：
```java
// 同步实例方法
public synchronized void increment() {
    count++;
}

// 同步静态方法
public static synchronized void incrementStatic() {
    staticCount++;
}

// 同步代码块
public void add(int value) {
    synchronized (this) {
        count += value;
    }
}
```

### 2.3 lock包

#### ReentrantLockCounter.java
**作用**：演示ReentrantLock的高级功能

**高级特性**：
- 可中断的锁获取
- 超时获取锁
- 公平锁
- 多条件变量（Condition）

**核心代码**：
```java
// 可中断获取锁
public void incrementWithInterruptibleLock() throws InterruptedException {
    lock.lockInterruptibly();
    try {
        count++;
    } finally {
        lock.unlock();
    }
}

// 超时获取锁
public boolean tryIncrement(long timeout, TimeUnit unit) throws InterruptedException {
    if (lock.tryLock(timeout, unit)) {
        try {
            count++;
            return true;
        } finally {
            lock.unlock();
        }
    }
    return false;
}

// 条件变量
public void waitForReady() throws InterruptedException {
    lock.lock();
    try {
        while (!ready) {
            condition.await();
        }
    } finally {
        lock.unlock();
    }
}
```

### 2.4 cas包

#### AtomicCounter.java
**作用**：演示CAS（比较并交换）操作和原子类

**核心内容**：
- AtomicInteger基本使用
- 显式CAS操作
- ABA问题及解决方案

**核心代码**：
```java
// 原子自增
public void increment() {
    count.incrementAndGet();
}

// 显式CAS
public boolean compareAndSet(int expect, int update) {
    return count.compareAndSet(expect, update);
}

// 循环CAS实现自定义操作
public void add(int delta) {
    int oldValue, newValue;
    do {
        oldValue = count.get();
        newValue = oldValue + delta;
    } while (!count.compareAndSet(oldValue, newValue));
}
```

**ABA问题解决方案**：
```java
// 使用AtomicStampedReference解决ABA问题
private final AtomicStampedReference<Integer> stampedRef = 
    new AtomicStampedReference<>(100, 0);

// 更新时需要同时检查值和版本号
stampedRef.compareAndSet(100, 101, 0, 1);
```

### 2.5 threadlocal包

#### ThreadLocalExample.java
**作用**：演示ThreadLocal实现线程本地存储

**核心内容**：
- ThreadLocal基本使用
- SimpleDateFormat线程安全问题
- 用户上下文管理
- 内存泄漏防范

**核心代码**：
```java
// ThreadLocal变量
private static final ThreadLocal<SimpleDateFormat> dateFormat = 
    ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

// 使用ThreadLocal
public String formatDate(Date date) {
    return dateFormat.get().format(date);
}

// 清理ThreadLocal（重要！）
public void clean() {
    dateFormat.remove();
}
```

### 2.6 lockoptimization包

#### LockOptimizationDemo.java
**作用**：演示HotSpot虚拟机的锁优化技术

**五种锁优化**：
1. **自旋锁**：忙等待避免线程切换
2. **锁消除**：逃逸分析消除无竞争锁
3. **锁粗化**：扩大同步范围减少加解锁次数
4. **轻量级锁**：CAS替代互斥量
5. **偏向锁**：记录线程ID避免CAS

**锁升级过程**：
```
无锁 → 偏向锁 → 轻量级锁 → 重量级锁
      (1个线程)  (交替执行)  (竞争激烈)
```

## 3. 测试方法

### 3.1 运行单个示例

每个Java类都包含main方法，可直接运行：

```bash
# 编译
javac -d target/classes src/main/java/com/linsir/abc/core/jvm/threadsafety/immutable/ImmutablePerson.java

# 运行
java -cp target/classes com.linsir.abc.core.jvm.threadsafety.immutable.ImmutablePerson
```

### 3.2 运行单元测试

使用Maven运行所有测试：

```bash
# 运行所有线程安全测试
cd linsir-abc-core
mvn test -Dtest=ThreadSafetyTest

# 运行单个测试方法
mvn test -Dtest=ThreadSafetyTest#testSynchronizedCounter

# 安静模式运行
mvn test -Dtest=ThreadSafetyTest -q
```

### 3.3 测试类说明

[ThreadSafetyTest.java](file:///d:/dev/2026/1.3%20code/develop/linsir-develop/linsir-abc/linsir-abc-core/src/test/java/com/linsir/abc/core/jvm/threadsafety/ThreadSafetyTest.java) 包含16个测试用例：

| 测试方法 | 测试内容 |
|----------|----------|
| testImmutablePerson | 不可变对象防御性拷贝 |
| testSynchronizedCounter | synchronized线程安全 |
| testSynchronizedStaticCounter | synchronized静态方法 |
| testSynchronizedReentrancy | synchronized可重入性 |
| testReentrantLockCounter | ReentrantLock线程安全 |
| testReentrantLockInterruptibility | ReentrantLock可中断性 |
| testReentrantLockTryLock | ReentrantLock超时获取 |
| testReentrantLockCondition | ReentrantLock条件变量 |
| testAtomicCounter | AtomicInteger线程安全 |
| testCASOperation | CAS操作 |
| testCustomCASOperation | 自定义CAS操作 |
| testThreadLocalIsolation | ThreadLocal隔离性 |
| testThreadLocalUserContext | ThreadLocal用户上下文 |
| testProducerConsumer | 生产者-消费者模式 |
| testABAProblem | ABA问题演示 |
| testFineGrainedLocking | 细粒度锁 |

## 4. 代码执行预期结果

### 4.1 ImmutablePerson

**预期输出**：
```
Original: ImmutablePerson{name='Alice', age=25, hobbies=[Reading, Coding]}
After external modify: ImmutablePerson{name='Alice', age=25, hobbies=[Reading, Coding]}
Cannot modify unmodifiable list: null
Original after withAge: ImmutablePerson{name='Alice', age=25, hobbies=[Reading, Coding]}
New person: ImmutablePerson{name='Alice', age=26, hobbies=[Reading, Coding]}
```

**验证点**：
- 外部修改原始列表不影响对象（防御性拷贝）
- 不可修改视图阻止修改操作
- withAge返回新对象，原对象不变

### 4.2 SynchronizedCounter

**预期输出**：
```
1. Testing synchronized instance methods:
Expected: 1000, Actual: 1000

2. Testing synchronized static methods:
Static count: 500

3. Testing reentrancy:
main in methodA
main in methodB
main in methodC

4. Testing fine-grained locking:
ValueA: 99
ValueB: 99
```

**验证点**：
- 多线程环境下计数准确
- 可重入性正常工作
- 细粒度锁互不干扰

### 4.3 ReentrantLockCounter

**预期输出**：
```
1. Basic ReentrantLock usage:
Expected: 1000, Actual: 1000

2. TryLock with timeout:
TryLock result: true
Count after tryLock: 1001

3. Condition variable:
Waiter-Thread waiting for ready...
Setting ready to true and signaling...
Waiter-Thread is ready!

4. Multi-condition example (Producer-Consumer):
Produced: 0
Consumed: 0
Produced: 1
Consumed: 1
...
```

**验证点**：
- 线程安全计数
- tryLock超时功能正常
- 条件变量协调线程
- 生产者-消费者正确工作

### 4.4 AtomicCounter

**预期输出**：
```
1. Basic AtomicInteger usage:
Expected: 10000, Actual: 10000

2. Explicit CAS operation:
CAS 10000 -> 20000: true
Current value: 20000

3. Custom operation with loop CAS:
After add(500): 20500

4. ABA Problem and Solution:
=== ABA Problem Demo ===
Initial value: 100
Thread2 changed 100 -> 200
Thread2 changed 200 -> 100
Thread1 CAS 100 -> 101: true
ABA problem occurred!

=== ABA Solution with StampedReference ===
Thread1 CAS 100 -> 101 with stamp 0: false
ABA problem solved!
```

**验证点**：
- 原子操作保证线程安全
- CAS操作成功/失败判断
- ABA问题被正确识别
- AtomicStampedReference解决ABA问题

### 4.5 ThreadLocalExample

**预期输出**：
```
1. Basic ThreadLocal usage:
Formatter-Thread-0 formatted: 2021-01-01 08:00:00
Formatter-Thread-1 formatted: 2021-01-02 08:00:00
Formatter-Thread-2 formatted: 2021-01-03 08:00:00

2. User Context example:
Request-1: User{id=1, username='alice', role='ADMIN'}
Request-2: User{id=2, username='bob', role='USER'}

3. Thread ID example:
ID-Thread-0 has ID: 0
ID-Thread-1 has ID: 1
...

4. SimpleDateFormat thread safety:
All threads completed successfully with ThreadLocal!

5. Memory leak prevention:
All tasks completed. ThreadLocal properly cleaned.
```

**验证点**：
- 每个线程有自己的格式化器实例
- 用户上下文线程隔离
- 线程ID唯一分配
- 内存泄漏防范正确

### 4.6 LockOptimizationDemo

**预期输出**：
```
=== Lock Optimization Demo ===

1. Spin Lock Concept:
自旋锁通过忙等待避免线程切换开销

2. Lock Elimination:
JVM eliminated unnecessary locks on local StringBuffer

3. Lock Coarsening:
JVM coarsened consecutive synchronized blocks

4. Lightweight Lock Demo:
Lock status: 轻量级锁 (00)

5. Biased Lock Demo:
Biased lock is efficient for single thread

6. Lock Escalation Process:
Stage 1: Biased Locking (single thread)
Stage 2: Lightweight Locking (alternating threads)
Stage 3: Heavyweight Locking (contention)
```

**验证点**：
- 锁消除、锁粗化概念正确
- 轻量级锁、偏向锁演示正常
- 锁升级过程展示完整

### 4.7 单元测试结果

**预期结果**：
```
Tests run: 16, Failures: 0, Errors: 0, Skipped: 0

Results :

Tests run: 16, Failures: 0, Errors: 0, Skipped: 0
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

**验证点**：
- 所有16个测试用例全部通过
- 无失败、无错误

## 5. 注意事项

1. **不可变对象**：
   - 必须进行防御性拷贝
   - 返回不可修改的视图或拷贝

2. **synchronized**：
   - 注意锁对象的选择
   - 避免在同步块中执行耗时操作

3. **ReentrantLock**：
   - 必须在finally块中释放锁
   - 公平锁性能较低，慎用

4. **CAS**：
   - 注意ABA问题
   - 高竞争时性能下降

5. **ThreadLocal**：
   - 使用完必须调用remove()
   - 线程池场景下特别注意

6. **锁优化**：
   - 偏向锁在Java 15+已默认禁用
   - 理解锁升级过程有助于性能调优
