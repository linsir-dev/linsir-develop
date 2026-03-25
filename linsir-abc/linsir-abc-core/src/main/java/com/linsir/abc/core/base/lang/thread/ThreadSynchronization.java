package com.linsir.abc.core.base.lang.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 线程同步机制演示
 * 
 * 本类演示Java中线程同步的各种机制：
 * 1. synchronized关键字（对象锁和类锁）
 * 2. wait/notify/notifyAll机制
 * 3. 生产者-消费者模式
 * 4. 读写锁模式
 * 
 * 同步原则：
 * - 锁的对象必须是final或不会发生变化的
 * - 避免在同步块中调用外部方法（可能导致死锁）
 * - 优先使用java.util.concurrent包中的工具类
 * 
 * @author linsir
 * @version 1.0
 * @since 1.0
 */
public class ThreadSynchronization {
    
    // 共享资源：计数器
    private int counter = 0;
    
    // 共享资源：数据缓冲区
    private final List<Integer> buffer = new ArrayList<>();
    private static final int BUFFER_SIZE = 10;
    
    // 读写锁状态
    private int readers = 0;
    private int writers = 0;
    private int writeRequests = 0;
    
    /**
     * 使用synchronized方法增加计数器
     * 方法锁：锁的是当前对象实例
     */
    public synchronized void increment() {
        counter++;
        System.out.println(Thread.currentThread().getName() + " 增加后: " + counter);
    }
    
    /**
     * 使用synchronized方法减少计数器
     */
    public synchronized void decrement() {
        counter--;
        System.out.println(Thread.currentThread().getName() + " 减少后: " + counter);
    }
    
    /**
     * 使用synchronized代码块
     * 可以指定锁的对象
     */
    public void incrementWithBlock() {
        synchronized (this) {
            counter++;
            System.out.println(Thread.currentThread().getName() + " 代码块增加后: " + counter);
        }
    }
    
    /**
     * 获取当前计数器值
     * 
     * @return 计数器值
     */
    public synchronized int getCounter() {
        return counter;
    }
    
    /**
     * 生产者方法：向缓冲区添加数据
     * 
     * @param value 要添加的值
     * @throws InterruptedException 如果等待被中断
     */
    public synchronized void produce(int value) throws InterruptedException {
        // 如果缓冲区已满，等待消费者消费
        while (buffer.size() == BUFFER_SIZE) {
            System.out.println("缓冲区已满，生产者等待...");
            wait();
        }
        
        // 添加数据到缓冲区
        buffer.add(value);
        System.out.println("生产者生产: " + value + ", 缓冲区大小: " + buffer.size());
        
        // 通知等待的消费者
        notifyAll();
    }
    
    /**
     * 消费者方法：从缓冲区取出数据
     * 
     * @return 取出的值
     * @throws InterruptedException 如果等待被中断
     */
    public synchronized int consume() throws InterruptedException {
        // 如果缓冲区为空，等待生产者生产
        while (buffer.isEmpty()) {
            System.out.println("缓冲区为空，消费者等待...");
            wait();
        }
        
        // 从缓冲区取出数据
        int value = buffer.remove(0);
        System.out.println("消费者消费: " + value + ", 缓冲区大小: " + buffer.size());
        
        // 通知等待的生产者
        notifyAll();
        
        return value;
    }
    
    /**
     * 获取读锁
     * 
     * @throws InterruptedException 如果等待被中断
     */
    public synchronized void lockRead() throws InterruptedException {
        // 如果有写请求或正在写入，等待
        while (writers > 0 || writeRequests > 0) {
            wait();
        }
        readers++;
    }
    
    /**
     * 释放读锁
     */
    public synchronized void unlockRead() {
        readers--;
        notifyAll();
    }
    
    /**
     * 获取写锁
     * 
     * @throws InterruptedException 如果等待被中断
     */
    public synchronized void lockWrite() throws InterruptedException {
        writeRequests++;
        // 如果有读者或写者，等待
        while (readers > 0 || writers > 0) {
            wait();
        }
        writeRequests--;
        writers++;
    }
    
