# java.util 包代码说明文档

## 一、代码结构

### 1.1 包结构概览

```
com.linsir.abc.core.base.util/
├── collection/                     # 集合框架
│   ├── list/                       # List实现
│   │   ├── ArrayListImplementation.java        # 动态数组实现
│   │   ├── LinkedListImplementation.java       # 双向链表实现
│   │   └── ListPerformanceComparison.java      # 性能对比
│   ├── set/                        # Set实现
│   │   ├── HashSetImplementation.java          # 哈希集合实现
│   │   └── TreeSetImplementation.java          # 树形集合实现
│   ├── map/                        # Map实现
│   │   ├── HashMapImplementation.java          # 哈希表实现
│   │   ├── TreeMapImplementation.java          # 红黑树Map实现
│   │   └── LinkedHashMapImplementation.java    # 链表哈希Map实现
│   └── queue/                      # Queue实现
│       ├── PriorityQueueImplementation.java    # 优先队列实现
│       └── ArrayDequeImplementation.java       # 双端队列实现
├── stream/                         # Stream API
│   ├── StreamPipelineBuilder.java              # 流管道构建
│   ├── ParallelStreamProcessor.java            # 并行流处理
│   └── CustomCollector.java                    # 自定义收集器
└── concurrent/                     # 并发包
    ├── collection/                 # 并发集合
    │   ├── ConcurrentHashMapImplementation.java  # 并发哈希表
    │   └── CopyOnWriteArrayListImplementation.java # 写时复制列表
    ├── executor/                   # 线程池
    │   ├── ThreadPoolExecutorImplementation.java   # 线程池执行器
    │   ├── ScheduledExecutorImplementation.java    # 定时执行器
    │   └── TaskRejectHandler.java                  # 任务拒绝策略
    └── lock/                       # 锁机制
        ├── ReentrantLockImplementation.java        # 可重入锁
        ├── ReadWriteLockImplementation.java        # 读写锁
        └── ConditionVariable.java                  # 条件变量
```

### 1.2 各类详细结构

#### 1.2.1 ArrayListImplementation.java

**对应JDK类**: `java.util.ArrayList`

**JDK源码位置**: `java.base/java/util/ArrayList.java`

**类结构**:
```java
public class ArrayListImplementation<E> implements Iterable<E> {
    // 构造器
    public ArrayListImplementation()                           // 默认容量10
    public ArrayListImplementation(int initialCapacity)        // 指定容量
    
    // 基本操作
    public int size()                                          // 元素数量
    public boolean isEmpty()                                   // 是否为空
    public boolean add(E e)                                    // 添加元素
    public void add(int index, E element)                      // 指定位置添加
    public E get(int index)                                    // 获取元素
    public E set(int index, E element)                         // 设置元素
    public E remove(int index)                                 // 删除元素
    public boolean remove(Object o)                            // 删除对象
    public boolean contains(Object o)                          // 是否包含
    public void clear()                                        // 清空
    
    // 扩容机制
    private void grow(int minCapacity)                         // 扩容方法
}
```

**设计要点**:
- 基于数组存储元素，支持快速随机访问
- 动态扩容机制：容量不足时扩容为原来的1.5倍
- 非线程安全，单线程环境使用
- 时间复杂度：get/set O(1)，add/remove O(n)

**JDK对应方法**:
| 本类方法 | JDK方法 | 说明 |
|----------|---------|------|
| `ArrayListImplementation()` | `ArrayList()` | 默认构造 |
| `size()` | `ArrayList.size()` | 获取大小 |
| `add(E)` | `ArrayList.add(E)` | 添加元素 |
| `add(int, E)` | `ArrayList.add(int, E)` | 指定位置添加 |
| `get(int)` | `ArrayList.get(int)` | 获取元素 |
| `set(int, E)` | `ArrayList.set(int, E)` | 设置元素 |
| `remove(int)` | `ArrayList.remove(int)` | 删除指定位置 |
| `remove(Object)` | `ArrayList.remove(Object)` | 删除指定对象 |
| `grow()` | `ArrayList.grow()` | 扩容方法（private） |

**扩容流程**:
```
初始容量: 10
添加元素 -> 容量不足 -> 扩容: 10 * 1.5 = 15
继续添加 -> 容量不足 -> 扩容: 15 * 1.5 = 22
...
```

#### 1.2.2 LinkedListImplementation.java

**对应JDK类**: `java.util.LinkedList`

**JDK源码位置**: `java.base/java/util/LinkedList.java`

**类结构**:
```java
public class LinkedListImplementation<E> implements Iterable<E> {
    // 节点类
    private static class Node<E> {
        E item;
        Node<E> next;
        Node<E> prev;
    }
    
    // 构造器
    public LinkedListImplementation()
    
    // 基本操作
    public int size()
    public boolean isEmpty()
    public boolean add(E e)
    public void add(int index, E element)
    public E get(int index)
    public E remove(int index)
    
    // 双端队列操作
    public void addFirst(E e)                                  // 头部添加
    public void addLast(E e)                                   // 尾部添加
    public E removeFirst()                                     // 头部删除
    public E removeLast()                                      // 尾部删除
    public E getFirst()                                        // 获取头部
    public E getLast()                                         // 获取尾部
}
```

**设计要点**:
- 基于双向链表结构，支持高效的头尾操作
- 不需要扩容，动态分配节点内存
- 非线程安全
- 时间复杂度：get O(n)，addFirst/addLast O(1)

**JDK对应方法**:
| 本类方法 | JDK方法 | 说明 |
|----------|---------|------|
| `LinkedListImplementation()` | `LinkedList()` | 默认构造 |
| `addFirst(E)` | `LinkedList.addFirst(E)` | 头部添加 |
| `addLast(E)` | `LinkedList.addLast(E)` | 尾部添加 |
| `removeFirst()` | `LinkedList.removeFirst()` | 头部删除 |
| `removeLast()` | `LinkedList.removeLast()` | 尾部删除 |
| `getFirst()` | `LinkedList.getFirst()` | 获取头部 |
| `getLast()` | `LinkedList.getLast()` | 获取尾部 |
| `Node` | `LinkedList.Node` | 节点内部类 |

