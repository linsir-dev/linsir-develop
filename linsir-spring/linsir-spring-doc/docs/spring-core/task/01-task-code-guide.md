# 任务执行模块代码说明

## 1. 模块概述

任务执行模块是 Spring Core 的基础设施之一，为整个 Spring 框架提供统一的异步任务执行能力。它定义了任务执行器的标准接口，支持同步和异步两种执行模式，是 Spring 异步编程和任务调度的基础。

## 2. 包结构

```
spring_core/task/
├── core/                           # 核心接口
│   ├── TaskExecutor.java           # 任务执行器接口
│   ├── AsyncTaskExecutor.java      # 异步任务执行器接口
│   ├── AsyncListenableTaskExecutor.java  # 支持回调的异步执行器
│   └── ListenableFuture.java       # 可监听的未来结果接口
├── support/                        # 支持类
│   ├── SyncTaskExecutor.java       # 同步执行器
│   ├── SimpleAsyncTaskExecutor.java # 简单异步执行器
│   └── ThreadPoolTaskExecutor.java # 线程池执行器
├── scheduler/                      # 任务调度
│   ├── TaskScheduler.java          # 任务调度器接口
│   ├── ConcurrentTaskScheduler.java # 并发任务调度器
│   └── CronTaskScheduler.java      # Cron表达式调度器
└── exception/                      # 异常类
    ├── TaskRejectedException.java  # 任务被拒绝异常
    └── TaskTimeoutException.java   # 任务超时异常
```

## 3. 核心类详解

### 3.1 TaskExecutor

`TaskExecutor` 是任务执行模块的最基础接口，定义了执行任务的标准方法。

#### 核心方法

| 方法 | 说明 |
|------|------|
| `execute(Runnable task)` | 执行给定的任务 |

#### 使用示例

```java
TaskExecutor executor = new SimpleAsyncTaskExecutor();
executor.execute(() -> {
    System.out.println("任务执行");
});
```

### 3.2 AsyncTaskExecutor

`AsyncTaskExecutor` 扩展了 `TaskExecutor`，支持返回 `Future` 的异步执行模式。

#### 核心方法

| 方法 | 说明 |
|------|------|
| `submit(Runnable task)` | 提交无返回值任务，返回 Future |
| `submit(Callable<T> task)` | 提交有返回值任务 |
| `submit(Runnable task, T result)` | 提交带结果的 Runnable |
| `setTaskTimeout(long timeout)` | 设置任务超时时间 |

#### 使用示例

```java
AsyncTaskExecutor executor = new ThreadPoolTaskExecutor();
executor.initialize();

// 提交 Callable 任务
Future<Integer> future = executor.submit(() -> {
    return 42;
});

Integer result = future.get();
```

### 3.3 AsyncListenableTaskExecutor

`AsyncListenableTaskExecutor` 扩展了 `AsyncTaskExecutor`，支持返回 `ListenableFuture` 的异步执行，允许添加成功和失败的回调。

#### 核心方法

| 方法 | 说明 |
|------|------|
| `submitListenable(Runnable task)` | 提交无返回值任务，返回 ListenableFuture |
| `submitListenable(Callable<T> task)` | 提交有返回值任务，返回 ListenableFuture |

#### 使用示例

```java
AsyncListenableTaskExecutor executor = new SimpleAsyncTaskExecutor();

ListenableFuture<String> future = executor.submitListenable(() -> "Hello");

future.addSuccessCallback(result -> {
    System.out.println("成功: " + result);
});

future.addFailureCallback(ex -> {
    System.out.println("失败: " + ex.getMessage());
});
```

### 3.4 ListenableFuture

`ListenableFuture` 扩展了 `Future` 接口，支持添加回调函数。

#### 核心方法

| 方法 | 说明 |
|------|------|
| `addSuccessCallback(callback)` | 添加成功回调 |
| `addFailureCallback(callback)` | 添加失败回调 |
| `addCallbacks(success, failure)` | 同时添加成功和失败回调 |

