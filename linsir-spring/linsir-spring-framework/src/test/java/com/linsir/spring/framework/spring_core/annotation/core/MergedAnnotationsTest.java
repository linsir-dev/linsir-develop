package com.linsir.spring.framework.spring_core.annotation.core;

import com.linsir.spring.framework.spring_core.annotation.meta.Component;
import com.linsir.spring.framework.spring_core.annotation.meta.Service;
import org.junit.jupiter.api.Test;

import java.lang.annotation.*;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MergedAnnotations 测试类
 *
 * 测试合并注解集合的各种功能。
 *
 * @author linsir
 * @since 1.0.0
 */
class MergedAnnotationsTest {

    /**
     * 测试用的元注解
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.ANNOTATION_TYPE)
    public @interface MetaAnnotation {
        String value() default "";
    }

    /**
     * 带元注解的自定义注解
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @MetaAnnotation("meta")
    public @interface CustomAnnotation {
        String name() default "";
    }

    /**
     * 带注解的测试类
     */
    @CustomAnnotation(name = "test")
    static class AnnotatedClass {
    }

    /**
     * 带 Service 注解的测试类
     */
    @Service("myService")
    static class ServiceClass {
    }

    /**
     * 无注解的测试类
     */
    static class NonAnnotatedClass {
    }

    /**
     * 父类
     */
    @CustomAnnotation(name = "parent")
    static class ParentClass {
    }

    /**
     * 子类
     */
    static class ChildClass extends ParentClass {
    }

    /**
     * 测试从元素创建 MergedAnnotations
     */
    @Test
    void testFrom() {
        MergedAnnotations annotations = MergedAnnotations.from(AnnotatedClass.class);
        assertNotNull(annotations);
        assertFalse(annotations.isEmpty());
    }

    /**
     * 测试判断是否包含注解
     */
    @Test
    void testIsPresent() {
        MergedAnnotations annotations = MergedAnnotations.from(AnnotatedClass.class);

        assertTrue(annotations.isPresent(CustomAnnotation.class));
        assertFalse(annotations.isPresent(Service.class));
    }

    /**
     * 测试判断是否直接声明注解
     */
    @Test
    void testIsDirectlyPresent() {
        MergedAnnotations annotations = MergedAnnotations.from(AnnotatedClass.class);

        assertTrue(annotations.isDirectlyPresent(CustomAnnotation.class));
        assertFalse(annotations.isDirectlyPresent(MetaAnnotation.class));
    }

    /**
     * 测试获取注解
     */
    @Test
    void testGet() {
        MergedAnnotations annotations = MergedAnnotations.from(AnnotatedClass.class);

        Optional<MergedAnnotation<CustomAnnotation>> optional = annotations.get(CustomAnnotation.class);
        assertTrue(optional.isPresent());

        MergedAnnotation<CustomAnnotation> mergedAnnotation = optional.get();
        assertEquals(CustomAnnotation.class, mergedAnnotation.getType());
        assertEquals("test", mergedAnnotation.getString("name"));
    }

    /**
     * 测试获取不存在的注解
     */
    @Test
    void testGetNotFound() {
        MergedAnnotations annotations = MergedAnnotations.from(AnnotatedClass.class);

        Optional<MergedAnnotation<Service>> optional = annotations.get(Service.class);
        assertFalse(optional.isPresent());
    }

    /**
     * 测试获取必需的注解
     */
    @Test
    void testGetRequired() {
        MergedAnnotations annotations = MergedAnnotations.from(AnnotatedClass.class);

        MergedAnnotation<CustomAnnotation> mergedAnnotation = annotations.getRequired(CustomAnnotation.class);
        assertNotNull(mergedAnnotation);
        assertEquals("test", mergedAnnotation.getString("name"));
    }

    /**
     * 测试获取不存在的必需注解抛出异常
     */
    @Test
    void testGetRequiredNotFound() {
        MergedAnnotations annotations = MergedAnnotations.from(AnnotatedClass.class);

        assertThrows(IllegalStateException.class, () -> {
            annotations.getRequired(Service.class);
        });
    }

    /**
     * 测试获取指定距离的注解
     */
    @Test
    void testGetWithDistance() {
        MergedAnnotations annotations = MergedAnnotations.from(
            AnnotatedClass.class,
            MergedAnnotations.SearchStrategy.INHERITED_ANNOTATIONS
        );

        // 距离 0 表示直接声明
        Optional<MergedAnnotation<CustomAnnotation>> direct = annotations.get(CustomAnnotation.class, 0);
        assertTrue(direct.isPresent());
        assertEquals(0, direct.get().getDistance());

        // 距离 1 表示元注解
        Optional<MergedAnnotation<MetaAnnotation>> meta = annotations.get(MetaAnnotation.class, 1);
        assertTrue(meta.isPresent());
        assertEquals(1, meta.get().getDistance());
    }

