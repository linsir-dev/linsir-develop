package com.linsir.spring.framework.spring_core.annotation.utils;

import com.linsir.spring.framework.spring_core.annotation.attribute.AnnotationAttributes;
import com.linsir.spring.framework.spring_core.annotation.meta.Component;
import com.linsir.spring.framework.spring_core.annotation.meta.Service;
import org.junit.jupiter.api.Test;

import java.lang.annotation.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AnnotationUtils 测试类
 *
 * 测试注解工具类的各种功能。
 *
 * @author linsir
 * @since 1.0.0
 */
class AnnotationUtilsTest {

    /**
     * 测试用的自定义注解
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
    public @interface TestAnnotation {
        String value() default "default";
        int count() default 0;
        boolean active() default false;
        Class<?> clazz() default Object.class;
        String[] names() default {};
    }

    /**
     * 带注解的测试类
     */
    @TestAnnotation(value = "test", count = 5, active = true)
    static class AnnotatedClass {
    }

    /**
     * 继承测试类
     */
    static class InheritedClass extends AnnotatedClass {
    }

    /**
     * 无注解的测试类
     */
    static class NonAnnotatedClass {
    }

    /**
     * 测试获取直接声明的注解
     */
    @Test
    void testGetAnnotation() {
        TestAnnotation annotation = AnnotationUtils.getAnnotation(
            AnnotatedClass.class, TestAnnotation.class);

        assertNotNull(annotation);
        assertEquals("test", annotation.value());
        assertEquals(5, annotation.count());
        assertTrue(annotation.active());
    }

    /**
     * 测试获取不存在的注解返回 null
     */
    @Test
    void testGetAnnotationNotFound() {
        TestAnnotation annotation = AnnotationUtils.getAnnotation(
            NonAnnotatedClass.class, TestAnnotation.class);

        assertNull(annotation);
    }

    /**
     * 测试获取 null 元素返回 null
     */
    @Test
    void testGetAnnotationWithNullElement() {
        TestAnnotation annotation = AnnotationUtils.getAnnotation(null, TestAnnotation.class);
        assertNull(annotation);
    }

    /**
     * 测试获取 null 注解类型返回 null
     */
    @Test
    void testGetAnnotationWithNullType() {
        TestAnnotation annotation = AnnotationUtils.getAnnotation(AnnotatedClass.class, null);
        assertNull(annotation);
    }

    /**
     * 测试查找继承的注解
     */
    @Test
    void testFindAnnotationFromSuperclass() {
        TestAnnotation annotation = AnnotationUtils.findAnnotation(
            InheritedClass.class, TestAnnotation.class);

        assertNotNull(annotation);
        assertEquals("test", annotation.value());
    }

    /**
     * 测试查找注解从当前类开始
     */
    @Test
    void testFindAnnotationFromCurrentClass() {
        TestAnnotation annotation = AnnotationUtils.findAnnotation(
            AnnotatedClass.class, TestAnnotation.class);

        assertNotNull(annotation);
        assertEquals("test", annotation.value());
    }

    /**
     * 测试判断是否存在注解
     */
    @Test
    void testHasAnnotation() {
        assertTrue(AnnotationUtils.hasAnnotation(AnnotatedClass.class, TestAnnotation.class));
        assertFalse(AnnotationUtils.hasAnnotation(NonAnnotatedClass.class, TestAnnotation.class));
    }

    /**
     * 测试获取注解属性
     */
    @Test
    void testGetAnnotationAttributes() {
        TestAnnotation annotation = AnnotatedClass.class.getAnnotation(TestAnnotation.class);
        AnnotationAttributes attributes = AnnotationUtils.getAnnotationAttributes(annotation);

        assertNotNull(attributes);
        assertEquals("test", attributes.getString("value"));
        assertEquals(5, attributes.getInt("count"));
        assertTrue(attributes.getBoolean("active"));
    }

    /**
     * 测试获取 null 注解的属性返回空属性
     */
    @Test
    void testGetAnnotationAttributesWithNull() {
        AnnotationAttributes attributes = AnnotationUtils.getAnnotationAttributes(null);
        assertNotNull(attributes);
        assertTrue(attributes.isEmpty());
    }

    /**
     * 测试获取单个属性值
     */
    @Test
    void testGetAnnotationAttribute() {
        TestAnnotation annotation = AnnotatedClass.class.getAnnotation(TestAnnotation.class);

        Object value = AnnotationUtils.getAnnotationAttribute(annotation, "value");
        assertEquals("test", value);

        Object count = AnnotationUtils.getAnnotationAttribute(annotation, "count");
        assertEquals(5, count);
    }

