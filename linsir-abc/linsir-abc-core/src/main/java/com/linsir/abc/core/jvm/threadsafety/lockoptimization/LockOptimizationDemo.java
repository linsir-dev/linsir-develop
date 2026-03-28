package com.linsir.abc.core.jvm.threadsafety.lockoptimization;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 锁优化技术演示 - LockOptimizationDemo
 * 
 * 演示HotSpot虚拟机的锁优化技术：
 * 1. 自旋锁（Spin Lock）
 * 2. 锁消除（Lock Elimination）
 * 3. 锁粗化（Lock Coarsening）
 * 4. 轻量级锁（Lightweight Locking）
 * 5. 偏向锁（Biased Locking）
 * 
 * 锁升级过程：
 * 无锁 → 偏向锁 → 轻量级锁 → 重量级锁
 *      (1个线程)  (交替执行)  (竞争激烈)
 * 
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-29
 */
public class LockOptimizationDemo {
    
    /**
     * 锁对象
     */
    private final Object lock = new Object();
    
    /**
     * 计数器
     */
    private int count = 0;
    
    /**
     * 演示自旋锁的概念
     * 自旋锁：线程不放弃CPU，通过忙等待获取锁
     * 适用于锁持有时间短的场景
     * 
     * 注意：Java中自旋锁是JVM自动实现的，这里演示概念
     */
    public void spinLockConcept() {
        System.out.println("=== Spin Lock Concept ===");
        System.out.println("自旋锁通过忙等待避免线程切换开销");
        System.out.println("适用于：锁持有时间短的场景");
        System.out.println("JDK 6引入自适应自旋：根据历史情况动态调整自旋时间\n");
    }
    
    /**
     * 演示锁消除
     * StringBuffer的append方法是同步的
     * 但由于sb不会逃逸出方法，JVM可以消除锁
     * 
     * JVM参数：-XX:+DoEscapeAnalysis -XX:+EliminateLocks
     */
    public String lockEliminationDemo(String s1, String s2, String s3) {
        // StringBuffer的所有方法都是synchronized
        // 但sb是局部变量，不会逃逸，JVM可以消除锁
        StringBuffer sb = new StringBuffer();
        sb.append(s1);  // 同步方法，但锁可以消除
        sb.append(s2);  // 同步方法，但锁可以消除
        sb.append(s3);  // 同步方法，但锁可以消除
        return sb.toString();
    }
    
    /**
     * 演示锁粗化
     * 连续的加锁解锁操作会被合并为一个更大的同步块
     */
    public void lockCoarseningDemo() {
        // 频繁的加锁解锁（优化前）
        // JVM会将这些连续的同步块粗化为一个
        
        synchronized (lock) {
            count++;  // operation 1
        }
        synchronized (lock) {
            count++;  // operation 2
        }
        synchronized (lock) {
            count++;  // operation 3
        }
        
        // 锁粗化后等效于：
        // synchronized (lock) {
        //     count++;
        //     count++;
        //     count++;
        // }
    }
    
    /**
     * 演示锁粗化的另一个场景
     * for循环中的同步操作
     */
    public void lockCoarseningInLoop() {
        // 优化前：循环100次，加锁解锁100次
        for (int i = 0; i < 100; i++) {
            synchronized (lock) {
                count++;
            }
        }
        
        // 锁粗化后：将锁移到循环外部
        // synchronized (lock) {
        //     for (int i = 0; i < 100; i++) {
        //         count++;
        //     }
        // }
    }
    
    /**
     * 轻量级锁演示类
     * 演示对象头中的锁状态变化
     */
    public static class LightweightLockDemo {
        
        /**
         * 用于加锁的对象
         */
        private final Object lockObj = new Object();
        
        /**
         * 演示轻量级锁的获取和释放
         * 当只有一个线程或线程交替执行时，使用轻量级锁
         */
        public void demonstrateLightweightLock() {
            System.out.println("=== Lightweight Lock Demo ===");
            System.out.println("对象头Mark Word存储指向栈中锁记录的指针");
            System.out.println("通过CAS操作获取和释放锁");
            System.out.println("适用于：线程交替执行的场景\n");
            
            // 同步块使用轻量级锁（假设无竞争）
            synchronized (lockObj) {
                System.out.println("In synchronized block");
                System.out.println("Lock status: 轻量级锁 (00)");
            }
            System.out.println("Lock released\n");
        }
        
