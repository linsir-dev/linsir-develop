# 任务执行模块测试说明

## 1. 测试概述

任务执行模块的测试采用 JUnit 5 框架，共包含 7 个测试类，95 个测试用例，覆盖了任务执行的各个方面。

## 2. 测试结构

```
test/java/com/linsir/spring/framework/spring_core/task/
├── core/
│   ├── TaskExecutorTest.java              # 14个测试
│   ├── AsyncTaskExecutorTest.java         # 17个测试
│   └── ListenableFutureTest.java          # 7个测试
├── support/
│   └── ThreadPoolTaskExecutorTest.java    # 17个测试
├── scheduler/
│   ├── TaskSchedulerTest.java             # 16个测试
│   └── CronTaskSchedulerTest.java         # 14个测试
└── exception/
    └── TaskExceptionTest.java             # 10个测试
```

## 3. 测试类详解

### 3.1 TaskExecutorTest

测试 `TaskExecutor` 接口的各种实现和执行行为。

#### 测试分组

| 分组 | 测试方法 | 说明 |
|------|----------|------|
| 同步执行 | `testSyncTaskExecutorExecute` | 测试同步执行器执行任务 |
| | `testSyncTaskExecutorMultipleTasks` | 测试同步执行器执行多个任务 |
| 异步执行 | `testSimpleAsyncTaskExecutorExecute` | 测试简单异步执行器 |
| | `testSimpleAsyncTaskExecutorMultipleTasks` | 测试异步执行器执行多个任务 |
| 异常处理 | `testExecuteNullTaskThrowsException` | 测试执行null任务抛出异常 |
| | `testSimpleAsyncExecutorInactive` | 测试关闭后的执行器拒绝任务 |
| 单例模式 | `testSyncExecutorIsSingleton` | 测试同步执行器是单例 |
| 配置 | `testSimpleAsyncExecutorThreadNamePrefix` | 测试线程名前缀设置 |
| | `testSimpleAsyncExecutorConstructorWithPrefix` | 测试带前缀的构造函数 |

#### 关键测试场景

```java
@Test
void testSyncTaskExecutorExecute() {
    // 测试同步执行器执行任务
    SyncTaskExecutor executor = SyncTaskExecutor.getInstance();
    AtomicInteger counter = new AtomicInteger(0);

    executor.execute(() -> counter.incrementAndGet());

    // 同步执行，计数器应立即增加
    assertEquals(1, counter.get());
}
```

### 3.2 AsyncTaskExecutorTest

测试 `AsyncTaskExecutor` 接口的异步执行和 Future 结果获取。

#### 测试分组

| 分组 | 测试方法 | 说明 |
|------|----------|------|
| 提交任务 | `testSubmitRunnable` | 测试提交 Runnable |
| | `testSubmitCallable` | 测试提交 Callable |
| | `testSubmitRunnableWithResult` | 测试提交带结果的 Runnable |
| Future操作 | `testFutureIsDone` | 测试 Future 完成状态 |
| | `testFutureCancel` | 测试取消任务 |
| 异常处理 | `testSubmitNullRunnableThrowsException` | 测试提交 null 抛出异常 |
| | `testExceptionInCallable` | 测试 Callable 中抛出异常 |
| 配置 | `testTaskTimeout` | 测试任务超时设置 |
| | `testThreadPoolConfiguration` | 测试线程池配置 |

#### 关键测试场景

```java
@Test
void testSubmitCallable() throws Exception {
    // 测试提交 Callable 任务
    Callable<Integer> task = () -> 42;

    Future<Integer> future = executor.submit(task);

    Integer result = future.get(2, TimeUnit.SECONDS);
    assertEquals(42, result);
}
```

### 3.3 ListenableFutureTest

测试 `ListenableFuture` 的回调机制。

#### 测试分组

| 分组 | 测试方法 | 说明 |
|------|----------|------|
| 成功回调 | `testSuccessCallback` | 测试成功回调 |
| | `testMultipleSuccessCallbacks` | 测试多个成功回调 |
| 失败回调 | `testFailureCallback` | 测试失败回调 |
| 组合回调 | `testBothCallbacks` | 测试同时添加成功和失败回调 |
| 其他 | `testRunnableListenableFuture` | 测试 Runnable 的 ListenableFuture |
| | `testListenableFutureGet` | 测试 get 方法 |
| | `testListenableFutureCancel` | 测试取消 |

#### 关键测试场景

