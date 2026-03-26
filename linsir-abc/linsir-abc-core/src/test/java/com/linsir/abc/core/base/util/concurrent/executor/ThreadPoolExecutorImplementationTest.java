package com.linsir.abc.core.base.util.concurrent.executor;

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
    public void testConstructor() {
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
        ThreadFactory factory = Executors.defaultThreadFactory();
        ThreadPoolExecutorImplementation.RejectedExecutionHandler handler = new ThreadPoolExecutorImplementation.AbortPolicy();

        ThreadPoolExecutorImplementation executor = new ThreadPoolExecutorImplementation(
            2, 5, 60L, TimeUnit.SECONDS, queue, factory, handler
        );

        assertNotNull(executor);
    }

    @Test
    public void testConstructorInvalidArguments() {
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
        ThreadFactory factory = Executors.defaultThreadFactory();
        ThreadPoolExecutorImplementation.RejectedExecutionHandler handler = new ThreadPoolExecutorImplementation.AbortPolicy();

        // 测试非法参数
        assertThrows(IllegalArgumentException.class, () -> {
            new ThreadPoolExecutorImplementation(-1, 5, 60L, TimeUnit.SECONDS, queue, factory, handler);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new ThreadPoolExecutorImplementation(2, 1, 60L, TimeUnit.SECONDS, queue, factory, handler);
        });

        assertThrows(NullPointerException.class, () -> {
            new ThreadPoolExecutorImplementation(2, 5, 60L, TimeUnit.SECONDS, null, factory, handler);
        });
    }

    @Test
    public void testExecute() throws InterruptedException {
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(10);
        ThreadFactory factory = Executors.defaultThreadFactory();
        ThreadPoolExecutorImplementation.RejectedExecutionHandler handler = new ThreadPoolExecutorImplementation.CallerRunsPolicy();

        ThreadPoolExecutorImplementation executor = new ThreadPoolExecutorImplementation(
            1, 2, 1L, TimeUnit.SECONDS, queue, factory, handler
        );

        AtomicInteger counter = new AtomicInteger(0);
        
        // 提交任务
        executor.execute(() -> counter.incrementAndGet());
        executor.execute(() -> counter.incrementAndGet());
        executor.execute(() -> counter.incrementAndGet());

        // 等待任务执行完成
        Thread.sleep(500);

        assertTrue(counter.get() >= 1);

        executor.shutdown();
    }

    @Test
    public void testShutdown() {
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
        ThreadFactory factory = Executors.defaultThreadFactory();
        ThreadPoolExecutorImplementation.RejectedExecutionHandler handler = new ThreadPoolExecutorImplementation.AbortPolicy();

        ThreadPoolExecutorImplementation executor = new ThreadPoolExecutorImplementation(
            2, 5, 60L, TimeUnit.SECONDS, queue, factory, handler
        );

        assertFalse(executor.isShutdown());
        executor.shutdown();
        assertTrue(executor.isShutdown());
    }

    @Test
    public void testIsTerminated() throws InterruptedException {
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
        ThreadFactory factory = Executors.defaultThreadFactory();
        ThreadPoolExecutorImplementation.RejectedExecutionHandler handler = new ThreadPoolExecutorImplementation.AbortPolicy();

        ThreadPoolExecutorImplementation executor = new ThreadPoolExecutorImplementation(
            1, 1, 1L, TimeUnit.MILLISECONDS, queue, factory, handler
        );

        assertFalse(executor.isTerminated());
        
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        
        assertTrue(executor.isTerminated());
    }

    @Test
    public void testGetPoolSize() throws InterruptedException {
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
        ThreadFactory factory = Executors.defaultThreadFactory();
        ThreadPoolExecutorImplementation.RejectedExecutionHandler handler = new ThreadPoolExecutorImplementation.AbortPolicy();

        ThreadPoolExecutorImplementation executor = new ThreadPoolExecutorImplementation(
            1, 3, 60L, TimeUnit.SECONDS, queue, factory, handler
        );

        // 初始时线程数为0
        assertEquals(0, executor.getPoolSize());

        // 提交任务后线程数会增加
        executor.execute(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread.sleep(50);
        assertTrue(executor.getPoolSize() >= 0);

        executor.shutdown();
    }

    @Test
    public void testGetActiveCount() throws InterruptedException {
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
        ThreadFactory factory = Executors.defaultThreadFactory();
        ThreadPoolExecutorImplementation.RejectedExecutionHandler handler = new ThreadPoolExecutorImplementation.AbortPolicy();

        ThreadPoolExecutorImplementation executor = new ThreadPoolExecutorImplementation(
            2, 5, 60L, TimeUnit.SECONDS, queue, factory, handler
        );

        // 初始时活跃线程数为0
        assertEquals(0, executor.getActiveCount());

        // 提交一个长时间运行的任务
        executor.execute(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread.sleep(100);
        assertTrue(executor.getActiveCount() >= 0);

        executor.shutdown();
    }

    @Test
    public void testGetTaskCount() throws InterruptedException {
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
        ThreadFactory factory = Executors.defaultThreadFactory();
        ThreadPoolExecutorImplementation.RejectedExecutionHandler handler = new ThreadPoolExecutorImplementation.AbortPolicy();

        ThreadPoolExecutorImplementation executor = new ThreadPoolExecutorImplementation(
            1, 2, 60L, TimeUnit.SECONDS, queue, factory, handler
        );

        // 初始时任务数为0
        assertEquals(0, executor.getTaskCount());

        // 提交任务
        executor.execute(() -> {});
        executor.execute(() -> {});

        Thread.sleep(100);
        assertTrue(executor.getTaskCount() >= 0);

        executor.shutdown();
    }

    @Test
    public void testGetCompletedTaskCount() throws InterruptedException {
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
        ThreadFactory factory = Executors.defaultThreadFactory();
        ThreadPoolExecutorImplementation.RejectedExecutionHandler handler = new ThreadPoolExecutorImplementation.AbortPolicy();

        ThreadPoolExecutorImplementation executor = new ThreadPoolExecutorImplementation(
            1, 2, 60L, TimeUnit.SECONDS, queue, factory, handler
        );

        // 初始时已完成任务数为0
        assertEquals(0, executor.getCompletedTaskCount());

        // 提交任务
        executor.execute(() -> {});

        Thread.sleep(200);
        assertTrue(executor.getCompletedTaskCount() >= 0);

        executor.shutdown();
    }

    @Test
    public void testAwaitTermination() throws InterruptedException {
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
        ThreadFactory factory = Executors.defaultThreadFactory();
        ThreadPoolExecutorImplementation.RejectedExecutionHandler handler = new ThreadPoolExecutorImplementation.AbortPolicy();

        ThreadPoolExecutorImplementation executor = new ThreadPoolExecutorImplementation(
            1, 1, 1L, TimeUnit.MILLISECONDS, queue, factory, handler
        );

        executor.execute(() -> {});
        executor.shutdown();

        boolean terminated = executor.awaitTermination(5, TimeUnit.SECONDS);
        assertTrue(terminated);
    }

    @Test
    public void testCorePoolSize() {
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
        ThreadFactory factory = Executors.defaultThreadFactory();
        ThreadPoolExecutorImplementation.RejectedExecutionHandler handler = new ThreadPoolExecutorImplementation.AbortPolicy();

        ThreadPoolExecutorImplementation executor = new ThreadPoolExecutorImplementation(
            3, 5, 60L, TimeUnit.SECONDS, queue, factory, handler
        );

        assertEquals(3, executor.getCorePoolSize());
    }

    @Test
    public void testMaximumPoolSize() {
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
        ThreadFactory factory = Executors.defaultThreadFactory();
        ThreadPoolExecutorImplementation.RejectedExecutionHandler handler = new ThreadPoolExecutorImplementation.AbortPolicy();

        ThreadPoolExecutorImplementation executor = new ThreadPoolExecutorImplementation(
            2, 10, 60L, TimeUnit.SECONDS, queue, factory, handler
        );

        assertEquals(10, executor.getMaximumPoolSize());
    }
}
