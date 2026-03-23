package com.linsir.spring.framework.spring_core.annotation.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * 合并注解集合接口
 *
 * 表示一个元素上的所有合并注解，提供遍历、筛选和获取注解的功能。
 *
 * @author linsir
 * @since 1.0.0
 */
public interface MergedAnnotations extends Iterable<MergedAnnotation<?>> {

    /**
     * 从注解元素创建 MergedAnnotations
     *
     * @param element 注解元素
     * @return MergedAnnotations 实例
     */
    static MergedAnnotations from(AnnotatedElement element) {
        return from(element, SearchStrategy.DIRECT);
    }

    /**
     * 从注解元素创建 MergedAnnotations，指定搜索策略
     *
     * @param element 注解元素
     * @param searchStrategy 搜索策略
     * @return MergedAnnotations 实例
     */
    static MergedAnnotations from(AnnotatedElement element, SearchStrategy searchStrategy) {
        return new MergedAnnotationsImpl(element, searchStrategy);
    }

    /**
     * 判断是否包含指定类型的注解
     *
     * @param annotationType 注解类型
     * @return true 如果包含
     */
    boolean isPresent(Class<? extends Annotation> annotationType);

    /**
     * 判断是否直接声明了指定类型的注解
     *
     * @param annotationType 注解类型
     * @return true 如果直接声明
     */
    boolean isDirectlyPresent(Class<? extends Annotation> annotationType);

    /**
     * 获取指定类型的合并注解
     *
     * @param annotationType 注解类型
     * @param <A> 注解类型参数
     * @return MergedAnnotation 的 Optional
     */
    <A extends Annotation> Optional<MergedAnnotation<A>> get(Class<A> annotationType);

    /**
     * 获取指定类型的合并注解，如果不存在则抛出异常
     *
     * @param annotationType 注解类型
     * @param <A> 注解类型参数
     * @return MergedAnnotation
     * @throws IllegalStateException 如果不存在
     */
    <A extends Annotation> MergedAnnotation<A> getRequired(Class<A> annotationType);

    /**
     * 获取指定类型的合并注解（指定距离）
     *
     * @param annotationType 注解类型
     * @param distance 距离（0 表示直接声明）
     * @param <A> 注解类型参数
     * @return MergedAnnotation 的 Optional
     */
    <A extends Annotation> Optional<MergedAnnotation<A>> get(Class<A> annotationType, int distance);

    /**
     * 获取所有指定类型的注解（包括重复注解）
     *
     * @param annotationType 注解类型
     * @param <A> 注解类型参数
     * @return MergedAnnotation 列表
     */
    <A extends Annotation> List<MergedAnnotation<A>> getAll(Class<A> annotationType);

    /**
     * 获取注解数量
     *
     * @return 注解数量
     */
    int size();

    /**
     * 判断是否为空
     *
     * @return true 如果没有注解
     */
    boolean isEmpty();

    /**
     * 转换为流
     *
     * @return MergedAnnotation 流
     */
    Stream<MergedAnnotation<?>> stream();

    /**
     * 根据条件筛选后转换为流
     *
     * @param predicate 筛选条件
     * @return MergedAnnotation 流
     */
    Stream<MergedAnnotation<?>> stream(Predicate<? super MergedAnnotation<?>> predicate);

    /**
     * 搜索策略枚举
     */
    enum SearchStrategy {
        /**
         * 只搜索直接声明的注解
         */
        DIRECT,

        /**
         * 搜索直接声明的注解和元注解
         */
        INHERITED_ANNOTATIONS,

        /**
         * 搜索超类和接口
         */
        SUPERCLASS,

        /**
         * 搜索类型层次结构（包括接口）
         */
        TYPE_HIERARCHY,

        /**
         * 搜索类型层次结构和封闭类
         */
        TYPE_HIERARCHY_AND_ENCLOSING_CLASSES
    }
}

/**
 * MergedAnnotations 的默认实现
 */
class MergedAnnotationsImpl implements MergedAnnotations {

    private final AnnotatedElement element;
    private final SearchStrategy searchStrategy;
    private final List<MergedAnnotation<?>> annotations;

    MergedAnnotationsImpl(AnnotatedElement element, SearchStrategy searchStrategy) {
        this.element = element;
        this.searchStrategy = searchStrategy;
        this.annotations = collectAnnotations();
    }

