package com.linsir.abc.core.base.util.collection.map;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Comparator;
import java.util.NoSuchElementException;

/**
 * TreeMapImplementation测试类
 */
public class TreeMapImplementationTest {

    /**
     * 测试基本添加和获取
     */
    @Test
    public void testBasicPutAndGet() {
        TreeMapImplementation<String, Integer> map = new TreeMapImplementation<>();

        assertNull(map.put("key1", 100));
        assertEquals(Integer.valueOf(100), map.get("key1"));
        assertEquals(1, map.size());
    }

    /**
     * 测试更新已存在的键
     */
    @Test
    public void testUpdateExistingKey() {
        TreeMapImplementation<String, Integer> map = new TreeMapImplementation<>();

        map.put("key1", 100);
        assertEquals(Integer.valueOf(100), map.put("key1", 200));
        assertEquals(Integer.valueOf(200), map.get("key1"));
        assertEquals(1, map.size());
    }

    /**
     * 测试自动排序
     */
    @Test
    public void testAutomaticSorting() {
        TreeMapImplementation<Integer, String> map = new TreeMapImplementation<>();

        map.put(30, "thirty");
        map.put(10, "ten");
        map.put(50, "fifty");
        map.put(20, "twenty");

        assertEquals(Integer.valueOf(10), map.firstKey());
        assertEquals(Integer.valueOf(50), map.lastKey());
    }

    /**
     * 测试自定义比较器
     */
    @Test
    public void testCustomComparator() {
        // 按字符串长度排序
        TreeMapImplementation<String, Integer> map = new TreeMapImplementation<>(
            (s1, s2) -> {
                int lenDiff = s1.length() - s2.length();
                return lenDiff != 0 ? lenDiff : s1.compareTo(s2);
            }
        );

        map.put("Apple", 1);
        map.put("Banana", 2);
        map.put("Cat", 3);

        assertEquals("Cat", map.firstKey());
        assertEquals("Banana", map.lastKey());
    }

    /**
     * 测试containsKey
     */
    @Test
    public void testContainsKey() {
        TreeMapImplementation<String, Integer> map = new TreeMapImplementation<>();

        map.put("key1", 100);
        assertTrue(map.containsKey("key1"));
        assertFalse(map.containsKey("key2"));
    }

    /**
     * 测试删除
     */
    @Test
    public void testRemove() {
        TreeMapImplementation<String, Integer> map = new TreeMapImplementation<>();

        map.put("key1", 100);
        assertEquals(Integer.valueOf(100), map.remove("key1"));
        assertNull(map.get("key1"));
        assertEquals(0, map.size());

        assertNull(map.remove("nonexistent"));
    }

    /**
     * 测试清空
     */
    @Test
    public void testClear() {
        TreeMapImplementation<String, Integer> map = new TreeMapImplementation<>();

        map.put("key1", 100);
        map.put("key2", 200);
        map.clear();

        assertEquals(0, map.size());
        assertTrue(map.isEmpty());
    }

    /**
     * 测试firstKey和lastKey
     */
    @Test
    public void testFirstAndLastKey() {
        TreeMapImplementation<Integer, String> map = new TreeMapImplementation<>();

        map.put(30, "thirty");
        map.put(10, "ten");
        map.put(50, "fifty");

        assertEquals(Integer.valueOf(10), map.firstKey());
        assertEquals(Integer.valueOf(50), map.lastKey());
    }

    @Test(expected = NoSuchElementException.class)
    public void testFirstKeyEmpty() {
        TreeMapImplementation<String, Integer> map = new TreeMapImplementation<>();
        map.firstKey();
    }

    @Test(expected = NoSuchElementException.class)
    public void testLastKeyEmpty() {
        TreeMapImplementation<String, Integer> map = new TreeMapImplementation<>();
        map.lastKey();
    }

    /**
     * 测试null键
     */
    @Test(expected = NullPointerException.class)
    public void testNullKey() {
        TreeMapImplementation<String, Integer> map = new TreeMapImplementation<>();
        map.put(null, 100);
    }

    /**
     * 测试大量元素
     */
    @Test
    public void testLargeNumberOfElements() {
        TreeMapImplementation<Integer, String> map = new TreeMapImplementation<>();

        // 添加100个元素
        for (int i = 0; i < 100; i++) {
            map.put(i, "value" + i);
        }

        assertEquals(100, map.size());
        assertEquals(Integer.valueOf(0), map.firstKey());
        assertEquals(Integer.valueOf(99), map.lastKey());

        // 验证所有元素
        for (int i = 0; i < 100; i++) {
            assertEquals("value" + i, map.get(i));
        }
    }

    /**
     * 测试删除后重新添加
     */
    @Test
    public void testRemoveAndReAdd() {
        TreeMapImplementation<String, Integer> map = new TreeMapImplementation<>();

        map.put("key1", 100);
        map.remove("key1");
        assertNull(map.put("key1", 200));
        assertEquals(Integer.valueOf(200), map.get("key1"));
    }
}
