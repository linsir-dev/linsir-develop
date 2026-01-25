# Guava 库示例代码

本项目提供了 Google Guava 库的全面示例代码，旨在展示 Guava 库的核心功能和使用方法。Guava 是 Google 开发的 Java 工具库，提供了丰富的实用工具和扩展功能，可大幅提高 Java 开发效率和代码质量。

## 1. 项目概述

本示例代码覆盖了 Guava 库的以下核心模块：
- 集合工具类
- 字符串处理工具
- 缓存工具
- 函数式编程工具
- 并发工具

## 2. 目录结构

示例代码位于 `tools/guava` 目录下，包含以下文件：

```
tools/
└── guava/
    ├── GuavaCollectionsDemo.java     # 集合工具类示例
    ├── GuavaStringsDemo.java         # 字符串工具类示例
    ├── GuavaCacheDemo.java           # 缓存工具类示例
    ├── GuavaFunctionalDemo.java      # 函数式编程示例
    ├── GuavaConcurrencyDemo.java     # 并发工具示例
    └── GuavaDemoMain.java            # 主类，运行所有示例
```

## 3. 模块功能详细说明

### 3.1 集合工具类示例 (`GuavaCollectionsDemo.java`)

**功能说明**：演示 Guava 提供的增强集合类型和集合操作工具。

**核心功能**：
- **不可变集合**：创建和使用 ImmutableList、ImmutableSet、ImmutableMap 等不可变集合
- **新集合类型**：
  - Multiset：允许重复元素的集合，提供计数功能
  - Multimap：一个键对应多个值的映射
  - BiMap：双向映射，支持键值互查
  - Table：二维映射，支持行键和列键
- **集合工具类**：
  - Lists：列表操作，如反转、分割
  - Sets：集合操作，如交集、并集、差集
  - Maps：映射操作，如差异比较
  - Iterables：可迭代对象操作
- **集合转换**：使用 Collections2.filter 和 Lists.transform 进行集合过滤和转换
- **原始类型集合**：使用 Ints 等工具类处理原始类型数组

### 3.2 字符串工具类示例 (`GuavaStringsDemo.java`)

**功能说明**：演示 Guava 提供的强大字符串处理工具。

**核心功能**：
- **CharMatcher**：字符匹配和处理，如空白字符处理、字母数字提取
- **Splitter**：字符串分割，支持多种分隔符、空白处理、忽略空字符串
- **Joiner**：字符串连接，支持处理 null 值、连接映射
- **Strings**：字符串工具，如判空、填充、重复、公共前缀/后缀
- **CaseFormat**：大小写格式转换，如驼峰命名转下划线命名
- **字符串处理综合示例**：处理用户输入、构建查询字符串、格式化日志消息

### 3.3 缓存工具类示例 (`GuavaCacheDemo.java`)

**功能说明**：演示 Guava 提供的内存缓存功能。

**核心功能**：
- **基本缓存操作**：创建缓存、添加/获取/移除缓存项
- **缓存过期策略**：
  - 写入后过期
  - 访问后过期
- **缓存加载器**：使用 CacheLoader 自动加载缓存项
- **缓存移除监听器**：监听缓存项被移除的事件
- **缓存统计**：记录和获取缓存命中率、加载时间等统计信息
- **缓存刷新**：支持自动刷新缓存项
- **缓存大小限制**：设置最大缓存大小，自动淘汰旧项
- **缓存综合使用**：结合多种功能的完整缓存示例

### 3.4 函数式编程示例 (`GuavaFunctionalDemo.java`)

**功能说明**：演示 Guava 提供的函数式编程工具。

**核心功能**：
- **Function 接口**：函数对象，支持函数组合
- **Predicate 接口**：谓词对象，支持谓词组合和集合过滤
- **Supplier 接口**：供应商对象，提供值的延迟计算
- **Consumer 接口**：消费者对象，处理输入值
- **工具类**：
  - Functions：函数工具，如 forMap、constant、toStringFunction
  - Predicates：谓词工具，如 alwaysTrue、isNull、instanceOf
  - Suppliers：供应商工具，如 ofInstance、memoize
- **函数式编程综合示例**：组合使用多种函数式接口处理数据
- **Optional 类**：处理可能为 null 的值，避免空指针异常