    /**
     * 测试获取不存在的属性返回 null
     */
    @Test
    void testGetAnnotationAttributeNotFound() {
        TestAnnotation annotation = AnnotatedClass.class.getAnnotation(TestAnnotation.class);
        Object value = AnnotationUtils.getAnnotationAttribute(annotation, "nonexistent");
        assertNull(value);
    }

    /**
     * 测试获取带类型的属性值
     */
    @Test
    void testGetAnnotationAttributeWithType() {
        TestAnnotation annotation = AnnotatedClass.class.getAnnotation(TestAnnotation.class);

        String value = AnnotationUtils.getAnnotationAttribute(annotation, "value", String.class);
        assertEquals("test", value);

        Integer count = AnnotationUtils.getAnnotationAttribute(annotation, "count", Integer.class);
        assertEquals(5, count);
    }

    /**
     * 测试获取类型不匹配的属性返回 null
     */
    @Test
    void testGetAnnotationAttributeTypeMismatch() {
        TestAnnotation annotation = AnnotatedClass.class.getAnnotation(TestAnnotation.class);
        Integer value = AnnotationUtils.getAnnotationAttribute(annotation, "value", Integer.class);
        assertNull(value);
    }

    /**
     * 测试获取默认值
     */
    @Test
    void testGetDefaultValue() {
        Object defaultValue = AnnotationUtils.getDefaultValue(TestAnnotation.class, "value");
        assertEquals("default", defaultValue);

        Object defaultCount = AnnotationUtils.getDefaultValue(TestAnnotation.class, "count");
        assertEquals(0, defaultCount);
    }

    /**
     * 测试获取不存在的默认值返回 null
     */
    @Test
    void testGetDefaultValueNotFound() {
        Object defaultValue = AnnotationUtils.getDefaultValue(TestAnnotation.class, "nonexistent");
        assertNull(defaultValue);
    }

    /**
     * 测试判断注解是否包含元注解
     */
    @Test
    void testIsAnnotatedWith() {
        // @Service 包含 @Component 元注解
        assertTrue(AnnotationUtils.isAnnotatedWith(Service.class, Component.class));
        // @Component 不包含 @Service
        assertFalse(AnnotationUtils.isAnnotatedWith(Component.class, Service.class));
    }

    /**
     * 测试获取所有注解
     */
    @Test
    void testGetAnnotations() {
        Annotation[] annotations = AnnotationUtils.getAnnotations(AnnotatedClass.class);
        assertNotNull(annotations);
        assertTrue(annotations.length > 0);

        boolean found = false;
        for (Annotation annotation : annotations) {
            if (annotation instanceof TestAnnotation) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    /**
     * 测试获取 null 元素的注解返回空数组
     */
    @Test
    void testGetAnnotationsWithNull() {
        Annotation[] annotations = AnnotationUtils.getAnnotations(null);
        assertNotNull(annotations);
        assertEquals(0, annotations.length);
    }

    /**
     * 测试获取重复注解
     */
    @Test
    void testGetRepeatableAnnotations() {
        TestAnnotation[] annotations = AnnotationUtils.getRepeatableAnnotations(
            AnnotatedClass.class, TestAnnotation.class);

        assertNotNull(annotations);
        assertEquals(1, annotations.length);
        assertEquals("test", annotations[0].value());
    }

    /**
     * 测试注解字符串表示
     */
    @Test
    void testToString() {
        TestAnnotation annotation = AnnotatedClass.class.getAnnotation(TestAnnotation.class);
        String str = AnnotationUtils.toString(annotation);

        assertNotNull(str);
        assertTrue(str.contains("@"));
        assertTrue(str.contains("TestAnnotation"));
        assertTrue(str.contains("value=\"test\""));
        assertTrue(str.contains("count=5"));
    }

    /**
     * 测试 null 注解的字符串表示
     */
    @Test
    void testToStringWithNull() {
        String str = AnnotationUtils.toString(null);
        assertEquals("null", str);
    }

    /**
     * 测试包含数组属性的注解字符串表示
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface ArrayAnnotation {
        String[] values() default {"a", "b", "c"};
    }

    @ArrayAnnotation(values = {"x", "y", "z"})
    static class ArrayAnnotatedClass {
    }

    @Test
    void testToStringWithArray() {
        ArrayAnnotation annotation = ArrayAnnotatedClass.class.getAnnotation(ArrayAnnotation.class);
        String str = AnnotationUtils.toString(annotation);

        assertNotNull(str);
        assertTrue(str.contains("values=[\"x\", \"y\", \"z\"]"));
    }
}
