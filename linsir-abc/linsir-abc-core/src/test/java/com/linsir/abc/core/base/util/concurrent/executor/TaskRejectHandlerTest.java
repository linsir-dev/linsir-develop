package com.linsir.abc.core.base.util.concurrent.executor;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TaskRejectHandler测试类
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class TaskRejectHandlerTest {

    @Test
    public void testAbortPolicy() {
        TaskRejectHandler.RejectedExecutionHandler handler = new TaskRejectHandler.AbortPolicy();
        ThreadPoolExecutor executor = createMockExecutor();

        assertThrows(RejectedExecutionException.class, () -> {
            handler.rejectedExecution(() -> {}, executor);
        });
    }

    @Test
    public void testCallerRunsPolicy() {
        TaskRejectHandler.RejectedExecutionHandler handler = new TaskRejectHandler.CallerRunsPolicy();
        ThreadPoolExecutor executor = createMockExecutor();

        AtomicBoolean executed = new AtomicBoolean(false);
        Runnable task = () -> executed.set(true);

        handler.rejectedExecution(task, executor);

        assertTrue(executed.get());
    }

    @Test
    public void testCallerRunsPolicyWhenShutdown() {
        TaskRejectHandler.RejectedExecutionHandler handler = new TaskRejectHandler.CallerRunsPolicy();
        ThreadPoolExecutor executor = createShutdownExecutor();

        AtomicBoolean executed = new AtomicBoolean(false);
        Runnable task = () -> executed.set(true);

        handler.rejectedExecution(task, executor);

        // 关闭状态下不应该执行任务
        assertFalse(executed.get());
    }

    @Test
    public void testDiscardPolicy() {
        TaskRejectHandler.RejectedExecutionHandler handler = new TaskRejectHandler.DiscardPolicy();
        ThreadPoolExecutor executor = createMockExecutor();

        AtomicBoolean executed = new AtomicBoolean(false);
        Runnable task = () -> executed.set(true);

        // 不应该抛出异常
        assertDoesNotThrow(() -> handler.rejectedExecution(task, executor));

        // 任务不应该被执行
        assertFalse(executed.get());
    }

    @Test
    public void testDiscardOldestPolicy() {
        TaskRejectHandler.RejectedExecutionHandler handler = new TaskRejectHandler.DiscardOldestPolicy();
        ThreadPoolExecutor executor = createMockExecutorWithQueue();

        AtomicBoolean executed = new AtomicBoolean(false);
        Runnable task = () -> executed.set(true);

        // 不应该抛出异常
        assertDoesNotThrow(() -> handler.rejectedExecution(task, executor));
    }

    @Test
    public void testDiscardOldestPolicyWhenShutdown() {
        TaskRejectHandler.RejectedExecutionHandler handler = new TaskRejectHandler.DiscardOldestPolicy();
        ThreadPoolExecutor executor = createShutdownExecutor();

        AtomicBoolean executed = new AtomicBoolean(false);
        Runnable task = () -> executed.set(true);

        handler.rejectedExecution(task, executor);

        // 关闭状态下不应该执行任务
        assertFalse(executed.get());
    }

    @Test
    public void testRetryPolicy() {
        TaskRejectHandler.RejectedExecutionHandler handler = new TaskRejectHandler.RetryPolicy(10, 3);
        ThreadPoolExecutor executor = createMockExecutor();

        // 由于mock executor会拒绝，重试后会抛出异常
        assertThrows(RejectedExecutionException.class, () -> {
            handler.rejectedExecution(() -> {}, executor);
        });
    }

    @Test
    public void testRetryPolicyWhenShutdown() {
        TaskRejectHandler.RejectedExecutionHandler handler = new TaskRejectHandler.RetryPolicy(10, 3);
        ThreadPoolExecutor executor = createShutdownExecutor();

        assertThrows(RejectedExecutionException.class, () -> {
            handler.rejectedExecution(() -> {}, executor);
        });
    }

    @Test
    public void testBlockPolicy() {
        TaskRejectHandler.RejectedExecutionHandler handler = new TaskRejectHandler.BlockPolicy(100);
        ThreadPoolExecutor executor = createShutdownExecutor();

        // 由于executor已关闭，应该抛出异常
        assertThrows(RejectedExecutionException.class, () -> {
            handler.rejectedExecution(() -> {}, executor);
        });
    }

    @Test
    public void testBlockPolicyWhenShutdown() {
        TaskRejectHandler.RejectedExecutionHandler handler = new TaskRejectHandler.BlockPolicy(100);
        ThreadPoolExecutor executor = createShutdownExecutor();

        assertThrows(RejectedExecutionException.class, () -> {
            handler.rejectedExecution(() -> {}, executor);
        });
    }

    @Test
    public void testLoggingPolicy() {
        TaskRejectHandler.RejectedExecutionHandler delegate = new TaskRejectHandler.DiscardPolicy();
        TaskRejectHandler.RejectedExecutionHandler handler = new TaskRejectHandler.LoggingPolicy(delegate);
        ThreadPoolExecutor executor = createMockExecutor();

        // 不应该抛出异常
        assertDoesNotThrow(() -> handler.rejectedExecution(() -> {}, executor));
    }

    @Test
    public void testTaskRejectedException() {
        Runnable task = () -> {};
        TaskRejectHandler.TaskRejectedException exception = new TaskRejectHandler.TaskRejectedException("Test", task);

        assertEquals("Test", exception.getMessage());
        assertEquals(task, exception.getTask());
        assertTrue(exception.getRejectTime() > 0);
    }

    @Test
    public void testRejectionStatistics() {
        TaskRejectHandler.RejectionStatistics stats = new TaskRejectHandler.RejectionStatistics();

        assertEquals(0, stats.getTotalRejections());
        assertEquals(0, stats.getLastRejectionTime());
        assertEquals(0, stats.getRejectionCount());

        stats.recordRejection();

        assertEquals(1, stats.getTotalRejections());
        assertTrue(stats.getLastRejectionTime() > 0);
        assertEquals(1, stats.getRejectionCount());

        stats.recordRejection();
        assertEquals(2, stats.getTotalRejections());
        assertEquals(2, stats.getRejectionCount());

        stats.reset();

        assertEquals(0, stats.getTotalRejections());
        assertEquals(0, stats.getLastRejectionTime());
        assertEquals(0, stats.getRejectionCount());
    }

    @Test
    public void testRejectionStatisticsConcurrency() throws InterruptedException {
        TaskRejectHandler.RejectionStatistics stats = new TaskRejectHandler.RejectionStatistics();

        int threadCount = 10;
        int iterations = 100;
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                for (int j = 0; j < iterations; j++) {
                    stats.recordRejection();
                }
                latch.countDown();
            }).start();
        }

        latch.await(5, TimeUnit.SECONDS);

        assertEquals(threadCount * iterations, stats.getRejectionCount());
    }

    // 辅助方法：创建模拟的ThreadPoolExecutor
    private ThreadPoolExecutor createMockExecutor() {
        return new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(1)) {
            @Override
            public void execute(Runnable command) {
                throw new RejectedExecutionException("Mock rejection");
            }
        };
    }

    // 辅助方法：创建已关闭的模拟ThreadPoolExecutor
    private ThreadPoolExecutor createShutdownExecutor() {
        return new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(1)) {
            @Override
            public boolean isShutdown() {
                return true;
            }
        };
    }

    // 辅助方法：创建带有队列的模拟ThreadPoolExecutor
    private ThreadPoolExecutor createMockExecutorWithQueue() {
        return new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(10)) {
            @Override
            public boolean isShutdown() {
                return false;
            }
        };
    }
}
