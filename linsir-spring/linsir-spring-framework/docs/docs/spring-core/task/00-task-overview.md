# 任务执行模块概述

## 1. 模块定位

任务执行（Task Execution）模块是 Spring Core 的基础设施之一，为整个 Spring 框架提供统一的异步任务执行能力。它定义了任务执行器的标准接口，支持同步和异步两种执行模式，是 Spring 异步编程和任务调度的基础。

## 2. 核心概念

### 2.1 任务执行器（TaskExecutor）

TaskExecutor 是任务执行模块的核心接口，定义了执行任务的标准方法：

```java
public interface TaskExecutor {
    void execute(Runnable task);
}
```

### 2.2 异步任务执行器（AsyncTaskExecutor）

扩展了 TaskExecutor，支持返回 Future 的异步执行：

```java
public interface AsyncTaskExecutor extends TaskExecutor {
    Future<?> submit(Runnable task);
    <T> Future<T> submit(Callable<T> task);
}
```

### 2.3 执行策略

| 策略 | 说明 | 适用场景 |
|------|------|----------|
| 同步执行 | 在当前线程直接执行任务 | 简单任务、无需并发 |
| 异步执行 | 在新线程或线程池中执行任务 | 耗时操作、IO密集型 |
| 延迟执行 | 延迟指定时间后执行 | 定时任务、延迟处理 |
| 周期执行 | 按固定周期重复执行 | 定时轮询、心跳检测 |

## 3. 核心能力矩阵

| 能力 | 核心类 | 解决的问题 | 使用频率 | 学习优先级 |
|------|--------|-----------|----------|-----------|
| **任务执行** | `TaskExecutor` | 统一任务执行接口 | 高 | 高 |
| **异步执行** | `AsyncTaskExecutor` | 异步任务和结果获取 | 高 | 高 |
| **线程池管理** | `ThreadPoolTaskExecutor` | 线程池配置和管理 | 中 | 中 |
| **任务调度** | `TaskScheduler` | 定时任务调度 | 中 | 中 |
| **异常处理** | `ErrorHandler` | 异步任务异常处理 | 中 | 中 |

## 4. 包结构全景

```
org.springframework.core.task
├── TaskExecutor.java              # 任务执行器接口
├── AsyncTaskExecutor.java         # 异步任务执行器接口
├── AsyncListenableTaskExecutor.java  # 支持回调的异步执行器
├── TaskRejectedException.java     # 任务拒绝异常
├── TaskTimeoutException.java      # 任务超时异常
└── support/
    ├── TaskExecutorAdapter.java   # 适配器模式实现
    ├── ConcurrentExecutorAdapter.java  # JDK Executor适配
    ├── SimpleAsyncTaskExecutor.java    # 简单异步执行器
    └── SyncTaskExecutor.java      # 同步执行器
```

## 5. 核心组件详解

### 5.1 TaskExecutor 接口

定义执行任务的基本契约：

- `execute(Runnable task)` - 执行无返回值的任务
- 实现类决定同步或异步执行策略

### 5.2 AsyncTaskExecutor 接口

扩展异步执行能力：

- `submit(Runnable task)` - 提交无返回值任务，返回 Future
- `submit(Callable<T> task)` - 提交有返回值任务
- 支持任务取消和结果获取

### 5.3 AsyncListenableTaskExecutor 接口

支持回调机制的异步执行：

- `submitListenable(Runnable task)` - 提交带回调的任务
- 通过 ListenableFuture 获取执行结果和回调

### 5.4 支持类

| 类名 | 功能 | 特点 |
|------|------|------|
| SimpleAsyncTaskExecutor | 为每个任务创建新线程 | 简单但资源消耗大 |
| SyncTaskExecutor | 同步执行 | 在当前线程执行 |
| ConcurrentExecutorAdapter | 适配 JDK Executor | 兼容标准线程池 |
| ThreadPoolTaskExecutor | 线程池执行器 | 生产环境推荐使用 |

## 6. 与其他模块的关系

```mermaid
flowchart TB
    subgraph Task["spring-core/task 任务执行"]
        te[TaskExecutor]
        ate[AsyncTaskExecutor]
        sched[TaskScheduler]
    end

    subgraph Context["spring-context"]
        async[@Async注解支持]
        sched2[@Scheduled注解支持]
    end

    subgraph Beans["spring-beans"]
        lifecycle[Bean生命周期]
    end

    subgraph Web["spring-web"]
        asyncWeb[异步Web请求]
    end

    te --> async
    ate --> async
    sched --> sched2
    te --> lifecycle
    ate --> asyncWeb
```

## 7. 使用场景

### 7.1 异步方法执行

```java
@Service
public class OrderService {
    
    @Async
    public CompletableFuture<Order> processOrderAsync(Order order) {
        // 异步处理订单
        return CompletableFuture.completedFuture(processedOrder);
    }
}
```

### 7.2 定时任务调度

```java
@Component
public class DataSyncTask {
    
    @Scheduled(fixedRate = 60000)
    public void syncData() {
        // 每分钟同步数据
    }
}
```

### 7.3 事件异步处理

```java
@Component
public class OrderEventListener {
    
    @EventListener
    @Async
    public void handleOrderCreated(OrderCreatedEvent event) {
        // 异步处理订单创建事件
    }
}
```

## 8. 设计要点

### 8.1 接口隔离原则

- TaskExecutor 提供最基础的能力
- AsyncTaskExecutor 扩展异步能力
- AsyncListenableTaskExecutor 增加回调支持
- 使用者按需选择接口

### 8.2 适配器模式

- TaskExecutorAdapter 适配不同执行策略
- ConcurrentExecutorAdapter 兼容 JDK 标准 Executor
- 便于集成第三方线程池实现

### 8.3 异常处理

- TaskRejectedException - 任务被拒绝
- TaskTimeoutException - 任务执行超时
- 统一的异常体系便于问题定位

## 9. 学习路径

1. **基础阶段**
   - 理解 TaskExecutor 接口设计
   - 掌握同步 vs 异步执行的区别
   - 学习 SimpleAsyncTaskExecutor 使用

2. **进阶阶段**
   - 深入 AsyncTaskExecutor 和 Future
   - 理解线程池配置和优化
   - 学习异常处理策略

3. **高级阶段**
   - 自定义 TaskExecutor 实现
   - 与 Spring @Async 集成
   - 实现复杂的任务调度策略

## 10. 参考资源

- [Spring Framework - Task Execution](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/core/task/package-summary.html)
- [Spring @Async 注解](https://docs.spring.io/spring-framework/docs/current/reference/html/integration.html#scheduling-annotation-support-async)
- [Spring TaskScheduler](https://docs.spring.io/spring-framework/docs/current/reference/html/integration.html#scheduling-task-scheduler)
