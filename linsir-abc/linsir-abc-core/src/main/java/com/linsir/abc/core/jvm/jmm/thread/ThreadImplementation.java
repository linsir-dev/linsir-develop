package com.linsir.abc.core.jvm.jmm.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Java线程实现方式示例
 * 
 * 演示三种创建线程的方式：
 * 1. 继承Thread类
 * 2. 实现Runnable接口
 * 3. 实现Callable接口（有返回值）
 * 
 * @author linsir
 * @version 1.0.0
 * @since 2024/1/1
 */
public class ThreadImplementation {
    
    /**
     * 方式1：继承Thread类
     * 优点：简单直接
     * 缺点：无法继承其他类（Java单继承限制）
     */
    public static class MyThread extends Thread {
        
        public MyThread(String name) {
            super(name);
        }
        
        @Override
        public void run() {
            System.out.println("Thread running: " + getName());
            for (int i = 1; i <= 3; i++) {
                System.out.println(getName() + " - Count: " + i);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
    
    /**
     * 方式2：实现Runnable接口
     * 优点：
     * - 可以继承其他类
     * - 资源共享（多个线程可以共享同一个Runnable实例）
     * 缺点：无返回值
     */
    public static class MyRunnable implements Runnable {
        
        private final String name;
        
        public MyRunnable(String name) {
            this.name = name;
        }
        
        @Override
        public void run() {
            System.out.println("Runnable running: " + name);
            for (int i = 1; i <= 3; i++) {
                System.out.println(name + " - Count: " + i);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
    
    /**
     * 方式3：实现Callable接口
     * 优点：
     * - 有返回值
     * - 可以抛出异常
     * 缺点：使用较复杂，需要配合FutureTask
     */
    public static class MyCallable implements Callable<String> {
        
        private final String name;
        
        public MyCallable(String name) {
            this.name = name;
        }
        
        @Override
        public String call() throws Exception {
            System.out.println("Callable running: " + name);
            StringBuilder result = new StringBuilder();
            for (int i = 1; i <= 3; i++) {
                result.append(name).append("-Step").append(i).append(" ");
                Thread.sleep(100);
            }
            return result.toString().trim();
        }
    }
    
    /**
     * 演示资源共享：多个线程共享同一个Runnable
     */
    public static class SharedResource implements Runnable {
        
        private int count = 0;
        
        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                increment();
            }
        }
        
        private synchronized void increment() {
            count++;
        }
        
        public int getCount() {
            return count;
        }
    }
    
    /**
     * 演示三种线程实现方式
     */
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        System.out.println("=== Thread Implementation Demo ===\n");
        
        // 1. 继承Thread类
        System.out.println("1. Extending Thread class:");
        MyThread thread1 = new MyThread("Thread-1");
        thread1.start();
        thread1.join();
        System.out.println();
        
        // 2. 实现Runnable接口
        System.out.println("2. Implementing Runnable interface:");
        Thread thread2 = new Thread(new MyRunnable("Runnable-1"), "Thread-2");
        thread2.start();
        thread2.join();
        System.out.println();
        
        // 3. 实现Callable接口
        System.out.println("3. Implementing Callable interface:");
        MyCallable callable = new MyCallable("Callable-1");
        FutureTask<String> futureTask = new FutureTask<>(callable);
        Thread thread3 = new Thread(futureTask, "Thread-3");
        thread3.start();
        String result = futureTask.get();  // 阻塞等待结果
        System.out.println("Callable result: " + result);
        System.out.println();
        
        // 4. 资源共享演示
        System.out.println("4. Resource Sharing Demo:");
        SharedResource sharedResource = new SharedResource();
        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(sharedResource, "Worker-" + i);
            threads[i].start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        System.out.println("Final count: " + sharedResource.getCount());
        System.out.println();
        
        // 5. Lambda表达式简化（Java 8+）
        System.out.println("5. Lambda Expression:");
        Thread lambdaThread = new Thread(() -> {
            System.out.println("Lambda thread running: " + Thread.currentThread().getName());
        }, "Lambda-Thread");
        lambdaThread.start();
        lambdaThread.join();
        
        System.out.println("\n=== Demo completed ===");
    }
}
