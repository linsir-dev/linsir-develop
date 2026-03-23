package com.linsir.spring.framework.spring_core.annotation.attribute;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 注解属性映射
 *
 * 用于存储和传递注解的属性值，提供类型安全的属性访问方法。
 * 继承自 LinkedHashMap，保持属性插入顺序。
 *
 * @author linsir
 * @since 1.0.0
 */
public class AnnotationAttributes extends LinkedHashMap<String, Object> {

    /**
     * 创建空的注解属性映射
     */
    public AnnotationAttributes() {
        super();
    }

    /**
     * 创建具有指定初始容量的注解属性映射
     *
     * @param initialCapacity 初始容量
     */
    public AnnotationAttributes(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * 从现有 AnnotationAttributes 创建拷贝
     *
     * @param attributes 源属性映射
     */
    public AnnotationAttributes(AnnotationAttributes attributes) {
        super(attributes);
    }

    /**
     * 从普通 Map 创建注解属性映射
     *
     * @param map 源 Map
     * @return 注解属性映射
     */
    public static AnnotationAttributes fromMap(Map<String, Object> map) {
        if (map == null) {
            return new AnnotationAttributes();
        }
        AnnotationAttributes attributes = new AnnotationAttributes(map.size());
        attributes.putAll(map);
        return attributes;
    }

    /**
     * 获取字符串类型的属性值
     *
     * @param attributeName 属性名
     * @return 字符串值，如果不存在则返回 null
     */
    public String getString(String attributeName) {
        Object value = get(attributeName);
        return value != null ? value.toString() : null;
    }

    /**
     * 获取字符串类型的属性值（带默认值）
     *
     * @param attributeName 属性名
     * @param defaultValue 默认值
     * @return 字符串值
     */
    public String getString(String attributeName, String defaultValue) {
        String value = getString(attributeName);
        return value != null ? value : defaultValue;
    }

    /**
     * 获取布尔类型的属性值
     *
     * @param attributeName 属性名
     * @return 布尔值，如果不存在则返回 false
     */
    public boolean getBoolean(String attributeName) {
        Object value = get(attributeName);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return false;
    }

    /**
     * 获取布尔类型的属性值（带默认值）
     *
     * @param attributeName 属性名
     * @param defaultValue 默认值
     * @return 布尔值
     */
    public boolean getBoolean(String attributeName, boolean defaultValue) {
        Object value = get(attributeName);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return defaultValue;
    }

    /**
     * 获取整数类型的属性值
     *
     * @param attributeName 属性名
     * @return 整数值，如果不存在则返回 0
     */
    public int getInt(String attributeName) {
        Object value = get(attributeName);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return 0;
    }

    /**
     * 获取整数类型的属性值（带默认值）
     *
     * @param attributeName 属性名
     * @param defaultValue 默认值
     * @return 整数值
     */
    public int getInt(String attributeName, int defaultValue) {
        Object value = get(attributeName);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return defaultValue;
    }

    /**
     * 获取长整数类型的属性值
     *
     * @param attributeName 属性名
     * @return 长整数值，如果不存在则返回 0
     */
    public long getLong(String attributeName) {
        Object value = get(attributeName);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return 0L;
    }

    /**
     * 获取长整数类型的属性值（带默认值）
     *
     * @param attributeName 属性名
     * @param defaultValue 默认值
     * @return 长整数值
     */
    public long getLong(String attributeName, long defaultValue) {
        Object value = get(attributeName);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return defaultValue;
    }

    /**
     * 获取指定类型的属性值
     *
     * @param attributeName 属性名
     * @param type 期望的类型
     * @param <T> 类型参数
     * @return 属性值
     */
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String attributeName, Class<T> type) {
        Object value = get(attributeName);
        if (value == null) {
            return null;
        }
        if (type.isInstance(value)) {
            return (T) value;
        }
        return null;
    }

    /**
     * 获取指定类型的属性值（带默认值）
     *
     * @param attributeName 属性名
     * @param type 期望的类型
     * @param defaultValue 默认值
     * @param <T> 类型参数
     * @return 属性值
     */
    public <T> T getAttribute(String attributeName, Class<T> type, T defaultValue) {
        T value = getAttribute(attributeName, type);
        return value != null ? value : defaultValue;
    }

    /**
     * 获取数组类型的属性值
     *
     * @param attributeName 属性名
     * @param <T> 数组元素类型
     * @return 数组
     */
    @SuppressWarnings("unchecked")
    public <T> T[] getArrayAttribute(String attributeName) {
        Object value = get(attributeName);
        if (value == null) {
            return null;
        }
        if (value.getClass().isArray()) {
            return (T[]) value;
        }
        return null;
    }

    /**
     * 获取类类型的属性值
     *
     * @param attributeName 属性名
     * @return 类对象
     */
    @SuppressWarnings("unchecked")
    public Class<?> getClass(String attributeName) {
        Object value = get(attributeName);
        if (value instanceof Class) {
            return (Class<?>) value;
        }
        return null;
    }

    /**
     * 获取类类型的属性值（带类型参数）
     *
     * @param attributeName 属性名
     * @param type 期望的类类型
     * @param <T> 类型参数
     * @return 类对象
     */
    @SuppressWarnings("unchecked")
    public <T> Class<T> getClassAttribute(String attributeName, Class<T> type) {
        Class<?> clazz = getClass(attributeName);
        if (clazz != null && type.isAssignableFrom(clazz)) {
            return (Class<T>) clazz;
        }
        return null;
    }

    /**
     * 获取注解类型的属性值
     *
     * @param attributeName 属性名
     * @param annotationType 注解类型
     * @param <A> 注解类型参数
     * @return 注解实例
     */
    @SuppressWarnings("unchecked")
    public <A extends java.lang.annotation.Annotation> A getAnnotation(String attributeName, Class<A> annotationType) {
        Object value = get(attributeName);
        if (value != null && annotationType.isInstance(value)) {
            return (A) value;
        }
        return null;
    }

    /**
     * 判断属性是否存在
     *
     * @param attributeName 属性名
     * @return true 如果存在
     */
    public boolean hasAttribute(String attributeName) {
        return containsKey(attributeName);
    }

    /**
     * 判断属性是否为空（null 或空字符串）
     *
     * @param attributeName 属性名
     * @return true 如果为空
     */
    public boolean isEmpty(String attributeName) {
        Object value = get(attributeName);
        if (value == null) {
            return true;
        }
        if (value instanceof String) {
            return ((String) value).isEmpty();
        }
        return false;
    }

    /**
     * 添加属性值（如果属性不存在）
     *
     * @param attributeName 属性名
     * @param value 属性值
     * @return 如果添加成功则返回 true
     */
    public boolean putIfAbsentAttribute(String attributeName, Object value) {
        if (!containsKey(attributeName)) {
            put(attributeName, value);
            return true;
        }
        return false;
    }

    /**
     * 合并另一个 AnnotationAttributes
     *
     * @param other 另一个属性映射
     * @return this
     */
    public AnnotationAttributes merge(AnnotationAttributes other) {
        if (other != null) {
            for (Map.Entry<String, Object> entry : other.entrySet()) {
                putIfAbsentAttribute(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : entrySet()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(entry.getKey()).append("=");
            Object value = entry.getValue();
            if (value instanceof String) {
                sb.append("\"").append(value).append("\"");
            } else if (value != null && value.getClass().isArray()) {
                sb.append(arrayToString(value));
            } else {
                sb.append(value);
            }
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * 将数组转换为字符串
     */
    private String arrayToString(Object array) {
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
