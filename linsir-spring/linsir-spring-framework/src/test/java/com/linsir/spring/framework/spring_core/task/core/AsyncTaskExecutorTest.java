package com.linsir.spring.framework.spring_core.task.core;

import com.linsir.spring.framework.spring_core.task.support.SimpleAsyncTaskExecutor;
import com.linsir.spring.framework.spring_core.task.support.SyncTaskExecutor;
import com.linsir.spring.framework.spring_core.task.support.ThreadPoolTaskExecutor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 异步任务执行器测试
 *
 * 测试 AsyncTaskExecutor 接口的异步执行和 Future 结果获取
 *
 * @author linsir
 * @since 1.0.0
 */
class AsyncTaskExecutorTest {

    private ThreadPoolTaskExecutor executor;

    @BeforeEach
    void setUp() {
        executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(10);
        executor.initialize();
    }

    @AfterEach
    void tearDown() {
        if (executor != null) {
            executor.shutdown();
        }
    }

    @Test
    void testSubmitRunnable() throws Exception {
        // 测试提交 Runnable 任务
        AtomicInteger counter = new AtomicInteger(0);

        Future<?> future = executor.submit(() -> counter.incrementAndGet());

        // 等待任务完成
        future.get(2, TimeUnit.SECONDS);
        assertEquals(1, counter.get());
    }

    @Test
    void testSubmitCallable() throws Exception {
        // 测试提交 Callable 任务
        Callable<Integer> task = () -> 42;

        Future<Integer> future = executor.submit(task);

        Integer result = future.get(2, TimeUnit.SECONDS);
        assertEquals(42, result);
    }

    @Test
    void testSubmitRunnableWithResult() throws Exception {
        // 测试提交带结果的 Runnable
        String expectedResult = "success";

        Future<String> future = executor.submit(() -> {
            // 执行任务
        }, expectedResult);

        String result = future.get(2, TimeUnit.SECONDS);
        assertEquals(expectedResult, result);
    }

    @Test
    void testSubmitMultipleTasks() throws Exception {
        // 测试提交多个任务
        int taskCount = 10;
        AtomicInteger counter = new AtomicInteger(0);
        Future<?>[] futures = new Future[taskCount];

        for (int i = 0; i < taskCount; i++) {
            futures[i] = executor.submit(() -> counter.incrementAndGet());
        }

        // 等待所有任务完成
        for (Future<?> future : futures) {
            future.get(5, TimeUnit.SECONDS);
        }

        assertEquals(taskCount, counter.get());
    }

    @Test
    void testFutureIsDone() throws Exception {
        // 测试 Future 完成状态
        CountDownLatch latch = new CountDownLatch(1);

        Future<?> future = executor.submit(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            latch.countDown();
        });

        assertFalse(future.isDone());

        latch.await(2, TimeUnit.SECONDS);
        future.get(); // 确保完成

        assertTrue(future.isDone());
        assertFalse(future.isCancelled());
    }

    @Test
    void testFutureCancel() throws Exception {
        // 测试取消任务
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch blockLatch = new CountDownLatch(1);

        Future<?> future = executor.submit(() -> {
            startLatch.countDown();
            try {
                blockLatch.await(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        startLatch.await(2, TimeUnit.SECONDS);
        boolean cancelled = future.cancel(true);

        assertTrue(cancelled || future.isDone());
    }

    @Test
    void testTaskTimeout() {
        // 测试任务超时设置
        executor.setTaskTimeout(5000);

        assertEquals(5000, executor.getTaskTimeout());
    }

    @Test
    void testSimpleAsyncTaskExecutorSubmit() throws Exception {
        // 测试 SimpleAsyncTaskExecutor 提交任务
        SimpleAsyncTaskExecutor simpleExecutor = new SimpleAsyncTaskExecutor();

        Callable<String> task = () -> "Hello";
        Future<String> future = simpleExecutor.submit(task);

        String result = future.get(2, TimeUnit.SECONDS);
        assertEquals("Hello", result);

        simpleExecutor.shutdown();
    }

    @Test
    void testSyncTaskExecutorSubmit() throws Exception {
        // 测试 SyncTaskExecutor 提交任务
        SyncTaskExecutor syncExecutor = SyncTaskExecutor.getInstance();

        Callable<Integer> task = () -> 100;
        Future<Integer> future = syncExecutor.submit(task);

        // 同步执行，立即完成
        assertTrue(future.isDone());
        assertEquals(100, future.get());
    }

    @Test
    void testSubmitNullRunnableThrowsException() {
        // 测试提交 null Runnable 抛出异常
        assertThrows(IllegalArgumentException.class, () -> executor.submit((Runnable) null));
    }

    @Test
    void testSubmitNullCallableThrowsException() {
        // 测试提交 null Callable 抛出异常
        assertThrows(IllegalArgumentException.class, () -> executor.submit((Callable<?>) null));
    }

    @Test
    void testConcurrentTaskExecution() throws Exception {
        // 测试并发任务执行
        int taskCount = 20;
        CountDownLatch latch = new CountDownLatch(taskCount);
        AtomicInteger counter = new AtomicInteger(0);

        for (int i = 0; i < taskCount; i++) {
            executor.submit(() -> {
                counter.incrementAndGet();
                latch.countDown();
            });
        }

        boolean completed = latch.await(5, TimeUnit.SECONDS);
        assertTrue(completed, "所有并发任务应完成");
        assertEquals(taskCount, counter.get());
    }

    @Test
    void testExceptionInCallable() {
        // 测试 Callable 中抛出异常
        Callable<Object> failingTask = () -> {
            throw new RuntimeException("Task failed");
        };

        Future<Object> future = executor.submit(failingTask);

        assertThrows(ExecutionException.class, () -> future.get(2, TimeUnit.SECONDS));
    }

    @Test
    void testThreadPoolConfiguration() {
        // 测试线程池配置
        ThreadPoolTaskExecutor customExecutor = new ThreadPoolTaskExecutor();
        customExecutor.setCorePoolSize(3);
        customExecutor.setMaxPoolSize(8);
        customExecutor.setQueueCapacity(20);
        customExecutor.setThreadNamePrefix("Custom-");
        customExecutor.initialize();

        assertFalse(customExecutor.isShutdown());

        customExecutor.shutdown();
        assertTrue(customExecutor.isShutdown());
    }
}
