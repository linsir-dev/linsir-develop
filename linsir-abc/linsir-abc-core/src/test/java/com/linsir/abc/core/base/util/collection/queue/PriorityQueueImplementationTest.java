package com.linsir.abc.core.base.util.collection.queue;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Comparator;
import java.util.NoSuchElementException;

/**
 * PriorityQueueImplementation测试类
 */
public class PriorityQueueImplementationTest {

    /**
     * 测试基本添加和获取
     */
    @Test
    public void testBasicAddAndPeek() {
        PriorityQueueImplementation<Integer> queue = new PriorityQueueImplementation<>();

        assertTrue(queue.add(30));
        assertTrue(queue.add(10));
        assertTrue(queue.add(50));

        assertEquals(3, queue.size());
        assertEquals(Integer.valueOf(10), queue.peek()); // 最小元素
    }

    /**
     * 测试poll顺序
     */
    @Test
    public void testPollOrder() {
        PriorityQueueImplementation<Integer> queue = new PriorityQueueImplementation<>();

        queue.add(30);
        queue.add(10);
        queue.add(50);
        queue.add(20);
        queue.add(40);

        assertEquals(Integer.valueOf(10), queue.poll());
        assertEquals(Integer.valueOf(20), queue.poll());
        assertEquals(Integer.valueOf(30), queue.poll());
        assertEquals(Integer.valueOf(40), queue.poll());
        assertEquals(Integer.valueOf(50), queue.poll());
        assertNull(queue.poll());
    }

    /**
     * 测试offer方法
     */
    @Test
    public void testOffer() {
        PriorityQueueImplementation<Integer> queue = new PriorityQueueImplementation<>();

        assertTrue(queue.offer(30));
        assertTrue(queue.offer(10));

        assertEquals(Integer.valueOf(10), queue.peek());
    }

    /**
     * 测试null元素
     */
    @Test(expected = NullPointerException.class)
    public void testNullElement() {
        PriorityQueueImplementation<Integer> queue = new PriorityQueueImplementation<>();
        queue.add(null);
    }

    /**
     * 测试空队列操作
     */
    @Test
    public void testEmptyQueue() {
        PriorityQueueImplementation<Integer> queue = new PriorityQueueImplementation<>();

        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
        assertNull(queue.peek());
        assertNull(queue.poll());
    }

    @Test(expected = NoSuchElementException.class)
    public void testRemoveEmpty() {
        PriorityQueueImplementation<Integer> queue = new PriorityQueueImplementation<>();
        queue.remove();
    }

    @Test(expected = NoSuchElementException.class)
    public void testElementEmpty() {
        PriorityQueueImplementation<Integer> queue = new PriorityQueueImplementation<>();
        queue.element();
    }

    /**
     * 测试自定义比较器（最大堆）
     */
    @Test
    public void testMaxHeap() {
        PriorityQueueImplementation<Integer> maxHeap = new PriorityQueueImplementation<>(
            (a, b) -> b - a
        );

        maxHeap.add(30);
        maxHeap.add(10);
        maxHeap.add(50);
        maxHeap.add(20);

        assertEquals(Integer.valueOf(50), maxHeap.peek());
        assertEquals(Integer.valueOf(50), maxHeap.poll());
        assertEquals(Integer.valueOf(30), maxHeap.poll());
    }

    /**
     * 测试contains
     */
    @Test
    public void testContains() {
        PriorityQueueImplementation<Integer> queue = new PriorityQueueImplementation<>();

        queue.add(10);
        queue.add(20);
        queue.add(30);

        assertTrue(queue.contains(20));
        assertFalse(queue.contains(40));
    }

    /**
     * 测试clear
     */
    @Test
    public void testClear() {
        PriorityQueueImplementation<Integer> queue = new PriorityQueueImplementation<>();

        queue.add(10);
        queue.add(20);
        queue.clear();

        assertEquals(0, queue.size());
        assertTrue(queue.isEmpty());
        assertNull(queue.peek());
    }

    /**
     * 测试toArray
     */
    @Test
    public void testToArray() {
        PriorityQueueImplementation<Integer> queue = new PriorityQueueImplementation<>();

        queue.add(30);
        queue.add(10);
        queue.add(50);

        Object[] array = queue.toArray();
        assertEquals(3, array.length);
    }

    /**
     * 测试扩容
     */
    @Test
    public void testGrow() {
        PriorityQueueImplementation<Integer> queue = new PriorityQueueImplementation<>(4);

        // 添加超过初始容量的元素
        for (int i = 1; i <= 20; i++) {
            queue.add(i);
        }

        assertEquals(20, queue.size());

        // 验证所有元素按顺序取出
        for (int i = 1; i <= 20; i++) {
            assertEquals(Integer.valueOf(i), queue.poll());
        }
    }

    /**
     * 测试不同构造方法
     */
    @Test
    public void testConstructors() {
        PriorityQueueImplementation<Integer> queue1 = new PriorityQueueImplementation<>();
        assertEquals(0, queue1.size());

        PriorityQueueImplementation<Integer> queue2 = new PriorityQueueImplementation<>(32);
        assertEquals(0, queue2.size());

        PriorityQueueImplementation<Integer> queue3 = new PriorityQueueImplementation<>(
            Comparator.reverseOrder()
        );
        assertEquals(0, queue3.size());

        PriorityQueueImplementation<Integer> queue4 = new PriorityQueueImplementation<>(
            32, Comparator.reverseOrder()
        );
        assertEquals(0, queue4.size());
    }

    /**
     * 测试复杂对象
     */
    @Test
    public void testComplexObjects() {
        class Task implements Comparable<Task> {
            String name;
            int priority;

            Task(String name, int priority) {
                this.name = name;
                this.priority = priority;
            }

            @Override
            public int compareTo(Task other) {
                return this.priority - other.priority;
            }

            @Override
            public boolean equals(Object obj) {
                if (obj instanceof Task) {
                    Task other = (Task) obj;
                    return this.priority == other.priority;
                }
                return false;
            }
        }

        PriorityQueueImplementation<Task> queue = new PriorityQueueImplementation<>();

        queue.add(new Task("Low", 5));
        queue.add(new Task("High", 1));
        queue.add(new Task("Medium", 3));

        assertEquals(1, queue.poll().priority);
        assertEquals(3, queue.poll().priority);
        assertEquals(5, queue.poll().priority);
    }
}
