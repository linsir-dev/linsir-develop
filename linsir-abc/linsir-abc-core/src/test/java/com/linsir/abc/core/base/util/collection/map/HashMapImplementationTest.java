package com.linsir.abc.core.base.util.collection.map;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * HashMapImplementation测试类
 */
public class HashMapImplementationTest {

    /**
     * 测试基本添加和获取
     */
    @Test
    public void testBasicPutAndGet() {
        HashMapImplementation<String, Integer> map = new HashMapImplementation<>();

        assertNull(map.put("key1", 100));
        assertEquals(Integer.valueOf(100), map.get("key1"));
        assertEquals(1, map.size());
    }

    /**
     * 测试更新已存在的键
     */
    @Test
    public void testUpdateExistingKey() {
        HashMapImplementation<String, Integer> map = new HashMapImplementation<>();

        map.put("key1", 100);
        assertEquals(Integer.valueOf(100), map.put("key1", 200));
        assertEquals(Integer.valueOf(200), map.get("key1"));
        assertEquals(1, map.size());
    }

    /**
     * 测试null键和null值
     */
    @Test
    public void testNullKeyAndValue() {
        HashMapImplementation<String, Integer> map = new HashMapImplementation<>();

        assertNull(map.put(null, 100));
        assertEquals(Integer.valueOf(100), map.get(null));

        assertNull(map.put("key1", null));
        assertNull(map.get("key1"));

        assertTrue(map.containsKey(null));
        assertTrue(map.containsKey("key1"));
    }

    /**
     * 测试containsKey
     */
    @Test
    public void testContainsKey() {
        HashMapImplementation<String, Integer> map = new HashMapImplementation<>();

        map.put("key1", 100);
        assertTrue(map.containsKey("key1"));
        assertFalse(map.containsKey("key2"));
    }

    /**
     * 测试containsValue
     */
    @Test
    public void testContainsValue() {
        HashMapImplementation<String, Integer> map = new HashMapImplementation<>();

        map.put("key1", 100);
        map.put("key2", 200);
        assertTrue(map.containsValue(100));
        assertTrue(map.containsValue(200));
        assertFalse(map.containsValue(300));
    }

    /**
     * 测试删除
     */
    @Test
    public void testRemove() {
        HashMapImplementation<String, Integer> map = new HashMapImplementation<>();

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
        HashMapImplementation<String, Integer> map = new HashMapImplementation<>();

        map.put("key1", 100);
        map.put("key2", 200);
        map.clear();

        assertEquals(0, map.size());
        assertTrue(map.isEmpty());
        assertNull(map.get("key1"));
    }

    /**
     * 测试isEmpty
     */
    @Test
    public void testIsEmpty() {
        HashMapImplementation<String, Integer> map = new HashMapImplementation<>();

        assertTrue(map.isEmpty());
        map.put("key1", 100);
        assertFalse(map.isEmpty());
    }

    /**
     * 测试扩容
     */
    @Test
    public void testResize() {
        HashMapImplementation<Integer, String> map = new HashMapImplementation<>(4, 0.75f);

        // 添加多个元素触发扩容
        for (int i = 0; i < 10; i++) {
            map.put(i, "value" + i);
        }

        assertEquals(10, map.size());

        // 验证所有元素都可访问
        for (int i = 0; i < 10; i++) {
            assertEquals("value" + i, map.get(i));
        }
    }

    /**
     * 测试哈希冲突
     */
    @Test
    public void testHashCollision() {
        HashMapImplementation<Integer, String> map = new HashMapImplementation<>(4);

        // 添加多个元素，会产生哈希冲突
        for (int i = 0; i < 20; i++) {
            map.put(i, "value" + i);
        }

        // 验证所有元素都可访问
        for (int i = 0; i < 20; i++) {
            assertEquals("value" + i, map.get(i));
        }
    }

    /**
     * 测试不同构造方法
     */
    @Test
    public void testConstructors() {
        HashMapImplementation<String, Integer> map1 = new HashMapImplementation<>();
        assertEquals(0, map1.size());

        HashMapImplementation<String, Integer> map2 = new HashMapImplementation<>(32);
        assertEquals(0, map2.size());

        HashMapImplementation<String, Integer> map3 = new HashMapImplementation<>(32, 0.5f);
        assertEquals(0, map3.size());
    }

    /**
     * 测试非法参数
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNegativeCapacity() {
        new HashMapImplementation<String, Integer>(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidLoadFactor() {
        new HashMapImplementation<String, Integer>(16, -0.5f);
    }
}
