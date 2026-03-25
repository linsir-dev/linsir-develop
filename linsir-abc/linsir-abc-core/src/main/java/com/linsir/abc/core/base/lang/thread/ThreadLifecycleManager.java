package com.linsir.abc.core.base.lang.thread;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程生命周期管理器
 * 
 * 本类演示Java线程的生命周期管理，包括：
 * 1. 线程的创建和启动
 * 2. 线程状态的转换
 * 3. 线程的等待和通知机制
 * 4. 线程的中断处理
 * 
 * 线程生命周期状态：
 * NEW -> RUNNABLE -> RUNNING -> BLOCKED/WAITING/TIMED_WAITING -> TERMINATED
 * 
 * @author linsir
 * @version 1.0
 * @since 1.0
 */
public class ThreadLifecycleManager {
    
    // 线程计数器，用于生成线程名称
    private static final AtomicInteger threadCounter = new AtomicInteger(1);
    
    // 线程状态监控标志
    private volatile boolean isRunning = false;
    private volatile boolean isPaused = false;
    
    /**
     * 创建并启动一个新线程
     * 
     * @param task 线程执行的任务
     * @param threadName 线程名称
     * @return 创建的线程对象
     */
    public Thread createAndStartThread(Runnable task, String threadName) {
        Thread thread = new Thread(task, threadName);
        thread.start();
        return thread;
    }
    
    /**
     * 创建并启动一个守护线程
     * 
     * @param task 线程执行的任务
     * @param threadName 线程名称
     * @return 创建的守护线程对象
     */
    public Thread createDaemonThread(Runnable task, String threadName) {
        Thread thread = new Thread(task, threadName);
        thread.setDaemon(true);
        thread.start();
        return thread;
    }
    
    /**
     * 等待线程执行完成
     * 
     * @param thread 要等待的线程
     * @throws InterruptedException 如果等待被中断
     */
    public void waitForCompletion(Thread thread) throws InterruptedException {
        thread.join();
    }
    
    /**
     * 等待线程执行完成，带超时
     * 
     * @param thread 要等待的线程
     * @param millis 超时时间（毫秒）
     * @return true如果线程在超时前完成，false如果超时
     * @throws InterruptedException 如果等待被中断
     */
    public boolean waitForCompletion(Thread thread, long millis) throws InterruptedException {
        thread.join(millis);
        return !thread.isAlive();
    }
    
    /**
     * 安全地中断线程
     * 
     * @param thread 要中断的线程
     */
    public void interruptThread(Thread thread) {
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }
    }
    
    /**
     * 获取线程的当前状态
     * 
     * @param thread 目标线程
     * @return 线程状态
     */
    public Thread.State getThreadState(Thread thread) {
        return thread.getState();
    }
    
    /**
     * 打印线程状态信息
     * 
     * @param thread 目标线程
     */
    public void printThreadInfo(Thread thread) {
        System.out.println("线程名称: " + thread.getName());
        System.out.println("线程ID: " + thread.getId());
        System.out.println("线程状态: " + thread.getState());
        System.out.println("是否存活: " + thread.isAlive());
        System.out.println("是否守护线程: " + thread.isDaemon());
        System.out.println("优先级: " + thread.getPriority());
        System.out.println("------------------------");
    }
    
    /**
     * 创建一个可控生命周期的任务
     * 
     * @return 可控生命周期任务
     */
    public Runnable createLifecycleTask() {
        return () -> {
            Thread currentThread = Thread.currentThread();
            System.out.println(currentThread.getName() + " 开始执行");
            
            isRunning = true;
            int count = 0;
            
            try {
                while (isRunning && !currentThread.isInterrupted()) {
                    if (isPaused) {
                        System.out.println(currentThread.getName() + " 已暂停");
                        synchronized (this) {
                            while (isPaused) {
                                wait(100);
                            }
                        }
                        System.out.println(currentThread.getName() + " 恢复执行");
                    }
                    
                    // 模拟工作任务
                    count++;
                    System.out.println(currentThread.getName() + " 执行第 " + count + " 次任务");
                    
                    // 检查中断状态
                    if (Thread.interrupted()) {
                        throw new InterruptedException("线程被中断");
                    }
                    
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                System.out.println(currentThread.getName() + " 被中断: " + e.getMessage());
                Thread.currentThread().interrupt();
            } finally {
                isRunning = false;
                System.out.println(currentThread.getName() + " 执行结束");
            }
        };
    }
    
    /**
     * 暂停任务执行
     */
    public synchronized void pause() {
        isPaused = true;
    }
    
    /**
     * 恢复任务执行
     */
    public synchronized void resume() {
        isPaused = false;
        notifyAll();
    }
    
    /**
     * 停止任务执行
     */
    public void stop() {
        isRunning = false;
    }
    
    /**
     * 检查任务是否正在运行
     * 
     * @return true如果正在运行
     */
    public boolean isRunning() {
        return isRunning;
    }
    
    /**
     * 获取下一个线程名称
     * 
     * @param prefix 名称前缀
     * @return 生成的线程名称
     */
    public static String generateThreadName(String prefix) {
        return prefix + "-" + threadCounter.getAndIncrement();
    }
    
    /**
     * 演示线程状态转换
     */
    public void demonstrateLifecycle() {
        Thread thread = new Thread(() -> {
            try {
                System.out.println("线程进入RUNNABLE状态");
                Thread.sleep(1000);
                System.out.println("线程进入TIMED_WAITING状态");
                
                synchronized (this) {
                    wait(500);
                }
                System.out.println("线程继续执行");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "LifecycleDemo");
        
        System.out.println("新建线程状态: " + thread.getState()); // NEW
        
        thread.start();
        System.out.println("启动后状态: " + thread.getState()); // RUNNABLE
        
        try {
            Thread.sleep(100);
            System.out.println("运行中状态: " + thread.getState()); // RUNNABLE or TIMED_WAITING
            
            thread.join();
            System.out.println("结束后状态: " + thread.getState()); // TERMINATED
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
