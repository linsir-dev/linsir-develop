package com.linsir.spring.framework.spring_core.annotation.utils;

import com.linsir.spring.framework.spring_core.annotation.attribute.AnnotationAttributes;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 注解工具类
 *
 * 提供获取和操作注解的通用方法，是注解处理的核心入口。
 * 支持直接注解获取和继承注解查找。
 *
 * @author linsir
 * @since 1.0.0
 */
public final class AnnotationUtils {

    /**
     * 缓存注解属性方法，避免重复反射
     */
    private static final Map<Class<? extends Annotation>, Method[]> annotationAttributeCache = new HashMap<>();

    private AnnotationUtils() {
        // 工具类，禁止实例化
    }

    /**
     * 获取元素上直接声明的指定类型的注解
     *
     * @param element 注解元素（类、方法、字段等）
     * @param annotationType 注解类型
     * @param <A> 注解类型
     * @return 注解实例，如果不存在则返回 null
     */
    public static <A extends Annotation> A getAnnotation(AnnotatedElement element, Class<A> annotationType) {
        if (element == null || annotationType == null) {
            return null;
        }
        return element.getDeclaredAnnotation(annotationType);
    }

    /**
     * 查找元素上的指定类型的注解（包括继承的注解）
     *
     * 对于类，会查找当前类和父类；对于方法，会查找当前方法和父类方法。
     *
     * @param element 注解元素
     * @param annotationType 注解类型
     * @param <A> 注解类型
     * @return 注解实例，如果不存在则返回 null
     */
    public static <A extends Annotation> A findAnnotation(AnnotatedElement element, Class<A> annotationType) {
        if (element == null || annotationType == null) {
            return null;
        }

        // 首先尝试直接获取
        A annotation = element.getDeclaredAnnotation(annotationType);
        if (annotation != null) {
            return annotation;
        }

        // 如果是类，查找父类
        if (element instanceof Class) {
            return findAnnotationFromClass((Class<?>) element, annotationType);
        }

        return null;
    }

    /**
     * 从类及其父类中查找注解
     *
     * @param clazz 要查找的类
     * @param annotationType 注解类型
     * @param <A> 注解类型
     * @return 注解实例
     */
    @SuppressWarnings("unchecked")
    private static <A extends Annotation> A findAnnotationFromClass(Class<?> clazz, Class<A> annotationType) {
        // 检查当前类
        A annotation = clazz.getDeclaredAnnotation(annotationType);
        if (annotation != null) {
            return annotation;
        }

        // 递归查找父类
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null && superclass != Object.class) {
            annotation = findAnnotationFromClass(superclass, annotationType);
            if (annotation != null) {
                return annotation;
            }
        }

        // 查找接口
        for (Class<?> iface : clazz.getInterfaces()) {
            annotation = findAnnotationFromClass(iface, annotationType);
            if (annotation != null) {
                return annotation;
            }
        }

