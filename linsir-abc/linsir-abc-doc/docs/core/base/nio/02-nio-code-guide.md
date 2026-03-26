# java.nio 包代码说明文档

## 一、代码结构

### 1.1 包结构概览

```
com.linsir.abc.core.base.nio/
├── buffer/                    # Buffer操作
│   ├── BufferStateManager.java        # Buffer状态管理（flip/clear/rewind/compact）
│   └── ByteBufferAllocator.java       # ByteBuffer分配（堆缓冲区/直接缓冲区）
├── channel/                   # Channel通信
│   ├── FileChannelTransfer.java       # 文件通道传输（零拷贝/内存映射）
│   └── SocketChannelCommunication.java # Socket通道通信
└── selector/                  # Selector多路复用
    ├── SelectorMultiplexer.java         # 选择器多路复用
    └── NonBlockingServer.java           # 非阻塞服务器
```

### 1.2 各类详细结构

#### 1.2.1 BufferStateManager.java

**JDK对应代码**: `java.nio.Buffer` / `java.nio.ByteBuffer`

**类结构**:
```java
public class BufferStateManager {
    // 演示方法
    public static void demonstrateBufferStates()     // Buffer状态管理演示
    public static void demonstrateCompact()          // Compact操作演示
    public static void demonstrateMarkReset()        // Mark/Reset演示
    
    // 工具方法
    private static void printBufferState(ByteBuffer) // 打印Buffer状态
}
```

**Buffer核心属性**:
- `capacity`: 容量，Buffer最大存储容量，创建后不可变
- `limit`: 限制，第一个不能读写的位置
- `position`: 位置，下一个要读写的位置
- `mark`: 标记，用于reset()恢复位置

**JDK类对应关系**:
| 本类方法 | 对应JDK类/方法 | 说明 |
|----------|----------------|------|
| `ByteBuffer` | `java.nio.ByteBuffer` | 字节缓冲区 |
| `Buffer` | `java.nio.Buffer` | 缓冲区基类 |
| `allocate()` | `ByteBuffer.allocate()` | 分配堆缓冲区 |
| `flip()` | `Buffer.flip()` | 切换读模式 |
| `clear()` | `Buffer.clear()` | 清空缓冲区 |
| `rewind()` | `Buffer.rewind()` | 重绕位置 |
| `compact()` | `ByteBuffer.compact()` | 压缩缓冲区 |
| `mark()` | `Buffer.mark()` | 标记位置 |
| `reset()` | `Buffer.reset()` | 恢复标记 |
| `position()` | `Buffer.position()` | 获取/设置位置 |
| `limit()` | `Buffer.limit()` | 获取/设置限制 |
| `capacity()` | `Buffer.capacity()` | 获取容量 |

**状态转换**:
```
allocate() → position=0, limit=capacity
    ↓
put() → position增加
    ↓
flip() → position=0, limit=原position (切换到读模式)
    ↓
get() → position增加
    ↓
clear() → position=0, limit=capacity (切换到写模式)
```

#### 1.2.2 ByteBufferAllocator.java

**JDK对应代码**: `java.nio.ByteBuffer` / `java.nio.MappedByteBuffer`

**类结构**:
```java
public class ByteBufferAllocator {
    // 分配方法
    public ByteBuffer allocateHeapBuffer(int capacity)      // 分配堆缓冲区
    public ByteBuffer allocateDirectBuffer(int capacity)    // 分配直接缓冲区
    
    // 对比方法
    public void comparePerformance()                        // 性能对比
    public void demonstrateUsage()                          // 使用演示
}
```

**堆缓冲区 vs 直接缓冲区**:

| 特性 | 堆缓冲区 | 直接缓冲区 |
|------|----------|------------|
| 分配位置 | JVM堆内存 | 堆外内存（操作系统） |
| 创建速度 | 快 | 慢 |
| 访问速度 | 慢（需要拷贝到堆外） | 快（直接访问） |
| 内存占用 | 受堆大小限制 | 受物理内存限制 |
| 适用场景 | 小数据量、短生命周期 | 大数据量、长生命周期、IO操作 |

