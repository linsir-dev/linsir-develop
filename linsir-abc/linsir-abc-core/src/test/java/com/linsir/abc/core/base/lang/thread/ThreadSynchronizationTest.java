package com.linsir.abc.core.base.lang.thread;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * ThreadSynchronization测试类
 */
public class ThreadSynchronizationTest {

    /**
     * 测试synchronized方法
     */
    @Test
    public void testSynchronizedMethods() throws InterruptedException {
        ThreadSynchronization sync = new ThreadSynchronization();

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                sync.increment();
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                sync.increment();
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        assertEquals(200, sync.getCounter());
    }

    /**
     * 测试递增和递减
     */
    @Test
    public void testIncrementAndDecrement() {
        ThreadSynchronization sync = new ThreadSynchronization();

        sync.increment();
        sync.increment();
        assertEquals(2, sync.getCounter());

        sync.decrement();
        assertEquals(1, sync.getCounter());
    }

    /**
     * 测试生产者消费者模式
     */
    @Test
    public void testProducerConsumer() throws InterruptedException {
        ThreadSynchronization sync = new ThreadSynchronization();
        final int[] consumed = new int[1];

        Thread producer = new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                try {
                    sync.produce(i);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        Thread consumer = new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                try {
                    consumed[0] += sync.consume();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        producer.start();
        consumer.start();
        producer.join();
        consumer.join();

        assertEquals(55, consumed[0]);
    }

    /**
     * 测试读写锁
     */
    @Test
    public void testReadWriteLock() throws InterruptedException {
        ThreadSynchronization sync = new ThreadSynchronization();
        final StringBuilder data = new StringBuilder("Initial");

        Thread reader = new Thread(() -> {
            try {
                sync.lockRead();
                String value = data.toString();
                sync.unlockRead();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread writer = new Thread(() -> {
            try {
                sync.lockWrite();
                data.append("-Modified");
                sync.unlockWrite();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        reader.start();
        writer.start();
        reader.join();
        writer.join();

        assertEquals("Initial-Modified", data.toString());
    }
}
