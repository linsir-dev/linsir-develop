package com.linsir.spring.framework.spring_core.type_system.conversion;

/**
 * 转换器接口 - 简单的一对一转换
 */
@FunctionalInterface
public interface Converter<S, T> {
    T convert(S source);
}