**JDK类对应关系**:
| 本类方法 | 对应JDK类/方法 | 说明 |
|----------|----------------|------|
| `allocateHeapBuffer()` | `ByteBuffer.allocate()` | 分配堆缓冲区 |
| `allocateDirectBuffer()` | `ByteBuffer.allocateDirect()` | 分配直接缓冲区 |
| `isDirect()` | `Buffer.isDirect()` | 是否直接缓冲区 |
| `MappedByteBuffer` | `java.nio.MappedByteBuffer` | 内存映射缓冲区 |
| `IntBuffer` | `java.nio.IntBuffer` | 整数缓冲区 |
| `LongBuffer` | `java.nio.LongBuffer` | 长整数缓冲区 |
| `CharBuffer` | `java.nio.CharBuffer` | 字符缓冲区 |
| `DoubleBuffer` | `java.nio.DoubleBuffer` | 双精度缓冲区 |
| `FloatBuffer` | `java.nio.FloatBuffer` | 浮点缓冲区 |
| `ShortBuffer` | `java.nio.ShortBuffer` | 短整数缓冲区 |

#### 1.2.3 FileChannelTransfer.java

**JDK对应代码**: `java.nio.channels.FileChannel` / `java.nio.MappedByteBuffer`

**类结构**:
```java
public class FileChannelTransfer {
    // 零拷贝传输
    public void zeroCopyTransfer(String source, String dest)    // transferTo零拷贝
    public void transferFrom(String source, String dest)        // transferFrom传输
    
    // 内存映射
    public void memoryMappedCopy(String source, String dest)    // 内存映射拷贝
    public String readWithMappedBuffer(String path)             // 映射读取
    public void writeWithMappedBuffer(String path, String data) // 映射写入
    
    // 分散聚集
    public void scatterRead(String path, ByteBuffer[] buffers)  // 分散读取
    public void gatherWrite(String path, ByteBuffer[] buffers)  // 聚集写入
}
```

**零拷贝原理**:
```
传统IO: 文件 -> 内核缓冲区 -> 用户缓冲区 -> Socket缓冲区 -> 网络
零拷贝:  文件 -> 内核缓冲区 -> Socket缓冲区 -> 网络
         （通过transferTo，数据不经过用户空间）
```

**JDK类对应关系**:
| 本类方法 | 对应JDK类/方法 | 说明 |
|----------|----------------|------|
| `FileChannel` | `java.nio.channels.FileChannel` | 文件通道 |
| `zeroCopyTransfer()` | `FileChannel.transferTo()` | 零拷贝传输 |
| `transferFrom()` | `FileChannel.transferFrom()` | 从通道传输 |
| `map()` | `FileChannel.map()` | 内存映射 |
| `MappedByteBuffer` | `java.nio.MappedByteBuffer` | 映射字节缓冲区 |
| `scatterRead()` | `FileChannel.read(ByteBuffer[])` | 分散读取 |
| `gatherWrite()` | `FileChannel.write(ByteBuffer[])` | 聚集写入 |
| `FileInputStream.getChannel()` | `java.io.FileInputStream` | 获取文件通道 |
| `FileOutputStream.getChannel()` | `java.io.FileOutputStream` | 获取文件通道 |
| `RandomAccessFile.getChannel()` | `java.io.RandomAccessFile` | 获取文件通道 |
| `FileLock` | `java.nio.channels.FileLock` | 文件锁 |

#### 1.2.4 SocketChannelCommunication.java

**JDK对应代码**: `java.nio.channels.SocketChannel` / `java.nio.channels.ServerSocketChannel`

**类结构**:
```java
public class SocketChannelCommunication {
    // 服务端方法
    public ServerSocketChannel createServer(int port)           // 创建服务端
    public SocketChannel acceptConnection(ServerSocketChannel)  // 接受连接
    
    // 客户端方法
    public SocketChannel connectToServer(String host, int port) // 连接服务端
    
    // 通信方法
    public int read(SocketChannel, ByteBuffer)                  // 读取数据
    public int write(SocketChannel, ByteBuffer)                 // 写入数据
    public void closeChannel(SocketChannel)                     // 关闭通道
    
    // 配置方法
    public void configureBlocking(SocketChannel, boolean)       // 配置阻塞模式
}
```

