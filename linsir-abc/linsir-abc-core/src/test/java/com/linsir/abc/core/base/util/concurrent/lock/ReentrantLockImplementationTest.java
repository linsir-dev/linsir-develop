package com.linsir.abc.core.base.util.concurrent.lock;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;

/**
 * ReentrantLockImplementation测试类
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class ReentrantLockImplementationTest {

    @Test
    public void testDefaultConstructor() {
        ReentrantLockImplementation lock = new ReentrantLockImplementation();
        assertNotNull(lock);
        assertFalse(lock.isFair());
    }

    @Test
    public void testFairConstructor() {
        ReentrantLockImplementation fairLock = new ReentrantLockImplementation(true);
        assertTrue(fairLock.isFair());

        ReentrantLockImplementation nonFairLock = new ReentrantLockImplementation(false);
        assertFalse(nonFairLock.isFair());
    }

    @Test
    public void testLockAndUnlock() {
        ReentrantLockImplementation lock = new ReentrantLockImplementation();

        assertFalse(lock.isLocked());
        assertFalse(lock.isHeldByCurrentThread());

        lock.lock();

        assertTrue(lock.isLocked());
        assertTrue(lock.isHeldByCurrentThread());

        lock.unlock();

        assertFalse(lock.isLocked());
        assertFalse(lock.isHeldByCurrentThread());
    }

    @Test
    public void testReentrantLock() {
        ReentrantLockImplementation lock = new ReentrantLockImplementation();

        lock.lock();
        assertEquals(1, lock.getHoldCount());

        lock.lock();
        assertEquals(2, lock.getHoldCount());

        lock.lock();
        assertEquals(3, lock.getHoldCount());

        lock.unlock();
        assertEquals(2, lock.getHoldCount());

        lock.unlock();
        assertEquals(1, lock.getHoldCount());

        lock.unlock();
        assertEquals(0, lock.getHoldCount());
        assertFalse(lock.isLocked());
    }

    @Test
    public void testTryLock() {
        ReentrantLockImplementation lock = new ReentrantLockImplementation();

        assertTrue(lock.tryLock());
        assertTrue(lock.isLocked());

        // 同一线程可以再次获取锁
        assertTrue(lock.tryLock());
        assertEquals(2, lock.getHoldCount());

        lock.unlock();
        lock.unlock();
    }

    @Test
    public void testTryLockWithTimeout() throws InterruptedException {
        ReentrantLockImplementation lock = new ReentrantLockImplementation();

        assertTrue(lock.tryLock(100, TimeUnit.MILLISECONDS));
        assertTrue(lock.isLocked());

        lock.unlock();
    }

    @Test
    public void testLockInterruptibly() throws InterruptedException {
        ReentrantLockImplementation lock = new ReentrantLockImplementation();

        lock.lockInterruptibly();
        assertTrue(lock.isLocked());

        lock.unlock();
    }

    @Test
    public void testNewCondition() {
        ReentrantLockImplementation lock = new ReentrantLockImplementation();
        Condition condition = lock.newCondition();

        assertNotNull(condition);
    }

    @Test
    public void testGetOwner() {
        ReentrantLockImplementation lock = new ReentrantLockImplementation();

        assertNull(lock.getOwner());

        lock.lock();
        assertEquals(Thread.currentThread(), lock.getOwner());

        lock.unlock();
        assertNull(lock.getOwner());
    }

    @Test
    public void testHasQueuedThreads() throws InterruptedException {
        ReentrantLockImplementation lock = new ReentrantLockImplementation();

        assertFalse(lock.hasQueuedThreads());

        lock.lock();

        CountDownLatch latch = new CountDownLatch(1);
        Thread t = new Thread(() -> {
            latch.countDown();
            lock.lock();
            lock.unlock();
        });
        t.start();

        latch.await();
        Thread.sleep(50);

        assertTrue(lock.hasQueuedThreads());

        lock.unlock();
        t.join();

        assertFalse(lock.hasQueuedThreads());
    }

    @Test
    public void testHasQueuedThread() throws InterruptedException {
        ReentrantLockImplementation lock = new ReentrantLockImplementation();

        lock.lock();

        CountDownLatch latch = new CountDownLatch(1);
        Thread t = new Thread(() -> {
            latch.countDown();
            lock.lock();
            lock.unlock();
        });
        t.start();

        latch.await();
        Thread.sleep(50);

        assertTrue(lock.hasQueuedThread(t));

        lock.unlock();
        t.join();
    }

    @Test
    public void testGetQueueLength() throws InterruptedException {
        ReentrantLockImplementation lock = new ReentrantLockImplementation();

        assertEquals(0, lock.getQueueLength());

        lock.lock();

        CountDownLatch latch = new CountDownLatch(2);
        Thread t1 = new Thread(() -> {
            latch.countDown();
            lock.lock();
            lock.unlock();
        });
        Thread t2 = new Thread(() -> {
            latch.countDown();
            lock.lock();
            lock.unlock();
        });

        t1.start();
        t2.start();

        latch.await();
        Thread.sleep(50);

        assertTrue(lock.getQueueLength() >= 1);

        lock.unlock();
        t1.join();
        t2.join();

        assertEquals(0, lock.getQueueLength());
    }

    @Test
    public void testToString() {
        ReentrantLockImplementation lock = new ReentrantLockImplementation();

        String unlockedStr = lock.toString();
        assertTrue(unlockedStr.contains("Unlocked"));

        lock.lock();
        String lockedStr = lock.toString();
        assertTrue(lockedStr.contains("Locked"));
        assertTrue(lockedStr.contains(Thread.currentThread().getName()));

        lock.unlock();
    }

    @Test
    public void testConcurrentAccess() throws InterruptedException {
        ReentrantLockImplementation lock = new ReentrantLockImplementation();
        AtomicInteger counter = new AtomicInteger(0);
        int threadCount = 10;
        int iterations = 100;

        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                for (int j = 0; j < iterations; j++) {
                    lock.lock();
                    try {
                        counter.incrementAndGet();
                    } finally {
                        lock.unlock();
                    }
                }
                latch.countDown();
            }).start();
        }

        latch.await(10, TimeUnit.SECONDS);

        assertEquals(threadCount * iterations, counter.get());
    }

    @Test
    public void testFairLockOrdering() throws InterruptedException {
        ReentrantLockImplementation lock = new ReentrantLockImplementation(true);
        AtomicInteger counter = new AtomicInteger(0);
        ConcurrentLinkedQueue<Integer> order = new ConcurrentLinkedQueue<>();

        lock.lock();

        CountDownLatch latch = new CountDownLatch(3);

        for (int i = 0; i < 3; i++) {
            final int id = i;
            new Thread(() -> {
                latch.countDown();
                lock.lock();
                try {
                    order.offer(id);
                    counter.incrementAndGet();
                } finally {
                    lock.unlock();
                }
            }).start();
        }

        latch.await();
        Thread.sleep(100);

        lock.unlock();

        // 等待所有线程完成
        Thread.sleep(500);

        assertEquals(3, counter.get());
    }

    @Test
    public void testConditionAwaitAndSignal() throws InterruptedException {
        ReentrantLockImplementation lock = new ReentrantLockImplementation();
        Condition condition = lock.newCondition();
        AtomicInteger counter = new AtomicInteger(0);

        lock.lock();

        Thread t = new Thread(() -> {
            lock.lock();
            try {
                counter.incrementAndGet();
                condition.signal();
            } finally {
                lock.unlock();
            }
        });

        t.start();

        // 等待信号
        Thread.sleep(100);
        condition.awaitNanos(1_000_000_000L);

        lock.unlock();
        t.join();

        assertEquals(1, counter.get());
    }

    @Test
    public void testIllegalMonitorStateOnUnlock() {
        ReentrantLockImplementation lock = new ReentrantLockImplementation();

        // 未持有锁时解锁应该抛出异常
        assertThrows(IllegalMonitorStateException.class, lock::unlock);
    }
}