#### 1.2.3 HashMapImplementation.java

**对应JDK类**: `java.util.HashMap`

**JDK源码位置**: `java.base/java/util/HashMap.java`

**类结构**:
```java
public class HashMapImplementation<K, V> {
    // 节点类
    static class Node<K, V> {
        final int hash;                                        // 哈希值
        final K key;                                           // 键
        V value;                                               // 值
        Node<K, V> next;                                       // 下一个节点
    }
    
    // 构造器
    public HashMapImplementation()
    public HashMapImplementation(int initialCapacity)
    
    // 基本操作
    public int size()
    public boolean isEmpty()
    public V put(K key, V value)                               // 添加键值对
    public V get(Object key)                                   // 获取值
    public V remove(Object key)                                // 删除键值对
    public boolean containsKey(Object key)                     // 是否包含键
    public boolean containsValue(Object value)                 // 是否包含值
    public void clear()
    
    // 内部方法
    private int hash(Object key)                               // 哈希函数
    private Node<K, V>[] resize()                              // 扩容方法
    private void treeifyBin(Node<K, V>[] tab, int hash)        // 链表转红黑树
}
```

**设计要点**:
- 基于数组+链表/红黑树的存储结构
- 哈希冲突解决：链地址法（链表长度>8时转红黑树）
- 负载因子0.75，扩容时重新哈希
- 允许null键和null值
- 非线程安全

**JDK对应方法**:
| 本类方法 | JDK方法 | 说明 |
|----------|---------|------|
| `HashMapImplementation()` | `HashMap()` | 默认构造 |
| `put(K, V)` | `HashMap.put(K, V)` | 添加键值对 |
| `get(Object)` | `HashMap.get(Object)` | 获取值 |
| `remove(Object)` | `HashMap.remove(Object)` | 删除键值对 |
| `containsKey()` | `HashMap.containsKey()` | 包含键检查 |
| `hash()` | `HashMap.hash()` | 哈希函数 |
| `resize()` | `HashMap.resize()` | 扩容方法 |
| `treeifyBin()` | `HashMap.treeifyBin()` | 链表转红黑树 |
| `Node` | `HashMap.Node` | 节点类 |

**HashMap存储结构**:
```
数组(桶数组):
[0] -> Node(key1, value1) -> Node(key2, value2) -> null
[1] -> null
[2] -> Node(key3, value3) -> null
[3] -> TreeNode (红黑树，当链表长度>8)
...
[n-1] -> null
```

#### 1.2.4 TreeMapImplementation.java

**对应JDK类**: `java.util.TreeMap`

**JDK源码位置**: `java.base/java/util/TreeMap.java`

**类结构**:
```java
public class TreeMapImplementation<K extends Comparable<K>, V> {
    // 节点类（红黑树）
    private static class Entry<K, V> {
        K key;
        V value;
        Entry<K, V> left;                                      // 左子节点
        Entry<K, V> right;                                     // 右子节点
        Entry<K, V> parent;                                    // 父节点
        boolean color;                                         // 颜色（红黑）
    }
    
    // 基本操作
    public V put(K key, V value)
    public V get(Object key)
    public V remove(Object key)
    public K firstKey()                                        // 最小键
    public K lastKey()                                         // 最大键
    
    // 红黑树操作
    private void rotateLeft(Entry<K, V> p)                     // 左旋
    private void rotateRight(Entry<K, V> p)                    // 右旋
    private void fixAfterInsertion(Entry<K, V> x)              // 插入修复
    private void fixAfterDeletion(Entry<K, V> x)               // 删除修复
}
```

**设计要点**:
- 基于红黑树实现，保证O(log n)的操作时间复杂度
- 键必须实现Comparable接口或提供Comparator
- 按键的自然顺序或指定顺序排序
- 非线程安全

**JDK对应方法**:
| 本类方法 | JDK方法 | 说明 |
|----------|---------|------|
| `put(K, V)` | `TreeMap.put(K, V)` | 添加键值对 |
| `get(Object)` | `TreeMap.get(Object)` | 获取值 |
| `remove(Object)` | `TreeMap.remove(Object)` | 删除键值对 |
| `firstKey()` | `TreeMap.firstKey()` | 最小键 |
| `lastKey()` | `TreeMap.lastKey()` | 最大键 |
| `rotateLeft()` | `TreeMap.rotateLeft()` | 左旋操作 |
| `rotateRight()` | `TreeMap.rotateRight()` | 右旋操作 |
| `fixAfterInsertion()` | `TreeMap.fixAfterInsertion()` | 插入修复 |
| `Entry` | `TreeMap.Entry` | 红黑树节点 |

#### 1.2.5 PriorityQueueImplementation.java

**对应JDK类**: `java.util.PriorityQueue`

**JDK源码位置**: `java.base/java/util/PriorityQueue.java`

**类结构**:
```java
public class PriorityQueueImplementation<E extends Comparable<E>> {
    // 存储结构（完全二叉堆）
    private Object[] queue;
    
    // 基本操作
    public boolean offer(E e)                                  // 添加元素
    public E poll()                                            // 移除并返回队首
    public E peek()                                            // 查看队首
    public int size()
    public boolean isEmpty()
    
    // 堆操作
    private void siftUp(int k, E x)                            // 上浮
    private void siftDown(int k, E x)                          // 下沉
    private void heapify()                                     // 建堆
}
```

**设计要点**:
- 基于完全二叉堆（小顶堆）实现
- 队首元素是优先级最高的元素（最小值）
- offer/poll操作时间复杂度O(log n)
- peek操作时间复杂度O(1)
- 非线程安全

**JDK对应方法**:
| 本类方法 | JDK方法 | 说明 |
|----------|---------|------|
| `offer(E)` | `PriorityQueue.offer(E)` | 添加元素 |
| `poll()` | `PriorityQueue.poll()` | 移除并返回队首 |
| `peek()` | `PriorityQueue.peek()` | 查看队首 |
| `siftUp()` | `PriorityQueue.siftUp()` | 上浮调整 |
| `siftDown()` | `PriorityQueue.siftDown()` | 下沉调整 |
| `heapify()` | `PriorityQueue.heapify()` | 建堆 |