**JDK类对应关系**:
| 本类方法 | 对应JDK类/方法 | 说明 |
|----------|----------------|------|
| `SocketChannel` | `java.nio.channels.SocketChannel` | Socket通道 |
| `ServerSocketChannel` | `java.nio.channels.ServerSocketChannel` | 服务端Socket通道 |
| `open()` | `SocketChannel.open()` | 打开通道 |
| `bind()` | `ServerSocketChannel.bind()` | 绑定地址 |
| `accept()` | `ServerSocketChannel.accept()` | 接受连接 |
| `connect()` | `SocketChannel.connect()` | 连接服务端 |
| `read()` | `SocketChannel.read()` | 读取数据 |
| `write()` | `SocketChannel.write()` | 写入数据 |
| `configureBlocking()` | `SelectableChannel.configureBlocking()` | 配置阻塞模式 |
| `isBlocking()` | `SelectableChannel.isBlocking()` | 是否阻塞模式 |
| `socket()` | `SocketChannel.socket()` | 获取Socket |
| `finishConnect()` | `SocketChannel.finishConnect()` | 完成连接 |
```

#### 1.2.5 SelectorMultiplexer.java

**JDK对应代码**: `java.nio.channels.Selector` / `java.nio.channels.SelectionKey`

**类结构**:
```java
public class SelectorMultiplexer {
    // 字段
    private Selector selector;                     // 选择器
    private volatile boolean running;              // 运行标志
    
    // 核心方法
    public void open()                            // 打开选择器
    public void registerChannel(SelectableChannel, int ops, Object att)  // 注册通道
    public int select()                           // 选择就绪通道
    public int select(long timeout)               // 带超时的选择
    public Set<SelectionKey> selectedKeys()       // 获取已选择键
    public void close()                           // 关闭选择器
}
```

**SelectionKey操作类型**:
- `OP_ACCEPT`: 接受连接（ServerSocketChannel）
- `OP_CONNECT`: 连接就绪（SocketChannel）
- `OP_READ`: 可读
- `OP_WRITE`: 可写

**JDK类对应关系**:
| 本类方法 | 对应JDK类/方法 | 说明 |
|----------|----------------|------|
| `Selector` | `java.nio.channels.Selector` | 选择器 |
| `SelectionKey` | `java.nio.channels.SelectionKey` | 选择键 |
| `open()` | `Selector.open()` | 打开选择器 |
| `register()` | `SelectableChannel.register()` | 注册通道 |
| `select()` | `Selector.select()` | 选择就绪通道 |
| `select(timeout)` | `Selector.select(long)` | 带超时选择 |
| `selectedKeys()` | `Selector.selectedKeys()` | 获取已选择键 |
| `keys()` | `Selector.keys()` | 获取所有键 |
| `wakeup()` | `Selector.wakeup()` | 唤醒选择器 |
| `close()` | `Selector.close()` | 关闭选择器 |
| `OP_ACCEPT` | `SelectionKey.OP_ACCEPT` | 接受连接操作 |
| `OP_CONNECT` | `SelectionKey.OP_CONNECT` | 连接操作 |
| `OP_READ` | `SelectionKey.OP_READ` | 读操作 |
| `OP_WRITE` | `SelectionKey.OP_WRITE` | 写操作 |
| `channel()` | `SelectionKey.channel()` | 获取通道 |
| `isAcceptable()` | `SelectionKey.isAcceptable()` | 是否可接受 |
| `isReadable()` | `SelectionKey.isReadable()` | 是否可读 |
| `isWritable()` | `SelectionKey.isWritable()` | 是否可写 |

#### 1.2.6 NonBlockingServer.java

**JDK对应代码**: `java.nio.channels.Selector` / `java.nio.channels.ServerSocketChannel` / `java.nio.channels.SocketChannel`

**类结构**:
```java
public class NonBlockingServer {
    // 字段
    private Selector selector;                     // 选择器
    private ServerSocketChannel serverChannel;     // 服务端通道
    private ExecutorService executor;              // 线程池
    private volatile boolean running;              // 运行标志
    private int port;                              // 端口
    private Map<SocketChannel, ClientState> clientStates;  // 客户端状态
    
