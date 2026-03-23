package com.linsir.spring.framework.spring_core.annotation.core;

import com.linsir.spring.framework.spring_core.annotation.attribute.AnnotationAttributes;

import java.lang.annotation.Annotation;
import java.util.Optional;

/**
 * 合并注解接口
 *
 * 表示一个经过合并处理后的注解，可能包含来自多个源（如元注解）的属性值。
 * 支持属性别名解析和默认值处理。
 *
 * @param <A> 注解类型
 * @author linsir
 * @since 1.0.0
 */
public interface MergedAnnotation<A extends Annotation> {

    /**
     * 获取注解类型
     *
     * @return 注解类型 Class
     */
    Class<A> getType();

    /**
     * 判断注解是否存在于元素上
     *
     * @return true 如果存在
     */
    boolean isPresent();

    /**
     * 获取注解实例（如果可直接获取）
     *
     * @return 注解实例的 Optional
     */
    Optional<A> getAnnotation();

    /**
     * 获取注解实例，如果不存在则抛出异常
     *
     * @return 注解实例
     * @throws IllegalStateException 如果不存在
     */
    A getRequiredAnnotation();

    /**
     * 获取指定名称的属性值
     *
     * @param attributeName 属性名
     * @return 属性值
     */
    Object getValue(String attributeName);

    /**
     * 获取指定名称的属性值（带默认值）
     *
     * @param attributeName 属性名
     * @param defaultValue 默认值
     * @return 属性值
     */
    Object getValue(String attributeName, Object defaultValue);

    /**
     * 获取字符串类型的属性值
     *
     * @param attributeName 属性名
     * @return 字符串值
     */
    String getString(String attributeName);

    /**
     * 获取字符串类型的属性值（带默认值）
     *
     * @param attributeName 属性名
     * @param defaultValue 默认值
     * @return 字符串值
     */
    String getString(String attributeName, String defaultValue);

    /**
     * 获取布尔类型的属性值
     *
     * @param attributeName 属性名
     * @return 布尔值
     */
    boolean getBoolean(String attributeName);

    /**
     * 获取布尔类型的属性值（带默认值）
     *
     * @param attributeName 属性名
     * @param defaultValue 默认值
     * @return 布尔值
     */
    boolean getBoolean(String attributeName, boolean defaultValue);

    /**
     * 获取整数类型的属性值
     *
     * @param attributeName 属性名
     * @return 整数值
     */
    int getInt(String attributeName);

    /**
     * 获取整数类型的属性值（带默认值）
     *
     * @param attributeName 属性名
     * @param defaultValue 默认值
     * @return 整数值
     */
    int getInt(String attributeName, int defaultValue);

    /**
     * 获取长整数类型的属性值
     *
     * @param attributeName 属性名
     * @return 长整数值
     */
    long getLong(String attributeName);

    /**
     * 获取长整数类型的属性值（带默认值）
     *
     * @param attributeName 属性名
     * @param defaultValue 默认值
     * @return 长整数值
     */
    long getLong(String attributeName, long defaultValue);

    /**
     * 获取指定类型的属性值
     *
     * @param attributeName 属性名
     * @param type 期望的类型
     * @param <T> 类型参数
     * @return 属性值
     */
    <T> T getAttribute(String attributeName, Class<T> type);

    /**
     * 获取指定类型的属性值（带默认值）
     *
     * @param attributeName 属性名
     * @param type 期望的类型
     * @param defaultValue 默认值
     * @param <T> 类型参数
     * @return 属性值
     */
    <T> T getAttribute(String attributeName, Class<T> type, T defaultValue);

    /**
     * 获取类类型的属性值
     *
     * @param attributeName 属性名
     * @return 类对象
     */
    Class<?> getClass(String attributeName);

    /**
     * 获取类类型的属性值（带类型参数）
     *
     * @param attributeName 属性名
     * @param type 期望的类类型
     * @param <T> 类型参数
     * @return 类对象
     */
    <T> Class<T> getClassAttribute(String attributeName, Class<T> type);

    /**
     * 获取数组类型的属性值
     *
     * @param attributeName 属性名
     * @param <T> 数组元素类型
     * @return 数组
     */
    <T> T[] getArrayAttribute(String attributeName);

    /**
     * 获取所有属性值
     *
     * @return 注解属性映射
     */
    AnnotationAttributes getAttributes();

    /**
     * 判断属性是否存在
     *
     * @param attributeName 属性名
     * @return true 如果存在
     */
    boolean hasAttribute(String attributeName);

    /**
     * 判断属性是否使用默认值
     *
     * @param attributeName 属性名
     * @return true 如果使用默认值
     */
    boolean isDefaultValue(String attributeName);

    /**
     * 获取注解的来源距离
     * 距离 0 表示直接声明在元素上，距离 1 表示来自直接元注解，以此类推
     *
     * @return 距离值
     */
    int getDistance();

    /**
     * 获取注解的来源元素
     *
     * @return 来源元素
     */
    Object getSource();
}
