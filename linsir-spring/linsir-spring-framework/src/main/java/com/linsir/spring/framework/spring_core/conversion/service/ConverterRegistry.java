package com.linsir.spring.framework.spring_core.conversion.service;

import com.linsir.spring.framework.spring_core.conversion.converter.Converter;
import com.linsir.spring.framework.spring_core.conversion.factory.ConverterFactory;
import com.linsir.spring.framework.spring_core.conversion.generic.GenericConverter;

/**
 * 转换器注册中心接口
 * 用于注册和管理各种转换器
 *
 * <p>提供以下注册能力：</p>
 * <ul>
 *   <li>注册 Converter 转换器</li>
 *   <li>注册 ConverterFactory 转换器工厂</li>
 *   <li>注册 GenericConverter 通用转换器</li>
 *   <li>移除已注册的转换器</li>
 * </ul>
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-23
 */
public interface ConverterRegistry {

    /**
     * 注册 Converter 转换器
     *
     * @param converter 转换器实例
     * @param <S> 源类型
     * @param <T> 目标类型
     */
    <S, T> void addConverter(Converter<S, T> converter);

    /**
     * 注册 Converter 转换器（指定类型）
     *
     * @param sourceType 源类型
     * @param targetType 目标类型
     * @param converter 转换器实例
     * @param <S> 源类型
     * @param <T> 目标类型
     */
    <S, T> void addConverter(Class<S> sourceType, Class<T> targetType, Converter<S, T> converter);

    /**
     * 注册 ConverterFactory 转换器工厂
     *
     * @param factory 转换器工厂实例
     * @param <S> 源类型
     * @param <R> 目标类型的基类
     */
    <S, R> void addConverterFactory(ConverterFactory<S, R> factory);

    /**
     * 注册 GenericConverter 通用转换器
     *
     * @param converter 通用转换器实例
     */
    void addConverter(GenericConverter converter);

    /**
     * 移除指定源类型和目标类型的转换器
     *
     * @param sourceType 源类型
     * @param targetType 目标类型
     */
    void removeConvertible(Class<?> sourceType, Class<?> targetType);
}