    // 生命周期方法
    public void start()                           // 启动服务器
    public void stop()                            // 停止服务器
    
    // 事件处理方法
    private void accept(SelectionKey)             // 接受连接
    private void read(SelectionKey)               // 读取数据
    private void write(SelectionKey)              // 写入数据
    private void closeChannel(SelectionKey)       // 关闭通道
}
```

**Reactor模式**:
```
单线程Reactor: Selector线程处理所有IO事件
    ↓
多线程Reactor: Selector线程处理accept，工作线程池处理读写
```

**JDK类对应关系**:
| 本类方法 | 对应JDK类/方法 | 说明 |
|----------|----------------|------|
| `Selector` | `java.nio.channels.Selector` | 选择器 |
| `ServerSocketChannel` | `java.nio.channels.ServerSocketChannel` | 服务端通道 |
| `SocketChannel` | `java.nio.channels.SocketChannel` | 客户端通道 |
| `SelectionKey` | `java.nio.channels.SelectionKey` | 选择键 |
| `ByteBuffer` | `java.nio.ByteBuffer` | 字节缓冲区 |
| `open()` | `Selector.open()` / `ServerSocketChannel.open()` | 打开通道/选择器 |
| `bind()` | `ServerSocketChannel.bind()` | 绑定端口 |
| `register()` | `SelectableChannel.register()` | 注册到选择器 |
| `select()` | `Selector.select()` | 选择就绪通道 |
| `selectedKeys()` | `Selector.selectedKeys()` | 获取已选择键 |
| `accept()` | `ServerSocketChannel.accept()` | 接受连接 |
| `read()` | `SocketChannel.read()` | 读取数据 |
| `write()` | `SocketChannel.write()` | 写入数据 |
| `close()` | `Channel.close()` | 关闭通道 |
| `isOpen()` | `Channel.isOpen()` | 是否打开 |
| `configureBlocking()` | `SelectableChannel.configureBlocking(false)` | 非阻塞模式 |

---

## 二、代码使用场景

### 2.1 BufferStateManager - Buffer状态管理

**适用场景**:
- 理解Buffer的工作原理
- 掌握Buffer状态转换
- 实现数据的读写切换

**使用示例**:
```java
// 创建Buffer
ByteBuffer buffer = ByteBuffer.allocate(1024);

// 写入数据（写模式）
buffer.put("Hello World".getBytes());

// 切换到读模式
buffer.flip();

// 读取数据
byte[] data = new byte[buffer.remaining()];
buffer.get(data);
System.out.println(new String(data));  // Hello World

// 清空Buffer，准备下次写入
buffer.clear();
```

**状态转换最佳实践**:
| 操作 | 方法 | position | limit | 说明 |
|------|------|----------|-------|------|
| 初始 | allocate | 0 | capacity | 准备写入 |
| 写入 | put | 增加 | 不变 | 写入数据 |
| 切换读 | flip | 0 | 原position | 准备读取 |
| 读取 | get | 增加 | 不变 | 读取数据 |
| 重读 | rewind | 0 | 不变 | 重新读取 |
| 清空 | clear | 0 | capacity | 准备写入 |
| 压缩 | compact | 原limit-position | capacity | 保留未读数据 |

### 2.2 ByteBufferAllocator - Buffer分配

**适用场景**:
- 需要选择堆缓冲区或直接缓冲区
- 大量IO操作时使用直接缓冲区
- 小数据量临时存储使用堆缓冲区

**使用示例**:
```java
ByteBufferAllocator allocator = new ByteBufferAllocator();

// 分配堆缓冲区（小数据量）
ByteBuffer heapBuffer = allocator.allocateHeapBuffer(1024);

// 分配直接缓冲区（大数据量、IO操作）
ByteBuffer directBuffer = allocator.allocateDirectBuffer(1024);

// 性能对比
allocator.comparePerformance();
```

**选择建议**:
- 文件IO、网络IO → 直接缓冲区
- 小数据量临时存储 → 堆缓冲区
- 需要频繁创建销毁 → 堆缓冲区
- 长生命周期大数据 → 直接缓冲区

### 2.3 FileChannelTransfer - 文件通道传输

**适用场景**:
- 大文件传输（零拷贝）
- 需要极高性能的文件操作
- 内存映射文件处理

**使用示例**:
```java
FileChannelTransfer transfer = new FileChannelTransfer();

