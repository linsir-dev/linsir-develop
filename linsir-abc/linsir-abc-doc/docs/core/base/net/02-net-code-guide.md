# java.net 包代码说明文档

## 一、代码结构

### 1.1 包结构概览

```
com.linsir.abc.core.base.net/
├── socket/                    # Socket编程
│   ├── SocketServerBuilder.java       # Socket服务端构建器
│   ├── SocketConnectionPool.java      # Socket连接池
│   ├── DatagramCommunicator.java      # 数据报通信(UDP)
│   └── MulticastGroupManager.java     # 多播组管理
└── url/                       # URL处理
    ├── UrlResourceFetcher.java        # URL资源获取
    └── HttpConnectionManager.java     # HTTP连接管理
```

### 1.2 各类详细结构

#### 1.2.1 SocketServerBuilder.java

**JDK对应代码**: `java.net.ServerSocket` / `java.net.Socket`

**类结构**:
```java
public class SocketServerBuilder {
    // 字段
    private ServerSocket serverSocket;           // 服务端Socket
    private ExecutorService executor;            // 线程池
    private volatile boolean running;            // 运行标志
    private ClientHandler clientHandler;         // 客户端处理器
    
    // 构建方法（链式调用）
    public SocketServerBuilder bind(int port)              // 绑定端口
    public SocketServerBuilder withThreadPool(int size)    // 设置线程池
    public SocketServerBuilder withHandler(ClientHandler)  // 设置处理器
    
    // 生命周期方法
    public void start()           // 启动服务端
    public void stop()            // 停止服务端
    public boolean isRunning()    // 是否运行中
    
    // 内部接口
    @FunctionalInterface
    public interface ClientHandler {
        void handle(Socket clientSocket);
    }
}
```

**设计要点**:
- 使用建造者模式（Builder Pattern）支持链式调用
- 使用线程池处理多客户端并发连接
- 支持优雅关闭和资源清理
- 客户端处理器使用函数式接口，支持Lambda表达式

**JDK类对应关系**:
| 本类方法 | 对应JDK类/方法 | 说明 |
|----------|----------------|------|
| `ServerSocket` | `java.net.ServerSocket` | 服务端Socket |
| `Socket` | `java.net.Socket` | 客户端Socket |
| `bind()` | `ServerSocket.bind()` | 绑定端口 |
| `accept()` | `ServerSocket.accept()` | 接受连接 |
| `getInputStream()` | `Socket.getInputStream()` | 获取输入流 |
| `getOutputStream()` | `Socket.getOutputStream()` | 获取输出流 |
| `close()` | `Socket.close()` / `ServerSocket.close()` | 关闭连接 |

#### 1.2.2 SocketConnectionPool.java

**JDK对应代码**: `java.net.Socket` / `java.util.concurrent.BlockingQueue`

**类结构**:
```java
public class SocketConnectionPool {
    // 配置字段
    private final String host;                   // 主机地址
    private final int port;                      // 端口号
    private final int maxConnections;            // 最大连接数
    private final long connectionTimeout;        // 连接超时时间
    
    // 管理字段
    private final BlockingQueue<Socket> availableConnections;  // 可用连接队列
    private final AtomicInteger activeConnections;             // 活跃连接计数
    private volatile boolean closed;             // 关闭标志
    
    // 核心方法
    public Socket borrowConnection()              // 获取连接
    public void returnConnection(Socket)          // 归还连接
    public void close()                           // 关闭连接池
    
    // 内部方法
    private Socket createConnection()             // 创建新连接
    private boolean isValid(Socket)               // 检查连接有效性
    private void closeConnection(Socket)          // 关闭连接
}
```

**设计要点**:
- 使用阻塞队列管理可用连接
- 使用原子变量保证线程安全
- 支持连接有效性检查和自动重连
- 实现资源限制（最大连接数）

**JDK类对应关系**:
| 本类方法 | 对应JDK类/方法 | 说明 |
|----------|----------------|------|
| `Socket` | `java.net.Socket` | TCP客户端Socket |
| `new Socket(host, port)` | `Socket` 构造方法 | 创建连接 |
| `BlockingQueue` | `java.util.concurrent.BlockingQueue` | 阻塞队列 |
| `ArrayBlockingQueue` | `java.util.concurrent.ArrayBlockingQueue` | 数组阻塞队列 |
| `AtomicInteger` | `java.util.concurrent.atomic.AtomicInteger` | 原子整数 |
| `isConnected()` | `Socket.isConnected()` | 连接状态检查 |
| `isClosed()` | `Socket.isClosed()` | 关闭状态检查 |

