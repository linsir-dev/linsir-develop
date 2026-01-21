package com.linsir.abc.pdai.thread.juc.lock;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.StampedLock;

/**
 * @author linsir
 * @version 1.0
 * @description: 演示JUC中的Lock框架
 * @date 2026/1/22 4:06
 */
public class LockFrameworkDemo {
    // 1. ReentrantLock示例
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
        
        // 可中断锁示例
        public void interruptibleLockTest() {
            Thread thread = new Thread(() -> {
                System.out.println("尝试获取锁...");
                try {
                    if (lock.tryLock(2000, java.util.concurrent.TimeUnit.MILLISECONDS)) {
                        try {
                            System.out.println("成功获取锁");
                            Thread.sleep(3000);
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
                Thread.sleep(1000);
                System.out.println("中断子线程");
                thread.interrupt();
                Thread.sleep(1000);
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
        
        public int getCount() {
            return count;
        }
        
        public static void test() {
            System.out.println("1. ReentrantLock示例:");
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
            
            // 测试可中断锁
            System.out.println("\nReentrantLock可中断锁测试:");
            demo.interruptibleLockTest();
            System.out.println();
        }
    }
    
    // 2. ReentrantReadWriteLock示例
    static class ReentrantReadWriteLockDemo {
        private int data = 0;
        private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
        private final ReentrantReadWriteLock.ReadLock readLock = rwLock.readLock();
        private final ReentrantReadWriteLock.WriteLock writeLock = rwLock.writeLock();
        
        // 读操作
        public int read() {
            readLock.lock();
            try {
                System.out.println("读线程 " + Thread.currentThread().getName() + " 读取数据: " + data);
                Thread.sleep(100); // 模拟读取操作
                return data;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return -1;
            } finally {
                readLock.unlock();
            }
        }
        
        // 写操作
        public void write(int newValue) {
            writeLock.lock();
            try {
                data = newValue;
                System.out.println("写线程 " + Thread.currentThread().getName() + " 写入数据: " + data);
                Thread.sleep(200); // 模拟写入操作
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                writeLock.unlock();
            }
        }
        
        public static void test() {
            System.out.println("2. ReentrantReadWriteLock示例:");
            ReentrantReadWriteLockDemo demo = new ReentrantReadWriteLockDemo();
            
            // 启动3个读线程
            for (int i = 0; i < 3; i++) {
                new Thread(() -> {
                    for (int j = 0; j < 3; j++) {
                        demo.read();
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }, "ReadThread-" + i).start();
            }
            
            // 启动2个写线程
            for (int i = 0; i < 2; i++) {
                final int value = (i + 1) * 10;
                new Thread(() -> {
                    for (int j = 0; j < 2; j++) {
                        demo.write(value + j);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }, "WriteThread-" + i).start();
            }
            
            // 等待所有线程结束
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println();
        }
    }
    
    // 3. StampedLock示例
    static class StampedLockDemo {
        private double x = 0.0;
        private double y = 0.0;
        private final StampedLock lock = new StampedLock();
        
        // 写操作
        public void move(double deltaX, double deltaY) {
            long stamp = lock.writeLock();
            try {
                x += deltaX;
                y += deltaY;
                System.out.println("写线程 " + Thread.currentThread().getName() + " 移动到: (" + x + ", " + y + ")");
                Thread.sleep(100); // 模拟写入操作
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlockWrite(stamp);
            }
        }
        
        // 读操作（乐观读）
        public double distanceFromOrigin() {
            long stamp = lock.tryOptimisticRead();
            double currentX = x;
            double currentY = y;
            
            // 检查乐观读期间是否有写操作
            if (!lock.validate(stamp)) {
                // 乐观读失败，升级为悲观读
                stamp = lock.readLock();
                try {
                    currentX = x;
                    currentY = y;
                    System.out.println("读线程 " + Thread.currentThread().getName() + " 悲观读取: (" + currentX + ", " + currentY + ")");
                } finally {
                    lock.unlockRead(stamp);
                }
            } else {
                System.out.println("读线程 " + Thread.currentThread().getName() + " 乐观读取: (" + currentX + ", " + currentY + ")");
            }
            
            try {
                Thread.sleep(50); // 模拟其他操作
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            return Math.sqrt(currentX * currentX + currentY * currentY);
        }
        
        public static void test() {
            System.out.println("3. StampedLock示例:");
            StampedLockDemo demo = new StampedLockDemo();
            
            // 启动3个读线程
            for (int i = 0; i < 3; i++) {
                new Thread(() -> {
                    for (int j = 0; j < 5; j++) {
                        double distance = demo.distanceFromOrigin();
                        System.out.println("读线程 " + Thread.currentThread().getName() + " 计算距离: " + distance);
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }, "ReadThread-" + i).start();
            }
            
            // 启动2个写线程
            for (int i = 0; i < 2; i++) {
                new Thread(() -> {
                    for (int j = 0; j < 3; j++) {
                        demo.move(1.0, 1.0);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }, "WriteThread-" + i).start();
            }
            
            // 等待所有线程结束
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println();
        }
    }
    
    public static void main(String[] args) {
        ReentrantLockDemo.test();
        ReentrantReadWriteLockDemo.test();
        StampedLockDemo.test();
    }
}