// 零拷贝文件传输（最高性能）
transfer.zeroCopyTransfer("/path/source.zip", "/path/dest.zip");

// 内存映射文件拷贝（适合超大文件）
transfer.memoryMappedCopy("/path/large.file", "/path/dest.file");

// 分散读取（从多个Buffer读取）
ByteBuffer[] buffers = new ByteBuffer[3];
buffers[0] = ByteBuffer.allocate(100);
buffers[1] = ByteBuffer.allocate(200);
buffers[2] = ByteBuffer.allocate(300);
transfer.scatterRead("/path/file.dat", buffers);
```

**性能对比**:
| 方法 | 适用场景 | 性能 |
|------|----------|------|
| 传统IO | 小文件 | 一般 |
| transferTo/From | 大文件传输 | 高（零拷贝） |
| 内存映射 | 超大文件随机访问 | 最高 |

### 2.4 SocketChannelCommunication - Socket通道通信

**适用场景**:
- 需要非阻塞网络通信
- 高性能网络应用
- 需要与Selector配合

**使用示例**:
```java
SocketChannelCommunication comm = new SocketChannelCommunication();

// 创建服务端
ServerSocketChannel server = comm.createServer(8080);

// 接受连接（非阻塞模式）
comm.configureBlocking(server, false);
SocketChannel client = comm.acceptConnection(server);

// 或者创建客户端
SocketChannel channel = comm.connectToServer("localhost", 8080);

// 读写数据
ByteBuffer buffer = ByteBuffer.allocate(1024);
comm.read(channel, buffer);

buffer.flip();
comm.write(channel, buffer);
```

### 2.5 SelectorMultiplexer - 选择器多路复用

**适用场景**:
- 单线程处理多个连接
- 高性能服务器开发
- 需要同时监控多个通道的IO事件

**使用示例**:
```java
SelectorMultiplexer multiplexer = new SelectorMultiplexer();
multiplexer.open();

// 注册通道
ServerSocketChannel serverChannel = ServerSocketChannel.open();
serverChannel.configureBlocking(false);
serverChannel.socket().bind(new InetSocketAddress(8080));

multiplexer.registerChannel(serverChannel, SelectionKey.OP_ACCEPT, null);

// 事件循环
while (running) {
    int readyChannels = multiplexer.select(1000);
    if (readyChannels == 0) continue;
    
    Set<SelectionKey> selectedKeys = multiplexer.selectedKeys();
    for (SelectionKey key : selectedKeys) {
        if (key.isAcceptable()) {
            // 处理接受连接
        } else if (key.isReadable()) {
            // 处理读取
        } else if (key.isWritable()) {
            // 处理写入
        }
    }
    selectedKeys.clear();
}

multiplexer.close();
```

### 2.6 NonBlockingServer - 非阻塞服务器

**适用场景**:
- 高并发网络服务
- 需要处理大量连接
- 对性能要求极高的场景

**使用示例**:
```java
// 创建并启动非阻塞服务器
NonBlockingServer server = new NonBlockingServer(8080);

// 在独立线程中启动
new Thread(() -> {
    try {
        server.start();
    } catch (IOException e) {
        e.printStackTrace();
    }
}).start();

// 停止服务器
server.stop();
```

**与传统IO对比**:

| 特性 | 传统BIO | NIO |
|------|---------|-----|
| 线程模型 | 一个连接一个线程 | 单线程处理多连接 |
| 阻塞性 | 阻塞 | 非阻塞 |
| 并发能力 | 低（受线程数限制） | 高（单线程可处理数千连接） |
| 适用场景 | 连接数少、短连接 | 连接数多、长连接 |
| 编程复杂度 | 简单 | 复杂 |

---

## 三、测试代码结构

### 3.1 测试包结构

```
src/test/java/com/linsir/abc/core/base/nio/
├── buffer/
│   ├── BufferStateManagerTest.java      # Buffer状态管理测试
│   └── ByteBufferAllocatorTest.java     # ByteBuffer分配测试
├── channel/
│   ├── FileChannelTransferTest.java     # 文件通道传输测试
│   └── SocketChannelCommunicationTest.java # Socket通道通信测试
└── selector/
    ├── SelectorMultiplexerTest.java     # 选择器多路复用测试
    └── NonBlockingServerTest.java       # 非阻塞服务器测试
