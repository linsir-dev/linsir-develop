package com.linsir.spring.framework.spring_core.annotation.utils;

import com.linsir.spring.framework.spring_core.annotation.attribute.AnnotationAttributes;
import com.linsir.spring.framework.spring_core.annotation.meta.*;
import org.junit.jupiter.api.Test;

import java.lang.annotation.*;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AnnotatedElementUtils 测试类
 *
 * 测试注解元素工具类的各种功能。
 *
 * @author linsir
 * @since 1.0.0
 */
class AnnotatedElementUtilsTest {

    /**
     * 测试用的元注解
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.ANNOTATION_TYPE)
    public @interface BusinessService {
        String value() default "";
    }

    /**
     * 带元注解的自定义注解
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @BusinessService
    @Component
    public @interface OrderService {
        String name() default "";
    }

    /**
     * 带注解的测试类
     */
    @OrderService(name = "orderService")
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
    @Component("parent")
    static class ParentClass {
    }

    /**
     * 子类
     */
    static class ChildClass extends ParentClass {
    }

    /**
     * 测试判断是否包含注解（包括元注解）
     */
    @Test
    void testHasAnnotation() {
        assertTrue(AnnotatedElementUtils.hasAnnotation(AnnotatedClass.class, OrderService.class));
        assertTrue(AnnotatedElementUtils.hasAnnotation(AnnotatedClass.class, Component.class));
        assertTrue(AnnotatedElementUtils.hasAnnotation(AnnotatedClass.class, BusinessService.class));
        assertFalse(AnnotatedElementUtils.hasAnnotation(AnnotatedClass.class, Service.class));
    }

    /**
     * 测试判断 null 元素不包含注解
     */
    @Test
    void testHasAnnotationWithNull() {
        assertFalse(AnnotatedElementUtils.hasAnnotation(null, Component.class));
        assertFalse(AnnotatedElementUtils.hasAnnotation(AnnotatedClass.class, null));
    }

    /**
     * 测试判断是否直接声明注解
     */
    @Test
    void testHasDirectAnnotation() {
        assertTrue(AnnotatedElementUtils.hasDirectAnnotation(AnnotatedClass.class, OrderService.class));
        assertFalse(AnnotatedElementUtils.hasDirectAnnotation(AnnotatedClass.class, Component.class));
        assertFalse(AnnotatedElementUtils.hasDirectAnnotation(AnnotatedClass.class, BusinessService.class));
    }

    /**
     * 测试获取合并后的注解
     */
    @Test
    void testGetMergedAnnotation() {
        Optional<OrderService> optional = AnnotatedElementUtils.getMergedAnnotation(
            AnnotatedClass.class, OrderService.class);

        assertTrue(optional.isPresent());
        assertEquals("orderService", optional.get().name());
    }

    /**
     * 测试获取不存在的合并注解
     */
    @Test
    void testGetMergedAnnotationNotFound() {
        Optional<Service> optional = AnnotatedElementUtils.getMergedAnnotation(
            AnnotatedClass.class, Service.class);

        assertFalse(optional.isPresent());
    }

    /**
     * 测试获取 null 元素的合并注解
     */
    @Test
    void testGetMergedAnnotationWithNull() {
        Optional<Component> optional = AnnotatedElementUtils.getMergedAnnotation(null, Component.class);
        assertFalse(optional.isPresent());
    }

    /**
     * 测试获取合并后的注解属性
     */
    @Test
    void testGetMergedAnnotationAttributes() {
        Optional<AnnotationAttributes> optional = AnnotatedElementUtils.getMergedAnnotationAttributes(
            AnnotatedClass.class, OrderService.class);

        assertTrue(optional.isPresent());
        assertEquals("orderService", optional.get().getString("name"));
    }

    /**
     * 测试获取 Service 注解的属性
     */
    @Test
    void testGetServiceAnnotationAttributes() {
        Optional<AnnotationAttributes> optional = AnnotatedElementUtils.getMergedAnnotationAttributes(
            ServiceClass.class, Service.class);

        assertTrue(optional.isPresent());
        assertEquals("myService", optional.get().getString("value"));
    }

    /**
     * 测试获取所有指定类型的注解（包括重复注解）
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Repeatable(Schedules.class)
    public @interface Scheduled {
        String cron() default "";
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Schedules {
        Scheduled[] value();
    }

    @Scheduled(cron = "0 0 * * * *")
    @Scheduled(cron = "0 30 * * * *")
    static class ScheduledClass {
    }

    @Test
    void testGetMergedRepeatableAnnotations() {
        List<Scheduled> annotations = AnnotatedElementUtils.getMergedRepeatableAnnotations(
            ScheduledClass.class, Scheduled.class);

        assertNotNull(annotations);
        assertEquals(2, annotations.size());
        assertEquals("0 0 * * * *", annotations.get(0).cron());
        assertEquals("0 30 * * * *", annotations.get(1).cron());
    }

    /**
     * 测试获取所有注解属性
     */
    @Test
    void testGetAllAnnotationAttributes() {
        List<AnnotationAttributes> attributes = AnnotatedElementUtils.getAllAnnotationAttributes(
            AnnotatedClass.class);

        assertNotNull(attributes);
        assertFalse(attributes.isEmpty());
    }

