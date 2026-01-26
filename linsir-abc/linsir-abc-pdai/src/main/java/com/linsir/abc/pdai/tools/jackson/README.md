# Jackson 库示例代码

本目录包含 Jackson 库的示例代码，演示了 Jackson 库的各种功能和用法。

## 代码结构

```
jackson/
├── JacksonDemoMain.java        # 主类，用于演示所有 Jackson 示例代码
├── JacksonBasicDemo.java       # 基本功能示例
├── JacksonAdvancedDemo.java    # 高级功能示例
├── JacksonTreeModelDemo.java   # 树模型操作示例
├── JacksonStreamingDemo.java   # 流式 API 操作示例
└── README.md                   # 本说明文档
```

## 依赖项

本示例代码依赖于以下 Jackson 库：

- `jackson-databind` - 核心数据绑定功能
- `jackson-core` - 核心 JSON 处理功能
- `jackson-annotations` - 注解支持
- `jackson-datatype-jsr310` - Java 8 日期时间支持（用于 LocalDateTime）

这些依赖项已在项目的 `pom.xml` 文件中配置。

## 如何运行示例代码

### 1. 编译项目

在项目根目录（`linsir-abc-pdai`）下运行以下命令：

```bash
mvn compile
```

### 2. 运行主类

运行 `JacksonDemoMain` 类来演示所有 Jackson 示例代码：

```bash
java -cp "target\classes;target\dependency\*" com.linsir.abc.pdai.tools.jackson.JacksonDemoMain
```

## 示例代码说明

### JacksonBasicDemo.java

演示 Jackson 的基本功能，包括：

- Java 对象转 JSON 字符串
- JSON 字符串转 Java 对象
- Java 集合转 JSON 字符串
- JSON 字符串转 Java 集合

### JacksonAdvancedDemo.java

演示 Jackson 的高级功能，包括：

- 注解使用（@JsonProperty, @JsonIgnore, @JsonInclude 等）
- 日期格式化
- 空值处理
- 多态类型处理

### JacksonTreeModelDemo.java

演示 Jackson 的树模型操作，包括：

- 创建 JSON 树
- 解析 JSON 树
- 修改 JSON 树
- 遍历 JSON 树

### JacksonStreamingDemo.java

演示 Jackson 的流式 API 操作，包括：

- 使用 JsonGenerator 写入 JSON
- 使用 JsonParser 读取 JSON
- 流式 API 处理大型 JSON

## 示例输出

运行 `JacksonDemoMain` 类将输出以下内容：

1. **基本功能示例**：
   - Java 对象转 JSON 字符串
   - JSON 字符串转 Java 对象
   - Java 集合转 JSON 字符串
   - JSON 字符串转 Java 集合

2. **高级功能示例**：
   - 注解使用示例
   - 日期格式化示例
   - 空值处理示例
   - 多态类型处理示例

3. **树模型操作示例**：
   - 创建 JSON 树
   - 解析 JSON 树
   - 修改 JSON 树
   - 遍历 JSON 树

4. **流式 API 操作示例**：
   - 使用 JsonGenerator 写入 JSON
   - 使用 JsonParser 读取 JSON
   - 流式 API 处理大型 JSON

## Jackson 库简介

Jackson 是一个流行的 Java JSON 处理库，提供了以下核心功能：

1. **数据绑定**：将 Java 对象与 JSON 之间进行转换
2. **树模型**：以树结构表示 JSON 数据
3. **流式 API**：高效处理大型 JSON 数据
4. **注解支持**：通过注解自定义序列化和反序列化行为
5. **扩展模块**：支持 Java 8 日期时间、XML、YAML 等格式

Jackson 库的性能优异，功能丰富，是 Java 生态系统中最常用的 JSON 处理库之一。

## 最佳实践

1. **重用 ObjectMapper**：ObjectMapper 实例是线程安全的，建议在应用中重用
2. **使用注解**：合理使用注解可以简化代码，提高可读性
3. **处理日期时间**：使用 JavaTimeModule 处理 Java 8 日期时间类型
4. **处理大型 JSON**：对于大型 JSON，使用流式 API 可以减少内存占用
5. **错误处理**：捕获并适当处理 JSON 解析和生成过程中的异常

通过本示例代码，您可以了解 Jackson 库的基本用法和高级特性，从而在实际项目中更有效地使用 Jackson 处理 JSON 数据。
