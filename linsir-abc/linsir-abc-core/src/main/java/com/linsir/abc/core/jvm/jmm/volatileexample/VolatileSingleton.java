package com.linsir.abc.core.jvm.jmm.volatileexample;

/**
 * volatile双重检查锁定（DCL）单例模式
 * 
 * 演示volatile在单例模式中的关键作用：
 * 1. 防止指令重排序导致的对象未完全初始化就被使用
 * 2. 保证instance变量的可见性
 * 
 * @author linsir
 * @version 1.0.0
 * @since 2024/1/1
 */
public class VolatileSingleton {
    
    /**
     * volatile修饰的单例实例
     * 关键作用：防止new操作的重排序问题
     * 
     * new操作实际分为三步：
     * 1. 分配内存空间
     * 2. 初始化对象
     * 3. 将引用指向内存地址
     * 
     * 没有volatile时，步骤2和3可能被重排序，
     * 导致其他线程获取到未完全初始化的对象
     */
    private volatile static VolatileSingleton instance;
    
    /**
     * 实例ID，用于验证单例的唯一性
     */
    private final long instanceId;
    
    /**
     * 私有构造方法
     */
    private VolatileSingleton() {
        this.instanceId = System.nanoTime();
        // 模拟耗时初始化
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * 获取单例实例（双重检查锁定）
     * 
     * @return VolatileSingleton实例
     */
    public static VolatileSingleton getInstance() {
        // 第一次检查：避免不必要的同步
        if (instance == null) {
            // 同步块：保证线程安全
            synchronized (VolatileSingleton.class) {
                // 第二次检查：防止多个线程同时通过第一次检查
                if (instance == null) {
                    instance = new VolatileSingleton();
                }
            }
        }
        return instance;
    }
    
    /**
     * 获取实例ID
     * @return 实例ID
     */
    public long getInstanceId() {
        return instanceId;
    }
    
    /**
     * 演示DCL单例的正确性
     */
    public static void main(String[] args) throws InterruptedException {
        final int threadCount = 100;
        Thread[] threads = new Thread[threadCount];
        
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                VolatileSingleton singleton = VolatileSingleton.getInstance();
                System.out.println(Thread.currentThread().getName() + 
                    " got instance with ID: " + singleton.getInstanceId());
            }, "Thread-" + i);
        }
        
        // 同时启动所有线程
        for (Thread thread : threads) {
            thread.start();
        }
        
        // 等待所有线程完成
        for (Thread thread : threads) {
            thread.join();
        }
        
        System.out.println("All threads finished. Instance ID: " + 
            VolatileSingleton.getInstance().getInstanceId());
    }
}
