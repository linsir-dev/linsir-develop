package com.linsir.spring.framework.spring_core.task.core;

import com.linsir.spring.framework.spring_core.task.support.SimpleAsyncTaskExecutor;
import com.linsir.spring.framework.spring_core.task.support.SyncTaskExecutor;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 任务执行器接口测试
 *
 * 测试 TaskExecutor 接口的各种实现和执行行为
 *
 * @author linsir
 * @since 1.0.0
 */
class TaskExecutorTest {

    @Test
    void testSyncTaskExecutorExecute() {
        // 测试同步执行器执行任务
        SyncTaskExecutor executor = SyncTaskExecutor.getInstance();
        AtomicInteger counter = new AtomicInteger(0);

        executor.execute(() -> counter.incrementAndGet());

        // 同步执行，计数器应立即增加
        assertEquals(1, counter.get());
    }

    @Test
    void testSyncTaskExecutorMultipleTasks() {
        // 测试同步执行器执行多个任务
        SyncTaskExecutor executor = SyncTaskExecutor.getInstance();
        AtomicInteger counter = new AtomicInteger(0);

        for (int i = 0; i < 5; i++) {
            executor.execute(() -> counter.incrementAndGet());
        }

        // 所有任务同步执行完成
        assertEquals(5, counter.get());
    }

    @Test
    void testSimpleAsyncTaskExecutorExecute() throws InterruptedException {
        // 测试简单异步执行器
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("Test-");
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger counter = new AtomicInteger(0);

        executor.execute(() -> {
            counter.incrementAndGet();
            latch.countDown();
        });

        // 等待任务完成
        boolean completed = latch.await(2, TimeUnit.SECONDS);
        assertTrue(completed, "任务应在2秒内完成");
        assertEquals(1, counter.get());

        executor.shutdown();
    }

    @Test
    void testSimpleAsyncTaskExecutorMultipleTasks() throws InterruptedException {
        // 测试异步执行器执行多个任务
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("Test-");
        int taskCount = 5;
        CountDownLatch latch = new CountDownLatch(taskCount);
        AtomicInteger counter = new AtomicInteger(0);

        for (int i = 0; i < taskCount; i++) {
            executor.execute(() -> {
                counter.incrementAndGet();
                latch.countDown();
            });
        }

        // 等待所有任务完成
        boolean completed = latch.await(3, TimeUnit.SECONDS);
        assertTrue(completed, "所有任务应在3秒内完成");
        assertEquals(taskCount, counter.get());

        executor.shutdown();
    }

    @Test
    void testExecuteNullTaskThrowsException() {
        // 测试执行null任务抛出异常
        SyncTaskExecutor executor = SyncTaskExecutor.getInstance();

        assertThrows(IllegalArgumentException.class, () -> executor.execute(null));
    }

    @Test
    void testSimpleAsyncExecutorInactive() {
        // 测试关闭后的执行器拒绝任务
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();
        executor.shutdown();

        assertThrows(com.linsir.spring.framework.spring_core.task.exception.TaskRejectedException.class,
                () -> executor.execute(() -> System.out.println("test")));
    }

    @Test
    void testSyncExecutorIsSingleton() {
        // 测试同步执行器是单例
        SyncTaskExecutor executor1 = SyncTaskExecutor.getInstance();
        SyncTaskExecutor executor2 = SyncTaskExecutor.getInstance();

        assertSame(executor1, executor2);
    }

    @Test
    void testSimpleAsyncExecutorThreadNamePrefix() {
        // 测试线程名前缀设置
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();
        executor.setThreadNamePrefix("CustomPrefix-");

        assertEquals("CustomPrefix-", executor.getThreadNamePrefix());
    }

    @Test
    void testSimpleAsyncExecutorConstructorWithPrefix() {
        // 测试带前缀的构造函数
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("MyPrefix-");

        assertEquals("MyPrefix-", executor.getThreadNamePrefix());
    }
}
