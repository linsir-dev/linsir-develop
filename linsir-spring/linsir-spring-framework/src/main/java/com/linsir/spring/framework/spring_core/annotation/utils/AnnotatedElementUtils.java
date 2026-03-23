package com.linsir.spring.framework.spring_core.annotation.utils;

import com.linsir.spring.framework.spring_core.annotation.attribute.AnnotationAttributes;
import com.linsir.spring.framework.spring_core.annotation.core.MergedAnnotation;
import com.linsir.spring.framework.spring_core.annotation.core.MergedAnnotations;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 注解元素工具类
 *
 * 提供处理注解元素的高级功能，支持组合注解、属性别名和重复注解。
 * 是 AnnotationUtils 的增强版本，提供更强大的注解处理能力。
 *
 * @author linsir
 * @since 1.0.0
 */
public final class AnnotatedElementUtils {

    private AnnotatedElementUtils() {
        // 工具类，禁止实例化
    }

    /**
     * 判断元素是否包含指定类型的注解（包括元注解）
     *
     * @param element 注解元素
     * @param annotationType 注解类型
     * @return true 如果包含该注解
     */
    public static boolean hasAnnotation(AnnotatedElement element, Class<? extends Annotation> annotationType) {
        if (element == null || annotationType == null) {
            return false;
        }
        return MergedAnnotations.from(element, MergedAnnotations.SearchStrategy.INHERITED_ANNOTATIONS)
            .isPresent(annotationType);
    }

    /**
     * 判断元素是否直接声明了指定类型的注解
     *
     * @param element 注解元素
     * @param annotationType 注解类型
     * @return true 如果直接声明
     */
    public static boolean hasDirectAnnotation(AnnotatedElement element, Class<? extends Annotation> annotationType) {
        if (element == null || annotationType == null) {
            return false;
        }
        return MergedAnnotations.from(element, MergedAnnotations.SearchStrategy.DIRECT)
            .isDirectlyPresent(annotationType);
    }

    /**
     * 获取合并后的注解（处理属性别名）
     *
     * @param element 注解元素
     * @param annotationType 注解类型
     * @param <A> 注解类型参数
     * @return 注解实例的 Optional
     */
    public static <A extends Annotation> Optional<A> getMergedAnnotation(AnnotatedElement element, Class<A> annotationType) {
        if (element == null || annotationType == null) {
            return Optional.empty();
        }

        return MergedAnnotations.from(element, MergedAnnotations.SearchStrategy.INHERITED_ANNOTATIONS)
            .get(annotationType)
            .flatMap(MergedAnnotation::getAnnotation);
    }

    /**
     * 获取合并后的注解属性
     *
     * @param element 注解元素
     * @param annotationType 注解类型
     * @param <A> 注解类型参数
     * @return 注解属性的 Optional
     */
    public static <A extends Annotation> Optional<AnnotationAttributes> getMergedAnnotationAttributes(
            AnnotatedElement element, Class<A> annotationType) {
        if (element == null || annotationType == null) {
            return Optional.empty();
        }

        return MergedAnnotations.from(element, MergedAnnotations.SearchStrategy.INHERITED_ANNOTATIONS)
            .get(annotationType)
            .map(MergedAnnotation::getAttributes);
    }

    /**
     * 获取所有指定类型的注解（包括重复注解）
     *
     * @param element 注解元素
     * @param annotationType 注解类型
     * @param <A> 注解类型参数
     * @return 注解列表
     */
    public static <A extends Annotation> List<A> getMergedRepeatableAnnotations(
            AnnotatedElement element, Class<A> annotationType) {
        if (element == null || annotationType == null) {
            return new ArrayList<>();
        }

        List<A> result = new ArrayList<>();

        // 首先检查是否有直接声明的注解
        A directAnnotation = element.getDeclaredAnnotation(annotationType);
        if (directAnnotation != null) {
            result.add(directAnnotation);
        }

        // 检查是否有重复注解容器
        Repeatable repeatable = annotationType.getAnnotation(Repeatable.class);
        if (repeatable != null) {
            Class<? extends Annotation> containerType = repeatable.value();
            Annotation container = element.getDeclaredAnnotation(containerType);
            if (container != null) {
                Annotation[] nestedAnnotations = getNestedAnnotations(container);
                for (Annotation nested : nestedAnnotations) {
                    if (annotationType.isInstance(nested)) {
                        result.add(annotationType.cast(nested));
                    }
                }
            }
        }

        return result;
    }

