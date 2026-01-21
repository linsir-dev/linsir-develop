package com.linsir.abc.pdai.juc.atomic;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * @author linsir
 * @version 1.0
 * @description: 演示JUC中的Atomic相关类
 * @date 2026/1/22 0:37
 */
public class AtomicDemo {
    // 1. AtomicInteger示例
    static class AtomicIntegerDemo {
        public static void test() {
            System.out.println("1. AtomicInteger示例:");
            AtomicInteger atomicInt = new AtomicInteger(10);
            
            System.out.println("初始值: " + atomicInt.get());
            System.out.println("getAndIncrement(): " + atomicInt.getAndIncrement()); // 获取当前值并自增
            System.out.println("当前值: " + atomicInt.get());
            System.out.println("incrementAndGet(): " + atomicInt.incrementAndGet()); // 自增并获取新值
            System.out.println("当前值: " + atomicInt.get());
            System.out.println("getAndDecrement(): " + atomicInt.getAndDecrement()); // 获取当前值并自减
            System.out.println("当前值: " + atomicInt.get());
            System.out.println("decrementAndGet(): " + atomicInt.decrementAndGet()); // 自减并获取新值
            System.out.println("当前值: " + atomicInt.get());
            System.out.println("getAndAdd(5): " + atomicInt.getAndAdd(5)); // 获取当前值并加上指定值
            System.out.println("当前值: " + atomicInt.get());
            System.out.println("addAndGet(3): " + atomicInt.addAndGet(3)); // 加上指定值并获取新值
            System.out.println("当前值: " + atomicInt.get());
            
            // 比较并交换（CAS）操作
            boolean swapped = atomicInt.compareAndSet(20, 25);
            System.out.println("compareAndSet(20, 25): " + swapped);
            System.out.println("当前值: " + atomicInt.get());
            
            // 尝试失败的CAS操作
            swapped = atomicInt.compareAndSet(20, 30);
            System.out.println("compareAndSet(20, 30): " + swapped);
            System.out.println("当前值: " + atomicInt.get());
            System.out.println();
        }
    }
    
    // 2. AtomicLong示例
    static class AtomicLongDemo {
        public static void test() {
            System.out.println("2. AtomicLong示例:");
            AtomicLong atomicLong = new AtomicLong(100);
            
            System.out.println("初始值: " + atomicLong.get());
            System.out.println("getAndIncrement(): " + atomicLong.getAndIncrement());
            System.out.println("当前值: " + atomicLong.get());
            System.out.println("incrementAndGet(): " + atomicLong.incrementAndGet());
            System.out.println("当前值: " + atomicLong.get());
            System.out.println("getAndAdd(10): " + atomicLong.getAndAdd(10));
            System.out.println("当前值: " + atomicLong.get());
            System.out.println("addAndGet(20): " + atomicLong.addAndGet(20));
            System.out.println("当前值: " + atomicLong.get());
            
            // 比较并交换操作
            boolean swapped = atomicLong.compareAndSet(142, 200);
            System.out.println("compareAndSet(142, 200): " + swapped);
            System.out.println("当前值: " + atomicLong.get());
            System.out.println();
        }
    }
    
    // 3. AtomicBoolean示例
    static class AtomicBooleanDemo {
        public static void test() {
            System.out.println("3. AtomicBoolean示例:");
            AtomicBoolean atomicBoolean = new AtomicBoolean(false);
            
            System.out.println("初始值: " + atomicBoolean.get());
            System.out.println("getAndSet(true): " + atomicBoolean.getAndSet(true));
            System.out.println("当前值: " + atomicBoolean.get());
            System.out.println("getAndSet(false): " + atomicBoolean.getAndSet(false));
            System.out.println("当前值: " + atomicBoolean.get());
            
            // 比较并交换操作
            boolean swapped = atomicBoolean.compareAndSet(false, true);
            System.out.println("compareAndSet(false, true): " + swapped);
            System.out.println("当前值: " + atomicBoolean.get());
            System.out.println();
        }
    }
    
    // 4. AtomicReference示例
    static class AtomicReferenceDemo {
        static class User {
            private String name;
            private int age;
            
            public User(String name, int age) {
                this.name = name;
                this.age = age;
            }
            
            public String getName() {
                return name;
            }
            
            public int getAge() {
                return age;
            }
            
            @Override
            public String toString() {
                return "User{" +
                        "name='" + name + '\'' +
                        ", age=" + age +
                        '}';
            }
        }
        
        public static void test() {
            System.out.println("4. AtomicReference示例:");
            User user1 = new User("Alice", 20);
            User user2 = new User("Bob", 25);
            
            AtomicReference<User> atomicRef = new AtomicReference<>(user1);
            System.out.println("初始值: " + atomicRef.get());
            
            // 获取并设置新值
            User oldUser = atomicRef.getAndSet(user2);
            System.out.println("getAndSet(user2): " + oldUser);
            System.out.println("当前值: " + atomicRef.get());
            
            // 比较并交换操作
            boolean swapped = atomicRef.compareAndSet(user2, user1);
            System.out.println("compareAndSet(user2, user1): " + swapped);
            System.out.println("当前值: " + atomicRef.get());
            System.out.println();
        }
    }
    
