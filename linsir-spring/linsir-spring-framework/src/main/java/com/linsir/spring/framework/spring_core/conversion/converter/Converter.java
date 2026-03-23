package com.linsir.spring.framework.spring_core.conversion.converter;

/**
 * 单向转换器接口
 * 用于将源类型转换为目标类型
 *
 * <p>这是一个函数式接口，可以使用 Lambda 表达式实现。</p>
 *
 * @param <S> 源类型
 * @param <T> 目标类型
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-23
 */
@FunctionalInterface
public interface Converter<S, T> {

    /**
     * 执行类型转换
     *
     * @param source 源对象，可能为 null
     * @return 转换后的目标类型对象，可能为 null
     */
    T convert(S source);
}
