package com.linsir.spring.framework.spring_core.annotation.attribute;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AnnotationAttributes 测试类
 *
 * 测试注解属性映射的各种功能。
 *
 * @author linsir
 * @since 1.0.0
 */
class AnnotationAttributesTest {

    /**
     * 测试创建空的属性映射
     */
    @Test
    void testEmptyConstructor() {
        AnnotationAttributes attributes = new AnnotationAttributes();
        assertNotNull(attributes);
        assertTrue(attributes.isEmpty());
    }

    /**
     * 测试从 Map 创建属性映射
     */
    @Test
    void testFromMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "test");
        map.put("count", 10);

        AnnotationAttributes attributes = AnnotationAttributes.fromMap(map);

        assertNotNull(attributes);
        assertEquals(2, attributes.size());
        assertEquals("test", attributes.getString("name"));
        assertEquals(10, attributes.getInt("count"));
    }

    /**
     * 测试从 null Map 创建返回空属性
     */
    @Test
    void testFromNullMap() {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(null);
        assertNotNull(attributes);
        assertTrue(attributes.isEmpty());
    }

    /**
     * 测试获取字符串属性
     */
    @Test
    void testGetString() {
        AnnotationAttributes attributes = new AnnotationAttributes();
        attributes.put("name", "test");

        assertEquals("test", attributes.getString("name"));
    }

    /**
     * 测试获取不存在的字符串属性返回 null
     */
    @Test
    void testGetStringNotFound() {
        AnnotationAttributes attributes = new AnnotationAttributes();
        assertNull(attributes.getString("nonexistent"));
    }

    /**
     * 测试获取字符串属性带默认值
     */
    @Test
    void testGetStringWithDefault() {
        AnnotationAttributes attributes = new AnnotationAttributes();
        attributes.put("name", "test");

        assertEquals("test", attributes.getString("name", "default"));
        assertEquals("default", attributes.getString("nonexistent", "default"));
    }

    /**
     * 测试获取布尔属性
     */
    @Test
    void testGetBoolean() {
        AnnotationAttributes attributes = new AnnotationAttributes();
        attributes.put("active", true);
        attributes.put("inactive", false);

        assertTrue(attributes.getBoolean("active"));
        assertFalse(attributes.getBoolean("inactive"));
    }

    /**
     * 测试获取不存在的布尔属性返回 false
     */
    @Test
    void testGetBooleanNotFound() {
        AnnotationAttributes attributes = new AnnotationAttributes();
        assertFalse(attributes.getBoolean("nonexistent"));
    }

    /**
     * 测试获取布尔属性带默认值
     */
    @Test
    void testGetBooleanWithDefault() {
        AnnotationAttributes attributes = new AnnotationAttributes();
        attributes.put("active", true);

        assertTrue(attributes.getBoolean("active", false));
        assertTrue(attributes.getBoolean("nonexistent", true));
    }

    /**
     * 测试获取整数属性
     */
    @Test
    void testGetInt() {
        AnnotationAttributes attributes = new AnnotationAttributes();
        attributes.put("count", 42);

        assertEquals(42, attributes.getInt("count"));
    }

    /**
     * 测试获取不存在的整数属性返回 0
     */
    @Test
    void testGetIntNotFound() {
        AnnotationAttributes attributes = new AnnotationAttributes();
        assertEquals(0, attributes.getInt("nonexistent"));
    }

    /**
     * 测试获取整数属性带默认值
     */
    @Test
    void testGetIntWithDefault() {
        AnnotationAttributes attributes = new AnnotationAttributes();
        attributes.put("count", 42);

        assertEquals(42, attributes.getInt("count", 0));
        assertEquals(100, attributes.getInt("nonexistent", 100));
    }

    /**
     * 测试获取长整数属性
     */
    @Test
    void testGetLong() {
        AnnotationAttributes attributes = new AnnotationAttributes();
        attributes.put("timestamp", 1234567890L);

        assertEquals(1234567890L, attributes.getLong("timestamp"));
    }

    /**
     * 测试获取长整数属性带默认值
     */
    @Test
    void testGetLongWithDefault() {
        AnnotationAttributes attributes = new AnnotationAttributes();
        assertEquals(100L, attributes.getLong("nonexistent", 100L));
    }

    /**
     * 测试获取指定类型的属性
     */
    @Test
    void testGetAttribute() {
        AnnotationAttributes attributes = new AnnotationAttributes();
        attributes.put("name", "test");

        String value = attributes.getAttribute("name", String.class);
        assertEquals("test", value);
    }

    /**
     * 测试获取指定类型的属性带默认值
     */
    @Test
    void testGetAttributeWithDefault() {
        AnnotationAttributes attributes = new AnnotationAttributes();
        attributes.put("name", "test");

        String value = attributes.getAttribute("name", String.class, "default");
        assertEquals("test", value);

        String defaultValue = attributes.getAttribute("nonexistent", String.class, "default");
        assertEquals("default", defaultValue);
    }

    /**
     * 测试获取类型不匹配返回 null
     */
    @Test
    void testGetAttributeTypeMismatch() {
        AnnotationAttributes attributes = new AnnotationAttributes();
        attributes.put("count", 42);

        String value = attributes.getAttribute("count", String.class);
        assertNull(value);
    }

    /**
     * 测试获取类属性
     */
    @Test
    void testGetClass() {
        AnnotationAttributes attributes = new AnnotationAttributes();
        attributes.put("clazz", String.class);

        Class<?> clazz = attributes.getClass("clazz");
        assertEquals(String.class, clazz);
    }

    /**
     * 测试获取类属性带类型参数
     */
    @Test
    void testGetClassWithType() {
        AnnotationAttributes attributes = new AnnotationAttributes();
        attributes.put("clazz", String.class);

        Class<String> clazz = attributes.getClassAttribute("clazz", String.class);
        assertEquals(String.class, clazz);
    }

    /**
     * 测试获取数组属性
     */
    @Test
    void testGetArrayAttribute() {
        AnnotationAttributes attributes = new AnnotationAttributes();
        String[] names = {"a", "b", "c"};
        attributes.put("names", names);

        String[] result = attributes.getArrayAttribute("names");
        assertNotNull(result);
        assertEquals(3, result.length);
        assertEquals("a", result[0]);
    }

    /**
     * 测试判断属性是否存在
     */
    @Test
    void testHasAttribute() {
        AnnotationAttributes attributes = new AnnotationAttributes();
        attributes.put("name", "test");

        assertTrue(attributes.hasAttribute("name"));
        assertFalse(attributes.hasAttribute("nonexistent"));
    }

    /**
     * 测试判断属性是否为空
     */
    @Test
    void testIsEmpty() {
        AnnotationAttributes attributes = new AnnotationAttributes();
        attributes.put("name", "test");
        attributes.put("empty", "");
        attributes.put("null", null);

        assertFalse(attributes.isEmpty("name"));
        assertTrue(attributes.isEmpty("empty"));
        assertTrue(attributes.isEmpty("null"));
        assertTrue(attributes.isEmpty("nonexistent"));
    }

    /**
     * 测试添加属性如果不存在
     */
    @Test
    void testPutIfAbsentAttribute() {
        AnnotationAttributes attributes = new AnnotationAttributes();

        assertTrue(attributes.putIfAbsentAttribute("name", "test"));
        assertEquals("test", attributes.getString("name"));

        assertFalse(attributes.putIfAbsentAttribute("name", "new"));
        assertEquals("test", attributes.getString("name"));
    }

    /**
     * 测试合并属性
     */
    @Test
    void testMerge() {
        AnnotationAttributes primary = new AnnotationAttributes();
        primary.put("name", "primary");
        primary.put("count", 10);

        AnnotationAttributes secondary = new AnnotationAttributes();
        secondary.put("name", "secondary");
        secondary.put("value", "test");

        primary.merge(secondary);

        assertEquals("primary", primary.getString("name"));
        assertEquals(10, primary.getInt("count"));
        assertEquals("test", primary.getString("value"));
    }

    /**
     * 测试合并 null 属性
     */
    @Test
    void testMergeNull() {
        AnnotationAttributes attributes = new AnnotationAttributes();
        attributes.put("name", "test");

        attributes.merge(null);

        assertEquals("test", attributes.getString("name"));
    }

    /**
     * 测试字符串表示
     */
    @Test
    void testToString() {
        AnnotationAttributes attributes = new AnnotationAttributes();
        attributes.put("name", "test");
        attributes.put("count", 42);

        String str = attributes.toString();
        assertTrue(str.contains("name=\"test\""));
        assertTrue(str.contains("count=42"));
    }

    /**
     * 测试包含数组的字符串表示
     */
    @Test
    void testToStringWithArrayAttribute() {
        AnnotationAttributes attributes = new AnnotationAttributes();
        attributes.put("names", new String[]{"a", "b", "c"});

        String str = attributes.toString();
        assertTrue(str.contains("names=[\"a\", \"b\", \"c\"]"));
    }
}