#### 1.2.3 DatagramCommunicator.java

**JDK对应代码**: `java.net.DatagramSocket` / `java.net.DatagramPacket`

**类结构**:
```java
public class DatagramCommunicator {
    // 字段
    private DatagramSocket socket;               // 数据报Socket
    private int bufferSize;                      // 缓冲区大小
    
    // 核心方法
    public void bind(int port)                    // 绑定端口
    public void send(byte[], String, int)         // 发送数据报
    public DatagramPacket receive()               // 接收数据报
    public void close()                           // 关闭Socket
    
    // 工具方法
    public void setTimeout(int timeout)           // 设置超时
    public boolean isBound()                      // 是否已绑定
}
```

**设计要点**:
- 基于UDP协议，无连接通信
- 支持发送和接收数据报
- 可设置超时时间避免阻塞
- 适用于实时性要求高、可容忍丢包的场景

**JDK类对应关系**:
| 本类方法 | 对应JDK类/方法 | 说明 |
|----------|----------------|------|
| `DatagramSocket` | `java.net.DatagramSocket` | UDP Socket |
| `DatagramPacket` | `java.net.DatagramPacket` | 数据报包 |
| `send()` | `DatagramSocket.send()` | 发送数据报 |
| `receive()` | `DatagramSocket.receive()` | 接收数据报 |
| `bind()` | `DatagramSocket.bind()` | 绑定地址 |
| `setSoTimeout()` | `DatagramSocket.setSoTimeout()` | 设置超时 |
| `InetAddress` | `java.net.InetAddress` | IP地址 |
| `InetSocketAddress` | `java.net.InetSocketAddress` | Socket地址 |

#### 1.2.4 MulticastGroupManager.java

**JDK对应代码**: `java.net.MulticastSocket` / `java.net.InetAddress`

**类结构**:
```java
public class MulticastGroupManager {
    // 字段
    private MulticastSocket socket;              // 多播Socket
    private InetAddress groupAddress;            // 多播组地址
    private int port;                            // 端口
    private NetworkInterface networkInterface;   // 网络接口
    
    // 核心方法
    public void joinGroup(String, int)            // 加入多播组
    public void leaveGroup()                      // 离开多播组
    public void send(byte[])                      // 发送多播消息
    public DatagramPacket receive()               // 接收多播消息
    public void close()                           // 关闭Socket
}
```

**设计要点**:
- 支持加入/离开多播组
- 多播地址范围：224.0.0.0 ~ 239.255.255.255
- 适用于一对多通信场景
- 常用于局域网内的服务发现

**JDK类对应关系**:
| 本类方法 | 对应JDK类/方法 | 说明 |
|----------|----------------|------|
| `MulticastSocket` | `java.net.MulticastSocket` | 多播Socket |
| `joinGroup()` | `MulticastSocket.joinGroup()` | 加入多播组 |
| `leaveGroup()` | `MulticastSocket.leaveGroup()` | 离开多播组 |
| `InetAddress` | `java.net.InetAddress` | IP地址 |
| `NetworkInterface` | `java.net.NetworkInterface` | 网络接口 |
| `DatagramPacket` | `java.net.DatagramPacket` | 数据报包 |
| 多播地址范围 | 224.0.0.0 ~ 239.255.255.255 | D类IP地址 |

#### 1.2.5 HttpConnectionManager.java

**JDK对应代码**: `java.net.HttpURLConnection` / `java.net.URL`

**类结构**:
```java
public class HttpConnectionManager {
    // HTTP请求方法
    public HttpResponse sendGet(String url)       // 发送GET请求
    public HttpResponse sendPost(String, String)  // 发送POST请求
    
    // 内部方法
    private HttpResponse executeRequest(HttpURLConnection)  // 执行请求
    
    // 内部类
    public static class HttpResponse {
        private int statusCode;                    // 状态码
        private String statusMessage;              // 状态消息
        private Map<String, List<String>> headers; // 响应头
        private String body;                       // 响应体
        
        // getters and setters
    }
}
```

