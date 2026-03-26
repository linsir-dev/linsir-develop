# java.util 包详细设计文档

## 一、模块概述

**包路径**: `com.linsir.abc.core.base.util`

**包含子包**:
- `collection` - 集合框架
  - `list` - List 实现
  - `set` - Set 实现
  - `map` - Map 实现
  - `queue` - Queue 实现
- `stream` - Stream API
- `concurrent` - 并发包
  - `collection` - 并发集合
  - `executor` - 线程池
  - `lock` - 锁机制

**类数**: 27个

---

## 二、集合框架 - List

**包路径**: `com.linsir.abc.core.base.util.collection.list`

| 类名 | 功能描述 | 核心方法 |
|------|----------|----------|
| `ArrayListImplementation` | 简化版 ArrayList 实现 | `add()`, `get()`, `remove()`, `grow()` |
| `LinkedListImplementation` | 简化版 LinkedList 实现 | `addFirst()`, `addLast()`, `remove()` |
| `ListPerformanceComparison` | List 性能对比 | `compareAddPerformance()`, `compareGetPerformance()` |

**设计要点**:
- ArrayList 的动态扩容机制（1.5倍）
- LinkedList 的双向链表结构
- RandomAccess 接口的作用

---

## 三、集合框架 - Map

**包路径**: `com.linsir.abc.core.base.util.collection.map`

| 类名 | 功能描述 | 核心方法 |
|------|----------|----------|
| `HashMapImplementation` | 简化版 HashMap 实现 | `put()`, `get()`, `resize()`, `treeifyBin()` |
| `TreeMapImplementation` | 简化版 TreeMap 实现 | `put()`, `get()`, `rotateLeft()`, `rotateRight()` |
| `LinkedHashMapImplementation` | 简化版 LinkedHashMap 实现 | `afterNodeInsertion()`, `afterNodeRemoval()` |

**设计要点**:
- HashMap 的哈希冲突解决（链表 + 红黑树）
- 负载因子和扩容阈值
- JDK 8 的链表转红黑树优化

---

## 四、集合框架 - Set

**包路径**: `com.linsir.abc.core.base.util.collection.set`

| 类名 | 功能描述 | 核心方法 |
|------|----------|----------|
| `HashSetImplementation` | 基于 HashMap 实现 Set | `add()`, `remove()`, `contains()` |
| `TreeSetImplementation` | 基于 TreeMap 实现 Set | `add()`, `first()`, `last()` |

**设计要点**:
- Set 的去重原理（依赖 Map 的 key）
- HashSet 和 TreeSet 的区别

---

## 五、集合框架 - Queue

**包路径**: `com.linsir.abc.core.base.util.collection.queue`

| 类名 | 功能描述 | 核心方法 |
|------|----------|----------|
| `PriorityQueueImplementation` | 堆实现的优先队列 | `offer()`, `poll()`, `siftUp()`, `siftDown()` |
| `ArrayDequeImplementation` | 数组双端队列 | `addFirst()`, `addLast()`, `pollFirst()`, `pollLast()` |

**设计要点**:
- 二叉堆的实现（完全二叉树）
- 优先队列的排序机制
- 双端队列的循环数组实现

---

## 六、Stream API

**包路径**: `com.linsir.abc.core.base.util.stream`

| 类名 | 功能描述 | 核心方法 |
|------|----------|----------|
| `StreamPipelineBuilder` | 流式操作链构建 | `filter()`, `map()`, `sorted()`, `collect()` |
| `ParallelStreamProcessor` | 并行流处理 | `parallel()`, `sequential()` |
| `CustomCollector` | 自定义收集器 | `supplier()`, `accumulator()`, `combiner()`, `finisher()` |

**设计要点**:
- 中间操作和终止操作的区别
- 惰性求值机制
- 并行流的线程安全问题

---

## 七、并发集合

**包路径**: `com.linsir.abc.core.base.util.concurrent.collection`

