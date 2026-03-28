package com.linsir.abc.core.jvm.jmm.volatileexample;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * volatile不能保证原子性的示例
 * 
 * 演示volatile在复合操作（如i++）中的局限性
 * 以及正确的解决方案（使用AtomicInteger）
 * 
 * @author linsir
 * @version 1.0.0
 * @since 2024/1/1
 */
public class VolatileCounter {
    
    /**
     * volatile修饰的计数器
     * 警告：volatile不能保证i++操作的原子性
     */
    private volatile int volatileCount = 0;
    
    /**
     * 原子类计数器（正确方案）
     * 保证复合操作的原子性
     */
    private AtomicInteger atomicCount = new AtomicInteger(0);
    
    /**
     * 线程不安全的自增操作
     * i++实际上包含三个步骤：
     * 1. 读取i的值
     * 2. 增加1
     * 3. 写回新值
     * 
     * volatile只能保证单次读写的可见性，不能保证这三步的原子性
     */
    public void unsafeIncrement() {
        volatileCount++;
    }
    
    /**
     * 线程安全的自增操作（使用AtomicInteger）
     * 使用CAS操作保证原子性
     */
    public void safeIncrement() {
        atomicCount.incrementAndGet();
    }
    
    /**
     * 获取volatile计数器的值
     * @return volatile计数
     */
    public int getVolatileCount() {
        return volatileCount;
    }
    
    /**
     * 获取原子计数器的值
     * @return 原子计数
     */
    public int getAtomicCount() {
        return atomicCount.get();
    }
    
    /**
     * 演示volatile不能保证原子性
     */
    public static void main(String[] args) throws InterruptedException {
        VolatileCounter counter = new VolatileCounter();
        final int threadCount = 100;
        final int incrementPerThread = 1000;
        
        // 测试volatile计数器（线程不安全）
        Thread[] volatileThreads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            volatileThreads[i] = new Thread(() -> {
                for (int j = 0; j < incrementPerThread; j++) {
                    counter.unsafeIncrement();
                }
            });
        }
        
        long startTime = System.currentTimeMillis();
        for (Thread thread : volatileThreads) {
            thread.start();
        }
        for (Thread thread : volatileThreads) {
            thread.join();
        }
        long volatileTime = System.currentTimeMillis() - startTime;
        
        System.out.println("=== Volatile Counter (Unsafe) ===");
        System.out.println("Expected: " + (threadCount * incrementPerThread));
        System.out.println("Actual: " + counter.getVolatileCount());
        System.out.println("Time: " + volatileTime + "ms");
        System.out.println();
        
        // 测试AtomicInteger计数器（线程安全）
        Thread[] atomicThreads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            atomicThreads[i] = new Thread(() -> {
                for (int j = 0; j < incrementPerThread; j++) {
                    counter.safeIncrement();
                }
            });
        }
        
        startTime = System.currentTimeMillis();
        for (Thread thread : atomicThreads) {
            thread.start();
        }
        for (Thread thread : atomicThreads) {
            thread.join();
        }
        long atomicTime = System.currentTimeMillis() - startTime;
        
        System.out.println("=== Atomic Counter (Safe) ===");
        System.out.println("Expected: " + (threadCount * incrementPerThread));
        System.out.println("Actual: " + counter.getAtomicCount());
        System.out.println("Time: " + atomicTime + "ms");
    }
}
