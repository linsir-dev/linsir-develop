package com.linsir.abc.core.base.util.concurrent.lock;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ConditionVariable测试类
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class ConditionVariableTest {

    @Test
    public void testConstructor() {
        Lock lock = new ReentrantLock();
        ConditionVariable condition = new ConditionVariable(lock);

        assertNotNull(condition);
    }

    @Test
    public void testConstructorWithNullLock() {
        assertThrows(NullPointerException.class, () -> {
            new ConditionVariable(null);
        });
    }

    @Test
    public void testAwaitAndSignal() throws InterruptedException {
        Lock lock = new ReentrantLock();
        ConditionVariable condition = new ConditionVariable(lock);
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
    public void testAwaitUninterruptibly() throws InterruptedException {
        Lock lock = new ReentrantLock();
        ConditionVariable condition = new ConditionVariable(lock);
        AtomicInteger counter = new AtomicInteger(0);

        Thread t = new Thread(() -> {
            lock.lock();
            try {
                Thread.sleep(100);
                counter.incrementAndGet();
                condition.signal();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        });

        t.start();

        lock.lock();
        try {
            condition.awaitUninterruptibly();
        } finally {
            lock.unlock();
        }

        t.join();
        assertEquals(1, counter.get());
    }

    @Test
    public void testAwaitWithTimeout() throws InterruptedException {
        Lock lock = new ReentrantLock();
        ConditionVariable condition = new ConditionVariable(lock);

        lock.lock();

        long start = System.nanoTime();
        long remaining = condition.awaitNanos(100_000_000L); // 100ms
        long elapsed = System.nanoTime() - start;

        lock.unlock();

        // 应该超时返回
        assertTrue(elapsed >= 100_000_000L || remaining <= 0);
    }

    @Test
    public void testAwaitUntil() throws InterruptedException {
        Lock lock = new ReentrantLock();
        ConditionVariable condition = new ConditionVariable(lock);

        lock.lock();

        // 设置一个已经过期的时间
        boolean result = condition.awaitUntil(new Date(System.currentTimeMillis() - 1000));

        lock.unlock();

        // 应该立即返回false（超时）
        assertFalse(result);
    }

    @Test
    public void testAwaitForTimeUnit() throws InterruptedException {
        Lock lock = new ReentrantLock();
        ConditionVariable condition = new ConditionVariable(lock);

        lock.lock();

        long start = System.currentTimeMillis();
        boolean result = condition.await(100, TimeUnit.MILLISECONDS);
        long elapsed = System.currentTimeMillis() - start;

        lock.unlock();

        // 应该超时返回false
        assertFalse(result);
        assertTrue(elapsed >= 100);
    }

    @Test
    public void testSignalAll() throws InterruptedException {
        Lock lock = new ReentrantLock();
        ConditionVariable condition = new ConditionVariable(lock);
        AtomicInteger counter = new AtomicInteger(0);
        int threadCount = 3;

        lock.lock();

        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                lock.lock();
                try {
                    latch.countDown();
                    condition.awaitUninterruptibly();
                    counter.incrementAndGet();
                } finally {
                    lock.unlock();
                }
            }).start();
        }

        latch.await();
        Thread.sleep(100);

        condition.signalAll();
        lock.unlock();

        Thread.sleep(200);
        assertEquals(threadCount, counter.get());
    }

    @Test
    public void testProducerConsumer() throws InterruptedException {
        Lock lock = new ReentrantLock();
        ConditionVariable notFull = new ConditionVariable(lock);
        ConditionVariable notEmpty = new ConditionVariable(lock);

        AtomicInteger produced = new AtomicInteger(0);
        AtomicInteger consumed = new AtomicInteger(0);
        final int capacity = 5;
        final int totalItems = 20;

        Thread producer = new Thread(() -> {
            for (int i = 0; i < totalItems; i++) {
                lock.lock();
                try {
                    while (produced.get() - consumed.get() >= capacity) {
                        notFull.await();
                    }
                    produced.incrementAndGet();
                    notEmpty.signal();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } finally {
                    lock.unlock();
                }
            }
        });

        Thread consumer = new Thread(() -> {
            for (int i = 0; i < totalItems; i++) {
                lock.lock();
                try {
                    while (produced.get() <= consumed.get()) {
                        notEmpty.await();
                    }
                    consumed.incrementAndGet();
                    notFull.signal();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } finally {
                    lock.unlock();
                }
            }
        });

        producer.start();
        consumer.start();

        producer.join(5000);
        consumer.join(5000);

        assertEquals(totalItems, produced.get());
        assertEquals(totalItems, consumed.get());
    }

    @Test
    public void testInterruptWhileWaiting() throws InterruptedException {
        Lock lock = new ReentrantLock();
        ConditionVariable condition = new ConditionVariable(lock);
        AtomicBoolean interrupted = new AtomicBoolean(false);

        lock.lock();

        Thread t = new Thread(() -> {
            lock.lock();
            try {
                condition.await();
            } catch (InterruptedException e) {
                interrupted.set(true);
            } finally {
                lock.unlock();
            }
        });

        t.start();
        Thread.sleep(100);

        t.interrupt();
        t.join(1000);

        lock.unlock();

        assertTrue(interrupted.get());
    }

    @Test
    public void testMultipleConditions() throws InterruptedException {
        Lock lock = new ReentrantLock();
        ConditionVariable condition1 = new ConditionVariable(lock);
        ConditionVariable condition2 = new ConditionVariable(lock);

        AtomicInteger counter1 = new AtomicInteger(0);
        AtomicInteger counter2 = new AtomicInteger(0);

        lock.lock();

        Thread t1 = new Thread(() -> {
            lock.lock();
            try {
                condition1.awaitUninterruptibly();
                counter1.incrementAndGet();
            } finally {
                lock.unlock();
            }
        });

        Thread t2 = new Thread(() -> {
            lock.lock();
            try {
                condition2.awaitUninterruptibly();
                counter2.incrementAndGet();
            } finally {
                lock.unlock();
            }
        });

        t1.start();
        t2.start();

        Thread.sleep(100);

        // 只通知condition1
        condition1.signal();
        lock.unlock();

        Thread.sleep(200);

        assertEquals(1, counter1.get());
        assertEquals(0, counter2.get());

        // 清理
        lock.lock();
        condition2.signal();
        lock.unlock();
        t2.join();
    }

    @Test
    public void testSpuriousWakeup() throws InterruptedException {
        Lock lock = new ReentrantLock();
        ConditionVariable condition = new ConditionVariable(lock);
        AtomicInteger counter = new AtomicInteger(0);
        AtomicBoolean ready = new AtomicBoolean(false);

        Thread t = new Thread(() -> {
            lock.lock();
            try {
                // 使用while循环防止虚假唤醒
                while (!ready.get()) {
                    condition.await();
                }
                counter.incrementAndGet();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        });

        t.start();
        Thread.sleep(100);

        lock.lock();
        ready.set(true);
        condition.signal();
        lock.unlock();

        t.join(1000);

        assertEquals(1, counter.get());
    }

    @Test
    public void testSignalWithoutLock() {
        Lock lock = new ReentrantLock();
        ConditionVariable condition = new ConditionVariable(lock);

        // 未持有锁时调用signal应该抛出异常
        assertThrows(IllegalMonitorStateException.class, condition::signal);
    }

    @Test
    public void testSignalAllWithoutLock() {
        Lock lock = new ReentrantLock();
        ConditionVariable condition = new ConditionVariable(lock);

        // 未持有锁时调用signalAll应该抛出异常
        assertThrows(IllegalMonitorStateException.class, condition::signalAll);
    }

    @Test
    public void testAwaitWithoutLock() {
        Lock lock = new ReentrantLock();
        ConditionVariable condition = new ConditionVariable(lock);

        // 未持有锁时调用await应该抛出异常
        assertThrows(IllegalMonitorStateException.class, () -> {
            try {
                condition.await();
            } catch (InterruptedException e) {
                // 忽略中断异常
            }
        });
    }
}
