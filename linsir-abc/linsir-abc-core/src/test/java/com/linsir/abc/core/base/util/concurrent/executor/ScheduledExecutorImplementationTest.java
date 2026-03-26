package com.linsir.abc.core.base.util.concurrent.executor;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ScheduledExecutorImplementation测试类
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class ScheduledExecutorImplementationTest {

    @Test
    public void testConstructor() {
        ScheduledExecutorImplementation executor = new ScheduledExecutorImplementation(2);
        assertNotNull(executor);
    }

    @Test
    public void testScheduleRunnable() throws InterruptedException, ExecutionException {
        ScheduledExecutorImplementation executor = new ScheduledExecutorImplementation(1);

        AtomicInteger counter = new AtomicInteger(0);
        ScheduledFuture<?> future = executor.schedule((Runnable) () -> counter.incrementAndGet(), 100, TimeUnit.MILLISECONDS);

        // 任务还未执行
        assertEquals(0, counter.get());

        // 等待任务执行
        future.get();

        assertEquals(1, counter.get());
        executor.shutdown();
    }

    @Test
    public void testScheduleWithCallable() throws InterruptedException, ExecutionException {
        ScheduledExecutorImplementation executor = new ScheduledExecutorImplementation(1);

        ScheduledFuture<String> future = executor.schedule(() -> "Hello", 50, TimeUnit.MILLISECONDS);

        String result = future.get();
        assertEquals("Hello", result);

        executor.shutdown();
    }

    @Test
    public void testScheduleAtFixedRate() throws InterruptedException {
        ScheduledExecutorImplementation executor = new ScheduledExecutorImplementation(1);

        AtomicInteger counter = new AtomicInteger(0);
        ScheduledFuture<?> future = executor.scheduleAtFixedRate(
            () -> counter.incrementAndGet(), 0, 100, TimeUnit.MILLISECONDS
        );

        // 等待执行几次
        Thread.sleep(500);

        assertTrue(counter.get() >= 1, "任务应该至少执行1次，实际执行了 " + counter.get() + " 次");

        future.cancel(false);
        executor.shutdown();
    }

    @Test
    public void testScheduleWithFixedDelay() throws InterruptedException {
        ScheduledExecutorImplementation executor = new ScheduledExecutorImplementation(1);

        AtomicInteger counter = new AtomicInteger(0);
        ScheduledFuture<?> future = executor.scheduleWithFixedDelay(
            () -> counter.incrementAndGet(), 0, 100, TimeUnit.MILLISECONDS
        );

        // 等待执行几次
        Thread.sleep(500);

        assertTrue(counter.get() >= 1, "任务应该至少执行1次，实际执行了 " + counter.get() + " 次");

        future.cancel(false);
        executor.shutdown();
    }

    @Test
    public void testScheduleNullCommand() {
        ScheduledExecutorImplementation executor = new ScheduledExecutorImplementation(1);

        assertThrows(NullPointerException.class, () -> {
            executor.schedule((Runnable) null, 100, TimeUnit.MILLISECONDS);
        });

        executor.shutdown();
    }

    @Test
    public void testScheduleNullUnit() {
        ScheduledExecutorImplementation executor = new ScheduledExecutorImplementation(1);

        assertThrows(NullPointerException.class, () -> {
            executor.schedule((Runnable) () -> {}, 100, null);
        });

        executor.shutdown();
    }

    @Test
    public void testScheduleNegativeDelay() throws InterruptedException, ExecutionException {
        ScheduledExecutorImplementation executor = new ScheduledExecutorImplementation(1);

        AtomicInteger counter = new AtomicInteger(0);
        // 负延迟应该被当作0处理
        ScheduledFuture<?> future = executor.schedule((Runnable) () -> counter.incrementAndGet(), -100, TimeUnit.MILLISECONDS);

        future.get();
        assertEquals(1, counter.get());

        executor.shutdown();
    }

    @Test
    public void testShutdown() {
        ScheduledExecutorImplementation executor = new ScheduledExecutorImplementation(1);

        assertFalse(executor.isShutdown());
        executor.shutdown();
        assertTrue(executor.isShutdown());
    }

    @Test
    public void testIsTerminated() throws InterruptedException {
        ScheduledExecutorImplementation executor = new ScheduledExecutorImplementation(1);

        assertFalse(executor.isTerminated());

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        assertTrue(executor.isTerminated());
    }

    @Test
    public void testAwaitTermination() throws InterruptedException {
        ScheduledExecutorImplementation executor = new ScheduledExecutorImplementation(1);

        executor.schedule((Runnable) () -> {}, 50, TimeUnit.MILLISECONDS);
        executor.shutdown();

        boolean terminated = executor.awaitTermination(5, TimeUnit.SECONDS);
        assertTrue(terminated);
    }

    @Test
    public void testCancelTask() throws InterruptedException {
        ScheduledExecutorImplementation executor = new ScheduledExecutorImplementation(1);

        AtomicInteger counter = new AtomicInteger(0);
        ScheduledFuture<?> future = executor.schedule((Runnable) () -> counter.incrementAndGet(), 500, TimeUnit.MILLISECONDS);

        // 取消任务
        boolean cancelled = future.cancel(false);
        assertTrue(cancelled);
        assertTrue(future.isCancelled());

        Thread.sleep(600);
        // 任务被取消，计数器不应该增加
        assertEquals(0, counter.get());

        executor.shutdown();
    }

    @Test
    public void testGetDelay() {
        ScheduledExecutorImplementation executor = new ScheduledExecutorImplementation(1);

        ScheduledFuture<?> future = executor.schedule((Runnable) () -> {}, 200, TimeUnit.MILLISECONDS);

        // 获取剩余延迟时间
        long delay = future.getDelay(TimeUnit.MILLISECONDS);
        assertTrue(delay > 0);
        assertTrue(delay <= 200);

        future.cancel(false);
        executor.shutdown();
    }

    @Test
    public void testPeriodicTaskCancellation() throws InterruptedException {
        ScheduledExecutorImplementation executor = new ScheduledExecutorImplementation(1);

        AtomicInteger counter = new AtomicInteger(0);
        ScheduledFuture<?> future = executor.scheduleAtFixedRate(
            () -> counter.incrementAndGet(), 0, 50, TimeUnit.MILLISECONDS
        );

        // 等待执行几次
        Thread.sleep(200);
        int countBeforeCancel = counter.get();

        // 取消任务
        future.cancel(false);

        // 等待一段时间，确认不再执行
        Thread.sleep(200);
        int countAfterCancel = counter.get();

        // 取消后不应该再增加太多（可能还有正在执行的任务）
        assertTrue(countAfterCancel - countBeforeCancel <= 1 || !future.isCancelled(),
            "取消后任务不应该继续执行多次");

        executor.shutdown();
    }

    @Test
    public void testScheduleAtFixedRateInvalidPeriod() {
        ScheduledExecutorImplementation executor = new ScheduledExecutorImplementation(1);

        assertThrows(IllegalArgumentException.class, () -> {
            executor.scheduleAtFixedRate(() -> {}, 0, 0, TimeUnit.MILLISECONDS);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            executor.scheduleAtFixedRate(() -> {}, 0, -1, TimeUnit.MILLISECONDS);
        });

        executor.shutdown();
    }

    @Test
    public void testScheduleWithFixedDelayInvalidDelay() {
        ScheduledExecutorImplementation executor = new ScheduledExecutorImplementation(1);

        assertThrows(IllegalArgumentException.class, () -> {
            executor.scheduleWithFixedDelay(() -> {}, 0, 0, TimeUnit.MILLISECONDS);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            executor.scheduleWithFixedDelay(() -> {}, 0, -1, TimeUnit.MILLISECONDS);
        });

        executor.shutdown();
    }

    @Test
    public void testShutdownNow() {
        ScheduledExecutorImplementation executor = new ScheduledExecutorImplementation(1);

        executor.schedule((Runnable) () -> {}, 1000, TimeUnit.MILLISECONDS);

        assertFalse(executor.isShutdown());
        executor.shutdownNow();
        assertTrue(executor.isShutdown());
    }
}
