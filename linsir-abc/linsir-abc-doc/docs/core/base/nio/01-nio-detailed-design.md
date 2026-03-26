# java.nio 包详细设计文档

## 一、模块概述

**包路径**: `com.linsir.abc.core.base.nio`

**包含子包**:
- `buffer` - Buffer 操作
- `channel` - Channel 通信
- `selector` - Selector 多路复用

**类数**: 6个

---

## 二、Buffer 操作

**包路径**: `com.linsir.abc.core.base.nio.buffer`

| 类名 | 功能描述 | 核心方法 |
|------|----------|----------|
| `BufferStateManager` | Buffer 状态管理 | `flip()`, `clear()`, `rewind()`, `compact()` |
| `ByteBufferAllocator` | ByteBuffer 分配 | `allocate()`, `allocateDirect()` |

**设计要点**:
- Buffer 的四个核心属性（mark, position, limit, capacity）
- 直接缓冲区 vs 堆缓冲区
- Buffer 状态转换（clear -> put -> flip -> get）

---

## 三、Channel 通信

**包路径**: `com.linsir.abc.core.base.nio.channel`

| 类名 | 功能描述 | 核心方法 |
|------|----------|----------|
| `FileChannelTransfer` | 文件通道传输 | `transferTo()`, `transferFrom()`, `map()` |
| `SocketChannelCommunication` | Socket 通道通信 | `connect()`, `read()`, `write()` |

**设计要点**:
- Channel 的双向通信能力
- 零拷贝传输（transferTo/transferFrom）
- 内存映射文件（MappedByteBuffer）

---

## 四、Selector 多路复用

**包路径**: `com.linsir.abc.core.base.nio.selector`

| 类名 | 功能描述 | 核心方法 |
|------|----------|----------|
| `SelectorMultiplexer` | 选择器多路复用 | `select()`, `selectedKeys()` |
| `NonBlockingServer` | 非阻塞服务器 | `accept()`, `register()`, `handleRead()`, `handleWrite()` |

**设计要点**:
- Selector 的注册和选择
- SelectionKey 的兴趣集合
- 非阻塞 IO 的事件驱动模型

---

## 五、完整类名列表

| 序号 | 完整类名 |
|------|----------|
| 1 | `com.linsir.abc.core.base.nio.buffer.BufferStateManager` |
| 2 | `com.linsir.abc.core.base.nio.buffer.ByteBufferAllocator` |
| 3 | `com.linsir.abc.core.base.nio.channel.FileChannelTransfer` |
| 4 | `com.linsir.abc.core.base.nio.channel.SocketChannelCommunication` |
| 5 | `com.linsir.abc.core.base.nio.selector.SelectorMultiplexer` |
| 6 | `com.linsir.abc.core.base.nio.selector.NonBlockingServer` |

---

**文档版本**: 1.0.0  
**最后更新**: 2026-03-26
