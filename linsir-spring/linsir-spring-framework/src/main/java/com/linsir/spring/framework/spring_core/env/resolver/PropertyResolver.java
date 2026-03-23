package com.linsir.spring.framework.spring_core.env.resolver;

/**
 * 属性解析器接口
 *
 * 提供属性解析的核心能力，包括获取属性值、解析占位符等。
 * 是 Environment 接口的父接口。
 *
 * @author linsir
 * @since 1.0.0
 */
public interface PropertyResolver {

    /**
     * 判断是否包含指定名称的属性
     *
     * @param key 属性名称
     * @return 如果包含该属性则返回 true
     */
    boolean containsProperty(String key);

    /**
     * 获取指定名称的属性值
     *
     * @param key 属性名称
     * @return 属性值，如果不存在则返回 null
     */
    String getProperty(String key);

    /**
     * 获取指定名称的属性值，如果不存在则返回默认值
     *
     * @param key 属性名称
     * @param defaultValue 默认值
     * @return 属性值，如果不存在则返回默认值
     */
    String getProperty(String key, String defaultValue);

    /**
     * 获取指定名称的属性值，并转换为指定类型
     *
     * @param key 属性名称
     * @param targetType 目标类型
     * @param <T> 目标类型
     * @return 转换后的属性值，如果不存在则返回 null
     */
    <T> T getProperty(String key, Class<T> targetType);

    /**
     * 获取指定名称的属性值，并转换为指定类型，如果不存在则返回默认值
     *
     * @param key 属性名称
     * @param targetType 目标类型
     * @param defaultValue 默认值
     * @param <T> 目标类型
     * @return 转换后的属性值，如果不存在则返回默认值
     */
    <T> T getProperty(String key, Class<T> targetType, T defaultValue);

    /**
     * 获取指定名称的必需属性值
     *
     * @param key 属性名称
     * @return 属性值
     * @throws IllegalStateException 如果属性不存在
     */
    String getRequiredProperty(String key) throws IllegalStateException;

    /**
     * 获取指定名称的必需属性值，并转换为指定类型
     *
     * @param key 属性名称
     * @param targetType 目标类型
     * @param <T> 目标类型
     * @return 转换后的属性值
     * @throws IllegalStateException 如果属性不存在
     */
    <T> T getRequiredProperty(String key, Class<T> targetType) throws IllegalStateException;

    /**
     * 解析字符串中的占位符
     *
     * @param text 包含占位符的文本
     * @return 解析后的文本
     */
    String resolvePlaceholders(String text);

    /**
     * 解析字符串中的占位符，如果存在无法解析的占位符则抛出异常
     *
     * @param text 包含占位符的文本
     * @return 解析后的文本
     * @throws IllegalArgumentException 如果存在无法解析的占位符
     */
    String resolveRequiredPlaceholders(String text) throws IllegalArgumentException;
}
