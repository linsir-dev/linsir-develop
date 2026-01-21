# Queue 队列示例

本目录包含 Java Queue 队列的示例代码，展示了 Queue 接口及其主要实现类的使用方法。

## 目录结构
```
queuedemo/
├── QueueDemo.java    // Queue 队列示例代码
└── README.md        // 本说明文件
```

## 示例代码功能

### Queue 接口
- **特点**：
  - 有序（FIFO - 先进先出，PriorityQueue 除外）
  - 允许重复元素
  - 主要用于存储和管理等待处理的元素

### 主要实现类

#### 1. LinkedList
- **特点**：
  - 基于双向链表实现
  - FIFO（先进先出）
  - 允许重复元素
  - 线程不安全
- **使用场景**：
  - 一般队列操作
  - 需要双向操作的场景

#### 2. PriorityQueue
- **特点**：
  - 基于优先级堆实现
  - 元素按优先级排序（默认小顶堆）
  - 不保证 FIFO
  - 线程不安全
- **使用场景**：
  - 需要优先级排序的场景
  - 任务调度

#### 3. ArrayDeque
- **特点**：
  - 基于可变数组实现
  - 双端队列（两端都可操作）
  - 线程不安全
  - 效率高
- **使用场景**：
  - 双端队列操作
  - 栈操作（比 Stack 效率高）
  - 队列操作

## 示例代码内容

### QueueDemo.java
- **功能**：
  - 展示 LinkedList 作为队列的使用方法
  - 展示 PriorityQueue 的使用方法
  - 展示 ArrayDeque 作为双端队列的使用方法
  - 展示队列的遍历方法
  - 展示队列的清空操作
  - 展示 PriorityQueue 对字符串的自动排序

- **主要方法**：
  - `offer()`：入队（添加元素，失败返回 false）
  - `poll()`：出队（移除并返回队首元素，队列为空返回 null）
  - `peek()`：查看队首元素（不移除，队列为空返回 null）
  - `size()`：获取队列大小
  - `isEmpty()`：检查队列是否为空
  - `offerFirst()`：从队首入队（ArrayDeque 特有）
  - `offerLast()`：从队尾入队（ArrayDeque 特有）
  - `pollFirst()`：从队首出队（ArrayDeque 特有）
  - `pollLast()`：从队尾出队（ArrayDeque 特有）
  - `peekFirst()`：查看队首元素（ArrayDeque 特有）
  - `peekLast()`：查看队尾元素（ArrayDeque 特有）

## 运行示例

### 编译和运行
1. **编译**：确保 QueueDemo.java 文件已编译
2. **运行**：执行 QueueDemo.java 的 `main` 方法

### 预期输出
```
=== Queue 队列示例 ===

1. LinkedList 作为队列示例:
   特点: 有序、可重复、基于双向链表实现、FIFO（先进先出）
   队列元素: [Apple, Banana, Orange, Pear]
   队列大小: 4
   队首元素: Apple
   出队元素: Apple
   出队后队列: [Banana, Orange, Pear]
   队首元素: Banana

2. PriorityQueue 示例:
   特点: 基于优先级堆实现、元素按优先级排序、默认小顶堆
   队列元素: [1, 2, 3, 8, 5]
   队列大小: 5
   队首元素: 1
   出队元素: 1
   出队后队列: [2, 5, 3, 8]
   队首元素: 2

3. ArrayDeque 作为双端队列示例:
   特点: 基于可变数组实现、双端队列、两端都可操作、效率高
   队列元素: [Apple, Banana, Orange]
   队列大小: 3
   队首元素: Apple
   队尾元素: Orange
   队首添加 Pear 后: [Pear, Apple, Banana, Orange]
   队首出队: Pear
   队首出队后: [Apple, Banana, Orange]
   队尾出队: Orange
   队尾出队后: [Apple, Banana]

4. 队列遍历示例:
   使用 for-each 循环:
   - Banana
   - Orange
   - Pear

5. 清空队列示例:
   清空前大小: 3
   出队: Banana
   出队: Orange
   出队: Pear
   清空后大小: 0
   是否为空: true

6. 优先队列的字符串排序示例:
   队列元素: [Apple, Banana, Orange, Pear]
   出队顺序:
   - Apple
   - Banana
   - Orange
   - Pear

=== Queue 队列总结 ===
1. LinkedList: 适合作为一般队列使用，支持 FIFO
2. PriorityQueue: 适合需要优先级排序的场景
3. ArrayDeque: 适合作为双端队列使用，两端操作效率高
4. 队列的主要操作:
   - offer(): 入队，失败返回 false
   - poll(): 出队，队列为空返回 null
   - peek(): 查看队首，队列为空返回 null
   - size(): 获取队列大小
   - isEmpty(): 检查队列是否为空
```

## 注意事项

### LinkedList
- 作为队列使用时，主要使用 `offer()`、`poll()`、`peek()` 方法
- 也可作为双端队列使用

### PriorityQueue
- 默认小顶堆，元素需要实现 `Comparable` 接口
- 可以通过构造函数传入 `Comparator` 来自定义排序规则
- 不允许 null 元素

### ArrayDeque
- 不允许 null 元素
- 作为栈使用时，推荐使用 `push()`、`pop()`、`peek()` 方法
- 作为队列使用时，推荐使用 `offer()`、`poll()`、`peek()` 方法

## 应用场景

### LinkedList 作为队列
- 一般队列操作
- 消息队列
- 任务队列

### PriorityQueue
- 任务调度（按优先级执行）
- 事件处理（按优先级处理）
- 最小/最大元素查找

### ArrayDeque
- 双端队列操作
- 栈操作（替代 Stack 类）
- 队列操作（比 LinkedList 效率高）
