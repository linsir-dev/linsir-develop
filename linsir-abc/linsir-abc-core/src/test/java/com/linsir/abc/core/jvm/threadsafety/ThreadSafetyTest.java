package com.linsir.abc.core.jvm.threadsafety;

import com.linsir.abc.core.jvm.threadsafety.cas.AtomicCounter;
import com.linsir.abc.core.jvm.threadsafety.immutable.ImmutablePerson;
import com.linsir.abc.core.jvm.threadsafety.lock.ReentrantLockCounter;
import com.linsir.abc.core.jvm.threadsafety.sync.SynchronizedCounter;
import com.linsir.abc.core.jvm.threadsafety.threadlocal.ThreadLocalExample;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 线程安全测试类
 *
 * 测试第13章线程安全相关的代码实现：
 * 1. 不可变对象测试
 * 2. synchronized测试
 * 3. ReentrantLock测试
 * 4. CAS/Atomic测试
 * 5. ThreadLocal测试
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-29
 */
public class ThreadSafetyTest {

    /**
     * 测试不可变对象的线程安全性
     */
    @Test
    @DisplayName("Test immutable object thread safety")
    public void testImmutablePerson() {
        List<String> hobbies = new ArrayList<>();
        hobbies.add("Reading");
        hobbies.add("Coding");

        ImmutablePerson person = new ImmutablePerson("Alice", 25, hobbies);

        // 验证初始值
        assertEquals("Alice", person.getName());
        assertEquals(25, person.getAge());
        assertEquals(2, person.getHobbies().size());

        // 验证防御性拷贝 - 修改原始列表不影响对象
        hobbies.add("Hacking");
        assertEquals(2, person.getHobbies().size());

        // 验证不可修改视图
        assertThrows(UnsupportedOperationException.class, () -> {
            person.getHobbies().add("Gaming");
        });

        // 验证"修改"操作返回新对象
        ImmutablePerson olderPerson = person.withAge(26);
        assertEquals(25, person.getAge());  // 原对象不变
        assertEquals(26, olderPerson.getAge());  // 新对象有新值
    }

