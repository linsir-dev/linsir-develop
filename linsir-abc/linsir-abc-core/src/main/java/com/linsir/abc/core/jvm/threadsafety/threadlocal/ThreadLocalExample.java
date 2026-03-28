package com.linsir.abc.core.jvm.threadsafety.threadlocal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ThreadLocal示例 - ThreadLocalExample
 * 
 * 演示线程本地存储的使用：
 * 1. ThreadLocal的基本使用
 * 2. SimpleDateFormat的线程安全问题
 * 3. ThreadLocal原理
 * 4. 内存泄漏问题及解决方案
 * 
 * ThreadLocal特点：
 * - 每个线程拥有独立的变量副本
 * - 线程间互不干扰
 * - 无需同步即可实现线程安全
 * - 适用于：线程上下文、数据库连接、用户会话等
 * 
 * 注意事项：
 * - 使用完必须调用remove()，防止内存泄漏
 * - 线程池场景下特别注意清理
 * 
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-29
 */
public class ThreadLocalExample {
    
    /**
     * ThreadLocal变量 - 每个线程有自己的SimpleDateFormat实例
     * withInitial是Java 8引入的初始化方式
     */
    private static final ThreadLocal<SimpleDateFormat> dateFormat = 
            ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    
    /**
     * 使用ThreadLocal的SimpleDateFormat格式化日期
     * 每个线程使用自己的实例，避免线程安全问题
     * 
     * @param date 要格式化的日期
     * @return 格式化后的字符串
     */
    public String formatDate(Date date) {
        return dateFormat.get().format(date);
    }
    
    /**
     * 解析日期字符串
     * 
     * @param dateStr 日期字符串
     * @return 解析后的Date对象
     * @throws java.text.ParseException 当解析失败时
     */
    public Date parseDate(String dateStr) throws java.text.ParseException {
        return dateFormat.get().parse(dateStr);
    }
    
    /**
     * 清理ThreadLocal变量
     * 重要：在线程池场景下，线程会被复用，必须清理
     */
    public void clean() {
        dateFormat.remove();
    }
    
    /**
     * 用户上下文示例
     * 演示ThreadLocal在Web应用中的典型使用场景
     */
    public static class UserContext {
        
        /**
         * 存储当前线程的用户信息
         */
        private static final ThreadLocal<User> currentUser = new ThreadLocal<>();
        
        /**
         * 设置当前用户
         * 
         * @param user 用户信息
         */
        public static void setCurrentUser(User user) {
            currentUser.set(user);
        }
        
        /**
         * 获取当前用户
         * 
         * @return 当前用户信息，未设置返回null
         */
        public static User getCurrentUser() {
            return currentUser.get();
        }
        
        /**
         * 清除当前用户
         * 在请求结束时必须调用
         */
        public static void clear() {
            currentUser.remove();
        }
        
        /**
         * 用户类
         */
        public static class User {
            private final Long id;
            private final String username;
            private final String role;
            
            public User(Long id, String username, String role) {
                this.id = id;
                this.username = username;
                this.role = role;
            }
            
            public Long getId() { return id; }
            public String getUsername() { return username; }
            public String getRole() { return role; }
            
            @Override
            public String toString() {
                return "User{id=" + id + ", username='" + username + '\'' + ", role='" + role + '\'' + '}';
            }
        }
    }
    
    /**
     * 线程ID示例
     * 演示每个线程拥有独立的ID
     */
    public static class ThreadId {
        
        /**
         * 原子整数，用于生成唯一的线程ID
         */
        private static final java.util.concurrent.atomic.AtomicInteger nextId = 
                new java.util.concurrent.atomic.AtomicInteger(0);
        
        /**
         * 每个线程的ID
         */
        private static final ThreadLocal<Integer> threadId = 
                ThreadLocal.withInitial(nextId::getAndIncrement);
        
        /**
         * 获取当前线程的ID
         * 
         * @return 线程ID
         */
        public static int get() {
            return threadId.get();
        }
        
        /**
         * 移除线程ID
         */
        public static void remove() {
            threadId.remove();
        }
    }
    
    /**
     * 演示SimpleDateFormat的线程安全问题
     * 以及ThreadLocal如何解决
     */
    public static class DateFormatThreadSafetyDemo {
        
        /**
         * 共享的SimpleDateFormat - 线程不安全
         */
        private static final SimpleDateFormat sharedFormat = 
                new SimpleDateFormat("yyyy-MM-dd");
        
