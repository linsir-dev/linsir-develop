# List 集合示例

本目录包含 Java List 集合的示例代码，展示了 List 接口及其主要实现类的使用方法。

## 目录结构
```
listdemo/
├── ListDemo.java    // List 集合示例代码
└── README.md       // 本说明文件
```

## 示例代码功能

### List 接口
- **特点**：
  - 有序（保持插入顺序）
  - 允许重复元素
  - 支持索引访问

### 主要实现类

#### 1. ArrayList
- **特点**：
  - 基于动态数组实现
  - 有序
  - 允许重复元素
  - 查询快（时间复杂度 O(1)）
  - 增删慢（时间复杂度 O(n)）
  - 线程不安全
- **使用场景**：
  - 频繁查询的场景
  - 元素数量相对稳定的场景

#### 2. LinkedList
- **特点**：
  - 基于双向链表实现
  - 有序
  - 允许重复元素
  - 查询慢（时间复杂度 O(n)）
  - 增删快（时间复杂度 O(1)）
  - 线程不安全
- **使用场景**：
  - 频繁增删的场景
  - 元素数量变化较大的场景

#### 3. Vector
- **特点**：
  - 基于动态数组实现
  - 有序
  - 允许重复元素
  - 线程安全（方法加了 synchronized）
  - 效率低
- **使用场景**：
  - 多线程环境
  - 不需要考虑性能的场景

## 示例代码内容

### ListDemo.java
- **功能**：
  - 展示 ArrayList 的使用方法
  - 展示 LinkedList 的使用方法
  - 展示 Vector 的使用方法
  - 展示 List 的遍历方法
  - 展示 List 的批量操作
  - 展示 List 的清空操作

- **主要方法**：
  - `add()`：添加元素
  - `get()`：获取指定索引的元素
  - `set()`：修改指定索引的元素
  - `remove()`：删除元素
  - `contains()`：检查元素是否存在
  - `indexOf()`：获取元素的索引
  - `size()`：获取集合大小
  - `isEmpty()`：检查集合是否为空
  - `clear()`：清空集合
  - `subList()`：获取子列表
  - `addAll()`：批量添加元素
  - `iterator()`：获取迭代器

## 运行示例

### 编译和运行
1. **编译**：确保 ListDemo.java 文件已编译
2. **运行**：执行 ListDemo.java 的 `main` 方法

### 预期输出
```
=== List 集合示例 ===

1. ArrayList 示例:
   特点: 有序、可重复、基于动态数组实现、查询快、增删慢
   元素: [Apple, Banana, Orange, Apple]
   大小: 4
   索引 1 的元素: Banana
   修改索引 1 后: [Apple, Pear, Orange, Apple]
   删除索引 2 后: [Apple, Pear, Apple]
   是否包含 Apple: true
   Apple 的索引: 0

2. LinkedList 示例:
   特点: 有序、可重复、基于双向链表实现、查询慢、增删快
   元素: [Apple, Banana, Orange, Apple]
   大小: 4
   索引 1 的元素: Banana

3. Vector 示例:
   特点: 有序、可重复、基于动态数组实现、线程安全、效率低
   元素: [Apple, Banana, Orange, Apple]
   大小: 4

4. 遍历 List 示例:
   使用 for-each 循环:
   - Apple
   - Pear
   - Apple

   使用普通 for 循环:
   - Apple
   - Banana
   - Orange
   - Apple

   使用迭代器:
   - Apple
   - Banana
   - Orange
   - Apple

5. 列表操作示例:
   子列表 (0-2): [Apple, Pear]

   清空列表:
   清空前大小: 3
   清空后大小: 0
   是否为空: true

6. 批量操作示例:
   添加新水果后: [Apple, Banana, Orange, Apple, Grape, Mango]

=== List 集合总结 ===
1. ArrayList: 查询快、增删慢，适合频繁查询的场景
2. LinkedList: 查询慢、增删快，适合频繁增删的场景
3. Vector: 线程安全、效率低，适合多线程环境
4. 所有 List 都允许重复元素，并且保持插入顺序
5. List 接口提供了丰富的索引操作方法
```

## 注意事项

### ArrayList
- **初始容量**：默认初始容量为 10
- **扩容机制**：当元素数量超过容量时，会自动扩容为原来的 1.5 倍
- **线程安全**：非线程安全，多线程环境下需要手动同步

### LinkedList
- **结构**：双向链表，每个节点包含 prev、next 引用
- **空间开销**：比 ArrayList 大，因为每个元素需要存储前后节点的引用

### Vector
- **初始容量**：默认初始容量为 10
- **扩容机制**：当元素数量超过容量时，会自动扩容为原来的 2 倍
- **线程安全**：线程安全，但效率低，推荐使用 `Collections.synchronizedList()` 替代

## 应用场景

### ArrayList
- 数据查询操作频繁的场景
- 元素数量相对稳定的场景
- 例如：数据展示、配置信息存储

### LinkedList
- 数据增删操作频繁的场景
- 元素数量变化较大的场景
- 例如：队列、栈、链表结构的实现

### Vector
- 多线程环境下需要线程安全的场景
- 例如：早期的多线程应用
