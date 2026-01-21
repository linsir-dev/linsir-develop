# Set 集合示例

本目录包含 Java Set 集合的示例代码，展示了 Set 接口及其主要实现类的使用方法。

## 目录结构
```
setdemo/
├── SetDemo.java    // Set 集合示例代码
└── README.md       // 本说明文件
```

## 示例代码功能

### Set 接口
- **特点**：
  - 无序（TreeSet 除外）
  - 不允许重复元素
  - 没有索引方法，不能通过索引访问元素

### 主要实现类

#### 1. HashSet
- **特点**：
  - 基于哈希表实现
  - 无序
  - 不允许重复元素
  - 效率高（时间复杂度 O(1)）
- **使用场景**：
  - 不需要排序的场景
  - 需要快速查找、添加、删除元素的场景

#### 2. LinkedHashSet
- **特点**：
  - 基于链表和哈希表实现
  - 有序（保持插入顺序）
  - 不允许重复元素
  - 效率略低于 HashSet
- **使用场景**：
  - 需要保持插入顺序的场景

#### 3. TreeSet
- **特点**：
  - 基于红黑树实现
  - 有序（自然排序或自定义排序）
  - 不允许重复元素
  - 效率较低（时间复杂度 O(log n)）
- **使用场景**：
  - 需要排序的场景

## 示例代码内容

### SetDemo.java
- **功能**：
  - 展示 HashSet 的使用方法
  - 展示 LinkedHashSet 的使用方法
  - 展示 TreeSet 的使用方法
  - 展示 TreeSet 对数字的自动排序
  - 展示 Set 的遍历方法
  - 展示 Set 的清空操作

- **主要方法**：
  - `add()`：添加元素
  - `remove()`：删除元素
  - `contains()`：检查元素是否存在
  - `size()`：获取集合大小
  - `isEmpty()`：检查集合是否为空
  - `clear()`：清空集合
  - `iterator()`：获取迭代器

## 运行示例

### 编译和运行
1. **编译**：确保 SetDemo.java 文件已编译
2. **运行**：执行 SetDemo.java 的 `main` 方法

### 预期输出
```
=== Set 集合示例 ===

1. HashSet 示例:
   特点: 无序、不重复、基于哈希表实现
   元素: [Apple, Banana, Orange]
   大小: 3
   是否包含 Banana: true
   删除 Orange 后: [Apple, Banana]

2. LinkedHashSet 示例:
   特点: 有序、不重复、基于链表和哈希表实现
   元素: [Apple, Banana, Orange]
   大小: 3

3. TreeSet 示例:
   特点: 有序、不重复、基于红黑树实现
   元素: [Apple, Banana, Orange]
   大小: 3

4. TreeSet 数字排序示例:
   元素: [1, 3, 5, 8]

5. 遍历 Set 示例:
   使用 for-each 循环:
   - Apple
   - Banana
   - Orange

   使用迭代器:
   - Apple
   - Banana

6. 清空 Set 示例:
   清空前大小: 2
   清空后大小: 0
   是否为空: true

=== Set 集合总结 ===
1. HashSet: 无序、高效，适合不需要排序的场景
2. LinkedHashSet: 有序（插入顺序）、略低于 HashSet，适合需要保持插入顺序的场景
3. TreeSet: 有序（自然排序）、效率较低，适合需要排序的场景
4. 所有 Set 都不允许重复元素
5. Set 接口没有索引方法，不能通过索引访问元素
```

## 注意事项

### HashSet 和 LinkedHashSet
- 元素需要正确实现 `equals()` 和 `hashCode()` 方法，以确保元素的唯一性

### TreeSet
- 元素需要实现 `Comparable` 接口，或在构造 TreeSet 时提供 `Comparator` 接口实现
- 元素的比较方法需要与 `equals()` 方法保持一致，以确保元素的唯一性

## 应用场景

### HashSet
- 存储不重复的元素
- 快速查找元素
- 去重操作

### LinkedHashSet
- 需要保持元素插入顺序的场景
- 缓存实现

### TreeSet
- 需要排序的场景
- 范围查询
- 有序集合操作
