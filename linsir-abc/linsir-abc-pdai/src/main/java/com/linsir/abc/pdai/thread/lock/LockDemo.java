package com.linsir.abc.pdai.thread.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author linsir
 * @version 1.0
 * @description: 演示synchronized和ReentrantLock的锁机制
 * @date 2026/1/21 22:51
 */
public class LockDemo {
    // 1. synchronized关键字示例
    static class SynchronizedDemo {
        private int count = 0;
        
        // 方法级别的synchronized
        public synchronized void increment1() {
            count++;
            System.out.println("synchronized方法: count = " + count);
        }
        
        // 代码块级别的synchronized
        public void increment2() {
            synchronized (this) {
                count++;
                System.out.println("synchronized代码块: count = " + count);
            }
        }
        
        // 静态synchronized方法
        private static int staticCount = 0;
        
        public static synchronized void staticIncrement() {
            staticCount++;
            System.out.println("静态synchronized方法: staticCount = " + staticCount);
        }
        
        public int getCount() {
            return count;
        }
        
        public static int getStaticCount() {
            return staticCount;
        }
        
        public static void test() {
            System.out.println("1. synchronized关键字示例:");
            SynchronizedDemo demo = new SynchronizedDemo();
            int threadCount = 5;
            Thread[] threads = new Thread[threadCount];
            
            for (int i = 0; i < threadCount; i++) {
                threads[i] = new Thread(() -> {
                    for (int j = 0; j < 3; j++) {
                        demo.increment1();
                        demo.increment2();
                        staticIncrement();
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }, "SynchronizedThread-" + i);
                threads[i].start();
            }
            
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            System.out.println("最终count: " + demo.getCount());
            System.out.println("最终staticCount: " + getStaticCount());
            System.out.println();
        }
    }
    
    // 2. ReentrantLock示例
    static class ReentrantLockDemo {
        private int count = 0;
        private final ReentrantLock lock = new ReentrantLock();
        
        public void increment() {
            lock.lock();
            try {
                count++;
                System.out.println("ReentrantLock: count = " + count);
            } finally {
                lock.unlock();
            }
        }
        
        // 可重入性测试
        public void reentrantTest() {
            lock.lock();
            try {
                System.out.println("ReentrantLock: 第一次获取锁");
                count++;
                
                // 再次获取锁（可重入）
                lock.lock();
                try {
                    System.out.println("ReentrantLock: 第二次获取锁（可重入）");
                    count++;
                } finally {
                    lock.unlock();
                    System.out.println("ReentrantLock: 释放第二次获取的锁");
                }
            } finally {
                lock.unlock();
                System.out.println("ReentrantLock: 释放第一次获取的锁");
            }
        }
        
