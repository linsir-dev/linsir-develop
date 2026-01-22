# 字符流示例

本目录包含 Java.io 包下字符流的示例代码，展示了各种字符流类的使用方法和特性。

## 目录结构
```
chario/
├── FileReaderWriterDemo.java              // FileReader和FileWriter示例
├── BufferedReaderWriterDemo.java          // BufferedReader和BufferedWriter示例
├── InputStreamReaderOutputStreamWriterDemo.java  // InputStreamReader和OutputStreamWriter示例
├── StringReaderWriterDemo.java            // StringReader和StringWriter示例
├── PrintWriterDemo.java                   // PrintWriter示例
├── CharStreamTest.java                    // 字符流测试主类
└── README.md                              // 本说明文件
```

## 示例代码功能

### 字符流概述
- **特点**：
  - 以字符为单位处理数据
  - 适合处理文本数据
  - 自动处理字符编码

### 输入字符流（Reader及其子类）

#### 1. FileReader
- **特点**：
  - 从文件系统中的文件读取字符
  - 使用默认字符编码
  - 适合读取文本文件
- **使用场景**：
  - 读取文本文件
  - 处理字符数据

#### 2. BufferedReader
- **特点**：
  - 带缓冲的输入流
  - 提高读取效率
  - 支持逐行读取（readLine()）
  - 装饰器模式，包装其他Reader
- **使用场景**：
  - 需要提高读取效率的场景
  - 逐行读取文本的场景

#### 3. InputStreamReader
- **特点**：
  - 字节流到字符流的桥接器
  - 可指定字符编码
  - 适合处理字节流中的字符数据
- **使用场景**：
  - 需要字符编码转换的场景
  - 处理网络字节流中的文本数据

#### 4. StringReader
- **特点**：
  - 从字符串中读取字符
  - 适合处理内存中的文本数据
  - 支持标记和重置操作
- **使用场景**：
  - 处理内存中的文本数据
  - 需要多次读取同一文本的场景

### 输出字符流（Writer及其子类）

#### 1. FileWriter
- **特点**：
  - 向文件系统中的文件写入字符
  - 使用默认字符编码
  - 适合写入文本文件
- **使用场景**：
  - 写入文本文件
  - 处理字符数据

#### 2. BufferedWriter
- **特点**：
  - 带缓冲的输出流
  - 提高写入效率
  - 支持换行操作（newLine()）
  - 装饰器模式，包装其他Writer
- **使用场景**：
  - 需要提高写入效率的场景
  - 需要换行操作的场景

#### 3. OutputStreamWriter
- **特点**：
  - 字符流到字节流的桥接器
  - 可指定字符编码
  - 适合将字符数据转换为字节流
- **使用场景**：
  - 需要字符编码转换的场景
  - 处理网络字节流中的文本数据

#### 4. StringWriter
- **特点**：
  - 向内存中的字符串缓冲区写入字符
  - 自动扩容
  - 适合构建字符串
- **使用场景**：
  - 构建复杂字符串
  - 内存中的文本数据处理

#### 5. PrintWriter
- **特点**：
  - 提供各种打印方法
  - 支持自动刷新
  - 适合生成格式化输出
- **使用场景**：
  - 生成格式化文本
  - 日志输出
  - 控制台输出

## 示例代码内容

### FileReaderWriterDemo.java
- **功能**：
  - 展示 FileReader 的使用方法
  - 展示 FileWriter 的使用方法
  - 演示文件字符流的基本操作

### BufferedReaderWriterDemo.java
- **功能**：
  - 展示 BufferedReader 的使用方法
  - 展示 BufferedWriter 的使用方法
  - 演示缓冲字符流的基本操作
  - 演示逐行读取和换行操作

### InputStreamReaderOutputStreamWriterDemo.java
- **功能**：
  - 展示 InputStreamReader 的使用方法
  - 展示 OutputStreamWriter 的使用方法
  - 演示字节流到字符流的转换
  - 演示字符编码的指定

### StringReaderWriterDemo.java
- **功能**：
  - 展示 StringReader 的使用方法
  - 展示 StringWriter 的使用方法
  - 演示字符串字符流的基本操作
  - 演示 StringWriter 的其他方法

### PrintWriterDemo.java
- **功能**：
  - 展示 PrintWriter 的使用方法
  - 演示各种打印方法
  - 演示自动刷新功能

### CharStreamTest.java
- **功能**：
  - 测试所有字符流示例代码
  - 依次运行所有示例
  - 验证各个字符流的功能

## 运行示例

### 编译和运行
1. **编译**：确保所有 .java 文件已编译
   ```
   cd linsir-abc/linsir-abc-pdai/src/main/java
   javac -d ../../../../target com/linsir/abc/pdai/io/chario/*.java
   ```

2. **运行**：执行 CharStreamTest 类
   ```
   cd linsir-abc/linsir-abc-pdai
   java -cp target com.linsir.abc.pdai.io.chario.CharStreamTest
   ```

### 预期输出
运行后，你将看到各个字符流类的使用示例输出，包括写入和读取操作的结果。

## 注意事项

### 流的关闭
- 使用 try-with-resources 语句自动关闭流，避免资源泄露
- 手动关闭流时，应按照从里到外的顺序关闭

### 字符编码
- FileReader 和 FileWriter 使用默认字符编码，可能导致跨平台问题
- 推荐使用 InputStreamReader 和 OutputStreamWriter 并指定字符编码

### 缓冲流
- 缓冲流需要调用 `flush()` 方法确保数据被写入到底层流
- try-with-resources 语句会自动调用 `flush()` 方法

### 打印流
- PrintWriter 的自动刷新功能仅在调用 println()、printf() 或 format() 方法时生效

## 应用场景

### FileReader/FileWriter
- 读取/写入文本文件
- 处理字符数据
- 简单的文本处理

### BufferedReader/BufferedWriter
- 逐行读取文本
- 提高读写效率
- 处理大文本文件

### InputStreamReader/OutputStreamWriter
- 处理字节流中的文本数据
- 需要字符编码转换的场景
- 网络编程中的文本传输

### StringReader/StringWriter
- 处理内存中的文本数据
- 构建复杂字符串
- 解析文本数据

### PrintWriter
- 生成格式化输出
- 日志记录
- 控制台输出
- 生成文本报告