        /**
         * 演示锁膨胀
         * 当多个线程竞争时，轻量级锁膨胀为重量级锁
         * 
         * @throws InterruptedException 当线程被中断时
         */
        public void demonstrateLockInflation() throws InterruptedException {
            System.out.println("=== Lock Inflation Demo ===");
            System.out.println("当多个线程竞争时，轻量级锁膨胀为重量级锁");
            
            Thread t1 = new Thread(() -> {
                synchronized (lockObj) {
                    System.out.println("Thread-1 acquired lock");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }, "Thread-1");
            
            Thread t2 = new Thread(() -> {
                synchronized (lockObj) {
                    System.out.println("Thread-2 acquired lock");
                }
            }, "Thread-2");
            
            t1.start();
            Thread.sleep(10);  // 确保t1先获取锁
            t2.start();
            
            t1.join();
            t2.join();
            
            System.out.println("Lock may have inflated to heavyweight\n");
        }
    }
    
    /**
     * 偏向锁演示类
     * 演示偏向锁的获取和撤销
     */
    public static class BiasedLockDemo {
        
        /**
         * 用于加锁的对象
         */
        private final Object lockObj = new Object();
        
        /**
         * 演示偏向锁
         * 偏向锁：锁会偏向于第一个获得它的线程
         * 如果只有一个线程访问，无需CAS操作
         */
        public void demonstrateBiasedLock() {
            System.out.println("=== Biased Lock Demo ===");
            System.out.println("偏向锁：锁偏向于第一个获得它的线程");
            System.out.println("Mark Word存储偏向线程ID");
            System.out.println("适用于：单线程重复获取锁的场景\n");
            
            // 第一次获取锁，成为偏向锁
            synchronized (lockObj) {
                System.out.println("First acquisition - becomes biased");
            }
            
            // 同一线程再次获取，无需CAS
            synchronized (lockObj) {
                System.out.println("Reentrant acquisition - no CAS needed");
            }
            
            System.out.println("Biased lock is efficient for single thread\n");
        }
        
        /**
         * 演示偏向锁撤销
         * 当其他线程竞争时，偏向锁被撤销
         * 
         * @throws InterruptedException 当线程被中断时
         */
        public void demonstrateBiasedLockRevocation() throws InterruptedException {
            System.out.println("=== Biased Lock Revocation Demo ===");
            System.out.println("当其他线程竞争时，偏向锁被撤销");
            
            // 线程1先获取锁，成为偏向锁
            Thread t1 = new Thread(() -> {
                synchronized (lockObj) {
                    System.out.println("Thread-1 acquired lock (biased)");
                }
            }, "Thread-1");
            
            t1.start();
            t1.join();
            
            // 线程2尝试获取锁，导致偏向锁撤销
            Thread t2 = new Thread(() -> {
                synchronized (lockObj) {
                    System.out.println("Thread-2 acquired lock (bias revoked)");
                }
            }, "Thread-2");
            
            t2.start();
            t2.join();
            
            System.out.println("Bias revoked due to competition\n");
        }
    }
    
    /**
     * 锁升级过程演示
     * 展示从偏向锁到重量级锁的升级过程
     */
    public static class LockEscalationDemo {
        
        private final Object lock = new Object();
        
        /**
         * 演示完整的锁升级过程
         * 
         * @throws InterruptedException 当线程被中断时
         */
        public void demonstrateLockEscalation() throws InterruptedException {
            System.out.println("=== Lock Escalation Process ===");
            System.out.println("无锁 → 偏向锁 → 轻量级锁 → 重量级锁\n");
            
            // 阶段1：偏向锁（单线程）
            System.out.println("Stage 1: Biased Locking (single thread)");
            synchronized (lock) {
                System.out.println("  Single thread access - biased lock");
            }
            
            // 阶段2：轻量级锁（线程交替）
            System.out.println("\nStage 2: Lightweight Locking (alternating threads)");
            Thread t1 = new Thread(() -> {
                synchronized (lock) {
                    System.out.println("  Thread-1 acquired lock");
                }
            });
            Thread t2 = new Thread(() -> {
                synchronized (lock) {
                    System.out.println("  Thread-2 acquired lock");
                }
            });
            
            t1.start();
            t1.join();
            t2.start();
            t2.join();
            
            // 阶段3：重量级锁（竞争激烈）
            System.out.println("\nStage 3: Heavyweight Locking (contention)");
            Thread[] threads = new Thread[5];
            for (int i = 0; i < 5; i++) {
                threads[i] = new Thread(() -> {
                    for (int j = 0; j < 100; j++) {
                        synchronized (lock) {
                            // 竞争激烈
                        }
                    }
                }, "Contention-Thread-" + i);
                threads[i].start();
            }
            
            for (Thread t : threads) {
                t.join();
            }
            
            System.out.println("  High contention - lock inflated to heavyweight");
            System.out.println("\nLock escalation completed!");
        }
    }
    
