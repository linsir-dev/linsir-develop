# JUC Lock框架示例

## 功能说明

本示例代码展示了Java并发包（JUC）中Lock框架的使用方法和特性，包括：

1. **ReentrantLock**：可重入锁，支持可中断获取、超时获取和公平锁
2. **ReentrantReadWriteLock**：读写锁，支持多个读线程同时访问，写线程独占访问
3. **StampedLock**： stamped锁，支持乐观读、悲观读和写操作，性能优于读写锁

## 示例代码结构

- **LockFrameworkDemo.java**：包含三个内部类，分别演示不同的Lock实现：
  - **ReentrantLockDemo**：演示ReentrantLock的基本使用和可中断获取
  - **ReentrantReadWriteLockDemo**：演示读写锁的使用，读共享写独占
  - **StampedLockDemo**：演示StampedLock的乐观读和悲观读

## 运行说明

1. 编译并运行LockFrameworkDemo.java文件
2. 观察控制台输出，了解不同Lock实现的使用方法和效果
3. 注意ReentrantLock的可中断获取特性
4. 注意ReentrantReadWriteLock的读写分离特性
5. 注意StampedLock的乐观读特性

## 核心知识点

- **可重入性**：线程可以多次获取同一把锁，不会导致死锁
- **可中断性**：线程在获取锁的过程中可以被中断
- **超时获取**：线程在指定时间内尝试获取锁，超时后放弃
- **公平锁**：线程按照请求锁的顺序获取锁，避免饥饿
- **读写分离**：多个读线程可以同时访问，写线程独占访问，提高并发性能
- **乐观读**：假设读操作期间不会有写操作，减少锁的竞争，提高性能

## 示例场景

- **ReentrantLockDemo**：模拟了多个线程对计数器的递增操作，展示了ReentrantLock的基本使用和可中断获取特性
- **ReentrantReadWriteLockDemo**：模拟了多个读线程和写线程同时访问数据，展示了读写锁的读写分离特性
- **StampedLockDemo**：模拟了多个读线程和写线程同时访问坐标数据，展示了StampedLock的乐观读特性

## 优缺点对比

| Lock实现 | 优点 | 缺点 |
|---------|------|------|
| ReentrantLock | 功能丰富，支持可中断、超时获取和公平锁 | 代码复杂，需要手动释放锁 |
| ReentrantReadWriteLock | 读共享写独占，提高并发性能 | 写线程可能会饥饿，读线程过多时写线程难以获取锁 |
| StampedLock | 支持乐观读，性能优于读写锁 | 代码复杂，使用不当容易出错 |

## 最佳实践

1. **选择合适的Lock实现**：
   - 简单场景使用synchronized
   - 需要高级特性时使用ReentrantLock
   - 读多写少场景使用ReentrantReadWriteLock
   - 高并发读场景使用StampedLock

2. **使用Lock的注意事项**：
   - 始终在finally块中释放锁，避免死锁
   - 合理使用tryLock()方法避免线程长时间阻塞
   - 根据实际场景选择公平锁或非公平锁
   - 注意StampedLock的乐观读可能需要重试

3. **性能优化**：
   - 锁的范围应尽可能小，只保护必要的代码
   - 避免嵌套锁，减少死锁的可能性
   - 读多写少场景优先使用读写锁或StampedLock

## 注意事项

- Lock是接口，具体实现有ReentrantLock、ReentrantReadWriteLock和StampedLock等
- 使用Lock时必须手动释放锁，通常在finally块中执行
- ReentrantReadWriteLock的读锁和写锁是互斥的，写锁和写锁也是互斥的
- StampedLock的乐观读需要通过validate()方法检查是否有写操作
- StampedLock不支持可重入性，使用时需要注意