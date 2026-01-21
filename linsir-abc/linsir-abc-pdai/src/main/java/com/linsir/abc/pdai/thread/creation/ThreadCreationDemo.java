package com.linsir.abc.pdai.thread.creation;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @author linsir
 * @version 1.0
 * @description: 演示Runnable、Callable和Thread的功能
 * @date 2026/1/21 22:31
 */
public class ThreadCreationDemo {
    // 1. 继承Thread类
    static class MyThread extends Thread {
        private String name;
        
        public MyThread(String name) {
            this.name = name;
        }
        
        @Override
        public void run() {
            System.out.println("1. 继承Thread类: " + name + " 开始执行");
            for (int i = 0; i < 5; i++) {
                System.out.println("1. 继承Thread类: " + name + " 执行中, i = " + i);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("1. 继承Thread类: " + name + " 执行完毕");
        }
    }
    
    // 2. 实现Runnable接口
    static class MyRunnable implements Runnable {
        private String name;
        
        public MyRunnable(String name) {
            this.name = name;
        }
        
        @Override
        public void run() {
            System.out.println("2. 实现Runnable接口: " + name + " 开始执行");
            for (int i = 0; i < 5; i++) {
                System.out.println("2. 实现Runnable接口: " + name + " 执行中, i = " + i);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("2. 实现Runnable接口: " + name + " 执行完毕");
        }
    }
    
    // 3. 实现Callable接口
    static class MyCallable implements Callable<Integer> {
        private String name;
        
        public MyCallable(String name) {
            this.name = name;
        }
        
        @Override
        public Integer call() throws Exception {
            System.out.println("3. 实现Callable接口: " + name + " 开始执行");
            int sum = 0;
            for (int i = 0; i < 5; i++) {
                sum += i;
                System.out.println("3. 实现Callable接口: " + name + " 执行中, i = " + i + ", sum = " + sum);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("3. 实现Callable接口: " + name + " 执行完毕, 结果 = " + sum);
            return sum;
        }
    }
    
    // 4. 使用lambda表达式实现Runnable
    static void testLambdaRunnable() {
        System.out.println("\n4. 使用lambda表达式实现Runnable:");
        Thread thread = new Thread(() -> {
            System.out.println("4. Lambda Runnable: 开始执行");
            for (int i = 0; i < 5; i++) {
                System.out.println("4. Lambda Runnable: 执行中, i = " + i);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("4. Lambda Runnable: 执行完毕");
        }, "LambdaThread");
        thread.start();
        
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    // 5. 使用lambda表达式实现Callable
    static void testLambdaCallable() {
        System.out.println("\n5. 使用lambda表达式实现Callable:");
        Callable<Integer> callable = () -> {
            System.out.println("5. Lambda Callable: 开始执行");
            int sum = 0;
            for (int i = 0; i < 5; i++) {
                sum += i;
                System.out.println("5. Lambda Callable: 执行中, i = " + i + ", sum = " + sum);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("5. Lambda Callable: 执行完毕, 结果 = " + sum);
            return sum;
        };
        
        FutureTask<Integer> futureTask = new FutureTask<>(callable);
        Thread thread = new Thread(futureTask, "LambdaCallableThread");
        thread.start();
        
        try {
            Integer result = futureTask.get();
            System.out.println("5. Lambda Callable: 获取结果 = " + result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        System.out.println("线程创建方式示例");
        
        // 1. 测试继承Thread类
        System.out.println("\n1. 测试继承Thread类:");
        MyThread thread1 = new MyThread("Thread-1");
        MyThread thread2 = new MyThread("Thread-2");
        thread1.start();
        thread2.start();
        
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // 2. 测试实现Runnable接口
        System.out.println("\n2. 测试实现Runnable接口:");
        MyRunnable runnable1 = new MyRunnable("Runnable-1");
        MyRunnable runnable2 = new MyRunnable("Runnable-2");
        Thread thread3 = new Thread(runnable1);
        Thread thread4 = new Thread(runnable2);
        thread3.start();
        thread4.start();
        
        try {
            thread3.join();
            thread4.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // 3. 测试实现Callable接口
        System.out.println("\n3. 测试实现Callable接口:");
        MyCallable callable1 = new MyCallable("Callable-1");
        MyCallable callable2 = new MyCallable("Callable-2");
        FutureTask<Integer> futureTask1 = new FutureTask<>(callable1);
        FutureTask<Integer> futureTask2 = new FutureTask<>(callable2);
        Thread thread5 = new Thread(futureTask1);
        Thread thread6 = new Thread(futureTask2);
        thread5.start();
        thread6.start();
        
        try {
            Integer result1 = futureTask1.get();
            Integer result2 = futureTask2.get();
            System.out.println("3. 测试实现Callable接口: Callable-1 结果 = " + result1);
            System.out.println("3. 测试实现Callable接口: Callable-2 结果 = " + result2);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        
        // 4. 测试lambda表达式实现Runnable
        testLambdaRunnable();
        
        // 5. 测试lambda表达式实现Callable
        testLambdaCallable();
        
        System.out.println("\n所有线程执行完毕");
    }
}