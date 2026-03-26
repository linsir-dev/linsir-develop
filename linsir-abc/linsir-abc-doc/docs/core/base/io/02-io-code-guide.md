# java.io 包代码说明文档

## 一、代码结构

### 1.1 包结构概览

```
com.linsir.abc.core.base.io/
├── stream/                    # 字节流
│   ├── ByteStreamProcessor.java       # 字节流处理（文件拷贝、读写）
│   ├── DataStreamSerializer.java      # 数据流序列化（基本类型读写）
│   ├── ObjectSerializer.java          # 对象序列化/反序列化
│   └── ExternalizableImplementation.java  # 自定义序列化实现
├── reader/                    # 字符流
│   ├── CharacterStreamProcessor.java  # 字符流处理（文本读写）
│   └── EncodingConverter.java         # 编码转换
└── decorator/                 # 装饰器模式
    ├── StreamDecoratorChain.java      # 流装饰器链构建
    ├── BufferedStreamDecorator.java   # 缓冲装饰器
    └── DataStreamDecorator.java       # 数据装饰器
```

### 1.2 各类详细结构

#### 1.2.1 ByteStreamProcessor.java

**JDK对应代码**: `java.io.InputStream` / `java.io.OutputStream` 及其子类

**类结构**:
```java
public class ByteStreamProcessor {
    // 常量
    private static final int DEFAULT_BUFFER_SIZE = 8192;  // 默认缓冲区大小
    
    // 核心方法
    public long copyFile(String sourcePath, String destPath)     // 文件拷贝
    public byte[] readBytes(String filePath)                     // 读取所有字节
    public void writeBytes(String filePath, byte[] data, boolean append)  // 写入字节
    public boolean compareFiles(String filePath1, String filePath2)       // 文件比较
    public List<byte[]> readInChunks(String filePath, int chunkSize)      // 分块读取
}
```

**设计要点**:
- 使用 `BufferedInputStream/BufferedOutputStream` 提高IO性能
- 采用 try-with-resources 确保资源正确关闭
- 支持大文件分块读写，避免内存溢出
- 默认缓冲区大小 8KB，平衡内存和性能

**JDK类对应关系**:
| 本类方法 | 对应JDK类/方法 | 说明 |
|----------|----------------|------|
| `copyFile()` | `FileInputStream` / `FileOutputStream` | 文件字节流 |
| `readBytes()` | `InputStream.read()` | 字节读取 |
| `writeBytes()` | `OutputStream.write()` | 字节写入 |
| `BufferedInputStream` | `java.io.BufferedInputStream` | 缓冲输入流 |
| `BufferedOutputStream` | `java.io.BufferedOutputStream` | 缓冲输出流 |

#### 1.2.2 ObjectSerializer.java

**JDK对应代码**: `java.io.ObjectOutputStream` / `java.io.ObjectInputStream`

**类结构**:
```java
public class ObjectSerializer {
    // 序列化方法
    public void serialize(String filePath, Serializable object)           // 序列化对象
    public <T> T deserialize(String filePath)                             // 反序列化对象
    public void serializeList(String filePath, List<? extends Serializable> objects)  // 序列化列表
    public <T> List<T> deserializeList(String filePath)                   // 反序列化列表
    public <T> T deepCopy(T object)                                       // 深度拷贝
}
```

**设计要点**:
- 基于 `ObjectOutputStream/ObjectInputStream` 实现
- 支持单个对象和对象列表的序列化
- 通过序列化实现深度拷贝
- 使用 `BufferedStream` 包装提高性能

**JDK类对应关系**:
| 本类方法 | 对应JDK类/方法 | 说明 |
|----------|----------------|------|
| `serialize()` | `ObjectOutputStream.writeObject()` | 对象序列化 |
| `deserialize()` | `ObjectInputStream.readObject()` | 对象反序列化 |
| `Serializable` | `java.io.Serializable` | 序列化接口 |
| `transient` | `java.io` 关键字 | 排除序列化字段 |
| `serialVersionUID` | `java.io` 字段 | 版本控制 |

#### 1.2.3 StreamDecoratorChain.java

**JDK对应代码**: `java.io.FilterInputStream` / `java.io.FilterOutputStream` 装饰器体系

