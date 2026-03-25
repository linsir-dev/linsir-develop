package com.linsir.abc.core.base.util.concurrent;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ConcurrentHashMapImplementation测试类
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class ConcurrentHashMapImplementationTest {

    @Test
    public void testBasicOperations() {
        ConcurrentHashMapImplementation<String, Integer> map = new ConcurrentHashMapImplementation<>();

        // 测试put和get
        map.put("key1", 100);
        assertEquals(100, map.get("key1"));

        // 测试size
        assertEquals(1, map.size());

        // 测试containsKey
        assertTrue(map.containsKey("key1"));
        assertFalse(map.containsKey("key2"));

        // 测试remove
        assertEquals(100, map.remove("key1"));
        assertNull(map.get("key1"));
        assertEquals(0, map.size());
    }

    @Test
    public void testPutIfAbsent() {
        ConcurrentHashMapImplementation<String, String> map = new ConcurrentHashMapImplementation<>();

        assertNull(map.putIfAbsent("key", "value1"));
        assertEquals("value1", map.putIfAbsent("key", "value2"));
        assertEquals("value1", map.get("key"));
    }

    @Test
    public void testRemoveWithValue() {
        ConcurrentHashMapImplementation<String, String> map = new ConcurrentHashMapImplementation<>();
        map.put("key", "value");

        assertFalse(map.remove("key", "wrong"));
        assertTrue(map.remove("key", "value"));
        assertNull(map.get("key"));
    }

    @Test
    public void testReplace() {
        ConcurrentHashMapImplementation<String, String> map = new ConcurrentHashMapImplementation<>();
        map.put("key", "old");

        assertFalse(map.replace("key", "wrong", "new"));
        assertTrue(map.replace("key", "old", "new"));
        assertEquals("new", map.get("key"));

        assertEquals("new", map.replace("key", "updated"));
        assertEquals("updated", map.get("key"));
    }

    @Test
    public void testCompute() {
        ConcurrentHashMapImplementation<String, Integer> map = new ConcurrentHashMapImplementation<>();
        map.put("key", 10);

        Integer result = map.compute("key", (k, v) -> v == null ? 1 : v * 2);
        assertEquals(20, result);
        assertEquals(20, map.get("key"));
    }

    @Test
    public void testConcurrentPut() throws InterruptedException {
        ConcurrentHashMapImplementation<Integer, Integer> map = new ConcurrentHashMapImplementation<>();
        int threadCount = 10;
        int iterations = 1000;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < iterations; j++) {
                        int key = threadId * iterations + j;
                        map.put(key, key);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        assertEquals(threadCount * iterations, map.size());

        // 验证所有数据
        for (int i = 0; i < threadCount * iterations; i++) {
            assertEquals(i, map.get(i));
        }
    }

    @Test
    public void testConcurrentReadWrite() throws InterruptedException {
        ConcurrentHashMapImplementation<String, AtomicInteger> map = new ConcurrentHashMapImplementation<>();
        map.put("counter", new AtomicInteger(0));

        int threadCount = 20;
        int iterations = 100;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        // 10个写线程
        for (int i = 0; i < threadCount / 2; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < iterations; j++) {
                        map.compute("counter", (k, v) -> {
                            v.incrementAndGet();
                            return v;
                        });
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        // 10个读线程
        for (int i = 0; i < threadCount / 2; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < iterations * 10; j++) {
                        map.get("counter");
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        assertEquals(threadCount / 2 * iterations, map.get("counter").get());
    }

    @Test
    public void testResize() {
        ConcurrentHashMapImplementation<Integer, String> map = new ConcurrentHashMapImplementation<>(4);

        // 添加足够多的元素触发扩容
        for (int i = 0; i < 100; i++) {
            map.put(i, "value" + i);
        }

        assertEquals(100, map.size());

        // 验证所有数据仍然可访问
        for (int i = 0; i < 100; i++) {
            assertEquals("value" + i, map.get(i));
        }
    }

    @Test
    public void testClear() {
        ConcurrentHashMapImplementation<String, Integer> map = new ConcurrentHashMapImplementation<>();
        map.put("key1", 1);
        map.put("key2", 2);

        map.clear();

        assertEquals(0, map.size());
        assertNull(map.get("key1"));
        assertNull(map.get("key2"));
    }

    @Test
    public void testNullKey() {
        ConcurrentHashMapImplementation<String, Integer> map = new ConcurrentHashMapImplementation<>();

        assertThrows(NullPointerException.class, () -> map.put(null, 1));
        assertThrows(NullPointerException.class, () -> map.get(null));
    }

    @Test
    public void testNullValue() {
        ConcurrentHashMapImplementation<String, Integer> map = new ConcurrentHashMapImplementation<>();

        assertThrows(NullPointerException.class, () -> map.put("key", null));
    }
}