**设计要点**:
- 基于HttpURLConnection实现
- 支持GET和POST请求
- 封装响应对象为HttpResponse
- 支持设置超时和请求头

**JDK类对应关系**:
| 本类方法 | 对应JDK类/方法 | 说明 |
|----------|----------------|------|
| `HttpURLConnection` | `java.net.HttpURLConnection` | HTTP连接 |
| `URL` | `java.net.URL` | URL类 |
| `openConnection()` | `URL.openConnection()` | 打开连接 |
| `setRequestMethod()` | `HttpURLConnection.setRequestMethod()` | 设置请求方法 |
| `setConnectTimeout()` | `HttpURLConnection.setConnectTimeout()` | 连接超时 |
| `setReadTimeout()` | `HttpURLConnection.setReadTimeout()` | 读取超时 |
| `getResponseCode()` | `HttpURLConnection.getResponseCode()` | 响应码 |
| `getHeaderFields()` | `HttpURLConnection.getHeaderFields()` | 响应头 |
| `getInputStream()` | `HttpURLConnection.getInputStream()` | 响应流 |

#### 1.2.6 UrlResourceFetcher.java

**JDK对应代码**: `java.net.URL` / `java.net.URLConnection`

**类结构**:
```java
public class UrlResourceFetcher {
    // 核心方法
    public InputStream openStream(String)         // 打开输入流
    public byte[] fetchBytes(String)              // 获取字节内容
    public String fetchText(String, String)       // 获取文本内容
    public void downloadToFile(String, String)    // 下载到文件
    
    // 信息方法
    public URLConnection openConnection(String)   // 打开连接
    public Map<String, List<String>> getHeaders(String)  // 获取头信息
}
```

**设计要点**:
- 支持多种资源获取方式
- 自动处理编码转换
- 支持大文件下载
- 提供资源元数据获取

**JDK类对应关系**:
| 本类方法 | 对应JDK类/方法 | 说明 |
|----------|----------------|------|
| `URL` | `java.net.URL` | URL类 |
| `openStream()` | `URL.openStream()` | 打开输入流 |
| `URLConnection` | `java.net.URLConnection` | URL连接 |
| `openConnection()` | `URL.openConnection()` | 打开连接 |
| `getContentType()` | `URLConnection.getContentType()` | 内容类型 |
| `getContentLength()` | `URLConnection.getContentLength()` | 内容长度 |
| `getLastModified()` | `URLConnection.getLastModified()` | 最后修改时间 |
| `getHeaderFields()` | `URLConnection.getHeaderFields()` | 头信息 |
| `URI` | `java.net.URI` | URI类 |

---

## 二、代码使用场景

### 2.1 SocketServerBuilder - Socket服务端构建

**适用场景**:
- 构建TCP服务端应用
- 需要处理多客户端并发连接
- 需要自定义客户端处理逻辑
- 需要优雅管理服务端生命周期

**使用示例**:
```java
// 构建并启动服务端
SocketServerBuilder server = new SocketServerBuilder();
server.bind(8080)                                    // 绑定端口
      .withThreadPool(10)                            // 设置线程池大小
      .withHandler(clientSocket -> {                 // 设置客户端处理器
          try {
              // 处理客户端请求
              BufferedReader reader = new BufferedReader(
                  new InputStreamReader(clientSocket.getInputStream()));
              String message = reader.readLine();
              System.out.println("收到消息: " + message);
              
              // 发送响应
              PrintWriter writer = new PrintWriter(
                  clientSocket.getOutputStream(), true);
              writer.println("收到: " + message);
          } catch (IOException e) {
              e.printStackTrace();
          }
      })
      .start();                                      // 启动服务端

// 停止服务端
server.stop();
```

**最佳实践**:
| 场景 | 推荐配置 | 原因 |
|------|----------|------|
| 少量客户端 | 线程池大小=客户端数 | 避免线程切换开销 |
| 大量客户端 | 线程池大小=CPU核心数*2 | 平衡并发和性能 |
| 长连接 | 设置心跳检测 | 检测连接有效性 |
| 短连接 | 使用CachedThreadPool | 灵活处理连接峰值 |

### 2.2 SocketConnectionPool - Socket连接池

