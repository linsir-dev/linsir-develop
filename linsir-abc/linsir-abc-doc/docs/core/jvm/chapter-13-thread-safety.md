# 第13章 线程安全与锁优化

## 13.1 概述

线程安全是并发编程中的核心问题。当多个线程同时访问一个对象时，如果不用考虑这些线程在运行时环境下的调度和交替执行，也不需要进行额外的同步，或者在调用方进行任何其他的协调操作，调用这个对象的行为都可以获得正确的结果，那这个对象是线程安全的。

本章将深入探讨Java语言中的线程安全分类、线程安全的实现方法，以及HotSpot虚拟机为了提升并发性能而进行的各种锁优化技术。

## 13.2 线程安全

### 13.2.1 Java语言中的线程安全

Java语言中各种操作共享的数据可以分为以下五类，按安全程度由强至弱排列：

#### 1. 不可变（Immutable）

**定义**：不可变的对象一定是线程安全的，无论是对象的方法实现还是方法的调用者，都不需要再采取任何的线程安全保障措施。

**实现方式**：
- 使用`final`关键字修饰基本数据类型
- 使用`final`修饰引用类型，并确保引用对象也是不可变的
- 使用`String`、`Integer`等不可变类
- 自定义不可变类（所有字段为final，无setter方法）

**示例**：
```java
// 不可变类
public final class ImmutablePerson {
    private final String name;
    private final int age;
    private final List<String> hobbies;  // 注意：需要防御性拷贝
    
    public ImmutablePerson(String name, int age, List<String> hobbies) {
        this.name = name;
        this.age = age;
        // 防御性拷贝，防止外部修改
        this.hobbies = new ArrayList<>(hobbies);
    }
    
    // 只有getter，没有setter
    public String getName() { return name; }
    public int getAge() { return age; }
    
    // 返回不可修改的视图
    public List<String> getHobbies() {
        return Collections.unmodifiableList(hobbies);
    }
}
```

**Java中的不可变对象**：
- `String`：字符串常量，任何修改都会创建新对象
- `Integer`、`Long`等包装类
- `BigInteger`、`BigDecimal`
- `java.time`包中的日期时间类

#### 2. 绝对线程安全（Absolute Thread Safety）

**定义**：不管运行时环境如何，调用者都不需要额外的同步措施。通常需要付出很大的甚至是不切实际的性能代价。

**示例**：
`Vector`是线程安全的容器，但在复合操作下仍需要同步：
```java
Vector<String> vector = new Vector<>();

// 线程A
if (!vector.contains("element")) {  // 检查
    vector.add("element");           // 操作
}
// 上述操作不是原子性的，可能出现竞态条件

// 正确的做法：使用同步
synchronized (vector) {
    if (!vector.contains("element")) {
        vector.add("element");
    }
}
```

#### 3. 相对线程安全（Relative Thread Safety）

**定义**：通常意义上讲的线程安全，需要保证对这个对象单独的操作是线程安全的，我们在调用的时候不需要做额外的保障措施，但是对于一些特定顺序的连续调用，就可能需要在调用端使用额外的同步手段来保证调用的正确性。

**Java中的相对线程安全类**：
- `Vector`、`Hashtable`、`Collections.synchronizedXXX()`
- `ConcurrentHashMap`（部分操作）
- `AtomicInteger`等原子类

#### 4. 线程兼容（Thread Compatible）

**定义**：对象本身并不是线程安全的，但是可以通过在调用端正确地使用同步手段来保证对象在并发环境中可以安全地使用。我们平常说一个类不是线程安全的，绝大多数指的是这种情况。

**示例**：
`ArrayList`、`HashMap`等集合类属于线程兼容：
```java
// 线程不安全的ArrayList
ArrayList<String> list = new ArrayList<>();

// 通过同步包装实现线程安全
List<String> synchronizedList = Collections.synchronizedList(list);
```

#### 5. 线程对立（Thread Hostile）

**定义**：无论调用端是否采取了同步措施，都无法在多线程环境中并发使用的代码。

**示例**：
- `Thread`类的`suspend()`和`resume()`方法（已废弃）
- 不恰当地使用`System.setIn()`、`System.setOut()`等全局方法

