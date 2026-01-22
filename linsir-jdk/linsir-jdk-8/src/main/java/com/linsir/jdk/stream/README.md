# Stream API 常用功能示例

## 概述

本目录包含Java Stream API的常用功能示例代码，展示了Stream的创建、中间操作、终端操作、数值Stream特化以及并行Stream等功能。

## 目录结构

```
stream/
├── StreamCommonOperations.java  # Stream API常用操作示例
├── StreamMain.java              # 示例运行类
└── README.md                    # 说明文档
```

## 示例内容

### 1. Stream的创建方法
- 从集合创建
- 从数组创建
- 使用of方法创建
- 创建空Stream
- 使用generate创建无限Stream
- 使用iterate创建无限Stream

### 2. 中间操作
- **filter**：过滤元素
- **map**：映射元素
- **flatMap**：扁平化映射
- **sorted**：排序
- **distinct**：去重
- **limit**：限制大小
- **skip**：跳过元素
- **peek**：调试操作

### 3. 终端操作
- **forEach**：遍历元素
- **collect**：收集元素
- **count**：计数
- **sum**：求和
- **average**：平均值
- **max**：最大值
- **min**：最小值
- **reduce**：归约
- **anyMatch**：任意匹配
- **allMatch**：全部匹配
- **noneMatch**：无匹配
- **findFirst**：找到第一个
- **findAny**：找到任意一个

### 4. 数值Stream特化
- **IntStream.range**：创建整数范围（不包含结束值）
- **IntStream.rangeClosed**：创建整数范围（包含结束值）
- 数值Stream的常用操作（sum、average、max、min等）

### 5. 并行Stream
- 串行Stream与并行Stream的性能对比

### 6. 综合示例
- 组合使用多种Stream操作

## 运行示例

运行StreamMain类的main方法，可以执行所有示例代码：

```bash
# 在linsir-jdk-8目录下执行
mvn exec:java -Dexec.mainClass="com.linsir.jdk.stream.StreamMain"
```

## Stream API 核心概念

### 中间操作 vs 终端操作

| 特性 | 中间操作 | 终端操作 |
|------|----------|----------|
| 返回类型 | Stream<T> | 非Stream类型 |
| 执行时机 | 惰性求值，延迟执行 | 立即执行，触发计算 |
| 操作次数 | 可多次调用 | 只能调用一次 |

### 并行Stream的使用场景

- **适合**：CPU密集型操作，如数值计算
- **不适合**：IO密集型操作，如文件读写、网络请求

### 注意事项

1. **Stream只能使用一次**：每个Stream只能被消费一次，再次使用会抛出异常
2. **惰性求值**：中间操作是惰性的，只有在终端操作被调用时才会执行
3. **无限Stream**：使用limit()等方法限制无限Stream的大小，否则可能导致无限循环
4. **并行Stream**：注意线程安全问题，避免在并行Stream中修改共享状态

## 最佳实践

1. **链式调用**：利用Stream的链式调用特性，使代码更简洁、可读
2. **合理使用中间操作**：根据需求选择合适的中间操作，避免不必要的计算
3. **选择合适的终端操作**：根据需要的结果类型选择合适的终端操作
4. **注意性能**：对于大数据集，考虑使用并行Stream，但要注意线程安全
5. **异常处理**：在Stream操作中适当处理异常，保持代码的健壮性

通过本示例代码，您可以了解Stream API的基本用法和常用操作，为实际项目中的数据处理提供参考。