package com.linsir.abc.core.jvm.threadsafety.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock示例 - ReentrantLockCounter
 * 
 * 演示ReentrantLock的高级功能：
 * 1. 可中断的锁获取
 * 2. 超时获取锁
 * 3. 公平锁
 * 4. 多条件变量（Condition）
 * 
 * ReentrantLock vs synchronized：
 * - 等待可中断：支持，synchronized不支持
 * - 公平锁：支持，synchronized非公平
 * - 多条件变量：支持多个Condition，synchronized只有一个wait/notify
 * - 性能：JDK6后两者接近
 * 
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-29
 */
public class ReentrantLockCounter {
    
    /**
     * 非公平锁（默认）- 性能更好，但可能导致饥饿
     */
    private final ReentrantLock lock = new ReentrantLock();
    
    /**
     * 公平锁 - 按请求锁的顺序获取，避免饥饿
     */
    private final ReentrantLock fairLock = new ReentrantLock(true);
    
    /**
     * 计数器
     */
    private int count = 0;
    
    /**
     * 条件变量 - 用于线程间协调
     */
    private final Condition condition = lock.newCondition();
    
    /**
     * 条件标志
     */
    private boolean ready = false;
    
    /**
     * 使用ReentrantLock进行同步
     * 必须在finally块中释放锁
     */
    public void increment() {
        lock.lock();  // 获取锁
        try {
            count++;
        } finally {
            lock.unlock();  // 确保释放锁
        }
    }
    
    /**
     * 获取当前计数值
     * 
     * @return 当前计数值
     */
    public int getCount() {
        lock.lock();
        try {
            return count;
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 可中断地获取锁
     * 当线程被中断时，会抛出InterruptedException
     * 
     * @throws InterruptedException 当线程被中断时
     */
    public void incrementWithInterruptibleLock() throws InterruptedException {
        lock.lockInterruptibly();  // 可中断地获取锁
        try {
            count++;
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 尝试获取锁，带超时时间
     * 如果在指定时间内无法获取锁，返回false
     * 
     * @param timeout 超时时间
     * @param unit 时间单位
     * @return 是否成功获取锁
     * @throws InterruptedException 当线程被中断时
     */
    public boolean tryIncrement(long timeout, TimeUnit unit) throws InterruptedException {
        if (lock.tryLock(timeout, unit)) {
            try {
                count++;
                return true;
            } finally {
                lock.unlock();
            }
        }
        return false;  // 获取锁超时
    }
    
    /**
     * 使用公平锁进行同步
     * 公平锁按请求顺序分配锁，避免线程饥饿
     */
    public void incrementWithFairLock() {
        fairLock.lock();
        try {
            count++;
        } finally {
            fairLock.unlock();
        }
    }
    
    /**
     * 等待条件满足
     * 使用Condition实现线程间协调
     * 
     * @throws InterruptedException 当线程被中断时
     */
    public void waitForReady() throws InterruptedException {
        lock.lock();
        try {
            while (!ready) {
                System.out.println(Thread.currentThread().getName() + " waiting for ready...");
                condition.await();  // 释放锁并等待
            }
            System.out.println(Thread.currentThread().getName() + " is ready!");
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 设置就绪状态并通知等待线程
     */
    public void setReady() {
        lock.lock();
        try {
            ready = true;
            System.out.println("Setting ready to true and signaling...");
            condition.signalAll();  // 通知所有等待线程
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * 获取锁的持有信息
     * 
     * @return 锁信息字符串
     */
    public String getLockInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Lock held by current thread: ").append(lock.isHeldByCurrentThread()).append("\n");
        sb.append("Lock hold count: ").append(lock.getHoldCount()).append("\n");
        sb.append("Queue length: ").append(lock.getQueueLength()).append("\n");
        sb.append("Has queued threads: ").append(lock.hasQueuedThreads()).append("\n");
        sb.append("Is fair: ").append(lock.isFair()).append("\n");
        return sb.toString();
    }
    
    /**
     * 多条件变量示例类
     * 演示使用多个Condition实现更精细的线程控制
     */
    public static class MultiConditionExample {
        private final ReentrantLock lock = new ReentrantLock();
        private final Condition producerCondition = lock.newCondition();
        private final Condition consumerCondition = lock.newCondition();
        
        private int data = 0;
        private boolean hasData = false;
        
        /**
         * 生产者方法
         * 
         * @param value 生产的值
         * @throws InterruptedException 当线程被中断时
         */
        public void produce(int value) throws InterruptedException {
            lock.lock();
            try {
                while (hasData) {
                    producerCondition.await();  // 有数据时等待消费者消费
                }
                data = value;
                hasData = true;
                System.out.println("Produced: " + value);
                consumerCondition.signal();  // 通知消费者
            } finally {
                lock.unlock();
            }
        }
        
        /**
         * 消费者方法
         * 
         * @return 消费的数据
         * @throws InterruptedException 当线程被中断时
         */
        public int consume() throws InterruptedException {
            lock.lock();
            try {
                while (!hasData) {
                    consumerCondition.await();  // 无数据时等待生产者生产
                }
                hasData = false;
                System.out.println("Consumed: " + data);
                producerCondition.signal();  // 通知生产者
                return data;
            } finally {
                lock.unlock();
            }
        }
    }
    
    /**
     * 主方法 - 演示ReentrantLock的使用
     * 
     * @param args 命令行参数
     * @throws InterruptedException 当线程被中断时
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== ReentrantLock Demo ===\n");
        
        // 1. 基本使用
        System.out.println("1. Basic ReentrantLock usage:");
        ReentrantLockCounter counter = new ReentrantLockCounter();
        
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    counter.increment();
                }
            }, "Thread-" + i);
            threads[i].start();
        }
        
        for (Thread t : threads) {
            t.join();
        }
        
        System.out.println("Expected: 1000, Actual: " + counter.getCount());
        
        // 2. 超时获取锁
        System.out.println("\n2. TryLock with timeout:");
        boolean success = counter.tryIncrement(1, TimeUnit.SECONDS);
        System.out.println("TryLock result: " + success);
        System.out.println("Count after tryLock: " + counter.getCount());
        
        // 3. 条件变量
        System.out.println("\n3. Condition variable:");
        ReentrantLockCounter conditionCounter = new ReentrantLockCounter();
        
        Thread waiter = new Thread(() -> {
            try {
                conditionCounter.waitForReady();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Waiter-Thread");
        
        waiter.start();
        Thread.sleep(500);  // 确保waiter先等待
        conditionCounter.setReady();
        waiter.join();
        
        // 4. 多条件变量示例
        System.out.println("\n4. Multi-condition example (Producer-Consumer):");
        MultiConditionExample pc = new MultiConditionExample();
        
        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    pc.produce(i);
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Producer");
        
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    pc.consume();
                    Thread.sleep(150);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Consumer");
        
        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
        
        // 5. 锁信息
        System.out.println("\n5. Lock information:");
        System.out.println(counter.getLockInfo());
    }
}
