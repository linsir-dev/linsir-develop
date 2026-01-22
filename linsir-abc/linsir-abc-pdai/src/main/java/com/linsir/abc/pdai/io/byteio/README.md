# 字节流示例

本目录包含 Java.io 包下字节流的示例代码，展示了各种字节流类的使用方法和特性。

## 目录结构
```
byteio/
├── FileStreamDemo.java       // FileInputStream和FileOutputStream示例
├── ByteArrayStreamDemo.java  // ByteArrayInputStream和ByteArrayOutputStream示例
├── BufferedStreamDemo.java   // BufferedInputStream和BufferedOutputStream示例
├── DataStreamDemo.java       // DataInputStream和DataOutputStream示例
├── SequenceStreamDemo.java   // SequenceInputStream示例
├── ByteStreamTest.java       // 字节流测试主类
└── README.md                 // 本说明文件
```

## 示例代码功能

### 字节流概述
- **特点**：
  - 以字节为单位处理数据
  - 适合处理二进制数据（如图像、音频、视频等）
  - 无缓冲（基本字节流）

### 输入字节流（InputStream及其子类）

#### 1. FileInputStream
- **特点**：
  - 从文件系统中的文件读取字节
  - 基本字节流，无缓冲
  - 适合读取二进制文件
- **使用场景**：
  - 读取文件数据
  - 处理二进制文件

#### 2. ByteArrayInputStream
- **特点**：
  - 从内存中的字节数组读取字节
  - 支持标记和重置操作
  - 适合处理内存中的二进制数据
- **使用场景**：
  - 处理内存中的二进制数据
  - 需要标记和重置功能的场景

#### 3. BufferedInputStream
- **特点**：
  - 带缓冲的输入流
  - 提高读取效率
  - 装饰器模式，包装其他输入流
- **使用场景**：
  - 需要提高读取效率的场景
  - 频繁读取小数据的场景

#### 4. DataInputStream
- **特点**：
  - 读取基本数据类型
  - 装饰器模式，包装其他输入流
  - 适合读取结构化数据
- **使用场景**：
  - 读取基本数据类型
  - 处理结构化的二进制数据

#### 5. SequenceInputStream
- **特点**：
  - 合并多个输入流为一个
  - 按顺序读取多个流的数据
  - 适合处理多个数据源
- **使用场景**：
  - 合并多个输入流
  - 处理多个数据源的场景

### 输出字节流（OutputStream及其子类）

#### 1. FileOutputStream
- **特点**：
  - 向文件系统中的文件写入字节
  - 基本字节流，无缓冲
  - 适合写入二进制文件
- **使用场景**：
  - 写入文件数据
  - 处理二进制文件

#### 2. ByteArrayOutputStream
- **特点**：
  - 向内存中的字节数组写入字节
  - 自动扩容
  - 适合处理内存中的二进制数据
- **使用场景**：
  - 处理内存中的二进制数据
  - 需要动态扩容的场景

#### 3. BufferedOutputStream
- **特点**：
  - 带缓冲的输出流
  - 提高写入效率
  - 装饰器模式，包装其他输出流
- **使用场景**：
  - 需要提高写入效率的场景
  - 频繁写入小数据的场景

#### 4. DataOutputStream
- **特点**：
  - 写入基本数据类型
  - 装饰器模式，包装其他输出流
  - 适合写入结构化数据
- **使用场景**：
  - 写入基本数据类型
  - 处理结构化的二进制数据

## 示例代码内容

### FileStreamDemo.java
- **功能**：
  - 展示 FileOutputStream 的使用方法
  - 展示 FileInputStream 的使用方法
  - 演示文件字节流的基本操作

- **主要方法**：
  - `writeToFile()`：写入数据到文件
  - `readFromFile()`：从文件读取数据
  - `cleanTestFiles()`：清理测试文件

### ByteArrayStreamDemo.java
- **功能**：
  - 展示 ByteArrayOutputStream 的使用方法
  - 展示 ByteArrayInputStream 的使用方法
  - 演示内存字节流的基本操作
  - 演示重置和标记功能

- **主要方法**：
  - `writeToByteArray()`：写入数据到字节数组
  - `readFromByteArray()`：从字节数组读取数据
  - `demonstrateResetAndMark()`：演示重置和标记功能

### BufferedStreamDemo.java
- **功能**：
  - 展示 BufferedOutputStream 的使用方法
  - 展示 BufferedInputStream 的使用方法
  - 演示缓冲字节流的基本操作

- **主要方法**：
  - `writeWithBuffer()`：使用缓冲流写入数据
  - `readWithBuffer()`：使用缓冲流读取数据
  - `cleanTestFiles()`：清理测试文件

### DataStreamDemo.java
- **功能**：
  - 展示 DataOutputStream 的使用方法
  - 展示 DataInputStream 的使用方法
  - 演示数据字节流的基本操作
  - 演示读写各种基本数据类型