**类结构**:
```java
public class StreamDecoratorChain {
    // 基础装饰方法
    public InputStream decorateWithBuffer(InputStream inputStream)        // 添加缓冲
    public OutputStream decorateWithBuffer(OutputStream outputStream)     // 添加缓冲
    public DataInputStream decorateWithData(InputStream inputStream)      // 添加数据功能
    public DataOutputStream decorateWithData(OutputStream outputStream)   // 添加数据功能
    public ObjectOutputStream decorateWithObject(OutputStream outputStream)  // 添加对象功能
    public ObjectInputStream decorateWithObject(InputStream inputStream)  // 添加对象功能
    
    // 链式构建方法
    public DataInputStream buildInputChain(String filePath)               // 构建输入链
    public DataOutputStream buildOutputChain(String filePath, boolean append)  // 构建输出链
    public ObjectInputStream buildObjectInputChain(String filePath)       // 构建对象输入链
    public ObjectOutputStream buildObjectOutputChain(String filePath, boolean append)  // 构建对象输出链
}
```

**设计要点**:
- 演示装饰器模式在IO流中的应用
- 支持灵活组合各种流装饰器
- 提供链式构建方法简化使用
- 典型装饰链：`FileInputStream -> BufferedInputStream -> DataInputStream`

**JDK类对应关系**:
| 本类方法 | 对应JDK类/方法 | 说明 |
|----------|----------------|------|
| `decorateWithBuffer()` | `BufferedInputStream` / `BufferedOutputStream` | 缓冲装饰器 |
| `decorateWithData()` | `DataInputStream` / `DataOutputStream` | 数据装饰器 |
| `decorateWithObject()` | `ObjectInputStream` / `ObjectOutputStream` | 对象装饰器 |
| `FilterInputStream` | `java.io.FilterInputStream` | 装饰器基类 |
| `FilterOutputStream` | `java.io.FilterOutputStream` | 装饰器基类 |
| `FileInputStream` | `java.io.FileInputStream` | 文件输入流 |
| `FileOutputStream` | `java.io.FileOutputStream` | 文件输出流 |

#### 1.2.4 CharacterStreamProcessor.java

**JDK对应代码**: `java.io.Reader` / `java.io.Writer` 字符流体系

**类结构**:
```java
public class CharacterStreamProcessor {
    // 字符读写方法
    public String readText(String filePath, String charset)               // 读取文本文件
    public void writeText(String filePath, String content, String charset, boolean append)  // 写入文本
    public List<String> readLines(String filePath, String charset)        // 按行读取
    public void writeLines(String filePath, List<String> lines, String charset, boolean append)  // 按行写入
    public void copyTextFile(String sourcePath, String destPath, String charset)  // 拷贝文本文件
}
```

**设计要点**:
- 基于 `Reader/Writer` 体系实现
- 支持指定字符编码
- 使用 `InputStreamReader/OutputStreamWriter` 作为字节到字符的桥梁
- 自动处理字符编码转换

**JDK类对应关系**:
| 本类方法 | 对应JDK类/方法 | 说明 |
|----------|----------------|------|
| `readText()` | `FileReader` / `InputStreamReader` | 字符读取 |
| `writeText()` | `FileWriter` / `OutputStreamWriter` | 字符写入 |
| `readLines()` | `BufferedReader.readLine()` | 按行读取 |
| `InputStreamReader` | `java.io.InputStreamReader` | 字节流转字符流 |
| `OutputStreamWriter` | `java.io.OutputStreamWriter` | 字符流转字节流 |
| `BufferedReader` | `java.io.BufferedReader` | 缓冲字符输入 |
| `BufferedWriter` | `java.io.BufferedWriter` | 缓冲字符输出 |
| `Charset` | `java.nio.charset.Charset` | 字符集处理 |

#### 1.2.5 EncodingConverter.java

**JDK对应代码**: `java.nio.charset.Charset` / `java.nio.charset.CharsetEncoder` / `java.nio.charset.CharsetDecoder`

**类结构**:
```java
public class EncodingConverter {
    // 编码转换方法
    public void convertEncoding(String sourcePath, String sourceCharset, 
                                String destPath, String destCharset)      // 文件编码转换
    public byte[] convertEncoding(byte[] data, String sourceCharset, 
                                  String destCharset)                     // 字节数组编码转换
    public String convertEncoding(String text, String sourceCharset, 
                                  String destCharset)                     // 字符串编码转换
}
```

**设计要点**:
- 支持文件、字节数组、字符串三种形式的编码转换
- 使用 `Charset` 类处理编码
- 正确处理BOM（字节顺序标记）

