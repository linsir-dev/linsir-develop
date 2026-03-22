package com.linsir.spring.framework.spring_core.type_system.conversion;

import java.util.Set;

/**
 * 通用转换器接口 - 支持复杂类型转换
 */
public interface GenericConverter {
    Set<ConvertiblePair> getConvertibleTypes();
    Object convert(Object source, Class<?> sourceType, Class<?> targetType);
}