**堆结构示例**:
```
        1
      /   \
     3     5
    / \   /
   7   9 8
```

#### 1.2.6 StreamPipelineBuilder.java

**对应JDK类**: `java.util.stream.Stream`

**JDK源码位置**: `java.base/java/util/stream/Stream.java`

**类结构**:
```java
public class StreamPipelineBuilder<T> {
    // 中间操作
    public StreamPipelineBuilder<T> filter(Predicate<T> predicate)      // 过滤
    public <R> StreamPipelineBuilder<R> map(Function<T, R> mapper)      // 映射
    public StreamPipelineBuilder<T> sorted()                            // 排序
    public StreamPipelineBuilder<T> distinct()                          // 去重
    public StreamPipelineBuilder<T> limit(long maxSize)                 // 限制
    public StreamPipelineBuilder<T> skip(long n)                        // 跳过
    
    // 终止操作
    public void forEach(Consumer<T> action)                             // 遍历
    public List<T> toList()                                             // 转列表
    public long count()                                                 // 计数
    public Optional<T> findFirst()                                      // 查找首个
    public boolean anyMatch(Predicate<T> predicate)                     // 任意匹配
    public <R, A> R collect(Collector<T, A, R> collector)               // 收集
    
    // 执行管道
    public List<T> execute()                                            // 执行操作链
}
```

**设计要点**:
- 延迟执行（Lazy Evaluation）：中间操作不立即执行
- 管道操作：多个操作串联形成处理链
- 短路操作：limit、findFirst等可以提前终止
- 无状态与有状态操作：distinct、sorted需要维护状态

**JDK对应方法**:
| 本类方法 | JDK方法 | 说明 |
|----------|---------|------|
| `filter()` | `Stream.filter()` | 过滤操作 |
| `map()` | `Stream.map()` | 映射操作 |
| `sorted()` | `Stream.sorted()` | 排序操作 |
| `distinct()` | `Stream.distinct()` | 去重操作 |
| `limit()` | `Stream.limit()` | 限制操作 |
| `skip()` | `Stream.skip()` | 跳过操作 |
| `forEach()` | `Stream.forEach()` | 遍历操作 |
| `count()` | `Stream.count()` | 计数操作 |
| `findFirst()` | `Stream.findFirst()` | 查找首个 |
| `anyMatch()` | `Stream.anyMatch()` | 任意匹配 |
| `collect()` | `Stream.collect()` | 收集操作 |

**Stream执行流程**:
```
数据源 -> 中间操作1 -> 中间操作2 -> ... -> 终止操作
         (filter)      (map)              (collect)
```

#### 1.2.7 ThreadPoolExecutorImplementation.java

**对应JDK类**: `java.util.concurrent.ThreadPoolExecutor`

**JDK源码位置**: `java.base/java/util/concurrent/ThreadPoolExecutor.java`

**类结构**:
```java
public class ThreadPoolExecutorImplementation {
    // 线程池状态
    private static final int RUNNING    = -1 << 29;            // 运行中
    private static final int SHUTDOWN   =  0 << 29;            // 关闭
    private static final int STOP       =  1 << 29;            // 停止
    private static final int TIDYING    =  2 << 29;            // 整理中
    private static final int TERMINATED =  3 << 29;            // 已终止
    
    // 核心参数
    private final int corePoolSize;                            // 核心线程数
    private final int maximumPoolSize;                         // 最大线程数
    private final long keepAliveTime;                          // 空闲线程存活时间
    private final BlockingQueue<Runnable> workQueue;           // 任务队列
    private final ThreadFactory threadFactory;                 // 线程工厂
    private final RejectedExecutionHandler handler;            // 拒绝策略
    
    // 操作方法
    public void execute(Runnable command)                      // 执行任务
    public void shutdown()                                     // 优雅关闭
    public void shutdownNow()                                  // 立即关闭
    public boolean isShutdown()
    public boolean isTerminated()
    
    // 拒绝策略
    public static class AbortPolicy implements RejectedExecutionHandler      // 抛出异常
    public static class CallerRunsPolicy implements RejectedExecutionHandler // 调用者执行
    public static class DiscardPolicy implements RejectedExecutionHandler    // 静默丢弃
    public static class DiscardOldestPolicy implements RejectedExecutionHandler // 丢弃最旧
}
```

**设计要点**:
- 核心线程数：常驻线程数量，即使空闲也保留
- 最大线程数：线程池允许的最大线程数
- 任务队列：存储等待执行的任务
- 拒绝策略：当线程池和队列都满时的处理策略

**JDK对应方法**:
| 本类方法 | JDK方法 | 说明 |
|----------|---------|------|
| `execute()` | `ThreadPoolExecutor.execute()` | 执行任务 |
| `shutdown()` | `ThreadPoolExecutor.shutdown()` | 优雅关闭 |
| `shutdownNow()` | `ThreadPoolExecutor.shutdownNow()` | 立即关闭 |
| `isShutdown()` | `ThreadPoolExecutor.isShutdown()` | 是否关闭 |
| `isTerminated()` | `ThreadPoolExecutor.isTerminated()` | 是否终止 |
| `AbortPolicy` | `ThreadPoolExecutor.AbortPolicy` | 拒绝异常策略 |
| `CallerRunsPolicy` | `ThreadPoolExecutor.CallerRunsPolicy` | 调用者执行策略 |
| `DiscardPolicy` | `ThreadPoolExecutor.DiscardPolicy` | 静默丢弃策略 |
| `DiscardOldestPolicy` | `ThreadPoolExecutor.DiscardOldestPolicy` | 丢弃最旧策略 |

**线程池工作流程**:
```
提交任务 -> 核心线程是否已满?
    否 -> 创建核心线程执行任务
    是 -> 任务队列是否已满?
        否 -> 加入任务队列
        是 -> 线程数是否达到最大?
            否 -> 创建非核心线程执行任务
            是 -> 执行拒绝策略
```

#### 1.2.8 ConcurrentHashMapImplementation.java

**对应JDK类**: `java.util.concurrent.ConcurrentHashMap`

**JDK源码位置**: `java.base/java/util/concurrent/ConcurrentHashMap.java`

