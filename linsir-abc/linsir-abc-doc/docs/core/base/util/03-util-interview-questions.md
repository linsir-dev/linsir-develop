# java.util 包面试题汇总

## 目录

1. [集合框架概述](#一集合框架概述)
2. [List 接口及实现](#二list-接口及实现)
3. [Map 接口及实现](#三map-接口及实现)
4. [Set 接口及实现](#四set-接口及实现)
5. [Queue 和 Deque](#五queue-和-deque)
6. [Stream API](#六stream-api)
7. [并发集合](#七并发集合)
8. [线程池](#八线程池)
9. [锁机制](#九锁机制)

---

## 一、集合框架概述

### 1.1 Java 集合框架体系

**问题**: 请简述 Java 集合框架的体系结构。

**答案**:

Java 集合框架主要分为两大接口体系：

```
Collection（单列集合）
├── List（有序、可重复）
│   ├── ArrayList
│   ├── LinkedList
│   └── Vector
│       └── Stack
├── Set（无序、不可重复）
│   ├── HashSet
│   ├── LinkedHashSet
│   └── TreeSet
└── Queue（队列）
    ├── LinkedList
    ├── PriorityQueue
    └── ArrayDeque

Map（双列集合）
├── HashMap
├── LinkedHashMap
├── TreeMap
├── Hashtable
└── WeakHashMap
```

**核心接口**:

| 接口 | 特点 | 实现类 |
|------|------|--------|
| `List` | 有序、可重复、有索引 | ArrayList, LinkedList |
| `Set` | 无序、不可重复 | HashSet, TreeSet |
| `Queue` | FIFO 队列 | LinkedList, PriorityQueue |
| `Deque` | 双端队列 | ArrayDeque, LinkedList |
| `Map` | 键值对、key 唯一 | HashMap, TreeMap |

---

### 1.2 集合与数组的区别

**问题**: 集合与数组有什么区别？

**答案**:

| 特性 | 数组 | 集合 |
|------|------|------|
| **长度** | 固定 | 可变 |
| **存储类型** | 基本类型 + 对象 | 只能存储对象（基本类型自动装箱）|
| **功能** | 简单存储 | 丰富的操作方法 |
| **泛型支持** | 不支持 | 支持 |
| **线程安全** | 不安全 | 部分实现线程安全 |

**选择建议**:
- 元素数量固定、类型一致 → 使用数组
- 需要动态扩容、丰富操作 → 使用集合

---

## 二、List 接口及实现

### 2.1 ArrayList 和 LinkedList 的区别

**问题**: `ArrayList` 和 `LinkedList` 有什么区别？

**答案**:

| 特性 | ArrayList | LinkedList |
|------|-----------|------------|
| **底层结构** | 动态数组 | 双向链表 |
| **随机访问** | O(1) | O(n) |
| **插入/删除** | O(n)，尾部 O(1) | O(1) |
| **内存占用** | 较少 | 较多（需要存储指针）|
| **扩容** | 需要（1.5倍） | 不需要 |
| **适用场景** | 查询多、增删少 | 增删多、查询少 |

**代码示例**:
```java
// ArrayList - 适合随机访问
List<String> arrayList = new ArrayList<>();
for (int i = 0; i < 10000; i++) {
    arrayList.add("item" + i);
}
String item = arrayList.get(5000); // O(1)

// LinkedList - 适合频繁插入删除
List<String> linkedList = new LinkedList<>();
linkedList.add("first");
((LinkedList<String>) linkedList).addFirst("newFirst"); // O(1)
```

---

### 2.2 ArrayList 的扩容机制

**问题**: `ArrayList` 的扩容机制是什么？

**答案**:

**扩容过程**:
1. 初始容量为 **10**（默认）
2. 当元素数量超过当前容量时触发扩容
3. 扩容为原来的 **1.5 倍**（`oldCapacity + (oldCapacity >> 1)`）
4. 使用 `Arrays.copyOf()` 复制元素到新数组

**源码分析**:
```java
private void grow(int minCapacity) {
    int oldCapacity = elementData.length;
    // 扩容 1.5 倍
    int newCapacity = oldCapacity + (oldCapacity >> 1);
    if (newCapacity - minCapacity < 0)
        newCapacity = minCapacity;
    if (newCapacity - MAX_ARRAY_SIZE > 0)
        newCapacity = hugeCapacity(minCapacity);
    elementData = Arrays.copyOf(elementData, newCapacity);
}
```

**优化建议**:
- 预估数据量，使用构造函数指定初始容量
```java
// 避免频繁扩容
List<String> list = new ArrayList<>(10000);
```

---

### 2.3 Vector 和 ArrayList 的区别

**问题**: `Vector` 和 `ArrayList` 有什么区别？

**答案**:

| 特性 | Vector | ArrayList |
|------|--------|-----------|
| **线程安全** | 安全（synchronized） | 不安全 |
| **扩容倍数** | 2 倍 | 1.5 倍 |
| **性能** | 较低 | 较高 |
| **出现版本** | JDK 1.0 | JDK 1.2 |

**使用建议**:
- 单线程环境 → 使用 `ArrayList`
- 多线程环境 → 使用 `Collections.synchronizedList()` 或 `CopyOnWriteArrayList`

---

## 三、Map 接口及实现

### 3.1 HashMap 的实现原理

**问题**: `HashMap` 的实现原理是什么？

**答案**:

**核心结构**:
- 数组 + 链表 + 红黑树（JDK 8+）
- 数组每个位置称为**桶（Bucket）**
- 每个桶存储一个链表（或红黑树）

**存储过程**:
1. 计算 key 的 hashCode
2. 通过 `(n - 1) & hash` 计算桶位置
3. 如果桶为空，直接插入
4. 如果桶有元素，遍历链表/树，key 相同则覆盖，不同则追加

**源码结构**:
```java
// 数组（桶数组）
transient Node<K,V>[] table;

// 链表节点
static class Node<K,V> implements Map.Entry<K,V> {
    final int hash;
    final K key;
    V value;
    Node<K,V> next;
}

// 树节点（JDK 8+）
static final class TreeNode<K,V> extends LinkedHashMap.Entry<K,V> {
    TreeNode<K,V> parent;
    TreeNode<K,V> left;
    TreeNode<K,V> right;
    TreeNode<K,V> prev;
    boolean red;
}
```

---

### 3.2 HashMap 的扩容机制

**问题**: `HashMap` 什么时候扩容？扩容过程是怎样的？

**答案**:

**触发条件**:
- 元素数量 > 容量 × 负载因子（默认 0.75）
- 单个链表长度 > 8，但桶数量 < 64

**扩容过程**:
1. 创建新数组，容量为原来的 **2 倍**
2. 重新计算每个元素的 hash 位置
3. 将元素移动到新数组

**JDK 8 优化**:
- 扩容时不需要重新计算 hash
- 只需要判断 hash 新增的高位是 0 还是 1
- 0：留在原位置；1：移到原位置 + 旧容量

```java
// 扩容源码（简化）
final Node<K,V>[] resize() {
    Node<K,V>[] oldTab = table;
    int oldCap = (oldTab == null) ? 0 : oldTab.length;
    int oldThr = threshold;
    int newCap = oldCap << 1; // 2倍
    
    // 迁移元素
    for (int j = 0; j < oldCap; ++j) {
        Node<K,V> e;
        if ((e = oldTab[j]) != null) {
            oldTab[j] = null;
            if (e.next == null)
                newTab[e.hash & (newCap - 1)] = e;
            // ...
        }
    }
}
```

---

### 3.3 HashMap 链表转红黑树

**问题**: `HashMap` 什么时候将链表转为红黑树？为什么要转？

**答案**:

**转换条件**:
- 链表长度 **≥ 8**
- 桶数量 **≥ 64**

**转换原因**:
- 链表查询复杂度 O(n)
- 红黑树查询复杂度 O(log n)
- 提高大数据量下的查询性能

**为什么不直接转树**:
- 树节点占用内存是链表节点的 2 倍
- 数据量小的时候，链表效率更高

**阈值说明**:
```java
// 链表转树阈值
static final int TREEIFY_THRESHOLD = 8;

// 树转链表阈值
static final int UNTREEIFY_THRESHOLD = 6;

// 最小树化容量
static final int MIN_TREEIFY_CAPACITY = 64;
```

---

### 3.4 HashMap 和 Hashtable 的区别

**问题**: `HashMap` 和 `Hashtable` 有什么区别？

**答案**:

| 特性 | HashMap | Hashtable |
|------|---------|-----------|
| **线程安全** | 不安全 | 安全（synchronized）|
| **null 支持** | key/value 可为 null | 都不可为 null |
| **扩容** | 2 倍 | 2 倍 + 1 |
| **性能** | 高 | 低 |
| **出现版本** | JDK 1.2 | JDK 1.0 |
| **遍历方式** | Iterator | Iterator + Enumeration |

**线程安全替代方案**:
```java
// 方式1: Collections.synchronizedMap
Map<String, String> syncMap = Collections.synchronizedMap(new HashMap<>());

// 方式2: ConcurrentHashMap（推荐）
Map<String, String> concurrentMap = new ConcurrentHashMap<>();
```

---

### 3.5 ConcurrentHashMap 的实现原理

**问题**: `ConcurrentHashMap` 是如何实现线程安全的？

**答案**:

**JDK 7 - 分段锁（Segment）**:
- 将数据分成多个 Segment
- 每个 Segment 是一个独立的 HashMap
- 每个 Segment 独立加锁
- 默认 16 个 Segment，最多支持 16 个并发

**JDK 8 - CAS + synchronized**:
- 取消 Segment，使用 Node 数组
- 插入使用 CAS 操作
- 冲突使用 synchronized 锁住链表头节点
- 读操作无锁（volatile 保证可见性）

**核心源码**:
```java
// 插入操作（JDK 8）
final V putVal(K key, V value, boolean onlyIfAbsent) {
    if (key == null || value == null) throw new NullPointerException();
    int hash = spread(key.hashCode());
    int binCount = 0;
    for (Node<K,V>[] tab = table;;) {
        Node<K,V> f; int n, i, fh;
        if (tab == null || (n = tab.length) == 0)
            tab = initTable();
        else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {
            // CAS 插入
            if (casTabAt(tab, i, null, new Node<K,V>(hash, key, value, null)))
                break;
        }
        else if ((fh = f.hash) == MOVED)
            tab = helpTransfer(tab, f);
        else {
            synchronized (f) { // 锁住链表头
                // 链表/树插入
            }
        }
    }
    addCount(1L, binCount);
    return null;
}
```

---

## 四、Set 接口及实现

### 4.1 HashSet 的实现原理

**问题**: `HashSet` 是如何实现的？如何保证元素不重复？

**答案**:

**实现原理**:
- 底层使用 `HashMap` 实现
- 元素作为 HashMap 的 key
- value 是一个固定的 Object 占位符

**源码**:
```java
public class HashSet<E> extends AbstractSet<E> {
    private transient HashMap<E,Object> map;
    private static final Object PRESENT = new Object();
    
    public boolean add(E e) {
        return map.put(e, PRESENT) == null;
    }
}
```

**去重原理**:
1. 计算元素的 hashCode
2. 如果 hashCode 相同，调用 equals 比较
3. equals 返回 true，则认为是重复元素，不添加

**使用建议**:
- 自定义对象放入 HashSet，必须重写 `hashCode()` 和 `equals()`

---

### 4.2 HashSet、LinkedHashSet、TreeSet 的区别

**问题**: `HashSet`、`LinkedHashSet`、`TreeSet` 有什么区别？

**答案**:

| 特性 | HashSet | LinkedHashSet | TreeSet |
|------|---------|---------------|---------|
| **底层实现** | HashMap | LinkedHashMap | TreeMap |
| **有序性** | 无序 | 插入顺序 | 自然/定制排序 |
| **排序方式** | 无 | 插入顺序 | 自然顺序或 Comparator |
| **性能** | O(1) | O(1) | O(log n) |
| **null 支持** | 支持 | 支持 | 不支持 |

**代码示例**:
```java
// HashSet - 无序
Set<String> hashSet = new HashSet<>();
hashSet.add("C"); hashSet.add("A"); hashSet.add("B");
System.out.println(hashSet); // [A, B, C] 或乱序

// LinkedHashSet - 插入顺序
Set<String> linkedSet = new LinkedHashSet<>();
linkedSet.add("C"); linkedSet.add("A"); linkedSet.add("B");
System.out.println(linkedSet); // [C, A, B]

// TreeSet - 自然排序
Set<String> treeSet = new TreeSet<>();
treeSet.add("C"); treeSet.add("A"); treeSet.add("B");
System.out.println(treeSet); // [A, B, C]
```

---

## 五、Queue 和 Deque

### 5.1 Queue 接口及实现

**问题**: `Queue` 接口有哪些实现类？它们的特点是什么？

**答案**:

**Queue 实现类**:

| 实现类 | 特点 | 使用场景 |
|--------|------|----------|
| `LinkedList` | 双向链表，可作队列/栈 | 通用队列 |
| `PriorityQueue` | 优先队列，堆实现 | 任务调度、Top K |
| `ArrayDeque` | 数组双端队列 | 高性能队列/栈 |
| `ConcurrentLinkedQueue` | 并发无界队列 | 高并发场景 |
| `BlockingQueue` | 阻塞队列 | 生产者-消费者 |

**Queue 方法对比**:

| 操作 | 抛出异常 | 返回特殊值 |
|------|----------|------------|
| 插入 | `add(e)` | `offer(e)` |
| 移除 | `remove()` | `poll()` |
| 检查 | `element()` | `peek()` |

---

### 5.2 PriorityQueue 的实现原理

**问题**: `PriorityQueue` 是如何实现的？

**答案**:

**实现原理**:
- 底层使用**小顶堆**（完全二叉树）
- 数组存储堆结构
- 父节点索引 = (子节点索引 - 1) / 2
- 左子节点 = 父节点 × 2 + 1
- 右子节点 = 父节点 × 2 + 2

**核心操作**:
- `offer()`: 插入元素，向上调整（siftUp）
- `poll()`: 移除堆顶，向下调整（siftDown）

**时间复杂度**:
- 插入: O(log n)
- 删除: O(log n)
- 查询堆顶: O(1)

**代码示例**:
```java
// 自然排序（小顶堆）
PriorityQueue<Integer> minHeap = new PriorityQueue<>();
minHeap.offer(3); minHeap.offer(1); minHeap.offer(2);
System.out.println(minHeap.poll()); // 1

// 大顶堆（使用 Comparator）
PriorityQueue<Integer> maxHeap = new PriorityQueue<>(
    (a, b) -> b - a
);
```

---

## 六、Stream API

### 6.1 Stream 是什么？

**问题**: 什么是 Stream？有什么特点？

**答案**:

**Stream**:
- JDK 8 引入的**函数式**数据处理 API
- 对集合进行**声明式**操作
- 支持**链式调用**和**并行处理**

**特点**:

| 特点 | 说明 |
|------|------|
| **声明式** | 描述做什么，而非怎么做 |
| **链式调用** | 多个操作串联 |
| **惰性求值** | 终止操作才执行 |
| **可并行** | 自动多线程处理 |
| **不修改源数据** | 返回新结果 |

**代码示例**:
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

// 传统方式
List<Integer> result = new ArrayList<>();
for (Integer n : numbers) {
    if (n % 2 == 0) {
        result.add(n * n);
    }
}
Collections.sort(result);

// Stream 方式
List<Integer> result = numbers.stream()
    .filter(n -> n % 2 == 0)    // 过滤偶数
    .map(n -> n * n)             // 平方
    .sorted()                    // 排序
    .collect(Collectors.toList());
```

---

### 6.2 中间操作和终止操作

**问题**: Stream 的中间操作和终止操作有什么区别？

**答案**:

**中间操作（Intermediate）**:
- 返回新的 Stream
- **惰性求值**，不会立即执行
- 可以链式调用

**终止操作（Terminal）**:
- 返回非 Stream 结果
- **触发执行**所有中间操作
- 只能有一个

**常见操作**:

| 类型 | 操作 | 说明 |
|------|------|------|
| 中间 | `filter` | 过滤 |
| 中间 | `map` | 映射转换 |
| 中间 | `flatMap` | 扁平化映射 |
| 中间 | `sorted` | 排序 |
| 中间 | `distinct` | 去重 |
| 中间 | `limit` | 限制数量 |
| 终止 | `collect` | 收集结果 |
| 终止 | `forEach` | 遍历 |
| 终止 | `reduce` | 归约 |
| 终止 | `count` | 计数 |

**代码示例**:
```java
List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David");

// 中间操作不会执行
Stream<String> stream = names.stream()
    .filter(s -> {
        System.out.println("filter: " + s);
        return s.length() > 3;
    });
// 无输出

// 终止操作触发执行
List<String> result = stream.collect(Collectors.toList());
// 输出: filter: Alice, filter: Bob, ...
```

---

### 6.3 并行流的注意事项

**问题**: 使用并行流（Parallel Stream）需要注意什么？

**答案**:

**适用场景**:
- 数据量大（通常 > 10,000）
- 复杂计算操作
- 无状态、无副作用

**注意事项**:

| 问题 | 说明 |
|------|------|
| **线程安全** | 避免修改共享变量 |
| **顺序问题** | `forEachOrdered` 性能差 |
| **拆箱装箱** | 使用 `IntStream` 等原始流 |
| **错误数据源** | `Stream.iterate`、`Stream.of` 不适合 |

**代码示例**:
```java
List<Integer> numbers = new ArrayList<>();
for (int i = 0; i < 1000000; i++) {
    numbers.add(i);
}

// 正确使用并行流
long sum = numbers.parallelStream()
    .mapToLong(n -> n * n)  // 使用原始流避免装箱
    .sum();

// 错误：修改共享变量
List<Integer> result = new ArrayList<>();
numbers.parallelStream().forEach(result::add); // 线程不安全

// 正确：使用 collect 合并结果
List<Integer> result = numbers.parallelStream()
    .collect(Collectors.toList());
```

---

## 七、并发集合

### 7.1 CopyOnWriteArrayList

**问题**: `CopyOnWriteArrayList` 的实现原理是什么？适用场景？

**答案**:

**实现原理**:
- **写时复制（Copy-On-Write）**
- 读操作：直接读取数组（无锁）
- 写操作：复制新数组，修改后替换引用

**源码分析**:
```java
public boolean add(E e) {
    final ReentrantLock lock = this.lock;
    lock.lock();
    try {
        Object[] elements = getArray();
        int len = elements.length;
        Object[] newElements = Arrays.copyOf(elements, len + 1);
        newElements[len] = e;
        setArray(newElements);
        return true;
    } finally {
        lock.unlock();
    }
}
```

**适用场景**:
- 读多写少
- 数据量小
- 实时性要求不高

**缺点**:
- 写操作开销大（需要复制数组）
- 内存占用高（同时存在两个数组）
- 数据最终一致性

---

### 7.2 并发集合对比

**问题**: Java 中有哪些并发集合？它们的特点是什么？

**答案**:

| 集合 | 特点 | 实现机制 |
|------|------|----------|
| `ConcurrentHashMap` | 线程安全 Map | CAS + synchronized |
| `CopyOnWriteArrayList` | 线程安全 List | 写时复制 |
| `CopyOnWriteArraySet` | 线程安全 Set | 写时复制 |
| `ConcurrentLinkedQueue` | 无界并发队列 | CAS |
| `BlockingQueue` | 阻塞队列 | 锁 + Condition |
| `ConcurrentSkipListMap` | 有序并发 Map | 跳表 |

**选择建议**:
- 高并发 Map → `ConcurrentHashMap`
- 读多写少 List → `CopyOnWriteArrayList`
- 生产者-消费者 → `BlockingQueue`

---

## 八、线程池

### 8.1 ThreadPoolExecutor 参数

**问题**: `ThreadPoolExecutor` 有哪些核心参数？

**答案**:

**核心参数**:

| 参数 | 说明 |
|------|------|
| `corePoolSize` | 核心线程数，即使空闲也保留 |
| `maximumPoolSize` | 最大线程数 |
| `keepAliveTime` | 非核心线程空闲存活时间 |
| `workQueue` | 任务等待队列 |
| `threadFactory` | 线程工厂 |
| `rejectedExecutionHandler` | 拒绝策略 |

**执行流程**:
1. 当前线程数 < corePoolSize：创建新线程执行任务
2. 当前线程数 ≥ corePoolSize：任务进入队列
3. 队列满且线程数 < maximumPoolSize：创建非核心线程
4. 队列满且线程数 ≥ maximumPoolSize：执行拒绝策略

**拒绝策略**:

| 策略 | 说明 |
|------|------|
| `AbortPolicy` | 抛出异常（默认）|
| `CallerRunsPolicy` | 调用者线程执行 |
| `DiscardPolicy` | 静默丢弃 |
| `DiscardOldestPolicy` | 丢弃队列最老任务 |

---

### 8.2 线程池的使用

**问题**: 如何正确使用线程池？

**答案**:

**创建线程池**:
```java
// 方式1: 手动创建（推荐）
ThreadPoolExecutor executor = new ThreadPoolExecutor(
    5,                      // 核心线程数
    10,                     // 最大线程数
    60L,                    // 空闲存活时间
    TimeUnit.SECONDS,       // 时间单位
    new LinkedBlockingQueue<>(100), // 任务队列
    new ThreadFactory() {   // 线程工厂
        private AtomicInteger count = new AtomicInteger(0);
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "pool-thread-" + count.incrementAndGet());
        }
    },
    new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略
);

// 方式2: Executors 工厂方法（不推荐）
ExecutorService executor = Executors.newFixedThreadPool(10);
```

**提交任务**:
```java
// execute - 无返回值
executor.execute(() -> System.out.println("Task 1"));

// submit - 有返回值
Future<Integer> future = executor.submit(() -> {
    return 42;
});
Integer result = future.get();

// invokeAll - 批量执行
List<Callable<Integer>> tasks = Arrays.asList(() -> 1, () -> 2);
List<Future<Integer>> futures = executor.invokeAll(tasks);
```

**关闭线程池**:
```java
// 优雅关闭
executor.shutdown();           // 不再接受新任务，等待执行完
executor.awaitTermination(60, TimeUnit.SECONDS); // 等待60秒

// 强制关闭
executor.shutdownNow();        // 立即关闭，返回未执行的任务
```

---

### 8.3 线程池大小设置

**问题**: 如何设置线程池的大小？

**答案**:

**CPU 密集型任务**:
```
线程数 = CPU 核心数 + 1
```

**IO 密集型任务**:
```
线程数 = CPU 核心数 × (1 + 等待时间 / 计算时间)
// 或简单设置：CPU 核心数 × 2
```

**获取 CPU 核心数**:
```java
int cpuCores = Runtime.getRuntime().availableProcessors();
```

**示例**:
```java
// CPU 密集型
ThreadPoolExecutor cpuExecutor = new ThreadPoolExecutor(
    cpuCores + 1,
    cpuCores + 1,
    0L, TimeUnit.MILLISECONDS,
    new LinkedBlockingQueue<>()
);

// IO 密集型
ThreadPoolExecutor ioExecutor = new ThreadPoolExecutor(
    cpuCores * 2,
    cpuCores * 4,
    60L, TimeUnit.SECONDS,
    new LinkedBlockingQueue<>(1000)
);
```

---

## 九、锁机制

### 9.1 synchronized 和 ReentrantLock 的区别

**问题**: `synchronized` 和 `ReentrantLock` 有什么区别？

**答案**:

| 特性 | synchronized | ReentrantLock |
|------|--------------|---------------|
| **实现方式** | JVM 层面（字节码） | API 层面（Java 代码）|
| **锁获取** | 自动获取/释放 | 手动 lock/unlock |
| **可中断** | 不支持 | `lockInterruptibly()` |
| **超时获取** | 不支持 | `tryLock(timeout)` |
| **公平锁** | 非公平 | 可配置公平/非公平 |
| **条件变量** | 一个（wait/notify） | 多个（Condition） |
| **性能** | JDK 6+ 优化后接近 | 与 synchronized 接近 |

**ReentrantLock 示例**:
```java
ReentrantLock lock = new ReentrantLock();

// 基本使用
lock.lock();
try {
    // 临界区
} finally {
    lock.unlock();
}

// 可中断锁
lock.lockInterruptibly();
try {
    // 临界区
} finally {
    lock.unlock();
}

// 尝试获取锁（带超时）
if (lock.tryLock(5, TimeUnit.SECONDS)) {
    try {
        // 临界区
    } finally {
        lock.unlock();
    }
}
```

---

### 9.2 读写锁（ReadWriteLock）

**问题**: 什么是读写锁？有什么优势？

**答案**:

**ReadWriteLock**:
- 维护一对锁：**读锁**和**写锁**
- 读锁：多个线程可同时获取
- 写锁：独占，与其他读写锁互斥

**优势**:
- 读多写少场景下提高并发性能
- 读操作不互斥，提高吞吐量

**使用示例**:
```java
public class ReadWriteLockExample {
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();
    private Map<String, Object> data = new HashMap<>();
    
    // 读操作
    public Object get(String key) {
        readLock.lock();
        try {
            return data.get(key);
        } finally {
            readLock.unlock();
        }
    }
    
    // 写操作
    public void put(String key, Object value) {
        writeLock.lock();
        try {
            data.put(key, value);
        } finally {
            writeLock.unlock();
        }
    }
}
```

**锁降级**:
```java
// 锁降级：写锁 → 读锁（支持）
writeLock.lock();
try {
    // 修改数据
    data.put("key", "value");
    
    // 获取读锁（锁降级）
    readLock.lock();
} finally {
    writeLock.unlock(); // 释放写锁，保留读锁
}

try {
    // 使用读锁读取数据
    Object value = data.get("key");
} finally {
    readLock.unlock();
}

// 锁升级：读锁 → 写锁（不支持，会导致死锁）
```

---

### 9.3 AQS（AbstractQueuedSynchronizer）

**问题**: 什么是 AQS？它在并发包中的作用是什么？

**答案**:

**AQS（抽象队列同步器）**:
- 并发包的**基础框架**
- `ReentrantLock`、`CountDownLatch`、`Semaphore` 等都基于 AQS
- 使用 **CLH 队列**管理等待线程

**核心思想**:
- **state 变量**: 表示同步状态
- **FIFO 队列**: 管理等待线程
- **模板方法模式**: 子类实现获取/释放逻辑

**实现类**:

| 类 | 用途 |
|-----|------|
| `ReentrantLock` | 可重入锁 |
| `ReentrantReadWriteLock` | 读写锁 |
| `CountDownLatch` | 倒计时门闩 |
| `Semaphore` | 信号量 |
| `CyclicBarrier` | 循环屏障 |

**AQS 结构**:
```java
public abstract class AbstractQueuedSynchronizer {
    private volatile int state;           // 同步状态
    private transient volatile Node head; // 队列头
    private transient volatile Node tail; // 队列尾
    
    // 子类实现
    protected abstract boolean tryAcquire(int arg);
    protected abstract boolean tryRelease(int arg);
}
```

---

## 十、面试题速查表

| 问题 | 核心要点 |
|------|----------|
| ArrayList vs LinkedList | 数组 vs 链表，查询快 vs 增删快 |
| HashMap 扩容 | 2 倍扩容，链表转红黑树（长度≥8）|
| ConcurrentHashMap | JDK 8 使用 CAS + synchronized |
| HashSet 原理 | 底层 HashMap，key 存元素 |
| Stream 惰性求值 | 中间操作不执行，终止操作触发 |
| CopyOnWriteArrayList | 写时复制，读多写少场景 |
| 线程池参数 | core、max、队列、拒绝策略 |
| ReentrantLock 优势 | 可中断、可超时、多 Condition |
| 读写锁 | 读共享、写独占，适合读多写少 |
| AQS | 并发包基础框架，state + CLH 队列 |

---

**文档版本**: 1.0.0  
**最后更新**: 2026-03-26  
**适用 JDK**: Java 8+
