package com.linsir.abc.core.base.util.collection.set;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.NoSuchElementException;

/**
 * TreeSetImplementation测试类
 */
public class TreeSetImplementationTest {

    /**
     * 测试基本添加和大小
     */
    @Test
    public void testBasicAddAndSize() {
        TreeSetImplementation<Integer> set = new TreeSetImplementation<>();

        assertTrue(set.add(10));
        assertEquals(1, set.size());

        assertTrue(set.add(20));
        assertEquals(2, set.size());
    }

    /**
     * 测试重复元素
     */
    @Test
    public void testDuplicateElements() {
        TreeSetImplementation<Integer> set = new TreeSetImplementation<>();

        assertTrue(set.add(10));
        assertFalse(set.add(10)); // 重复元素返回false
        assertEquals(1, set.size());
    }

    /**
     * 测试自动排序
     */
    @Test
    public void testAutomaticSorting() {
        TreeSetImplementation<Integer> set = new TreeSetImplementation<>();

        set.add(30);
        set.add(10);
        set.add(50);
        set.add(20);

        assertEquals(Integer.valueOf(10), set.first());
        assertEquals(Integer.valueOf(50), set.last());

        // 验证遍历顺序
        Integer prev = null;
        for (Integer i : set) {
            if (prev != null) {
                assertTrue(i > prev);
            }
            prev = i;
        }
    }

    /**
     * 测试自定义比较器
     */
    @Test
    public void testCustomComparator() {
        // 按字符串长度排序
        TreeSetImplementation<String> set = new TreeSetImplementation<>(
            (s1, s2) -> {
                int lenDiff = s1.length() - s2.length();
                return lenDiff != 0 ? lenDiff : s1.compareTo(s2);
            }
        );

        set.add("Apple");
        set.add("Banana");
        set.add("Cat");

        assertEquals("Cat", set.first());
        assertEquals("Banana", set.last());
    }

    /**
     * 测试contains
     */
    @Test
    public void testContains() {
        TreeSetImplementation<Integer> set = new TreeSetImplementation<>();

        set.add(10);
        assertTrue(set.contains(10));
        assertFalse(set.contains(20));
    }

    /**
     * 测试remove
     */
    @Test
    public void testRemove() {
        TreeSetImplementation<Integer> set = new TreeSetImplementation<>();

        set.add(10);
        assertTrue(set.remove(10));
        assertEquals(0, set.size());
        assertFalse(set.contains(10));

        assertFalse(set.remove(20));
    }

    /**
     * 测试first和last
     */
    @Test
    public void testFirstAndLast() {
        TreeSetImplementation<Integer> set = new TreeSetImplementation<>();

        set.add(30);
        set.add(10);
        set.add(50);

        assertEquals(Integer.valueOf(10), set.first());
        assertEquals(Integer.valueOf(50), set.last());
    }

    @Test(expected = NoSuchElementException.class)
    public void testFirstEmpty() {
        TreeSetImplementation<Integer> set = new TreeSetImplementation<>();
        set.first();
    }

    @Test(expected = NoSuchElementException.class)
    public void testLastEmpty() {
        TreeSetImplementation<Integer> set = new TreeSetImplementation<>();
        set.last();
    }

    /**
     * 测试范围查询方法
     */
    @Test
    public void testRangeQueries() {
        TreeSetImplementation<Integer> set = new TreeSetImplementation<>();

        set.add(10);
        set.add(20);
        set.add(30);
        set.add(40);
        set.add(50);

        assertEquals(Integer.valueOf(20), set.lower(25));
        assertEquals(Integer.valueOf(20), set.floor(20));
        assertEquals(Integer.valueOf(20), set.floor(25));
        assertEquals(Integer.valueOf(30), set.ceiling(25));
        assertEquals(Integer.valueOf(30), set.ceiling(30));
        assertEquals(Integer.valueOf(40), set.higher(30));
    }

    /**
     * 测试pollFirst和pollLast
     */
    @Test
    public void testPoll() {
        TreeSetImplementation<Integer> set = new TreeSetImplementation<>();

        set.add(10);
        set.add(20);
        set.add(30);

        assertEquals(Integer.valueOf(10), set.pollFirst());
        assertEquals(2, set.size());

        assertEquals(Integer.valueOf(30), set.pollLast());
        assertEquals(1, set.size());
    }

    @Test
    public void testPollEmpty() {
        TreeSetImplementation<Integer> set = new TreeSetImplementation<>();
        assertNull(set.pollFirst());
        assertNull(set.pollLast());
    }

    /**
     * 测试clear
     */
    @Test
    public void testClear() {
        TreeSetImplementation<Integer> set = new TreeSetImplementation<>();

        set.add(10);
        set.add(20);
        set.clear();

        assertEquals(0, set.size());
        assertTrue(set.isEmpty());
    }

    /**
     * 测试addAll
     */
    @Test
    public void testAddAll() {
        TreeSetImplementation<Integer> set = new TreeSetImplementation<>();

        set.addAll(Arrays.asList(30, 10, 50, 20));
        assertEquals(4, set.size());
        assertEquals(Integer.valueOf(10), set.first());
        assertEquals(Integer.valueOf(50), set.last());
    }

    /**
     * 测试不同构造方法
     */
    @Test
    public void testConstructors() {
        TreeSetImplementation<Integer> set1 = new TreeSetImplementation<>();
        assertEquals(0, set1.size());

        TreeSetImplementation<Integer> set2 = new TreeSetImplementation<>(
            Comparator.reverseOrder()
        );
        assertEquals(0, set2.size());

        TreeSetImplementation<Integer> set3 = new TreeSetImplementation<>(
            Arrays.asList(10, 20, 30)
        );
        assertEquals(3, set3.size());
    }
}