- **主要方法**：
  - `writeData()`：写入各种类型的数据
  - `readData()`：读取各种类型的数据
  - `cleanTestFiles()`：清理测试文件

### SequenceStreamDemo.java
- **功能**：
  - 展示 SequenceInputStream 的使用方法
  - 演示序列字节流的基本操作
  - 演示合并多个输入流

- **主要方法**：
  - `basicExample()`：基本使用示例
  - `vectorExample()`：使用Vector合并多个流
  - `combinedExample()`：综合示例

### ByteStreamTest.java
- **功能**：
  - 测试所有字节流示例代码
  - 依次运行所有示例
  - 验证各个字节流的功能

## 运行示例

### 编译和运行
1. **编译**：确保所有 .java 文件已编译
   ```
   cd linsir-abc/linsir-abc-pdai/src/main/java
   javac -d ../../../../target com/linsir/abc/pdai/io/byteio/*.java
   ```

2. **运行**：执行 ByteStreamTest 类
   ```
   cd linsir-abc/linsir-abc-pdai
   java -cp target com.linsir.abc.pdai.io.byteio.ByteStreamTest
   ```

### 预期输出
```
=== Java.io 字节流测试 ===

1. FileOutputStream写入示例:
成功写入数据到文件:
Hello, File Stream!
This is a test for FileOutputStream.

2. FileInputStream读取示例:
成功从文件读取数据:
Hello, File Stream!
This is a test for FileOutputStream.

3. ByteArrayOutputStream写入示例:
成功写入数据到字节数组:
Hello, ByteArray Stream!
This is a test for ByteArrayOutputStream.
字节数组长度: 72

4. ByteArrayInputStream读取示例:
成功从字节数组读取数据:
Hello, ByteArray Stream!
This is a test for ByteArrayInputStream.

5. ByteArrayInputStream重置和标记功能示例:
读取前10个字节: Hello, Byte
读取接下来的5个字节: Array 
重置后读取的字节: Array Stream!

6. BufferedOutputStream写入示例:
成功使用BufferedOutputStream写入数据:
Hello, Buffered Stream!
This is a test for BufferedOutputStream.
Buffered streams improve performance.

7. BufferedInputStream读取示例:
成功使用BufferedInputStream读取数据:
Hello, Buffered Stream!
This is a test for BufferedOutputStream.
Buffered streams improve performance.

8. DataOutputStream写入示例:
成功使用DataOutputStream写入各种类型的数据:
Boolean: true
Byte: 100
Short: 2000
Int: 300000
Long: 4000000000
Float: 3.14
Double: 3.1415926535
String: Hello, Data Stream!

9. DataInputStream读取示例:
成功使用DataInputStream读取各种类型的数据:
Boolean: true
Byte: 100
Short: 2000
Int: 300000
Long: 4000000000
Float: 3.14
Double: 3.1415926535
String: Hello, Data Stream!

10. SequenceInputStream基本使用示例:
成功使用SequenceInputStream读取多个流:
读取内容: Hello, Sequence Stream!

11. SequenceInputStream使用Vector合并多个流示例:
成功使用SequenceInputStream读取多个流:
读取内容: First part. Second part. Third part. Fourth part.

12. SequenceInputStream综合示例:
生成的报告:
=== Report Header ===
This is the main content of the report.
It contains important information.
=== Report Footer ===

=== 字节流测试完成 ===
```

## 注意事项

### 流的关闭
- 使用 try-with-resources 语句自动关闭流，避免资源泄露
- 手动关闭流时，应按照从里到外的顺序关闭

### 缓冲流
- 缓冲流需要调用 `flush()` 方法确保数据被写入到底层流
- try-with-resources 语句会自动调用 `flush()` 方法

### 数据流
- 读取数据的顺序必须与写入数据的顺序一致
- 读取的数据类型必须与写入的数据类型一致

### 序列流
- 序列流会按顺序读取多个流，当一个流读取完毕后，会自动关闭该流
- 序列流关闭时，会关闭所有未关闭的流

## 应用场景

### FileInputStream/FileOutputStream
- 读取/写入二进制文件
- 处理文件数据
- 文件复制操作

### ByteArrayInputStream/ByteArrayOutputStream
- 处理内存中的二进制数据
- 数据转换操作
- 网络编程中的数据处理

### BufferedInputStream/BufferedOutputStream
- 需要提高读写效率的场景
- 频繁读写小数据的场景
- 网络编程中的数据传输

### DataInputStream/DataOutputStream
- 读取/写入基本数据类型
- 处理结构化的二进制数据
- 网络编程中的数据传输

### SequenceInputStream
- 合并多个输入流
- 处理多个数据源的场景
- 日志文件合并操作