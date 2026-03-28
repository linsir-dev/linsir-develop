package com.linsir.abc.core.jvm.threadsafety.cas;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * CAS（比较并交换）示例 - AtomicCounter
 * 
 * 演示非阻塞同步的实现方式：
 * 1. AtomicInteger的基本使用
 * 2. CAS操作原理
 * 3. ABA问题及解决方案
 * 
 * CAS特点：
 * - 乐观并发策略：先操作，失败则重试
 * - 无阻塞：不会挂起线程
 * - 原子性：硬件级别的原子操作
 * - 适用场景：低竞争、短操作的场景
 * 
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-29
 */
public class AtomicCounter {
    
    /**
     * 原子整数 - 线程安全的计数器
     */
    private final AtomicInteger count = new AtomicInteger(0);
    
    /**
     * 使用AtomicInteger的自增方法
     * 底层使用CAS实现
     */
    public void increment() {
        count.incrementAndGet();
    }
    
    /**
     * 使用AtomicInteger的自减方法
     */
    public void decrement() {
        count.decrementAndGet();
    }
    
    /**
     * 获取当前值
     * 
     * @return 当前计数值
     */
    public int getCount() {
        return count.get();
    }
    
    /**
     * 显式使用CAS操作
     * 演示CAS的工作原理
     * 
     * @param expect 预期值
     * @param update 更新值
     * @return 是否更新成功
     */
    public boolean compareAndSet(int expect, int update) {
        return count.compareAndSet(expect, update);
    }
    
    /**
     * 使用循环CAS实现自定义操作
     * 演示乐观锁的"失败重试"机制
     * 
     * @param delta 增量
     */
    public void add(int delta) {
        int oldValue, newValue;
        do {
            oldValue = count.get();           // 获取当前值
            newValue = oldValue + delta;      // 计算新值
        } while (!count.compareAndSet(oldValue, newValue));  // CAS更新，失败则重试
    }
    
    /**
     * 带版本号的原子引用 - 解决ABA问题
     */
    public static class ABADemo {
        
        /**
         * 普通原子引用 - 存在ABA问题
         */
        private final AtomicInteger atomicRef = new AtomicInteger(100);
        
        /**
         * 带版本号的原子引用 - 解决ABA问题
         */
        private final AtomicStampedReference<Integer> stampedRef = 
                new AtomicStampedReference<>(100, 0);
        
