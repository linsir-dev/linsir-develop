package com.linsir.abc.pdai.thread;

/**
 * @author linsir
 * @version 1.0
 * @description: 演示volatile、synchronized和final的功能
 * @date 2026/1/21 22:30
 */
public class ThreadDemo {
    // 1. volatile关键字示例
    static class VolatileDemo {
        // 使用volatile关键字修饰的变量
        private volatile boolean flag = false;
        private int count = 0;
        
        // 写线程
        public void writer() {
            flag = true; // 写volatile变量
            count = 1;   // 写普通变量
        }
        
        // 读线程
        public void reader() {
            if (flag) { // 读volatile变量
                System.out.println("VolatileDemo: flag is true, count = " + count);
            }
        }
        
        public static void test() {
            System.out.println("\n1. Volatile关键字示例:");
            VolatileDemo demo = new VolatileDemo();
            
            // 启动读线程
            Thread readerThread = new Thread(() -> {
                while (!demo.flag) {
                    // 等待flag变为true
                }
                demo.reader();
            });
            readerThread.start();
            
            // 启动写线程
            Thread writerThread = new Thread(() -> {
                try {
                    Thread.sleep(100); // 确保读线程先启动
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                demo.writer();
                System.out.println("VolatileDemo: writer thread finished");
            });
            writerThread.start();
            
            // 等待线程结束
            try {
                readerThread.join();
                writerThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    // 2. synchronized关键字示例
    static class SynchronizedDemo {
        private int count = 0;
        private final Object lock = new Object();
        
        // 方法级别的synchronized
        public synchronized void increment1() {
            count++;
        }
        
        // 代码块级别的synchronized
        public void increment2() {
            synchronized (lock) {
                count++;
            }
        }
        
        // 静态方法级别的synchronized
        public static synchronized void staticMethod() {
            System.out.println("SynchronizedDemo: static synchronized method called");
        }
        
        public int getCount() {
            return count;
        }
        
        public static void test() {
            System.out.println("\n2. Synchronized关键字示例:");
            SynchronizedDemo demo = new SynchronizedDemo();
            int threadCount = 10;
            Thread[] threads = new Thread[threadCount];
            
            // 启动多个线程进行递增操作
            for (int i = 0; i < threadCount; i++) {
                threads[i] = new Thread(() -> {
                    for (int j = 0; j < 1000; j++) {
                        demo.increment1();
                        demo.increment2();
                    }
                });
                threads[i].start();
            }
            
            // 等待所有线程结束
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            System.out.println("SynchronizedDemo: final count = " + demo.getCount());
            System.out.println("期望结果: " + (threadCount * 1000 * 2));
            
            // 测试静态同步方法
            staticMethod();
        }
    }
    
    // 3. final关键字示例
    static class FinalDemo {
        // final修饰的基本类型变量
        private final int finalInt = 10;
        
        // final修饰的引用类型变量
        private final StringBuffer finalBuffer = new StringBuffer("Hello");
        
        // final修饰的方法
        public final void finalMethod() {
            System.out.println("FinalDemo: final method called");
        }
        
        // final修饰的类
        public static final class FinalClass {
            public void method() {
                System.out.println("FinalDemo: final class method called");
            }
        }
        
        public void test() {
            System.out.println("\n3. Final关键字示例:");
            System.out.println("FinalDemo: finalInt = " + finalInt);
            
            // 可以修改final引用类型变量的内部状态，但不能修改引用本身
            finalBuffer.append(" World");
            System.out.println("FinalDemo: finalBuffer = " + finalBuffer);
            // finalBuffer = new StringBuffer("New"); // 编译错误，不能修改final引用
            
            // 调用final方法
            finalMethod();
            
            // 使用final类
            FinalClass finalClass = new FinalClass();
            finalClass.method();
        }
    }
    
    public static void main(String[] args) {
        // 测试volatile关键字
        VolatileDemo.test();
        
        // 测试synchronized关键字
        SynchronizedDemo.test();
        
        // 测试final关键字
        FinalDemo finalDemo = new FinalDemo();
        finalDemo.test();
    }
}