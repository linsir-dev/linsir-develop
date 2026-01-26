# Gson 库示例代码

本目录包含 Gson 库的示例代码，演示了 Gson 库的各种功能和用法。

## 代码结构

```
gson/
├── GsonDemoMain.java        # 主类，用于演示所有 Gson 示例代码
├── GsonBasicDemo.java       # 基本功能示例
├── GsonAdvancedDemo.java    # 高级功能示例
├── GsonBuilderDemo.java     # GsonBuilder 配置示例
└── README.md                # 本说明文档
```

## 依赖项

本示例代码依赖于以下 Gson 库：

- `gson` - Google JSON 处理库

这些依赖项已在项目的 `pom.xml` 文件中配置。

## 如何运行示例代码

### 1. 编译项目

在项目根目录（`linsir-abc-pdai`）下运行以下命令：

```bash
mvn compile
```

### 2. 运行主类

运行 `GsonDemoMain` 类来演示所有 Gson 示例代码：

```bash
java -cp "target\classes;target\dependency\*" com.linsir.abc.pdai.tools.gson.GsonDemoMain
```

## 示例代码说明

### GsonBasicDemo.java

演示 Gson 的基本功能，包括：

- Java 对象转 JSON 字符串
- JSON 字符串转 Java 对象
- Java 集合转 JSON 字符串
- JSON 字符串转 Java 集合

### GsonAdvancedDemo.java

演示 Gson 的高级功能，包括：

- 注解使用（@SerializedName, @Expose 等）
- 版本控制（@Since, @Until 等）
- 泛型处理
- 嵌套对象处理

### GsonBuilderDemo.java

演示 GsonBuilder 的各种配置选项，包括：

- 美化输出
- 序列化空值
- 禁用 HTML 转义
- 日期格式化
- 自定义序列化器
- 版本控制

## 示例输出

运行 `GsonDemoMain` 类将输出以下内容：

1. **基本功能示例**：
   - Java 对象转 JSON 字符串
   - JSON 字符串转 Java 对象
   - Java 集合转 JSON 字符串
   - JSON 字符串转 Java 集合

2. **高级功能示例**：
   - 注解使用示例
   - 版本控制示例
   - 泛型处理示例
   - 嵌套对象处理示例

3. **GsonBuilder 配置示例**：
   - 基本配置示例
   - 日期格式化示例
   - 自定义序列化器示例
   - 版本控制示例

## Gson 库简介

Gson 是 Google 开发的一个 Java 库，用于将 Java 对象转换为 JSON 表示，以及将 JSON 字符串转换回 Java 对象。Gson 提供了以下核心功能：

1. **简单易用**：API 简洁明了，易于使用
2. **支持复杂类型**：支持泛型、嵌套对象、集合等复杂类型
3. **注解支持**：通过注解自定义序列化和反序列化行为
4. **版本控制**：支持基于版本的序列化和反序列化
5. **自定义序列化器和反序列化器**：支持自定义类型适配器
6. **性能优异**：处理大型 JSON 数据时性能良好

Gson 库的设计目标是提供一种简单、灵活、高效的方式来处理 JSON 数据，是 Java 生态系统中最常用的 JSON 处理库之一。

## 最佳实践

1. **重用 Gson 实例**：Gson 实例是线程安全的，建议在应用中重用
2. **使用注解**：合理使用注解可以简化代码，提高可读性
3. **处理日期时间**：使用合适的日期格式化方式处理日期时间类型
4. **处理空值**：根据需要配置是否序列化空值
5. **错误处理**：捕获并适当处理 JSON 解析和生成过程中的异常
6. **使用 GsonBuilder**：对于复杂需求，使用 GsonBuilder 进行详细配置

通过本示例代码，您可以了解 Gson 库的基本用法和高级特性，从而在实际项目中更有效地使用 Gson 处理 JSON 数据。