| 类名 | 功能描述 | 核心方法 |
|------|----------|----------|
| `ConcurrentHashMapImplementation` | 简化版 ConcurrentHashMap | `put()`, `get()`, `transfer()` |
| `CopyOnWriteArrayListImplementation` | 写时复制 List | `add()`, `get()`, `iterator()` |

**设计要点**:
- 分段锁 vs CAS + synchronized
- volatile 保证可见性
- 写时复制的读写分离思想

---

## 八、线程池

**包路径**: `com.linsir.abc.core.base.util.concurrent.executor`

| 类名 | 功能描述 | 核心方法 |
|------|----------|----------|
| `ThreadPoolExecutorImplementation` | 简化版线程池 | `execute()`, `addWorker()`, `runWorker()` |
| `ScheduledExecutorImplementation` | 定时任务执行器 | `schedule()`, `scheduleAtFixedRate()` |
| `TaskRejectHandler` | 任务拒绝策略 | `rejectedExecution()` |

**设计要点**:
- 核心线程数和最大线程数
- 任务队列的作用
- 拒绝策略的实现

---

## 九、锁机制

**包路径**: `com.linsir.abc.core.base.util.concurrent.lock`

| 类名 | 功能描述 | 核心方法 |
|------|----------|----------|
| `ReentrantLockImplementation` | 简化版可重入锁 | `lock()`, `unlock()`, `tryLock()` |
| `ReadWriteLockImplementation` | 读写锁实现 | `readLock()`, `writeLock()` |
| `ConditionVariable` | 条件变量 | `await()`, `signal()`, `signalAll()` |

**设计要点**:
- AQS（AbstractQueuedSynchronizer）框架
- 独占锁和共享锁的区别
- 条件变量的等待/通知机制

---

## 十、完整类名列表

| 序号 | 完整类名 |
|------|----------|
| 1 | `com.linsir.abc.core.base.util.collection.list.ArrayListImplementation` |
| 2 | `com.linsir.abc.core.base.util.collection.list.LinkedListImplementation` |
| 3 | `com.linsir.abc.core.base.util.collection.list.ListPerformanceComparison` |
| 4 | `com.linsir.abc.core.base.util.collection.map.HashMapImplementation` |
| 5 | `com.linsir.abc.core.base.util.collection.map.TreeMapImplementation` |
| 6 | `com.linsir.abc.core.base.util.collection.map.LinkedHashMapImplementation` |
| 7 | `com.linsir.abc.core.base.util.collection.set.HashSetImplementation` |
| 8 | `com.linsir.abc.core.base.util.collection.set.TreeSetImplementation` |
| 9 | `com.linsir.abc.core.base.util.collection.queue.PriorityQueueImplementation` |
| 10 | `com.linsir.abc.core.base.util.collection.queue.ArrayDequeImplementation` |
| 11 | `com.linsir.abc.core.base.util.stream.StreamPipelineBuilder` |
| 12 | `com.linsir.abc.core.base.util.stream.ParallelStreamProcessor` |
| 13 | `com.linsir.abc.core.base.util.stream.CustomCollector` |
| 14 | `com.linsir.abc.core.base.util.concurrent.collection.ConcurrentHashMapImplementation` |
| 15 | `com.linsir.abc.core.base.util.concurrent.collection.CopyOnWriteArrayListImplementation` |
| 16 | `com.linsir.abc.core.base.util.concurrent.executor.ThreadPoolExecutorImplementation` |
| 17 | `com.linsir.abc.core.base.util.concurrent.executor.ScheduledExecutorImplementation` |
| 18 | `com.linsir.abc.core.base.util.concurrent.executor.TaskRejectHandler` |
| 19 | `com.linsir.abc.core.base.util.concurrent.lock.ReentrantLockImplementation` |
| 20 | `com.linsir.abc.core.base.util.concurrent.lock.ReadWriteLockImplementation` |
| 21 | `com.linsir.abc.core.base.util.concurrent.lock.ConditionVariable` |

---

**文档版本**: 1.0.0  
**最后更新**: 2026-03-26
