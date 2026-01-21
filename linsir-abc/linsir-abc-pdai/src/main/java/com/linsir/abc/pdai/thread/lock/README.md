# 锁机制示例

## 功能说明

本示例代码展示了Java中两种主要的锁机制：synchronized关键字和ReentrantLock类，包括它们的基本使用、高级功能和对比。具体功能包括：

1. **synchronized关键字**：演示了方法级别、代码块级别和静态方法级别的synchronized使用
2. **ReentrantLock类**：演示了基本使用、可重入性、可中断性、超时获取和公平锁等特性
3. **锁机制对比**：对比了synchronized和ReentrantLock的各种特性和适用场景
4. **条件变量**：演示了ReentrantLock的条件变量功能，实现了生产者-消费者模式

## 示例代码结构

- **LockDemo.java**：包含四个内部类，分别演示不同的锁机制功能：
  - **SynchronizedDemo**：演示synchronized关键字的使用
  - **ReentrantLockDemo**：演示ReentrantLock的各种特性
  - **LockComparisonDemo**：对比synchronized和ReentrantLock
  - **ConditionDemo**：演示ReentrantLock的条件变量功能

## 运行说明

1. 编译并运行LockDemo.java文件
2. 观察控制台输出，了解不同锁机制的执行流程和效果
3. 注意synchronized和ReentrantLock的区别
4. 注意ReentrantLock的高级功能，如可中断性、超时获取和公平锁
5. 注意条件变量的使用方法和生产者-消费者模式的实现

## 核心知识点

- **synchronized关键字**：
  - 隐式获取和释放锁
  - 可重入性
  - 基于对象的内置锁
  - 不能中断获取锁的过程
  - 不能设置超时获取
  - 非公平锁

- **ReentrantLock类**：
  - 显式获取和释放锁（必须在finally块中释放）
  - 可重入性
  - 可中断的获取锁
  - 支持超时获取锁
  - 可配置公平锁或非公平锁
  - 支持多个条件变量
  - 可查询锁状态

- **条件变量**：
  - 通过ReentrantLock的newCondition()方法创建
  - 用于线程间的协作
  - 支持await()、signal()和signalAll()方法

## 示例场景

- **SynchronizedDemo**：模拟了多个线程对计数器的递增操作，展示了synchronized的基本用法
- **ReentrantLockDemo**：演示了ReentrantLock的各种特性，包括基本使用、可重入性、可中断性和公平锁
- **LockComparisonDemo**：对比了synchronized和ReentrantLock的各种特性
- **ConditionDemo**：实现了一个有界缓冲区的生产者-消费者模式，展示了条件变量的使用

## 优缺点对比

| 锁机制 | 优点 | 缺点 |
|-------|------|------|
| synchronized | 简单易用，代码简洁 | 功能有限，不支持高级特性 |
| ReentrantLock | 功能丰富，支持高级特性 | 代码复杂，需要手动释放锁 |

## 最佳实践

1. **选择锁机制**：
   - 低并发场景或代码简单时，优先使用synchronized
   - 高并发场景或需要高级特性时，使用ReentrantLock

2. **使用ReentrantLock的注意事项**：
   - 始终在finally块中释放锁，避免死锁
   - 合理使用tryLock()方法避免线程长时间阻塞
   - 根据实际场景选择公平锁或非公平锁
   - 正确使用条件变量实现线程协作

3. **性能考虑**：
   - 锁的范围应尽可能小，只保护必要的代码
   - 避免嵌套锁，减少死锁的可能性
   - 考虑使用读写锁（ReentrantReadWriteLock）优化读多写少的场景

## 注意事项

- synchronized是Java的关键字，由JVM实现，而ReentrantLock是Java类库提供的，由Java代码实现
- 使用ReentrantLock时，必须确保在finally块中释放锁，否则可能导致死锁
- 公平锁虽然公平，但性能通常比非公平锁差，因为线程需要排队获取锁
- 条件变量的await()方法必须在持有锁的情况下调用，否则会抛出IllegalMonitorStateException
- 生产者-消费者模式中，必须使用while循环检查条件，而不是if语句，以避免虚假唤醒