### 13.2.2 线程安全的实现方法

#### 1. 互斥同步（Mutual Exclusion & Synchronization）

**定义**：互斥同步是最常见的一种并发正确性保障手段。同步是指在多个线程并发访问共享数据时，保证共享数据在同一时刻只被一个（或者是一些，使用信号量的时候）线程使用。而互斥是实现同步的一种手段，临界区（Critical Section）、互斥量（Mutex）和信号量（Semaphore）都是主要的互斥实现方式。

**synchronized关键字**：

Java中最基本的互斥同步手段就是`synchronized`关键字，经过编译后会在同步块的前后分别形成`monitorenter`和`monitorexit`两个字节码指令。

**特点**：
- 可重入：同一线程可以多次获取同一把锁
- 非公平：锁的获取不保证顺序
- 不可中断：获取锁的过程不能响应中断

**ReentrantLock**：

`java.util.concurrent.locks.ReentrantLock`是另一种互斥同步手段，与`synchronized`相比增加了以下高级功能：

| 特性 | ReentrantLock | synchronized |
|------|---------------|--------------|
| 等待可中断 | ✅ 支持 | ❌ 不支持 |
| 公平锁 | ✅ 支持 | ❌ 非公平 |
| 锁绑定多个条件 | ✅ 支持多个Condition | ❌ 一个wait/notify |
| 性能 | JDK6后接近 | 优化后接近 |

**使用示例**：
```java
public class ReentrantLockExample {
    private final ReentrantLock lock = new ReentrantLock();
    private int count = 0;
    
    public void increment() {
        lock.lock();  // 获取锁
        try {
            count++;
        } finally {
            lock.unlock();  // 确保释放锁
        }
    }
    
    // 可中断获取锁
    public void tryLockWithTimeout() throws InterruptedException {
        if (lock.tryLock(3, TimeUnit.SECONDS)) {
            try {
                // 执行业务逻辑
            } finally {
                lock.unlock();
            }
        } else {
            // 获取锁超时
        }
    }
    
    // 公平锁
    private final ReentrantLock fairLock = new ReentrantLock(true);
}
```

**选择建议**：
- 简单同步场景：使用`synchronized`（简洁、自动释放）
- 需要高级功能：使用`ReentrantLock`

#### 2. 非阻塞同步（Non-Blocking Synchronization）

**定义**：互斥同步面临的主要问题是进行线程阻塞和唤醒所带来的性能开销，因此这种同步也被称为阻塞同步（Blocking Synchronization）。非阻塞同步基于冲突检测的乐观并发策略，先进行操作，如果没有其他线程争用共享数据，那操作就成功了；如果共享数据有争用，产生了冲突，那就再采取其他的补偿措施（最常见的补偿措施是不断地重试，直到成功为止）。

**CAS操作**：

比较并交换（Compare-and-Swap，CAS）是一种原子操作，包含三个操作数：
- 内存位置V
- 预期原值A
- 新值B

当且仅当V的值等于A时，CAS通过原子方式用新值B来更新V的值，否则不会执行任何操作。

**Java中的CAS实现**：

```java
public class CASExample {
    private AtomicInteger count = new AtomicInteger(0);
    
    public void increment() {
        int oldValue, newValue;
        do {
            oldValue = count.get();
            newValue = oldValue + 1;
        } while (!count.compareAndSet(oldValue, newValue));
        // 或者简写：count.incrementAndGet();
    }
}
```

**CAS的ABA问题**：

如果一个变量V初次读取的时候是A值，并且在准备赋值的时候检查到它仍然是A值，那我们就能说明它的值没有被其他线程改变过吗？不一定，因为在这段时间它的值可能被改为其他值，然后又改回A。

**解决方案**：使用`AtomicStampedReference`，增加版本号标识：
```java
AtomicStampedReference<Integer> stampedRef = 
    new AtomicStampedReference<>(100, 0);

// 更新时需要同时检查值和版本号
stampedRef.compareAndSet(100, 101, 0, 1);
```

#### 3. 无同步方案

