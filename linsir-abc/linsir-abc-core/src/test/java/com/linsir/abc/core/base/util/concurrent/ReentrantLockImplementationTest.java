package com.linsir.abc.core.base.util.concurrent;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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
    public void testBasicLock() {
        ReentrantLockImplementation lock = new ReentrantLockImplementation();
        AtomicInteger counter = new AtomicInteger(0);

        lock.lock();
        try {
            counter.incrementAndGet();
        } finally {
            lock.unlock();
        }

        assertEquals(1, counter.get());
    }

    @Test
    public void testReentrancy() {
        ReentrantLockImplementation lock = new ReentrantLockImplementation();

        lock.lock();
        try {
            assertEquals(1, lock.getHoldCount());
            lock.lock();
            try {
                assertEquals(2, lock.getHoldCount());
            } finally {
                lock.unlock();
            }
            assertEquals(1, lock.getHoldCount());
        } finally {
            lock.unlock();
        }

        assertEquals(0, lock.getHoldCount());
    }

    @Test
    public void testTryLock() {
        ReentrantLockImplementation lock = new ReentrantLockImplementation();

        assertTrue(lock.tryLock());
        try {
            assertTrue(lock.isHeldByCurrentThread());
            assertTrue(lock.isLocked());
        } finally {
            lock.unlock();
        }

        assertFalse(lock.isHeldByCurrentThread());
    }

    @Test
    public void testTryLockWithTimeout() throws InterruptedException {
        ReentrantLockImplementation lock = new ReentrantLockImplementation();

        assertTrue(lock.tryLock(1, TimeUnit.SECONDS));
        try {
            assertTrue(lock.isHeldByCurrentThread());
        } finally {
            lock.unlock();
        }
    }

    @Test
    public void testLockInterruptibly() throws InterruptedException {
        ReentrantLockImplementation lock = new ReentrantLockImplementation();

        lock.lockInterruptibly();
        try {
            assertTrue(lock.isHeldByCurrentThread());
        } finally {
            lock.unlock();
        }
    }

    @Test
    public void testConcurrentLock() throws InterruptedException {
        ReentrantLockImplementation lock = new ReentrantLockImplementation();
        AtomicInteger counter = new AtomicInteger(0);
        int threadCount = 10;
        int iterations = 100;

        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < iterations; j++) {
                        lock.lock();
                        try {
                            counter.incrementAndGet();
                        } finally {
                            lock.unlock();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        assertEquals(threadCount * iterations, counter.get());
    }

    @Test
    public void testFairLock() throws InterruptedException {
        ReentrantLockImplementation fairLock = new ReentrantLockImplementation(true);

        assertTrue(fairLock.isFair());

        AtomicInteger order = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(3);

        ExecutorService executor = Executors.newFixedThreadPool(3);

        for (int i = 0; i < 3; i++) {
            final int threadId = i;
            executor.submit(() -> {
                fairLock.lock();
                try {
                    int currentOrder = order.getAndIncrement();
                    // 公平锁应该按顺序获取
                    assertTrue(currentOrder >= threadId);
                } finally {
                    fairLock.unlock();
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();
    }

    @Test
    public void testCondition() throws InterruptedException {
        ReentrantLockImplementation lock = new ReentrantLockImplementation();
        Condition condition = lock.newCondition();
        AtomicInteger value = new AtomicInteger(0);

        Thread producer = new Thread(() -> {
            lock.lock();
            try {
                value.set(42);
                condition.signal();
            } finally {
                lock.unlock();
            }
        });

        Thread consumer = new Thread(() -> {
            lock.lock();
            try {
                while (value.get() == 0) {
                    condition.await();
                }
                assertEquals(42, value.get());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        });

        consumer.start();
        Thread.sleep(50);
        producer.start();

        consumer.join(1000);
        producer.join(1000);

        assertEquals(42, value.get());
    }

    @Test
    public void testMultipleConditions() throws InterruptedException {
        ReentrantLockImplementation lock = new ReentrantLockImplementation();
        Condition condition1 = lock.newCondition();
        Condition condition2 = lock.newCondition();

        AtomicInteger flag1 = new AtomicInteger(0);
        AtomicInteger flag2 = new AtomicInteger(0);

        Thread thread1 = new Thread(() -> {
            lock.lock();
            try {
                flag1.set(1);
                condition1.signal();
            } finally {
                lock.unlock();
            }
        });

        Thread thread2 = new Thread(() -> {
            lock.lock();
            try {
                flag2.set(2);
                condition2.signal();
            } finally {
                lock.unlock();
            }
        });

        Thread waiter1 = new Thread(() -> {
            lock.lock();
            try {
                while (flag1.get() == 0) {
                    condition1.await();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        });

        Thread waiter2 = new Thread(() -> {
            lock.lock();
            try {
                while (flag2.get() == 0) {
                    condition2.await();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        });

        waiter1.start();
        waiter2.start();
        Thread.sleep(50);
        thread1.start();
        thread2.start();

        waiter1.join(1000);
        waiter2.join(1000);
        thread1.join(1000);
        thread2.join(1000);

        assertEquals(1, flag1.get());
        assertEquals(2, flag2.get());
    }

    @Test
    public void testToString() {
        ReentrantLockImplementation lock = new ReentrantLockImplementation();

        assertTrue(lock.toString().contains("Unlocked"));

        lock.lock();
        try {
            assertTrue(lock.toString().contains("Locked"));
        } finally {
            lock.unlock();
        }
    }
}
