# java.io 包面试题汇总

## 目录

1. [IO 基础概念](#一io-基础概念)
2. [字节流](#二字节流)
3. [字符流](#三字符流)
4. [序列化与反序列化](#四序列化与反序列化)
5. [装饰器模式](#五装饰器模式)
6. [NIO 对比](#六nio-对比)

---

## 一、IO 基础概念

### 1.1 什么是 Java IO？

**问题**: 请简述 Java IO 是什么，以及它的核心作用？

**答案**:
Java IO（Input/Output）是 Java 提供的用于数据输入输出的 API，位于 `java.io` 包中。核心作用包括：

- **数据传输**: 实现程序与外部设备（文件、网络、控制台等）之间的数据交换
- **数据持久化**: 将内存中的数据保存到磁盘
- **数据读取**: 从各种数据源读取数据到程序

**核心设计**:
- 基于**流（Stream）**的概念，数据像水流一样连续传输
- 提供**字节流**和**字符流**两种抽象
- 采用**装饰器模式**实现功能的灵活组合

---

### 1.2 字节流和字符流的区别？

**问题**: 字节流（Byte Stream）和字符流（Character Stream）有什么区别？如何选择？

**答案**:

| 对比项 | 字节流 | 字符流 |
|--------|--------|--------|
| **处理单位** | 字节（8位） | 字符（16位 Unicode） |
| **抽象类** | `InputStream` / `OutputStream` | `Reader` / `Writer` |
| **适用场景** | 二进制数据（图片、视频、可执行文件） | 文本数据（TXT、XML、JSON） |
| **编码处理** | 不处理编码，直接读写原始字节 | 自动处理字符编码转换 |
| **缓冲机制** | 需要手动实现或配合缓冲流 | 通常配合缓冲流使用 |

**选择建议**:
- 操作文本文件 → 使用字符流
- 操作二进制文件 → 使用字节流
- 不确定文件类型 → 使用字节流

**代码示例**:
```java
// 字节流 - 复制图片
FileInputStream fis = new FileInputStream("image.jpg");
FileOutputStream fos = new FileOutputStream("copy.jpg");
int b;
while ((b = fis.read()) != -1) {
    fos.write(b);
}

// 字符流 - 复制文本
FileReader fr = new FileReader("text.txt");
FileWriter fw = new FileWriter("copy.txt");
int c;
while ((c = fr.read()) != -1) {
    fw.write(c);
}
```

---

### 1.3 输入流和输出流的区别？

**问题**: 输入流（Input Stream）和输出流（Output Stream）有什么区别？

**答案**:

**输入流（Input Stream）**:
- 方向：从数据源（文件、网络、键盘等）**读取**数据到程序
- 核心方法：`read()` 系列方法
- 代表类：`FileInputStream`、`BufferedReader`

**输出流（Output Stream）**:
- 方向：从程序**写入**数据到目的地（文件、网络、控制台等）
- 核心方法：`write()` 系列方法
- 代表类：`FileOutputStream`、`BufferedWriter`

**记忆技巧**: 以**程序**为中心，数据流入程序是输入，流出程序是输出。

---

## 二、字节流

### 2.1 FileInputStream 和 FileOutputStream

**问题**: `FileInputStream` 和 `FileOutputStream` 的使用场景是什么？有什么注意事项？

**答案**:

**使用场景**:
- 文件的**字节级**读写操作
- 适合处理**二进制文件**（图片、音频、视频、可执行文件等）
- 文件复制、文件加密解密等操作

**注意事项**:

1. **必须关闭流**: 使用 `try-with-resources` 或手动调用 `close()`
```java
try (FileInputStream fis = new FileInputStream("file.txt")) {
    // 读取操作
} catch (IOException e) {
    e.printStackTrace();
}
```

2. **追加模式**: `FileOutputStream` 构造函数第二个参数控制是否追加
```java
// 覆盖模式（默认）
new FileOutputStream("file.txt");
// 追加模式
new FileOutputStream("file.txt", true);
```

3. **缓冲优化**: 单字节读写效率低，建议使用缓冲区
```java
byte[] buffer = new byte[1024];
int len;
while ((len = fis.read(buffer)) != -1) {
    fos.write(buffer, 0, len);
}
```

---

### 2.2 BufferedInputStream 和 BufferedOutputStream

**问题**: 缓冲流（Buffered Stream）的作用是什么？为什么能提高性能？

**答案**:

**作用**:
- 为字节流提供**缓冲功能**，减少系统调用次数
- 内置**8KB缓冲区**（可自定义大小）
- 实现批量读写，减少 IO 操作次数

**性能提升原理**:

| 操作方式 | 1MB 文件耗时 | 原理 |
|----------|--------------|------|
| 单字节读写 | ~60秒 | 每次读写都进行系统调用 |
| 1KB 缓冲区 | ~100ms | 减少系统调用次数 |
| 缓冲流 | ~50ms | 内置优化，批量读写 |

**代码示例**:
```java
// 不使用缓冲流 - 慢
FileInputStream fis = new FileInputStream("large.zip");
FileOutputStream fos = new FileOutputStream("copy.zip");
int b;
while ((b = fis.read()) != -1) {  // 每次1字节
    fos.write(b);
}

// 使用缓冲流 - 快
BufferedInputStream bis = new BufferedInputStream(
    new FileInputStream("large.zip"));
BufferedOutputStream bos = new BufferedOutputStream(
    new FileOutputStream("copy.zip"));
int b;
while ((b = bis.read()) != -1) {  // 内部批量读取
    bos.write(b);
}
```

**最佳实践**:
- 处理大文件时**必须使用**缓冲流
- 缓冲区大小建议 8KB 或 16KB
- 最后一定要 `flush()` 或 `close()`

---

### 2.3 DataInputStream 和 DataOutputStream

**问题**: 数据流（Data Stream）有什么特点？适合什么场景？

**答案**:

**特点**:
- 支持**基本数据类型**的读写（int、long、double、boolean 等）
- 保持数据类型信息，读取时不需要手动转换
- 采用**机器无关**的格式存储

**适用场景**:
- 需要保存基本数据类型的二进制文件
- 网络传输中的协议数据
- 游戏存档、配置文件等结构化数据

**核心方法**:

| 写入方法 | 读取方法 | 说明 |
|----------|----------|------|
| `writeInt(int)` | `readInt()` | 4字节整数 |
| `writeLong(long)` | `readLong()` | 8字节长整数 |
| `writeDouble(double)` | `readDouble()` | 8字节双精度浮点 |
| `writeUTF(String)` | `readUTF()` | UTF-8 编码字符串 |
| `writeBoolean(boolean)` | `readBoolean()` | 1字节布尔值 |

**代码示例**:
```java
// 写入数据
try (DataOutputStream dos = new DataOutputStream(
        new FileOutputStream("data.bin"))) {
    dos.writeInt(100);
    dos.writeDouble(3.14);
    dos.writeUTF("Hello");
    dos.writeBoolean(true);
}

// 读取数据
try (DataInputStream dis = new DataInputStream(
        new FileInputStream("data.bin"))) {
    int i = dis.readInt();
    double d = dis.readDouble();
    String s = dis.readUTF();
    boolean b = dis.readBoolean();
}
```

**注意事项**:
- **读写顺序必须一致**
- `readUTF()` 只能读取 `writeUTF()` 写入的字符串

---

## 三、字符流

### 3.1 FileReader 和 FileWriter

**问题**: `FileReader` 和 `FileWriter` 与字节流有什么区别？有什么缺陷？

**答案**:

**区别**:
- 操作单位是**字符**（16位 Unicode）而非字节
- 自动处理**字符编码**转换
- 适合处理**文本文件**

**缺陷**:
- **不能指定字符编码**，使用系统默认编码
- 在跨平台时可能出现**乱码问题**
- 无法处理非默认编码的文件（如 UTF-8 文件在 GBK 系统上）

**解决方案**:
使用 `InputStreamReader` / `OutputStreamWriter` 包装字节流，显式指定编码：
```java
// 指定 UTF-8 编码读取
InputStreamReader isr = new InputStreamReader(
    new FileInputStream("utf8.txt"), "UTF-8");

// 指定 GBK 编码写入
OutputStreamWriter osw = new OutputStreamWriter(
    new FileOutputStream("gbk.txt"), "GBK");
```

---

### 3.2 InputStreamReader 和 OutputStreamWriter

**问题**: 转换流（InputStreamReader/OutputStreamWriter）的作用是什么？

**答案**:

**作用**:
- **桥接**字节流和字符流
- 允许**显式指定字符编码**
- 解决跨平台编码问题

**设计模式**: **适配器模式（Adapter Pattern）**
- 将字节流适配为字符流接口
- 内部完成字节到字符的编码转换

**代码示例**:
```java
// 读取 UTF-8 编码的文件
InputStreamReader isr = new InputStreamReader(
    new FileInputStream("file.txt"), StandardCharsets.UTF_8);
BufferedReader br = new BufferedReader(isr);
String line;
while ((line = br.readLine()) != null) {
    System.out.println(line);
}

// 写入 GBK 编码的文件
OutputStreamWriter osw = new OutputStreamWriter(
    new FileOutputStream("file.txt"), Charset.forName("GBK"));
BufferedWriter bw = new BufferedWriter(osw);
bw.write("中文内容");
bw.newLine();  // 跨平台的换行
```

**常用编码**:
- `StandardCharsets.UTF_8` - 通用编码
- `StandardCharsets.ISO_8859_1` - 西欧语言
- `Charset.forName("GBK")` - 简体中文
- `Charset.forName("GB2312")` - 简体中文（旧版）

---

### 3.3 BufferedReader 和 BufferedWriter

**问题**: 字符缓冲流的优势是什么？`readLine()` 和 `newLine()` 的作用？

**答案**:

**优势**:
1. **缓冲机制**: 减少 IO 操作次数
2. **按行读写**: `readLine()` 方便处理文本文件
3. **跨平台换行**: `newLine()` 自动适配系统换行符

**核心方法**:

| 方法 | 作用 |
|------|------|
| `readLine()` | 读取一行文本（不含换行符） |
| `newLine()` | 写入系统相关的换行符 |
| `read(char[])` | 批量读取字符 |
| `write(String)` | 写入字符串 |

**代码示例**:
```java
// 复制文本文件，按行处理
try (BufferedReader br = new BufferedReader(
        new FileReader("source.txt"));
     BufferedWriter bw = new BufferedWriter(
        new FileWriter("target.txt"))) {
    
    String line;
    while ((line = br.readLine()) != null) {
        // 处理每一行
        bw.write(line.toUpperCase());
        bw.newLine();  // 写入换行符
    }
}
```

**注意事项**:
- `readLine()` 返回 `null` 表示文件结束
- `readLine()` **不包含**换行符，需要手动添加
- 大文件处理时，按行读取比按字符读取效率高

---

### 3.4 字符编码问题

**问题**: 什么是字符编码？常见的编码有哪些？如何解决乱码问题？

**答案**:

**字符编码**: 将字符映射为字节的规则

**常见编码**:

| 编码 | 字节数 | 特点 |
|------|--------|------|
| ASCII | 1字节 | 128个字符，英文和符号 |
| ISO-8859-1 | 1字节 | 256个字符，西欧语言 |
| GBK | 1-2字节 | 简体中文，兼容 GB2312 |
| UTF-8 | 1-4字节 | 变长编码，国际通用 |
| UTF-16 | 2或4字节 | Java 内部使用 |

**乱码原因**:
1. **编码不一致**: 文件用 UTF-8 保存，用 GBK 读取
2. **缺少指定**: 使用默认编码，跨平台时不同
3. **BOM 头问题**: 某些编辑器添加的 UTF-8 BOM

**解决方案**:
```java
// 1. 明确指定编码
BufferedReader br = new BufferedReader(
    new InputStreamReader(
        new FileInputStream("file.txt"), 
        StandardCharsets.UTF_8));

// 2. 统一项目编码
// IDE 设置、文件保存、JVM 参数都使用 UTF-8

// 3. 处理 BOM
// 读取时跳过前3个字节（EF BB BF）
```

---

## 四、序列化与反序列化

### 4.1 什么是序列化？

**问题**: 什么是 Java 序列化（Serialization）？有什么作用？

**答案**:

**定义**: 将对象转换为**字节序列**的过程，便于存储或传输。

**反序列化**: 将字节序列恢复为对象的过程。

**作用**:
1. **对象持久化**: 将对象保存到文件或数据库
2. **网络传输**: RMI、RPC 等远程调用
3. **缓存**: 将对象序列化后存入缓存系统
4. **深拷贝**: 通过序列化实现对象的深拷贝

**实现方式**:
```java
// 实现 Serializable 接口
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private int age;
    // ...
}
```

---

### 4.2 Serializable 接口

**问题**: `Serializable` 接口有什么特点？为什么是一个空接口？

**答案**:

**特点**:
- **标记接口（Marker Interface）**: 没有任何方法
- JVM 识别该接口后，启用序列化机制
- 所有非 transient 字段都会被序列化

**为什么是空接口**:
- 仅用于**标记**该类可以被序列化
- 具体序列化逻辑由 JVM 底层实现（`ObjectOutputStream`）
- 遵循**约定优于配置**原则

**代码示例**:
```java
public class Person implements Serializable {
    // 序列化版本号
    private static final long serialVersionUID = 1L;
    
    private String name;      // 会被序列化
    private transient int age; // 不会被序列化
    private static String country; // 静态变量不会被序列化
}
```

---

### 4.3 serialVersionUID 的作用

**问题**: `serialVersionUID` 是什么？不指定会有什么后果？

**答案**:

**作用**:
- 序列化的**版本标识符**
- 反序列化时验证**版本兼容性**
- 确保序列化和反序列化的类版本一致

**不指定的后果**:
- JVM 根据类的结构**自动生成** serialVersionUID
- 类结构变化后，自动生成的 UID 会改变
- 导致**反序列化失败**，抛出 `InvalidClassException`

**最佳实践**:
```java
public class User implements Serializable {
    // 显式指定，避免自动生成的风险
    private static final long serialVersionUID = 1L;
    // 或根据类结构生成
    private static final long serialVersionUID = 123456789L;
}
```

**版本兼容性规则**:
- UID 相同 → 可以反序列化
- UID 不同 → 抛出异常
- 新增字段 → 使用默认值
- 删除字段 → 忽略该字段

---

### 4.4 transient 关键字

**问题**: `transient` 关键字的作用是什么？使用场景有哪些？

**答案**:

**作用**: 标记字段**不参与序列化**

**使用场景**:

1. **敏感信息**: 密码、密钥等安全数据
```java
public class User implements Serializable {
    private String username;
    private transient String password;  // 不序列化密码
    private transient String token;     // 不序列化令牌
}
```

2. **临时数据**: 缓存、计算结果等
```java
public class Data implements Serializable {
    private int[] rawData;
    private transient int sum;  // 可重新计算，无需序列化
}
```

3. **不可序列化的对象**: Thread、InputStream 等
```java
public class Task implements Serializable {
    private String name;
    private transient Thread worker;  // Thread 不可序列化
}
```

**反序列化后**:
- transient 字段值为**默认值**（null、0、false）
- 可通过 `readObject()` 方法自定义恢复逻辑

---

### 4.5 自定义序列化

**问题**: 如何自定义序列化过程？`writeObject()` 和 `readObject()` 的作用？

**答案**:

**自定义方法**: 在类中定义私有方法
```java
private void writeObject(ObjectOutputStream out) throws IOException
private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
```

**使用场景**:
1. **加密敏感字段**
2. **压缩数据**
3. **处理 transient 字段的恢复**
4. **验证数据完整性**

**代码示例**:
```java
public class User implements Serializable {
    private String username;
    private transient String password;
    
    // 自定义序列化
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();  // 序列化非 transient 字段
        // 加密后序列化密码
        out.writeObject(encrypt(password));
    }
    
    // 自定义反序列化
    private void readObject(ObjectInputStream in) 
            throws IOException, ClassNotFoundException {
        in.defaultReadObject();  // 反序列化非 transient 字段
        // 解密密码
        password = decrypt((String) in.readObject());
    }
    
    private String encrypt(String pwd) { /* ... */ }
    private String decrypt(String pwd) { /* ... */ }
}
```

---

### 4.6 Externalizable 接口

**问题**: `Externalizable` 与 `Serializable` 有什么区别？

**答案**:

| 对比项 | Serializable | Externalizable |
|--------|--------------|----------------|
| **接口方法** | 空接口 | `writeExternal()` / `readExternal()` |
| **控制粒度** | JVM 自动处理 | 完全由程序员控制 |
| **灵活性** | 较低 | 高 |
| **性能** | 一般 | 更好（可优化） |
| **复杂度** | 简单 | 复杂 |
| **无参构造** | 不需要 | **必须提供** |

**Externalizable 示例**:
```java
public class User implements Externalizable {
    private String name;
    private int age;
    
    // 必须提供无参构造
    public User() {}
    
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(name);
        out.writeInt(age);
    }
    
    @Override
    public void readExternal(ObjectInput in) 
            throws IOException, ClassNotFoundException {
        name = in.readUTF();
        age = in.readInt();
    }
}
```

**选择建议**:
- 简单场景 → 使用 `Serializable`
- 需要完全控制 → 使用 `Externalizable`
- 性能敏感 → 使用 `Externalizable`

---

## 五、装饰器模式

### 5.1 IO 中的装饰器模式

**问题**: Java IO 中使用了什么设计模式？请举例说明。

**答案**:

**主要设计模式**: **装饰器模式（Decorator Pattern）**

**模式特点**:
- 动态地给对象添加额外的职责
- 比继承更灵活，避免类爆炸
- 通过包装（Wrapper）实现功能增强

**IO 中的装饰器**:

```
InputStream (抽象组件)
├── FileInputStream (具体组件)
├── ByteArrayInputStream (具体组件)
├── FilterInputStream (抽象装饰器)
│   ├── BufferedInputStream (具体装饰器)
│   ├── DataInputStream (具体装饰器)
│   └── PushbackInputStream (具体装饰器)
```

**代码示例**:
```java
// 基础流
InputStream fis = new FileInputStream("file.txt");

// 添加缓冲功能
InputStream bis = new BufferedInputStream(fis);

// 添加数据类型支持
DataInputStream dis = new DataInputStream(bis);

// 使用
int i = dis.readInt();
double d = dis.readDouble();
```

**优势**:
- 功能可**自由组合**
- 新增功能无需修改原有类
- 符合**开闭原则**

---

### 5.2 装饰器 vs 继承

**问题**: 装饰器模式相比继承有什么优势？

**答案**:

| 对比项 | 继承 | 装饰器模式 |
|--------|------|------------|
| **灵活性** | 静态，编译时确定 | 动态，运行时组合 |
| **类数量** | 功能组合导致类爆炸 | 类数量可控 |
| **功能叠加** | 需要创建新子类 | 可以嵌套包装 |
| **代码复用** | 通过父类复用 | 通过组件复用 |

**类爆炸示例**:
```
// 继承方式 - 需要大量子类
FileInputStream
├── BufferedFileInputStream
├── DataFileInputStream
├── BufferedDataFileInputStream
└── ...

// 装饰器方式 - 只需几个类
FileInputStream + BufferedInputStream + DataInputStream
```

---

## 六、NIO 对比

### 6.1 IO vs NIO

**问题**: 传统 IO 和 NIO（New IO）有什么区别？

**答案**:

| 特性 | 传统 IO | NIO |
|------|---------|-----|
| **数据处理方式** | 流式（Stream） | 块式（Block） |
| **操作单位** | 字节/字符流 | Buffer（缓冲区） |
| **IO 模式** | 阻塞 IO | 支持非阻塞 IO |
| **选择器** | 不支持 | Selector 多路复用 |
| **性能** | 一般 | 高并发场景更优 |
| **复杂度** | 简单 | 较复杂 |
| **适用场景** | 简单文件操作 | 高并发网络编程 |

**核心区别**:

1. **数据流向**:
   - IO: 单向流，只能读或写
   - NIO: Buffer 可读写切换（flip）

2. **阻塞方式**:
   - IO: 阻塞式，线程等待数据就绪
   - NIO: 支持非阻塞，线程可处理多个连接

3. **选择器**:
   - IO: 一个连接一个线程
   - NIO: 一个线程管理多个连接（Selector）

**代码对比**:
```java
// 传统 IO - 阻塞
BufferedReader reader = new BufferedReader(
    new FileReader("file.txt"));
String line = reader.readLine();  // 阻塞等待

// NIO - 非阻塞
FileChannel channel = new RandomAccessFile("file.txt", "r")
    .getChannel();
ByteBuffer buffer = ByteBuffer.allocate(1024);
int bytesRead = channel.read(buffer);  // 可能立即返回
```

---

### 6.2 什么时候使用 NIO？

**问题**: 什么场景下应该选择 NIO 而不是传统 IO？

**答案**:

**使用 NIO 的场景**:

1. **高并发网络服务器**
   - 单机需要处理数万连接
   - 使用 Selector 实现单线程多路复用

2. **大文件操作**
   - 内存映射文件（MappedByteBuffer）
   - 直接操作内存，绕过系统缓冲区

3. **性能敏感场景**
   - 需要精细控制缓冲区
   - 减少数据拷贝次数

**使用传统 IO 的场景**:

1. **简单文件读写**
   - 代码简单，易于维护
   - 性能足够满足需求

2. **阻塞式网络通信**
   - 连接数不多
   - 逻辑简单清晰

3. **文本处理**
   - 字符流处理更方便
   - 编码处理更简单

---

## 七、常见问题与最佳实践

### 7.1 资源释放

**问题**: IO 操作后为什么要关闭流？如何正确关闭？

**答案**:

**原因**:
1. **释放系统资源**: 文件句柄、网络连接等
2. **刷新缓冲区**: 确保数据写入目标
3. **避免内存泄漏**: 长期占用资源

**正确关闭方式**:

```java
// 方式1: try-with-resources（推荐）
try (BufferedReader br = new BufferedReader(
        new FileReader("file.txt"))) {
    String line;
    while ((line = br.readLine()) != null) {
        System.out.println(line);
    }
} catch (IOException e) {
    e.printStackTrace();
}

// 方式2: 手动关闭（Java 7 之前）
BufferedReader br = null;
try {
    br = new BufferedReader(new FileReader("file.txt"));
    // 读取操作
} catch (IOException e) {
    e.printStackTrace();
} finally {
    if (br != null) {
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// 方式3: 多个流的情况
try (FileInputStream fis = new FileInputStream("in.txt");
     FileOutputStream fos = new FileOutputStream("out.txt")) {
    // 复制操作
} catch (IOException e) {
    e.printStackTrace();
}
```

**注意事项**:
- 关闭外层流会自动关闭内层流
- `BufferedWriter` 关闭前建议 `flush()`

---

### 7.2 异常处理

**问题**: IO 操作中常见的异常有哪些？如何处理？

**答案**:

**常见异常**:

| 异常 | 原因 | 处理建议 |
|------|------|----------|
| `FileNotFoundException` | 文件不存在 | 检查路径，创建文件 |
| `IOException` | 读写错误 | 检查磁盘空间、权限 |
| `EOFException` | 意外到达文件尾 | 检查数据完整性 |
| `SocketException` | 网络连接问题 | 检查网络状态 |
| `UnsupportedEncodingException` | 不支持的编码 | 使用标准编码 |

**处理策略**:
```java
public void copyFile(String src, String dest) {
    try (InputStream is = new FileInputStream(src);
         OutputStream os = new FileOutputStream(dest)) {
        
        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        
    } catch (FileNotFoundException e) {
        System.err.println("文件不存在: " + e.getMessage());
        // 记录日志，通知用户
    } catch (IOException e) {
        System.err.println("IO 错误: " + e.getMessage());
        // 记录日志，考虑重试
    }
}
```

---

### 7.3 性能优化

**问题**: 如何提高 IO 操作的性能？

**答案**:

**优化策略**:

1. **使用缓冲流**
```java
// 不使用缓冲 - 慢
FileInputStream fis = new FileInputStream("file.zip");

// 使用缓冲 - 快
BufferedInputStream bis = new BufferedInputStream(
    new FileInputStream("file.zip"));
```

2. **批量读写**
```java
// 单字节读写 - 慢
int b;
while ((b = is.read()) != -1) {
    os.write(b);
}

// 批量读写 - 快
byte[] buffer = new byte[8192];
int len;
while ((len = is.read(buffer)) != -1) {
    os.write(buffer, 0, len);
}
```

3. **NIO 处理大文件**
```java
FileChannel channel = new RandomAccessFile("large.bin", "r")
    .getChannel();
MappedByteBuffer buffer = channel.map(
    FileChannel.MapMode.READ_ONLY, 0, channel.size());
```

4. **避免频繁创建流**
```java
// 不好：循环中创建流
for (String file : files) {
    new FileInputStream(file);  // 重复创建
}

// 好：复用或批量处理
```

---

## 八、面试题速查表

| 问题 | 核心要点 |
|------|----------|
| 字节流 vs 字符流 | 字节流处理二进制，字符流处理文本 |
| 缓冲流的作用 | 减少系统调用，批量读写 |
| 序列化作用 | 对象持久化、网络传输 |
| serialVersionUID | 版本控制，防止不兼容 |
| transient | 标记不序列化的字段 |
| 装饰器模式 | 动态添加功能，灵活组合 |
| IO vs NIO | 阻塞 vs 非阻塞，流 vs 缓冲区 |
| 资源关闭 | try-with-resources 或 finally |

---

**文档版本**: 1.0.0  
**最后更新**: 2026-03-26  
**适用 JDK**: Java 8+
