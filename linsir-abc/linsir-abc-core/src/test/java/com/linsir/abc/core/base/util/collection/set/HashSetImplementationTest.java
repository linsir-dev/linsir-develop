package com.linsir.abc.core.base.util.collection.set;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Arrays;

/**
 * HashSetImplementation测试类
 */
public class HashSetImplementationTest {

    /**
     * 测试基本添加和大小
     */
    @Test
    public void testBasicAddAndSize() {
        HashSetImplementation<String> set = new HashSetImplementation<>();

        assertTrue(set.add("element1"));
        assertEquals(1, set.size());

        assertTrue(set.add("element2"));
        assertEquals(2, set.size());
    }

    /**
     * 测试重复元素
     */
    @Test
    public void testDuplicateElements() {
        HashSetImplementation<String> set = new HashSetImplementation<>();

        assertTrue(set.add("element"));
        assertFalse(set.add("element")); // 重复元素返回false
        assertEquals(1, set.size());
    }

    /**
     * 测试null元素
     */
    @Test
    public void testNullElement() {
        HashSetImplementation<String> set = new HashSetImplementation<>();

        assertTrue(set.add(null));
        assertEquals(1, set.size());
        assertTrue(set.contains(null));
    }

    /**
     * 测试contains
     */
    @Test
    public void testContains() {
        HashSetImplementation<String> set = new HashSetImplementation<>();

        set.add("element1");
        assertTrue(set.contains("element1"));
        assertFalse(set.contains("element2"));
    }

    /**
     * 测试remove
     */
    @Test
    public void testRemove() {
        HashSetImplementation<String> set = new HashSetImplementation<>();

        set.add("element1");
        assertTrue(set.remove("element1"));
        assertEquals(0, set.size());
        assertFalse(set.contains("element1"));

        assertFalse(set.remove("nonexistent"));
    }

    /**
     * 测试clear
     */
    @Test
    public void testClear() {
        HashSetImplementation<String> set = new HashSetImplementation<>();

        set.add("element1");
        set.add("element2");
        set.clear();

        assertEquals(0, set.size());
        assertTrue(set.isEmpty());
    }

    /**
     * 测试isEmpty
     */
    @Test
    public void testIsEmpty() {
        HashSetImplementation<String> set = new HashSetImplementation<>();

        assertTrue(set.isEmpty());
        set.add("element");
        assertFalse(set.isEmpty());
    }

    /**
     * 测试addAll
     */
    @Test
    public void testAddAll() {
        HashSetImplementation<String> set = new HashSetImplementation<>();

        set.addAll(Arrays.asList("a", "b", "c"));
        assertEquals(3, set.size());
        assertTrue(set.contains("a"));
        assertTrue(set.contains("b"));
        assertTrue(set.contains("c"));
    }

    /**
     * 测试containsAll
     */
    @Test
    public void testContainsAll() {
        HashSetImplementation<String> set = new HashSetImplementation<>();

        set.addAll(Arrays.asList("a", "b", "c"));
        assertTrue(set.containsAll(Arrays.asList("a", "b")));
        assertFalse(set.containsAll(Arrays.asList("a", "d")));
    }

    /**
     * 测试removeAll
     */
    @Test
    public void testRemoveAll() {
        HashSetImplementation<String> set = new HashSetImplementation<>();

        set.addAll(Arrays.asList("a", "b", "c", "d"));
        assertTrue(set.removeAll(Arrays.asList("a", "c")));
        assertEquals(2, set.size());
        assertFalse(set.contains("a"));
        assertFalse(set.contains("c"));
    }

    /**
     * 测试retainAll
     */
    @Test
    public void testRetainAll() {
        HashSetImplementation<String> set = new HashSetImplementation<>();

        set.addAll(Arrays.asList("a", "b", "c", "d"));
        assertTrue(set.retainAll(Arrays.asList("a", "c")));
        assertEquals(2, set.size());
        assertTrue(set.contains("a"));
        assertTrue(set.contains("c"));
    }

    /**
     * 测试toArray
     */
    @Test
    public void testToArray() {
        HashSetImplementation<String> set = new HashSetImplementation<>();

        set.addAll(Arrays.asList("a", "b", "c"));
        Object[] array = set.toArray();
        assertEquals(3, array.length);
    }

    /**
     * 测试不同构造方法
     */
    @Test
    public void testConstructors() {
        HashSetImplementation<String> set1 = new HashSetImplementation<>();
        assertEquals(0, set1.size());

        HashSetImplementation<String> set2 = new HashSetImplementation<>(32);
        assertEquals(0, set2.size());

        HashSetImplementation<String> set3 = new HashSetImplementation<>(32, 0.5f);
        assertEquals(0, set3.size());

        HashSetImplementation<String> set4 = new HashSetImplementation<>(Arrays.asList("a", "b"));
        assertEquals(2, set4.size());
    }

    /**
     * 测试迭代器
     */
    @Test
    public void testIterator() {
        HashSetImplementation<String> set = new HashSetImplementation<>();

        set.addAll(Arrays.asList("a", "b", "c"));

        int count = 0;
        for (String s : set) {
            assertNotNull(s);
            count++;
        }
        assertEquals(3, count);
    }
}
