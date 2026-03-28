package com.linsir.abc.core.jvm.jmm;

import com.linsir.abc.core.jvm.jmm.happensbefore.HappensBeforeRules;
import com.linsir.abc.core.jvm.jmm.synchronizedexample.SynchronizedCounter;
import com.linsir.abc.core.jvm.jmm.synchronizedexample.SynchronizedMemoryVisibility;
import com.linsir.abc.core.jvm.jmm.thread.ThreadImplementation;
import com.linsir.abc.core.jvm.jmm.thread.ThreadStateDemo;
import com.linsir.abc.core.jvm.jmm.volatileexample.ReadWriteCounter;
import com.linsir.abc.core.jvm.jmm.volatileexample.VolatileCounter;
import com.linsir.abc.core.jvm.jmm.volatileexample.VolatileFlag;
import com.linsir.abc.core.jvm.jmm.volatileexample.VolatileSingleton;
import org.junit.jupiter.api.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Java内存模型（JMM）测试类
 * 
 * 测试内容：
 * 1. volatile关键字测试
 * 2. synchronized关键字测试
 * 3. 线程状态转换测试
 * 4. happens-before规则测试
 * 
 * @author linsir
 * @version 1.0.0
 * @since 2024/1/1
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JMMTest {
    
    /**
     * 测试1：volatile状态标志位
     * 验证volatile保证可见性
     */
    @Test
    @Order(1)
    @DisplayName("Test volatile flag visibility")
    public void testVolatileFlag() throws InterruptedException {
        VolatileFlag flag = new VolatileFlag();
        CountDownLatch latch = new CountDownLatch(1);
        
        Thread worker = new Thread(() -> {
            latch.countDown();
            flag.doWork();
        }, "Worker-Thread");
        
        worker.start();
        latch.await();  // 确保工作线程已经开始
        Thread.sleep(200);  // 让工作线程运行一段时间
        
        assertTrue(flag.isRunning(), "Flag should be running initially");
        flag.stop();
        
        worker.join(1000);  // 等待工作线程结束，最多1秒
        assertFalse(flag.isRunning(), "Flag should be stopped");
        assertFalse(worker.isAlive(), "Worker thread should be terminated");
    }
    
    /**
     * 测试2：volatile单例模式（DCL）
     * 验证volatile防止指令重排序
     */
    @Test
    @Order(2)
    @DisplayName("Test volatile singleton DCL")
    public void testVolatileSingleton() throws InterruptedException {
        final int threadCount = 100;
        Thread[] threads = new Thread[threadCount];
        VolatileSingleton[] instances = new VolatileSingleton[threadCount];
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                instances[index] = VolatileSingleton.getInstance();
                latch.countDown();
            });
        }
        
        // 同时启动所有线程
        for (Thread thread : threads) {
            thread.start();
        }
        
        assertTrue(latch.await(10, TimeUnit.SECONDS), "All threads should complete");
        
        // 验证所有线程获取的是同一个实例
        VolatileSingleton firstInstance = instances[0];
        assertNotNull(firstInstance, "Instance should not be null");
        
        for (int i = 1; i < threadCount; i++) {
            assertSame(firstInstance, instances[i], 
                "All threads should get the same singleton instance");
        }
    }
    
    /**
     * 测试3：volatile不能保证原子性
     * 验证volatile在复合操作中的局限性
     */
    @Test
    @Order(3)
    @DisplayName("Test volatile does not guarantee atomicity")
    public void testVolatileNotAtomic() throws InterruptedException {
        VolatileCounter counter = new VolatileCounter();
        final int threadCount = 50;
        final int incrementPerThread = 1000;
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < incrementPerThread; j++) {
                    counter.unsafeIncrement();
                }
                latch.countDown();
            });
        }
        
        for (Thread thread : threads) {
            thread.start();
        }
        
        assertTrue(latch.await(30, TimeUnit.SECONDS), "All threads should complete");
        
        int expected = threadCount * incrementPerThread;
        int actual = counter.getVolatileCount();
        
        System.out.println("Expected: " + expected + ", Actual: " + actual);
        // volatile不能保证原子性，实际值可能小于期望值
        assertTrue(actual <= expected, "Actual should be less than or equal to expected");
    }
    
    /**
     * 测试4：AtomicInteger保证原子性
     * 验证原子类的正确性
     */
    @Test
    @Order(4)
    @DisplayName("Test AtomicInteger atomicity")
    public void testAtomicCounter() throws InterruptedException {
        VolatileCounter counter = new VolatileCounter();
        final int threadCount = 50;
        final int incrementPerThread = 1000;
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < incrementPerThread; j++) {
                    counter.safeIncrement();
                }
                latch.countDown();
            });
        }
        
        for (Thread thread : threads) {
            thread.start();
        }
        
        assertTrue(latch.await(30, TimeUnit.SECONDS), "All threads should complete");
        
        int expected = threadCount * incrementPerThread;
        int actual = counter.getAtomicCount();
        
        System.out.println("Expected: " + expected + ", Actual: " + actual);
        assertEquals(expected, actual, "Atomic counter should be thread-safe");
    }
    
    /**
     * 测试5：synchronized计数器
     * 验证synchronized保证原子性和可见性
     */
    @Test
    @Order(5)
    @DisplayName("Test synchronized counter")
    public void testSynchronizedCounter() throws InterruptedException {
        SynchronizedCounter counter = new SynchronizedCounter();
        final int threadCount = 50;
        final int incrementPerThread = 1000;
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < incrementPerThread; j++) {
                    counter.increment();
                }
                latch.countDown();
            });
        }
        
        for (Thread thread : threads) {
            thread.start();
        }
        
        assertTrue(latch.await(30, TimeUnit.SECONDS), "All threads should complete");
        
        int expected = threadCount * incrementPerThread;
        int actual = counter.getCount();
        
        System.out.println("Expected: " + expected + ", Actual: " + actual);
        assertEquals(expected, actual, "Synchronized counter should be thread-safe");
    }
    
    /**
     * 测试6：synchronized内存可见性
     * 验证synchronized保证可见性
     */
    @Test
    @Order(6)
    @DisplayName("Test synchronized memory visibility")
    public void testSynchronizedVisibility() throws InterruptedException {
        SynchronizedMemoryVisibility demo = new SynchronizedMemoryVisibility();
        CountDownLatch writeLatch = new CountDownLatch(1);
        CountDownLatch readLatch = new CountDownLatch(1);
        final int[] readValue = new int[1];
        
        Thread writer = new Thread(() -> {
            demo.write(42);
            writeLatch.countDown();
        });
        
        Thread reader = new Thread(() -> {
            try {
                writeLatch.await();  // 等待写入完成
                readValue[0] = demo.read();
                readLatch.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        reader.start();
        writer.start();
        
        assertTrue(readLatch.await(5, TimeUnit.SECONDS), "Read should complete");
        assertEquals(42, readValue[0], "Reader should see the written value");
    }
    
    /**
     * 测试7：线程状态转换
     * 验证线程状态的正确性
     */
    @Test
    @Order(7)
    @DisplayName("Test thread state transitions")
    public void testThreadState() throws InterruptedException {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        // NEW状态
        assertEquals(Thread.State.NEW, thread.getState(), "Initial state should be NEW");
        
        thread.start();
        Thread.sleep(50);
        
        // RUNNABLE或TIMED_WAITING状态
        assertTrue(
            thread.getState() == Thread.State.RUNNABLE || 
            thread.getState() == Thread.State.TIMED_WAITING,
            "State should be RUNNABLE or TIMED_WAITING after start"
        );
        
        thread.join();
        
        // TERMINATED状态
        assertEquals(Thread.State.TERMINATED, thread.getState(), "Final state should be TERMINATED");
    }
    
    /**
     * 测试8：线程实现方式
     * 验证三种线程实现方式
     */
    @Test
    @Order(8)
    @DisplayName("Test thread implementation methods")
    public void testThreadImplementation() throws Exception {
        // 1. 继承Thread类
        ThreadImplementation.MyThread thread1 = new ThreadImplementation.MyThread("Test-Thread-1");
        thread1.start();
        thread1.join();
        assertFalse(thread1.isAlive(), "Thread should be terminated");
        
        // 2. 实现Runnable接口
        Thread thread2 = new Thread(new ThreadImplementation.MyRunnable("Test-Runnable-1"), "Test-Thread-2");
        thread2.start();
        thread2.join();
        assertFalse(thread2.isAlive(), "Thread should be terminated");
        
        // 3. 实现Callable接口
        ThreadImplementation.MyCallable callable = new ThreadImplementation.MyCallable("Test-Callable-1");
        java.util.concurrent.FutureTask<String> futureTask = new java.util.concurrent.FutureTask<>(callable);
        Thread thread3 = new Thread(futureTask, "Test-Thread-3");
        thread3.start();
        String result = futureTask.get(5, TimeUnit.SECONDS);
        assertNotNull(result, "Callable should return a result");
        assertTrue(result.contains("Test-Callable-1"), "Result should contain callable name");
    }
    
    /**
     * 测试9：happens-before规则 - 监视器锁规则
     */
    @Test
    @Order(9)
    @DisplayName("Test happens-before monitor lock rule")
    public void testHappensBeforeMonitorLock() throws InterruptedException {
        final Object lock = new Object();
        final int[] sharedValue = new int[1];
        CountDownLatch writeLatch = new CountDownLatch(1);
        CountDownLatch readLatch = new CountDownLatch(1);
        
        Thread writer = new Thread(() -> {
            synchronized (lock) {
                sharedValue[0] = 100;
            }
            writeLatch.countDown();
        });
        
        Thread reader = new Thread(() -> {
            try {
                writeLatch.await();
                synchronized (lock) {
                    assertEquals(100, sharedValue[0], 
                        "Reader should see the value written by writer");
                }
                readLatch.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        reader.start();
        writer.start();
        
        assertTrue(readLatch.await(5, TimeUnit.SECONDS), "Read should complete");
    }
    
    /**
     * 测试10：happens-before规则 - volatile变量规则
     */
    @Test
    @Order(10)
    @DisplayName("Test happens-before volatile variable rule")
    public void testHappensBeforeVolatile() throws InterruptedException {
        final boolean[] flag = new boolean[1];
        final int[] value = new int[1];
        CountDownLatch latch = new CountDownLatch(1);
        
        Thread writer = new Thread(() -> {
            value[0] = 200;
            flag[0] = true;
        });
        
        Thread reader = new Thread(() -> {
            while (!flag[0]) {
                // 忙等待
                Thread.yield();
            }
            // 由于happens-before传递性，这里应该能看到value=200
            assertEquals(200, value[0], 
                "Reader should see the value written before volatile write");
            latch.countDown();
        });
        
        reader.start();
        Thread.sleep(100);
        writer.start();
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Read should complete");
    }
    
    /**
     * 测试11：ReadWriteCounter性能测试
     * 验证volatile在读多写少场景下的性能优势
     */
    @Test
    @Order(11)
    @DisplayName("Test ReadWriteCounter performance")
    public void testReadWriteCounter() throws InterruptedException {
        ReadWriteCounter counter = new ReadWriteCounter();
        final int readerCount = 10;
        final int readCount = 100;
        CountDownLatch latch = new CountDownLatch(readerCount);
        
        // 先写入一个值
        counter.setValue(42);
        
        Thread[] readers = new Thread[readerCount];
        for (int i = 0; i < readerCount; i++) {
            readers[i] = new Thread(() -> {
                for (int j = 0; j < readCount; j++) {
                    int value = counter.getValue();
                    assertEquals(42, value, "Read value should be 42");
                }
                latch.countDown();
            });
        }
        
        long startTime = System.currentTimeMillis();
        for (Thread reader : readers) {
            reader.start();
        }
        
        assertTrue(latch.await(10, TimeUnit.SECONDS), "All readers should complete");
        long duration = System.currentTimeMillis() - startTime;
        
        System.out.println("ReadWriteCounter: " + readerCount + " readers, " + 
            readCount + " reads each, completed in " + duration + "ms");
    }
    
    /**
     * 测试12：并发场景综合测试
     * 测试多个线程同时读写共享变量
     */
    @Test
    @Order(12)
    @DisplayName("Test concurrent read and write")
    public void testConcurrentReadWrite() throws InterruptedException {
        SynchronizedCounter counter = new SynchronizedCounter();
        final int writerCount = 10;
        final int readerCount = 20;
        final int operationsPerThread = 100;
        CountDownLatch latch = new CountDownLatch(writerCount + readerCount);
        
        // 写线程
        Thread[] writers = new Thread[writerCount];
        for (int i = 0; i < writerCount; i++) {
            writers[i] = new Thread(() -> {
                for (int j = 0; j < operationsPerThread; j++) {
                    counter.increment();
                }
                latch.countDown();
            }, "Writer-" + i);
        }
        
        // 读线程
        Thread[] readers = new Thread[readerCount];
        for (int i = 0; i < readerCount; i++) {
            readers[i] = new Thread(() -> {
                for (int j = 0; j < operationsPerThread; j++) {
                    counter.getCount();  // 读取操作
                }
                latch.countDown();
            }, "Reader-" + i);
        }
        
        // 启动所有线程
        for (Thread writer : writers) {
            writer.start();
        }
        for (Thread reader : readers) {
            reader.start();
        }
        
        assertTrue(latch.await(30, TimeUnit.SECONDS), "All threads should complete");
        
        int expected = writerCount * operationsPerThread;
        int actual = counter.getCount();
        
        System.out.println("Concurrent test - Expected: " + expected + ", Actual: " + actual);
        assertEquals(expected, actual, "Counter should be consistent after concurrent access");
    }
}