**可重入代码（Reentrant Code）**：

也叫纯代码（Pure Code），可以在代码执行的任何时刻中断它，转而去执行另外一段代码（包括递归调用它本身），而在控制权返回后，原来的程序不会出现任何错误。

特征：
- 不依赖存储在堆上的数据和公用的系统资源
- 用到的状态量都由参数中传入
- 不调用非可重入的方法

**线程本地存储（Thread Local Storage）**：

如果一段代码中所需要的数据必须与其他代码共享，那就看看这些共享数据的代码是否能保证在同一个线程中执行。如果能保证，就可以把共享数据的可见范围限制在同一个线程之内，这样，无须同步也能保证线程之间不出现数据争用的问题。

**ThreadLocal使用示例**：
```java
public class ThreadLocalExample {
    // 线程本地变量
    private static final ThreadLocal<SimpleDateFormat> dateFormat =
        ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));
    
    public String format(Date date) {
        // 每个线程使用自己的SimpleDateFormat实例
        return dateFormat.get().format(date);
    }
    
    // 使用完记得清理（尤其在线程池中）
    public void clean() {
        dateFormat.remove();
    }
}
```

**ThreadLocal原理**：
- 每个Thread对象有一个`ThreadLocalMap`
- `ThreadLocal`对象作为key，变量值作为value
- 操作的都是线程自己的Map，无需同步

**注意事项**：
- 使用完需要调用`remove()`，防止内存泄漏
- 线程池场景下特别注意清理

## 13.3 锁优化

高效并发是从JDK 5升级到JDK 6后一项重要的改进项，HotSpot虚拟机开发团队在这个版本上花费了大量的资源去实现各种锁优化技术，如适应性自旋（Adaptive Spinning）、锁消除（Lock Elimination）、锁粗化（Lock Coarsening）、轻量级锁（Lightweight Locking）和偏向锁（Biased Locking）等。

### 13.3.1 自旋锁与自适应自旋

**自旋锁（Spin Lock）**：

互斥同步对性能最大的影响是阻塞的实现，挂起线程和恢复线程的操作都需要转入内核态中完成，这些操作给系统的并发性能带来了很大的压力。如果物理机器有一个以上的处理器或者处理器核心，能让两个或以上的线程同时并行执行，我们就可以让后面请求锁的那个线程"稍等一会"，但不放弃处理器的执行时间，看看持有锁的线程是否很快就会释放锁。为了让线程等待，我们只须让线程执行一个忙循环（自旋），这项技术就是所谓的自旋锁。

**自旋锁的优缺点**：

| 优点 | 缺点 |
|------|------|
| 避免线程切换开销 | 占用CPU资源 |
| 适用于锁持有时间短的场景 | 锁持有时间长时浪费CPU |

**自适应自旋（Adaptive Spinning）**：

JDK 6对自旋锁进行了优化，引入了自适应自旋。自适应意味着自旋的时间不再是固定的，而是由前一次在同一个锁上的自旋时间及锁的拥有者的状态来决定的。

**自适应策略**：
- 如果在同一个锁对象上，自旋等待刚刚成功获得过锁，并且持有锁的线程正在运行中，那么虚拟机就会认为这次自旋也很有可能再次成功，进而允许自旋等待持续相对更长的时间
- 如果对于某个锁，自旋很少成功获得过，那在以后要获取这个锁时将有可能直接省略掉自旋过程，以避免浪费处理器资源

### 13.3.2 锁消除（Lock Elimination）

**定义**：锁消除是指虚拟机即时编译器在运行时，对一些代码要求同步，但是对被检测到不可能存在共享数据竞争的锁进行消除。

**逃逸分析（Escape Analysis）**：

锁消除的主要判定依据来源于逃逸分析的数据支持，如果判断到一段代码中，在堆上的所有数据都不会逃逸出去被其他线程访问到，那就可以把它们当作栈上数据对待，认为它们是线程私有的，同步加锁自然就无须再进行。

**示例**：
```java
public String concatString(String s1, String s2, String s3) {
    StringBuffer sb = new StringBuffer();
    sb.append(s1);
    sb.append(s2);
    sb.append(s3);
    return sb.toString();
}
```