**适用场景**:
- 客户端需要频繁建立Socket连接
- 需要复用连接减少开销
- 需要限制最大连接数
- 需要连接有效性检查

**使用示例**:
```java
// 创建连接池
SocketConnectionPool pool = new SocketConnectionPool(
    "localhost",      // 主机
    8080,             // 端口
    10,               // 最大连接数
    5000              // 超时时间(毫秒)
);

// 获取连接
try {
    Socket socket = pool.borrowConnection();
    
    // 使用连接发送数据
    PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
    writer.println("Hello Server");
    
    // 读取响应
    BufferedReader reader = new BufferedReader(
        new InputStreamReader(socket.getInputStream()));
    String response = reader.readLine();
    
    // 归还连接
    pool.returnConnection(socket);
} catch (IOException | InterruptedException e) {
    e.printStackTrace();
}

// 关闭连接池
pool.close();
```

**连接池优势**:
- 减少TCP三次握手开销
- 控制并发连接数
- 自动管理连接生命周期
- 提高系统稳定性

### 2.3 DatagramCommunicator - UDP通信

**适用场景**:
- 实时音视频传输
- 在线游戏状态同步
- DNS查询
- 日志收集
- 广播消息

**使用示例**:
```java
// 服务端
DatagramCommunicator server = new DatagramCommunicator();
server.bind(9876);

// 接收数据
new Thread(() -> {
    while (true) {
        DatagramPacket packet = server.receive();
        String message = new String(packet.getData(), 0, packet.getLength());
        System.out.println("收到: " + message + " from " + packet.getAddress());
    }
}).start();

// 客户端
DatagramCommunicator client = new DatagramCommunicator();
client.bind(0);  // 自动分配端口

// 发送数据
byte[] data = "Hello UDP".getBytes();
client.send(data, "localhost", 9876);

client.close();
```

**TCP vs UDP选择**:
| 特性 | TCP | UDP |
|------|-----|-----|
| 连接方式 | 面向连接 | 无连接 |
| 可靠性 | 可靠传输 | 不保证可靠 |
| 顺序性 | 保证顺序 | 不保证顺序 |
| 开销 | 较大 | 较小 |
| 适用场景 | 文件传输、HTTP | 视频流、游戏 |

### 2.4 MulticastGroupManager - 多播通信

**适用场景**:
- 局域网服务发现
- 实时数据分发
- 在线会议
- 股票行情广播

**使用示例**:
```java
// 加入多播组
MulticastGroupManager member = new MulticastGroupManager();
member.joinGroup("230.0.0.1", 4446);

// 接收多播消息
new Thread(() -> {
    while (true) {
        DatagramPacket packet = member.receive();
        String message = new String(packet.getData(), 0, packet.getLength());
        System.out.println("收到多播消息: " + message);
    }
}).start();

// 发送多播消息
byte[] data = "Hello Multicast Group".getBytes();
member.send(data);

// 离开多播组
member.leaveGroup();
member.close();
```

### 2.5 HttpConnectionManager - HTTP请求

**适用场景**:
- 调用RESTful API
- 网页抓取
- 文件下载
- 与Web服务交互

**使用示例**:
```java
HttpConnectionManager http = new HttpConnectionManager();

// 发送GET请求
try {
    HttpConnectionManager.HttpResponse response = http.sendGet("https://api.example.com/users");
    System.out.println("状态码: " + response.getStatusCode());
    System.out.println("响应体: " + response.getBody());
} catch (IOException e) {
    e.printStackTrace();
}

// 发送POST请求
try {
    String body = "name=张三&age=25";
    HttpConnectionManager.HttpResponse response = http.sendPost(
        "https://api.example.com/users", body);
    System.out.println("状态码: " + response.getStatusCode());
} catch (IOException e) {
    e.printStackTrace();
}
```

### 2.6 UrlResourceFetcher - 资源获取

**适用场景**:
- 下载远程文件
- 获取网页内容
- 读取远程配置文件
- 获取资源元数据

