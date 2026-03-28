package com.linsir.abc.core.jvm.threadsafety.sync;

/**
 * synchronized关键字示例 - SynchronizedCounter
 * 
 * 演示synchronized的三种用法：
 * 1. 同步实例方法
 * 2. 同步静态方法
 * 3. 同步代码块
 * 
 * synchronized特点：
 * - 可重入：同一线程可以多次获取同一把锁
 * - 非公平：锁的获取不保证顺序
 * - 不可中断：获取锁的过程不能响应中断
 * - 自动释放：代码执行完毕或异常时自动释放锁
 * 
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-29
 */
public class SynchronizedCounter {
    
    /**
     * 实例计数器
     */
    private int count = 0;
    
    /**
     * 静态计数器
     */
    private static int staticCount = 0;
    
    /**
     * 同步实例方法 - 锁对象为当前实例(this)
     * 
     * 适用于：保护实例变量
     */
    public synchronized void increment() {
        count++;
    }
    
    /**
     * 同步实例方法 - 获取当前值
     * 
     * @return 当前计数值
     */
    public synchronized int getCount() {
        return count;
    }
    
    /**
     * 同步实例方法 - 递减
     */
    public synchronized void decrement() {
        count--;
    }
    
    /**
     * 同步静态方法 - 锁对象为类的Class对象(SynchronizedCounter.class)
     * 
     * 适用于：保护静态变量
     */
    public static synchronized void incrementStatic() {
        staticCount++;
    }
    
    /**
     * 同步静态方法 - 获取静态计数器值
     * 
     * @return 静态计数值
     */
    public static synchronized int getStaticCount() {
        return staticCount;
    }
    
    /**
     * 同步代码块 - 可指定任意对象作为锁
     * 
     * 优点：
     * 1. 精确控制同步范围，减少锁持有时间
     * 2. 可选择不同的锁对象实现细粒度锁
     * 
     * @param value 要添加的值
     */
    public void add(int value) {
        // 前置处理（无需同步）
        System.out.println(Thread.currentThread().getName() + " preparing to add " + value);
        
        // 同步代码块 - 使用this作为锁
        synchronized (this) {
            int newValue = count + value;
            // 模拟一些处理时间
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            count = newValue;
        }
        
        // 后置处理（无需同步）
        System.out.println(Thread.currentThread().getName() + " finished adding " + value);
    }
    
    /**
     * 使用独立的锁对象 - 细粒度锁示例
     */
    private final Object lockA = new Object();
    private final Object lockB = new Object();
    
    private int valueA = 0;
    private int valueB = 0;
    
    /**
     * 修改valueA - 使用lockA
     * 
     * @param value 新值
     */
    public void setValueA(int value) {
        synchronized (lockA) {
            valueA = value;
        }
    }
    
    /**
     * 修改valueB - 使用lockB
     * 
     * @param value 新值
     */
    public void setValueB(int value) {
        synchronized (lockB) {
            valueB = value;
        }
    }
    
    /**
     * 获取valueA
     * 
     * @return valueA的值
     */
    public int getValueA() {
        synchronized (lockA) {
            return valueA;
        }
    }
    
    /**
     * 获取valueB
     * 
     * @return valueB的值
     */
    public int getValueB() {
        synchronized (lockB) {
            return valueB;
        }
    }
    
    /**
     * 可重入性演示 - 同一线程可多次获取同一把锁
     */
    public synchronized void methodA() {
        System.out.println(Thread.currentThread().getName() + " in methodA");
        methodB();  // 同一线程可以再次获取锁
    }
    
    public synchronized void methodB() {
        System.out.println(Thread.currentThread().getName() + " in methodB");
        methodC();  // 继续重入
    }
    
    public synchronized void methodC() {
        System.out.println(Thread.currentThread().getName() + " in methodC");
    }
    
    /**
     * 主方法 - 演示synchronized的使用
     * 
     * @param args 命令行参数
     * @throws InterruptedException 当线程被中断时
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Synchronized Counter Demo ===\n");
        
        // 1. 演示同步实例方法
        System.out.println("1. Testing synchronized instance methods:");
        SynchronizedCounter counter = new SynchronizedCounter();
        
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    counter.increment();
                }
            }, "Thread-" + i);
            threads[i].start();
        }
        
        for (Thread t : threads) {
            t.join();
        }
        
        System.out.println("Expected: 1000, Actual: " + counter.getCount());
        
        // 2. 演示同步静态方法
        System.out.println("\n2. Testing synchronized static methods:");
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    SynchronizedCounter.incrementStatic();
                }
            }, "Static-Thread-" + i).start();
        }
        
        Thread.sleep(500);
        System.out.println("Static count: " + SynchronizedCounter.getStaticCount());
        
        // 3. 演示可重入性
        System.out.println("\n3. Testing reentrancy:");
        counter.methodA();
        
        // 4. 演示细粒度锁
        System.out.println("\n4. Testing fine-grained locking:");
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                counter.setValueA(i);
            }
        }, "ValueA-Thread");
        
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                counter.setValueB(i);
            }
        }, "ValueB-Thread");
        
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        
        System.out.println("ValueA: " + counter.getValueA());
        System.out.println("ValueB: " + counter.getValueB());
    }
}