### 3.5 SyncTaskExecutor

`SyncTaskExecutor` 是同步任务执行器，在当前线程直接执行任务。

#### 特点

- 单例模式实现
- 在当前线程同步执行
- 适用于调试和测试

#### 使用示例

```java
TaskExecutor executor = SyncTaskExecutor.getInstance();

// 同步执行
executor.execute(() -> {
    System.out.println("在当前线程执行");
});
```

### 3.6 SimpleAsyncTaskExecutor

`SimpleAsyncTaskExecutor` 是简单异步执行器，为每个任务创建一个新线程。

#### 特点

- 为每个任务创建新线程
- 支持线程名前缀配置
- 支持回调机制
- 适用于任务数量较少的场景

#### 配置选项

| 属性 | 说明 | 默认值 |
|------|------|--------|
| `threadNamePrefix` | 线程名前缀 | "SimpleAsyncTaskExecutor-" |

#### 使用示例

```java
SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("MyThread-");

executor.execute(() -> {
    System.out.println("在新线程执行");
});

executor.shutdown();
```

### 3.7 ThreadPoolTaskExecutor

`ThreadPoolTaskExecutor` 是基于线程池的任务执行器，是生产环境推荐使用的执行器。

#### 特点

- 基于 Java ThreadPoolExecutor
- 支持核心线程数、最大线程数配置
- 支持队列容量配置
- 支持线程存活时间配置

#### 配置选项

| 属性 | 说明 | 默认值 |
|------|------|--------|
| `corePoolSize` | 核心线程数 | 1 |
| `maxPoolSize` | 最大线程数 | 1 |
| `queueCapacity` | 队列容量 | 0 |
| `keepAliveSeconds` | 线程存活时间（秒） | 60 |
| `threadNamePrefix` | 线程名前缀 | "ThreadPoolTaskExecutor-" |

#### 使用示例

```java
ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
executor.setCorePoolSize(5);
executor.setMaxPoolSize(10);
executor.setQueueCapacity(100);
executor.setThreadNamePrefix("MyPool-");
executor.initialize();

executor.execute(() -> {
    System.out.println("在线程池中执行");
});

executor.shutdown();
```

### 3.8 TaskScheduler

`TaskScheduler` 是任务调度器接口，支持多种调度模式。

#### 核心方法

| 方法 | 说明 |
|------|------|
| `schedule(task, startTime)` | 在指定时间执行 |
| `scheduleWithDelay(task, delay)` | 延迟执行 |
| `scheduleAtFixedRate(task, period)` | 固定频率执行 |
| `scheduleWithFixedDelay(task, delay)` | 固定延迟执行 |

### 3.9 ConcurrentTaskScheduler

`ConcurrentTaskScheduler` 是基于 `ScheduledThreadPoolExecutor` 的任务调度器实现。

#### 特点

- 基于 Java ScheduledThreadPoolExecutor
- 支持延迟执行
- 支持固定频率执行
- 支持固定延迟执行

#### 使用示例

```java
ConcurrentTaskScheduler scheduler = new ConcurrentTaskScheduler(2);
scheduler.initialize();

// 延迟执行
scheduler.scheduleWithDelay(() -> {
    System.out.println("延迟执行");
}, 5000);

// 固定频率执行
scheduler.scheduleAtFixedRate(() -> {
    System.out.println("每5秒执行一次");
}, 5000);

scheduler.shutdown();
```

### 3.10 CronTaskScheduler

`CronTaskScheduler` 支持 Unix Cron 表达式格式的任务调度。

#### 特点

- 支持 Cron 表达式
- 支持任务取消
- 支持多任务管理

#### Cron 表达式格式

格式：`秒 分 时 日 月 周`

