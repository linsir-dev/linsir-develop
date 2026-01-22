# Stream API 示例代码说明

## 目录结构

```
stream/
├── StreamCommonOperations.java  # Stream API常用操作示例
├── StreamParallelDemo.java      # Stream串行与并行模式演示
├── StreamPerformanceTest.java   # Stream与ParallelStream性能测试
├── StreamDemoTest.java          # 综合测试类
└── README.md                    # 说明文档
```

## 示例代码说明

### 1. StreamCommonOperations.java

演示Stream API的常用操作，包括：

- **创建Stream的方法**：从集合、数组创建，使用of方法，创建空Stream，生成无限Stream等
- **中间操作**：filter、map、flatMap、sorted、distinct、limit、skip、peek等
- **终端操作**：forEach、collect、count、sum、average、max、min、reduce、anyMatch、allMatch、noneMatch、findFirst、findAny等
- **数值Stream特化**：IntStream.range、IntStream.rangeClosed等
- **综合示例**：组合使用多种Stream操作

### 2. StreamParallelDemo.java

演示Stream的串行与并行模式，包括：

- **串行Stream演示**：展示串行Stream的执行方式和线程使用
- **并行Stream演示**：使用parallel()方法创建并行Stream
- **parallelStream()方法演示**：直接使用集合的parallelStream()方法
- **并行Stream的无序性演示**：展示并行Stream的处理顺序和forEachOrdered的使用
- **并行Stream的使用场景**：CPU密集型操作和大数据集处理

### 3. StreamPerformanceTest.java

测试Stream与ParallelStream的性能对比，包括：

- **CPU密集型操作性能测试**：测试质数计算的性能
- **大数据集处理性能测试**：测试处理百万级数据的性能
- **不同数据大小下的性能对比**：测试不同规模数据的处理性能
- **流式操作链性能测试**：测试多步操作链的性能

### 4. StreamDemoTest.java

综合测试类，运行所有示例代码，包括：

- Stream API常用操作示例
- Stream串行与并行模式示例
- Stream性能测试示例

## 运行示例

运行StreamDemoTest类的main方法，可以执行所有示例代码：

```bash
# 在linsir-jdk-8目录下执行
mvn exec:java -Dexec.mainClass="com.linsir.jdk.stream.StreamDemoTest"
```

## Stream API 核心概念

### 1. 中间操作 vs 终端操作

| 特性 | 中间操作 | 终端操作 |
|------|----------|----------|
| 返回类型 | Stream<T> | 非Stream类型 |
| 执行时机 | 惰性求值，延迟执行 | 立即执行，触发计算 |
| 操作次数 | 可多次调用 | 只能调用一次 |

### 2. 串行Stream vs 并行Stream

| 特性 | 串行Stream | 并行Stream |
|------|------------|------------|
| 线程数 | 单线程 | 多线程（使用Fork/Join框架） |
| 执行顺序 | 顺序执行 | 并行执行，可能无序 |
| 性能 | 适合IO密集型操作 | 适合CPU密集型操作 |
| 内存消耗 | 较低 | 较高 |
| 安全性 | 线程安全 | 需要注意线程安全问题 |

### 3. 并行Stream的使用建议

- **适合使用并行Stream的场景**：
  - CPU密集型操作，如数值计算
  - 大数据集处理
  - 操作链较长且无状态

- **不适合使用并行Stream的场景**：
  - IO密集型操作，如文件读写、网络请求
  - 数据量较小（并行开销可能大于收益）
  - 操作链中包含状态或副作用
  - 需要保持处理顺序

### 4. 性能测试结果解读

- **性能提升**：并行Stream在CPU密集型操作和大数据集处理上通常比串行Stream快
- **数据大小影响**：数据量越大，并行Stream的优势越明显
- **硬件影响**：CPU核心数越多，并行Stream的性能提升越显著
- **操作类型影响**：不同类型的操作对并行Stream的性能提升影响不同

## 注意事项

1. **Stream只能使用一次**：每个Stream只能被消费一次，再次使用会抛出异常
2. **惰性求值**：中间操作是惰性的，只有在终端操作被调用时才会执行
3. **无限Stream**：使用limit()等方法限制无限Stream的大小，否则可能导致无限循环
4. **并行Stream的线程安全**：避免在并行Stream中修改共享状态，如需修改，确保线程安全
5. **性能测试的环境影响**：性能测试结果会受到硬件、JVM配置和系统负载的影响

通过本示例代码，您可以全面了解Stream API的常用功能、串行与并行模式的区别以及性能特点，为实际项目中的Stream使用提供参考。