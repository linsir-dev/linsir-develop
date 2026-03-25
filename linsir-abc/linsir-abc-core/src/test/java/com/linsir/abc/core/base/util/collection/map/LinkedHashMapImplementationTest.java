package com.linsir.abc.core.base.util.collection.map;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * LinkedHashMapImplementation测试类
 */
public class LinkedHashMapImplementationTest {

    /**
     * 测试基本添加和获取
     */
    @Test
    public void testBasicPutAndGet() {
        LinkedHashMapImplementation<String, Integer> map = new LinkedHashMapImplementation<>();

        assertNull(map.put("key1", 100));
        assertEquals(Integer.valueOf(100), map.get("key1"));
        assertEquals(1, map.size());
    }

    /**
     * 测试插入顺序
     */
    @Test
    public void testInsertionOrder() {
        LinkedHashMapImplementation<String, Integer> map = new LinkedHashMapImplementation<>();

        map.put("first", 1);
        map.put("second", 2);
        map.put("third", 3);

        // 验证插入顺序
        LinkedHashMapImplementation.Node<String, Integer> current = map.head;
        assertEquals("first", current.key);
        current = current.after;
        assertEquals("second", current.key);
        current = current.after;
        assertEquals("third", current.key);
        assertNull(current.after);
    }

    /**
     * 测试访问顺序
     */
    @Test
    public void testAccessOrder() {
        LinkedHashMapImplementation<String, Integer> map =
            new LinkedHashMapImplementation<>(16, 0.75f, true);

        map.put("A", 1);
        map.put("B", 2);
        map.put("C", 3);
        map.put("D", 4);

        // 访问B
        map.get("B");

        // 验证B移到了最后
        LinkedHashMapImplementation.Node<String, Integer> current = map.head;
        assertEquals("A", current.key);
        current = current.after;
        assertEquals("C", current.key);
        current = current.after;
        assertEquals("D", current.key);
        current = current.after;
        assertEquals("B", current.key);
    }

    /**
     * 测试null键和null值
     */
    @Test
    public void testNullKeyAndValue() {
        LinkedHashMapImplementation<String, Integer> map = new LinkedHashMapImplementation<>();

        assertNull(map.put(null, 100));
        assertEquals(Integer.valueOf(100), map.get(null));

        assertNull(map.put("key1", null));
        assertNull(map.get("key1"));
    }

    /**
     * 测试containsKey和containsValue
     */
    @Test
    public void testContains() {
        LinkedHashMapImplementation<String, Integer> map = new LinkedHashMapImplementation<>();

        map.put("key1", 100);
        assertTrue(map.containsKey("key1"));
        assertTrue(map.containsValue(100));
        assertFalse(map.containsKey("key2"));
        assertFalse(map.containsValue(200));
    }

    /**
     * 测试删除
     */
    @Test
    public void testRemove() {
        LinkedHashMapImplementation<String, Integer> map = new LinkedHashMapImplementation<>();

        map.put("key1", 100);
        map.put("key2", 200);

        assertEquals(Integer.valueOf(100), map.remove("key1"));
        assertEquals(1, map.size());
        assertFalse(map.containsKey("key1"));

        // 验证链表链接正确
        assertEquals("key2", map.head.key);
        assertEquals("key2", map.tail.key);
    }

    /**
     * 测试清空
     */
    @Test
    public void testClear() {
        LinkedHashMapImplementation<String, Integer> map = new LinkedHashMapImplementation<>();

        map.put("key1", 100);
        map.put("key2", 200);
        map.clear();

        assertEquals(0, map.size());
        assertTrue(map.isEmpty());
        assertNull(map.head);
        assertNull(map.tail);
    }

    /**
     * 测试更新已存在的键不改变顺序（插入顺序模式）
     */
    @Test
    public void testUpdateDoesNotChangeOrder() {
        LinkedHashMapImplementation<String, Integer> map = new LinkedHashMapImplementation<>();

        map.put("first", 1);
        map.put("second", 2);
        map.put("third", 3);

        // 更新second的值
        map.put("second", 20);

        // 验证顺序不变
        LinkedHashMapImplementation.Node<String, Integer> current = map.head;
        assertEquals("first", current.key);
        current = current.after;
        assertEquals("second", current.key);
        current = current.after;
        assertEquals("third", current.key);
    }

    /**
     * 测试扩容
     */
    @Test
    public void testResize() {
        LinkedHashMapImplementation<Integer, String> map = new LinkedHashMapImplementation<>(4, 0.75f, false);

        for (int i = 0; i < 20; i++) {
            map.put(i, "value" + i);
        }

        assertEquals(20, map.size());

        // 验证顺序保持不变
        LinkedHashMapImplementation.Node<Integer, String> current = map.head;
        for (int i = 0; i < 20; i++) {
            assertEquals(Integer.valueOf(i), current.key);
            current = current.after;
        }
    }

    /**
     * 测试不同构造方法
     */
    @Test
    public void testConstructors() {
        LinkedHashMapImplementation<String, Integer> map1 = new LinkedHashMapImplementation<>();
        assertEquals(0, map1.size());

        LinkedHashMapImplementation<String, Integer> map2 = new LinkedHashMapImplementation<>(32);
        assertEquals(0, map2.size());

        LinkedHashMapImplementation<String, Integer> map3 = new LinkedHashMapImplementation<>(32, 0.5f);
        assertEquals(0, map3.size());

        LinkedHashMapImplementation<String, Integer> map4 = new LinkedHashMapImplementation<>(32, 0.5f, true);
        assertEquals(0, map4.size());
    }
}