| 字段 | 范围 | 说明 |
|------|------|------|
| 秒 | 0-59 | |
| 分 | 0-59 | |
| 时 | 0-23 | |
| 日 | 1-31 | |
| 月 | 1-12 | |
| 周 | 0-7 | 0和7都表示周日 |

#### 使用示例

```java
CronTaskScheduler scheduler = new CronTaskScheduler();

// 每秒执行
String taskId = scheduler.scheduleWithCron(() -> {
    System.out.println("每秒执行");
}, "* * * * * *");

// 取消任务
scheduler.cancelTask(taskId);

scheduler.shutdown();
```

### 3.11 异常类

#### TaskRejectedException

当任务执行器无法接受新任务时抛出。

```java
try {
    executor.execute(task);
} catch (TaskRejectedException e) {
    System.out.println("任务被拒绝: " + e.getMessage());
}
```

#### TaskTimeoutException

当任务执行时间超过设定的超时时间时抛出。

```java
try {
    future.get(5, TimeUnit.SECONDS);
} catch (TimeoutException e) {
    throw new TaskTimeoutException("任务执行超时", e);
}
```

## 4. 设计要点

### 4.1 接口隔离原则

- `TaskExecutor` 提供最基础的能力
- `AsyncTaskExecutor` 扩展异步能力
- `AsyncListenableTaskExecutor` 增加回调支持
- 使用者按需选择接口

### 4.2 适配器模式

- 支持不同的执行策略
- 便于集成第三方线程池实现
- 统一的异常处理

### 4.3 回调机制

- `ListenableFuture` 支持回调
- 支持多个回调函数
- 支持任务完成后添加回调（立即执行）

## 5. 使用场景

### 5.1 异步方法执行

```java
@Service
public class OrderService {
    
    private final AsyncTaskExecutor executor;
    
    public OrderService(AsyncTaskExecutor executor) {
        this.executor = executor;
    }
    
    public Future<Order> processOrderAsync(Order order) {
        return executor.submit(() -> {
            // 处理订单
            return processedOrder;
        });
    }
}
```

### 5.2 定时任务调度

```java
@Component
public class DataSyncTask {
    
    private final TaskScheduler scheduler;
    
    public DataSyncTask(TaskScheduler scheduler) {
        this.scheduler = scheduler;
    }
    
    @PostConstruct
    public void init() {
        scheduler.scheduleAtFixedRate(() -> {
            // 同步数据
        }, 60000);
    }
}
```

### 5.3 事件异步处理

```java
@Component
public class OrderEventListener {
    
    private final AsyncListenableTaskExecutor executor;
    
    public OrderEventListener(AsyncListenableTaskExecutor executor) {
        this.executor = executor;
    }
    
    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        ListenableFuture<?> future = executor.submitListenable(() -> {
            // 异步处理事件
        });
        
        future.addFailureCallback(ex -> {
            logger.error("事件处理失败", ex);
        });
    }
}
```

## 6. 线程安全

所有执行器实现都是线程安全的：

- `SyncTaskExecutor`：无状态，线程安全
- `SimpleAsyncTaskExecutor`：使用原子变量，线程安全
- `ThreadPoolTaskExecutor`：基于 ThreadPoolExecutor，线程安全
- `ConcurrentTaskScheduler`：基于 ScheduledThreadPoolExecutor，线程安全

## 7. 性能考虑

### 7.1 执行器选择

| 执行器 | 适用场景 | 性能特点 |
|--------|----------|----------|
| SyncTaskExecutor | 测试、简单任务 | 无开销 |
| SimpleAsyncTaskExecutor | 少量任务 | 创建线程开销大 |
| ThreadPoolTaskExecutor | 生产环境 | 线程复用，性能高 |

### 7.2 线程池配置

```java
ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

// CPU密集型任务
executor.setCorePoolSize(Runtime.getRuntime().availableProcessors());
executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors() * 2);

// IO密集型任务
executor.setCorePoolSize(Runtime.getRuntime().availableProcessors() * 2);
executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors() * 4);
```
