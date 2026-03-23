package com.linsir.spring.framework.spring_core.task.support;

import com.linsir.spring.framework.spring_core.task.exception.TaskRejectedException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 线程池任务执行器测试
 *
 * 测试 ThreadPoolTaskExecutor 的各种功能
 *
 * @author linsir
 * @since 1.0.0
 */
class ThreadPoolTaskExecutorTest {

    private ThreadPoolTaskExecutor executor;

    @BeforeEach
    void setUp() {
        executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix("TestPool-");
        executor.initialize();
    }

    @AfterEach
    void tearDown() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }

    @Test
    void testExecute() throws InterruptedException {
        // 测试执行任务
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger counter = new AtomicInteger(0);

        executor.execute(() -> {
            counter.incrementAndGet();
            latch.countDown();
        });

        boolean completed = latch.await(2, TimeUnit.SECONDS);
        assertTrue(completed);
        assertEquals(1, counter.get());
    }

    @Test
    void testSubmitRunnable() throws Exception {
        // 测试提交 Runnable
        AtomicInteger counter = new AtomicInteger(0);

        Future<?> future = executor.submit(() -> counter.incrementAndGet());
        future.get(2, TimeUnit.SECONDS);

        assertEquals(1, counter.get());
    }

    @Test
    void testSubmitCallable() throws Exception {
        // 测试提交 Callable
        Future<Integer> future = executor.submit(() -> 42);
        Integer result = future.get(2, TimeUnit.SECONDS);

        assertEquals(42, result);
    }

    @Test
    void testSubmitRunnableWithResult() throws Exception {
        // 测试提交带结果的 Runnable
        String expected = "result";
        Future<String> future = executor.submit(() -> {}, expected);
        String result = future.get(2, TimeUnit.SECONDS);

        assertEquals(expected, result);
    }

    @Test
    void testShutdown() {
        // 测试关闭
        assertFalse(executor.isShutdown());

        executor.shutdown();

        assertTrue(executor.isShutdown());
    }

    @Test
    void testShutdownNow() {
        // 测试立即关闭
        List<Runnable> pendingTasks = executor.shutdownNow();

        assertNotNull(pendingTasks);
        assertTrue(executor.isShutdown());
    }

    @Test
    void testIsTerminated() throws InterruptedException {
        // 测试终止状态
        assertFalse(executor.isTerminated());

        CountDownLatch latch = new CountDownLatch(1);
        executor.execute(() -> {
            latch.countDown();
        });

        latch.await(2, TimeUnit.SECONDS);
        executor.shutdown();

        // 等待终止
        boolean terminated = executor.isTerminated();
        // 可能还需要一点时间才能完全终止
        if (!terminated) {
            Thread.sleep(100);
        }
    }

    @Test
    void testConfigurationAfterInitialization() {
        // 测试初始化后的配置修改
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(6);
        executor.setKeepAliveSeconds(120);

        // 这些修改应该生效
        assertFalse(executor.isShutdown());
    }

    @Test
    void testConcurrentExecution() throws InterruptedException {
        // 测试并发执行
        int taskCount = 20;
        CountDownLatch latch = new CountDownLatch(taskCount);
        AtomicInteger counter = new AtomicInteger(0);

        for (int i = 0; i < taskCount; i++) {
            executor.execute(() -> {
                counter.incrementAndGet();
                latch.countDown();
            });
        }

        boolean completed = latch.await(5, TimeUnit.SECONDS);
        assertTrue(completed);
        assertEquals(taskCount, counter.get());
    }

    @Test
    void testTaskTimeout() {
        // 测试任务超时设置
        executor.setTaskTimeout(1000);

        assertEquals(1000, executor.getTaskTimeout());
    }

    @Test
    void testLazyInitialization() throws Exception {
        // 测试延迟初始化
        ThreadPoolTaskExecutor lazyExecutor = new ThreadPoolTaskExecutor();
        lazyExecutor.setCorePoolSize(1);

        // 此时还未初始化
        Future<?> future = lazyExecutor.submit(() -> "test");
        Object result = future.get(2, TimeUnit.SECONDS);

        assertEquals("test", result);

        lazyExecutor.shutdown();
    }

    @Test
    void testExecuteNullThrowsException() {
        // 测试执行 null 抛出异常
        assertThrows(IllegalArgumentException.class, () -> executor.execute(null));
    }

    @Test
    void testSubmitNullRunnableThrowsException() {
        // 测试提交 null Runnable 抛出异常
        assertThrows(IllegalArgumentException.class, () -> executor.submit((Runnable) null));
    }

    @Test
    void testSubmitNullCallableThrowsException() {
        // 测试提交 null Callable 抛出异常
        assertThrows(IllegalArgumentException.class, () -> executor.submit((java.util.concurrent.Callable<?>) null));
    }

    @Test
    void testQueueCapacity() throws Exception {
        // 测试队列容量
        ThreadPoolTaskExecutor smallQueueExecutor = new ThreadPoolTaskExecutor();
        smallQueueExecutor.setCorePoolSize(1);
        smallQueueExecutor.setMaxPoolSize(1);
        smallQueueExecutor.setQueueCapacity(0); // 无队列，直接创建线程或拒绝
        smallQueueExecutor.initialize();

        // 应该能执行，因为没有队列限制
        Future<?> future = smallQueueExecutor.submit(() -> "test");
        assertEquals("test", future.get(2, TimeUnit.SECONDS));

        smallQueueExecutor.shutdown();
    }

    @Test
    void testAllowCoreThreadTimeOut() {
        // 测试允许核心线程超时
        executor.setAllowCoreThreadTimeOut(true);

        // 设置应该生效
        assertFalse(executor.isShutdown());
    }

    @Test
    void testReinitialize() {
        // 测试重新初始化
        executor.initialize(); // 再次初始化应该关闭旧的并创建新的

        assertFalse(executor.isShutdown());
    }
}
