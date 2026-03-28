package com.linsir.abc.core.jvm.jmm.synchronizedexample;

/**
 * synchronized线程安全计数器示例
 * 
 * 演示synchronized的三种用法：
 * 1. 同步实例方法
 * 2. 同步静态方法
 * 3. 同步代码块
 * 
 * synchronized的内存语义：
 * - 进入synchronized块：清空工作内存，从主内存重新读取变量值
 * - 退出synchronized块：将工作内存中的变量值刷新到主内存
 * 
 * @author linsir
 * @version 1.0.0
 * @since 2024/1/1
 */
public class SynchronizedCounter {
    
    /**
     * 实例计数器
     */
    private int count = 0;
    
    /**
     * 静态计数器
     */
    private static int staticCount = 0;
    
    /**
     * 同步实例方法
     * 锁对象是当前实例（this）
     * 同一实例的此方法同一时间只能被一个线程执行
     */
    public synchronized void increment() {
        count++;
    }
    
    /**
     * 同步实例方法
     * 锁对象是当前实例（this）
     */
    public synchronized void decrement() {
        count--;
    }
    
    /**
     * 同步实例方法
     * 获取当前计数值（读取也需要同步保证可见性）
     */
    public synchronized int getCount() {
        return count;
    }
    
    /**
     * 同步静态方法
     * 锁对象是类的Class对象（SynchronizedCounter.class）
     * 所有实例共享同一把锁
     */
    public static synchronized void incrementStatic() {
        staticCount++;
    }
    
    /**
     * 同步静态方法
     */
    public static synchronized int getStaticCount() {
        return staticCount;
    }
    
    /**
     * 使用同步代码块
     * 锁对象是指定的lock对象
     * 可以精确控制同步范围，提高并发性能
     */
    public void incrementWithBlock() {
        // 非同步代码（可以并行执行）
        System.out.println(Thread.currentThread().getName() + " preparing...");
        
        // 同步代码块
        synchronized (this) {
            count++;
            System.out.println(Thread.currentThread().getName() + " incremented to " + count);
        }
    }
    
    /**
     * 使用自定义对象作为锁
     * 可以实现更细粒度的锁控制
     */
    private final Object lock = new Object();
    
    public void incrementWithCustomLock() {
        synchronized (lock) {
            count++;
        }
    }
    
    /**
     * 演示synchronized计数器的线程安全性
     */
    public static void main(String[] args) throws InterruptedException {
        SynchronizedCounter counter = new SynchronizedCounter();
        final int threadCount = 100;
        final int incrementPerThread = 1000;
        
        // 测试实例方法同步
        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < incrementPerThread; j++) {
                    counter.increment();
                }
            });
        }
        
        long startTime = System.currentTimeMillis();
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        long duration = System.currentTimeMillis() - startTime;
        
        System.out.println("=== Synchronized Counter Test ===");
        System.out.println("Thread count: " + threadCount);
        System.out.println("Increment per thread: " + incrementPerThread);
        System.out.println("Expected: " + (threadCount * incrementPerThread));
        System.out.println("Actual: " + counter.getCount());
        System.out.println("Time: " + duration + "ms");
        System.out.println("Correct: " + (counter.getCount() == threadCount * incrementPerThread));
        
        // 测试静态方法同步
        Thread[] staticThreads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            staticThreads[i] = new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    SynchronizedCounter.incrementStatic();
                }
            });
        }
        
        for (Thread thread : staticThreads) {
            thread.start();
        }
        for (Thread thread : staticThreads) {
            thread.join();
        }
        
        System.out.println("\n=== Static Counter Test ===");
        System.out.println("Expected: " + (threadCount * 100));
        System.out.println("Actual: " + SynchronizedCounter.getStaticCount());
    }
}
