package com.linsir.spring.framework.spring_core.task.core;

import com.linsir.spring.framework.spring_core.task.support.SimpleAsyncTaskExecutor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 可监听的未来结果测试
 *
 * 测试 ListenableFuture 的回调机制
 *
 * @author linsir
 * @since 1.0.0
 */
class ListenableFutureTest {

    private SimpleAsyncTaskExecutor executor;

    @BeforeEach
    void setUp() {
        executor = new SimpleAsyncTaskExecutor("ListenableTest-");
    }

    @AfterEach
    void tearDown() {
        if (executor != null) {
            executor.shutdown();
        }
    }

    @Test
    void testSuccessCallback() throws InterruptedException {
        // 测试成功回调
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> resultRef = new AtomicReference<>();

        ListenableFuture<String> future = executor.submitListenable(() -> "Success");

        future.addSuccessCallback(result -> {
            resultRef.set(result);
            latch.countDown();
        });

        boolean completed = latch.await(2, TimeUnit.SECONDS);
        assertTrue(completed, "回调应在2秒内执行");
        assertEquals("Success", resultRef.get());
    }

    @Test
    void testFailureCallback() throws InterruptedException {
        // 测试失败回调
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Throwable> exceptionRef = new AtomicReference<>();

        ListenableFuture<?> future = executor.submitListenable((Callable<?>) () -> {
            throw new RuntimeException("Test exception");
        });

        future.addFailureCallback(ex -> {
            exceptionRef.set(ex);
            latch.countDown();
        });

        boolean completed = latch.await(2, TimeUnit.SECONDS);
        assertTrue(completed, "失败回调应在2秒内执行");
        assertNotNull(exceptionRef.get());
    }

    @Test
    void testBothCallbacks() throws InterruptedException {
        // 测试同时添加成功和失败回调
        CountDownLatch successLatch = new CountDownLatch(1);
        CountDownLatch failureLatch = new CountDownLatch(1);
        AtomicBoolean successCalled = new AtomicBoolean(false);
        AtomicBoolean failureCalled = new AtomicBoolean(false);

        ListenableFuture<String> future = executor.submitListenable(() -> "Result");

        future.addCallbacks(
                result -> {
                    successCalled.set(true);
                    successLatch.countDown();
                },
                ex -> {
                    failureCalled.set(true);
                    failureLatch.countDown();
                }
        );

        boolean completed = successLatch.await(2, TimeUnit.SECONDS);
        assertTrue(completed, "成功回调应执行");
        assertTrue(successCalled.get());
        assertFalse(failureCalled.get());
    }

    @Test
    void testMultipleSuccessCallbacks() throws InterruptedException {
        // 测试多个成功回调 - 使用较慢的任务确保回调在添加后才执行
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch latch = new CountDownLatch(2);
        AtomicInteger counter = new AtomicInteger(0);

        ListenableFuture<String> future = executor.submitListenable(() -> {
            startLatch.countDown();
            Thread.sleep(100); // 短暂延迟确保回调先注册
            return "Test";
        });

        // 等待任务开始执行
        startLatch.await(1, TimeUnit.SECONDS);

        future.addSuccessCallback(result -> {
            counter.incrementAndGet();
            latch.countDown();
        });

        future.addSuccessCallback(result -> {
            counter.incrementAndGet();
            latch.countDown();
        });

        boolean completed = latch.await(3, TimeUnit.SECONDS);
        assertTrue(completed, "所有回调应执行");
        assertEquals(2, counter.get());
    }

    @Test
    void testRunnableListenableFuture() throws InterruptedException {
        // 测试 Runnable 的 ListenableFuture
        CountDownLatch taskLatch = new CountDownLatch(1);
        CountDownLatch callbackLatch = new CountDownLatch(1);

        ListenableFuture<?> future = executor.submitListenable(() -> {
            taskLatch.countDown();
        });

        future.addSuccessCallback(result -> callbackLatch.countDown());

        boolean taskCompleted = taskLatch.await(2, TimeUnit.SECONDS);
        boolean callbackCompleted = callbackLatch.await(2, TimeUnit.SECONDS);

        assertTrue(taskCompleted, "任务应完成");
        assertTrue(callbackCompleted, "回调应执行");
    }

    @Test
    void testListenableFutureGet() throws Exception {
        // 测试 ListenableFuture 的 get 方法
        ListenableFuture<Integer> future = executor.submitListenable(() -> 42);

        Integer result = future.get(2, TimeUnit.SECONDS);
        assertEquals(42, result);
    }

    @Test
    void testListenableFutureCancel() {
        // 测试 ListenableFuture 的取消
        CountDownLatch blockLatch = new CountDownLatch(1);

        ListenableFuture<?> future = executor.submitListenable(() -> {
            try {
                blockLatch.await(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        boolean cancelled = future.cancel(true);
        assertTrue(cancelled || future.isDone());
    }
}
