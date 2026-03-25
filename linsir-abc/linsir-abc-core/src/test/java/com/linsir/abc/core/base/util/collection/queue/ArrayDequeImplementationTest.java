package com.linsir.abc.core.base.util.collection.queue;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * ArrayDequeImplementation测试类
 */
public class ArrayDequeImplementationTest {

    /**
     * 测试基本添加和获取
     */
    @Test
    public void testBasicAddAndPeek() {
        ArrayDequeImplementation<Integer> deque = new ArrayDequeImplementation<>();

        assertTrue(deque.add(10));
        assertTrue(deque.add(20));
        assertTrue(deque.add(30));

        assertEquals(3, deque.size());
        assertEquals(Integer.valueOf(10), deque.peek());
    }

    /**
     * 测试addFirst和addLast
     */
    @Test
    public void testAddFirstAndLast() {
        ArrayDequeImplementation<String> deque = new ArrayDequeImplementation<>();

        deque.addFirst("middle");
        deque.addFirst("first");
        deque.addLast("last");

        assertEquals(3, deque.size());
        assertEquals("first", deque.peekFirst());
        assertEquals("last", deque.peekLast());
    }

    /**
     * 测试pollFirst和pollLast
     */
    @Test
    public void testPollFirstAndLast() {
        ArrayDequeImplementation<String> deque = new ArrayDequeImplementation<>();

        deque.add("first");
        deque.add("second");
        deque.add("third");

        assertEquals("first", deque.pollFirst());
        assertEquals("third", deque.pollLast());
        assertEquals(1, deque.size());
    }

    /**
     * 测试作为队列使用（FIFO）
     */
    @Test
    public void testAsQueue() {
        ArrayDequeImplementation<String> queue = new ArrayDequeImplementation<>();

        queue.offer("Alice");
        queue.offer("Bob");
        queue.offer("Charlie");

        assertEquals("Alice", queue.poll());
        assertEquals("Bob", queue.poll());
        assertEquals("Charlie", queue.poll());
        assertNull(queue.poll());
    }

    /**
     * 测试作为栈使用（LIFO）
     */
    @Test
    public void testAsStack() {
        ArrayDequeImplementation<String> stack = new ArrayDequeImplementation<>();

        stack.push("first");
        stack.push("second");
        stack.push("third");

        assertEquals("third", stack.pop());
        assertEquals("second", stack.pop());
        assertEquals("first", stack.pop());
    }

    @Test(expected = NoSuchElementException.class)
    public void testPopEmpty() {
        ArrayDequeImplementation<String> stack = new ArrayDequeImplementation<>();
        stack.pop();
    }

    /**
     * 测试null元素
     */
    @Test(expected = NullPointerException.class)
    public void testNullElement() {
        ArrayDequeImplementation<String> deque = new ArrayDequeImplementation<>();
        deque.add(null);
    }

    @Test(expected = NullPointerException.class)
    public void testNullElementFirst() {
        ArrayDequeImplementation<String> deque = new ArrayDequeImplementation<>();
        deque.addFirst(null);
    }

    /**
     * 测试空队列操作
     */
    @Test
    public void testEmptyDeque() {
        ArrayDequeImplementation<String> deque = new ArrayDequeImplementation<>();

        assertTrue(deque.isEmpty());
        assertEquals(0, deque.size());
        assertNull(deque.peekFirst());
        assertNull(deque.peekLast());
        assertNull(deque.pollFirst());
        assertNull(deque.pollLast());
    }

    @Test(expected = NoSuchElementException.class)
    public void testRemoveFirstEmpty() {
        ArrayDequeImplementation<String> deque = new ArrayDequeImplementation<>();
        deque.removeFirst();
    }

    @Test(expected = NoSuchElementException.class)
    public void testRemoveLastEmpty() {
        ArrayDequeImplementation<String> deque = new ArrayDequeImplementation<>();
        deque.removeLast();
    }

    @Test(expected = NoSuchElementException.class)
    public void testElementEmpty() {
        ArrayDequeImplementation<String> deque = new ArrayDequeImplementation<>();
        deque.element();
    }

    /**
     * 测试contains
     */
    @Test
    public void testContains() {
        ArrayDequeImplementation<String> deque = new ArrayDequeImplementation<>();

        deque.add("Alice");
        deque.add("Bob");
        deque.add("Charlie");

        assertTrue(deque.contains("Bob"));
        assertFalse(deque.contains("David"));
    }

    /**
     * 测试clear
     */
    @Test
    public void testClear() {
        ArrayDequeImplementation<String> deque = new ArrayDequeImplementation<>();

        deque.add("Alice");
        deque.add("Bob");
        deque.clear();

        assertEquals(0, deque.size());
        assertTrue(deque.isEmpty());
    }

    /**
     * 测试扩容
     */
    @Test
    public void testGrow() {
        ArrayDequeImplementation<Integer> deque = new ArrayDequeImplementation<>(4);

        // 添加超过初始容量的元素
        for (int i = 0; i < 20; i++) {
            deque.add(i);
        }

        assertEquals(20, deque.size());

        // 验证所有元素
        for (int i = 0; i < 20; i++) {
            assertTrue(deque.contains(i));
        }
    }

    /**
     * 测试迭代器
     */
    @Test
    public void testIterator() {
        ArrayDequeImplementation<String> deque = new ArrayDequeImplementation<>();

        deque.add("Alice");
        deque.add("Bob");
        deque.add("Charlie");

        int count = 0;
        for (String s : deque) {
            assertNotNull(s);
            count++;
        }
        assertEquals(3, count);
    }

    /**
     * 测试toArray
     */
    @Test
    public void testToArray() {
        ArrayDequeImplementation<String> deque = new ArrayDequeImplementation<>();

        deque.add("Alice");
        deque.add("Bob");
        deque.add("Charlie");

        Object[] array = deque.toArray();
        assertEquals(3, array.length);
    }

    /**
     * 测试addAll
     */
    @Test
    public void testAddAll() {
        ArrayDequeImplementation<String> deque = new ArrayDequeImplementation<>();

        deque.addAll(Arrays.asList("Alice", "Bob", "Charlie"));

        assertEquals(3, deque.size());
        assertTrue(deque.contains("Alice"));
        assertTrue(deque.contains("Bob"));
        assertTrue(deque.contains("Charlie"));
    }

    /**
     * 测试不同构造方法
     */
    @Test
    public void testConstructors() {
        ArrayDequeImplementation<String> deque1 = new ArrayDequeImplementation<>();
        assertEquals(0, deque1.size());

        ArrayDequeImplementation<String> deque2 = new ArrayDequeImplementation<>(32);
        assertEquals(0, deque2.size());

        ArrayDequeImplementation<String> deque3 = new ArrayDequeImplementation<>(
            Arrays.asList("Alice", "Bob")
        );
        assertEquals(2, deque3.size());
    }

    /**
     * 测试交替添加和删除
     */
    @Test
    public void testAlternatingAddRemove() {
        ArrayDequeImplementation<Integer> deque = new ArrayDequeImplementation<>();

        // 交替在两端添加
        for (int i = 1; i <= 5; i++) {
            deque.addFirst(i);
            deque.addLast(i + 10);
        }

        assertEquals(10, deque.size());

        // 验证顺序
        assertEquals(Integer.valueOf(5), deque.pollFirst());
        assertEquals(Integer.valueOf(15), deque.pollLast());
    }
}