    /**
     * 性能对比测试
     * 对比不同锁策略的性能
     */
    public static class LockPerformanceTest {
        
        private int synchronizedCount = 0;
        private final ReentrantLock reentrantLock = new ReentrantLock();
        private final Object lock = new Object();
        
        /**
         * 使用synchronized
         */
        public void synchronizedIncrement() {
            synchronized (lock) {
                synchronizedCount++;
            }
        }
        
        /**
         * 使用ReentrantLock
         */
        public void reentrantLockIncrement() {
            reentrantLock.lock();
            try {
                synchronizedCount++;
            } finally {
                reentrantLock.unlock();
            }
        }
        
        /**
         * 运行性能测试
         * 
         * @throws InterruptedException 当线程被中断时
         */
        public void runTest() throws InterruptedException {
            final int threadCount = 10;
            final int incrementPerThread = 10000;
            
            System.out.println("=== Lock Performance Test ===");
            System.out.println("Threads: " + threadCount);
            System.out.println("Increments per thread: " + incrementPerThread + "\n");
            
            // 1. synchronized测试
            synchronizedCount = 0;
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
            long syncTime = System.currentTimeMillis() - start;
            System.out.println("synchronized: " + syncTime + "ms");
            
            // 2. ReentrantLock测试
            synchronizedCount = 0;
            start = System.currentTimeMillis();
            for (int i = 0; i < threadCount; i++) {
                threads[i] = new Thread(() -> {
                    for (int j = 0; j < incrementPerThread; j++) {
                        reentrantLockIncrement();
                    }
                });
                threads[i].start();
            }
            for (Thread t : threads) {
                t.join();
            }
            long lockTime = System.currentTimeMillis() - start;
            System.out.println("ReentrantLock: " + lockTime + "ms");
            
            System.out.println("\nNote: With lock optimizations, performance is similar.");
        }
    }
    
    /**
     * 主方法 - 演示锁优化技术
     * 
     * @param args 命令行参数
     * @throws InterruptedException 当线程被中断时
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Lock Optimization Demo ===\n");
        
        LockOptimizationDemo demo = new LockOptimizationDemo();
        
        // 1. 自旋锁概念
        demo.spinLockConcept();
        
        // 2. 锁消除
        System.out.println("2. Lock Elimination:");
        String result = demo.lockEliminationDemo("Hello", " ", "World");
        System.out.println("Result: " + result);
        System.out.println("JVM eliminated unnecessary locks on local StringBuffer\n");
        
        // 3. 锁粗化
        System.out.println("3. Lock Coarsening:");
        demo.lockCoarseningDemo();
        System.out.println("JVM coarsened consecutive synchronized blocks\n");
        
        // 4. 轻量级锁
        LightweightLockDemo lightweightDemo = new LightweightLockDemo();
        lightweightDemo.demonstrateLightweightLock();
        lightweightDemo.demonstrateLockInflation();
        
        // 5. 偏向锁
        BiasedLockDemo biasedDemo = new BiasedLockDemo();
        biasedDemo.demonstrateBiasedLock();
        biasedDemo.demonstrateBiasedLockRevocation();
        
        // 6. 锁升级过程
        LockEscalationDemo escalationDemo = new LockEscalationDemo();
        escalationDemo.demonstrateLockEscalation();
        
        // 7. 性能测试
        System.out.println("\n");
        LockPerformanceTest perfTest = new LockPerformanceTest();
        perfTest.runTest();
    }
}
