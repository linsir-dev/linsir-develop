package com.linsir.spring.framework.spring_core.conversion.support;

import com.linsir.spring.framework.spring_core.conversion.converter.Converter;
import com.linsir.spring.framework.spring_core.conversion.factory.ConverterFactory;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 字符串转数字转换器工厂
 * 用于创建各种数字类型的转换器
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-23
 */
public class StringToNumberConverterFactory implements ConverterFactory<String, Number> {

    @Override
    public <T extends Number> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToNumberConverter<>(targetType);
    }

    /**
     * 字符串转数字转换器
     *
     * @param <T> 目标数字类型
     */
    private static class StringToNumberConverter<T extends Number> implements Converter<String, T> {

        private final Class<T> targetType;

        public StringToNumberConverter(Class<T> targetType) {
            this.targetType = targetType;
        }

        @Override
        public T convert(String source) {
            if (source == null || source.isEmpty()) {
                return null;
            }

            String trimmed = source.trim();

            if (targetType == Integer.class || targetType == int.class) {
                return (T) Integer.valueOf(trimmed);
            } else if (targetType == Long.class || targetType == long.class) {
                return (T) Long.valueOf(trimmed);
            } else if (targetType == Double.class || targetType == double.class) {
                return (T) Double.valueOf(trimmed);
            } else if (targetType == Float.class || targetType == float.class) {
                return (T) Float.valueOf(trimmed);
            } else if (targetType == Short.class || targetType == short.class) {
                return (T) Short.valueOf(trimmed);
            } else if (targetType == Byte.class || targetType == byte.class) {
                return (T) Byte.valueOf(trimmed);
            } else if (targetType == BigDecimal.class) {
                return (T) new BigDecimal(trimmed);
            } else if (targetType == BigInteger.class) {
                return (T) new BigInteger(trimmed);
            }

            throw new IllegalArgumentException("Unsupported number type: " + targetType);
        }
    }
}