**类结构**:
```java
public class ConcurrentHashMapImplementation<K, V> {
    // 段（Segment）结构
    static class Segment<K, V> {
        final ReadWriteLock lock;                              // 读写锁
        volatile HashEntry<K, V>[] table;                      // 哈希表
    }
    
    // 构造器
    public ConcurrentHashMapImplementation()
    public ConcurrentHashMapImplementation(int initialCapacity)
    
    // 基本操作（线程安全）
    public V put(K key, V value)
    public V get(Object key)
    public V remove(Object key)
    public int size()
    public boolean isEmpty()
    
    // 并发控制
    private Segment<K, V> segmentFor(int hash)                 // 获取段
    private int hash(Object key)                               // 哈希函数
}
```

**设计要点**:
- 分段锁（Segment）：将哈希表分成多个段，每段独立加锁
- 读操作：不需要加锁，使用volatile保证可见性
- 写操作：只锁定相关段，不同段可并行写入
- 高并发场景下性能优于Hashtable和Collections.synchronizedMap

**JDK对应方法**:
| 本类方法 | JDK方法 | 说明 |
|----------|---------|------|
| `put(K, V)` | `ConcurrentHashMap.put(K, V)` | 线程安全添加 |
| `get(Object)` | `ConcurrentHashMap.get(Object)` | 线程安全获取 |
| `remove(Object)` | `ConcurrentHashMap.remove(Object)` | 线程安全删除 |
| `size()` | `ConcurrentHashMap.size()` | 获取大小 |
| `Segment` | `ConcurrentHashMap.Segment` (JDK7) | 分段锁结构 |
| `segmentFor()` | `ConcurrentHashMap.segmentFor()` | 获取段（JDK7） |

**分段锁结构**:
```
ConcurrentHashMap:
├── Segment[0] (锁A)
│   └── table[0...n]
├── Segment[1] (锁B)
│   └── table[0...n]
├── Segment[2] (锁C)
│   └── table[0...n]
...
└── Segment[15] (锁P)
    └── table[0...n]
```

#### 1.2.9 ReentrantLockImplementation.java

**对应JDK类**: `java.util.concurrent.locks.ReentrantLock`

**JDK源码位置**: `java.base/java/util/concurrent/locks/ReentrantLock.java`

**类结构**:
```java
public class ReentrantLockImplementation implements Lock {
    // 同步器
    private final Sync sync;
    
    // 抽象同步器
    abstract static class Sync extends AbstractQueuedSynchronizer {
        abstract void lock();
        final boolean nonfairTryAcquire(int acquires)          // 非公平获取
        protected final boolean tryRelease(int releases)       // 尝试释放
    }
    
    // 公平/非公平同步器
    static final class NonfairSync extends Sync                // 非公平锁
    static final class FairSync extends Sync                   // 公平锁
    
    // 锁操作
    public void lock()                                         // 获取锁
    public void lockInterruptibly() throws InterruptedException // 可中断获取
    public boolean tryLock()                                   // 尝试获取
    public boolean tryLock(long timeout, TimeUnit unit)        // 超时获取
    public void unlock()                                       // 释放锁
    public Condition newCondition()                            // 创建条件变量
    public boolean isLocked()                                  // 是否被锁定
    public boolean isHeldByCurrentThread()                     // 当前线程是否持有
    public int getHoldCount()                                  // 持有计数
}
```

**设计要点**:
- 可重入：同一线程可以多次获取锁，计数器记录重入次数
- 公平/非公平：公平锁按请求顺序获取，非公平锁允许插队
- 独占锁：同一时间只有一个线程可以持有锁
- 条件变量：支持多个等待队列

**JDK对应方法**:
| 本类方法 | JDK方法 | 说明 |
|----------|---------|------|
| `lock()` | `ReentrantLock.lock()` | 获取锁 |
| `lockInterruptibly()` | `ReentrantLock.lockInterruptibly()` | 可中断获取 |
| `tryLock()` | `ReentrantLock.tryLock()` | 尝试获取 |
| `tryLock(long, TimeUnit)` | `ReentrantLock.tryLock(long, TimeUnit)` | 超时获取 |
| `unlock()` | `ReentrantLock.unlock()` | 释放锁 |
| `newCondition()` | `ReentrantLock.newCondition()` | 创建条件变量 |
| `isLocked()` | `ReentrantLock.isLocked()` | 是否被锁定 |
| `isHeldByCurrentThread()` | `ReentrantLock.isHeldByCurrentThread()` | 当前线程是否持有 |
| `getHoldCount()` | `ReentrantLock.getHoldCount()` | 持有计数 |
| `Sync` | `ReentrantLock.Sync` | 同步器抽象类 |
| `NonfairSync` | `ReentrantLock.NonfairSync` | 非公平同步器 |
| `FairSync` | `ReentrantLock.FairSync` | 公平同步器 |

**与synchronized对比**:

| 特性 | ReentrantLock | synchronized |
|------|---------------|--------------|
| 可重入 | 支持 | 支持 |
| 公平性 | 支持公平/非公平 | 非公平 |
| 可中断 | 支持 | 不支持 |
| 超时获取 | 支持 | 不支持 |
| 多条件变量 | 支持 | 不支持 |
| 性能 | 高 | 高（JDK6+优化后） |

---

## 二、代码使用场景

### 2.1 ArrayListImplementation - 动态数组

**适用场景**:
- 频繁随机访问元素（get/set）
- 元素数量相对固定或增长缓慢
- 单线程环境
- 需要快速遍历的场景

**使用示例**:
```java
// 创建列表
ArrayListImplementation<String> list = new ArrayListImplementation<>();

// 添加元素
list.add("元素1");
list.add("元素2");
list.add(1, "插入元素");  // 在索引1处插入

// 访问元素
String first = list.get(0);
list.set(1, "修改后");

// 删除元素
list.remove(0);              // 按索引删除
list.remove("元素2");        // 按对象删除

// 遍历
for (String item : list) {
    System.out.println(item);
}
```

### 2.2 LinkedListImplementation - 双向链表

**适用场景**:
- 频繁在头尾添加/删除元素
- 实现队列或栈
- 不需要频繁随机访问
- 单线程环境

