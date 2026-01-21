package com.linsir.abc.pdai.thread.juc.executors;

import java.util.concurrent.*;
import java.util.stream.IntStream;

/**
 * @author linsir
 * @version 1.0
 * @description: 演示JUC框架下的Executors的特征
 * @date 2026/1/22 4:33
 */
public class ExecutorsDemo {
    // 1. FixedThreadPool示例
    static class FixedThreadPoolDemo {
        public static void test() {
            System.out.println("1. FixedThreadPool示例:");
            // 创建固定大小的线程池
            ExecutorService executor = Executors.newFixedThreadPool(3);
            
            // 提交10个任务
            IntStream.range(0, 10).forEach(i -> {
                final int taskId = i;
                executor.submit(() -> {
                    System.out.println("任务" + taskId + " 由线程 " + Thread.currentThread().getName() + " 执行");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            });
            
            // 关闭线程池
            executor.shutdown();
            try {
                if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
            System.out.println("FixedThreadPool执行完成");
            System.out.println();
        }
    }
    
    // 2. CachedThreadPool示例
    static class CachedThreadPoolDemo {
        public static void test() {
            System.out.println("2. CachedThreadPool示例:");
            // 创建可缓存的线程池
            ExecutorService executor = Executors.newCachedThreadPool();
            
            // 提交10个任务
            IntStream.range(0, 10).forEach(i -> {
                final int taskId = i;
                executor.submit(() -> {
                    System.out.println("任务" + taskId + " 由线程 " + Thread.currentThread().getName() + " 执行");
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            });
            
            // 关闭线程池
            executor.shutdown();
            try {
                if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
            System.out.println("CachedThreadPool执行完成");
            System.out.println();
        }
    }
    
    // 3. SingleThreadExecutor示例
    static class SingleThreadExecutorDemo {
        public static void test() {
            System.out.println("3. SingleThreadExecutor示例:");
            // 创建单线程的线程池
            ExecutorService executor = Executors.newSingleThreadExecutor();
            
            // 提交10个任务
            IntStream.range(0, 10).forEach(i -> {
                final int taskId = i;
                executor.submit(() -> {
                    System.out.println("任务" + taskId + " 由线程 " + Thread.currentThread().getName() + " 执行");
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            });
            
            // 关闭线程池
            executor.shutdown();
            try {
                if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
            System.out.println("SingleThreadExecutor执行完成");
            System.out.println();
        }
    }
    
    // 4. ScheduledThreadPool示例
    static class ScheduledThreadPoolDemo {
        public static void test() {
            System.out.println("4. ScheduledThreadPool示例:");
            // 创建定时任务线程池
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
            
            // 延迟执行任务
            System.out.println("提交延迟任务，当前时间: " + System.currentTimeMillis());
            executor.schedule(() -> {
                System.out.println("延迟1秒执行的任务，执行时间: " + System.currentTimeMillis());
            }, 1, TimeUnit.SECONDS);
            
            // 周期性执行任务
            System.out.println("提交周期性任务，当前时间: " + System.currentTimeMillis());
            ScheduledFuture<?> future = executor.scheduleAtFixedRate(() -> {
                System.out.println("周期性执行的任务，执行时间: " + System.currentTimeMillis());
            }, 0, 500, TimeUnit.MILLISECONDS);
            
            // 3秒后取消周期性任务
            try {
                Thread.sleep(3000);
                future.cancel(false);
                System.out.println("取消周期性任务");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            // 关闭线程池
            executor.shutdown();
            try {
                if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
            System.out.println("ScheduledThreadPool执行完成");
            System.out.println();
        }
    }
    
    // 5. SingleThreadScheduledExecutor示例
    static class SingleThreadScheduledExecutorDemo {
        public static void test() {
            System.out.println("5. SingleThreadScheduledExecutor示例:");
            // 创建单线程定时任务线程池
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            
            // 延迟执行任务
            System.out.println("提交延迟任务，当前时间: " + System.currentTimeMillis());
            executor.schedule(() -> {
                System.out.println("延迟1秒执行的任务，执行时间: " + System.currentTimeMillis());
            }, 1, TimeUnit.SECONDS);
            
            // 周期性执行任务
            System.out.println("提交周期性任务，当前时间: " + System.currentTimeMillis());
            ScheduledFuture<?> future = executor.scheduleWithFixedDelay(() -> {
                System.out.println("周期性执行的任务，执行时间: " + System.currentTimeMillis());
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }, 0, 500, TimeUnit.MILLISECONDS);
            
            // 3秒后取消周期性任务
            try {
                Thread.sleep(3000);
                future.cancel(false);
                System.out.println("取消周期性任务");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            // 关闭线程池
            executor.shutdown();
            try {
                if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
            System.out.println("SingleThreadScheduledExecutor执行完成");
            System.out.println();
        }
    }
    
    // 6. WorkStealingPool示例
    static class WorkStealingPoolDemo {
        public static void test() {
            System.out.println("6. WorkStealingPool示例:");
            // 创建工作窃取线程池
            ExecutorService executor = Executors.newWorkStealingPool();
            
            // 提交10个任务
            IntStream.range(0, 10).forEach(i -> {
                final int taskId = i;
                executor.submit(() -> {
                    System.out.println("任务" + taskId + " 由线程 " + Thread.currentThread().getName() + " 执行");
                    try {
                        if (taskId % 2 == 0) {
                            Thread.sleep(1000); // 偶数任务执行时间较长
                        } else {
                            Thread.sleep(100); // 奇数任务执行时间较短
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            });
            
            // 关闭线程池
            executor.shutdown();
            try {
                if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
            System.out.println("WorkStealingPool执行完成");
            System.out.println();
        }
    }
    
    // 7. ThreadPoolExecutor自定义线程池示例
    static class CustomThreadPoolDemo {
        public static void test() {
            System.out.println("7. ThreadPoolExecutor自定义线程池示例:");
            // 自定义线程池参数
            int corePoolSize = 2;
            int maximumPoolSize = 5;
            long keepAliveTime = 60L;
            TimeUnit unit = TimeUnit.SECONDS;
            BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(10);
            ThreadFactory threadFactory = Executors.defaultThreadFactory();
            RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();
            
            // 创建自定义线程池
            ThreadPoolExecutor executor = new ThreadPoolExecutor(
                    corePoolSize,
                    maximumPoolSize,
                    keepAliveTime,
                    unit,
                    workQueue,
                    threadFactory,
                    handler
            );
            
            // 提交15个任务
            IntStream.range(0, 15).forEach(i -> {
                final int taskId = i;
                executor.submit(() -> {
                    System.out.println("任务" + taskId + " 由线程 " + Thread.currentThread().getName() + " 执行");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            });
            
            // 关闭线程池
            executor.shutdown();
            try {
                if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
            System.out.println("自定义ThreadPoolExecutor执行完成");
            System.out.println();
        }
    }
    
    // 8. Future和Callable示例
    static class FutureCallableDemo {
        public static void test() {
            System.out.println("8. Future和Callable示例:");
            ExecutorService executor = Executors.newFixedThreadPool(3);
            
            // 提交Callable任务，返回Future
            Future<Integer> future1 = executor.submit(() -> {
                System.out.println("Callable任务1执行中");
                Thread.sleep(1000);
                return 100;
            });
            
            Future<String> future2 = executor.submit(() -> {
                System.out.println("Callable任务2执行中");
                Thread.sleep(1500);
                return "Hello, Callable!";
            });
            
            // 提交Runnable任务，返回Future
            Future<?> future3 = executor.submit(() -> {
                System.out.println("Runnable任务执行中");
                Thread.sleep(500);
            });
            
            // 获取Future结果
            try {
                System.out.println("Callable任务1结果: " + future1.get());
                System.out.println("Callable任务2结果: " + future2.get());
                System.out.println("Runnable任务结果: " + future3.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            
            // 关闭线程池
            executor.shutdown();
            try {
                if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
            System.out.println("Future和Callable示例执行完成");
            System.out.println();
        }
    }
    
    // 9. CompletableFuture示例
    static class CompletableFutureDemo {
        public static void test() {
            System.out.println("9. CompletableFuture示例:");
            
            // 异步执行任务
            CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
                System.out.println("CompletableFuture任务1执行中，线程: " + Thread.currentThread().getName());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "任务1结果";
            });
            
            CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> {
                System.out.println("CompletableFuture任务2执行中，线程: " + Thread.currentThread().getName());
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return 42;
            });
            
            // 组合任务
            CompletableFuture<String> combinedFuture = future1.thenCombine(future2, (result1, result2) -> {
                return result1 + " 和 " + result2;
            });
            
            // 处理结果
            combinedFuture.thenAccept(result -> {
                System.out.println("组合任务结果: " + result);
            });
            
            // 等待所有任务完成
            CompletableFuture.allOf(future1, future2, combinedFuture).join();
            System.out.println("CompletableFuture示例执行完成");
            System.out.println();
        }
    }
    
    // 10. 线程池性能测试
    static class ExecutorPerformanceTest {
        public static void test() {
            System.out.println("10. 线程池性能测试:");
            final int taskCount = 10000;
            final int threadCount = 10;
            
            // 测试FixedThreadPool
            System.out.println("测试FixedThreadPool:");
            ExecutorService fixedExecutor = Executors.newFixedThreadPool(threadCount);
            long startTime = System.currentTimeMillis();
            
            CountDownLatch fixedLatch = new CountDownLatch(taskCount);
            IntStream.range(0, taskCount).forEach(i -> {
                fixedExecutor.submit(() -> {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    fixedLatch.countDown();
                });
            });
            
            try {
                fixedLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            long fixedTime = System.currentTimeMillis() - startTime;
            fixedExecutor.shutdown();
            System.out.println("FixedThreadPool执行时间: " + fixedTime + "ms");
            
            // 测试CachedThreadPool
            System.out.println("测试CachedThreadPool:");
            ExecutorService cachedExecutor = Executors.newCachedThreadPool();
            startTime = System.currentTimeMillis();
            
            CountDownLatch cachedLatch = new CountDownLatch(taskCount);
            IntStream.range(0, taskCount).forEach(i -> {
                cachedExecutor.submit(() -> {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    cachedLatch.countDown();
                });
            });
            
            try {
                cachedLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            long cachedTime = System.currentTimeMillis() - startTime;
            cachedExecutor.shutdown();
            System.out.println("CachedThreadPool执行时间: " + cachedTime + "ms");
            
            // 测试WorkStealingPool
            System.out.println("测试WorkStealingPool:");
            ExecutorService workStealingExecutor = Executors.newWorkStealingPool();
            startTime = System.currentTimeMillis();
            
            CountDownLatch workStealingLatch = new CountDownLatch(taskCount);
            IntStream.range(0, taskCount).forEach(i -> {
                workStealingExecutor.submit(() -> {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    workStealingLatch.countDown();
                });
            });
            
            try {
                workStealingLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            long workStealingTime = System.currentTimeMillis() - startTime;
            workStealingExecutor.shutdown();
            System.out.println("WorkStealingPool执行时间: " + workStealingTime + "ms");
            System.out.println();
        }
    }
    
    public static void main(String[] args) {
        FixedThreadPoolDemo.test();
        CachedThreadPoolDemo.test();
        SingleThreadExecutorDemo.test();
        ScheduledThreadPoolDemo.test();
        SingleThreadScheduledExecutorDemo.test();
        WorkStealingPoolDemo.test();
        CustomThreadPoolDemo.test();
        FutureCallableDemo.test();
        CompletableFutureDemo.test();
        ExecutorPerformanceTest.test();
    }
}