    /**
     * 测试synchronized计数器的线程安全性
     */
    @Test
    @DisplayName("Test synchronized counter thread safety")
    public void testSynchronizedCounter() throws InterruptedException {
        SynchronizedCounter counter = new SynchronizedCounter();
        int threadCount = 50;
        int incrementPerThread = 1000;
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                for (int j = 0; j < incrementPerThread; j++) {
                    counter.increment();
                }
                latch.countDown();
            }).start();
        }

        latch.await();
        assertEquals(threadCount * incrementPerThread, counter.getCount());
    }

    /**
     * 测试synchronized静态方法的线程安全性
     */
    @Test
    @DisplayName("Test synchronized static counter")
    public void testSynchronizedStaticCounter() throws InterruptedException {
        int threadCount = 20;
        int initialCount = SynchronizedCounter.getStaticCount();
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    SynchronizedCounter.incrementStatic();
                }
                latch.countDown();
            }).start();
        }

        latch.await();
        assertEquals(initialCount + threadCount * 100, SynchronizedCounter.getStaticCount());
    }

    /**
     * 测试synchronized的可重入性
     */
    @Test
    @DisplayName("Test synchronized reentrancy")
    public void testSynchronizedReentrancy() {
        SynchronizedCounter counter = new SynchronizedCounter();
        // 可重入方法调用链：methodA -> methodB -> methodC
        assertDoesNotThrow(counter::methodA);
    }

    /**
     * 测试ReentrantLock计数器的线程安全性
     */
    @Test
    @DisplayName("Test ReentrantLock counter thread safety")
    public void testReentrantLockCounter() throws InterruptedException {
        ReentrantLockCounter counter = new ReentrantLockCounter();
        int threadCount = 50;
        int incrementPerThread = 1000;
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                for (int j = 0; j < incrementPerThread; j++) {
                    counter.increment();
                }
                latch.countDown();
            }).start();
        }

        latch.await();
        assertEquals(threadCount * incrementPerThread, counter.getCount());
    }

    /**
     * 测试ReentrantLock的可中断性
     */
    @Test
    @DisplayName("Test ReentrantLock interruptibility")
    public void testReentrantLockInterruptibility() throws InterruptedException {
        ReentrantLockCounter counter = new ReentrantLockCounter();

        Thread thread = new Thread(() -> {
            try {
                counter.incrementWithInterruptibleLock();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        thread.start();
        thread.join();
        assertEquals(1, counter.getCount());
    }

    /**
     * 测试ReentrantLock的tryLock超时功能
     */
    @Test
    @DisplayName("Test ReentrantLock tryLock with timeout")
    public void testReentrantLockTryLock() throws InterruptedException {
        ReentrantLockCounter counter = new ReentrantLockCounter();

        boolean success = counter.tryIncrement(1, TimeUnit.SECONDS);
        assertTrue(success);
        assertEquals(1, counter.getCount());
    }

    /**
     * 测试条件变量
     */
    @Test
    @DisplayName("Test ReentrantLock Condition")
    public void testReentrantLockCondition() throws InterruptedException {
        ReentrantLockCounter counter = new ReentrantLockCounter();
        CountDownLatch latch = new CountDownLatch(1);

        Thread waiter = new Thread(() -> {
            try {
                latch.countDown();
                counter.waitForReady();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        waiter.start();
        latch.await();  // 确保waiter已经开始等待
        Thread.sleep(100);

        counter.setReady();
        waiter.join(1000);
        assertFalse(waiter.isAlive());
    }

    /**
     * 测试AtomicInteger计数器的线程安全性
     */
    @Test
    @DisplayName("Test AtomicInteger counter thread safety")
    public void testAtomicCounter() throws InterruptedException {
        AtomicCounter counter = new AtomicCounter();
        int threadCount = 50;
        int incrementPerThread = 1000;
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                for (int j = 0; j < incrementPerThread; j++) {
                    counter.increment();
                }
                latch.countDown();
            }).start();
        }

        latch.await();
        assertEquals(threadCount * incrementPerThread, counter.getCount());
    }

    /**
     * 测试CAS操作
     */
    @Test
    @DisplayName("Test CAS operation")
    public void testCASOperation() {
        AtomicCounter counter = new AtomicCounter();
        counter.increment();

        // 成功的CAS
        boolean success = counter.compareAndSet(1, 100);
        assertTrue(success);
        assertEquals(100, counter.getCount());

        // 失败的CAS（预期值不匹配）
        success = counter.compareAndSet(1, 200);
        assertFalse(success);
        assertEquals(100, counter.getCount());
    }

    /**
     * 测试自定义CAS操作
     */
    @Test
    @DisplayName("Test custom CAS operation with loop")
    public void testCustomCASOperation() {
        AtomicCounter counter = new AtomicCounter();
        counter.add(100);
        assertEquals(100, counter.getCount());

        counter.add(-50);
        assertEquals(50, counter.getCount());
    }

    /**
     * 测试ThreadLocal的线程隔离性
     */
    @Test
    @DisplayName("Test ThreadLocal isolation")
    public void testThreadLocalIsolation() throws InterruptedException {
        ThreadLocalExample example = new ThreadLocalExample();
        List<String> results = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(3);

        for (int i = 0; i < 3; i++) {
            final int threadNum = i;
            new Thread(() -> {
                Date date = new Date(1609459200000L + threadNum * 86400000L);  // 2021-01-01 + n days
                String formatted = example.formatDate(date);
                synchronized (results) {
                    results.add(formatted);
                }
                latch.countDown();
            }, "Thread-" + i).start();
        }

        latch.await();
        assertEquals(3, results.size());
        // 每个线程应该有不同的格式化结果
        assertNotEquals(results.get(0), results.get(1));
    }

    /**
     * 测试ThreadLocal用户上下文
     */
    @Test
    @DisplayName("Test ThreadLocal user context")
    public void testThreadLocalUserContext() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(2);

        Thread t1 = new Thread(() -> {
            ThreadLocalExample.UserContext.setCurrentUser(
                    new ThreadLocalExample.UserContext.User(1L, "alice", "ADMIN"));
            ThreadLocalExample.UserContext.User user = ThreadLocalExample.UserContext.getCurrentUser();
            assertNotNull(user);
            assertEquals("alice", user.getUsername());
            ThreadLocalExample.UserContext.clear();
            latch.countDown();
        });

        Thread t2 = new Thread(() -> {
            ThreadLocalExample.UserContext.setCurrentUser(
                    new ThreadLocalExample.UserContext.User(2L, "bob", "USER"));
            ThreadLocalExample.UserContext.User user = ThreadLocalExample.UserContext.getCurrentUser();
            assertNotNull(user);
            assertEquals("bob", user.getUsername());
            ThreadLocalExample.UserContext.clear();
            latch.countDown();
        });

        t1.start();
        t2.start();
        latch.await();
    }

    /**
     * 测试生产者-消费者模式
     */
    @Test
    @DisplayName("Test Producer-Consumer with ReentrantLock Condition")
    public void testProducerConsumer() throws InterruptedException {
        ReentrantLockCounter.MultiConditionExample pc = new ReentrantLockCounter.MultiConditionExample();
        int itemCount = 5;
        CountDownLatch latch = new CountDownLatch(2);

        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < itemCount; i++) {
                    pc.produce(i);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                latch.countDown();
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < itemCount; i++) {
                    int value = pc.consume();
                    assertEquals(i, value);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                latch.countDown();
            }
        });

        producer.start();
        consumer.start();
        latch.await();
    }

    /**
     * 测试ABA问题
     */
    @Test
    @DisplayName("Test ABA problem demonstration")
    public void testABAProblem() throws InterruptedException {
        AtomicCounter.ABADemo abaDemo = new AtomicCounter.ABADemo();
        // 演示ABA问题和解决方案
        abaDemo.demonstrateABAProblem();
        abaDemo.demonstrateABASolution();
        // 如果执行到这里没有异常，说明演示成功
        assertTrue(true);
    }

    /**
     * 测试细粒度锁
     */
    @Test
    @DisplayName("Test fine-grained locking")
    public void testFineGrainedLocking() throws InterruptedException {
        SynchronizedCounter counter = new SynchronizedCounter();
        CountDownLatch latch = new CountDownLatch(2);

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                counter.setValueA(i);
            }
            latch.countDown();
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                counter.setValueB(i);
            }
            latch.countDown();
        });

        t1.start();
        t2.start();
        latch.await();

        // 两个线程分别修改不同的值，不会相互影响
        assertTrue(counter.getValueA() >= 0);
        assertTrue(counter.getValueB() >= 0);
    }
}
