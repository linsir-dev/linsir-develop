package com.linsir.abc.core.base.util.concurrent.lock;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

/**
 * ReadWriteLockImplementation测试类
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class ReadWriteLockImplementationTest {

    @Test
    public void testBasicReadLock() {
        ReadWriteLockImplementation rwLock = new ReadWriteLockImplementation();
        Lock readLock = rwLock.readLock();

        readLock.lock();
        try {
            assertTrue(rwLock.getReadLockCount() > 0);
        } finally {
            readLock.unlock();
        }

        assertEquals(0, rwLock.getReadLockCount());
    }

    @Test
    public void testBasicWriteLock() {
        ReadWriteLockImplementation rwLock = new ReadWriteLockImplementation();
        Lock writeLock = rwLock.writeLock();

        assertFalse(rwLock.isWriteLocked());

        writeLock.lock();
        try {
            assertTrue(rwLock.isWriteLocked());
            assertTrue(rwLock.isWriteLockedByCurrentThread());
        } finally {
            writeLock.unlock();
        }

        assertFalse(rwLock.isWriteLocked());
    }

    @Test
    public void testMultipleReadLocks() {
        ReadWriteLockImplementation rwLock = new ReadWriteLockImplementation();
        Lock readLock = rwLock.readLock();

        // 同一线程可以多次获取读锁（重入）
        readLock.lock();
        readLock.lock();
        try {
            assertEquals(2, rwLock.getReadHoldCount());
        } finally {
            readLock.unlock();
            readLock.unlock();
        }

        assertEquals(0, rwLock.getReadHoldCount());
    }

    @Test
    public void testWriteLockReentrancy() {
        ReadWriteLockImplementation rwLock = new ReadWriteLockImplementation();
        Lock writeLock = rwLock.writeLock();

        writeLock.lock();
        writeLock.lock();
        try {
            assertEquals(2, rwLock.getWriteHoldCount());
        } finally {
            writeLock.unlock();
            writeLock.unlock();
        }

        assertEquals(0, rwLock.getWriteHoldCount());
    }

    @Test
    public void testConcurrentRead() throws InterruptedException {
        ReadWriteLockImplementation rwLock = new ReadWriteLockImplementation();
        Lock readLock = rwLock.readLock();
        AtomicInteger counter = new AtomicInteger(0);

        int threadCount = 10;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                readLock.lock();
                try {
                    counter.incrementAndGet();
                    // 模拟读取操作
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    readLock.unlock();
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        assertEquals(threadCount, counter.get());
    }

    @Test
    public void testConcurrentWrite() throws InterruptedException {
        ReadWriteLockImplementation rwLock = new ReadWriteLockImplementation();
        Lock writeLock = rwLock.writeLock();
        AtomicInteger counter = new AtomicInteger(0);

        int threadCount = 5;
        int iterations = 100;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < iterations; j++) {
                        writeLock.lock();
                        try {
                            counter.incrementAndGet();
                        } finally {
                            writeLock.unlock();
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
    public void testReadWriteBlocking() throws InterruptedException {
        ReadWriteLockImplementation rwLock = new ReadWriteLockImplementation();
        Lock readLock = rwLock.readLock();
        Lock writeLock = rwLock.writeLock();

        // 获取读锁
        readLock.lock();
        try {
            // 尝试获取写锁（应该失败，因为有读锁）
            assertFalse(writeLock.tryLock());
        } finally {
            readLock.unlock();
        }

        // 获取写锁
        writeLock.lock();
        try {
            // 尝试获取读锁（应该失败，因为写锁被其他线程持有）
            // 但当前线程持有写锁，所以可以获取读锁（锁降级）
            assertTrue(readLock.tryLock());
            readLock.unlock();
        } finally {
            writeLock.unlock();
        }
    }

    @Test
    public void testLockDowngrade() {
        ReadWriteLockImplementation rwLock = new ReadWriteLockImplementation();
        Lock readLock = rwLock.readLock();
        Lock writeLock = rwLock.writeLock();

        AtomicInteger data = new AtomicInteger(0);

        // 获取写锁
        writeLock.lock();
        try {
            // 修改数据
            data.set(42);

            // 锁降级：在释放写锁之前获取读锁
            readLock.lock();
        } finally {
            writeLock.unlock();
        }

        // 现在持有读锁，可以继续读取
        try {
            assertEquals(42, data.get());
        } finally {
            readLock.unlock();
        }
    }

    @Test
    public void testTryLock() {
        ReadWriteLockImplementation rwLock = new ReadWriteLockImplementation();
        Lock readLock = rwLock.readLock();
        Lock writeLock = rwLock.writeLock();

        assertTrue(readLock.tryLock());
        readLock.unlock();

        assertTrue(writeLock.tryLock());
        writeLock.unlock();
    }

    @Test
    public void testTryLockWithTimeout() throws InterruptedException {
        ReadWriteLockImplementation rwLock = new ReadWriteLockImplementation();
        Lock readLock = rwLock.readLock();
        Lock writeLock = rwLock.writeLock();

        assertTrue(readLock.tryLock(1, TimeUnit.SECONDS));
        readLock.unlock();

        assertTrue(writeLock.tryLock(1, TimeUnit.SECONDS));
        writeLock.unlock();
    }

    @Test
    public void testFairLock() {
        ReadWriteLockImplementation fairLock = new ReadWriteLockImplementation(true);
        assertTrue(fairLock.isFair());
    }

    @Test
    public void testToString() {
        ReadWriteLockImplementation rwLock = new ReadWriteLockImplementation();

        String str = rwLock.toString();
        assertTrue(str.contains("Write locks = 0"));
        assertTrue(str.contains("Read locks = 0"));

        Lock writeLock = rwLock.writeLock();
        writeLock.lock();
        try {
            str = rwLock.toString();
            assertTrue(str.contains("Write locks = 1"));
        } finally {
            writeLock.unlock();
        }
    }
}