    /**
     * 释放写锁
     */
    public synchronized void unlockWrite() {
        writers--;
        notifyAll();
    }
    
    /**
     * 演示synchronized关键字的使用
     */
    public void demonstrateSynchronized() {
        // 重置计数器
        counter = 0;
        
        Runnable task = () -> {
            for (int i = 0; i < 5; i++) {
                increment();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };
        
        Thread t1 = new Thread(task, "Thread-A");
        Thread t2 = new Thread(task, "Thread-B");
        
        t1.start();
        t2.start();
        
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("最终计数器值: " + getCounter());
    }
    
    /**
     * 演示生产者-消费者模式
     */
    public void demonstrateProducerConsumer() {
        // 清空缓冲区
        buffer.clear();
        
        // 生产者线程
        Thread producer = new Thread(() -> {
            for (int i = 1; i <= 20; i++) {
                try {
                    produce(i);
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "Producer");
        
        // 消费者线程
        Thread consumer = new Thread(() -> {
            for (int i = 1; i <= 20; i++) {
                try {
                    consume();
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "Consumer");
        
        producer.start();
        consumer.start();
        
        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * 演示读写锁模式
     */
    public void demonstrateReadWriteLock() {
        // 共享数据
        final StringBuilder data = new StringBuilder("Initial Data");
        
        // 读者线程
        Runnable readerTask = () -> {
            try {
                lockRead();
                System.out.println(Thread.currentThread().getName() + " 读取数据: " + data.toString());
                Thread.sleep(100);
                unlockRead();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
        
        // 写者线程
        Runnable writerTask = () -> {
            try {
                lockWrite();
                data.append(" - Modified by ").append(Thread.currentThread().getName());
                System.out.println(Thread.currentThread().getName() + " 写入数据: " + data.toString());
                Thread.sleep(200);
                unlockWrite();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
        
        Thread r1 = new Thread(readerTask, "Reader-1");
        Thread r2 = new Thread(readerTask, "Reader-2");
        Thread w1 = new Thread(writerTask, "Writer-1");
        Thread r3 = new Thread(readerTask, "Reader-3");
        
        r1.start();
        r2.start();
        w1.start();
        r3.start();
        
        try {
            r1.join();
            r2.join();
            w1.join();
            r3.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * 演示wait/notify的注意事项
     */
    public void demonstrateWaitNotify() {
        final Object lock = new Object();
        final boolean[] condition = {false};
        
        // 等待线程
        Thread waiter = new Thread(() -> {
            synchronized (lock) {
                try {
                    System.out.println("等待线程：开始等待条件...");
                    while (!condition[0]) {
                        lock.wait();
                    }
                    System.out.println("等待线程：条件满足，继续执行");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "Waiter");
        
        // 通知线程
        Thread notifier = new Thread(() -> {
            try {
                Thread.sleep(1000);
                synchronized (lock) {
                    System.out.println("通知线程：设置条件并通知");
                    condition[0] = true;
                    lock.notifyAll();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Notifier");
        
        waiter.start();
        notifier.start();
        
        try {
            waiter.join();
            notifier.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * 演示死锁情况（用于教育目的）
     */
    public void demonstrateDeadlock() {
        final Object lock1 = new Object();
        final Object lock2 = new Object();
        
        Thread t1 = new Thread(() -> {
            synchronized (lock1) {
                System.out.println("线程1获取锁1");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("线程1尝试获取锁2...");
                synchronized (lock2) {
                    System.out.println("线程1获取锁2");
                }
            }
        }, "Deadlock-1");
        
        Thread t2 = new Thread(() -> {
            synchronized (lock2) {
                System.out.println("线程2获取锁2");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("线程2尝试获取锁1...");
                synchronized (lock1) {
                    System.out.println("线程2获取锁1");
                }
            }
        }, "Deadlock-2");
        
        t1.start();
        t2.start();
        
        // 注意：这里会产生死锁，仅用于演示
        // 实际运行时会卡住
        try {
            // 等待一段时间，然后中断
            Thread.sleep(2000);
            t1.interrupt();
            t2.interrupt();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("死锁演示结束（实际项目中应避免死锁）");
    }
}
