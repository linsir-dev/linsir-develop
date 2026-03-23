package com.linsir.spring.framework.spring_core.env.support;

import com.linsir.spring.framework.spring_core.env.source.MapPropertySource;
import com.linsir.spring.framework.spring_core.env.source.PropertySource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MutablePropertySources 测试类
 *
 * 测试 MutablePropertySources 的各种操作
 *
 * @author linsir
 * @since 1.0.0
 */
class MutablePropertySourcesTest {

    private MutablePropertySources propertySources;

    @BeforeEach
    void setUp() {
        propertySources = new MutablePropertySources();
    }

    @Test
    void testAddFirst() {
        Map<String, Object> map1 = new HashMap<>();
        map1.put("key1", "value1");
        MapPropertySource source1 = new MapPropertySource("source1", map1);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("key2", "value2");
        MapPropertySource source2 = new MapPropertySource("source2", map2);

        // 添加第一个属性源
        propertySources.addFirst(source1);
        assertEquals(1, propertySources.size());
        assertSame(source1, propertySources.get(0));

        // 在开头添加第二个属性源
        propertySources.addFirst(source2);
        assertEquals(2, propertySources.size());
        assertSame(source2, propertySources.get(0));
        assertSame(source1, propertySources.get(1));
    }

    @Test
    void testAddLast() {
        Map<String, Object> map1 = new HashMap<>();
        map1.put("key1", "value1");
        MapPropertySource source1 = new MapPropertySource("source1", map1);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("key2", "value2");
        MapPropertySource source2 = new MapPropertySource("source2", map2);

        // 添加第一个属性源
        propertySources.addLast(source1);
        assertEquals(1, propertySources.size());
        assertSame(source1, propertySources.get(0));

        // 在末尾添加第二个属性源
        propertySources.addLast(source2);
        assertEquals(2, propertySources.size());
        assertSame(source1, propertySources.get(0));
        assertSame(source2, propertySources.get(1));
    }

    @Test
    void testAddBefore() {
        Map<String, Object> map1 = new HashMap<>();
        MapPropertySource source1 = new MapPropertySource("source1", map1);

        Map<String, Object> map2 = new HashMap<>();
        MapPropertySource source2 = new MapPropertySource("source2", map2);

        Map<String, Object> map3 = new HashMap<>();
        MapPropertySource source3 = new MapPropertySource("source3", map3);

        propertySources.addLast(source1);
        propertySources.addLast(source2);

        // 在 source2 之前添加 source3
        propertySources.addBefore("source2", source3);

        assertEquals(3, propertySources.size());
        assertSame(source1, propertySources.get(0));
        assertSame(source3, propertySources.get(1));
        assertSame(source2, propertySources.get(2));
    }

    @Test
    void testAddAfter() {
        Map<String, Object> map1 = new HashMap<>();
        MapPropertySource source1 = new MapPropertySource("source1", map1);

        Map<String, Object> map2 = new HashMap<>();
        MapPropertySource source2 = new MapPropertySource("source2", map2);

        Map<String, Object> map3 = new HashMap<>();
        MapPropertySource source3 = new MapPropertySource("source3", map3);

        propertySources.addLast(source1);
        propertySources.addLast(source2);

        // 在 source1 之后添加 source3
        propertySources.addAfter("source1", source3);

        assertEquals(3, propertySources.size());
        assertSame(source1, propertySources.get(0));
        assertSame(source3, propertySources.get(1));
        assertSame(source2, propertySources.get(2));
    }

    @Test
    void testGetByName() {
        Map<String, Object> map1 = new HashMap<>();
        MapPropertySource source1 = new MapPropertySource("source1", map1);

        propertySources.addLast(source1);

        PropertySource<?> retrieved = propertySources.get("source1");
        assertSame(source1, retrieved);

        assertNull(propertySources.get("nonexistent"));
    }