```

### 3.2 测试类结构示例

#### BufferStateManagerTest.java

```java
public class BufferStateManagerTest {
    // 基础属性测试
    @Test void testByteBufferAllocate()       // 分配Buffer
    @Test void testByteBufferPut()            // 写入数据
    
    // 状态转换测试
    @Test void testByteBufferFlip()           // flip操作
    @Test void testByteBufferGet()            // 读取数据
    @Test void testByteBufferRewind()         // rewind操作
    @Test void testByteBufferClear()          // clear操作
    @Test void testByteBufferCompact()        // compact操作
    
    // mark/reset测试
    @Test void testByteBufferMarkReset()      // mark/reset操作
}
```

#### FileChannelTransferTest.java

```java
public class FileChannelTransferTest {
    @TempDir Path tempDir;
    
    // 传输测试
    @Test void testZeroCopyTransfer()         // 零拷贝传输
    @Test void testTransferFrom()             // transferFrom传输
    
    // 内存映射测试
    @Test void testMemoryMappedCopy()         // 内存映射拷贝
    @Test void testReadWithMappedBuffer()     // 映射读取
    @Test void testWriteWithMappedBuffer()    // 映射写入
    
    // 分散聚集测试
    @Test void testScatterRead()              // 分散读取
    @Test void testGatherWrite()              // 聚集写入
}
```

#### NonBlockingServerTest.java

```java
public class NonBlockingServerTest {
    // 生命周期测试
    @Test void testServerStart()              // 服务器启动
    @Test void testServerStop()               // 服务器停止
    
    // 连接测试
    @Test void testAcceptConnection()         // 接受连接
    @Test void testReadWrite()                // 读写数据
    