**使用示例**:
```java
// 创建链表
LinkedListImplementation<String> list = new LinkedListImplementation<>();

// 双端操作
list.addFirst("头部");
list.addLast("尾部");
String first = list.getFirst();
String last = list.getLast();
list.removeFirst();
list.removeLast();

// 作为队列使用（FIFO）
list.addLast("任务1");
list.addLast("任务2");
String task = list.removeFirst();  // 取出最先加入的任务

// 作为栈使用（LIFO）
list.addLast("数据1");
list.addLast("数据2");
String data = list.removeLast();   // 取出最后加入的数据
```

### 2.3 HashMapImplementation - 哈希表

**适用场景**:
- 键值对存储和快速查找
- 不需要排序的场景
- 单线程环境
- 缓存实现

**使用示例**:
```java
// 创建Map
HashMapImplementation<String, Integer> map = new HashMapImplementation<>();

// 添加键值对
map.put("key1", 100);
map.put("key2", 200);

// 获取值
Integer value = map.get("key1");

// 检查键/值
boolean hasKey = map.containsKey("key1");
boolean hasValue = map.containsValue(100);

// 删除键值对
map.remove("key1");

// 遍历
for (Map.Entry<String, Integer> entry : map.entrySet()) {
    System.out.println(entry.getKey() + " = " + entry.getValue());
}
```

### 2.4 TreeMapImplementation - 红黑树Map

**适用场景**:
- 需要按键排序的场景
- 需要范围查询（如查找某个范围内的键）
- 需要获取最小/最大键
- 单线程环境

**使用示例**:
```java
// 创建TreeMap
TreeMapImplementation<Integer, String> map = new TreeMapImplementation<>();

// 添加键值对（自动按键排序）
map.put(3, "C");
map.put(1, "A");
map.put(2, "B");

// 获取最小/最大键
Integer min = map.firstKey();   // 1
Integer max = map.lastKey();    // 3

// 范围查询
Map<Integer, String> subMap = map.subMap(1, 3);  // 包含1，不包含3
```

### 2.5 PriorityQueueImplementation - 优先队列

**适用场景**:
- 任务调度（按优先级执行）
- 实现Top K问题
- 合并有序序列
- 单线程环境

**使用示例**:
```java
// 创建优先队列（小顶堆）
PriorityQueueImplementation<Integer> queue = new PriorityQueueImplementation<>();

// 添加元素
queue.offer(5);
queue.offer(1);
queue.offer(3);

// 获取并移除队首（最小值）
Integer min = queue.poll();  // 1
Integer next = queue.poll(); // 3

// 查看队首不移除
Integer peek = queue.peek(); // 5
```

### 2.6 StreamPipelineBuilder - Stream流处理

**适用场景**:
- 集合数据的过滤、映射、转换
- 复杂的数据处理管道
- 函数式编程风格
- 延迟执行优化性能

**使用示例**:
```java
// 创建Stream
StreamPipelineBuilder<Integer> stream = StreamPipelineBuilder.of(1, 2, 3, 4, 5);

// 链式操作
List<Integer> result = stream
    .filter(n -> n > 2)           // 过滤大于2的
    .map(n -> n * n)              // 映射为平方
    .sorted((a, b) -> b - a)      // 降序排序
    .limit(2)                     // 限制2个
    .toList();                    // 收集为列表
// 结果: [25, 16]

// 复杂收集
Map<String, List<Integer>> grouped = StreamPipelineBuilder.of(1, 2, 3, 4, 5, 6)
    .collect(Collectors.groupingBy(n -> n % 2 == 0 ? "偶数" : "奇数"));
// 结果: {奇数=[1, 3, 5], 偶数=[2, 4, 6]}
```

### 2.7 ThreadPoolExecutorImplementation - 线程池

**适用场景**:
- 异步任务执行
- 批量任务处理
- 定时任务调度
- 限制并发线程数

**使用示例**:
```java
// 创建线程池
BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(100);
ThreadFactory factory = Executors.defaultThreadFactory();
ThreadPoolExecutorImplementation.RejectedExecutionHandler handler = 
    new ThreadPoolExecutorImplementation.CallerRunsPolicy();

ThreadPoolExecutorImplementation executor = new ThreadPoolExecutorImplementation(
    5,                      // 核心线程数
    10,                     // 最大线程数
    60L,                    // 空闲线程存活时间
    TimeUnit.SECONDS,       // 时间单位
    queue,                  // 任务队列
    factory,                // 线程工厂
    handler                 // 拒绝策略
);

// 提交任务
executor.execute(() -> {
    System.out.println("任务执行: " + Thread.currentThread().getName());
});

// 优雅关闭
executor.shutdown();
executor.awaitTermination(60, TimeUnit.SECONDS);
```

### 2.8 ConcurrentHashMapImplementation - 并发哈希表

**适用场景**:
- 高并发读写的键值对存储
- 缓存实现（多线程环境）
- 计数器（如访问统计）
- 线程安全的配置存储

**使用示例**:
```java
// 创建并发Map
ConcurrentHashMapImplementation<String, Integer> map = new ConcurrentHashMapImplementation<>();

// 多线程并发操作
ExecutorService executor = Executors.newFixedThreadPool(10);

for (int i = 0; i < 100; i++) {
    final int index = i;
    executor.execute(() -> {
        map.put("key" + index, index);
        Integer value = map.get("key" + index);
        System.out.println("Thread: " + Thread.currentThread().getName() + 
                          ", Key: key" + index + ", Value: " + value);
    });
}

executor.shutdown();
```

### 2.9 ReentrantLockImplementation - 可重入锁

**适用场景**:
- 需要更灵活的锁控制
- 需要可中断的锁获取
- 需要超时获取锁
- 需要多个条件变量

