# Java equals() 和 hashCode() 示例

本目录包含 Java 中 `equals()` 和 `hashCode()` 方法的示例代码，展示了它们的使用方法、区别以及为什么必须同时重写。

## 目录结构
```
equalsdemo/
├── EqualsHashCodeDemo.java  // equals() 和 hashCode() 演示
└── PersonEqualsOnly.java    // 只重写 equals() 的示例
```

## 示例代码功能

### 1. 完整演示 (`EqualsHashCodeDemo.java`)
- **三种情况对比**：
  - **情况1**：默认继承 Object，未重写 equals 和 hashCode
  - **情况2**：只重写 equals，未重写 hashCode
  - **情况3**：正确重写 equals 和 hashCode
- **测试内容**：
  - 比较对象引用 (`==`)
  - 比较对象内容 (`equals()`)
  - 计算对象哈希码 (`hashCode()`)
  - 测试对象在 HashSet 中的行为
- **核心结论**：
  - 演示了为什么必须同时重写 equals() 和 hashCode()
  - 展示了违反 Java 契约的后果

### 2. 只重写 equals 的示例 (`PersonEqualsOnly.java`)
- **功能**：
  - 只重写了 `equals()` 方法
  - 未重写 `hashCode()` 方法
- **使用场景**：
  - 用于演示只重写 equals() 时的问题
  - 作为反例展示正确的实现方式

## equals() 和 hashCode() 的区别

### equals() 方法
- **作用**：判断两个对象逻辑上是否相等
- **默认行为**：比较内存地址（`==`）
- **重写后**：通常比较对象的内容（如 name 和 age）
- **使用场景**：对象比较、集合中的查找

### hashCode() 方法
- **作用**：计算对象的哈希码，主要用于哈希表中确定存储位置
- **默认行为**：基于内存地址计算
- **重写后**：基于对象内容计算
- **使用场景**：HashMap、HashSet 等哈希集合

## 为什么必须同时重写？

这是 Java 的一条重要契约：**如果两个对象 equals() 返回 true，那么它们的 hashCode() 必须相等**。

### 违反契约的后果
- **现象**：`p1.equals(p2)` 是 true，但 p1 和 p2 的哈希码不同
- **后果**：当你把它们放入 HashSet 或 HashMap 时，集合会认为它们是两个完全不同的元素
- **Bug**：HashSet 中会出现重复元素，HashMap 的 get() 方法可能取不到值

### 正确实现的好处
- **保证逻辑相等的对象拥有相同的哈希特征**
- **在集合中正确去重和索引**
- **提高哈希表的性能**

## 正确实现示例

### equals() 方法实现要点
1. **自反性**：`x.equals(x)` 必须返回 true
2. **对称性**：如果 `x.equals(y)` 返回 true，那么 `y.equals(x)` 也必须返回 true
3. **传递性**：如果 `x.equals(y)` 和 `y.equals(z)` 都返回 true，那么 `x.equals(z)` 也必须返回 true
4. **一致性**：如果对象的内容没有变化，多次调用 `equals()` 应该返回相同的结果
5. **非空性**：`x.equals(null)` 必须返回 false

### hashCode() 方法实现要点
1. **一致性**：如果对象的内容没有变化，多次调用 `hashCode()` 应该返回相同的结果
2. **等价性**：如果 `x.equals(y)` 返回 true，那么 `x.hashCode()` 必须等于 `y.hashCode()`
3. **高效性**：哈希码的计算应该高效
4. **分散性**：不同对象的哈希码应该尽可能不同（减少哈希冲突）

## 运行示例
执行 `EqualsHashCodeDemo.java` 类的 `main` 方法，即可看到完整的演示结果，包括三种情况的对比和详细的解释。
