package com.linsir.spring.framework.spring_core.conversion.factory;

import com.linsir.spring.framework.spring_core.conversion.converter.Converter;

/**
 * 转换器工厂接口
 * 用于创建针对特定目标类型的转换器
 *
 * <p>当需要将一种类型转换为多种相关类型时（如 String 转各种 Number 子类），
 * 使用 ConverterFactory 可以避免创建多个类似的转换器。</p>
 *
 * @param <S> 源类型
 * @param <R> 目标类型的基类
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-23
 */
public interface ConverterFactory<S, R> {

    /**
     * 获取指定目标类型的转换器
     *
     * @param targetType 目标类型
     * @param <T> 目标类型泛型参数
     * @return 转换器实例
     */
    <T extends R> Converter<S, T> getConverter(Class<T> targetType);
}