**使用示例**:
```java
// 创建锁（非公平）
ReentrantLockImplementation lock = new ReentrantLockImplementation();
Condition condition = lock.newCondition();

// 基本使用
lock.lock();
try {
    // 临界区代码
    System.out.println("获取锁，执行临界区代码");
} finally {
    lock.unlock();
}

// 可中断获取
try {
    lock.lockInterruptibly();
    try {
        // 临界区代码
    } finally {
        lock.unlock();
    }
} catch (InterruptedException e) {
    // 处理中断
}

// 超时获取
if (lock.tryLock(5, TimeUnit.SECONDS)) {
    try {
        // 临界区代码
    } finally {
        lock.unlock();
    }
} else {
    System.out.println("获取锁超时");
}
```

---

## 三、测试代码结构

### 3.1 测试包结构

```
src/test/java/com/linsir/abc/core/base/util/
├── collection/
│   ├── list/
│   │   ├── ArrayListImplementationTest.java
│   │   ├── LinkedListImplementationTest.java
│   │   └── ListPerformanceComparisonTest.java
│   ├── set/
│   │   ├── HashSetImplementationTest.java
│   │   └── TreeSetImplementationTest.java
│   ├── map/
│   │   ├── HashMapImplementationTest.java
│   │   ├── TreeMapImplementationTest.java
│   │   └── LinkedHashMapImplementationTest.java
│   └── queue/
│       ├── PriorityQueueImplementationTest.java
│       └── ArrayDequeImplementationTest.java
├── stream/
│   ├── StreamPipelineBuilderTest.java
│   ├── ParallelStreamProcessorTest.java
│   └── CustomCollectorTest.java
└── concurrent/
    ├── collection/
    │   ├── ConcurrentHashMapImplementationTest.java
    │   └── CopyOnWriteArrayListImplementationTest.java
    ├── executor/
    │   ├── ThreadPoolExecutorImplementationTest.java
    │   ├── ScheduledExecutorImplementationTest.java
    │   └── TaskRejectHandlerTest.java
    └── lock/
        ├── ReentrantLockImplementationTest.java
        ├── ReadWriteLockImplementationTest.java
        └── ConditionVariableTest.java
```

### 3.2 测试类结构示例

#### ArrayListImplementationTest.java

```java
public class ArrayListImplementationTest {
    // 构造器测试
    @Test void testDefaultConstructor()           // 默认构造
    @Test void testConstructorWithCapacity()       // 指定容量构造
    
    // 基本操作测试
    @Test void testAdd()                           // 添加元素
    @Test void testAddAtIndex()                    // 指定位置添加
    @Test void testGet()                           // 获取元素
    @Test void testSet()                           // 设置元素
    @Test void testRemove()                        // 删除元素
    @Test void testContains()                      // 包含检查
    @Test void testClear()                         // 清空
    
    // 边界测试
    @Test void testAddAtIndexOutOfBounds()         // 索引越界
    @Test void testGetOutOfBounds()                // 获取越界
    @Test void testRemoveOutOfBounds()             // 删除越界
    
    // 扩容测试
    @Test void testGrow()                          // 扩容机制
    @Test void testIterator()                      // 迭代器
}
```

#### HashMapImplementationTest.java

```java
public class HashMapImplementationTest {
    // 基本操作测试
    @Test void testPut()                           // 添加键值对
    @Test void testGet()                           // 获取值
    @Test void testRemove()                        // 删除键值对
    @Test void testContainsKey()                   // 包含键
    @Test void testContainsValue()                 // 包含值
    @Test void testSize()                          // 大小
    @Test void testIsEmpty()                       // 是否为空
    @Test void testClear()                         // 清空
    
    // 哈希冲突测试
    @Test void testHashCollision()                 // 哈希冲突处理
    @Test void testResize()                        // 扩容
    @Test void testNullKey()                       // null键
    @Test void testNullValue()                     // null值
}
```

#### ThreadPoolExecutorImplementationTest.java

```java
public class ThreadPoolExecutorImplementationTest {
    // 构造器测试
    @Test void testConstructor()                   // 正常构造
    @Test void testConstructorInvalidArguments()   // 非法参数
    
    // 任务执行测试
    @Test void testExecute()                       // 执行任务
    @Test void testExecuteMultipleTasks()          // 多任务执行
    
    // 生命周期测试
    @Test void testShutdown()                      // 优雅关闭
    @Test void testShutdownNow()                   // 立即关闭
    @Test void testIsShutdown()                    // 是否关闭
    @Test void testIsTerminated()                  // 是否终止
    
    // 拒绝策略测试
    @Test void testAbortPolicy()                   // 拒绝异常
    @Test void testCallerRunsPolicy()              // 调用者执行
    @Test void testDiscardPolicy()                 // 静默丢弃
    @Test void testDiscardOldestPolicy()           // 丢弃最旧
}
```

---

## 四、单元测试预期结果

### 4.1 ArrayListImplementationTest 预期结果

| 测试方法 | 预期结果 | 说明 |
|----------|----------|------|
| `testDefaultConstructor` | ✅ PASS | 默认构造，容量10，size=0 |
| `testConstructorWithCapacity` | ✅ PASS | 指定容量构造成功 |
| `testAdd` | ✅ PASS | 添加元素成功，size增加 |
| `testAddAtIndex` | ✅ PASS | 指定位置插入正确 |
| `testGet` | ✅ PASS | 获取元素正确 |
| `testSet` | ✅ PASS | 设置元素正确，返回旧值 |
| `testRemove` | ✅ PASS | 删除元素正确，返回被删元素 |
| `testContains` | ✅ PASS | 包含检查正确 |
| `testClear` | ✅ PASS | 清空后size=0 |
| `testAddAtIndexOutOfBounds` | ✅ PASS | 抛出IndexOutOfBoundsException |
| `testGetOutOfBounds` | ✅ PASS | 抛出IndexOutOfBoundsException |
| `testGrow` | ✅ PASS | 扩容后容量为原来的1.5倍 |
| `testIterator` | ✅ PASS | 迭代器遍历正确 |

### 4.2 LinkedListImplementationTest 预期结果

| 测试方法 | 预期结果 | 说明 |
|----------|----------|------|
| `testAdd` | ✅ PASS | 添加元素成功 |
| `testAddFirst` | ✅ PASS | 头部添加正确 |
| `testAddLast` | ✅ PASS | 尾部添加正确 |
| `testGetFirst` | ✅ PASS | 获取头部正确 |
| `testGetLast` | ✅ PASS | 获取尾部正确 |
| `testRemoveFirst` | ✅ PASS | 删除头部正确 |
| `testRemoveLast` | ✅ PASS | 删除尾部正确 |
| `testQueueOperations` | ✅ PASS | 队列操作正确（FIFO） |
| `testStackOperations` | ✅ PASS | 栈操作正确（LIFO） |