**JDK类对应关系**:
| 本类方法 | 对应JDK类/方法 | 说明 |
|----------|----------------|------|
| `convertEncoding()` | `Charset.forName()` | 获取字符集 |
| 字节转字符 | `CharsetDecoder.decode()` | 解码操作 |
| 字符转字节 | `CharsetEncoder.encode()` | 编码操作 |
| `StandardCharsets` | `java.nio.charset.StandardCharsets` | 标准字符集 |
| `Charset` | `java.nio.charset.Charset` | 字符集抽象类 |

---

## 二、代码使用场景

### 2.1 ByteStreamProcessor - 字节流处理

**适用场景**:
- 二进制文件读写（图片、视频、可执行文件等）
- 大文件拷贝和传输
- 文件内容比较
- 需要精确控制字节级别的操作

**使用示例**:
```java
ByteStreamProcessor processor = new ByteStreamProcessor();

// 文件拷贝
long copied = processor.copyFile("/path/source.dat", "/path/dest.dat");
System.out.println("拷贝了 " + copied + " 字节");

// 读取文件字节
byte[] data = processor.readBytes("/path/file.bin");

// 写入字节到文件
processor.writeBytes("/path/output.bin", data, false);

// 比较两个文件
boolean same = processor.compareFiles("/path/file1.bin", "/path/file2.bin");
```

**最佳实践**:
| 场景 | 推荐方法 | 原因 |
|------|----------|------|
| 小文件读写 | `readBytes()` / `writeBytes()` | 简单直接 |
| 大文件拷贝 | `copyFile()` | 使用缓冲，性能更好 |
| 超大文件处理 | `readInChunks()` | 分块读取，避免内存溢出 |
| 文件校验 | `compareFiles()` | 逐字节比较，精确可靠 |

### 2.2 ObjectSerializer - 对象序列化

**适用场景**:
- 对象持久化存储
- 网络传输对象
- 对象深度拷贝
- 缓存对象到磁盘

**使用示例**:
```java
ObjectSerializer serializer = new ObjectSerializer();

// 序列化单个对象
User user = new User("张三", 25);
serializer.serialize("/path/user.dat", user);

// 反序列化
User restored = serializer.deserialize("/path/user.dat");

// 序列化对象列表
List<User> users = Arrays.asList(user1, user2, user3);
serializer.serializeList("/path/users.dat", users);

// 深度拷贝
User copy = serializer.deepCopy(user);
```

**注意事项**:
- 被序列化的类必须实现 `Serializable` 接口
- 敏感字段使用 `transient` 关键字排除
- 建议显式定义 `serialVersionUID`

### 2.3 StreamDecoratorChain - 流装饰器链

**适用场景**:
- 需要灵活组合流功能的场景
- 动态添加缓冲、数据、对象等功能
- 理解装饰器模式在IO中的应用

**使用示例**:
```java
StreamDecoratorChain chain = new StreamDecoratorChain();

// 方式1：使用链式构建方法（推荐）
DataInputStream dis = chain.buildInputChain("/path/data.bin");
int value = dis.readInt();
dis.close();

// 方式2：手动构建装饰链
FileInputStream fis = new FileInputStream("/path/data.bin");
BufferedInputStream bis = new BufferedInputStream(fis);
DataInputStream dis = new DataInputStream(bis);

// 方式3：使用装饰方法逐步包装
InputStream is = new FileInputStream("/path/data.bin");
InputStream buffered = chain.decorateWithBuffer(is);
DataInputStream data = chain.decorateWithData(buffered);
```

**装饰器层次**:
```
最底层: FileInputStream/FileOutputStream (文件字节流)
    ↓
第二层: BufferedInputStream/BufferedOutputStream (缓冲)
    ↓
第三层: DataInputStream/DataOutputStream (基本数据类型)
    ↓
第四层: ObjectInputStream/ObjectOutputStream (对象序列化)
```

### 2.4 CharacterStreamProcessor - 字符流处理

**适用场景**:
- 文本文件读写
- 需要指定编码的文件处理
- 按行处理文本文件
- 配置文件、日志文件处理

**使用示例**:
```java
CharacterStreamProcessor processor = new CharacterStreamProcessor();

// 读取文本文件
String content = processor.readText("/path/file.txt", "UTF-8");

// 写入文本文件
processor.writeText("/path/output.txt", "Hello World", "UTF-8", false);

// 按行读取
List<String> lines = processor.readLines("/path/file.txt", "UTF-8");
for (String line : lines) {
    System.out.println(line);
}

// 按行写入
List<String> lines = Arrays.asList("Line 1", "Line 2", "Line 3");
processor.writeLines("/path/lines.txt", lines, "UTF-8", false);
```