**使用示例**:
```java
UrlResourceFetcher fetcher = new UrlResourceFetcher();

// 下载文件
fetcher.downloadToFile("https://example.com/file.zip", "/local/path/file.zip");

// 获取文本内容
String html = fetcher.fetchText("https://example.com", "UTF-8");

// 获取字节内容
byte[] imageData = fetcher.fetchBytes("https://example.com/image.png");

// 获取头信息
Map<String, List<String>> headers = fetcher.getHeaders("https://example.com");
headers.forEach((key, values) -> {
    System.out.println(key + ": " + values);
});
```

---

## 三、测试代码结构

### 3.1 测试包结构

```
src/test/java/com/linsir/abc/core/base/net/
├── socket/
│   ├── SocketServerBuilderTest.java       # Socket服务端构建器测试
│   ├── SocketConnectionPoolTest.java      # Socket连接池测试
│   ├── DatagramCommunicatorTest.java      # 数据报通信测试
│   └── MulticastGroupManagerTest.java     # 多播组管理测试
└── url/
    ├── UrlResourceFetcherTest.java        # URL资源获取测试
    └── HttpConnectionManagerTest.java     # HTTP连接管理测试
```

### 3.2 测试类结构示例

#### SocketServerBuilderTest.java

```java
public class SocketServerBuilderTest {
    // 构建器测试
    @Test void testBind()                    // 绑定端口
    @Test void testWithThreadPool()          // 设置线程池
    @Test void testWithHandler()             // 设置处理器
    @Test void testBuilderChain()            // 链式构建
    
    // 异常测试
    @Test void testStartWithoutBind()        // 未绑定启动
    @Test void testStartWithoutHandler()     // 未设置处理器启动
    
    // 功能测试
    @Test void testServerStartAndStop()      // 启动停止
    @Test void testEchoServer()              // Echo服务测试
}
```

#### SocketConnectionPoolTest.java

```java
public class SocketConnectionPoolTest {
    // 连接管理测试
    @Test void testBorrowConnection()         // 获取连接
    @Test void testReturnConnection()         // 归还连接
    @Test void testConnectionReuse()          // 连接复用
    
    // 边界测试
    @Test void testMaxConnections()           // 最大连接数限制
    @Test void testConnectionTimeout()        // 连接超时
    
    // 生命周期测试
    @Test void testClosePool()                // 关闭连接池
    @Test void testBorrowAfterClose()         // 关闭后获取连接
}
```

#### HttpConnectionManagerTest.java

```java
public class HttpConnectionManagerTest {
    // HTTP方法测试
    @Test void testSendGet()                  // GET请求
    @Test void testSendPost()                 // POST请求
    
    // 响应测试
    @Test void testResponseStatusCode()       // 响应状态码
    @Test void testResponseHeaders()          // 响应头
    @Test void testResponseBody()             // 响应体
    
    // 异常测试
    @Test void testInvalidUrl()               // 无效URL
    @Test void testConnectionTimeout()        // 连接超时
}
```

---

## 四、单元测试预期结果

### 4.1 SocketServerBuilderTest 预期结果

| 测试方法 | 预期结果 | 说明 |
|----------|----------|------|
| `testBind` | ✅ PASS | 成功绑定端口 |
| `testWithThreadPool` | ✅ PASS | 线程池设置成功 |
| `testWithHandler` | ✅ PASS | 处理器设置成功 |
| `testBuilderChain` | ✅ PASS | 链式调用正常 |
| `testStartWithoutBind` | ✅ PASS | 抛出IllegalStateException |
| `testStartWithoutHandler` | ✅ PASS | 抛出IllegalStateException |
| `testServerStartAndStop` | ✅ PASS | 正常启动和停止 |
| `testEchoServer` | ✅ PASS | Echo服务正常工作 |

### 4.2 SocketConnectionPoolTest 预期结果

| 测试方法 | 预期结果 | 说明 |
|----------|----------|------|
| `testBorrowConnection` | ✅ PASS | 成功获取连接 |
| `testReturnConnection` | ✅ PASS | 成功归还连接 |
| `testConnectionReuse` | ✅ PASS | 连接被复用 |
| `testMaxConnections` | ✅ PASS | 达到最大连接数后等待或报错 |
| `testConnectionTimeout` | ✅ PASS | 超时抛出异常 |
| `testClosePool` | ✅ PASS | 连接池正常关闭 |
| `testBorrowAfterClose` | ✅ PASS | 抛出IllegalStateException |

### 4.3 DatagramCommunicatorTest 预期结果