    /**
     * 收集所有注解
     */
    private List<MergedAnnotation<?>> collectAnnotations() {
        List<MergedAnnotation<?>> result = new ArrayList<>();

        if (element == null) {
            return result;
        }

        // 获取直接声明的注解
        Annotation[] directAnnotations = element.getDeclaredAnnotations();
        for (Annotation annotation : directAnnotations) {
            result.add(new MergedAnnotationImpl<>(annotation, 0, element));

            // 如果需要，递归收集元注解
            if (searchStrategy.ordinal() >= SearchStrategy.INHERITED_ANNOTATIONS.ordinal()) {
                collectMetaAnnotations(annotation, result, 1);
            }
        }

        // 如果需要搜索超类
        if (searchStrategy.ordinal() >= SearchStrategy.SUPERCLASS.ordinal() && element instanceof Class) {
            collectFromSuperclass((Class<?>) element, result);
        }

        return result;
    }

    /**
     * 收集元注解
     */
    private void collectMetaAnnotations(Annotation annotation, List<MergedAnnotation<?>> result, int distance) {
        Class<? extends Annotation> annotationType = annotation.annotationType();
        Annotation[] metaAnnotations = annotationType.getDeclaredAnnotations();

        for (Annotation metaAnnotation : metaAnnotations) {
            // 跳过 Java 内置注解
            if (isJavaMetaAnnotation(metaAnnotation.annotationType())) {
                continue;
            }

            result.add(new MergedAnnotationImpl<>(metaAnnotation, distance, annotationType));

            // 递归收集更深层的元注解
            if (distance < 3) { // 限制递归深度
                collectMetaAnnotations(metaAnnotation, result, distance + 1);
            }
        }
    }

    /**
     * 从超类收集注解
     */
    private void collectFromSuperclass(Class<?> clazz, List<MergedAnnotation<?>> result) {
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null && superclass != Object.class) {
            Annotation[] annotations = superclass.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                result.add(new MergedAnnotationImpl<>(annotation, 1, superclass));
            }
            collectFromSuperclass(superclass, result);
        }

        // 收集接口的注解
        for (Class<?> iface : clazz.getInterfaces()) {
            Annotation[] annotations = iface.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                result.add(new MergedAnnotationImpl<>(annotation, 1, iface));
            }
        }
    }

    /**
     * 判断是否为 Java 内置元注解
     */
    private boolean isJavaMetaAnnotation(Class<? extends Annotation> type) {
        return type.getName().startsWith("java.lang.annotation.");
    }

    @Override
    public boolean isPresent(Class<? extends Annotation> annotationType) {
        return get(annotationType).isPresent();
    }

    @Override
    public boolean isDirectlyPresent(Class<? extends Annotation> annotationType) {
        return get(annotationType, 0).isPresent();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A extends Annotation> Optional<MergedAnnotation<A>> get(Class<A> annotationType) {
        for (MergedAnnotation<?> annotation : annotations) {
            if (annotation.getType() == annotationType) {
                return Optional.of((MergedAnnotation<A>) annotation);
            }
        }
        return Optional.empty();
    }

    @Override
    public <A extends Annotation> MergedAnnotation<A> getRequired(Class<A> annotationType) {
        return get(annotationType)
            .orElseThrow(() -> new IllegalStateException(
                "Required annotation '" + annotationType.getName() + "' not found"
            ));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A extends Annotation> Optional<MergedAnnotation<A>> get(Class<A> annotationType, int distance) {
        for (MergedAnnotation<?> annotation : annotations) {
            if (annotation.getType() == annotationType && annotation.getDistance() == distance) {
                return Optional.of((MergedAnnotation<A>) annotation);
            }
        }
        return Optional.empty();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A extends Annotation> List<MergedAnnotation<A>> getAll(Class<A> annotationType) {
        List<MergedAnnotation<A>> result = new ArrayList<>();
        for (MergedAnnotation<?> annotation : annotations) {
            if (annotation.getType() == annotationType) {
                result.add((MergedAnnotation<A>) annotation);
            }
        }
        return result;
    }

    @Override
    public int size() {
        return annotations.size();
    }

    @Override
    public boolean isEmpty() {
        return annotations.isEmpty();
    }

    @Override
    public Stream<MergedAnnotation<?>> stream() {
        return annotations.stream();
    }

    @Override
    public Stream<MergedAnnotation<?>> stream(Predicate<? super MergedAnnotation<?>> predicate) {
        return annotations.stream().filter(predicate);
    }

    @Override
    public java.util.Iterator<MergedAnnotation<?>> iterator() {
        return annotations.iterator();
    }
}