`StringBuffer`的`append()`方法是同步的，但这里的`sb`引用不会逃逸出方法，其他线程无法访问，因此可以安全地消除锁。

**JVM参数**：
```bash
-XX:+DoEscapeAnalysis  # 开启逃逸分析
-XX:+EliminateLocks    # 开启锁消除
```

### 13.3.3 锁粗化（Lock Coarsening）

**定义**：如果虚拟机探测到有这样一串零碎的操作都对同一个对象加锁，将会把加锁同步的范围扩展（粗化）到整个操作序列的外部。

**示例**：
```java
public void method() {
    // 频繁的加锁解锁
    synchronized (lock) {
        // operation 1
    }
    synchronized (lock) {
        // operation 2
    }
    synchronized (lock) {
        // operation 3
    }
}

// 锁粗化后
public void methodOptimized() {
    synchronized (lock) {
        // operation 1
        // operation 2
        // operation 3
    }
}
```

**另一个典型场景**：
```java
// StringBuffer的连续append
public String concat(String s1, String s2, String s3) {
    StringBuffer sb = new StringBuffer();
    sb.append(s1);  // 每个append都有同步
    sb.append(s2);
    sb.append(s3);
    return sb.toString();
}

// 锁粗化后，三个append在一个同步块内
```

### 13.3.4 轻量级锁（Lightweight Locking）

**设计初衷**：在没有多线程竞争的前提下，减少传统的重量级锁使用操作系统互斥量产生的性能消耗。

**对象头（Object Header）**：

HotSpot虚拟机的对象头（Object Header）分为两部分：
- **Mark Word**：存储对象的hashCode、分代年龄、锁状态标志、偏向线程ID等
- **Class Metadata Address**：指向对象所属类的元数据

**Mark Word的结构**（32位JVM）：

| 锁状态 | 存储内容 | 标志位 |
|--------|----------|--------|
| 无锁 | 对象的hashCode、分代年龄 | 01 |
| 偏向锁 | 偏向线程ID、偏向时间戳、分代年龄 | 01 |
| 轻量级锁 | 指向栈中锁记录的指针 | 00 |
| 重量级锁 | 指向互斥量（重量级锁）的指针 | 10 |
| GC标记 | 空 | 11 |

**轻量级锁的加锁过程**：

1. **代码进入同步块时**：
   - 如果同步对象锁状态为无锁状态（锁标志位为"01"状态，是否为偏向锁为"0"）
   - 虚拟机首先将在当前线程的栈帧中建立一个名为锁记录（Lock Record）的空间，用于存储锁对象目前的Mark Word的拷贝

2. **CAS操作**：
   - 虚拟机将使用CAS操作尝试把对象的Mark Word更新为指向Lock Record的指针
   - 如果这个更新动作成功了，即代表该线程拥有了这个对象的锁，并且对象Mark Word的锁标志位将转变为"00"，表示此对象处于轻量级锁定状态

3. **失败处理**：
   - 如果这个更新操作失败了，虚拟机首先会检查对象的Mark Word是否指向当前线程的栈帧
   - 如果是，说明当前线程已经拥有了这个对象的锁，那就可以直接进入同步块继续执行
   - 否则说明这个锁对象已经被其他线程抢占了

**轻量级锁的解锁过程**：

1. 使用CAS操作把对象当前的Mark Word和线程中复制的Displaced Mark Word替换回来
2. 如果替换成功，整个同步过程就完成了
3. 如果替换失败，说明有其他线程尝试过获取该锁，就要在释放锁的同时，唤醒被挂起的线程

**轻量级锁的适用场景**：
- 线程交替执行同步块的场景
- 不存在多线程竞争或竞争不激烈

**轻量级锁膨胀**：

如果存在两条以上的线程争用同一个锁，那轻量级锁就不再有效，必须要膨胀为重量级锁，锁标志的状态值变为"10"，Mark Word中存储的就是指向重量级锁（互斥量）的指针，后面等待锁的线程也要进入阻塞状态。

### 13.3.5 偏向锁（Biased Locking）