    /**
     * 测试获取所有指定类型的注解
     */
    @Test
    void testGetAll() {
        MergedAnnotations annotations = MergedAnnotations.from(AnnotatedClass.class);

        List<MergedAnnotation<CustomAnnotation>> list = annotations.getAll(CustomAnnotation.class);
        assertNotNull(list);
        assertEquals(1, list.size());
    }

    /**
     * 测试获取大小
     */
    @Test
    void testSize() {
        MergedAnnotations annotations = MergedAnnotations.from(AnnotatedClass.class);
        assertTrue(annotations.size() > 0);
    }

    /**
     * 测试判断是否为空
     */
    @Test
    void testIsEmpty() {
        MergedAnnotations annotations = MergedAnnotations.from(NonAnnotatedClass.class);
        assertTrue(annotations.isEmpty());

        MergedAnnotations nonEmpty = MergedAnnotations.from(AnnotatedClass.class);
        assertFalse(nonEmpty.isEmpty());
    }

    /**
     * 测试转换为流
     */
    @Test
    void testStream() {
        MergedAnnotations annotations = MergedAnnotations.from(AnnotatedClass.class);

        long count = annotations.stream().count();
        assertTrue(count > 0);
    }

    /**
     * 测试带条件的流
     */
    @Test
    void testStreamWithPredicate() {
        MergedAnnotations annotations = MergedAnnotations.from(
            AnnotatedClass.class,
            MergedAnnotations.SearchStrategy.INHERITED_ANNOTATIONS
        );

        long count = annotations.stream(
            a -> a.getType() == CustomAnnotation.class || a.getType() == MetaAnnotation.class
        ).count();

        assertTrue(count >= 2);
    }

    /**
     * 测试迭代器
     */
    @Test
    void testIterator() {
        MergedAnnotations annotations = MergedAnnotations.from(AnnotatedClass.class);

        int count = 0;
        for (MergedAnnotation<?> annotation : annotations) {
            count++;
        }

        assertTrue(count > 0);
    }

    /**
     * 测试 Service 注解包含 Component 元注解
     */
    @Test
    void testServiceMetaAnnotation() {
        MergedAnnotations annotations = MergedAnnotations.from(
            ServiceClass.class,
            MergedAnnotations.SearchStrategy.INHERITED_ANNOTATIONS
        );

        assertTrue(annotations.isPresent(Service.class));
        assertTrue(annotations.isPresent(Component.class));

        Optional<MergedAnnotation<Service>> serviceOpt = annotations.get(Service.class);
        assertTrue(serviceOpt.isPresent());
        assertEquals("myService", serviceOpt.get().getString("value"));
    }

    /**
     * 测试父类注解继承
     */
    @Test
    void testSuperclassInheritance() {
        MergedAnnotations annotations = MergedAnnotations.from(
            ChildClass.class,
            MergedAnnotations.SearchStrategy.SUPERCLASS
        );

        assertTrue(annotations.isPresent(CustomAnnotation.class));

        Optional<MergedAnnotation<CustomAnnotation>> optional = annotations.get(CustomAnnotation.class);
        assertTrue(optional.isPresent());
        assertEquals("parent", optional.get().getString("name"));
    }

    /**
     * 测试 MergedAnnotation 的各种属性获取方法
     */
    @Test
    void testMergedAnnotationAttributes() {
        MergedAnnotations annotations = MergedAnnotations.from(AnnotatedClass.class);

        MergedAnnotation<CustomAnnotation> merged = annotations.getRequired(CustomAnnotation.class);

        // 测试类型
        assertEquals(CustomAnnotation.class, merged.getType());

        // 测试存在性
        assertTrue(merged.isPresent());

        // 测试获取注解实例
        Optional<CustomAnnotation> annotationOpt = merged.getAnnotation();
        assertTrue(annotationOpt.isPresent());

        // 测试获取必需注解
        CustomAnnotation annotation = merged.getRequiredAnnotation();
        assertNotNull(annotation);

        // 测试获取属性值
        assertEquals("test", merged.getValue("name"));
        assertEquals("test", merged.getString("name"));

        // 测试获取不存在的属性
        assertNull(merged.getValue("nonexistent"));

        // 测试获取带默认值的属性
        assertEquals("test", merged.getValue("name", "default"));
        assertEquals("default", merged.getValue("nonexistent", "default"));

        // 测试属性存在性
        assertTrue(merged.hasAttribute("name"));
        assertFalse(merged.hasAttribute("nonexistent"));

        // 测试距离
        assertEquals(0, merged.getDistance());

        // 测试来源
        assertNotNull(merged.getSource());
    }