**字节流 vs 字符流选择**:
| 场景 | 推荐 | 原因 |
|------|------|------|
| 文本文件 | 字符流 | 自动处理编码 |
| 二进制文件 | 字节流 | 精确控制每个字节 |
| 网络传输文本 | 字符流 | 正确处理多字节字符 |
| 图片/视频 | 字节流 | 保持原始字节不变 |

### 2.5 EncodingConverter - 编码转换

**适用场景**:
- 处理不同编码的文本文件
- 历史系统数据迁移
- 跨平台文件交换
- 编码不一致问题处理

**使用示例**:
```java
EncodingConverter converter = new EncodingConverter();

// 文件编码转换（GBK转UTF-8）
converter.convertEncoding("/path/gbk.txt", "GBK", 
                          "/path/utf8.txt", "UTF-8");

// 字符串编码转换
String utf8Text = converter.convertEncoding(gbkText, "GBK", "UTF-8");

// 字节数组编码转换
byte[] utf8Bytes = converter.convertEncoding(gbkBytes, "GBK", "UTF-8");
```

---

## 三、测试代码结构

### 3.1 测试包结构

```
src/test/java/com/linsir/abc/core/base/io/
├── stream/
│   ├── ByteStreamProcessorTest.java         # 字节流处理器测试
│   ├── DataStreamSerializerTest.java        # 数据流序列化测试
│   ├── ObjectSerializerTest.java            # 对象序列化测试
│   └── ExternalizableImplementationTest.java # 自定义序列化测试
├── reader/
│   ├── CharacterStreamProcessorTest.java    # 字符流处理器测试
│   └── EncodingConverterTest.java           # 编码转换测试
└── decorator/
    ├── StreamDecoratorChainTest.java        # 流装饰器链测试
    ├── BufferedStreamDecoratorTest.java     # 缓冲装饰器测试
    └── DataStreamDecoratorTest.java         # 数据装饰器测试
```

### 3.2 测试类结构示例

#### ByteStreamProcessorTest.java

```java
public class ByteStreamProcessorTest {
    @TempDir Path tempDir;  // JUnit 5临时目录
    
    @BeforeEach void setUp()  // 测试前初始化
    
    // 基础功能测试
    @Test void testWriteAndReadBytes()      // 写入读取字节
    @Test void testAppendWrite()            // 追加写入
    @Test void testCopyFile()               // 文件拷贝
    
    // 文件比较测试
    @Test void testCompareFilesSame()       // 相同文件比较
    @Test void testCompareFilesDifferent()  // 不同文件比较
    
    // 异常测试
    @Test void testReadNonExistentFile()    // 读取不存在文件
    @Test void testCopyNonExistentFile()    // 拷贝不存在文件
}
```

#### ObjectSerializerTest.java

```java
public class ObjectSerializerTest {
    @TempDir Path tempDir;
    
    // 序列化测试
    @Test void testSerializeAndDeserialize()           // 序列化反序列化
    @Test void testSerializeAndDeserializeList()       // 列表序列化
    @Test void testDeepCopy()                          // 深度拷贝
    
    // 特殊场景测试
    @Test void testSerializeWithTransientField()       // transient字段
    @Test void testSerializeNull()                     // null对象
    @Test void testDeserializeNonExistentFile()       // 文件不存在
}
```

#### StreamDecoratorChainTest.java

```java
public class StreamDecoratorChainTest {
    @TempDir Path tempDir;
    
    // 装饰器测试
    @Test void testDecorateWithBuffer()         // 缓冲装饰器
    @Test void testDecorateWithData()           // 数据装饰器
    @Test void testDecorateWithObject()         // 对象装饰器
    
    // 链式构建测试
    @Test void testBuildInputChain()            // 构建输入链
    @Test void testBuildOutputChain()           // 构建输出链
    @Test void testBuildObjectChain()           // 构建对象链
}
```

---

## 四、单元测试预期结果

### 4.1 ByteStreamProcessorTest 预期结果

| 测试方法 | 预期结果 | 说明 |
|----------|----------|------|
| `testWriteAndReadBytes` | ✅ PASS | 写入的字节能正确读取 |
| `testAppendWrite` | ✅ PASS | 追加写入内容正确 |
| `testCopyFile` | ✅ PASS | 文件拷贝字节数和内容正确 |
| `testCompareFilesSame` | ✅ PASS | 相同文件返回true |
| `testCompareFilesDifferent` | ✅ PASS | 不同文件返回false |
| `testReadInChunks` | ✅ PASS | 分块读取内容完整 |
| `testReadNonExistentFile` | ✅ PASS | 抛出FileNotFoundException |