```java
@Test
void testSuccessCallback() throws InterruptedException {
    // 测试成功回调
    CountDownLatch latch = new CountDownLatch(1);
    AtomicReference<String> resultRef = new AtomicReference<>();

    ListenableFuture<String> future = executor.submitListenable(() -> "Success");

    future.addSuccessCallback(result -> {
        resultRef.set(result);
        latch.countDown();
    });

    boolean completed = latch.await(2, TimeUnit.SECONDS);
    assertTrue(completed);
    assertEquals("Success", resultRef.get());
}
```

### 3.4 TaskSchedulerTest

测试 `TaskScheduler` 的各种调度功能。

#### 测试分组

| 分组 | 测试方法 | 说明 |
|------|----------|------|
| 延迟执行 | `testScheduleWithDelay` | 测试延迟调度 |
| 固定频率 | `testScheduleAtFixedRate` | 测试固定频率调度 |
| 固定延迟 | `testScheduleWithFixedDelay` | 测试固定延迟调度 |
| 指定时间 | `testScheduleAtSpecificTime` | 测试在指定时间调度 |
| 立即执行 | `testExecuteImmediately` | 测试立即执行 |
| 取消 | `testScheduleCancel` | 测试取消调度任务 |
| 关闭 | `testShutdown` | 测试关闭调度器 |
| 异常 | `testScheduleNullTaskThrowsException` | 测试 null 任务抛出异常 |
| | `testNegativeDelayThrowsException` | 测试负延迟抛出异常 |

#### 关键测试场景

```java
@Test
void testScheduleWithDelay() throws InterruptedException {
    // 测试延迟调度
    CountDownLatch latch = new CountDownLatch(1);
    AtomicInteger counter = new AtomicInteger(0);
    long startTime = System.currentTimeMillis();

    ScheduledFuture<?> future = scheduler.scheduleWithDelay(() -> {
        counter.incrementAndGet();
        latch.countDown();
    }, 200);

    boolean completed = latch.await(1, TimeUnit.SECONDS);
    long elapsed = System.currentTimeMillis() - startTime;

    assertTrue(completed);
    assertEquals(1, counter.get());
    assertTrue(elapsed >= 150);
}
```

### 3.5 CronTaskSchedulerTest

测试 `CronTaskScheduler` 的 Cron 表达式调度功能。

#### 测试分组

| 分组 | 测试方法 | 说明 |
|------|----------|------|
| Cron调度 | `testScheduleWithCronEverySecond` | 测试每秒执行的 Cron 任务 |
| | `testScheduleWithCronSpecificSecond` | 测试在特定秒数执行 |
| 取消 | `testCancelNonExistentTask` | 测试取消不存在的任务 |
| 异常 | `testScheduleWithInvalidCronExpression` | 测试无效的 Cron 表达式 |
| | `testScheduleWithNullCronExpression` | 测试 null Cron 表达式 |
| | `testScheduleWithNullTask` | 测试 null 任务 |
| 关闭 | `testCronSchedulerShutdown` | 测试关闭调度器 |
| 多任务 | `testMultipleCronTasks` | 测试多个 Cron 任务 |

#### 关键测试场景

```java
@Test
void testScheduleWithCronEverySecond() throws InterruptedException {
    // 测试每秒执行的 Cron 任务
    CountDownLatch latch = new CountDownLatch(2);
    AtomicInteger counter = new AtomicInteger(0);

    String taskId = scheduler.scheduleWithCron(() -> {
        counter.incrementAndGet();
        latch.countDown();
    }, "* * * * * *");

    boolean completed = latch.await(3, TimeUnit.SECONDS);
    assertTrue(completed);
    assertTrue(counter.get() >= 2);

    scheduler.cancelTask(taskId);
}
```

### 3.6 TaskExceptionTest

测试 `TaskRejectedException` 和 `TaskTimeoutException`。

#### 测试分组

| 分组 | 测试方法 | 说明 |
|------|----------|------|
| TaskRejectedException | `testTaskRejectedExceptionDefaultConstructor` | 测试默认构造函数 |
| | `testTaskRejectedExceptionWithMessage` | 测试带消息的构造函数 |
| | `testTaskRejectedExceptionWithMessageAndCause` | 测试带消息和原因的构造函数 |
| | `testTaskRejectedExceptionWithCause` | 测试只带原因的构造函数 |
| TaskTimeoutException | `testTaskTimeoutExceptionDefaultConstructor` | 测试默认构造函数 |
| | `testTaskTimeoutExceptionWithMessage` | 测试带消息的构造函数 |
| | `testTaskTimeoutExceptionWithMessageAndCause` | 测试带消息和原因的构造函数 |
| | `testTaskTimeoutExceptionWithCause` | 测试只带原因的构造函数 |
| 其他 | `testExceptionChaining` | 测试异常链 |

