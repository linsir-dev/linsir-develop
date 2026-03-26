# java.net 包面试题汇总

## 目录

1. [网络编程基础](#一网络编程基础)
2. [Socket 编程](#二socket-编程)
3. [TCP 与 UDP](#三tcp-与-udp)
4. [URL 与 HTTP](#四url-与-http)
5. [连接池与性能优化](#五连接池与性能优化)
6. [高级网络编程](#六高级网络编程)

---

## 一、网络编程基础

### 1.1 什么是 Java 网络编程？

**问题**: 请简述 Java 网络编程是什么，以及它的核心作用？

**答案**:
Java 网络编程是通过 `java.net` 包提供的 API，实现程序之间的网络通信。核心作用包括：

- **网络通信**: 实现不同主机间进程的数据交换
- **分布式计算**: 支持客户端-服务器架构
- **资源共享**: 访问远程文件、数据库、服务
- **互联网应用**: Web 应用、即时通讯、在线游戏等

**核心组件**:
- **Socket**: 端到端的通信端点
- **ServerSocket**: 服务端监听连接请求
- **URL**: 统一资源定位符
- **URLConnection**: URL 的连接抽象

---

### 1.2 IP 地址和端口号

**问题**: 什么是 IP 地址和端口号？它们在网络通信中的作用是什么？

**答案**:

**IP 地址（Internet Protocol Address）**:
- 标识网络中的**主机**（计算机或设备）
- IPv4: 32位地址，如 `192.168.1.1`
- IPv6: 128位地址，如 `2001:0db8:85a3::8a2e:0370:7334`

**端口号（Port）**:
- 标识主机上的**应用程序**
- 范围: 0-65535
- 分类:
  - 0-1023: 系统保留端口（HTTP: 80, HTTPS: 443, SSH: 22）
  - 1024-49151: 注册端口
  - 49152-65535: 动态/私有端口

**作用**:
- IP 地址 → 找到**哪台计算机**
- 端口号 → 找到计算机上的**哪个程序**
- 组合起来 → **Socket = IP + Port**

**代码示例**:
```java
// 获取本地 IP
InetAddress localHost = InetAddress.getLocalHost();
System.out.println("IP: " + localHost.getHostAddress());

// 根据主机名获取 IP
InetAddress google = InetAddress.getByName("www.google.com");
System.out.println("Google IP: " + google.getHostAddress());
```

---

### 1.3 InetAddress 类

**问题**: `InetAddress` 类的作用是什么？常用方法有哪些？

**答案**:

**作用**: 表示互联网协议（IP）地址，封装了 IP 地址的相关操作。

**常用方法**:

| 方法 | 说明 |
|------|------|
| `getLocalHost()` | 获取本地主机地址 |
| `getByName(String host)` | 根据主机名获取地址 |
| `getAllByName(String host)` | 获取主机名的所有地址 |
| `getHostAddress()` | 获取 IP 地址字符串 |
| `getHostName()` | 获取主机名 |
| `isReachable(int timeout)` | 测试是否可达 |

**代码示例**:
```java
// 获取本地地址
InetAddress local = InetAddress.getLocalHost();
System.out.println("主机名: " + local.getHostName());
System.out.println("IP地址: " + local.getHostAddress());

// 解析域名
InetAddress[] addresses = InetAddress.getAllByName("www.baidu.com");
for (InetAddress addr : addresses) {
    System.out.println(addr.getHostAddress());
}

// 测试连通性
boolean reachable = local.isReachable(5000); // 5秒超时
```

---

## 二、Socket 编程

### 2.1 什么是 Socket？

**问题**: 什么是 Socket？Socket 通信的基本流程是什么？

**答案**:

**Socket（套接字）**:
- 网络通信的**端点**
- 封装了 IP 地址和端口号
- 提供**发送**和**接收**数据的能力

**Socket 类型**:
- **流式 Socket（TCP）**: `Socket` / `ServerSocket`
- **数据报 Socket（UDP）**: `DatagramSocket`

**TCP Socket 通信流程**:

```
服务端                          客户端
  |                              |
  |---- 1. 创建 ServerSocket ----|
  |                              |
  |---- 2. bind() 绑定端口 -------|
  |                              |
  |---- 3. listen() 监听 ---------|
  |                              |---- 1. 创建 Socket
  |                              |
  |<--- 4. accept() 等待连接 -----|---- 2. connect() 发起连接
  |                              |        (三次握手)
  |<===============================> 3. 连接建立
  |                              |
  |<---- 5. 收发数据 ------------>|---- 4. 收发数据
  |                              |
  |<===============================> 5. 关闭连接
  |                              |        (四次挥手)
```

**代码示例**:
```java
// 服务端
ServerSocket serverSocket = new ServerSocket(8080);
Socket clientSocket = serverSocket.accept(); // 阻塞等待连接

// 客户端
Socket socket = new Socket("localhost", 8080);
```

---

### 2.2 ServerSocket 和 Socket

**问题**: `ServerSocket` 和 `Socket` 的区别是什么？

**答案**:

| 特性 | ServerSocket | Socket |
|------|--------------|--------|
| **作用** | 服务端监听连接 | 客户端发起连接 |
| **创建方式** | `new ServerSocket(port)` | `new Socket(host, port)` |
| **核心方法** | `accept()` | `connect()`, `getInputStream()` |
| **数量** | 一个服务端一个 | 每个客户端一个 |
| **生命周期** | 长期运行 | 连接关闭即结束 |

**关系**:
```
ServerSocket (1个)
    └── accept() 返回 Socket (多个客户端连接)
```

**代码示例**:
```java
// 服务端
ServerSocket server = new ServerSocket(8080);
System.out.println("服务端启动，等待连接...");

while (true) {
    Socket client = server.accept(); // 阻塞等待
    System.out.println("客户端连接: " + client.getInetAddress());
    
    // 处理客户端请求
    handleClient(client);
}

// 客户端
Socket socket = new Socket("localhost", 8080);
System.out.println("连接服务端成功");
```

---

### 2.3 Socket 通信示例

**问题**: 请编写一个简单的 Socket 通信示例，包括服务端和客户端。

**答案**:

**服务端代码**:
```java
public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("服务端启动，监听端口 8080");
        
        Socket clientSocket = serverSocket.accept();
        System.out.println("客户端已连接: " + clientSocket.getInetAddress());
        
        // 读取客户端数据
        BufferedReader in = new BufferedReader(
            new InputStreamReader(clientSocket.getInputStream()));
        String message = in.readLine();
        System.out.println("收到消息: " + message);
        
        // 发送响应
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        out.println("服务端已收到: " + message);
        
        // 关闭资源
        in.close();
        out.close();
        clientSocket.close();
        serverSocket.close();
    }
}
```

**客户端代码**:
```java
public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 8080);
        System.out.println("连接服务端成功");
        
        // 发送数据
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        out.println("Hello, Server!");
        
        // 接收响应
        BufferedReader in = new BufferedReader(
            new InputStreamReader(socket.getInputStream()));
        String response = in.readLine();
        System.out.println("服务端响应: " + response);
        
        // 关闭资源
        in.close();
        out.close();
        socket.close();
    }
}
```

---

### 2.4 多线程服务端

**问题**: 为什么需要多线程服务端？如何实现？

**答案**:

**原因**:
- 单线程服务端一次只能处理**一个客户端**
- 其他客户端必须**等待**当前连接关闭
- 多线程可以同时服务**多个客户端**

**实现方式**:
```java
public class MultiThreadServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("多线程服务端启动");
        
        while (true) {
            Socket clientSocket = serverSocket.accept();
            // 为每个客户端创建新线程
            new Thread(new ClientHandler(clientSocket)).start();
        }
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;
    
    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }
    
    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(
                clientSocket.getOutputStream(), true)
        ) {
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("收到: " + message);
                out.println("回声: " + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

**优化方案**:
- 使用**线程池**替代直接创建线程
- 控制并发数量，避免资源耗尽

---

## 三、TCP 与 UDP

### 3.1 TCP 和 UDP 的区别？

**问题**: TCP 和 UDP 有什么区别？各自的适用场景是什么？

**答案**:

| 特性 | TCP | UDP |
|------|-----|-----|
| **连接方式** | 面向连接 | 无连接 |
| **可靠性** | 可靠传输 | 不可靠传输 |
| **顺序性** | 保证数据顺序 | 不保证顺序 |
| **流量控制** | 有 | 无 |
| **拥塞控制** | 有 | 无 |
| **头部开销** | 大（20字节） | 小（8字节） |
| **传输效率** | 较低 | 高 |
| **适用场景** | 文件传输、HTTP、邮件 | 视频流、DNS、游戏 |

**TCP 特点**:
- **三次握手**建立连接
- **四次挥手**关闭连接
- **确认机制**保证可靠
- **重传机制**处理丢包

**UDP 特点**:
- 直接发送，无需连接
- 速度快，开销小
- 可能丢包，不保证顺序

**适用场景**:
- **TCP**: HTTP/HTTPS、FTP、SMTP、数据库连接
- **UDP**: 视频直播、在线游戏、DNS 查询、物联网

---

### 3.2 TCP 三次握手和四次挥手

**问题**: 请详细说明 TCP 的三次握手和四次挥手过程。

**答案**:

**三次握手（建立连接）**:

```
客户端                          服务端
  |                                |
  |-------- SYN=1, seq=x -------->|  ① 客户端请求连接
  |                                |
  |<-- SYN=1, ACK=1, seq=y, ack=x+1 --|  ② 服务端确认并请求
  |                                |
  |-------- ACK=1, ack=y+1 ------>|  ③ 客户端确认
  |                                |
  |<========= 连接建立 ===========>|
```

**为什么是三次？**
- 防止**历史重复连接**初始化
- 同步双方的**初始序列号**
- 确认双方的**收发能力**正常

**四次挥手（关闭连接）**:

```
客户端                          服务端
  |                                |
  |-------- FIN=1, seq=u -------->|  ① 客户端请求关闭
  |                                |
  |<-------- ACK=1, ack=u+1 ------|  ② 服务端确认
  |                                |     （此时服务端可能还有数据发送）
  |<-------- FIN=1, seq=w --------|  ③ 服务端请求关闭
  |                                |
  |-------- ACK=1, ack=w+1 ------>|  ④ 客户端确认
  |                                |
  |<========= 连接关闭 ===========>|
```

**为什么是四次？**
- 全双工通信，需要**双方分别关闭**
- 服务端可能还有**数据未发送完**
- 确保**所有数据**都传输完毕

---

### 3.3 DatagramSocket 和 UDP 通信

**问题**: 如何使用 UDP 进行网络通信？

**答案**:

**UDP 通信特点**:
- 使用 `DatagramSocket` 和 `DatagramPacket`
- 无需建立连接
- 每个数据包独立发送

**服务端代码**:
```java
public class UdpServer {
    public static void main(String[] args) throws IOException {
        DatagramSocket socket = new DatagramSocket(8080);
        System.out.println("UDP 服务端启动");
        
        byte[] buffer = new byte[1024];
        
        while (true) {
            // 接收数据包
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet); // 阻塞等待
            
            String received = new String(packet.getData(), 0, packet.getLength());
            System.out.println("收到: " + received);
            
            // 发送响应
            String response = "已收到: " + received;
            DatagramPacket responsePacket = new DatagramPacket(
                response.getBytes(),
                response.length(),
                packet.getAddress(),
                packet.getPort()
            );
            socket.send(responsePacket);
        }
    }
}
```

**客户端代码**:
```java
public class UdpClient {
    public static void main(String[] args) throws IOException {
        DatagramSocket socket = new DatagramSocket(); // 客户端不需要绑定端口
        
        String message = "Hello UDP!";
        byte[] data = message.getBytes();
        
        // 创建数据包
        DatagramPacket packet = new DatagramPacket(
            data,
            data.length,
            InetAddress.getByName("localhost"),
            8080
        );
        
        // 发送
        socket.send(packet);
        System.out.println("消息已发送");
        
        // 接收响应
        byte[] buffer = new byte[1024];
        DatagramPacket response = new DatagramPacket(buffer, buffer.length);
        socket.receive(response);
        
        String received = new String(response.getData(), 0, response.getLength());
        System.out.println("响应: " + received);
        
        socket.close();
    }
}
```

---

### 3.4 广播和多播

**问题**: 什么是广播和多播？如何实现？

**答案**:

**广播（Broadcast）**:
- 向**局域网内所有主机**发送数据
- 广播地址: `255.255.255.255`
- 使用场景: DHCP、ARP 发现

**多播（Multicast）**:
- 向**特定组**的主机发送数据
- 多播地址: `224.0.0.0` ~ `239.255.255.255`
- 使用场景: 视频会议、股票行情、在线直播

**多播实现代码**:
```java
// 多播服务端（发送）
public class MulticastServer {
    public static void main(String[] args) throws IOException {
        MulticastSocket socket = new MulticastSocket();
        
        String message = "多播消息";
        byte[] data = message.getBytes();
        
        // 多播组地址
        InetAddress group = InetAddress.getByName("230.0.0.1");
        DatagramPacket packet = new DatagramPacket(
            data, data.length, group, 4446
        );
        
        socket.send(packet);
        System.out.println("多播消息已发送");
        socket.close();
    }
}

// 多播客户端（接收）
public class MulticastClient {
    public static void main(String[] args) throws IOException {
        MulticastSocket socket = new MulticastSocket(4446);
        
        InetAddress group = InetAddress.getByName("230.0.0.1");
        socket.joinGroup(group); // 加入多播组
        
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        
        socket.receive(packet);
        String received = new String(packet.getData(), 0, packet.getLength());
        System.out.println("收到多播: " + received);
        
        socket.leaveGroup(group); // 离开多播组
        socket.close();
    }
}
```

---

## 四、URL 与 HTTP

### 4.1 URL 和 URI 的区别？

**问题**: URL 和 URI 有什么区别？

**答案**:

**URI（Uniform Resource Identifier）统一资源标识符**:
- 标识资源的**字符串**
- 包括 URL 和 URN
- 格式: `scheme:[//authority]path[?query][#fragment]`

**URL（Uniform Resource Locator）统一资源定位符**:
- URI 的**子集**
- 不仅标识资源，还指明**如何定位**
- 包含协议、主机、端口、路径

**URN（Uniform Resource Name）统一资源名称**:
- 仅标识资源**名称**
- 不包含定位信息
- 例: `urn:isbn:0451450523`

**关系图**:
```
URI
├── URL (定位符)
│   └── http://example.com/page
└── URN (名称)
    └── urn:isbn:0451450523
```

**代码示例**:
```java
URL url = new URL("https://www.example.com:8080/path?query=1#fragment");

System.out.println("协议: " + url.getProtocol());   // https
System.out.println("主机: " + url.getHost());       // www.example.com
System.out.println("端口: " + url.getPort());       // 8080
System.out.println("路径: " + url.getPath());       // /path
System.out.println("查询: " + url.getQuery());      // query=1
System.out.println("片段: " + url.getRef());        // fragment
```

---

### 4.2 URLConnection 和 HttpURLConnection

**问题**: `URLConnection` 和 `HttpURLConnection` 的作用是什么？

**答案**:

**URLConnection**:
- 应用程序和 URL 之间的**通信链接**
- 抽象类，提供通用的 URL 连接操作
- 可用于各种协议（HTTP、FTP、File 等）

**HttpURLConnection**:
- `URLConnection` 的 HTTP 协议**子类**
- 提供 HTTP 特定的功能
- 支持 GET、POST 等方法
- 可设置请求头、读取响应码

**代码示例**:
```java
// GET 请求
URL url = new URL("https://api.example.com/data");
HttpURLConnection conn = (HttpURLConnection) url.openConnection();
conn.setRequestMethod("GET");
conn.setConnectTimeout(5000);
conn.setReadTimeout(5000);

// 设置请求头
conn.setRequestProperty("Accept", "application/json");
conn.setRequestProperty("Authorization", "Bearer token");

// 获取响应
int responseCode = conn.getResponseCode();
System.out.println("响应码: " + responseCode);

BufferedReader reader = new BufferedReader(
    new InputStreamReader(conn.getInputStream()));
String line;
while ((line = reader.readLine()) != null) {
    System.out.println(line);
}
reader.close();
conn.disconnect();
```

---

### 4.3 发送 HTTP POST 请求

**问题**: 如何使用 `HttpURLConnection` 发送 POST 请求？

**答案**:

```java
public class HttpPostExample {
    public static void main(String[] args) throws IOException {
        URL url = new URL("https://api.example.com/submit");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        // 设置请求方法
        conn.setRequestMethod("POST");
        conn.setDoOutput(true); // 允许写入
        
        // 设置请求头
        conn.setRequestProperty("Content-Type", "application/json");
        
        // 写入请求体
        String jsonInput = "{\"name\":\"张三\",\"age\":25}";
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInput.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        
        // 获取响应
        int responseCode = conn.getResponseCode();
        System.out.println("响应码: " + responseCode);
        
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }
            System.out.println("响应: " + response);
        }
        
        conn.disconnect();
    }
}
```

**关键步骤**:
1. `setDoOutput(true)` - 允许写入请求体
2. `setRequestProperty()` - 设置 Content-Type
3. `getOutputStream()` - 获取输出流写入数据
4. `getInputStream()` - 获取响应数据

---

### 4.4 处理 HTTP 响应码

**问题**: 常见的 HTTP 响应码有哪些？如何处理？

**答案**:

**常见响应码**:

| 状态码 | 含义 | 处理建议 |
|--------|------|----------|
| 200 | OK | 请求成功 |
| 301/302 | 重定向 | 跟随 Location 头跳转 |
| 400 | Bad Request | 检查请求参数 |
| 401 | Unauthorized | 检查认证信息 |
| 403 | Forbidden | 无权限访问 |
| 404 | Not Found | 资源不存在 |
| 500 | Internal Server Error | 服务端错误，稍后重试 |
| 502 | Bad Gateway | 网关错误 |
| 503 | Service Unavailable | 服务不可用 |

**处理代码**:
```java
int responseCode = conn.getResponseCode();

if (responseCode == HttpURLConnection.HTTP_OK) {
    // 200 - 正常处理
    processResponse(conn.getInputStream());
} else if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP ||
           responseCode == HttpURLConnection.HTTP_MOVED_PERM) {
    // 301/302 - 重定向
    String newUrl = conn.getHeaderField("Location");
    System.out.println("重定向到: " + newUrl);
    // 重新请求新 URL
} else if (responseCode >= 400 && responseCode < 500) {
    // 4xx - 客户端错误
    System.err.println("客户端错误: " + responseCode);
    processError(conn.getErrorStream());
} else if (responseCode >= 500) {
    // 5xx - 服务端错误
    System.err.println("服务端错误: " + responseCode);
    // 可重试
}
```

---

## 五、连接池与性能优化

### 5.1 什么是连接池？

**问题**: 什么是 Socket 连接池？为什么要使用连接池？

**答案**:

**连接池**:
- 预先创建并维护一组**可复用**的连接
- 避免频繁创建和销毁连接的开销
- 控制并发连接数量

**为什么要使用**:

| 问题 | 连接池解决方案 |
|------|----------------|
| 频繁创建连接开销大 | 复用已有连接 |
| 连接数过多导致资源耗尽 | 限制最大连接数 |
| 连接管理复杂 | 统一分配和回收 |
| 连接异常处理 | 健康检查和自动重建 |

**连接池核心参数**:
- **初始连接数**: 启动时创建的连接数
- **最大连接数**: 允许的最大连接数
- **连接超时**: 获取连接的最大等待时间
- **空闲超时**: 连接最大空闲时间

---

### 5.2 实现简单的连接池

**问题**: 如何实现一个简单的 Socket 连接池？

**答案**:

```java
public class SimpleConnectionPool {
    private final String host;
    private final int port;
    private final int maxConnections;
    private final BlockingQueue<Socket> pool;
    private final AtomicInteger currentSize = new AtomicInteger(0);
    
    public SimpleConnectionPool(String host, int port, int maxConnections) {
        this.host = host;
        this.port = port;
        this.maxConnections = maxConnections;
        this.pool = new LinkedBlockingQueue<>(maxConnections);
    }
    
    // 获取连接
    public Socket borrowConnection() throws IOException, InterruptedException {
        Socket socket = pool.poll();
        if (socket != null && isValid(socket)) {
            return socket;
        }
        
        if (currentSize.get() < maxConnections) {
            currentSize.incrementAndGet();
            return createConnection();
        }
        
        // 等待可用连接
        return pool.take();
    }
    
    // 归还连接
    public void returnConnection(Socket socket) {
        if (socket != null && isValid(socket)) {
            pool.offer(socket);
        } else {
            currentSize.decrementAndGet();
            closeQuietly(socket);
        }
    }
    
    private Socket createConnection() throws IOException {
        return new Socket(host, port);
    }
    
    private boolean isValid(Socket socket) {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }
    
    private void closeQuietly(Socket socket) {
        try {
            if (socket != null) socket.close();
        } catch (IOException ignored) {}
    }
}
```

---

### 5.3 Socket 性能优化

**问题**: 如何优化 Socket 网络通信性能？

**答案**:

**优化策略**:

1. **使用连接池**
```java
// 复用连接，避免频繁创建
ConnectionPool pool = new ConnectionPool(host, port, 10);
Socket socket = pool.borrowConnection();
// 使用...
pool.returnConnection(socket);
```

2. **启用 TCP_NODELAY**
```java
Socket socket = new Socket();
socket.setTcpNoDelay(true); // 禁用 Nagle 算法，减少延迟
```

3. **设置合适的缓冲区大小**
```java
socket.setSendBufferSize(64 * 1024);  // 64KB 发送缓冲区
socket.setReceiveBufferSize(64 * 1024); // 64KB 接收缓冲区
```

4. **使用缓冲流**
```java
BufferedInputStream bis = new BufferedInputStream(
    socket.getInputStream(), 8192);
BufferedOutputStream bos = new BufferedOutputStream(
    socket.getOutputStream(), 8192);
```

5. **批量读写**
```java
byte[] buffer = new byte[8192];
int len;
while ((len = input.read(buffer)) != -1) {
    output.write(buffer, 0, len);
}
```

6. **设置超时**
```java
socket.setSoTimeout(5000); // 读取超时 5秒
socket.connect(address, 3000); // 连接超时 3秒
```

---

## 六、高级网络编程

### 6.1 非阻塞 IO（NIO）

**问题**: 传统 Socket 编程有什么问题？NIO 如何解决？

**答案**:

**传统 Socket 的问题**:
- **阻塞式**: `accept()`、`read()` 都会阻塞线程
- **一连接一线程**: 高并发时线程数过多
- **资源浪费**: 线程大部分时间处于等待状态

**NIO 解决方案**:
- **非阻塞模式**: 线程不会被 IO 操作阻塞
- **Selector 多路复用**: 一个线程管理多个连接
- **Channel 和 Buffer**: 更高效的数据传输

**NIO 核心组件**:

```java
// 1. Selector - 多路复用器
Selector selector = Selector.open();

// 2. ServerSocketChannel - 服务端通道
ServerSocketChannel serverChannel = ServerSocketChannel.open();
serverChannel.bind(new InetSocketAddress(8080));
serverChannel.configureBlocking(false); // 非阻塞
serverChannel.register(selector, SelectionKey.OP_ACCEPT);

// 3. 事件循环
while (true) {
    selector.select(); // 阻塞等待就绪事件
    
    Set<SelectionKey> keys = selector.selectedKeys();
    for (SelectionKey key : keys) {
        if (key.isAcceptable()) {
            // 处理连接请求
            SocketChannel client = serverChannel.accept();
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);
        } else if (key.isReadable()) {
            // 处理读取事件
            SocketChannel client = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            client.read(buffer);
        }
    }
    keys.clear();
}
```

---

### 6.2 心跳机制

**问题**: 什么是心跳机制？为什么需要心跳？

**答案**:

**心跳机制**:
- 定期发送**探测消息**检测连接状态
- 确认对端是否存活
- 防止连接被中间设备（防火墙、NAT）断开

**为什么需要**:
- **检测死连接**: 及时发现对端崩溃或网络故障
- **保持连接活跃**: 防止被防火墙断开
- **快速故障转移**: 及时发现故障，切换到备用连接

**实现代码**:
```java
public class HeartbeatHandler {
    private final Socket socket;
    private final long heartbeatInterval = 30000; // 30秒
    private volatile boolean running = true;
    
    public void startHeartbeat() {
        // 发送心跳线程
        new Thread(() -> {
            while (running) {
                try {
                    sendHeartbeat();
                    Thread.sleep(heartbeatInterval);
                } catch (Exception e) {
                    handleDisconnect();
                    break;
                }
            }
        }).start();
        
        // 接收心跳响应线程
        new Thread(() -> {
            while (running) {
                try {
                    if (!receiveHeartbeat()) {
                        handleDisconnect();
                        break;
                    }
                } catch (Exception e) {
                    handleDisconnect();
                    break;
                }
            }
        }).start();
    }
    
    private void sendHeartbeat() throws IOException {
        OutputStream out = socket.getOutputStream();
        out.write("PING".getBytes());
        out.flush();
    }
    
    private boolean receiveHeartbeat() throws IOException {
        socket.setSoTimeout(60000); // 60秒超时
        InputStream in = socket.getInputStream();
        byte[] buffer = new byte[4];
        int read = in.read(buffer);
        return read > 0 && "PONG".equals(new String(buffer));
    }
    
    private void handleDisconnect() {
        running = false;
        System.err.println("连接断开，执行重连...");
        // 重连逻辑
    }
}
```

---

### 6.3 粘包和拆包问题

**问题**: 什么是粘包和拆包？如何解决？

**答案**:

**粘包（Packet Sticking）**:
- 多个小数据包被合并成一个大数据包发送
- 接收方一次读取到多个消息

**拆包（Packet Splitting）**:
- 一个大数据包被拆分成多个小包发送
- 接收方需要多次读取才能获取完整消息

**原因**:
- TCP 是**流式协议**，不保留消息边界
- 操作系统和网络的优化（Nagle 算法等）

**解决方案**:

1. **固定长度**: 每个消息固定字节数
```java
// 发送
byte[] message = padToLength(data, 100); // 填充到100字节
output.write(message);

// 接收
byte[] buffer = new byte[100];
input.readFully(buffer); // 确保读取100字节
```

2. **分隔符**: 使用特殊字符分隔消息
```java
// 发送
output.write((data + "\n").getBytes());

// 接收
BufferedReader reader = new BufferedReader(
    new InputStreamReader(input));
String line = reader.readLine(); // 按行读取
```

3. **长度前缀**: 消息头包含消息长度
```java
// 发送
byte[] data = message.getBytes();
ByteBuffer buffer = ByteBuffer.allocate(4 + data.length);
buffer.putInt(data.length); // 4字节长度前缀
buffer.put(data);
output.write(buffer.array());

// 接收
DataInputStream dis = new DataInputStream(input);
int length = dis.readInt(); // 先读长度
byte[] data = new byte[length];
dis.readFully(data); // 再读数据
```

**推荐方案**: **长度前缀**，最灵活可靠

---

## 七、常见问题与最佳实践

### 7.1 Socket 异常处理

**问题**: Socket 编程中常见的异常有哪些？如何处理？

**答案**:

| 异常 | 原因 | 处理建议 |
|------|------|----------|
| `ConnectException` | 连接被拒绝 | 检查服务端是否启动、端口是否正确 |
| `SocketTimeoutException` | 连接或读取超时 | 检查网络，增加超时时间，重试 |
| `SocketException` | 连接重置/关闭 | 对端关闭连接，清理资源 |
| `BindException` | 端口被占用 | 更换端口，检查是否有进程占用 |
| `UnknownHostException` | 无法解析主机名 | 检查 DNS，使用 IP 地址 |
| `IOException` | 通用 IO 错误 | 记录日志，根据具体情况处理 |

**异常处理示例**:
```java
try {
    Socket socket = new Socket();
    socket.connect(new InetSocketAddress(host, port), 5000);
    // 通信逻辑
} catch (ConnectException e) {
    System.err.println("连接被拒绝，请检查服务端状态");
} catch (SocketTimeoutException e) {
    System.err.println("连接超时，请检查网络");
    // 可重试
} catch (UnknownHostException e) {
    System.err.println("无法解析主机: " + host);
} catch (IOException e) {
    System.err.println("IO 错误: " + e.getMessage());
}
```

---

### 7.2 资源释放

**问题**: Socket 编程中如何正确释放资源？

**答案**:

**正确关闭顺序**:
```java
Socket socket = null;
InputStream in = null;
OutputStream out = null;

try {
    socket = new Socket(host, port);
    in = socket.getInputStream();
    out = socket.getOutputStream();
    // 通信逻辑
} catch (IOException e) {
    e.printStackTrace();
} finally {
    // 关闭顺序: 流 -> Socket
    closeQuietly(in);
    closeQuietly(out);
    closeQuietly(socket);
}

private void closeQuietly(Closeable closeable) {
    if (closeable != null) {
        try {
            closeable.close();
        } catch (IOException ignored) {}
    }
}
```

**try-with-resources 方式**:
```java
try (Socket socket = new Socket(host, port);
     BufferedReader in = new BufferedReader(
         new InputStreamReader(socket.getInputStream()));
     PrintWriter out = new PrintWriter(
         socket.getOutputStream(), true)) {
    
    // 通信逻辑
    
} catch (IOException e) {
    e.printStackTrace();
}
// 自动关闭，顺序相反（后开的先关）
```

---

### 7.3 面试题速查表

| 问题 | 核心要点 |
|------|----------|
| TCP vs UDP | TCP 可靠连接，UDP 无连接快速 |
| 三次握手 | SYN → SYN+ACK → ACK |
| 四次挥手 | FIN → ACK → FIN → ACK |
| Socket vs ServerSocket | 客户端 vs 服务端 |
| 多线程服务端 | 每个连接一个线程或使用线程池 |
| 连接池作用 | 复用连接，控制并发，提升性能 |
| 粘包拆包 | 固定长度、分隔符、长度前缀 |
| 心跳机制 | 定期探测，保持连接，检测故障 |
| NIO 优势 | 非阻塞，Selector 多路复用 |

---

**文档版本**: 1.0.0  
**最后更新**: 2026-03-26  
**适用 JDK**: Java 8+
