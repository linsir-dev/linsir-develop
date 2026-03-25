package com.linsir.abc.core.base.lang.thread;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * ThreadLifecycleManager测试类
 */
public class ThreadLifecycleManagerTest {

    /**
     * 测试创建和启动线程
     */
    @Test
    public void testCreateAndStartThread() throws InterruptedException {
        ThreadLifecycleManager manager = new ThreadLifecycleManager();
        final boolean[] executed = {false};

        Thread thread = manager.createAndStartThread(() -> {
            executed[0] = true;
        }, "TestThread");

        assertNotNull(thread);
        assertEquals("TestThread", thread.getName());

        manager.waitForCompletion(thread);
        assertTrue(executed[0]);
    }

    /**
     * 测试创建守护线程
     */
    @Test
    public void testCreateDaemonThread() {
        ThreadLifecycleManager manager = new ThreadLifecycleManager();

        Thread thread = manager.createDaemonThread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "DaemonThread");

        assertNotNull(thread);
        assertTrue(thread.isDaemon());
    }

    /**
     * 测试等待线程完成
     */
    @Test
    public void testWaitForCompletion() throws InterruptedException {
        ThreadLifecycleManager manager = new ThreadLifecycleManager();

        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        thread.start();
        long startTime = System.currentTimeMillis();
        manager.waitForCompletion(thread);
        long endTime = System.currentTimeMillis();

        assertTrue(endTime - startTime >= 100);
        assertFalse(thread.isAlive());
    }

    /**
     * 测试带超时的等待
     */
    @Test
    public void testWaitForCompletionWithTimeout() throws InterruptedException {
        ThreadLifecycleManager manager = new ThreadLifecycleManager();

        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        thread.start();
        boolean completed = manager.waitForCompletion(thread, 100);

        assertFalse(completed);
        assertTrue(thread.isAlive());

        thread.join();
    }

    /**
     * 测试中断线程
     */
    @Test
    public void testInterruptThread() throws InterruptedException {
        ThreadLifecycleManager manager = new ThreadLifecycleManager();
        final boolean[] interrupted = {false};

        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                interrupted[0] = true;
                Thread.currentThread().interrupt();
            }
        });

        thread.start();
        Thread.sleep(100);
        manager.interruptThread(thread);
        thread.join();

        assertTrue(interrupted[0]);
    }

    /**
     * 测试获取线程状态
     */
    @Test
    public void testGetThreadState() throws InterruptedException {
        ThreadLifecycleManager manager = new ThreadLifecycleManager();

        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        assertEquals(Thread.State.NEW, manager.getThreadState(thread));

        thread.start();
        Thread.sleep(10);
        Thread.State state = manager.getThreadState(thread);
        assertTrue(state == Thread.State.RUNNABLE || state == Thread.State.TIMED_WAITING);

        thread.join();
        assertEquals(Thread.State.TERMINATED, manager.getThreadState(thread));
    }

    /**
     * 测试生成线程名称
     */
    @Test
    public void testGenerateThreadName() {
        String name1 = ThreadLifecycleManager.generateThreadName("Prefix");
        String name2 = ThreadLifecycleManager.generateThreadName("Prefix");

        assertTrue(name1.startsWith("Prefix-"));
        assertTrue(name2.startsWith("Prefix-"));
        assertNotEquals(name1, name2);
    }
}
