package com.linsir.spring.framework.spring_core.env.source;

import java.util.Objects;

/**
 * 属性源抽象基类
 *
 * 表示一个命名的属性源，可以从中获取属性值。
 * 这是所有具体属性源（如 MapPropertySource、SystemEnvironmentPropertySource 等）的基类。
 *
 * @param <T> 属性源的实际类型
 * @author linsir
 * @since 1.0.0
 */
public abstract class PropertySource<T> {

    /**
     * 属性源名称
     */
    protected final String name;

    /**
     * 实际的属性源对象
     */
    protected final T source;

    /**
     * 创建一个新的 PropertySource
     *
     * @param name 属性源名称，不能为 null
     * @param source 实际的属性源对象，不能为 null
     */
    public PropertySource(String name, T source) {
        if (name == null) {
            throw new IllegalArgumentException("Property source name must not be null");
        }
        if (source == null) {
            throw new IllegalArgumentException("Property source must not be null");
        }
        this.name = name;
        this.source = source;
    }

    /**
     * 获取属性源名称
     *
     * @return 属性源名称
     */
    public String getName() {
        return this.name;
    }

    /**
     * 获取实际的属性源对象
     *
     * @return 属性源对象
     */
    public T getSource() {
        return this.source;
    }

    /**
     * 判断是否包含指定名称的属性
     *
     * @param name 属性名称
     * @return 如果包含该属性则返回 true，否则返回 false
     */
    public abstract boolean containsProperty(String name);

    /**
     * 获取指定名称的属性值
     *
     * @param name 属性名称
     * @return 属性值，如果不存在则返回 null
     */
    public abstract Object getProperty(String name);

    /**
     * 获取指定名称的属性值，并转换为指定类型
     *
     * @param name 属性名称
     * @param targetType 目标类型
     * @param <E> 目标类型
     * @return 转换后的属性值，如果不存在则返回 null
     */
    @SuppressWarnings("unchecked")
    public <E> E getProperty(String name, Class<E> targetType) {
        Object value = getProperty(name);
        if (value == null) {
            return null;
        }
        if (targetType.isInstance(value)) {
            return (E) value;
        }
        throw new IllegalArgumentException(
            String.format("Cannot convert value '%s' from %s to %s",
                value, value.getClass().getName(), targetType.getName()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertySource<?> that = (PropertySource<?>) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        if (this.name == null) {
            return "<unknown>";
        }
        StringBuilder sb = new StringBuilder(this.name);
        if (this.source != null) {
            sb.append(" [").append(this.source.getClass().getSimpleName()).append("]");
        }
        return sb.toString();
    }
}