    /**
     * 获取嵌套在容器注解中的注解数组
     */
    private static Annotation[] getNestedAnnotations(Annotation container) {
        try {
            java.lang.reflect.Method valueMethod = container.annotationType().getMethod("value");
            Object value = valueMethod.invoke(container);
            if (value instanceof Annotation[]) {
                return (Annotation[]) value;
            }
        } catch (Exception e) {
            // 忽略异常
        }
        return new Annotation[0];
    }

    /**
     * 获取元素上所有注解的属性
     *
     * @param element 注解元素
     * @return 注解属性列表
     */
    public static List<AnnotationAttributes> getAllAnnotationAttributes(AnnotatedElement element) {
        if (element == null) {
            return new ArrayList<>();
        }

        return MergedAnnotations.from(element, MergedAnnotations.SearchStrategy.DIRECT)
            .stream()
            .map(MergedAnnotation::getAttributes)
            .collect(Collectors.toList());
    }

    /**
     * 获取指定类型的所有注解属性（包括重复注解）
     *
     * @param element 注解元素
     * @param annotationType 注解类型
     * @param <A> 注解类型参数
     * @return 注解属性列表
     */
    public static <A extends Annotation> List<AnnotationAttributes> getAllMergedAnnotationAttributes(
            AnnotatedElement element, Class<A> annotationType) {
        if (element == null || annotationType == null) {
            return new ArrayList<>();
        }

        return MergedAnnotations.from(element, MergedAnnotations.SearchStrategy.INHERITED_ANNOTATIONS)
            .getAll(annotationType)
            .stream()
            .map(MergedAnnotation::getAttributes)
            .collect(Collectors.toList());
    }

    /**
     * 查找第一个指定类型的注解（包括超类和接口）
     *
     * @param element 注解元素
     * @param annotationType 注解类型
     * @param <A> 注解类型参数
     * @return 注解实例的 Optional
     */
    public static <A extends Annotation> Optional<A> findFirstAnnotation(
            AnnotatedElement element, Class<A> annotationType) {
        if (element == null || annotationType == null) {
            return Optional.empty();
        }

        // 首先检查直接声明的注解
        A annotation = element.getDeclaredAnnotation(annotationType);
        if (annotation != null) {
            return Optional.of(annotation);
        }

        // 使用 MergedAnnotations 搜索
        return MergedAnnotations.from(element, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY)
            .get(annotationType)
            .flatMap(MergedAnnotation::getAnnotation);
    }

    /**
     * 查找所有指定类型的注解（包括超类和接口）
     *
     * @param element 注解元素
     * @param annotationType 注解类型
     * @param <A> 注解类型参数
     * @return 注解列表
     */
    public static <A extends Annotation> List<A> findAllAnnotations(
            AnnotatedElement element, Class<A> annotationType) {
        if (element == null || annotationType == null) {
            return new ArrayList<>();
        }

        return MergedAnnotations.from(element, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY)
            .getAll(annotationType)
            .stream()
            .map(MergedAnnotation::getAnnotation)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }

    /**
     * 判断注解是否包含指定元注解
     *
     * @param element 注解元素
     * @param metaAnnotationType 元注解类型
     * @return true 如果包含该元注解
     */
    public static boolean hasMetaAnnotation(AnnotatedElement element, Class<? extends Annotation> metaAnnotationType) {
        if (element == null || metaAnnotationType == null) {
            return false;
        }

        return MergedAnnotations.from(element, MergedAnnotations.SearchStrategy.INHERITED_ANNOTATIONS)
            .stream()
            .anyMatch(mergedAnnotation -> {
                Class<?> type = mergedAnnotation.getType();
                return type != null && type.isAnnotationPresent(metaAnnotationType);
            });
    }

