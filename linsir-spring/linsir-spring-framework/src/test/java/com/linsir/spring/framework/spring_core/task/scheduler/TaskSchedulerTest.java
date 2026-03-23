package com.linsir.spring.framework.spring_core.task.scheduler;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 任务调度器测试
 *
 * 测试 TaskScheduler 的各种调度功能
 *
 * @author linsir
 * @since 1.0.0
 */
class TaskSchedulerTest {

    private ConcurrentTaskScheduler scheduler;

    @BeforeEach
    void setUp() {
        scheduler = new ConcurrentTaskScheduler(2);
        scheduler.initialize();
    }

    @AfterEach
    void tearDown() {
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }

    @Test
    void testScheduleWithDelay() throws InterruptedException {
        // 测试延迟调度
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger counter = new AtomicInteger(0);
        long startTime = System.currentTimeMillis();

        ScheduledFuture<?> future = scheduler.scheduleWithDelay(() -> {
            counter.incrementAndGet();
            latch.countDown();
        }, 200);

        boolean completed = latch.await(1, TimeUnit.SECONDS);
        long elapsed = System.currentTimeMillis() - startTime;

        assertTrue(completed, "任务应完成");
        assertEquals(1, counter.get());
        assertTrue(elapsed >= 150, "应延迟至少150ms");

        future.cancel(false);
    }

    @Test
    void testScheduleAtFixedRate() throws InterruptedException {
        // 测试固定频率调度
        CountDownLatch latch = new CountDownLatch(3);
        AtomicInteger counter = new AtomicInteger(0);

        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(() -> {
            counter.incrementAndGet();
            latch.countDown();
        }, 100);

        boolean completed = latch.await(1, TimeUnit.SECONDS);

        assertTrue(completed, "应至少执行3次");
        assertTrue(counter.get() >= 3, "计数器应至少为3");

        future.cancel(false);
    }

    @Test
    void testScheduleWithFixedDelay() throws InterruptedException {
        // 测试固定延迟调度
        CountDownLatch latch = new CountDownLatch(2);
        AtomicInteger counter = new AtomicInteger(0);

        ScheduledFuture<?> future = scheduler.scheduleWithFixedDelay(() -> {
            counter.incrementAndGet();
            latch.countDown();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, 100);

        boolean completed = latch.await(1, TimeUnit.SECONDS);

        assertTrue(completed, "应至少执行2次");
        assertTrue(counter.get() >= 2);

        future.cancel(false);
    }

    @Test
    void testScheduleAtSpecificTime() throws InterruptedException {
        // 测试在指定时间调度
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger counter = new AtomicInteger(0);

        // 设置200ms后的时间
        Date futureTime = new Date(System.currentTimeMillis() + 200);

        ScheduledFuture<?> future = scheduler.schedule(() -> {
            counter.incrementAndGet();
            latch.countDown();
        }, futureTime);

        boolean completed = latch.await(1, TimeUnit.SECONDS);

        assertTrue(completed, "任务应在指定时间执行");
        assertEquals(1, counter.get());

        future.cancel(false);
    }

    @Test
    void testExecuteImmediately() throws InterruptedException {
        // 测试立即执行
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger counter = new AtomicInteger(0);

        ScheduledFuture<?> future = scheduler.executeImmediately(() -> {
            counter.incrementAndGet();
            latch.countDown();
        });

        boolean completed = latch.await(500, TimeUnit.MILLISECONDS);

        assertTrue(completed, "任务应立即执行");
        assertEquals(1, counter.get());

        future.cancel(false);
    }

    @Test
    void testScheduleCancel() throws InterruptedException {
        // 测试取消调度任务
        CountDownLatch latch = new CountDownLatch(1);

        ScheduledFuture<?> future = scheduler.scheduleWithDelay(() -> {
            latch.countDown();
        }, 500);

        // 立即取消
        boolean cancelled = future.cancel(false);
        assertTrue(cancelled);

        // 等待一段时间，确认任务未执行
        boolean executed = latch.await(1, TimeUnit.SECONDS);
        assertFalse(executed, "取消的任务不应执行");
    }

    @Test
    void testShutdown() {
        // 测试关闭调度器
        assertFalse(scheduler.isShutdown());

        scheduler.shutdown();

        assertTrue(scheduler.isShutdown());
    }

    @Test
    void testScheduleNullTaskThrowsException() {
        // 测试调度 null 任务抛出异常
        assertThrows(IllegalArgumentException.class,
                () -> scheduler.scheduleWithDelay(null, 100));
    }

    @Test
    void testScheduleNullDateThrowsException() {
        // 测试调度 null 时间抛出异常
        assertThrows(IllegalArgumentException.class,
                () -> scheduler.schedule(() -> {}, null));
    }

    @Test
    void testNegativeDelayThrowsException() {
        // 测试负延迟抛出异常
        assertThrows(IllegalArgumentException.class,
                () -> scheduler.scheduleWithDelay(() -> {}, -1));
    }

    @Test
    void testZeroPeriodThrowsException() {
        // 测试零周期抛出异常
        assertThrows(IllegalArgumentException.class,
                () -> scheduler.scheduleAtFixedRate(() -> {}, 0));
    }

    @Test
    void testMultipleScheduledTasks() throws InterruptedException {
        // 测试多个调度任务
        int taskCount = 5;
        CountDownLatch latch = new CountDownLatch(taskCount);
        AtomicInteger counter = new AtomicInteger(0);

        for (int i = 0; i < taskCount; i++) {
            scheduler.scheduleWithDelay(() -> {
                counter.incrementAndGet();
                latch.countDown();
            }, 50);
        }

        boolean completed = latch.await(2, TimeUnit.SECONDS);

        assertTrue(completed, "所有任务应完成");
        assertEquals(taskCount, counter.get());
    }

    @Test
    void testSchedulerConfiguration() {
        // 测试调度器配置
        ConcurrentTaskScheduler customScheduler = new ConcurrentTaskScheduler(5);
        customScheduler.setThreadNamePrefix("CustomScheduler-");
        customScheduler.setRemoveOnCancelPolicy(true);
        customScheduler.initialize();

        assertFalse(customScheduler.isShutdown());

        customScheduler.shutdown();
        assertTrue(customScheduler.isShutdown());
    }
}