**设计初衷**：消除数据在无竞争情况下的同步原语，进一步提高程序的运行性能。

**核心思想**：
- 假设锁不仅不存在多线程竞争，而且总是由同一线程多次获得
- 为了让线程获得锁的代价更低，引入了偏向锁
- 锁会偏向于第一个获得它的线程，如果在接下来的执行过程中，该锁没有被其他的线程获取，则持有偏向锁的线程将永远不需要再进行同步

**偏向锁的获取过程**：

1. 访问Mark Word中偏向锁的标识是否设置成1，锁标志位是否为01
2. 确认为可偏向状态后，测试线程ID是否指向当前线程
3. 如果测试成功，表示线程已经获得了锁
4. 如果测试失败，则需要再测试一下Mark Word中偏向锁的标识是否设置成1
5. 如果没有设置，则使用CAS竞争锁
6. 如果设置了，则尝试使用CAS将对象头的偏向锁指向当前线程

**偏向锁的撤销**：

偏向锁使用了一种等到竞争出现才释放锁的机制，所以当其他线程尝试竞争偏向锁时，持有偏向锁的线程才会释放锁。

**撤销过程**：
1. 等待全局安全点（在这个时间点上没有正在执行的字节码）
2. 暂停拥有偏向锁的线程，检查持有偏向锁的线程是否活着
3. 如果线程不处于活动状态，则将对象头设置成无锁状态
4. 如果线程仍然活着，拥有偏向锁的栈会被执行，遍历偏向对象的锁记录，栈中的锁记录和对象头的Mark Word要么重新偏向于其他线程，要么恢复到无锁或者标记对象不适合作为偏向锁
5. 最后唤醒暂停的线程

**偏向锁的关闭**：

偏向锁在Java 6和Java 7里是默认启用的，但是它在应用程序启动几秒钟之后才激活，如有必要可以使用JVM参数来关闭延迟：

```bash
-XX:BiasedLockingStartupDelay=0  # 立即启用偏向锁
-XX:-UseBiasedLocking            # 关闭偏向锁
```

**锁升级过程总结**：

```
无锁 → 偏向锁 → 轻量级锁 → 重量级锁
      (1个线程)  (交替执行)  (竞争激烈)
```

**锁升级的目的**：
- 在几乎没有锁竞争的场合，使用偏向锁进一步提高性能
- 在轻度锁竞争的场合，使用轻量级锁减少互斥量开销
- 在重度锁竞争的场合，使用重量级锁保证正确性

**锁优化技术对比**：

| 优化技术 | 适用场景 | 核心思想 |
|----------|----------|----------|
| 自旋锁 | 锁持有时间短 | 忙等待，避免线程切换 |
| 锁消除 | 不可能存在竞争 | 编译期消除不必要的锁 |
| 锁粗化 | 频繁加解锁 | 扩大同步范围，减少加解锁次数 |
| 轻量级锁 | 线程交替执行 | CAS替代互斥量 |
| 偏向锁 | 单线程重复获取 | 记录线程ID，避免CAS |

## 13.4 本章小结

本章介绍了线程安全的基本概念和实现方法，以及HotSpot虚拟机为了提升并发性能而进行的各种锁优化技术。

**线程安全的实现方法**：
1. **互斥同步**：synchronized、ReentrantLock，保证互斥性但性能开销大
2. **非阻塞同步**：CAS原子操作，乐观并发策略，无阻塞但可能自旋
3. **无同步方案**：可重入代码、线程本地存储，从根本上避免竞争

**锁优化技术**：
1. **自适应自旋**：根据历史情况动态调整自旋时间
2. **锁消除**：逃逸分析后消除不可能竞争的锁
3. **锁粗化**：扩大同步范围减少加解锁次数
4. **轻量级锁**：CAS操作替代操作系统互斥量
5. **偏向锁**：单线程场景下记录线程ID避免CAS

**锁升级过程**：无锁 → 偏向锁 → 轻量级锁 → 重量级锁

理解这些概念和技术，对于编写高效且正确的并发程序至关重要。在实际开发中，应该根据具体场景选择合适的线程安全方案，既要保证正确性，也要考虑性能。