    /**
     * 测试获取 null 元素的所有属性
     */
    @Test
    void testGetAllAnnotationAttributesWithNull() {
        List<AnnotationAttributes> attributes = AnnotatedElementUtils.getAllAnnotationAttributes(null);
        assertNotNull(attributes);
        assertTrue(attributes.isEmpty());
    }

    /**
     * 测试查找第一个注解（包括超类）
     */
    @Test
    void testFindFirstAnnotation() {
        Optional<Component> optional = AnnotatedElementUtils.findFirstAnnotation(
            ChildClass.class, Component.class);

        assertTrue(optional.isPresent());
        assertEquals("parent", optional.get().value());
    }

    /**
     * 测试查找第一个注解从当前类开始
     */
    @Test
    void testFindFirstAnnotationFromCurrent() {
        Optional<OrderService> optional = AnnotatedElementUtils.findFirstAnnotation(
            AnnotatedClass.class, OrderService.class);

        assertTrue(optional.isPresent());
        assertEquals("orderService", optional.get().name());
    }

    /**
     * 测试查找不存在的注解
     */
    @Test
    void testFindFirstAnnotationNotFound() {
        Optional<Service> optional = AnnotatedElementUtils.findFirstAnnotation(
            NonAnnotatedClass.class, Service.class);

        assertFalse(optional.isPresent());
    }

    /**
     * 测试查找所有注解（包括超类）
     */
    @Test
    void testFindAllAnnotations() {
        List<Component> annotations = AnnotatedElementUtils.findAllAnnotations(
            ChildClass.class, Component.class);

        assertNotNull(annotations);
        assertEquals(1, annotations.size());
        assertEquals("parent", annotations.get(0).value());
    }

    /**
     * 测试判断是否包含元注解
     */
    @Test
    void testHasMetaAnnotation() {
        assertTrue(AnnotatedElementUtils.hasMetaAnnotation(AnnotatedClass.class, BusinessService.class));
        assertTrue(AnnotatedElementUtils.hasMetaAnnotation(AnnotatedClass.class, Component.class));
        assertFalse(AnnotatedElementUtils.hasMetaAnnotation(AnnotatedClass.class, Service.class));
    }

    /**
     * 测试获取指定元注解标记的所有注解
     */
    @Test
    void testGetAnnotationsWithMetaAnnotation() {
        List<Class<? extends Annotation>> annotations = AnnotatedElementUtils.getAnnotationsWithMetaAnnotation(
            AnnotatedClass.class, Component.class);

        assertNotNull(annotations);
        assertTrue(annotations.contains(OrderService.class));
    }

    /**
     * 测试获取注解的元注解
     */
    @Test
    void testGetMetaAnnotations() {
        List<Annotation> metaAnnotations = AnnotatedElementUtils.getMetaAnnotations(OrderService.class);

        assertNotNull(metaAnnotations);

        boolean foundBusinessService = false;
        boolean foundComponent = false;

        for (Annotation annotation : metaAnnotations) {
            if (annotation instanceof BusinessService) {
                foundBusinessService = true;
            }
            if (annotation instanceof Component) {
                foundComponent = true;
            }
        }

        assertTrue(foundBusinessService);
        assertTrue(foundComponent);
    }

    /**
     * 测试获取 null 注解类型的元注解
     */
    @Test
    void testGetMetaAnnotationsWithNull() {
        List<Annotation> metaAnnotations = AnnotatedElementUtils.getMetaAnnotations(null);
        assertNotNull(metaAnnotations);
        assertTrue(metaAnnotations.isEmpty());
    }

    /**
     * 测试合并两个元素的注解属性
     */
    @Component("primary")
    static class PrimaryClass {
    }

    @Service("secondary")
    static class SecondaryClass {
    }

    @Test
    void testMergeAnnotationAttributes() {
        List<AnnotationAttributes> merged = AnnotatedElementUtils.mergeAnnotationAttributes(
            PrimaryClass.class, SecondaryClass.class);

        assertNotNull(merged);
        assertTrue(merged.size() >= 1);
    }

    /**
     * 测试注解相等性
     */
    @Test
    void testEquals() {
        Component component1 = PrimaryClass.class.getAnnotation(Component.class);
        Component component2 = PrimaryClass.class.getAnnotation(Component.class);
        Service service = SecondaryClass.class.getAnnotation(Service.class);

        assertTrue(AnnotatedElementUtils.equals(component1, component2));
        assertFalse(AnnotatedElementUtils.equals(component1, service));
        assertFalse(AnnotatedElementUtils.equals(component1, null));
        assertTrue(AnnotatedElementUtils.equals(null, null));
    }

    /**
     * 测试注解哈希码
     */
    @Test
    void testHashCode() {
        Component component = PrimaryClass.class.getAnnotation(Component.class);

        int hashCode = AnnotatedElementUtils.hashCode(component);
        assertTrue(hashCode != 0);

        int nullHashCode = AnnotatedElementUtils.hashCode(null);
        assertEquals(0, nullHashCode);
    }

    /**
     * 测试注解字符串表示
     */
    @Test
    void testToString() {
        Component component = PrimaryClass.class.getAnnotation(Component.class);

        String str = AnnotatedElementUtils.toString(component);
        assertNotNull(str);
        assertTrue(str.contains("@"));
        assertTrue(str.contains("Component"));
    }
}