| 测试方法 | 预期结果 | 说明 |
|----------|----------|------|
| `testBind` | ✅ PASS | 成功绑定端口 |
| `testSendAndReceive` | ✅ PASS | 发送接收数据报正常 |
| `testTimeout` | ✅ PASS | 超时设置生效 |
| `testMultiplePackets` | ✅ PASS | 多个数据报处理正常 |

### 4.4 MulticastGroupManagerTest 预期结果

| 测试方法 | 预期结果 | 说明 |
|----------|----------|------|
| `testJoinGroup` | ✅ PASS | 成功加入多播组 |
| `testLeaveGroup` | ✅ PASS | 成功离开多播组 |
| `testSendAndReceive` | ✅ PASS | 多播消息收发正常 |
| `testMultipleMembers` | ✅ PASS | 多成员接收正常 |

### 4.5 HttpConnectionManagerTest 预期结果

| 测试方法 | 预期结果 | 说明 |
|----------|----------|------|
| `testSendGet` | ✅ PASS | GET请求成功 |
| `testSendPost` | ✅ PASS | POST请求成功 |
| `testResponseStatusCode` | ✅ PASS | 状态码正确 |
| `testResponseHeaders` | ✅ PASS | 响应头正确 |
| `testResponseBody` | ✅ PASS | 响应体正确 |
| `testInvalidUrl` | ✅ PASS | 抛出IOException |
| `testConnectionTimeout` | ✅ PASS | 超时抛出异常 |

### 4.6 UrlResourceFetcherTest 预期结果

| 测试方法 | 预期结果 | 说明 |
|----------|----------|------|
| `testOpenStream` | ✅ PASS | 成功打开输入流 |
| `testFetchBytes` | ✅ PASS | 字节内容获取正确 |
| `testFetchText` | ✅ PASS | 文本内容获取正确 |
| `testDownloadToFile` | ✅ PASS | 文件下载成功 |
| `testGetHeaders` | ✅ PASS | 头信息获取正确 |

### 4.7 所有测试汇总

| 测试类 | 测试数 | 预期通过率 |
|--------|--------|------------|
| SocketServerBuilderTest | 8 | 100% |
| SocketConnectionPoolTest | 7 | 100% |
| DatagramCommunicatorTest | 4 | 100% |
| MulticastGroupManagerTest | 4 | 100% |
| HttpConnectionManagerTest | 7 | 100% |
| UrlResourceFetcherTest | 5 | 100% |
| **总计** | **35** | **100%** |

---

## 五、运行测试

### 5.1 运行所有net包测试

```bash
mvn test -Dtest="com.linsir.abc.core.base.net.**"
```

### 5.2 运行单个测试类

```bash
mvn test -Dtest=SocketServerBuilderTest
```

### 5.3 预期输出

```
Tests run: 35, Failures: 0, Errors: 0, Skipped: 0
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.linsir.abc.core.base.net.socket.SocketServerBuilderTest
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
...
[INFO] BUILD SUCCESS
```

---

## 六、网络编程选择指南

### 6.1 按协议选择

| 协议 | 适用场景 | 推荐类 |
|------|----------|--------|
| TCP | 可靠传输、文件传输 | SocketServerBuilder, SocketConnectionPool |
| UDP | 实时通信、广播 | DatagramCommunicator |
| Multicast | 一对多通信 | MulticastGroupManager |
| HTTP | Web服务调用 | HttpConnectionManager |

### 6.2 按角色选择

| 角色 | 功能 | 推荐类 |
|------|------|--------|
| 服务端 | 监听连接、处理请求 | SocketServerBuilder |
| 客户端 | 发起连接、发送请求 | SocketConnectionPool, HttpConnectionManager |
| 资源获取 | 下载文件、获取内容 | UrlResourceFetcher |

### 6.3 注意事项

1. **端口选择**: 避免使用系统保留端口（0-1023），推荐使用1024以上的端口
2. **超时设置**: 网络操作必须设置超时，避免无限阻塞
3. **资源释放**: 使用try-with-resources或finally块确保Socket关闭
4. **线程安全**: 多线程环境下注意同步问题
5. **异常处理**: 网络操作可能抛出多种异常，需要完善处理

---

**文档版本**: 1.0.0  
**最后更新**: 2026-03-26