### 3.5 并发工具示例 (`GuavaConcurrencyDemo.java`)

**功能说明**：演示 Guava 提供的并发编程工具。

**核心功能**：
- **ListenableFuture**：可监听的 Future，支持添加回调
- **RateLimiter**：速率限制器，控制请求速率
- **Striped**：条纹锁，支持基于键的并发控制
- **Monitor**：监视器，提供更灵活的线程同步机制
- **MoreExecutors**：执行器工具，如直接执行器、命名线程池
- **Futures**：Future 工具，如 allAsList、successfulAsList
- **有界队列实现**：使用 Monitor 实现线程安全的有界队列

### 3.6 主类 (`GuavaDemoMain.java`)

**功能说明**：组织和运行所有 Guava 示例代码。

**核心功能**：
- 按顺序运行所有示例模块
- 提供统一的输出格式和错误处理
- 验证所有示例代码的正确性

## 4. 如何运行示例

### 4.1 前提条件

- JDK 8 或更高版本
- Maven 3.0 或更高版本
- Guava 33.1.0-jre 或兼容版本

### 4.2 运行步骤

1. **编译项目**：
   ```bash
   mvn compile
   ```

2. **运行示例**：
   ```bash
   mvn exec:java -Dexec.mainClass="com.linsir.abc.pdai.tools.guava.GuavaDemoMain"
   ```

   或使用 Java 命令直接运行：
   ```bash
   java -cp target\classes com.linsir.abc.pdai.tools.guava.GuavaDemoMain
   ```

### 4.3 运行结果

运行后，控制台将输出所有示例的执行结果，包括：
- 不可变集合的创建和使用
- 新集合类型的操作
- 字符串处理的各种功能
- 缓存的各种策略和操作
- 函数式编程的使用方法
- 并发工具的应用场景

## 5. 核心功能亮点

### 5.1 集合增强

Guava 提供了丰富的集合增强功能，如 Multiset、Multimap、BiMap 和 Table，这些新集合类型解决了传统 Java 集合的许多局限性，使代码更简洁、更易读。

### 5.2 字符串处理

Guava 的字符串处理工具非常强大，特别是 CharMatcher、Splitter 和 Joiner，它们提供了灵活、高效的字符串操作能力，大幅简化了字符串处理代码。

### 5.3 缓存系统

Guava 的缓存系统功能完备，支持多种过期策略、自动加载、统计信息等，是构建高性能应用的重要工具。

### 5.4 函数式编程

Guava 提供了函数式编程的核心接口和工具，如 Function、Predicate、Supplier 等，使 Java 代码能够更具表达力和灵活性。

### 5.5 并发工具

Guava 的并发工具扩展了 Java 标准库的并发功能，如 ListenableFuture、RateLimiter、Striped 等，提供了更高级的并发控制能力。

## 6. 技术要点

### 6.1 依赖管理

本项目使用 Maven 管理依赖，在 pom.xml 中添加了 Guava 依赖：

```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>33.1.0-jre</version>
</dependency>
```

### 6.2 编译配置

为支持 lambda 表达式和其他 Java 8 特性，在 pom.xml 中配置了 Maven 编译插件：

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <source>8</source>
                <target>8</target>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### 6.3 代码结构

示例代码采用了模块化设计，每个功能模块独立成类，主类负责组织和运行所有示例。代码结构清晰，注释详细，便于理解和学习。

### 6.4 最佳实践

示例代码展示了 Guava 库的最佳使用实践，包括：
- 不可变集合的使用
- 缓存策略的选择
- 函数式编程的应用
- 并发工具的正确使用

## 7. 总结

本项目提供了全面的 Guava 库示例代码，覆盖了 Guava 的核心功能模块。通过学习和运行这些示例，开发者可以快速掌握 Guava 库的使用方法，从而在实际项目中应用 Guava 提高开发效率和代码质量。

Guava 库是 Java 开发中的重要工具，它不仅提供了丰富的实用功能，还体现了许多优秀的设计思想和编程实践。希望本示例代码能够帮助开发者更好地理解和应用 Guava 库，构建更加高效、可靠的 Java 应用。