    // 并发测试
    @Test void testMultipleClients()          // 多客户端
}
```

---

## 四、单元测试预期结果

### 4.1 BufferStateManagerTest 预期结果

| 测试方法 | 预期结果 | 说明 |
|----------|----------|------|
| `testByteBufferAllocate` | ✅ PASS | capacity=10, position=0, limit=10 |
| `testByteBufferPut` | ✅ PASS | position=5, limit=10 |
| `testByteBufferFlip` | ✅ PASS | position=0, limit=5 |
| `testByteBufferGet` | ✅ PASS | 读取内容正确，position增加 |
| `testByteBufferRewind` | ✅ PASS | position=0, limit不变 |
| `testByteBufferClear` | ✅ PASS | position=0, limit=capacity |
| `testByteBufferCompact` | ✅ PASS | 未读数据移到头部，position=剩余字节数 |
| `testByteBufferMarkReset` | ✅ PASS | reset后position回到mark位置 |

### 4.2 ByteBufferAllocatorTest 预期结果

| 测试方法 | 预期结果 | 说明 |
|----------|----------|------|
| `testAllocateHeapBuffer` | ✅ PASS | 堆缓冲区创建成功，isDirect=false |
| `testAllocateDirectBuffer` | ✅ PASS | 直接缓冲区创建成功，isDirect=true |
| `testHeapBufferReadWrite` | ✅ PASS | 堆缓冲区读写正常 |
| `testDirectBufferReadWrite` | ✅ PASS | 直接缓冲区读写正常 |
| `testDirectBufferPerformance` | ✅ PASS | 直接缓冲区IO性能优于堆缓冲区 |

### 4.3 FileChannelTransferTest 预期结果

| 测试方法 | 预期结果 | 说明 |
|----------|----------|------|
| `testZeroCopyTransfer` | ✅ PASS | 文件传输成功，内容一致 |
| `testTransferFrom` | ✅ PASS | 文件传输成功，内容一致 |
| `testMemoryMappedCopy` | ✅ PASS | 内存映射拷贝成功 |
| `testReadWithMappedBuffer` | ✅ PASS | 映射读取内容正确 |
| `testWriteWithMappedBuffer` | ✅ PASS | 映射写入成功 |
| `testScatterRead` | ✅ PASS | 分散读取数据正确 |
| `testGatherWrite` | ✅ PASS | 聚集写入成功 |

### 4.4 SocketChannelCommunicationTest 预期结果

| 测试方法 | 预期结果 | 说明 |
|----------|----------|------|
| `testCreateServer` | ✅ PASS | 服务端创建成功 |
| `testConnectToServer` | ✅ PASS | 客户端连接成功 |
| `testReadWrite` | ✅ PASS | 数据读写正确 |
| `testNonBlockingMode` | ✅ PASS | 非阻塞模式工作正常 |
| `testCloseChannel` | ✅ PASS | 通道关闭成功 |

### 4.5 SelectorMultiplexerTest 预期结果

| 测试方法 | 预期结果 | 说明 |
|----------|----------|------|
| `testOpenClose` | ✅ PASS | 选择器打开关闭正常 |
| `testRegisterChannel` | ✅ PASS | 通道注册成功 |
| `testSelect` | ✅ PASS | 选择操作正常 |
| `testSelectWithTimeout` | ✅ PASS | 带超时选择正常 |
| `testSelectedKeys` | ✅ PASS | 已选择键集合正确 |

### 4.6 NonBlockingServerTest 预期结果

| 测试方法 | 预期结果 | 说明 |
|----------|----------|------|
| `testServerStart` | ✅ PASS | 服务器启动成功 |
| `testServerStop` | ✅ PASS | 服务器停止成功 |
| `testAcceptConnection` | ✅ PASS | 接受连接成功 |
| `testReadWrite` | ✅ PASS | 读写数据正确 |
| `testMultipleClients` | ✅ PASS | 多客户端处理正常 |

### 4.7 所有测试汇总

| 测试类 | 测试数 | 预期通过率 |
|--------|--------|------------|
| BufferStateManagerTest | 8 | 100% |
| ByteBufferAllocatorTest | 5 | 100% |
| FileChannelTransferTest | 7 | 100% |
| SocketChannelCommunicationTest | 5 | 100% |
| SelectorMultiplexerTest | 5 | 100% |
| NonBlockingServerTest | 5 | 100% |
| **总计** | **35** | **100%** |

---

## 五、运行测试

### 5.1 运行所有nio包测试

```bash
mvn test -Dtest="com.linsir.abc.core.base.nio.**"
```

### 5.2 运行单个测试类

```bash
mvn test -Dtest=BufferStateManagerTest
```

### 5.3 预期输出

```
Tests run: 35, Failures: 0, Errors: 0, Skipped: 0
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.linsir.abc.core.base.nio.buffer.BufferStateManagerTest
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
...
[INFO] BUILD SUCCESS
```

---

## 六、NIO选择指南

### 6.1 按功能选择

| 功能需求 | 推荐类 | 说明 |
|----------|--------|------|
| Buffer操作 | BufferStateManager | 状态管理、读写切换 |
| Buffer分配 | ByteBufferAllocator | 堆/直接缓冲区选择 |
| 文件传输 | FileChannelTransfer | 零拷贝、内存映射 |
| 网络通信 | SocketChannelCommunication | 非阻塞Socket |
| 多路复用 | SelectorMultiplexer | 单线程多连接 |
| 完整服务器 | NonBlockingServer | Reactor模式实现 |

### 6.2 IO模型选择

| 模型 | 适用场景 | 推荐实现 |
|------|----------|----------|
| BIO | 连接数少、短连接 | 传统Socket |
| NIO | 连接数多、长连接 | Selector + Channel |
| AIO | 异步IO、高并发 | AsynchronousChannel |

### 6.3 注意事项

1. **Buffer状态管理**: 读写切换时必须调用flip()或clear()
2. **直接缓冲区**: 使用完毕后建议手动clean()释放堆外内存
3. **Selector选择**: select()可能返回0，需要循环处理
4. **Channel关闭**: 关闭Selector会自动关闭所有注册的Channel
5. **零拷贝限制**: transferTo/From有2GB大小限制

---

**文档版本**: 1.0.0  
**最后更新**: 2026-03-26
