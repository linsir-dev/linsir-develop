# java.nio 包面试题汇总

## 目录

1. [NIO 基础概念](#一nio-基础概念)
2. [Buffer 缓冲区](#二buffer-缓冲区)
3. [Channel 通道](#三channel-通道)
4. [Selector 选择器](#四selector-选择器)
5. [零拷贝与内存映射](#五零拷贝与内存映射)
6. [NIO 实战与优化](#六nio-实战与优化)

---

## 一、NIO 基础概念

### 1.1 什么是 NIO？

**问题**: 什么是 Java NIO？与传统 IO 有什么区别？

**答案**:

**NIO（New IO / Non-blocking IO）**:
- Java 1.4 引入的新的 IO API
- 位于 `java.nio` 包中
- 支持**非阻塞**IO 操作
- 提供**缓冲区（Buffer）**、**通道（Channel）**、**选择器（Selector）**三大核心组件

**NIO vs 传统 IO 对比**:

| 特性 | 传统 IO | NIO |
|------|---------|-----|
| **数据处理方式** | 流式（Stream） | 块式（Block） |
| **操作单位** | 字节/字符流 | Buffer（缓冲区） |
| **IO 模式** | 阻塞式 | 支持非阻塞式 |
| **线程模型** | 一连接一线程 | 一线程多连接（Selector） |
| **性能** | 一般 | 高并发场景更优 |
| **复杂度** | 简单 | 较复杂 |
| **适用场景** | 简单文件操作 | 高并发网络编程 |

**核心组件**:
```
NIO
├── Buffer（缓冲区）- 数据容器
├── Channel（通道）- 数据传输
└── Selector（选择器）- 多路复用
```

---

### 1.2 为什么使用 NIO？

**问题**: 什么场景下应该使用 NIO？有什么优势？

**答案**:

**使用 NIO 的场景**:

1. **高并发网络服务器**
   - 需要同时处理数万连接
   - 使用 Selector 实现单线程多路复用
   - 例：聊天服务器、游戏服务器、消息队列

2. **大文件操作**
   - 内存映射文件（MappedByteBuffer）
   - 直接操作内存，绕过系统缓冲区
   - 适合处理 GB 级大文件

3. **性能敏感场景**
   - 需要精细控制缓冲区
   - 减少数据拷贝次数（零拷贝）

**NIO 的优势**:

| 优势 | 说明 |
|------|------|
| **非阻塞** | 线程不会被 IO 操作阻塞，可处理其他任务 |
| **多路复用** | 一个线程管理多个连接，减少线程开销 |
| **零拷贝** | 减少内核态和用户态之间的数据拷贝 |
| **内存映射** | 大文件处理更高效 |
| **直接缓冲区** | 绕过 JVM 堆，减少 GC 影响 |

---

### 1.3 NIO 的核心组件

**问题**: NIO 的三大核心组件是什么？它们之间的关系是什么？

**答案**:

**三大核心组件**:

1. **Buffer（缓冲区）**
   - 数据的**容器**
   - 所有数据都通过 Buffer 读写
   - 支持双向操作（读/写）

2. **Channel（通道）**
   - 数据的**传输通道**
   - 双向通信（可读可写）
   - 必须配合 Buffer 使用

3. **Selector（选择器）**
   - **多路复用器**
   - 一个线程管理多个 Channel
   - 基于事件驱动模型

**关系图**:
```
        Selector
           │
    ┌──────┼──────┐
    │      │      │
Channel Channel Channel
    │      │      │
    └──────┼──────┘
           │
         Buffer
           │
        数据
```

**工作流程**:
```java
// 1. 创建 Buffer
ByteBuffer buffer = ByteBuffer.allocate(1024);

// 2. 打开 Channel
FileChannel channel = new FileInputStream("file.txt").getChannel();

// 3. 读取数据到 Buffer
channel.read(buffer);

// 4. 切换为读模式
buffer.flip();

// 5. 从 Buffer 读取数据
while (buffer.hasRemaining()) {
    byte b = buffer.get();
}
```

---

## 二、Buffer 缓冲区

### 2.1 Buffer 的四个核心属性

**问题**: Buffer 的四个核心属性是什么？它们的作用是什么？

**答案**:

**四个核心属性**:

| 属性 | 说明 | 初始值 | 变化规则 |
|------|------|--------|----------|
| **capacity** | 缓冲区容量，最大存储数据量 | 创建时指定 | 固定不变 |
| **position** | 当前操作位置 | 0 | 写模式：下一个写入位置；读模式：下一个读取位置 |
| **limit** | 可操作数据的上限 | capacity | 写模式：等于 capacity；读模式：等于最后写入位置 |
| **mark** | 标记位置，用于 reset | -1 | 调用 mark() 时记录 position |

**关系**: `0 <= mark <= position <= limit <= capacity`

**状态变化示例**:
```
初始状态（clear后）:
[   空缓冲区   ]
0            capacity
↑
position=0, limit=capacity

写入3个字节后:
[abc          ]
   ↑         
  position=3, limit=capacity

flip后（切换读模式）:
[abc          ]
0  ↑        ↑
   limit=3  capacity
   position=0

读取2个字节后:
[abc          ]
0    ↑      ↑
     limit=3
     position=2
```

---

### 2.2 Buffer 的核心方法

**问题**: Buffer 的核心方法有哪些？flip()、clear()、rewind() 的区别是什么？

**答案**:

**核心方法**:

| 方法 | 作用 | position | limit |
|------|------|----------|-------|
| `flip()` | 切换为读模式 | 设为 0 | 设为当前 position |
| `clear()` | 清空缓冲区（写模式） | 设为 0 | 设为 capacity |
| `rewind()` | 重读数据 | 设为 0 | 不变 |
| `compact()` | 压缩缓冲区 | 设为剩余数据长度 | 设为 capacity |
| `mark()` | 标记当前 position | 不变 | 不变 |
| `reset()` | 恢复到 mark 位置 | 设为 mark | 不变 |

**方法详解**:

```java
ByteBuffer buffer = ByteBuffer.allocate(10);

// 1. clear() - 准备写入
buffer.clear();
// position=0, limit=10

buffer.put("Hello".getBytes());
// position=5, limit=10

// 2. flip() - 切换为读模式
buffer.flip();
// position=0, limit=5

// 读取数据
byte[] data = new byte[buffer.remaining()];
buffer.get(data);
// position=5, limit=5

// 3. rewind() - 重新读取
buffer.rewind();
// position=0, limit=5 (不变)

// 4. clear() - 清空，准备再次写入
buffer.clear();
// position=0, limit=10 (数据未被清除，只是重置指针)

// 5. compact() - 压缩（保留未读数据，准备写入）
buffer.put("Hello".getBytes());
buffer.flip();
buffer.get(); // 读取 'H'
buffer.get(); // 读取 'e'
// position=2, limit=5

buffer.compact();
// 将 'llo' 移到开头
// position=3, limit=10
```

**使用场景**:
- `flip()`: 写入完成后，准备读取
- `clear()`: 读取完成后，准备重新写入
- `rewind()`: 需要重新读取同一数据
- `compact()`: 部分读取后，继续写入

---

### 2.3 ByteBuffer 的创建方式

**问题**: `ByteBuffer` 有哪些创建方式？`allocate()` 和 `allocateDirect()` 有什么区别？

**答案**:

**创建方式**:

```java
// 1. 堆缓冲区（Heap Buffer）
ByteBuffer heapBuffer = ByteBuffer.allocate(1024);

// 2. 直接缓冲区（Direct Buffer）
ByteBuffer directBuffer = ByteBuffer.allocateDirect(1024);

// 3. 包装现有数组
byte[] array = new byte[1024];
ByteBuffer wrappedBuffer = ByteBuffer.wrap(array);

// 4. 包装数组的一部分
ByteBuffer wrappedOffset = ByteBuffer.wrap(array, 100, 200);
```

**allocate() vs allocateDirect() 对比**:

| 特性 | allocate()（堆缓冲区） | allocateDirect()（直接缓冲区） |
|------|------------------------|-------------------------------|
| **存储位置** | JVM 堆内存 | 堆外内存（操作系统内存） |
| **创建速度** | 快 | 慢（需要向 OS 申请） |
| **读写速度** | 慢（需要拷贝到堆外） | 快（直接操作） |
| **内存占用** | 受 JVM 堆限制 | 受物理内存限制 |
| **GC 影响** | 受 GC 管理 | 不受 GC 直接影响 |
| **适用场景** | 小数据、短生命周期 | 大数据、长生命周期、频繁 IO |

**数据流向对比**:

```
堆缓冲区（Heap Buffer）:
磁盘/网络 → 系统缓冲区 → 堆外内存 → 拷贝 → JVM 堆（ByteBuffer）
                              ↑
                         需要一次拷贝

直接缓冲区（Direct Buffer）:
磁盘/网络 → 系统缓冲区 → 堆外内存（ByteBuffer）
                    ↑
               无需拷贝，直接操作
```

**选择建议**:
- 数据量小（< 1KB）→ 使用堆缓冲区
- 数据量大、频繁 IO → 使用直接缓冲区
- 大文件传输、网络通信 → 使用直接缓冲区

---

### 2.4 各种 Buffer 类型

**问题**: NIO 提供了哪些类型的 Buffer？

**答案**:

**Buffer 类型**（对应 Java 基本数据类型）:

| Buffer 类型 | 数据类型 | 说明 |
|-------------|----------|------|
| `ByteBuffer` | byte | 最常用，可映射其他类型 |
| `CharBuffer` | char | 字符数据 |
| `ShortBuffer` | short | 短整数 |
| `IntBuffer` | int | 整数 |
| `LongBuffer` | long | 长整数 |
| `FloatBuffer` | float | 单精度浮点 |
| `DoubleBuffer` | double | 双精度浮点 |

**ByteBuffer 的视图转换**:
```java
ByteBuffer byteBuffer = ByteBuffer.allocate(100);

// 转换为 CharBuffer（2字节一个字符）
CharBuffer charBuffer = byteBuffer.asCharBuffer();

// 转换为 IntBuffer（4字节一个整数）
IntBuffer intBuffer = byteBuffer.asIntBuffer();

// 转换为 LongBuffer（8字节一个长整数）
LongBuffer longBuffer = byteBuffer.asLongBuffer();

// 转换为 FloatBuffer
FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();

// 转换为 DoubleBuffer
DoubleBuffer doubleBuffer = byteBuffer.asDoubleBuffer();
```

**使用示例**:
```java
ByteBuffer buffer = ByteBuffer.allocate(100);

// 写入不同类型的数据
buffer.putInt(100);      // 4字节
buffer.putLong(1000L);   // 8字节
buffer.putDouble(3.14);  // 8字节
buffer.put("Hello".getBytes());

// 读取
buffer.flip();
int i = buffer.getInt();
long l = buffer.getLong();
double d = buffer.getDouble();
```

---

## 三、Channel 通道

### 3.1 什么是 Channel？

**问题**: 什么是 Channel？与传统 IO 的 Stream 有什么区别？

**答案**:

**Channel（通道）**:
- 表示与实体（文件、Socket 等）的**开放连接**
- 用于**读写数据**
- 必须配合 **Buffer** 使用

**Channel vs Stream 对比**:

| 特性 | Stream | Channel |
|------|--------|---------|
| **方向** | 单向（Input/Output） | 双向（可读可写） |
| **异步** | 不支持 | 支持 |
| **数据载体** | 直接读写 | 必须通过 Buffer |
| **阻塞** | 阻塞 | 可配置为非阻塞 |

**Channel 类型**:

```
Channel
├── FileChannel          # 文件通道
├── SocketChannel        # TCP 客户端通道
├── ServerSocketChannel  # TCP 服务端通道
├── DatagramChannel      # UDP 通道
└── Pipe.SinkChannel     # 管道（线程间通信）
    Pipe.SourceChannel
```

**基本使用**:
```java
// 文件通道
FileChannel fileChannel = new FileInputStream("file.txt").getChannel();

// Socket 通道
SocketChannel socketChannel = SocketChannel.open();
socketChannel.connect(new InetSocketAddress("localhost", 8080));

// 必须配合 Buffer 使用
ByteBuffer buffer = ByteBuffer.allocate(1024);
fileChannel.read(buffer);  // 读取到 Buffer
fileChannel.write(buffer); // 从 Buffer 写入
```

---

### 3.2 FileChannel

**问题**: `FileChannel` 的特点是什么？如何使用？

**答案**:

**FileChannel 特点**:
- 用于**文件读写**
- **双向**通道（可读可写）
- 支持**内存映射文件**
- 支持**零拷贝**传输
- **线程安全**（多个线程可并发读写）

**创建方式**:
```java
// 方式1: 通过 FileInputStream/FileOutputStream
FileChannel readChannel = new FileInputStream("file.txt").getChannel();
FileChannel writeChannel = new FileOutputStream("out.txt").getChannel();

// 方式2: 通过 RandomAccessFile（读写模式）
FileChannel rwChannel = new RandomAccessFile("file.txt", "rw").getChannel();

// 方式3: 通过 FileChannel.open()
FileChannel channel = FileChannel.open(
    Paths.get("file.txt"),
    StandardOpenOption.READ,
    StandardOpenOption.WRITE
);
```

**基本操作**:
```java
// 读取文件
FileChannel channel = new FileInputStream("file.txt").getChannel();
ByteBuffer buffer = ByteBuffer.allocate(1024);
int bytesRead = channel.read(buffer);

// 写入文件
FileChannel outChannel = new FileOutputStream("out.txt").getChannel();
buffer.flip();
outChannel.write(buffer);

// 关闭
channel.close();
```

**核心方法**:

| 方法 | 说明 |
|------|------|
| `read(ByteBuffer)` | 从通道读取数据到 Buffer |
| `write(ByteBuffer)` | 从 Buffer 写入数据到通道 |
| `position()` | 获取/设置当前位置 |
| `size()` | 获取文件大小 |
| `truncate(long)` | 截断文件 |
| `force(boolean)` | 强制刷新到磁盘 |
| `transferTo/transferFrom` | 零拷贝传输 |
| `map()` | 内存映射文件 |

---

### 3.3 SocketChannel 和 ServerSocketChannel

**问题**: `SocketChannel` 和 `ServerSocketChannel` 的作用是什么？如何使用？

**答案**:

**SocketChannel**:
- TCP **客户端**通道
- 支持**非阻塞**模式
- 双向数据传输

**ServerSocketChannel**:
- TCP **服务端**通道
- 监听客户端连接
- `accept()` 返回 `SocketChannel`

**阻塞模式示例**:
```java
// 服务端
ServerSocketChannel serverChannel = ServerSocketChannel.open();
serverChannel.bind(new InetSocketAddress(8080));

while (true) {
    SocketChannel clientChannel = serverChannel.accept(); // 阻塞
    handleClient(clientChannel);
}

// 客户端
SocketChannel socketChannel = SocketChannel.open();
socketChannel.connect(new InetSocketAddress("localhost", 8080)); // 阻塞

ByteBuffer buffer = ByteBuffer.allocate(1024);
socketChannel.read(buffer);  // 阻塞
```

**非阻塞模式示例**:
```java
// 服务端
ServerSocketChannel serverChannel = ServerSocketChannel.open();
serverChannel.bind(new InetSocketAddress(8080));
serverChannel.configureBlocking(false); // 非阻塞

Selector selector = Selector.open();
serverChannel.register(selector, SelectionKey.OP_ACCEPT);

while (true) {
    selector.select(); // 阻塞等待就绪事件
    
    for (SelectionKey key : selector.selectedKeys()) {
        if (key.isAcceptable()) {
            SocketChannel client = serverChannel.accept();
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);
        }
    }
}

// 客户端
SocketChannel socketChannel = SocketChannel.open();
socketChannel.configureBlocking(false);
socketChannel.connect(new InetSocketAddress("localhost", 8080));

// 非阻塞连接需要轮询
while (!socketChannel.finishConnect()) {
    // 可以做其他事情
}
```

---

### 3.4 DatagramChannel

**问题**: `DatagramChannel` 的作用是什么？与 UDP 有什么关系？

**答案**:

**DatagramChannel**:
- 用于 **UDP** 协议通信
- **无连接**的通道
- 支持**非阻塞**模式
- 使用 `send()` 和 `receive()` 方法

**与 UDP 的关系**:
- UDP 是**无连接**的传输协议
- `DatagramChannel` 是 UDP 的 Java NIO 实现
- 不保证数据到达、不保证顺序

**使用示例**:
```java
// 服务端
DatagramChannel serverChannel = DatagramChannel.open();
serverChannel.bind(new InetSocketAddress(8080));

ByteBuffer buffer = ByteBuffer.allocate(1024);
SocketAddress clientAddress = serverChannel.receive(buffer); // 接收数据

// 发送响应
buffer.flip();
serverChannel.send(buffer, clientAddress);

// 客户端
DatagramChannel clientChannel = DatagramChannel.open();

ByteBuffer buffer = ByteBuffer.wrap("Hello".getBytes());
clientChannel.send(buffer, new InetSocketAddress("localhost", 8080));

// 接收响应
buffer.clear();
clientChannel.receive(buffer);
```

---

## 四、Selector 选择器

### 4.1 什么是 Selector？

**问题**: 什么是 Selector？它的作用是什么？

**答案**:

**Selector（选择器）**:
- NIO 的**多路复用器**
- 一个线程可以管理**多个 Channel**
- 基于**事件驱动**模型

**作用**:
- **减少线程数**: 一个线程处理多个连接
- **提高性能**: 避免线程切换开销
- **非阻塞**: 只在有数据就绪时才处理

**工作原理**:
```
Selector
    │
    ├── 注册 Channel（关注特定事件）
    │
    ├── select() 阻塞等待事件
    │
    └── 返回就绪的 SelectionKey 集合
            │
            ├── OP_ACCEPT  → 有新连接
            ├── OP_CONNECT → 连接建立
            ├── OP_READ    → 可读
            └── OP_WRITE   → 可写
```

**代码示例**:
```java
// 1. 创建 Selector
Selector selector = Selector.open();

// 2. 创建 Channel 并注册
ServerSocketChannel serverChannel = ServerSocketChannel.open();
serverChannel.bind(new InetSocketAddress(8080));
serverChannel.configureBlocking(false);

// 注册到 Selector，关注 ACCEPT 事件
serverChannel.register(selector, SelectionKey.OP_ACCEPT);

// 3. 事件循环
while (true) {
    // 阻塞等待就绪事件
    int readyCount = selector.select();
    
    // 获取就绪的 SelectionKey
    Set<SelectionKey> readyKeys = selector.selectedKeys();
    Iterator<SelectionKey> iterator = readyKeys.iterator();
    
    while (iterator.hasNext()) {
        SelectionKey key = iterator.next();
        
        if (key.isAcceptable()) {
            // 处理新连接
            handleAccept(key);
        } else if (key.isReadable()) {
            // 处理读事件
            handleRead(key);
        } else if (key.isWritable()) {
            // 处理写事件
            handleWrite(key);
        }
        
        iterator.remove(); // 必须移除
    }
}
```

---

### 4.2 SelectionKey 和事件类型

**问题**: `SelectionKey` 是什么？有哪些事件类型？

**答案**:

**SelectionKey**:
- 表示 **Channel 在 Selector 中的注册信息**
- 封装了 Channel、Selector、感兴趣的事件
- 用于判断就绪事件类型

**事件类型**（位掩码）:

| 事件 | 值 | 说明 | 适用 Channel |
|------|-----|------|--------------|
| `OP_ACCEPT` | 16 | 接受连接 | ServerSocketChannel |
| `OP_CONNECT` | 8 | 连接建立 | SocketChannel |
| `OP_READ` | 1 | 可读 | SocketChannel、DatagramChannel |
| `OP_WRITE` | 4 | 可写 | SocketChannel、DatagramChannel |

**SelectionKey 方法**:

| 方法 | 说明 |
|------|------|
| `channel()` | 获取关联的 Channel |
| `selector()` | 获取关联的 Selector |
| `isAcceptable()` | 是否可接受连接 |
| `isConnectable()` | 是否连接就绪 |
| `isReadable()` | 是否可读 |
| `isWritable()` | 是否可写 |
| `interestOps()` | 获取/设置感兴趣的事件 |
| `readyOps()` | 获取就绪的事件 |
| `attach(Object)` | 附加对象（如 Session） |
| `attachment()` | 获取附加对象 |

**代码示例**:
```java
// 注册多个事件
int interestSet = SelectionKey.OP_READ | SelectionKey.OP_WRITE;
channel.register(selector, interestSet);

// 获取和修改感兴趣的事件
SelectionKey key = channel.keyFor(selector);
int currentOps = key.interestOps();
key.interestOps(currentOps | SelectionKey.OP_WRITE); // 添加写事件

// 附加对象
key.attach(new ClientSession());
ClientSession session = (ClientSession) key.attachment();
```

---

### 4.3 完整的 NIO 服务器示例

**问题**: 请编写一个完整的基于 NIO 的服务器示例。

**答案**:

```java
public class NIOServer {
    private Selector selector;
    private ServerSocketChannel serverChannel;
    
    public void start(int port) throws IOException {
        // 1. 创建 Selector
        selector = Selector.open();
        
        // 2. 创建服务端 Channel
        serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(port));
        serverChannel.configureBlocking(false);
        
        // 3. 注册到 Selector
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        
        System.out.println("NIO 服务器启动，端口: " + port);
        
        // 4. 事件循环
        while (true) {
            // 阻塞等待就绪事件
            selector.select();
            
            // 处理就绪的 Key
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
            
            while (keys.hasNext()) {
                SelectionKey key = keys.next();
                keys.remove(); // 必须移除
                
                try {
                    if (key.isAcceptable()) {
                        handleAccept(key);
                    } else if (key.isReadable()) {
                        handleRead(key);
                    } else if (key.isWritable()) {
                        handleWrite(key);
                    }
                } catch (IOException e) {
                    key.cancel();
                    key.channel().close();
                }
            }
        }
    }
    
    private void handleAccept(SelectionKey key) throws IOException {
        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        SocketChannel client = server.accept();
        client.configureBlocking(false);
        
        // 注册读事件
        client.register(selector, SelectionKey.OP_READ);
        
        System.out.println("客户端连接: " + client.getRemoteAddress());
    }
    
    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        
        int bytesRead = channel.read(buffer);
        if (bytesRead == -1) {
            // 连接关闭
            key.cancel();
            channel.close();
            return;
        }
        
        buffer.flip();
        byte[] data = new byte[buffer.remaining()];
        buffer.get(data);
        String message = new String(data);
        
        System.out.println("收到: " + message);
        
        // 注册写事件，发送响应
        key.interestOps(SelectionKey.OP_WRITE);
        key.attach("Echo: " + message);
    }
    
    private void handleWrite(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        String response = (String) key.attachment();
        
        ByteBuffer buffer = ByteBuffer.wrap(response.getBytes());
        channel.write(buffer);
        
        // 写完后注册读事件
        key.interestOps(SelectionKey.OP_READ);
        key.attach(null);
    }
    
    public static void main(String[] args) throws IOException {
        new NIOServer().start(8080);
    }
}
```

---

## 五、零拷贝与内存映射

### 5.1 什么是零拷贝？

**问题**: 什么是零拷贝（Zero-Copy）？NIO 如何实现零拷贝？

**答案**:

**零拷贝**:
- 减少数据在**内核态**和**用户态**之间的拷贝次数
- 直接从内核缓冲区传输到目标（网络/文件）
- 减少 CPU 消耗和内存带宽占用

**传统 IO 的数据拷贝**（4次拷贝，4次上下文切换）:
```
磁盘 → 内核缓冲区 → 用户缓冲区 → Socket 缓冲区 → 网络
       ↑                ↑
    上下文切换       上下文切换
```

**零拷贝的数据传输**（2次拷贝，2次上下文切换）:
```
磁盘 → 内核缓冲区 → Socket 缓冲区 → 网络
       ↑
    上下文切换
```

**NIO 零拷贝方法**:

| 方法 | 说明 |
|------|------|
| `FileChannel.transferTo()` | 文件发送到 Socket |
| `FileChannel.transferFrom()` | 从 Socket/文件接收 |

**代码示例**:
```java
// 传统方式（需要用户缓冲区）
FileInputStream fis = new FileInputStream("large.zip");
Socket socket = new Socket("localhost", 8080);
byte[] buffer = new byte[8192];
int len;
while ((len = fis.read(buffer)) != -1) {
    socket.getOutputStream().write(buffer, 0, len);
}

// 零拷贝方式
FileChannel sourceChannel = new FileInputStream("large.zip").getChannel();
SocketChannel destChannel = SocketChannel.open(
    new InetSocketAddress("localhost", 8080));

// 直接传输，无需用户缓冲区
sourceChannel.transferTo(0, sourceChannel.size(), destChannel);
```

**适用场景**:
- 大文件传输
- 静态文件服务器
- 消息队列（Kafka 使用零拷贝）

---

### 5.2 内存映射文件（MappedByteBuffer）

**问题**: 什么是内存映射文件？有什么优势？

**答案**:

**内存映射文件（Memory-Mapped File）**:
- 将文件直接映射到**虚拟内存**
- 通过内存操作来读写文件
- 由操作系统管理数据的加载和回写

**优势**:

| 优势 | 说明 |
|------|------|
| **速度快** | 直接内存操作，避免系统调用 |
| **大文件处理** | 可处理超过物理内存的文件 |
| **延迟加载** | 按需加载，减少内存占用 |
| **共享内存** | 多个进程可映射同一文件 |

**使用方式**:
```java
FileChannel channel = new RandomAccessFile("large.bin", "rw").getChannel();

// 将文件映射到内存
MappedByteBuffer buffer = channel.map(
    FileChannel.MapMode.READ_WRITE,  // 映射模式
    0,                               // 起始位置
    channel.size()                   // 映射大小
);

// 直接读写内存
buffer.putInt(100);
buffer.putLong(1000L);
buffer.put("Hello".getBytes());

// 强制刷新到磁盘
buffer.force();

channel.close();
```

**映射模式**:

| 模式 | 说明 |
|------|------|
| `READ_ONLY` | 只读映射 |
| `READ_WRITE` | 读写映射 |
| `PRIVATE` | 写时复制（Copy-on-Write） |

**注意事项**:
- 映射的文件不会立即加载到内存
- 由操作系统按需分页加载
- 大文件映射不会耗尽内存

---

## 六、NIO 实战与优化

### 6.1 NIO 的优缺点

**问题**: NIO 有什么优缺点？什么场景不适合使用 NIO？

**答案**:

**优点**:

| 优点 | 说明 |
|------|------|
| **高并发** | 单线程处理数万连接 |
| **非阻塞** | 线程不会被 IO 阻塞 |
| **零拷贝** | 减少数据拷贝，提升性能 |
| **内存映射** | 高效处理大文件 |
| **直接缓冲区** | 减少 GC 影响 |

**缺点**:

| 缺点 | 说明 |
|------|------|
| **复杂度高** | 编程模型复杂，容易出错 |
| **Bug 多** | 早期版本有 epoll bug（JDK 1.4-1.6） |
| **调试困难** | 异步逻辑难以调试 |
| **粘包处理** | 需要手动处理半包、粘包 |
| **内存泄漏** | 直接缓冲区不受 GC 管理，容易泄漏 |

**不适合 NIO 的场景**:
- 简单的文件读写 → 使用传统 IO
- 连接数少的应用 → 使用传统 IO 或 AIO
- 对延迟不敏感的应用 → 使用传统 IO
- 团队不熟悉 NIO → 使用 Netty 框架

---

### 6.2 NIO 的粘包和拆包处理

**问题**: NIO 中如何处理粘包和拆包问题？

**答案**:

**粘包/拆包原因**:
- TCP 是**流式协议**，不保留消息边界
- NIO 非阻塞模式下，一次可能读取不完整数据

**解决方案**:

**1. 固定长度**:
```java
// 每个消息固定 100 字节
ByteBuffer buffer = ByteBuffer.allocate(100);

// 确保读取完整消息
while (buffer.hasRemaining()) {
    int read = channel.read(buffer);
    if (read == -1) break;
}
```

**2. 分隔符**:
```java
// 使用换行符分隔
ByteBuffer buffer = ByteBuffer.allocate(1024);
channel.read(buffer);

buffer.flip();
StringBuilder message = new StringBuilder();
while (buffer.hasRemaining()) {
    char c = (char) buffer.get();
    if (c == '\n') {
        // 处理完整消息
        processMessage(message.toString());
        message.setLength(0);
    } else {
        message.append(c);
    }
}
```

**3. 长度前缀（推荐）**:
```java
public class LengthPrefixDecoder {
    private ByteBuffer buffer = ByteBuffer.allocate(1024);
    
    public void read(SocketChannel channel) throws IOException {
        channel.read(buffer);
        
        buffer.flip();
        while (buffer.remaining() >= 4) {
            buffer.mark();
            int length = buffer.getInt();
            
            if (buffer.remaining() < length) {
                buffer.reset(); // 数据不足，重置位置
                buffer.compact();
                return;
            }
            
            byte[] data = new byte[length];
            buffer.get(data);
            processMessage(data);
        }
        
        buffer.compact();
    }
}
```

---

### 6.3 NIO 性能优化

**问题**: 如何优化 NIO 应用的性能？

**答案**:

**优化策略**:

1. **使用直接缓冲区**
```java
// 大文件传输使用直接缓冲区
ByteBuffer buffer = ByteBuffer.allocateDirect(64 * 1024);
```

2. **合理设置缓冲区大小**
```java
// 根据 MTU 设置（通常 1500 字节）
ByteBuffer buffer = ByteBuffer.allocate(1500);

// 大文件传输使用更大缓冲区
ByteBuffer buffer = ByteBuffer.allocateDirect(64 * 1024);
```

3. **使用零拷贝**
```java
// 文件传输使用 transferTo
fileChannel.transferTo(position, count, socketChannel);
```

4. **多线程 Reactor 模型**
```java
// 主 Reactor 处理连接
// 从 Reactor 处理 IO
Selector acceptSelector = Selector.open();
Selector ioSelector = Selector.open();

// 主线程处理 ACCEPT
// 工作线程处理 READ/WRITE
```

5. **避免频繁创建 Buffer**
```java
// 使用 ThreadLocal 复用 Buffer
private static final ThreadLocal<ByteBuffer> BUFFER_HOLDER = 
    ThreadLocal.withInitial(() -> ByteBuffer.allocateDirect(8192));

ByteBuffer buffer = BUFFER_HOLDER.get();
```

6. **及时清理 SelectionKey**
```java
// 处理完后必须移除
iterator.remove();

// 取消无效的 Key
if (!key.isValid()) {
    key.cancel();
}
```

---

### 6.4 面试题速查表

| 问题 | 核心要点 |
|------|----------|
| NIO 三大组件 | Buffer、Channel、Selector |
| Buffer 四属性 | capacity、position、limit、mark |
| flip() vs clear() | flip 切换读模式，clear 清空写模式 |
| 直接 vs 堆缓冲区 | 直接缓冲区快但创建慢，适合大文件 |
| Channel vs Stream | Channel 双向，必须配合 Buffer |
| Selector 作用 | 多路复用，单线程管理多连接 |
| 零拷贝 | transferTo/transferFrom，减少数据拷贝 |
| 内存映射 | MappedByteBuffer，大文件高效处理 |
| NIO 适用场景 | 高并发网络、大文件处理 |

---

**文档版本**: 1.0.0  
**最后更新**: 2026-03-26  
**适用 JDK**: Java 8+
