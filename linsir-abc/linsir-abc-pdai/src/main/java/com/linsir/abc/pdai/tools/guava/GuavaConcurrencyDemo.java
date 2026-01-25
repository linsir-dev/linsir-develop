package com.linsir.abc.pdai.tools.guava;

import com.google.common.util.concurrent.*;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Guava 并发工具示例
 * 演示 Guava 提供的强大并发工具
 */
public class GuavaConcurrencyDemo {

    /**
     * 演示 ListenableFuture
     */
    public static void demonstrateListenableFuture() throws InterruptedException, ExecutionException {
        System.out.println("=== ListenableFuture 示例 ===");
        
        // 创建 ListeningExecutorService
        ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(5));
        
        // 提交任务获取 ListenableFuture
        ListenableFuture<String> future = executorService.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                System.out.println("任务执行中...");
                Thread.sleep(2000);
                return "Hello, ListenableFuture!";
            }
        });
        
        // 添加回调
        future.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("回调执行，任务结果: " + future.get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, executorService);
        
        // 使用 Futures.addCallback
        Futures.addCallback(future, new FutureCallback<String>() {
            @Override
            public void onSuccess(String result) {
                System.out.println("FutureCallback 成功，结果: " + result);
            }
            
            @Override
            public void onFailure(Throwable t) {
                System.out.println("FutureCallback 失败: " + t.getMessage());
            }
        }, executorService);
        
        // 获取结果
        System.out.println("等待任务完成...");
        String result = future.get();
        System.out.println("主线程获取结果: " + result);
        
        // 关闭执行器
        executorService.shutdown();
    }

    /**
     * 演示 RateLimiter
     */
    public static void demonstrateRateLimiter() throws InterruptedException {
        System.out.println("\n=== RateLimiter 示例 ===");
        
        // 创建速率限制器，每秒最多 2 个请求
        RateLimiter rateLimiter = RateLimiter.create(2.0);
        
        // 模拟 10 个请求
        for (int i = 1; i <= 10; i++) {
            // 尝试获取许可
            double waitTime = rateLimiter.acquire();
            System.out.println("请求 " + i + " 获取许可成功，等待时间: " + waitTime + " 秒");
            // 模拟处理时间
            Thread.sleep(100);
        }
        
        // 演示突发请求
        System.out.println("\n演示突发请求:");
        RateLimiter burstRateLimiter = RateLimiter.create(5.0); // 每秒 5 个请求
        
        // 预热
        burstRateLimiter.acquire(3);
        System.out.println("预热后，速率: " + burstRateLimiter.getRate());
        
        // 突发请求
        for (int i = 1; i <= 8; i++) {
            double waitTime = burstRateLimiter.acquire();
            System.out.println("突发请求 " + i + " 获取许可成功，等待时间: " + waitTime + " 秒");
        }
    }

    /**
     * 演示 Striped
     */
    public static void demonstrateStriped() throws InterruptedException {
        System.out.println("\n=== Striped 示例 ===");
        
        // 创建 5 个锁的 Striped
        Striped<Lock> stripedLock = Striped.lock(5);
        
        // 创建计数器
        final AtomicInteger counter = new AtomicInteger(0);
        
        // 创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        
        // 提交 10 个任务，使用不同的键获取锁
        for (int i = 0; i < 10; i++) {
            final int taskId = i;
            final String key = "key" + (i % 3); // 只有 3 个不同的键
            
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    // 获取对应键的锁
                    Lock lock = stripedLock.get(key);
                    lock.lock();
                    try {
                        System.out.println("任务 " + taskId + " 获取锁，键: " + key + ", 计数器当前值: " + counter.get());
                        counter.incrementAndGet();
                        Thread.sleep(500);
                        System.out.println("任务 " + taskId + " 释放锁，键: " + key + ", 计数器新值: " + counter.get());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        lock.unlock();
                    }
                }
            });
        }
        
        // 关闭执行器
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);
    }

    /**
     * 演示 Monitor
     */
    public static void demonstrateMonitor() throws InterruptedException {
        System.out.println("\n=== Monitor 示例 ===");
        
        // 创建一个共享资源
        final BoundedQueue<String> queue = new BoundedQueue<>(3);
        
        // 创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        
        // 提交 5 个生产者任务
        for (int i = 0; i < 5; i++) {
            final int producerId = i;
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        String item = "Item-" + producerId + "-" + System.currentTimeMillis();
                        queue.put(item);
                        System.out.println("生产者 " + producerId + " 放入: " + item);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        
        // 提交 3 个消费者任务
        for (int i = 0; i < 3; i++) {
            final int consumerId = i;
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        String item = queue.take();
                        System.out.println("消费者 " + consumerId + " 取出: " + item);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        
        // 关闭执行器
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
    }

    /**
     * 演示 MoreExecutors
     */
    public static void demonstrateMoreExecutors() throws InterruptedException, ExecutionException {
        System.out.println("\n=== MoreExecutors 示例 ===");
        
        // 创建直接执行的执行器（在当前线程执行）
        ExecutorService directExecutor = MoreExecutors.newDirectExecutorService();
        Future<String> directFuture = directExecutor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                System.out.println("直接执行器执行任务，线程名: " + Thread.currentThread().getName());
                return "Direct executor result";
            }
        });
        System.out.println("直接执行器结果: " + directFuture.get());
        
        // 创建命名线程池
        ListeningExecutorService namedExecutor = MoreExecutors.listeningDecorator(
                Executors.newFixedThreadPool(3, new ThreadFactoryBuilder().setNameFormat("named-thread-%d").build())
        );
        
        for (int i = 0; i < 3; i++) {
            namedExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    System.out.println("命名线程池执行任务，线程名: " + Thread.currentThread().getName());
                }
            });
        }
        
        // 关闭执行器
        directExecutor.shutdown();
        namedExecutor.shutdown();
    }

    /**
     * 演示 Futures 工具类
     */
    public static void demonstrateFutures() throws InterruptedException, ExecutionException {        System.out.println("\n=== Futures 工具类示例 ===");
        
        // 创建执行器
        ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(5));
        
        // 创建多个 ListenableFuture
        ListenableFuture<String> future1 = executorService.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                Thread.sleep(1000);
                return "Result 1";
            }
        });
        
        ListenableFuture<String> future2 = executorService.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                Thread.sleep(1500);
                return "Result 2";
            }
        });
        
        ListenableFuture<String> future3 = executorService.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                Thread.sleep(800);
                return "Result 3";
            }
        });
        
        // 等待所有任务完成
        List<ListenableFuture<String>> futures = Lists.newArrayList(future1, future2, future3);
        ListenableFuture<List<String>> allFutures = Futures.allAsList(futures);
        
        System.out.println("等待所有任务完成...");
        List<String> results = allFutures.get();
        System.out.println("所有任务结果: " + results);
        
        // 获取第一个完成的任务
        ListenableFuture<String> firstCompleted = Futures.firstCompletedOf(futures, executorService);
        System.out.println("第一个完成的任务结果: " + firstCompleted.get());
        
        // 关闭执行器
        executorService.shutdown();
    }

    /**
     * 有界队列，使用 Monitor 实现
     */
    private static class BoundedQueue<E> {
        private final List<E> queue;
        private final int capacity;
        private final Monitor monitor;
        private final Monitor.Guard notFull;
        private final Monitor.Guard notEmpty;
        
        public BoundedQueue(int capacity) {
            this.queue = Lists.newArrayList();
            this.capacity = capacity;
            this.monitor = new Monitor();
            this.notFull = new Monitor.Guard(monitor) {
                @Override
                public boolean isSatisfied() {
                    return queue.size() < capacity;
                }
            };
            this.notEmpty = new Monitor.Guard(monitor) {
                @Override
                public boolean isSatisfied() {
                    return !queue.isEmpty();
                }
            };
        }
        
        public void put(E element) throws InterruptedException {
            if (monitor.enterWhen(notFull)) {
                try {
                    queue.add(element);
                } finally {
                    monitor.leave();
                }
            }
        }
        
        public E take() throws InterruptedException {
            if (monitor.enterWhen(notEmpty)) {
                try {
                    return queue.remove(0);
                } finally {
                    monitor.leave();
                }
            }
            return null;
        }
    }
}
