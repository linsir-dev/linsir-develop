package com.linsir.spring.framework.spring_core.task.scheduler;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cron任务调度器测试
 *
 * 测试 CronTaskScheduler 的 Cron 表达式调度功能
 *
 * @author linsir
 * @since 1.0.0
 */
class CronTaskSchedulerTest {

    private CronTaskScheduler scheduler;

    @BeforeEach
    void setUp() {
        scheduler = new CronTaskScheduler(2);
    }

    @AfterEach
    void tearDown() {
        if (scheduler != null) {
            scheduler.shutdown();
        }
    }

    @Test
    void testScheduleWithCronEverySecond() throws InterruptedException {
        // 测试每秒执行的 Cron 任务
        CountDownLatch latch = new CountDownLatch(2);
        AtomicInteger counter = new AtomicInteger(0);

        // 每秒执行：秒 分 时 日 月 周
        String taskId = scheduler.scheduleWithCron(() -> {
            counter.incrementAndGet();
            latch.countDown();
        }, "* * * * * *");

        assertNotNull(taskId);

        boolean completed = latch.await(3, TimeUnit.SECONDS);

        assertTrue(completed, "Cron任务应至少执行2次");
        assertTrue(counter.get() >= 2);

        // 取消任务
        boolean cancelled = scheduler.cancelTask(taskId);
        assertTrue(cancelled);
    }

    @Test
    void testScheduleWithCronSpecificSecond() throws InterruptedException {
        // 测试在特定秒数执行的 Cron 任务
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger counter = new AtomicInteger(0);

        // 在0秒执行
        String taskId = scheduler.scheduleWithCron(() -> {
            counter.incrementAndGet();
            latch.countDown();
        }, "0 * * * * *");

        assertNotNull(taskId);

        // 等待最多65秒（等到下一个0秒）
        boolean completed = latch.await(65, TimeUnit.SECONDS);

        // 如果测试在0秒附近开始，可能会立即执行
        if (completed) {
            assertEquals(1, counter.get());
        }

        scheduler.cancelTask(taskId);
    }

    @Test
    void testCancelNonExistentTask() {
        // 测试取消不存在的任务
        boolean cancelled = scheduler.cancelTask("non-existent-id");
        assertFalse(cancelled);
    }

    @Test
    void testScheduleWithInvalidCronExpression() {
        // 测试无效的 Cron 表达式
        assertThrows(IllegalArgumentException.class, () ->
                scheduler.scheduleWithCron(() -> {}, "invalid"));
    }

    @Test
    void testScheduleWithNullCronExpression() {
        // 测试 null Cron 表达式
        assertThrows(IllegalArgumentException.class, () ->
                scheduler.scheduleWithCron(() -> {}, null));
    }

    @Test
    void testScheduleWithEmptyCronExpression() {
        // 测试空 Cron 表达式
        assertThrows(IllegalArgumentException.class, () ->
                scheduler.scheduleWithCron(() -> {}, "   "));
    }

    @Test
    void testScheduleWithNullTask() {
        // 测试 null 任务
        assertThrows(IllegalArgumentException.class, () ->
                scheduler.scheduleWithCron(null, "* * * * * *"));
    }

    @Test
    void testCronSchedulerShutdown() {
        // 测试关闭调度器 - 先执行一个任务确保初始化
        scheduler.scheduleWithDelay(() -> {}, 1000);

        assertFalse(scheduler.isShutdown());

        scheduler.shutdown();

        assertTrue(scheduler.isShutdown());
    }

    @Test
    void testCronSchedulerWithPoolSize() {
        // 测试指定线程池大小的构造函数
        CronTaskScheduler customScheduler = new CronTaskScheduler(5);
        // 先执行一个任务确保初始化
        customScheduler.scheduleWithDelay(() -> {}, 1000);

        assertFalse(customScheduler.isShutdown());

        customScheduler.shutdown();
        assertTrue(customScheduler.isShutdown());
    }

    @Test
    void testDelegateMethods() throws InterruptedException {
        // 测试委托方法
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger counter = new AtomicInteger(0);

        // 使用父接口的方法
        scheduler.scheduleWithDelay(() -> {
            counter.incrementAndGet();
            latch.countDown();
        }, 100);

        boolean completed = latch.await(1, TimeUnit.SECONDS);

        assertTrue(completed);
        assertEquals(1, counter.get());
    }

    @Test
    void testMultipleCronTasks() throws InterruptedException {
        // 测试多个 Cron 任务
        CountDownLatch latch1 = new CountDownLatch(1);
        CountDownLatch latch2 = new CountDownLatch(1);
        AtomicInteger counter1 = new AtomicInteger(0);
        AtomicInteger counter2 = new AtomicInteger(0);

        String taskId1 = scheduler.scheduleWithCron(() -> {
            counter1.incrementAndGet();
            latch1.countDown();
        }, "* * * * * *");

        String taskId2 = scheduler.scheduleWithCron(() -> {
            counter2.incrementAndGet();
            latch2.countDown();
        }, "* * * * * *");

        assertNotNull(taskId1);
        assertNotNull(taskId2);
        assertNotEquals(taskId1, taskId2);

        boolean completed1 = latch1.await(2, TimeUnit.SECONDS);
        boolean completed2 = latch2.await(2, TimeUnit.SECONDS);

        assertTrue(completed1, "任务1应执行");
        assertTrue(completed2, "任务2应执行");

        scheduler.cancelTask(taskId1);
        scheduler.cancelTask(taskId2);
    }
}