### 4.3 HashMapImplementationTest 预期结果

| 测试方法 | 预期结果 | 说明 |
|----------|----------|------|
| `testPut` | ✅ PASS | 添加键值对成功 |
| `testGet` | ✅ PASS | 获取值正确 |
| `testRemove` | ✅ PASS | 删除键值对正确 |
| `testContainsKey` | ✅ PASS | 包含键检查正确 |
| `testContainsValue` | ✅ PASS | 包含值检查正确 |
| `testHashCollision` | ✅ PASS | 哈希冲突使用链表解决 |
| `testResize` | ✅ PASS | 扩容后重新哈希 |
| `testNullKey` | ✅ PASS | 支持null键 |
| `testNullValue` | ✅ PASS | 支持null值 |

### 4.4 TreeMapImplementationTest 预期结果

| 测试方法 | 预期结果 | 说明 |
|----------|----------|------|
| `testPut` | ✅ PASS | 添加键值对并保持有序 |
| `testGet` | ✅ PASS | 获取值正确 |
| `testRemove` | ✅ PASS | 删除键值对正确 |
| `testFirstKey` | ✅ PASS | 返回最小键 |
| `testLastKey` | ✅ PASS | 返回最大键 |
| `testSubMap` | ✅ PASS | 范围查询正确 |
| `testTreeBalance` | ✅ PASS | 红黑树平衡维护正确 |

### 4.5 PriorityQueueImplementationTest 预期结果

| 测试方法 | 预期结果 | 说明 |
|----------|----------|------|
| `testOffer` | ✅ PASS | 添加元素并调整堆 |
| `testPoll` | ✅ PASS | 移除并返回队首（最小值） |
| `testPeek` | ✅ PASS | 查看队首不移除 |
| `testHeapOrder` | ✅ PASS | 堆序性质保持正确 |
| `testSiftUp` | ✅ PASS | 上浮调整正确 |
| `testSiftDown` | ✅ PASS | 下沉调整正确 |

### 4.6 StreamPipelineBuilderTest 预期结果

| 测试方法 | 预期结果 | 说明 |
|----------|----------|------|
| `testFilter` | ✅ PASS | 过滤操作正确 |
| `testMap` | ✅ PASS | 映射操作正确 |
| `testSorted` | ✅ PASS | 排序操作正确 |
| `testDistinct` | ✅ PASS | 去重操作正确 |
| `testLimit` | ✅ PASS | 限制操作正确 |
| `testSkip` | ✅ PASS | 跳过操作正确 |
| `testCollect` | ✅ PASS | 收集操作正确 |
| `testChaining` | ✅ PASS | 链式操作正确 |
| `testLazyEvaluation` | ✅ PASS | 延迟执行正确 |

### 4.7 ThreadPoolExecutorImplementationTest 预期结果

| 测试方法 | 预期结果 | 说明 |
|----------|----------|------|
| `testConstructor` | ✅ PASS | 构造器参数正确设置 |
| `testConstructorInvalidArguments` | ✅ PASS | 非法参数抛出异常 |
| `testExecute` | ✅ PASS | 任务执行成功 |
| `testExecuteMultipleTasks` | ✅ PASS | 多任务执行正确 |
| `testShutdown` | ✅ PASS | 优雅关闭正确 |
| `testShutdownNow` | ✅ PASS | 立即关闭正确 |
| `testIsShutdown` | ✅ PASS | 关闭状态检查正确 |
| `testIsTerminated` | ✅ PASS | 终止状态检查正确 |
| `testAbortPolicy` | ✅ PASS | 拒绝时抛出异常 |
| `testCallerRunsPolicy` | ✅ PASS | 调用者线程执行 |
| `testDiscardPolicy` | ✅ PASS | 静默丢弃任务 |
| `testDiscardOldestPolicy` | ✅ PASS | 丢弃最旧任务 |

### 4.8 ConcurrentHashMapImplementationTest 预期结果

| 测试方法 | 预期结果 | 说明 |
|----------|----------|------|
| `testPut` | ✅ PASS | 线程安全添加 |
| `testGet` | ✅ PASS | 线程安全获取 |
| `testRemove` | ✅ PASS | 线程安全删除 |
| `testConcurrentPut` | ✅ PASS | 并发写入正确 |
| `testConcurrentGet` | ✅ PASS | 并发读取正确 |
| `testSegmentLocking` | ✅ PASS | 分段锁工作正确 |
| `testReadWriteLock` | ✅ PASS | 读写锁工作正确 |

### 4.9 ReentrantLockImplementationTest 预期结果

| 测试方法 | 预期结果 | 说明 |
|----------|----------|------|
| `testLock` | ✅ PASS | 获取锁成功 |
| `testUnlock` | ✅ PASS | 释放锁成功 |
| `testReentrantLock` | ✅ PASS | 可重入特性正确 |
| `testLockInterruptibly` | ✅ PASS | 可中断获取正确 |
| `testTryLock` | ✅ PASS | 尝试获取正确 |
| `testTryLockWithTimeout` | ✅ PASS | 超时获取正确 |
| `testFairLock` | ✅ PASS | 公平锁按顺序获取 |
| `testNonFairLock` | ✅ PASS | 非公平锁允许插队 |
| `testConditionAwait` | ✅ PASS | 条件等待正确 |
| `testConditionSignal` | ✅ PASS | 条件通知正确 |

### 4.10 所有测试汇总