#### 关键测试场景

```java
@Test
void testTaskRejectedExceptionWithMessageAndCause() {
    // 测试带消息和原因的构造函数
    String message = "Task was rejected";
    Throwable cause = new RuntimeException("Original error");
    TaskRejectedException exception = new TaskRejectedException(message, cause);

    assertEquals(message, exception.getMessage());
    assertSame(cause, exception.getCause());
}
```

### 3.7 ThreadPoolTaskExecutorTest

测试 `ThreadPoolTaskExecutor` 的各种功能。

#### 测试分组

| 分组 | 测试方法 | 说明 |
|------|----------|------|
| 基本功能 | `testExecute` | 测试执行任务 |
| | `testSubmitRunnable` | 测试提交 Runnable |
| | `testSubmitCallable` | 测试提交 Callable |
| 关闭 | `testShutdown` | 测试关闭 |
| | `testShutdownNow` | 测试立即关闭 |
| | `testIsTerminated` | 测试终止状态 |
| 配置 | `testConfigurationAfterInitialization` | 测试初始化后的配置修改 |
| | `testLazyInitialization` | 测试延迟初始化 |
| 并发 | `testConcurrentExecution` | 测试并发执行 |
| 异常 | `testExecuteNullThrowsException` | 测试执行 null 抛出异常 |

#### 关键测试场景

```java
@Test
void testConcurrentExecution() throws InterruptedException {
    // 测试并发执行
    int taskCount = 20;
    CountDownLatch latch = new CountDownLatch(taskCount);
    AtomicInteger counter = new AtomicInteger(0);

    for (int i = 0; i < taskCount; i++) {
        executor.execute(() -> {
            counter.incrementAndGet();
            latch.countDown();
        });
    }

    boolean completed = latch.await(5, TimeUnit.SECONDS);
    assertTrue(completed);
    assertEquals(taskCount, counter.get());
}
```

## 4. 测试工具类

### 4.1 CountDownLatch

用于等待异步任务完成：

```java
CountDownLatch latch = new CountDownLatch(1);
executor.execute(() -> {
    // 任务逻辑
    latch.countDown();
});
boolean completed = latch.await(2, TimeUnit.SECONDS);
```

### 4.2 AtomicInteger / AtomicReference

用于在 Lambda 表达式中修改变量：

```java
AtomicInteger counter = new AtomicInteger(0);
executor.execute(() -> counter.incrementAndGet());
assertEquals(1, counter.get());
```

### 4.3 Future

用于获取异步任务的执行结果：

```java
Future<Integer> future = executor.submit(() -> 42);
Integer result = future.get(2, TimeUnit.SECONDS);
assertEquals(42, result);
```

## 5. 测试策略

### 5.1 单元测试

每个类都有对应的单元测试，测试其所有公共方法。

### 5.2 集成测试

测试多个组件的协同工作，如 `CronTaskScheduler` 使用 `ConcurrentTaskScheduler`。

### 5.3 异常测试

测试各种异常情况，如 null 参数、无效配置等。

### 5.4 并发测试

测试多线程环境下的正确性，如并发任务执行。

## 6. 运行测试

### 6.1 运行所有测试

```bash
mvn test -Dtest="TaskExecutorTest,AsyncTaskExecutorTest,ListenableFutureTest,TaskSchedulerTest,CronTaskSchedulerTest,TaskExceptionTest,ThreadPoolTaskExecutorTest" -pl linsir-spring/linsir-spring-framework
```

### 6.2 运行单个测试类

```bash
mvn test -Dtest=TaskExecutorTest -pl linsir-spring/linsir-spring-framework
```

### 6.3 运行单个测试方法

```bash
mvn test -Dtest=TaskExecutorTest#testSyncTaskExecutorExecute -pl linsir-spring/linsir-spring-framework
```

## 7. 测试覆盖

| 模块 | 测试类 | 测试数 | 覆盖范围 |
|------|--------|--------|----------|
| 核心接口 | TaskExecutorTest | 14 | execute, 同步/异步 |
| 异步执行 | AsyncTaskExecutorTest | 17 | submit, Future, 回调 |
| 回调机制 | ListenableFutureTest | 7 | 成功/失败回调 |
| 线程池 | ThreadPoolTaskExecutorTest | 17 | 线程池配置, 并发 |
| 调度器 | TaskSchedulerTest | 16 | 延迟, 固定频率, 固定延迟 |
| Cron调度 | CronTaskSchedulerTest | 14 | Cron表达式, 多任务 |
| 异常 | TaskExceptionTest | 10 | 异常构造, 异常链 |

**总计: 95 个测试**
