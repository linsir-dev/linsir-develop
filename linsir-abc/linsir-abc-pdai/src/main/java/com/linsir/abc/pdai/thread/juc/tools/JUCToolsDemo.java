package com.linsir.abc.pdai.thread.juc.tools;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Exchanger;
import java.util.concurrent.Semaphore;

/**
 * @author linsir
 * @version 1.0
 * @description: 演示JUC中的Tools类
 * @date 2026/1/22 4:06
 */
public class JUCToolsDemo {
    // 1. CountDownLatch示例
    static class CountDownLatchDemo {
        public static void test() {
            System.out.println("1. CountDownLatch示例:");
            final int threadCount = 5;
            final CountDownLatch latch = new CountDownLatch(threadCount);
            
            System.out.println("主线程: 准备开始任务");
            
            for (int i = 0; i < threadCount; i++) {
                final int taskId = i;
                new Thread(() -> {
                    try {
                        System.out.println("任务" + taskId + " 开始执行");
                        Thread.sleep((long) (Math.random() * 1000));
                        System.out.println("任务" + taskId + " 执行完毕");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        latch.countDown(); // 任务完成，计数器减1
                        System.out.println("任务" + taskId + " 计数器减1，当前计数: " + (latch.getCount() + 1) + " -> " + latch.getCount());
                    }
                }, "TaskThread-" + i).start();
            }
            
            try {
                System.out.println("主线程: 等待所有任务完成");
                latch.await(); // 等待计数器减为0
                System.out.println("主线程: 所有任务完成，继续执行");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println();
        }
    }
    
    // 2. CyclicBarrier示例
    static class CyclicBarrierDemo {
        public static void test() {
            System.out.println("2. CyclicBarrier示例:");
            final int threadCount = 4;
            // 当所有线程到达屏障时，执行指定的回调任务
            final CyclicBarrier barrier = new CyclicBarrier(threadCount, () -> {
                System.out.println("所有线程到达屏障，执行回调任务");
            });
            
            for (int i = 0; i < threadCount; i++) {
                final int playerId = i;
                new Thread(() -> {
                    try {
                        System.out.println("选手" + playerId + " 准备就绪");
                        Thread.sleep((long) (Math.random() * 1000));
                        System.out.println("选手" + playerId + " 到达起跑点");
                        barrier.await(); // 等待所有选手到达起跑点
                        System.out.println("选手" + playerId + " 开始比赛");
                        Thread.sleep((long) (Math.random() * 1000));
                        System.out.println("选手" + playerId + " 完成比赛");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, "PlayerThread-" + i).start();
            }
            
            // 等待所有线程完成
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println();
        }
    }
    
    // 3. Semaphore示例
    static class SemaphoreDemo {
        public static void test() {
            System.out.println("3. Semaphore示例:");
            final int permits = 3; // 可用资源数量
            final Semaphore semaphore = new Semaphore(permits);
            final int threadCount = 6; // 线程数量
            
            System.out.println("可用资源数量: " + permits);
            
            for (int i = 0; i < threadCount; i++) {
                final int threadId = i;
                new Thread(() -> {
                    try {
                        System.out.println("线程" + threadId + " 尝试获取资源");
                        semaphore.acquire(); // 获取资源
                        System.out.println("线程" + threadId + " 成功获取资源，当前可用资源: " + semaphore.availablePermits());
                        Thread.sleep((long) (Math.random() * 1000)); // 使用资源
                        System.out.println("线程" + threadId + " 使用资源完毕");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        semaphore.release(); // 释放资源
                        System.out.println("线程" + threadId + " 释放资源，当前可用资源: " + semaphore.availablePermits());
                    }
                }, "ResourceThread-" + i).start();
            }
            
            // 等待所有线程完成
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println();
        }
    }
    
    // 4. Exchanger示例
    static class ExchangerDemo {
        public static void test() {
            System.out.println("4. Exchanger示例:");
            final Exchanger<String> exchanger = new Exchanger<>();
            
            // 线程1
            new Thread(() -> {
                try {
                    String data1 = "线程1的数据";
                    System.out.println("线程1: 准备交换数据: " + data1);
                    Thread.sleep(1000);
                    String exchangedData = exchanger.exchange(data1);
                    System.out.println("线程1: 交换得到数据: " + exchangedData);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }, "ExchangerThread-1").start();
            
            // 线程2
            new Thread(() -> {
                try {
                    String data2 = "线程2的数据";
                    System.out.println("线程2: 准备交换数据: " + data2);
                    Thread.sleep(2000);
                    String exchangedData = exchanger.exchange(data2);
                    System.out.println("线程2: 交换得到数据: " + exchangedData);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }, "ExchangerThread-2").start();
            
            // 等待所有线程完成
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println();
        }
    }
    
    public static void main(String[] args) {
        CountDownLatchDemo.test();
        CyclicBarrierDemo.test();
        SemaphoreDemo.test();
        ExchangerDemo.test();
    }
}