        /**
         * ThreadLocal的SimpleDateFormat - 线程安全
         */
        private static final ThreadLocal<SimpleDateFormat> threadLocalFormat = 
                ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));
        
        /**
         * 使用共享的SimpleDateFormat（线程不安全）
         * 
         * @param dateStr 日期字符串
         * @return 解析结果
         */
        public Date unsafeParse(String dateStr) throws java.text.ParseException {
            synchronized (sharedFormat) {  // 必须同步
                return sharedFormat.parse(dateStr);
            }
        }
        
        /**
         * 使用ThreadLocal的SimpleDateFormat（线程安全）
         * 
         * @param dateStr 日期字符串
         * @return 解析结果
         */
        public Date safeParse(String dateStr) throws java.text.ParseException {
            return threadLocalFormat.get().parse(dateStr);
        }
        
        /**
         * 演示线程安全问题
         * 
         * @throws InterruptedException 当线程被中断时
         */
        public void demonstrateThreadSafety() throws InterruptedException {
            System.out.println("=== SimpleDateFormat Thread Safety Demo ===");
            
            String[] dateStrings = {
                "2024-01-01", "2024-02-02", "2024-03-03", "2024-04-04", "2024-05-05"
            };
            
            // 使用ThreadLocal版本（安全）
            System.out.println("\nUsing ThreadLocal (safe):");
            Thread[] threads = new Thread[5];
            for (int i = 0; i < 5; i++) {
                final int index = i;
                threads[i] = new Thread(() -> {
                    try {
                        Date date = safeParse(dateStrings[index]);
                        System.out.println(Thread.currentThread().getName() + 
                                " parsed: " + date);
                    } catch (Exception e) {
                        System.out.println(Thread.currentThread().getName() + 
                                " error: " + e.getMessage());
                    }
                }, "Thread-" + i);
                threads[i].start();
            }
            
            for (Thread t : threads) {
                t.join();
            }
            
            System.out.println("\nAll threads completed successfully with ThreadLocal!");
        }
    }
    
    /**
     * 演示线程池中的ThreadLocal内存泄漏问题
     */
    public static class ThreadPoolMemoryLeakDemo {
        
        /**
         * 存储大量数据的ThreadLocal
         */
        private static final ThreadLocal<byte[]> largeData = new ThreadLocal<>();
        
        /**
         * 模拟业务操作
         * 
         * @param executor 线程池
         * @param taskId 任务ID
         */
        public void submitTask(ExecutorService executor, int taskId) {
            executor.submit(() -> {
                // 存储大量数据到ThreadLocal
                largeData.set(new byte[1024 * 1024]);  // 1MB
                System.out.println("Task " + taskId + " stored data in ThreadLocal");
                
                // 执行业务逻辑...
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                // 重要：必须清理，否则线程复用时会造成内存泄漏
                largeData.remove();
                System.out.println("Task " + taskId + " cleaned up ThreadLocal");
            });
        }
        
        /**
         * 演示内存泄漏问题
         * 
         * @throws InterruptedException 当线程被中断时
         */
        public void demonstrateMemoryLeak() throws InterruptedException {
            System.out.println("\n=== ThreadPool Memory Leak Demo ===");
            
            // 创建固定大小的线程池
            ExecutorService executor = Executors.newFixedThreadPool(2);
            
            // 提交10个任务，但线程池只有2个线程
            for (int i = 0; i < 10; i++) {
                submitTask(executor, i);
                Thread.sleep(50);
            }
            
            Thread.sleep(1000);
            executor.shutdown();
            System.out.println("All tasks completed. ThreadLocal properly cleaned.");
        }
    }
    
    /**
     * 主方法 - 演示ThreadLocal的使用
     * 
     * @param args 命令行参数
     * @throws InterruptedException 当线程被中断时
     */
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== ThreadLocal Demo ===\n");
        
        // 1. 基本使用
        System.out.println("1. Basic ThreadLocal usage:");
        ThreadLocalExample example = new ThreadLocalExample();
        
        Thread[] threads = new Thread[3];
        for (int i = 0; i < 3; i++) {
            final int threadNum = i;
            threads[i] = new Thread(() -> {
                Date date = new Date(System.currentTimeMillis() + threadNum * 86400000);
                String formatted = example.formatDate(date);
                System.out.println(Thread.currentThread().getName() + 
                        " formatted: " + formatted);
            }, "Formatter-Thread-" + i);
            threads[i].start();
        }
        
        for (Thread t : threads) {
            t.join();
        }
        
        // 2. 用户上下文
        System.out.println("\n2. User Context example:");
        Thread request1 = new Thread(() -> {
            UserContext.setCurrentUser(new UserContext.User(1L, "alice", "ADMIN"));
            System.out.println(Thread.currentThread().getName() + ": " + UserContext.getCurrentUser());
            UserContext.clear();
        }, "Request-1");
        
        Thread request2 = new Thread(() -> {
            UserContext.setCurrentUser(new UserContext.User(2L, "bob", "USER"));
            System.out.println(Thread.currentThread().getName() + ": " + UserContext.getCurrentUser());
            UserContext.clear();
        }, "Request-2");
        
        request1.start();
        request2.start();
        request1.join();
        request2.join();
        
        // 3. 线程ID
        System.out.println("\n3. Thread ID example:");
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + 
                        " has ID: " + ThreadId.get());
            }, "ID-Thread-" + i).start();
        }
        
        Thread.sleep(100);
        
        // 4. SimpleDateFormat线程安全
        System.out.println("\n4. SimpleDateFormat thread safety:");
        DateFormatThreadSafetyDemo dateDemo = new DateFormatThreadSafetyDemo();
        dateDemo.demonstrateThreadSafety();
        
        // 5. 内存泄漏演示
        System.out.println("\n5. Memory leak prevention:");
        ThreadPoolMemoryLeakDemo leakDemo = new ThreadPoolMemoryLeakDemo();
        leakDemo.demonstrateMemoryLeak();
    }
}