    /**
     * 测试 MergedAnnotation 的布尔属性获取
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface BooleanAnnotation {
        boolean active() default false;
        boolean enabled() default true;
    }

    @BooleanAnnotation(active = true, enabled = false)
    static class BooleanAnnotatedClass {
    }

    @Test
    void testMergedAnnotationBooleanAttributes() {
        MergedAnnotations annotations = MergedAnnotations.from(BooleanAnnotatedClass.class);
        MergedAnnotation<BooleanAnnotation> merged = annotations.getRequired(BooleanAnnotation.class);

        assertTrue(merged.getBoolean("active"));
        assertFalse(merged.getBoolean("enabled"));

        // 测试带默认值的获取
        assertTrue(merged.getBoolean("active", false));
        assertFalse(merged.getBoolean("enabled", true));
        assertTrue(merged.getBoolean("nonexistent", true));
    }

    /**
     * 测试 MergedAnnotation 的整数属性获取
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface IntAnnotation {
        int count() default 0;
        int size() default 100;
    }

    @IntAnnotation(count = 42, size = 200)
    static class IntAnnotatedClass {
    }

    @Test
    void testMergedAnnotationIntAttributes() {
        MergedAnnotations annotations = MergedAnnotations.from(IntAnnotatedClass.class);
        MergedAnnotation<IntAnnotation> merged = annotations.getRequired(IntAnnotation.class);

        assertEquals(42, merged.getInt("count"));
        assertEquals(200, merged.getInt("size"));

        // 测试带默认值的获取
        assertEquals(42, merged.getInt("count", 0));
        assertEquals(0, merged.getInt("nonexistent", 0));
    }

    /**
     * 测试 MergedAnnotation 的长整数属性获取
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface LongAnnotation {
        long timestamp() default 0L;
    }

    @LongAnnotation(timestamp = 1234567890L)
    static class LongAnnotatedClass {
    }

    @Test
    void testMergedAnnotationLongAttributes() {
        MergedAnnotations annotations = MergedAnnotations.from(LongAnnotatedClass.class);
        MergedAnnotation<LongAnnotation> merged = annotations.getRequired(LongAnnotation.class);

        assertEquals(1234567890L, merged.getLong("timestamp"));
        assertEquals(100L, merged.getLong("nonexistent", 100L));
    }

    /**
     * 测试 MergedAnnotation 的类属性获取
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface ClassAnnotation {
        Class<?> clazz() default Object.class;
    }

    @ClassAnnotation(clazz = String.class)
    static class ClassAnnotatedClass {
    }

    @Test
    void testMergedAnnotationClassAttributes() {
        MergedAnnotations annotations = MergedAnnotations.from(ClassAnnotatedClass.class);
        MergedAnnotation<ClassAnnotation> merged = annotations.getRequired(ClassAnnotation.class);

        assertEquals(String.class, merged.getClass("clazz"));
        assertEquals(String.class, merged.getClassAttribute("clazz", String.class));
    }

    /**
     * 测试 MergedAnnotation 的数组属性获取
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface ArrayAnnotation {
        String[] names() default {};
    }

    @ArrayAnnotation(names = {"a", "b", "c"})
    static class ArrayAnnotatedClass {
    }

    @Test
    void testMergedAnnotationArrayAttributes() {
        MergedAnnotations annotations = MergedAnnotations.from(ArrayAnnotatedClass.class);
        MergedAnnotation<ArrayAnnotation> merged = annotations.getRequired(ArrayAnnotation.class);

        String[] names = merged.getArrayAttribute("names");
        assertNotNull(names);
        assertEquals(3, names.length);
        assertEquals("a", names[0]);
    }

    /**
     * 测试 MergedAnnotation 获取所有属性
     */
    @Test
    void testMergedAnnotationGetAttributes() {
        MergedAnnotations annotations = MergedAnnotations.from(AnnotatedClass.class);
        MergedAnnotation<CustomAnnotation> merged = annotations.getRequired(CustomAnnotation.class);

        var attrs = merged.getAttributes();
        assertNotNull(attrs);
        assertEquals("test", attrs.getString("name"));
    }

    /**
     * 测试 MergedAnnotation 的 toString
     */
    @Test
    void testMergedAnnotationToString() {
        MergedAnnotations annotations = MergedAnnotations.from(AnnotatedClass.class);
        MergedAnnotation<CustomAnnotation> merged = annotations.getRequired(CustomAnnotation.class);

        String str = merged.toString();
        assertNotNull(str);
        assertTrue(str.contains("CustomAnnotation"));
    }
}
