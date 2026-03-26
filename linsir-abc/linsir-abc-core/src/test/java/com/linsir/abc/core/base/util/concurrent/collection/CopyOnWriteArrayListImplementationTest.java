package com.linsir.abc.core.base.util.concurrent.collection;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * CopyOnWriteArrayListImplementation测试类
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class CopyOnWriteArrayListImplementationTest {

    @Test
    public void testBasicOperations() {
        CopyOnWriteArrayListImplementation<String> list = new CopyOnWriteArrayListImplementation<>();

        // 测试add
        assertTrue(list.add("item1"));
        assertTrue(list.add("item2"));
        assertEquals(2, list.size());

        // 测试get
        assertEquals("item1", list.get(0));
        assertEquals("item2", list.get(1));

        // 测试set
        list.set(0, "updated");
        assertEquals("updated", list.get(0));

        // 测试remove
        assertEquals("item2", list.remove(1));
        assertEquals(1, list.size());
    }

    @Test
    public void testConstructorWithCollection() {
        List<String> source = Arrays.asList("a", "b", "c");
        CopyOnWriteArrayListImplementation<String> list = new CopyOnWriteArrayListImplementation<>(source);

        assertEquals(3, list.size());
        assertEquals("a", list.get(0));
        assertEquals("b", list.get(1));
        assertEquals("c", list.get(2));
    }

    @Test
    public void testIterator() {
        CopyOnWriteArrayListImplementation<String> list = new CopyOnWriteArrayListImplementation<>();
        list.add("a");
        list.add("b");
        list.add("c");

        Iterator<String> iterator = list.iterator();
        assertTrue(iterator.hasNext());
        assertEquals("a", iterator.next());
        assertEquals("b", iterator.next());
        assertEquals("c", iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testIteratorSnapshot() {
        CopyOnWriteArrayListImplementation<String> list = new CopyOnWriteArrayListImplementation<>();
        list.add("a");
        list.add("b");

        Iterator<String> iterator = list.iterator();

        // 修改原列表
        list.add("c");
        list.remove("a");

        // 迭代器应该看到快照
        assertEquals("a", iterator.next());
        assertEquals("b", iterator.next());
        assertFalse(iterator.hasNext());

        // 原列表已修改
        assertEquals(2, list.size());
        assertEquals("b", list.get(0));
        assertEquals("c", list.get(1));
    }

    @Test
    public void testConcurrentRead() throws InterruptedException {
        CopyOnWriteArrayListImplementation<Integer> list = new CopyOnWriteArrayListImplementation<>();
        for (int i = 0; i < 100; i++) {
            list.add(i);
        }

        int threadCount = 10;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < 1000; j++) {
                        for (Integer value : list) {
                            // 只读操作
                            assertNotNull(value);
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // 列表应该保持不变
        assertEquals(100, list.size());
    }

    @Test
    public void testConcurrentWrite() throws InterruptedException {
        CopyOnWriteArrayListImplementation<Integer> list = new CopyOnWriteArrayListImplementation<>();

        int threadCount = 5;
        int iterations = 100;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < iterations; j++) {
                        list.add(threadId * iterations + j);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        assertEquals(threadCount * iterations, list.size());
    }

    @Test
    public void testIndexOf() {
        CopyOnWriteArrayListImplementation<String> list = new CopyOnWriteArrayListImplementation<>();
        list.add("a");
        list.add("b");
        list.add("c");
        list.add("b");

        assertEquals(0, list.indexOf("a"));
        assertEquals(1, list.indexOf("b"));
        assertEquals(2, list.indexOf("c"));
        assertEquals(-1, list.indexOf("d"));

        assertEquals(3, list.lastIndexOf("b"));
        assertEquals(-1, list.lastIndexOf("d"));
    }

    @Test
    public void testContains() {
        CopyOnWriteArrayListImplementation<String> list = new CopyOnWriteArrayListImplementation<>();
        list.add("a");
        list.add("b");

        assertTrue(list.contains("a"));
        assertTrue(list.contains("b"));
        assertFalse(list.contains("c"));
    }

    @Test
    public void testAddAll() {
        CopyOnWriteArrayListImplementation<String> list = new CopyOnWriteArrayListImplementation<>();
        list.add("a");

        List<String> toAdd = Arrays.asList("b", "c", "d");
        assertTrue(list.addAll(toAdd));

        assertEquals(4, list.size());
        assertEquals("d", list.get(3));
    }

    @Test
    public void testClear() {
        CopyOnWriteArrayListImplementation<String> list = new CopyOnWriteArrayListImplementation<>();
        list.add("a");
        list.add("b");

        list.clear();

        assertEquals(0, list.size());
        assertTrue(list.isEmpty());
    }

    @Test
    public void testToArray() {
        CopyOnWriteArrayListImplementation<String> list = new CopyOnWriteArrayListImplementation<>();
        list.add("a");
        list.add("b");
        list.add("c");

        Object[] array = list.toArray();
        assertEquals(3, array.length);
        assertEquals("a", array[0]);

        String[] typedArray = list.toArray(new String[0]);
        assertEquals(3, typedArray.length);
        assertEquals("a", typedArray[0]);
    }

    @Test
    public void testRemoveIf() {
        CopyOnWriteArrayListImplementation<Integer> list = new CopyOnWriteArrayListImplementation<>();
        for (int i = 1; i <= 10; i++) {
            list.add(i);
        }

        list.removeIf(n -> n % 2 == 0);

        assertEquals(5, list.size());
        assertTrue(list.contains(1));
        assertFalse(list.contains(2));
        assertTrue(list.contains(9));
    }

    @Test
    public void testForEach() {
        CopyOnWriteArrayListImplementation<Integer> list = new CopyOnWriteArrayListImplementation<>();
        for (int i = 1; i <= 5; i++) {
            list.add(i);
        }

        List<Integer> result = new ArrayList<>();
        list.forEach(result::add);

        assertEquals(Arrays.asList(1, 2, 3, 4, 5), result);
    }
}
