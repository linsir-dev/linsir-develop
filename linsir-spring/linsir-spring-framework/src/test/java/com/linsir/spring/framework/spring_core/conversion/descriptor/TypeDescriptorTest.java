package com.linsir.spring.framework.spring_core.conversion.descriptor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 类型描述符测试类
 * 测试 TypeDescriptor 的各种功能
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-23
 */
@DisplayName("TypeDescriptor 测试")
public class TypeDescriptorTest {

    @Test
    @DisplayName("测试从 Class 创建 TypeDescriptor")
    void testValueOf() {
        TypeDescriptor descriptor = TypeDescriptor.valueOf(String.class);
        assertEquals(String.class, descriptor.getType());
        assertEquals(String.class, descriptor.getObjectType());
    }

    @Test
    @DisplayName("测试从对象创建 TypeDescriptor")
    void testForObject() {
        TypeDescriptor descriptor = TypeDescriptor.forObject("test");
        assertEquals(String.class, descriptor.getType());

        TypeDescriptor nullDescriptor = TypeDescriptor.forObject(null);
        assertEquals(Object.class, nullDescriptor.getType());
    }

    @Test
    @DisplayName("测试从字段创建 TypeDescriptor")
    void testForField() throws NoSuchFieldException {
        Field stringField = TestClass.class.getDeclaredField("stringField");
        TypeDescriptor descriptor = TypeDescriptor.forField(stringField);
        assertEquals(String.class, descriptor.getType());
    }

    @Test
    @DisplayName("测试集合类型")
    void testCollectionType() {
        TypeDescriptor listDescriptor = TypeDescriptor.valueOf(List.class);
        assertTrue(listDescriptor.isCollection());
        assertFalse(listDescriptor.isMap());
        assertFalse(listDescriptor.isArray());
    }

    @Test
    @DisplayName("测试 Map 类型")
    void testMapType() {
        TypeDescriptor mapDescriptor = TypeDescriptor.valueOf(Map.class);
        assertFalse(mapDescriptor.isCollection());
        assertTrue(mapDescriptor.isMap());
        assertFalse(mapDescriptor.isArray());
    }

    @Test
    @DisplayName("测试数组类型")
    void testArrayType() {
        TypeDescriptor arrayDescriptor = TypeDescriptor.valueOf(String[].class);
        assertFalse(arrayDescriptor.isCollection());
        assertFalse(arrayDescriptor.isMap());
        assertTrue(arrayDescriptor.isArray());
    }

    @Test
    @DisplayName("测试数组元素类型")
    void testArrayElementType() {
        TypeDescriptor arrayDescriptor = TypeDescriptor.valueOf(String[].class);
        TypeDescriptor elementDescriptor = arrayDescriptor.getElementTypeDescriptor();

        assertNotNull(elementDescriptor);
        assertEquals(String.class, elementDescriptor.getType());
    }

    @Test
    @DisplayName("测试基本类型")
    void testPrimitiveType() {
        TypeDescriptor intDescriptor = TypeDescriptor.valueOf(int.class);
        assertEquals(int.class, intDescriptor.getType());

        TypeDescriptor booleanDescriptor = TypeDescriptor.valueOf(boolean.class);
        assertEquals(boolean.class, booleanDescriptor.getType());
    }

    @Test
    @DisplayName("测试注解获取")
    void testAnnotation() throws NoSuchFieldException {
        Field annotatedField = TestClass.class.getDeclaredField("annotatedField");
        TypeDescriptor descriptor = TypeDescriptor.forField(annotatedField);

        TestAnnotation annotation = descriptor.getAnnotation(TestAnnotation.class);
        assertNotNull(annotation);
        assertEquals("test", annotation.value());
    }

    @Test
    @DisplayName("测试类型描述符相等性")
    void testEquality() {
        TypeDescriptor desc1 = TypeDescriptor.valueOf(String.class);
        TypeDescriptor desc2 = TypeDescriptor.valueOf(String.class);
        TypeDescriptor desc3 = TypeDescriptor.valueOf(Integer.class);

        assertEquals(desc1, desc2);
        assertEquals(desc1.hashCode(), desc2.hashCode());
        assertNotEquals(desc1, desc3);
    }

    @Test
    @DisplayName("测试 toString")
    void testToString() {
        TypeDescriptor descriptor = TypeDescriptor.valueOf(String.class);
        String str = descriptor.toString();
        assertTrue(str.contains("String"));
    }

    @Test
    @DisplayName("测试创建集合类型描述符")
    void testCollectionCreation() {
        TypeDescriptor elementType = TypeDescriptor.valueOf(String.class);
        TypeDescriptor collectionType = TypeDescriptor.collection(ArrayList.class, elementType);

        assertTrue(collectionType.isCollection());
    }

    @Test
    @DisplayName("测试创建 Map 类型描述符")
    void testMapCreation() {
        TypeDescriptor keyType = TypeDescriptor.valueOf(String.class);
        TypeDescriptor valueType = TypeDescriptor.valueOf(Integer.class);
        TypeDescriptor mapType = TypeDescriptor.map(HashMap.class, keyType, valueType);

        assertTrue(mapType.isMap());
    }

    // 测试用的注解
    @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    @interface TestAnnotation {
        String value();
    }

    // 测试类
    @SuppressWarnings("unused")
    private static class TestClass {
        private String stringField;

        @TestAnnotation("test")
        private String annotatedField;

        private List<String> stringList;

        private Map<String, Integer> stringIntegerMap;

        public void testMethod(String param1, Integer param2) {
        }
    }
}
