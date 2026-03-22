package com.linsir.spring.framework.spring_core.type_system.conversion;

/**
 * 转换服务接口
 */
public interface ConversionService {
    boolean canConvert(Class<?> sourceType, Class<?> targetType);
    <T> T convert(Object source, Class<T> targetType);
}