### 4.2 ObjectSerializerTest 预期结果

| 测试方法 | 预期结果 | 说明 |
|----------|----------|------|
| `testSerializeAndDeserialize` | ✅ PASS | 对象序列化反序列化正确 |
| `testSerializeAndDeserializeList` | ✅ PASS | 列表序列化反序列化正确 |
| `testDeepCopy` | ✅ PASS | 深度拷贝对象独立 |
| `testSerializeWithTransientField` | ✅ PASS | transient字段不被序列化 |
| `testDeserializeNonExistentFile` | ✅ PASS | 抛出FileNotFoundException |

### 4.3 StreamDecoratorChainTest 预期结果

| 测试方法 | 预期结果 | 说明 |
|----------|----------|------|
| `testDecorateWithBuffer` | ✅ PASS | 缓冲装饰器正常工作 |
| `testDecorateWithData` | ✅ PASS | 数据装饰器读写正确 |
| `testDecorateWithObject` | ✅ PASS | 对象装饰器序列化正确 |
| `testBuildInputChain` | ✅ PASS | 输入链构建成功 |
| `testBuildOutputChain` | ✅ PASS | 输出链构建成功 |
| `testBuildObjectChain` | ✅ PASS | 对象链构建成功 |

### 4.4 CharacterStreamProcessorTest 预期结果

| 测试方法 | 预期结果 | 说明 |
|----------|----------|------|
| `testReadAndWriteText` | ✅ PASS | 文本读写正确 |
| `testReadAndWriteLines` | ✅ PASS | 按行读写正确 |
| `testCopyTextFile` | ✅ PASS | 文本文件拷贝正确 |
| `testDifferentCharsets` | ✅ PASS | 不同编码处理正确 |
| `testAppendText` | ✅ PASS | 文本追加写入正确 |

### 4.5 EncodingConverterTest 预期结果

| 测试方法 | 预期结果 | 说明 |
|----------|----------|------|
| `testConvertFileEncoding` | ✅ PASS | 文件编码转换正确 |
| `testConvertBytesEncoding` | ✅ PASS | 字节数组编码转换正确 |
| `testConvertStringEncoding` | ✅ PASS | 字符串编码转换正确 |
| `testUtf8ToGbk` | ✅ PASS | UTF-8转GBK正确 |
| `testGbkToUtf8` | ✅ PASS | GBK转UTF-8正确 |

### 4.6 所有测试汇总

| 测试类 | 测试数 | 预期通过率 |
|--------|--------|------------|
| ByteStreamProcessorTest | 7 | 100% |
| DataStreamSerializerTest | 6 | 100% |
| ObjectSerializerTest | 6 | 100% |
| ExternalizableImplementationTest | 4 | 100% |
| CharacterStreamProcessorTest | 6 | 100% |
| EncodingConverterTest | 5 | 100% |
| StreamDecoratorChainTest | 6 | 100% |
| BufferedStreamDecoratorTest | 4 | 100% |
| DataStreamDecoratorTest | 4 | 100% |
| **总计** | **48** | **100%** |

---

## 五、运行测试

### 5.1 运行所有io包测试

```bash
mvn test -Dtest="com.linsir.abc.core.base.io.**"
```

### 5.2 运行单个测试类

```bash
mvn test -Dtest=ByteStreamProcessorTest
```

### 5.3 预期输出

```
Tests run: 48, Failures: 0, Errors: 0, Skipped: 0
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.linsir.abc.core.base.io.stream.ByteStreamProcessorTest
[INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0
...
[INFO] BUILD SUCCESS
```

---

## 六、IO流选择指南

### 6.1 按数据类型选择

| 数据类型 | 推荐流 | 类 |
|----------|--------|-----|
| 字节数据 | 字节流 | ByteStreamProcessor |
| 文本数据 | 字符流 | CharacterStreamProcessor |
| Java对象 | 对象流 | ObjectSerializer |
| 基本类型 | 数据流 | DataStreamSerializer |

### 6.2 按功能选择

| 功能需求 | 推荐方式 | 说明 |
|----------|----------|------|
| 简单读写 | 基础流 | FileInputStream/FileOutputStream |
| 提高性能 | 缓冲流 | BufferedInputStream/BufferedOutputStream |
| 读写基本类型 | 数据流 | DataInputStream/DataOutputStream |
| 读写对象 | 对象流 | ObjectInputStream/ObjectOutputStream |
| 灵活组合 | 装饰器链 | StreamDecoratorChain |

---

**文档版本**: 1.0.0  
**最后更新**: 2026-03-26