    /**
     * 获取指定元注解标记的所有注解
     *
     * @param element 注解元素
     * @param metaAnnotationType 元注解类型
     * @return 注解类型列表
     */
    public static List<Class<? extends Annotation>> getAnnotationsWithMetaAnnotation(
            AnnotatedElement element, Class<? extends Annotation> metaAnnotationType) {
        if (element == null || metaAnnotationType == null) {
            return new ArrayList<>();
        }

        List<Class<? extends Annotation>> result = new ArrayList<>();
        Annotation[] annotations = element.getDeclaredAnnotations();

        for (Annotation annotation : annotations) {
            if (annotation.annotationType().isAnnotationPresent(metaAnnotationType)) {
                result.add(annotation.annotationType());
            }
        }

        return result;
    }

    /**
     * 获取注解的元注解
     *
     * @param annotationType 注解类型
     * @return 元注解列表
     */
    public static List<Annotation> getMetaAnnotations(Class<? extends Annotation> annotationType) {
        if (annotationType == null) {
            return new ArrayList<>();
        }

        List<Annotation> result = new ArrayList<>();
        Annotation[] metaAnnotations = annotationType.getDeclaredAnnotations();

        for (Annotation metaAnnotation : metaAnnotations) {
            // 跳过 Java 内置注解
            if (!isJavaMetaAnnotation(metaAnnotation.annotationType())) {
                result.add(metaAnnotation);
            }
        }

        return result;
    }

    /**
     * 判断是否为 Java 内置元注解
     */
    private static boolean isJavaMetaAnnotation(Class<? extends Annotation> type) {
        return type.getName().startsWith("java.lang.annotation.");
    }

    /**
     * 合并两个注解元素的注解
     *
     * @param primary 主元素（优先级高）
     * @param secondary 次元素
     * @return 合并后的注解属性列表
     */
    public static List<AnnotationAttributes> mergeAnnotationAttributes(
            AnnotatedElement primary, AnnotatedElement secondary) {
        List<AnnotationAttributes> result = new ArrayList<>();

        // 获取主元素的所有注解
        List<AnnotationAttributes> primaryAttributes = getAllAnnotationAttributes(primary);
        result.addAll(primaryAttributes);

        // 获取次元素的注解，只添加主元素中没有的
        List<AnnotationAttributes> secondaryAttributes = getAllAnnotationAttributes(secondary);
        for (AnnotationAttributes attrs : secondaryAttributes) {
            // 简单合并策略：如果主元素中没有相同类型的注解，则添加
            boolean exists = primaryAttributes.stream()
                .anyMatch(primaryAttr -> primaryAttr.keySet().equals(attrs.keySet()));
            if (!exists) {
                result.add(attrs);
            }
        }

        return result;
    }

    /**
     * 判断两个注解是否相等（比较类型和属性值）
     *
     * @param annotation1 第一个注解
     * @param annotation2 第二个注解
     * @return true 如果相等
     */
    public static boolean equals(Annotation annotation1, Annotation annotation2) {
        if (annotation1 == annotation2) {
            return true;
        }
        if (annotation1 == null || annotation2 == null) {
            return false;
        }
        if (annotation1.annotationType() != annotation2.annotationType()) {
            return false;
        }

        AnnotationAttributes attrs1 = AnnotationUtils.getAnnotationAttributes(annotation1);
        AnnotationAttributes attrs2 = AnnotationUtils.getAnnotationAttributes(annotation2);

        return attrs1.equals(attrs2);
    }

    /**
     * 获取注解的哈希码
     *
     * @param annotation 注解
     * @return 哈希码
     */
    public static int hashCode(Annotation annotation) {
        if (annotation == null) {
            return 0;
        }
        return AnnotationUtils.getAnnotationAttributes(annotation).hashCode();
    }

    /**
     * 获取注解的字符串表示
     *
     * @param annotation 注解
     * @return 字符串表示
     */
    public static String toString(Annotation annotation) {
        return AnnotationUtils.toString(annotation);
    }
}