    @Test
    void testContains() {
        Map<String, Object> map1 = new HashMap<>();
        MapPropertySource source1 = new MapPropertySource("source1", map1);

        propertySources.addLast(source1);

        assertTrue(propertySources.contains("source1"));
        assertFalse(propertySources.contains("nonexistent"));
    }

    @Test
    void testRemove() {
        Map<String, Object> map1 = new HashMap<>();
        MapPropertySource source1 = new MapPropertySource("source1", map1);

        propertySources.addLast(source1);
        assertEquals(1, propertySources.size());

        PropertySource<?> removed = propertySources.remove("source1");
        assertSame(source1, removed);
        assertEquals(0, propertySources.size());

        // 移除不存在的属性源
        assertNull(propertySources.remove("nonexistent"));
    }

    @Test
    void testReplace() {
        Map<String, Object> map1 = new HashMap<>();
        MapPropertySource source1 = new MapPropertySource("source1", map1);

        Map<String, Object> map2 = new HashMap<>();
        MapPropertySource source2 = new MapPropertySource("source1", map2); // 同名

        propertySources.addLast(source1);

        PropertySource<?> old = propertySources.replace("source1", source2);
        assertSame(source1, old);
        assertSame(source2, propertySources.get("source1"));

        // 替换不存在的属性源
        assertNull(propertySources.replace("nonexistent", source2));
    }

    @Test
    void testDuplicateNameHandling() {
        Map<String, Object> map1 = new HashMap<>();
        MapPropertySource source1 = new MapPropertySource("sameName", map1);

        Map<String, Object> map2 = new HashMap<>();
        MapPropertySource source2 = new MapPropertySource("sameName", map2);

        propertySources.addLast(source1);
        assertEquals(1, propertySources.size());

        // 添加同名属性源应该替换旧的
        propertySources.addLast(source2);
        assertEquals(1, propertySources.size());
        assertSame(source2, propertySources.get("sameName"));
    }

    @Test
    void testIsEmpty() {
        assertTrue(propertySources.isEmpty());

        Map<String, Object> map1 = new HashMap<>();
        MapPropertySource source1 = new MapPropertySource("source1", map1);
        propertySources.addLast(source1);

        assertFalse(propertySources.isEmpty());
    }

    @Test
    void testGetPropertySourceNames() {
        Map<String, Object> map1 = new HashMap<>();
        MapPropertySource source1 = new MapPropertySource("source1", map1);

        Map<String, Object> map2 = new HashMap<>();
        MapPropertySource source2 = new MapPropertySource("source2", map2);

        propertySources.addLast(source1);
        propertySources.addLast(source2);

        var names = propertySources.getPropertySourceNames();
        assertEquals(2, names.size());
        assertTrue(names.contains("source1"));
        assertTrue(names.contains("source2"));
    }

    @Test
    void testIterator() {
        Map<String, Object> map1 = new HashMap<>();
        MapPropertySource source1 = new MapPropertySource("source1", map1);

        Map<String, Object> map2 = new HashMap<>();
        MapPropertySource source2 = new MapPropertySource("source2", map2);

        propertySources.addLast(source1);
        propertySources.addLast(source2);

        int count = 0;
        for (PropertySource<?> source : propertySources) {
            count++;
        }
        assertEquals(2, count);
    }

    @Test
    void testCopyConstructor() {
        Map<String, Object> map1 = new HashMap<>();
        MapPropertySource source1 = new MapPropertySource("source1", map1);

        propertySources.addLast(source1);

        MutablePropertySources copy = new MutablePropertySources(propertySources);
        assertEquals(1, copy.size());
        assertTrue(copy.contains("source1"));
    }

    @Test
    void testNullValidation() {
        assertThrows(IllegalArgumentException.class, () -> {
            propertySources.addFirst(null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            propertySources.addLast(null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            propertySources.addBefore("nonexistent", null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            Map<String, Object> map = new HashMap<>();
            MapPropertySource source = new MapPropertySource("test", map);
            propertySources.addBefore("nonexistent", source);
        });
    }
}
