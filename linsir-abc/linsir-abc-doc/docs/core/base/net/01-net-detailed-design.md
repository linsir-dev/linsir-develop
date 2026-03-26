# java.net 包详细设计文档

## 一、模块概述

**包路径**: `com.linsir.abc.core.base.net`

**包含子包**:
- `socket` - Socket 编程
- `url` - URL 处理

**类数**: 6个

---

## 二、Socket 编程

**包路径**: `com.linsir.abc.core.base.net.socket`

| 类名 | 功能描述 | 核心方法 |
|------|----------|----------|
| `SocketServerBuilder` | Socket 服务端构建 | `bind()`, `accept()`, `handleClient()` |
| `SocketConnectionPool` | Socket 连接池 | `borrowConnection()`, `returnConnection()` |
| `DatagramCommunicator` | 数据报通信 | `send()`, `receive()` |
| `MulticastGroupManager` | 多播组管理 | `joinGroup()`, `leaveGroup()` |

**设计要点**:
- ServerSocket 和 Socket 的使用
- TCP 三次握手和四次挥手
- 连接池的资源管理
- DatagramSocket 和 DatagramPacket
- UDP 的无连接特性
- 多播和广播的实现

---

## 三、URL 处理

**包路径**: `com.linsir.abc.core.base.net.url`

| 类名 | 功能描述 | 核心方法 |
|------|----------|----------|
| `UrlResourceFetcher` | URL 资源获取 | `openConnection()`, `getInputStream()` |
| `HttpConnectionManager` | HTTP 连接管理 | `setRequestMethod()`, `getResponseCode()` |

**设计要点**:
- URL 和 URI 的区别
- URLConnection 的使用
- HTTP 请求的发送和响应处理

---

## 四、完整类名列表

| 序号 | 完整类名 |
|------|----------|
| 1 | `com.linsir.abc.core.base.net.socket.SocketServerBuilder` |
| 2 | `com.linsir.abc.core.base.net.socket.SocketConnectionPool` |
| 3 | `com.linsir.abc.core.base.net.socket.DatagramCommunicator` |
| 4 | `com.linsir.abc.core.base.net.socket.MulticastGroupManager` |
| 5 | `com.linsir.abc.core.base.net.url.UrlResourceFetcher` |
| 6 | `com.linsir.abc.core.base.net.url.HttpConnectionManager` |

---

**文档版本**: 1.0.0  
**最后更新**: 2026-03-26