| 测试类 | 测试数 | 预期通过率 |
|--------|--------|------------|
| ArrayListImplementationTest | 13 | 100% |
| LinkedListImplementationTest | 9 | 100% |
| HashSetImplementationTest | 8 | 100% |
| TreeSetImplementationTest | 7 | 100% |
| HashMapImplementationTest | 9 | 100% |
| TreeMapImplementationTest | 7 | 100% |
| LinkedHashMapImplementationTest | 6 | 100% |
| PriorityQueueImplementationTest | 6 | 100% |
| ArrayDequeImplementationTest | 7 | 100% |
| StreamPipelineBuilderTest | 9 | 100% |
| ParallelStreamProcessorTest | 5 | 100% |
| CustomCollectorTest | 4 | 100% |
| ConcurrentHashMapImplementationTest | 7 | 100% |
| CopyOnWriteArrayListImplementationTest | 6 | 100% |
| ThreadPoolExecutorImplementationTest | 12 | 100% |
| ScheduledExecutorImplementationTest | 5 | 100% |
| TaskRejectHandlerTest | 4 | 100% |
| ReentrantLockImplementationTest | 10 | 100% |
| ReadWriteLockImplementationTest | 6 | 100% |
| ConditionVariableTest | 5 | 100% |
| ListPerformanceComparisonTest | 3 | 100% |
| **总计** | **147** | **100%** |

---

## 五、运行测试

### 5.1 运行所有util包测试

```bash
mvn test -Dtest="com.linsir.abc.core.base.util.**"
```

### 5.2 运行单个模块测试

```bash
# 集合测试
mvn test -Dtest="com.linsir.abc.core.base.util.collection.**"

# Stream测试
mvn test -Dtest="com.linsir.abc.core.base.util.stream.**"

# 并发测试
mvn test -Dtest="com.linsir.abc.core.base.util.concurrent.**"
```

### 5.3 运行单个测试类

```bash
mvn test -Dtest=ArrayListImplementationTest
mvn test -Dtest=HashMapImplementationTest
mvn test -Dtest=ThreadPoolExecutorImplementationTest
```

### 5.4 预期输出

```
Tests run: 147, Failures: 0, Errors: 0, Skipped: 0
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.linsir.abc.core.base.util.collection.list.ArrayListImplementationTest
[INFO] Tests run: 13, Failures: 0, Errors: 0, Skipped: 0
...
[INFO] Running com.linsir.abc.core.base.util.concurrent.executor.ThreadPoolExecutorImplementationTest
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
...
[INFO] BUILD SUCCESS
```

---

## 六、集合选择指南

### 6.1 List选择

| 场景 | 推荐类 | 说明 |
|------|--------|------|
| 频繁随机访问 | ArrayList | O(1)访问时间 |
| 频繁头尾操作 | LinkedList | O(1)头尾操作 |
| 需要队列功能 | LinkedList | 支持FIFO/LIFO |
| 多线程环境 | CopyOnWriteArrayList | 线程安全 |

### 6.2 Map选择

| 场景 | 推荐类 | 说明 |
|------|--------|------|
| 快速查找 | HashMap | O(1)平均时间 |
| 需要排序 | TreeMap | 按键排序 |
| 保持插入顺序 | LinkedHashMap | 按插入顺序遍历 |
| 高并发读写 | ConcurrentHashMap | 线程安全，高并发 |
| 读多写少 | ConcurrentHashMap | 读操作无锁 |

### 6.3 Set选择

| 场景 | 推荐类 | 说明 |
|------|--------|------|
| 快速去重 | HashSet | 基于HashMap |
| 需要排序 | TreeSet | 基于TreeMap |
| 保持插入顺序 | LinkedHashSet | 基于LinkedHashMap |

### 6.4 Queue选择

| 场景 | 推荐类 | 说明 |
|------|--------|------|
| 优先级队列 | PriorityQueue | 按优先级排序 |
| 双端队列 | ArrayDeque | 支持头尾操作 |
| 阻塞队列 | LinkedBlockingQueue | 支持阻塞等待 |

---

## 七、并发工具选择指南

### 7.1 锁选择

| 场景 | 推荐类 | 说明 |
|------|--------|------|
| 简单同步 | synchronized | 语法简洁，性能优秀 |
| 需要超时 | ReentrantLock | tryLock支持超时 |
| 需要可中断 | ReentrantLock | lockInterruptibly |
| 需要公平性 | ReentrantLock(公平) | 按请求顺序获取 |
| 读多写少 | ReadWriteLock | 读写分离 |
| 多条件等待 | ReentrantLock + Condition | 多个条件变量 |

### 7.2 线程池选择

| 场景 | 推荐类型 | 说明 |
|------|----------|------|
| 固定线程数 | newFixedThreadPool | 固定大小线程池 |
| 单线程 | newSingleThreadExecutor | 顺序执行任务 |
| 可缓存 | newCachedThreadPool | 弹性线程数 |
| 定时任务 | newScheduledThreadPool | 支持定时调度 |
| 自定义 | ThreadPoolExecutor | 完全自定义参数 |

---

## 八、性能对比

### 8.1 List性能对比

| 操作 | ArrayList | LinkedList |
|------|-----------|------------|
| get(index) | O(1) | O(n) |
| add(E) | O(1) amortized | O(1) |
| add(index, E) | O(n) | O(n) |
| remove(index) | O(n) | O(n) |
| addFirst/removeFirst | O(n) | O(1) |
| addLast/removeLast | O(1) amortized | O(1) |
| 内存占用 | 较少（数组紧凑） | 较多（节点开销） |

### 8.2 Map性能对比

| 操作 | HashMap | TreeMap | LinkedHashMap | ConcurrentHashMap |
|------|---------|---------|---------------|-------------------|
| get | O(1) | O(log n) | O(1) | O(1) |
| put | O(1) | O(log n) | O(1) | O(1) |
| remove | O(1) | O(log n) | O(1) | O(1) |
| 有序性 | 否 | 是 | 插入顺序 | 否 |
| 线程安全 | 否 | 否 | 否 | 是 |

### 8.3 锁性能对比

| 场景 | synchronized | ReentrantLock | ReadWriteLock |
|------|--------------|---------------|---------------|
| 单线程 | 快 | 快 | 慢 |
| 低竞争 | 快 | 快 | 慢 |
| 高竞争 | 中等 | 中等 | 中等 |
| 读多写少 | 慢 | 慢 | 快 |
| 功能丰富度 | 低 | 高 | 高 |

---

**文档版本**: 1.0.0  
**最后更新**: 2026-03-26