        // 可中断锁测试
        public void interruptibleLockTest() {
            Thread thread = new Thread(() -> {
                System.out.println("尝试获取锁...");
                try {
                    if (lock.tryLock(2, TimeUnit.SECONDS)) {
                        try {
                            System.out.println("成功获取锁");
                            Thread.sleep(3000); // 持有锁3秒
                        } finally {
                            lock.unlock();
                            System.out.println("释放锁");
                        }
                    } else {
                        System.out.println("获取锁超时");
                    }
                } catch (InterruptedException e) {
                    System.out.println("获取锁时被中断");
                }
            }, "InterruptibleThread");
            
            thread.start();
            
            // 主线程获取锁
            lock.lock();
            System.out.println("主线程获取锁");
            
            try {
                Thread.sleep(1000); // 持有锁1秒
                System.out.println("中断子线程");
                thread.interrupt();
                Thread.sleep(1000); // 再持有锁1秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
                System.out.println("主线程释放锁");
            }
            
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        // 公平锁测试
        public void fairLockTest() {
            System.out.println("\nReentrantLock公平锁测试:");
            ReentrantLock fairLock = new ReentrantLock(true); // 公平锁
            
            for (int i = 0; i < 5; i++) {
                final int threadId = i;
                new Thread(() -> {
                    for (int j = 0; j < 2; j++) {
                        fairLock.lock();
                        try {
                            System.out.println("公平锁 - 线程" + threadId + " 获取锁, 当前等待线程数: " + fairLock.getQueueLength());
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            fairLock.unlock();
                        }
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }, "FairThread-" + i).start();
            }
            
            // 等待所有线程结束
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        public int getCount() {
            return count;
        }
        
        public static void test() {
            System.out.println("2. ReentrantLock示例:");
            ReentrantLockDemo demo = new ReentrantLockDemo();
            int threadCount = 5;
            Thread[] threads = new Thread[threadCount];
            
            for (int i = 0; i < threadCount; i++) {
                threads[i] = new Thread(() -> {
                    for (int j = 0; j < 3; j++) {
                        demo.increment();
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }, "ReentrantLockThread-" + i);
                threads[i].start();
            }
            
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            System.out.println("最终count: " + demo.getCount());
            
            // 测试可重入性
            System.out.println("\nReentrantLock可重入性测试:");
            demo.reentrantTest();
            
            // 测试可中断锁
            System.out.println("\nReentrantLock可中断锁测试:");
            demo.interruptibleLockTest();
            
            // 测试公平锁
            demo.fairLockTest();
            System.out.println();
        }
    }
    
    // 3. synchronized和ReentrantLock对比
    static class LockComparisonDemo {
        public static void test() {
            System.out.println("3. synchronized和ReentrantLock对比:");
            System.out.println("| 特性 | synchronized | ReentrantLock |");
            System.out.println("|------|-------------|---------------|");
            System.out.println("| 锁的获取方式 | 隐式获取（进入同步块自动获取） | 显式获取（调用lock()方法） |");
            System.out.println("| 锁的释放方式 | 隐式释放（退出同步块自动释放） | 显式释放（调用unlock()方法） |");
            System.out.println("| 可重入性 | 支持 | 支持 |");
            System.out.println("| 可中断性 | 不支持（不可中断的获取） | 支持（lockInterruptibly()方法） |");
            System.out.println("| 超时获取 | 不支持 | 支持（tryLock(long, TimeUnit)方法） |");
            System.out.println("| 公平锁 | 非公平 | 可配置（构造函数参数） |");
            System.out.println("| 条件变量 | 每个对象只有一个条件变量 | 可创建多个条件变量（newCondition()方法） |");
            System.out.println("| 锁状态查询 | 不可查询 | 可查询（isLocked()、isHeldByCurrentThread()等方法） |");
            System.out.println("| 性能 | 低并发下性能较好 | 高并发下性能较好 |");
            System.out.println();
        }
    }
    
    // 4. 条件变量示例（ReentrantLock的高级功能）
    static class ConditionDemo {
        private final ReentrantLock lock = new ReentrantLock();
        private final java.util.concurrent.locks.Condition notEmpty = lock.newCondition();
        private final java.util.concurrent.locks.Condition notFull = lock.newCondition();
        private final Object[] buffer = new Object[5];
        private int count, putIndex, takeIndex;
        
        public void put(Object item) throws InterruptedException {
            lock.lock();
            try {
                while (count == buffer.length) {
                    System.out.println("缓冲区已满，等待取出...");
                    notFull.await(); // 等待缓冲区非满
                }
                buffer[putIndex] = item;
                if (++putIndex == buffer.length) putIndex = 0;
                count++;
                System.out.println("放入元素: " + item + ", 当前缓冲区大小: " + count);
                notEmpty.signal(); // 通知缓冲区非空
            } finally {
                lock.unlock();
            }
        }
        
        public Object take() throws InterruptedException {
            lock.lock();
            try {
                while (count == 0) {
                    System.out.println("缓冲区为空，等待放入...");
                    notEmpty.await(); // 等待缓冲区非空
                }
                Object item = buffer[takeIndex];
                if (++takeIndex == buffer.length) takeIndex = 0;
                count--;
                System.out.println("取出元素: " + item + ", 当前缓冲区大小: " + count);
                notFull.signal(); // 通知缓冲区非满
                return item;
            } finally {
                lock.unlock();
            }
        }
        
        public static void test() {
            System.out.println("4. ReentrantLock条件变量示例（生产者-消费者模式）:");
            ConditionDemo buffer = new ConditionDemo();
            
            // 启动生产者线程
            for (int i = 0; i < 2; i++) {
                final int producerId = i;
                new Thread(() -> {
                    for (int j = 0; j < 5; j++) {
                        try {
                            String item = "Item-" + producerId + "-" + j;
                            buffer.put(item);
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }, "Producer-" + i).start();
            }
            
            // 启动消费者线程
            for (int i = 0; i < 3; i++) {
                final int consumerId = i;
                new Thread(() -> {
                    for (int j = 0; j < 3; j++) {
                        try {
                            Object item = buffer.take();
                            System.out.println("消费者" + consumerId + " 消费: " + item);
                            Thread.sleep(800);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }, "Consumer-" + i).start();
            }
            
            // 等待所有线程结束
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println();
        }
    }
    
    public static void main(String[] args) {
        SynchronizedDemo.test(); // 测试synchronized
        ReentrantLockDemo.test(); // 测试ReentrantLock
        LockComparisonDemo.test(); // 对比两种锁
        ConditionDemo.test(); // 测试ReentrantLock的条件变量
    }
}