package com.linsir.abc.core.base.util.concurrent;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ThreadPoolExecutorImplementation测试类
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class ThreadPoolExecutorImplementationTest {

    @Test
    public void testBasicExecution() throws InterruptedException {
        ThreadPoolExecutorImplementation executor = new ThreadPoolExecutorImplementation(
                2, 4, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(10));

        AtomicInteger counter = new AtomicInteger(0);

        for (int i = 0; i < 5; i++) {
            executor.execute(counter::incrementAndGet);
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));

        assertEquals(5, counter.get());
    }

    @Test
    public void testCorePoolSize() {
        ThreadPoolExecutorImplementation executor = new ThreadPoolExecutorImplementation(
                2, 4, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(10));

        assertEquals(2, executor.getCorePoolSize());
        assertEquals(4, executor.getMaximumPoolSize());
    }

    @Test
    public void testPoolExpansion() throws InterruptedException {
        // 使用同步队列强制创建新线程
        ThreadPoolExecutorImplementation executor = new ThreadPoolExecutorImplementation(
                1, 3, 60L, TimeUnit.SECONDS, new SynchronousQueue<>());

        CountDownLatch latch = new CountDownLatch(3);

        // 提交3个任务，应该创建3个线程
        for (int i = 0; i < 3; i++) {
            executor.execute(() -> {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
        }

        // 等待一段时间让线程创建
        Thread.sleep(50);
        assertTrue(executor.getPoolSize() >= 1);

        latch.await();
        executor.shutdown();
    }

    @Test
    public void testShutdown() throws InterruptedException {
        ThreadPoolExecutorImplementation executor = new ThreadPoolExecutorImplementation(
                2, 4, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(10));

        executor.execute(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        assertFalse(executor.isShutdown());
        executor.shutdown();
        assertTrue(executor.isShutdown());
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
        assertTrue(executor.isTerminated());
    }

    @Test
    public void testShutdownNow() throws InterruptedException {
        ThreadPoolExecutorImplementation executor = new ThreadPoolExecutorImplementation(
                1, 1, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(10));

        // 提交一个长时间运行的任务
        executor.execute(() -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // 提交一个等待的任务
        executor.execute(() -> System.out.println("task2"));

        Thread.sleep(50);

        List<Runnable> pending = executor.shutdownNow();
        assertTrue(executor.isShutdown());

        // 等待任务被取消
        Thread.sleep(100);
    }

    @Test
    public void testAbortPolicy() {
        ThreadPoolExecutorImplementation executor = new ThreadPoolExecutorImplementation(
                1, 1, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1),
                new ThreadPoolExecutorImplementation.AbortPolicy());

        // 填充线程和队列
        executor.execute(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        executor.execute(() -> {});

        // 应该抛出异常
        assertThrows(RejectedExecutionException.class, () -> {
            executor.execute(() -> {});
        });

        executor.shutdownNow();
    }

    @Test
    public void testCallerRunsPolicy() throws InterruptedException {
        Thread mainThread = Thread.currentThread();
        AtomicInteger executed = new AtomicInteger(0);

        ThreadPoolExecutorImplementation executor = new ThreadPoolExecutorImplementation(
                1, 1, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1),
                new ThreadPoolExecutorImplementation.CallerRunsPolicy());

        // 填充线程和队列
        executor.execute(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        executor.execute(() -> {});

        // 这个任务应该由调用者线程执行
        executor.execute(() -> {
            executed.incrementAndGet();
        });

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        assertEquals(1, executed.get());
    }

    @Test
    public void testDiscardPolicy() throws InterruptedException {
        ThreadPoolExecutorImplementation executor = new ThreadPoolExecutorImplementation(
                1, 1, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1),
                new ThreadPoolExecutorImplementation.DiscardPolicy());

        // 填充线程和队列
        executor.execute(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        executor.execute(() -> {});

        // 这个任务应该被静默丢弃
        assertDoesNotThrow(() -> executor.execute(() -> {}));

        executor.shutdownNow();
    }

    @Test
    public void testTaskCount() throws InterruptedException {
        ThreadPoolExecutorImplementation executor = new ThreadPoolExecutorImplementation(
                2, 2, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(10));

        int taskCount = 10;
        CountDownLatch latch = new CountDownLatch(taskCount);

        for (int i = 0; i < taskCount; i++) {
            executor.execute(() -> {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        assertEquals(taskCount, executor.getCompletedTaskCount());
        assertEquals(taskCount, executor.getTaskCount());

        executor.shutdown();
    }

    @Test
    public void testActiveCount() throws InterruptedException {
        ThreadPoolExecutorImplementation executor = new ThreadPoolExecutorImplementation(
                2, 2, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(10));

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(2);

        for (int i = 0; i < 2; i++) {
            executor.execute(() -> {
                try {
                    startLatch.countDown();
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            });
        }

        // 等待任务开始
        startLatch.await();
        Thread.sleep(50);

        // 应该有2个活动线程
        assertEquals(2, executor.getActiveCount());

        endLatch.await();
        executor.shutdown();
    }
}
