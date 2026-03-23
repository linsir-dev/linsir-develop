package com.linsir.spring.framework.spring_core.annotation.core;

import com.linsir.spring.framework.spring_core.annotation.attribute.AnnotationAttributes;
import com.linsir.spring.framework.spring_core.annotation.utils.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * MergedAnnotation 的默认实现
 *
 * @param <A> 注解类型
 * @author linsir
 * @since 1.0.0
 */
class MergedAnnotationImpl<A extends Annotation> implements MergedAnnotation<A> {

    private final A annotation;
    private final int distance;
    private final Object source;
    private final AnnotationAttributes attributes;

    /**
     * 创建 MergedAnnotation 实例
     *
     * @param annotation 注解实例
     * @param distance 距离
     * @param source 来源
     */
    MergedAnnotationImpl(A annotation, int distance, Object source) {
        this.annotation = annotation;
        this.distance = distance;
        this.source = source;
        this.attributes = AnnotationUtils.getAnnotationAttributes(annotation);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<A> getType() {
        if (annotation == null) {
            return null;
        }
        return (Class<A>) annotation.annotationType();
    }

    @Override
    public boolean isPresent() {
        return annotation != null;
    }

    @Override
    public Optional<A> getAnnotation() {
        return Optional.ofNullable(annotation);
    }

    @Override
    public A getRequiredAnnotation() {
        if (annotation == null) {
            throw new IllegalStateException("Annotation is not present");
        }
        return annotation;
    }

    @Override
    public Object getValue(String attributeName) {
        return attributes.get(attributeName);
    }

    @Override
    public Object getValue(String attributeName, Object defaultValue) {
        Object value = attributes.get(attributeName);
        return value != null ? value : defaultValue;
    }

    @Override
    public String getString(String attributeName) {
        return attributes.getString(attributeName);
    }

    @Override
    public String getString(String attributeName, String defaultValue) {
        return attributes.getString(attributeName, defaultValue);
    }

    @Override
    public boolean getBoolean(String attributeName) {
        return attributes.getBoolean(attributeName);
    }

    @Override
    public boolean getBoolean(String attributeName, boolean defaultValue) {
        return attributes.getBoolean(attributeName, defaultValue);
    }

    @Override
    public int getInt(String attributeName) {
        return attributes.getInt(attributeName);
    }

    @Override
    public int getInt(String attributeName, int defaultValue) {
        return attributes.getInt(attributeName, defaultValue);
    }

    @Override
    public long getLong(String attributeName) {
        return attributes.getLong(attributeName);
    }

    @Override
    public long getLong(String attributeName, long defaultValue) {
        return attributes.getLong(attributeName, defaultValue);
    }

    @Override
    public <T> T getAttribute(String attributeName, Class<T> type) {
        return attributes.getAttribute(attributeName, type);
    }

    @Override
    public <T> T getAttribute(String attributeName, Class<T> type, T defaultValue) {
        return attributes.getAttribute(attributeName, type, defaultValue);
    }

    @Override
    public Class<?> getClass(String attributeName) {
        return attributes.getClass(attributeName);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Class<T> getClassAttribute(String attributeName, Class<T> type) {
        return attributes.getClassAttribute(attributeName, type);
    }

    @Override
    public <T> T[] getArrayAttribute(String attributeName) {
        return attributes.getArrayAttribute(attributeName);
    }

    @Override
    public AnnotationAttributes getAttributes() {
        return new AnnotationAttributes(attributes);
    }

    @Override
    public boolean hasAttribute(String attributeName) {
        return attributes.containsKey(attributeName);
    }

    @Override
    public boolean isDefaultValue(String attributeName) {
        if (annotation == null) {
            return true;
        }

        Object currentValue = getValue(attributeName);
        Object defaultValue = AnnotationUtils.getDefaultValue(getType(), attributeName);

        if (currentValue == null && defaultValue == null) {
            return true;
        }
        if (currentValue == null || defaultValue == null) {
            return false;
        }
        return currentValue.equals(defaultValue);
    }

    @Override
    public int getDistance() {
        return distance;
    }

    @Override
    public Object getSource() {
        return source;
    }

    @Override
    public String toString() {
        return "MergedAnnotation{" +
            "type=" + (annotation != null ? annotation.annotationType().getName() : "null") +
            ", distance=" + distance +
            ", attributes=" + attributes +
            '}';
    }
}
