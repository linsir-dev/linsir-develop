package com.linsir.abc.core.base.lang.thread;

import java.util.HashMap;
import java.util.Map;

/**
 * ThreadLocal上下文管理器
 * 
 * 本类演示ThreadLocal的使用场景和原理：
 * 1. 线程隔离的数据存储
 * 2. 用户会话信息存储
 * 3. 数据库连接管理
 * 4. 事务上下文管理
 * 
 * ThreadLocal原理：
 * - 每个Thread对象内部有一个ThreadLocalMap
 * - ThreadLocal作为key，存储的数据作为value
 * - 线程结束时，ThreadLocalMap会被自动回收
 * 
 * 内存泄漏问题：
 * - ThreadLocalMap的Entry使用弱引用指向ThreadLocal
 * - 但value是强引用，如果线程长时间存活，value可能无法回收
 * - 解决方案：使用完ThreadLocal后调用remove()
 * 
 * @author linsir
 * @version 1.0
 * @since 1.0
 */
public class ThreadLocalContext {
    
    // 线程本地变量：存储用户ID
    private static final ThreadLocal<String> userIdHolder = new ThreadLocal<>();
    
    // 线程本地变量：存储请求ID
    private static final ThreadLocal<String> requestIdHolder = new ThreadLocal<>();
    
    // 线程本地变量：存储上下文Map
    private static final ThreadLocal<Map<String, Object>> contextHolder = 
        ThreadLocal.withInitial(HashMap::new);
    
    // 线程本地变量：存储开始时间（用于性能监控）
    private static final ThreadLocal<Long> startTimeHolder = new ThreadLocal<>();
    
    /**
     * 设置当前线程的用户ID
     * 
     * @param userId 用户ID
     */
    public static void setUserId(String userId) {
        userIdHolder.set(userId);
    }
    
    /**
     * 获取当前线程的用户ID
     * 
     * @return 用户ID，如果没有设置返回null
     */
    public static String getUserId() {
        return userIdHolder.get();
    }
    
    /**
     * 设置当前线程的请求ID
     * 
     * @param requestId 请求ID
     */
    public static void setRequestId(String requestId) {
        requestIdHolder.set(requestId);
    }
    
    /**
     * 获取当前线程的请求ID
     * 
     * @return 请求ID，如果没有设置返回null
     */
    public static String getRequestId() {
        return requestIdHolder.get();
    }
    
    /**
     * 在上下文中存储数据
     * 
     * @param key 键
     * @param value 值
     */
    public static void setAttribute(String key, Object value) {
        contextHolder.get().put(key, value);
    }
    
    /**
     * 从上下文中获取数据
     * 
     * @param key 键
     * @return 值，如果没有返回null
     */
    public static Object getAttribute(String key) {
        return contextHolder.get().get(key);
    }
    
    /**
     * 从上下文中移除数据
     * 
     * @param key 键
     */
    public static void removeAttribute(String key) {
        contextHolder.get().remove(key);
    }
    
    /**
     * 获取当前线程的所有上下文数据
     * 
     * @return 上下文Map的副本
     */
    public static Map<String, Object> getAllAttributes() {
        return new HashMap<>(contextHolder.get());
    }
    
    /**
     * 记录开始时间
     */
    public static void recordStartTime() {
        startTimeHolder.set(System.currentTimeMillis());
    }
    
    /**
     * 获取执行时间（毫秒）
     * 
     * @return 从recordStartTime到现在的时间差（毫秒）
     */
    public static long getElapsedTime() {
        Long startTime = startTimeHolder.get();
        if (startTime == null) {
            return 0;
        }
        return System.currentTimeMillis() - startTime;
    }
    
    /**
     * 清除当前线程的所有ThreadLocal数据
     * 重要：在线程结束时必须调用，防止内存泄漏
     */
    public static void clear() {
        userIdHolder.remove();
        requestIdHolder.remove();
        contextHolder.remove();
        startTimeHolder.remove();
    }
    
    /**
     * 打印当前线程的上下文信息
     */
    public static void printContext() {
        System.out.println("线程: " + Thread.currentThread().getName());
        System.out.println("用户ID: " + getUserId());
        System.out.println("请求ID: " + getRequestId());
        System.out.println("上下文: " + getAllAttributes());
        System.out.println("------------------------");
    }
    
    /**
     * 演示ThreadLocal的线程隔离特性
     */
    public static void demonstrateThreadIsolation() {
        // 主线程设置值
        setUserId("MainUser");
        setRequestId("MainRequest");
        setAttribute("key1", "value1");
        
        System.out.println("主线程设置后的值:");
        printContext();
        
        // 创建子线程
        Thread childThread = new Thread(() -> {
            System.out.println("子线程初始值:");
            printContext();
            
            // 子线程设置自己的值
            setUserId("ChildUser");
            setRequestId("ChildRequest");
            setAttribute("key1", "childValue");
            
            System.out.println("子线程设置后的值:");
            printContext();
        }, "ChildThread");
        
        childThread.start();
        
        try {
            childThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // 主线程的值不受影响
        System.out.println("子线程结束后，主线程的值:");
        printContext();
        
        // 清理
        clear();
    }
    
    /**
     * 演示InheritableThreadLocal
     * 子线程可以继承父线程的ThreadLocal值
     */
    public static void demonstrateInheritableThreadLocal() {
        // 使用InheritableThreadLocal
        ThreadLocal<String> inheritableHolder = new InheritableThreadLocal<>();
        
        inheritableHolder.set("ParentValue");
        System.out.println("父线程值: " + inheritableHolder.get());
        
        Thread childThread = new Thread(() -> {
            // 子线程可以获取父线程的值
            System.out.println("子线程继承的值: " + inheritableHolder.get());
            
            // 子线程修改不影响父线程
            inheritableHolder.set("ChildValue");
            System.out.println("子线程修改后的值: " + inheritableHolder.get());
        });
        
        childThread.start();
        
        try {
            childThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // 父线程的值不变
        System.out.println("子线程结束后，父线程的值: " + inheritableHolder.get());
    }
    
    /**
     * 模拟Web请求处理场景
     */
    public static void simulateWebRequest() {
        Runnable requestHandler = () -> {
            try {
                // 模拟从请求中获取信息
                String threadName = Thread.currentThread().getName();
                setUserId("User-" + threadName);
                setRequestId("REQ-" + System.currentTimeMillis());
                setAttribute("clientIp", "192.168.1." + threadName.hashCode() % 256);
                
                recordStartTime();
                
                System.out.println("处理请求开始: " + getRequestId());
                printContext();
                
                // 模拟业务处理
                Thread.sleep(100);
                
                System.out.println("处理请求结束: " + getRequestId() + 
                    ", 耗时: " + getElapsedTime() + "ms");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                // 必须清理，防止内存泄漏
                clear();
            }
        };
        
        // 模拟多个并发请求
        Thread t1 = new Thread(requestHandler, "Thread-1");
        Thread t2 = new Thread(requestHandler, "Thread-2");
        Thread t3 = new Thread(requestHandler, "Thread-3");
        
        t1.start();
        t2.start();
        t3.start();
        
        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
