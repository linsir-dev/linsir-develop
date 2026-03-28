package com.linsir.abc.core.jvm.jmm.thread;

import java.util.concurrent.TimeUnit;

/**
 * Java线程状态转换示例
 * 
 * 演示Java线程的六种状态：
 * - NEW：新建状态
 * - RUNNABLE：可运行状态（包含Ready和Running）
 * - BLOCKED：阻塞状态（等待监视器锁）
 * - WAITING：等待状态（无限期等待）
 * - TIMED_WAITING：限时等待状态
 * - TERMINATED：终止状态
 * 
 * @author linsir
 * @version 1.0.0
 * @since 2024/1/1
 */
public class ThreadStateDemo {
    
    /**
     * 同步锁对象
     */
    private static final Object lock = new Object();
    
    /**
     * 演示NEW状态
     * 线程被创建但未启动
     */
    public static void demonstrateNewState() {
        Thread thread = new Thread(() -> {
            System.out.println("Running...");
        });
        System.out.println("NEW State: " + thread.getState());  // NEW
    }
    
    /**
     * 演示RUNNABLE状态
     * 线程正在运行或等待CPU时间片
     */
    public static void demonstrateRunnableState() throws InterruptedException {
        Thread thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                // 持续运行
            }
        });
        thread.start();
        Thread.sleep(100);  // 确保线程已经启动
        System.out.println("RUNNABLE State: " + thread.getState());  // RUNNABLE
        thread.interrupt();
        thread.join();
    }
    
    /**
     * 演示BLOCKED状态
     * 线程等待获取监视器锁
     */
    public static void demonstrateBlockedState() throws InterruptedException {
        Thread holder = new Thread(() -> {
            synchronized (lock) {
                try {
                    Thread.sleep(2000);  // 持有锁2秒
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        
        Thread waiter = new Thread(() -> {
            synchronized (lock) {
                System.out.println("Got the lock!");
            }
        });
        
        holder.start();
        Thread.sleep(100);  // 确保holder先获得锁
        waiter.start();
        Thread.sleep(100);  // 确保waiter开始等待锁
        
        System.out.println("BLOCKED State: " + waiter.getState());  // BLOCKED
        
        holder.join();
        waiter.join();
    }
    
    /**
     * 演示WAITING状态
     * 线程无限期等待另一个线程的通知
     */
    public static void demonstrateWaitingState() throws InterruptedException {
        Thread waitingThread = new Thread(() -> {
            synchronized (lock) {
                try {
                    lock.wait();  // 无限期等待
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        
        waitingThread.start();
        Thread.sleep(100);  // 确保线程进入wait状态
        
        System.out.println("WAITING State: " + waitingThread.getState());  // WAITING
        
        synchronized (lock) {
            lock.notify();
        }
        waitingThread.join();
    }
    
    /**
     * 演示TIMED_WAITING状态
     * 线程在指定时间内等待
     */
    public static void demonstrateTimedWaitingState() throws InterruptedException {
        Thread timedThread = new Thread(() -> {
            try {
                Thread.sleep(5000);  // 睡眠5秒
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        timedThread.start();
        Thread.sleep(100);  // 确保线程进入sleep状态
        
        System.out.println("TIMED_WAITING State: " + timedThread.getState());  // TIMED_WAITING
        
        timedThread.join();
    }
    
    /**
     * 演示TERMINATED状态
     * 线程执行完毕或异常退出
     */
    public static void demonstrateTerminatedState() throws InterruptedException {
        Thread thread = new Thread(() -> {
            System.out.println("Thread executing...");
        });
        
        thread.start();
        thread.join();  // 等待线程结束
        
        System.out.println("TERMINATED State: " + thread.getState());  // TERMINATED
    }
    
    /**
     * 完整的状态转换演示
     */
    public static void demonstrateStateTransition() throws InterruptedException {
        System.out.println("\n=== Thread State Transition Demo ===\n");
        
        Thread thread = new Thread(() -> {
            try {
                // RUNNABLE -> TIMED_WAITING -> RUNNABLE
                System.out.println("1. Sleeping...");
                Thread.sleep(500);
                
                // RUNNABLE -> BLOCKED -> RUNNABLE
                System.out.println("2. Trying to acquire lock...");
                synchronized (lock) {
                    System.out.println("3. Got the lock!");
                }
                
                // RUNNABLE -> WAITING -> RUNNABLE
                System.out.println("4. Waiting for notification...");
                synchronized (ThreadStateDemo.class) {
                    ThreadStateDemo.class.wait(1000);  // 限时等待
                }
                
                System.out.println("5. Continuing execution...");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        // NEW
        System.out.println("Initial State: " + thread.getState());
        
        thread.start();
        Thread.sleep(100);
        
        // 观察状态变化
        while (thread.getState() != Thread.State.TERMINATED) {
            System.out.println("Current State: " + thread.getState());
            Thread.sleep(200);
        }
        
        // TERMINATED
        System.out.println("Final State: " + thread.getState());
    }
    
    /**
     * 主方法：运行所有演示
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Java Thread States Demo ===\n");
        
        demonstrateNewState();
        demonstrateRunnableState();
        demonstrateBlockedState();
        demonstrateWaitingState();
        demonstrateTimedWaitingState();
        demonstrateTerminatedState();
        
        demonstrateStateTransition();
        
        System.out.println("\n=== All demonstrations completed ===");
    }
}