        /**
         * 演示ABA问题
         * 线程1准备将100改为101，但在此期间：
         * 线程2将100改为200，又将200改回100
         * 线程1的CAS操作会成功，但实际上值已经被修改过
         * 
         * @throws InterruptedException 当线程被中断时
         */
        public void demonstrateABAProblem() throws InterruptedException {
            System.out.println("=== ABA Problem Demo ===");
            System.out.println("Initial value: " + atomicRef.get());
            
            Thread t1 = new Thread(() -> {
                int expected = 100;
                try {
                    Thread.sleep(500);  // 模拟处理时间
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                boolean success = atomicRef.compareAndSet(expected, 101);
                System.out.println("Thread1 CAS " + expected + " -> 101: " + success);
                System.out.println("Final value: " + atomicRef.get());
            }, "Thread1");
            
            Thread t2 = new Thread(() -> {
                try {
                    Thread.sleep(100);  // 确保在Thread1之后执行
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                atomicRef.compareAndSet(100, 200);
                System.out.println("Thread2 changed 100 -> 200");
                atomicRef.compareAndSet(200, 100);
                System.out.println("Thread2 changed 200 -> 100");
            }, "Thread2");
            
            t1.start();
            t2.start();
            t1.join();
            t2.join();
            
            System.out.println("ABA problem occurred: Thread1 succeeded but value was modified!\n");
        }
        
        /**
         * 使用AtomicStampedReference解决ABA问题
         * 每次更新都会增加版本号，即使值相同，版本号也不同
         * 
         * @throws InterruptedException 当线程被中断时
         */
        public void demonstrateABASolution() throws InterruptedException {
            System.out.println("=== ABA Solution with StampedReference ===");
            System.out.println("Initial value: " + stampedRef.getReference() + 
                    ", stamp: " + stampedRef.getStamp());
            
            Thread t1 = new Thread(() -> {
                int[] stampHolder = new int[1];
                Integer value = stampedRef.get(stampHolder);
                int stamp = stampHolder[0];
                
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                boolean success = stampedRef.compareAndSet(value, 101, stamp, stamp + 1);
                System.out.println("Thread1 CAS " + value + " -> 101 with stamp " + stamp + ": " + success);
                System.out.println("Final value: " + stampedRef.getReference() + 
                        ", stamp: " + stampedRef.getStamp());
            }, "Thread1");
            
            Thread t2 = new Thread(() -> {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                int[] stampHolder = new int[1];
                Integer value = stampedRef.get(stampHolder);
                int stamp = stampHolder[0];
                
                stampedRef.compareAndSet(value, 200, stamp, stamp + 1);
                System.out.println("Thread2 changed " + value + " -> 200, stamp: " + (stamp + 1));
                
                value = stampedRef.get(stampHolder);
                stamp = stampHolder[0];
                stampedRef.compareAndSet(value, 100, stamp, stamp + 1);
                System.out.println("Thread2 changed " + value + " -> 100, stamp: " + (stamp + 1));
            }, "Thread2");
            
            t1.start();
            t2.start();
            t1.join();
            t2.join();
            
            System.out.println("ABA problem solved: Thread1 failed due to stamp mismatch!\n");
        }
    }
    
    /**
     * CAS性能对比测试
     * 对比synchronized、AtomicInteger、volatile的性能
     */
    public static class PerformanceTest {
        private int synchronizedCount = 0;
        private final AtomicInteger atomicCount = new AtomicInteger(0);
        private volatile int volatileCount = 0;
        
        /**
         * synchronized计数
         */
        public synchronized void synchronizedIncrement() {
            synchronizedCount++;
        }
        
        /**
         * AtomicInteger计数
         */
        public void atomicIncrement() {
            atomicCount.incrementAndGet();
        }
        
        /**
         * volatile计数（非线程安全，仅用于对比）
         */
        public void volatileIncrement() {
            volatileCount++;
        }
        
        /**
         * 运行性能测试
         * 
         * @throws InterruptedException 当线程被中断时
         */
        public void runTest() throws InterruptedException {
            final int threadCount = 10;
            final int incrementPerThread = 100000;
            
            System.out.println("=== CAS Performance Test ===");
            System.out.println("Threads: " + threadCount + ", Increments per thread: " + incrementPerThread);
            
            // 1. synchronized测试
            long start = System.currentTimeMillis();
            Thread[] threads = new Thread[threadCount];
            for (int i = 0; i < threadCount; i++) {
                threads[i] = new Thread(() -> {
                    for (int j = 0; j < incrementPerThread; j++) {
                        synchronizedIncrement();
                    }
                });
                threads[i].start();
            }
            for (Thread t : threads) {
                t.join();
            }
            long synchronizedTime = System.currentTimeMillis() - start;
            System.out.println("synchronized: " + synchronizedTime + "ms, count: " + synchronizedCount);
            
            // 2. AtomicInteger测试
            start = System.currentTimeMillis();
            for (int i = 0; i < threadCount; i++) {
                threads[i] = new Thread(() -> {
                    for (int j = 0; j < incrementPerThread; j++) {
                        atomicIncrement();
                    }
                });
                threads[i].start();
            }
            for (Thread t : threads) {
                t.join();
            }
            long atomicTime = System.currentTimeMillis() - start;
            System.out.println("AtomicInteger: " + atomicTime + "ms, count: " + atomicCount.get());
            
            // 3. volatile测试（非线程安全）
            start = System.currentTimeMillis();
            for (int i = 0; i < threadCount; i++) {
                threads[i] = new Thread(() -> {
                    for (int j = 0; j < incrementPerThread; j++) {
                        volatileIncrement();
                    }
                });
                threads[i].start();
            }
            for (Thread t : threads) {
                t.join();
            }
            long volatileTime = System.currentTimeMillis() - start;
            System.out.println("volatile (unsafe): " + volatileTime + "ms, count: " + volatileCount);
            
            System.out.println("\nConclusion: AtomicInteger is faster than synchronized in low contention scenarios.");
        }
    }
    
    /**
     * 主方法 - 演示CAS的使用
     * 
     * @param args 命令行参数
     * @throws InterruptedException 当线程被中断时
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Atomic Counter Demo ===\n");
        
        // 1. 基本使用
        System.out.println("1. Basic AtomicInteger usage:");
        AtomicCounter counter = new AtomicCounter();
        
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    counter.increment();
                }
            }, "Thread-" + i);
            threads[i].start();
        }
        
        for (Thread t : threads) {
            t.join();
        }
        
        System.out.println("Expected: 10000, Actual: " + counter.getCount());
        
        // 2. 显式CAS操作
        System.out.println("\n2. Explicit CAS operation:");
        boolean success = counter.compareAndSet(10000, 20000);
        System.out.println("CAS 10000 -> 20000: " + success);
        System.out.println("Current value: " + counter.getCount());
        
        // 3. 自定义操作
        System.out.println("\n3. Custom operation with loop CAS:");
        counter.add(500);
        System.out.println("After add(500): " + counter.getCount());
        
        // 4. ABA问题演示
        System.out.println("\n4. ABA Problem and Solution:");
        ABADemo abaDemo = new ABADemo();
        abaDemo.demonstrateABAProblem();
        abaDemo.demonstrateABASolution();
        
        // 5. 性能测试
        System.out.println("5. Performance Test:");
        PerformanceTest perfTest = new PerformanceTest();
        perfTest.runTest();
    }
}
