package com.linsir.abc.core.base.util.collection.list;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * LinkedListImplementation测试类
 */
public class LinkedListImplementationTest {

    /**
     * 测试添加元素
     */
    @Test
    public void testAdd() {
        LinkedListImplementation<String> list = new LinkedListImplementation<>();

        assertTrue(list.add("A"));
        assertTrue(list.add("B"));
        assertTrue(list.add("C"));

        assertEquals(3, list.size());
        assertEquals("A", list.get(0));
        assertEquals("B", list.get(1));
        assertEquals("C", list.get(2));
    }

    /**
     * 测试在头部添加元素
     */
    @Test
    public void testAddFirst() {
        LinkedListImplementation<String> list = new LinkedListImplementation<>();

        list.addFirst("B");
        list.addFirst("A");

        assertEquals(2, list.size());
        assertEquals("A", list.getFirst());
        assertEquals("B", list.getLast());
    }

    /**
     * 测试在尾部添加元素
     */
    @Test
    public void testAddLast() {
        LinkedListImplementation<String> list = new LinkedListImplementation<>();

        list.addLast("A");
        list.addLast("B");

        assertEquals(2, list.size());
        assertEquals("A", list.getFirst());
        assertEquals("B", list.getLast());
    }

    /**
     * 测试在指定位置插入元素
     */
    @Test
    public void testAddAtIndex() {
        LinkedListImplementation<String> list = new LinkedListImplementation<>();

        list.add("A");
        list.add("C");
        list.add(1, "B");

        assertEquals(3, list.size());
        assertEquals("A", list.get(0));
        assertEquals("B", list.get(1));
        assertEquals("C", list.get(2));
    }

    /**
     * 测试获取元素
     */
    @Test
    public void testGet() {
        LinkedListImplementation<String> list = new LinkedListImplementation<>();
        list.add("A");
        list.add("B");
        list.add("C");

        assertEquals("A", list.get(0));
        assertEquals("B", list.get(1));
        assertEquals("C", list.get(2));
    }

    /**
     * 测试获取第一个元素
     */
    @Test
    public void testGetFirst() {
        LinkedListImplementation<String> list = new LinkedListImplementation<>();
        list.add("A");
        list.add("B");

        assertEquals("A", list.getFirst());
    }

    /**
     * 测试获取最后一个元素
     */
    @Test
    public void testGetLast() {
        LinkedListImplementation<String> list = new LinkedListImplementation<>();
        list.add("A");
        list.add("B");

        assertEquals("B", list.getLast());
    }

    /**
     * 测试移除第一个元素
     */
    @Test
    public void testRemoveFirst() {
        LinkedListImplementation<String> list = new LinkedListImplementation<>();
        list.add("A");
        list.add("B");

        String removed = list.removeFirst();
        assertEquals("A", removed);
        assertEquals(1, list.size());
        assertEquals("B", list.getFirst());
    }

    /**
     * 测试移除最后一个元素
     */
    @Test
    public void testRemoveLast() {
        LinkedListImplementation<String> list = new LinkedListImplementation<>();
        list.add("A");
        list.add("B");

        String removed = list.removeLast();
        assertEquals("B", removed);
        assertEquals(1, list.size());
        assertEquals("A", list.getLast());
    }

    /**
     * 测试移除指定位置的元素
     */
    @Test
    public void testRemoveAtIndex() {
        LinkedListImplementation<String> list = new LinkedListImplementation<>();
        list.add("A");
        list.add("B");
        list.add("C");

        String removed = list.remove(1);
        assertEquals("B", removed);
        assertEquals(2, list.size());
        assertEquals("A", list.get(0));
        assertEquals("C", list.get(1));
    }

    /**
     * 测试移除指定元素
     */
    @Test
    public void testRemoveObject() {
        LinkedListImplementation<String> list = new LinkedListImplementation<>();
        list.add("A");
        list.add("B");
        list.add("C");

        assertTrue(list.remove("B"));
        assertEquals(2, list.size());
        assertFalse(list.remove("D"));
    }

    /**
     * 测试队列操作
     */
    @Test
    public void testQueueOperations() {
        LinkedListImplementation<String> list = new LinkedListImplementation<>();

        // offer操作
        assertTrue(list.offerFirst("A"));
        assertTrue(list.offerLast("B"));

        // peek操作
        assertEquals("A", list.peekFirst());
        assertEquals("B", list.peekLast());

        // poll操作
        assertEquals("A", list.pollFirst());
        assertEquals("B", list.pollLast());
        assertEquals(0, list.size());
    }

    /**
     * 测试迭代器
     */
    @Test
    public void testIterator() {
        LinkedListImplementation<String> list = new LinkedListImplementation<>();
        list.add("A");
        list.add("B");
        list.add("C");

        Iterator<String> iterator = list.iterator();
        assertTrue(iterator.hasNext());
        assertEquals("A", iterator.next());
        assertEquals("B", iterator.next());
        assertEquals("C", iterator.next());
        assertFalse(iterator.hasNext());
    }

    /**
     * 测试清空列表
     */
    @Test
    public void testClear() {
        LinkedListImplementation<String> list = new LinkedListImplementation<>();
        list.add("A");
        list.add("B");

        list.clear();
        assertEquals(0, list.size());
        assertTrue(list.isEmpty());
    }

    /**
     * 测试空列表异常
     */
    @Test(expected = NoSuchElementException.class)
    public void testGetFirstEmpty() {
        LinkedListImplementation<String> list = new LinkedListImplementation<>();
        list.getFirst();
    }

    /**
     * 测试索引越界异常
     */
    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetOutOfBounds() {
        LinkedListImplementation<String> list = new LinkedListImplementation<>();
        list.add("A");
        list.get(1);
    }
}