        return null;
    }

    /**
     * 判断元素是否包含指定类型的注解
     *
     * @param element 注解元素
     * @param annotationType 注解类型
     * @return true 如果存在该注解
     */
    public static boolean hasAnnotation(AnnotatedElement element, Class<? extends Annotation> annotationType) {
        return getAnnotation(element, annotationType) != null;
    }

    /**
     * 获取注解的所有属性值
     *
     * @param annotation 注解实例
     * @return 包含所有属性值的 AnnotationAttributes
     */
    public static AnnotationAttributes getAnnotationAttributes(Annotation annotation) {
        if (annotation == null) {
            return new AnnotationAttributes();
        }

        AnnotationAttributes attributes = new AnnotationAttributes();
        Class<? extends Annotation> annotationType = annotation.annotationType();

        // 获取缓存的方法或反射获取
        Method[] methods = annotationAttributeCache.computeIfAbsent(
            annotationType,
            type -> type.getDeclaredMethods()
        );

        for (Method method : methods) {
            // 只处理无参数的方法（注解属性方法）
            if (method.getParameterCount() == 0) {
                try {
                    Object value = method.invoke(annotation);
                    attributes.put(method.getName(), value);
                } catch (Exception e) {
                    // 忽略无法访问的属性
                }
            }
        }

        return attributes;
    }

    /**
     * 获取注解的指定属性值
     *
     * @param annotation 注解实例
     * @param attributeName 属性名
     * @return 属性值，如果不存在则返回 null
     */
    public static Object getAnnotationAttribute(Annotation annotation, String attributeName) {
        if (annotation == null || attributeName == null) {
            return null;
        }

        try {
            Method method = annotation.annotationType().getDeclaredMethod(attributeName);
            return method.invoke(annotation);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取注解的指定属性值（带类型转换）
     *
     * @param annotation 注解实例
     * @param attributeName 属性名
     * @param type 期望的类型
     * @param <T> 类型参数
     * @return 属性值
     */
    @SuppressWarnings("unchecked")
    public static <T> T getAnnotationAttribute(Annotation annotation, String attributeName, Class<T> type) {
        Object value = getAnnotationAttribute(annotation, attributeName);
        if (value == null) {
            return null;
        }
        if (type.isInstance(value)) {
            return (T) value;
        }
        return null;
    }

    /**
     * 获取注解的默认值
     *
     * @param annotationType 注解类型
     * @param attributeName 属性名
     * @return 默认值
     */
    public static Object getDefaultValue(Class<? extends Annotation> annotationType, String attributeName) {
        if (annotationType == null || attributeName == null) {
            return null;
        }

        try {
            Method method = annotationType.getDeclaredMethod(attributeName);
            return method.getDefaultValue();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断注解是否包含指定元注解
     *
     * @param annotationType 要检查的注解类型
     * @param metaAnnotationType 元注解类型
     * @return true 如果包含该元注解
     */
    public static boolean isAnnotatedWith(Class<? extends Annotation> annotationType,
                                          Class<? extends Annotation> metaAnnotationType) {
        if (annotationType == null || metaAnnotationType == null) {
            return false;
        }
        return annotationType.isAnnotationPresent(metaAnnotationType);
    }

    /**
     * 获取元素上的所有注解
     *
     * @param element 注解元素
     * @return 注解数组
     */
    public static Annotation[] getAnnotations(AnnotatedElement element) {
        if (element == null) {
            return new Annotation[0];
        }
        return element.getDeclaredAnnotations();
    }

    /**
     * 获取元素上所有指定类型的注解（包括重复注解）
     *
     * @param element 注解元素
     * @param annotationType 注解类型
     * @param <A> 注解类型
     * @return 注解数组
     */
    @SuppressWarnings("unchecked")
    public static <A extends Annotation> A[] getRepeatableAnnotations(AnnotatedElement element, Class<A> annotationType) {
        if (element == null || annotationType == null) {
            return (A[]) java.lang.reflect.Array.newInstance(annotationType, 0);
        }

        A annotation = element.getDeclaredAnnotation(annotationType);
        if (annotation != null) {
            A[] array = (A[]) java.lang.reflect.Array.newInstance(annotationType, 1);
            array[0] = annotation;
            return array;
        }

        return (A[]) java.lang.reflect.Array.newInstance(annotationType, 0);
    }

    /**
     * 获取注解的字符串表示
     *
     * @param annotation 注解实例
     * @return 字符串表示
     */
    public static String toString(Annotation annotation) {
        if (annotation == null) {
            return "null";
        }

        AnnotationAttributes attributes = getAnnotationAttributes(annotation);
        StringBuilder sb = new StringBuilder();
        sb.append("@").append(annotation.annotationType().getName());

        if (!attributes.isEmpty()) {
            sb.append("(");
            boolean first = true;
            for (Map.Entry<String, Object> entry : attributes.entrySet()) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append(entry.getKey()).append("=");
                Object value = entry.getValue();
                if (value instanceof String) {
                    sb.append("\"").append(value).append("\"");
                } else if (value.getClass().isArray()) {
                    sb.append(arrayToString(value));
                } else {
                    sb.append(value);
                }
                first = false;
            }
            sb.append(")");
        }

        return sb.toString();
    }

    /**
     * 将数组转换为字符串
     */
    private static String arrayToString(Object array) {
        if (array == null) {
            return "null";
        }

        StringBuilder sb = new StringBuilder("[");
        int length = java.lang.reflect.Array.getLength(array);
        for (int i = 0; i < length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            Object element = java.lang.reflect.Array.get(array, i);
            if (element instanceof String) {
                sb.append("\"").append(element).append("\"");
            } else {
                sb.append(element);
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