    // 5. AtomicStampedReference示例（解决ABA问题）
    static class AtomicStampedReferenceDemo {
        public static void test() {
            System.out.println("5. AtomicStampedReference示例（解决ABA问题）:");
            String initialRef = "A";
            int initialStamp = 0;
            
            AtomicStampedReference<String> atomicStampedRef = new AtomicStampedReference<>(initialRef, initialStamp);
            System.out.println("初始值: " + atomicStampedRef.getReference() + ", 初始版本: " + atomicStampedRef.getStamp());
            
            // 获取当前值和版本号
            int[] stampHolder = new int[1];
            String currentRef = atomicStampedRef.get(stampHolder);
            int currentStamp = stampHolder[0];
            System.out.println("获取当前值: " + currentRef + ", 当前版本: " + currentStamp);
            
            // 尝试更新值和版本号
            boolean swapped = atomicStampedRef.compareAndSet(currentRef, "B", currentStamp, currentStamp + 1);
            System.out.println("compareAndSet(A, B, 0, 1): " + swapped);
            System.out.println("当前值: " + atomicStampedRef.getReference() + ", 当前版本: " + atomicStampedRef.getStamp());
            
            // 尝试使用旧版本号更新（应该失败）
            swapped = atomicStampedRef.compareAndSet("B", "C", 0, 2);
            System.out.println("compareAndSet(B, C, 0, 2): " + swapped);
            System.out.println("当前值: " + atomicStampedRef.getReference() + ", 当前版本: " + atomicStampedRef.getStamp());
            
            // 使用正确的版本号更新
            currentRef = atomicStampedRef.get(stampHolder);
            currentStamp = stampHolder[0];
            swapped = atomicStampedRef.compareAndSet(currentRef, "C", currentStamp, currentStamp + 1);
            System.out.println("compareAndSet(B, C, 1, 2): " + swapped);
            System.out.println("当前值: " + atomicStampedRef.getReference() + ", 当前版本: " + atomicStampedRef.getStamp());
            System.out.println();
        }
    }
    
    // 6. 原子类在多线程环境中的应用
    static class AtomicInMultiThreadDemo {
        private static AtomicInteger counter = new AtomicInteger(0);
        private static final int THREAD_COUNT = 10;
        private static final int INCREMENT_PER_THREAD = 1000;
        
        public static void test() {
            System.out.println("6. 原子类在多线程环境中的应用:");
            System.out.println("初始计数器值: " + counter.get());
            
            Thread[] threads = new Thread[THREAD_COUNT];
            for (int i = 0; i < THREAD_COUNT; i++) {
                threads[i] = new Thread(() -> {
                    for (int j = 0; j < INCREMENT_PER_THREAD; j++) {
                        counter.incrementAndGet();
                    }
                }, "IncrementThread-" + i);
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
            
            System.out.println("最终计数器值: " + counter.get());
            System.out.println("期望计数器值: " + (THREAD_COUNT * INCREMENT_PER_THREAD));
            System.out.println("计数是否正确: " + (counter.get() == THREAD_COUNT * INCREMENT_PER_THREAD));
            System.out.println();
        }
    }
    
    // 7. 原子类的性能对比（与synchronized相比）
    static class AtomicPerformanceDemo {
        private static int syncCounter = 0;
        private static AtomicInteger atomicCounter = new AtomicInteger(0);
        private static final int THREAD_COUNT = 10;
        private static final int INCREMENT_PER_THREAD = 10000;
        
        // 使用synchronized的计数器
        private static synchronized void incrementSync() {
            syncCounter++;
        }
        
        public static void test() {
            System.out.println("7. 原子类的性能对比（与synchronized相比）:");
            
            // 测试synchronized
            long startTime = System.currentTimeMillis();
            Thread[] syncThreads = new Thread[THREAD_COUNT];
            for (int i = 0; i < THREAD_COUNT; i++) {
                syncThreads[i] = new Thread(() -> {
                    for (int j = 0; j < INCREMENT_PER_THREAD; j++) {
                        incrementSync();
                    }
                });
                syncThreads[i].start();
            }
            
            for (Thread thread : syncThreads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            long syncTime = System.currentTimeMillis() - startTime;
            System.out.println("synchronized执行时间: " + syncTime + "ms, 结果: " + syncCounter);
            
            // 测试AtomicInteger
            startTime = System.currentTimeMillis();
            Thread[] atomicThreads = new Thread[THREAD_COUNT];
            for (int i = 0; i < THREAD_COUNT; i++) {
                atomicThreads[i] = new Thread(() -> {
                    for (int j = 0; j < INCREMENT_PER_THREAD; j++) {
                        atomicCounter.incrementAndGet();
                    }
                });
                atomicThreads[i].start();
            }
            
            for (Thread thread : atomicThreads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            long atomicTime = System.currentTimeMillis() - startTime;
            System.out.println("AtomicInteger执行时间: " + atomicTime + "ms, 结果: " + atomicCounter.get());
            System.out.println("性能提升: " + (syncTime - atomicTime) + "ms");
            System.out.println();
        }
    }
    
    public static void main(String[] args) {
        AtomicIntegerDemo.test();
        AtomicLongDemo.test();
        AtomicBooleanDemo.test();
        AtomicReferenceDemo.test();
        AtomicStampedReferenceDemo.test();
        AtomicInMultiThreadDemo.test();
        AtomicPerformanceDemo.test();
    }
}