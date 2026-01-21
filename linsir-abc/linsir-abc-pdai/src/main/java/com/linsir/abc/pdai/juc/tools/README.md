# JUC Tools类示例

## 功能说明

本示例代码展示了Java并发包（JUC）中常用的Tools类的使用方法和特性，包括：

1. **CountDownLatch**：倒计时门闩，用于等待多个线程完成任务
2. **CyclicBarrier**：循环屏障，用于多个线程互相等待，达到屏障后同时继续执行
3. **Semaphore**：信号量，用于控制对共享资源的访问数量
4. **Exchanger**：交换器，用于两个线程之间交换数据

## 示例代码结构

- **JUCToolsDemo.java**：包含四个内部类，分别演示不同的Tools类：
  - **CountDownLatchDemo**：演示CountDownLatch的使用，等待多个任务完成
  - **CyclicBarrierDemo**：演示CyclicBarrier的使用，多个线程互相等待
  - **SemaphoreDemo**：演示Semaphore的使用，控制资源访问数量
  - **ExchangerDemo**：演示Exchanger的使用，两个线程交换数据

## 运行说明

1. 编译并运行JUCToolsDemo.java文件
2. 观察控制台输出，了解不同Tools类的使用方法和效果
3. 注意CountDownLatch的倒计时机制
4. 注意CyclicBarrier的屏障机制和回调功能
5. 注意Semaphore的资源控制机制
6. 注意Exchanger的数据交换机制

## 核心知识点

- **CountDownLatch**：
  - 初始化时设置计数器
  - 线程完成任务后调用countDown()方法减少计数器
  - 主线程调用await()方法等待计数器减为0
  - 计数器减为0后，主线程继续执行
  - 只能使用一次，计数器不能重置

- **CyclicBarrier**：
  - 初始化时设置参与线程数量
  - 线程到达屏障时调用await()方法等待
  - 所有线程到达屏障后，同时继续执行
  - 可以设置回调任务，在所有线程到达屏障时执行
  - 可以重复使用，屏障可以重置

- **Semaphore**：
  - 初始化时设置可用资源数量
  - 线程获取资源时调用acquire()方法
  - 线程释放资源时调用release()方法
  - 控制同时访问共享资源的线程数量
  - 支持公平和非公平模式

- **Exchanger**：
  - 用于两个线程之间交换数据
  - 线程调用exchange()方法交换数据，同时等待对方
  - 两个线程都到达exchange()方法时，交换数据并继续执行
  - 支持超时交换

## 示例场景

- **CountDownLatchDemo**：模拟了多个任务线程执行任务，主线程等待所有任务完成后继续执行
- **CyclicBarrierDemo**：模拟了多个选手到达起跑点，所有选手到达后同时开始比赛
- **SemaphoreDemo**：模拟了多个线程竞争有限的资源，信号量控制同时访问资源的线程数量
- **ExchangerDemo**：模拟了两个线程之间交换数据的过程

## 优缺点对比

| Tools类 | 优点 | 缺点 |
|--------|------|------|
| CountDownLatch | 简单易用，适合等待多个任务完成 | 计数器不能重置，只能使用一次 |
| CyclicBarrier | 可以重复使用，支持回调功能 | 所有线程必须到达屏障才能继续，灵活性较低 |
| Semaphore | 灵活控制资源访问数量 | 需要手动释放资源，使用不当可能导致资源泄漏 |
| Exchanger | 简单直接地在两个线程间交换数据 | 只适用于两个线程的场景，使用范围有限 |

## 最佳实践

1. **选择合适的Tools类**：
   - 等待多个任务完成：使用CountDownLatch
   - 多个线程互相等待：使用CyclicBarrier
   - 控制资源访问数量：使用Semaphore
   - 两个线程交换数据：使用Exchanger

2. **使用注意事项**：
   - CountDownLatch：确保所有线程都调用countDown()方法，避免主线程永久等待
   - CyclicBarrier：注意线程中断或超时可能导致屏障损坏
   - Semaphore：始终在finally块中释放资源，避免资源泄漏
   - Exchanger：注意处理超时情况，避免线程永久等待

3. **性能考虑**：
   - 合理设置计数器、屏障数量和信号量大小
   - 避免过度使用这些工具，增加系统复杂性
   - 对于简单场景，考虑使用更简单的同步机制

## 注意事项

- CountDownLatch和CyclicBarrier的区别：
  - CountDownLatch是主线程等待多个子线程完成，子线程之间不需要互相等待
  - CyclicBarrier是多个线程互相等待，到达屏障后同时继续执行
- Semaphore的acquire()和release()方法必须成对使用
- Exchanger只能在两个线程之间交换数据，不支持多个线程
- 这些工具类都是线程安全的，可以在多线程环境中安全使用