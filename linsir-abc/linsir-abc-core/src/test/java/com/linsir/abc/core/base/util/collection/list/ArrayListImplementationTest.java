package com.linsir.abc.core.base.util.collection.list;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * ArrayListImplementation测试类
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class ArrayListImplementationTest {

    @Test
    public void testDefaultConstructor() {
        ArrayListImplementation<String> list = new ArrayListImplementation<>();
        assertEquals(0, list.size());
        assertTrue(list.isEmpty());
    }

    @Test
    public void testConstructorWithCapacity() {
        ArrayListImplementation<String> list = new ArrayListImplementation<>(20);
        assertEquals(0, list.size());
        assertTrue(list.isEmpty());

        // 测试非法容量
        assertThrows(IllegalArgumentException.class, () -> {
            new ArrayListImplementation<String>(-1);
        });
    }

    @Test
    public void testAdd() {
        ArrayListImplementation<String> list = new ArrayListImplementation<>();
        
        assertTrue(list.add("元素1"));
        assertEquals(1, list.size());
        
        list.add("元素2");
        list.add("元素3");
        assertEquals(3, list.size());
    }

    @Test
    public void testAddAtIndex() {
        ArrayListImplementation<String> list = new ArrayListImplementation<>();
        list.add("元素1");
        list.add("元素2");
        list.add("元素3");
        
        // 在中间插入
        list.add(1, "插入元素");
        assertEquals(4, list.size());
        assertEquals("元素1", list.get(0));
        assertEquals("插入元素", list.get(1));
        assertEquals("元素2", list.get(2));
        assertEquals("元素3", list.get(3));
        
        // 在开头插入
        list.add(0, "开头元素");
        assertEquals("开头元素", list.get(0));
        
        // 在末尾插入
        list.add(list.size(), "末尾元素");
        assertEquals("末尾元素", list.get(list.size() - 1));
    }

    @Test
    public void testAddAtIndexOutOfBounds() {
        ArrayListImplementation<String> list = new ArrayListImplementation<>();
        list.add("元素1");
        
        assertThrows(IndexOutOfBoundsException.class, () -> {
            list.add(-1, "非法元素");
        });
        
        assertThrows(IndexOutOfBoundsException.class, () -> {
            list.add(10, "非法元素");
        });
    }

    @Test
    public void testGet() {
        ArrayListImplementation<String> list = new ArrayListImplementation<>();
        list.add("元素1");
        list.add("元素2");
        list.add("元素3");
        
        assertEquals("元素1", list.get(0));
        assertEquals("元素2", list.get(1));
        assertEquals("元素3", list.get(2));
    }

    @Test
    public void testGetOutOfBounds() {
        ArrayListImplementation<String> list = new ArrayListImplementation<>();
        list.add("元素1");
        
        assertThrows(IndexOutOfBoundsException.class, () -> {
            list.get(-1);
        });
        
        assertThrows(IndexOutOfBoundsException.class, () -> {
            list.get(10);
        });
    }

    @Test
    public void testSet() {
        ArrayListImplementation<String> list = new ArrayListImplementation<>();
        list.add("元素1");
        list.add("元素2");
        
        String oldValue = list.set(0, "新元素");
        assertEquals("元素1", oldValue);
        assertEquals("新元素", list.get(0));
    }

    @Test
    public void testRemoveByIndex() {
        ArrayListImplementation<String> list = new ArrayListImplementation<>();
        list.add("元素1");
        list.add("元素2");
        list.add("元素3");
        
        String removed = list.remove(1);
        assertEquals("元素2", removed);
        assertEquals(2, list.size());
        assertEquals("元素1", list.get(0));
        assertEquals("元素3", list.get(1));
    }

    @Test
    public void testRemoveByObject() {
        ArrayListImplementation<String> list = new ArrayListImplementation<>();
        list.add("元素1");
        list.add("元素2");
        list.add("元素3");
        
        assertTrue(list.remove("元素2"));
        assertEquals(2, list.size());
        assertFalse(list.contains("元素2"));
        
        // 移除不存在的元素
        assertFalse(list.remove("不存在的元素"));
    }

    @Test
    public void testRemoveNull() {
        ArrayListImplementation<String> list = new ArrayListImplementation<>();
        list.add("元素1");
        list.add(null);
        list.add("元素3");
        
        assertTrue(list.remove(null));
        assertEquals(2, list.size());
        assertFalse(list.contains(null));
    }

    @Test
    public void testContains() {
        ArrayListImplementation<String> list = new ArrayListImplementation<>();
        list.add("元素1");
        list.add("元素2");
        
        assertTrue(list.contains("元素1"));
        assertTrue(list.contains("元素2"));
        assertFalse(list.contains("元素3"));
    }

    @Test
    public void testIndexOf() {
        ArrayListImplementation<String> list = new ArrayListImplementation<>();
        list.add("元素1");
        list.add("元素2");
        list.add("元素1"); // 重复元素
        
        assertEquals(0, list.indexOf("元素1"));
        assertEquals(1, list.indexOf("元素2"));
        assertEquals(-1, list.indexOf("元素3"));
    }

    @Test
    public void testIndexOfNull() {
        ArrayListImplementation<String> list = new ArrayListImplementation<>();
        list.add("元素1");
        list.add(null);
        list.add("元素3");
        
        assertEquals(1, list.indexOf(null));
    }

    @Test
    public void testClear() {
        ArrayListImplementation<String> list = new ArrayListImplementation<>();
        list.add("元素1");
        list.add("元素2");
        list.add("元素3");
        
        list.clear();
        assertEquals(0, list.size());
        assertTrue(list.isEmpty());
    }

    @Test
    public void testGrow() {
        // 测试扩容机制
        ArrayListImplementation<Integer> list = new ArrayListImplementation<>(2);
        
        // 添加超过初始容量的元素，触发扩容
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }
        
        assertEquals(20, list.size());
        
        // 验证所有元素都正确存储
        for (int i = 0; i < 20; i++) {
            assertEquals(i, list.get(i));
        }
    }

    @Test
    public void testIterator() {
        ArrayListImplementation<String> list = new ArrayListImplementation<>();
        list.add("元素1");
        list.add("元素2");
        list.add("元素3");
        
        Iterator<String> iterator = list.iterator();
        assertTrue(iterator.hasNext());
        assertEquals("元素1", iterator.next());
        assertEquals("元素2", iterator.next());
        assertEquals("元素3", iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testIteratorNoSuchElement() {
        ArrayListImplementation<String> list = new ArrayListImplementation<>();
        list.add("元素1");
        
        Iterator<String> iterator = list.iterator();
        iterator.next(); // 消费唯一元素
        
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    public void testToArray() {
        ArrayListImplementation<String> list = new ArrayListImplementation<>();
        list.add("元素1");
        list.add("元素2");
        list.add("元素3");
        
        Object[] array = list.toArray();
        assertEquals(3, array.length);
        assertEquals("元素1", array[0]);
        assertEquals("元素2", array[1]);
        assertEquals("元素3", array[2]);
    }

    @Test
    public void testToString() {
        ArrayListImplementation<String> list = new ArrayListImplementation<>();
        assertEquals("[]", list.toString());
        
        list.add("元素1");
        assertEquals("[元素1]", list.toString());
        
        list.add("元素2");
        assertEquals("[元素1, 元素2]", list.toString());
    }

    @Test
    public void testSizeAndIsEmpty() {
        ArrayListImplementation<String> list = new ArrayListImplementation<>();
        
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
        
        list.add("元素1");
        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
        
        list.remove(0);
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    public void testAddWithNullElements() {
        ArrayListImplementation<String> list = new ArrayListImplementation<>();
        list.add(null);
        list.add("元素2");
        list.add(null);
        
        assertEquals(3, list.size());
        assertNull(list.get(0));
        assertEquals("元素2", list.get(1));
        assertNull(list.get(2));
    }

    @Test
    public void testRemoveFirstElement() {
        ArrayListImplementation<String> list = new ArrayListImplementation<>();
        list.add("元素1");
        list.add("元素2");
        list.add("元素3");
        
        list.remove(0);
        assertEquals(2, list.size());
        assertEquals("元素2", list.get(0));
        assertEquals("元素3", list.get(1));
    }

    @Test
    public void testRemoveLastElement() {
        ArrayListImplementation<String> list = new ArrayListImplementation<>();
        list.add("元素1");
        list.add("元素2");
        list.add("元素3");
        
        list.remove(2);
        assertEquals(2, list.size());
        assertEquals("元素1", list.get(0));
        assertEquals("元素2", list.get(1));
    }
}
