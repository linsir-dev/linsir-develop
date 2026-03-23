package com.linsir.spring.framework.spring_core.conversion.exception;

/**
 * 类型转换异常
 * 当类型转换失败时抛出
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-23
 */
public class ConversionException extends RuntimeException {

    private final Class<?> sourceType;
    private final Class<?> targetType;
    private final Object sourceValue;

    /**
     * 构造转换异常
     *
     * @param message 异常消息
     */
    public ConversionException(String message) {
        super(message);
        this.sourceType = null;
        this.targetType = null;
        this.sourceValue = null;
    }

    /**
     * 构造转换异常
     *
     * @param message 异常消息
     * @param cause 原始异常
     */
    public ConversionException(String message, Throwable cause) {
        super(message, cause);
        this.sourceType = null;
        this.targetType = null;
        this.sourceValue = null;
    }

    /**
     * 构造转换异常
     *
     * @param sourceType 源类型
     * @param targetType 目标类型
     * @param sourceValue 源值
     * @param cause 原始异常
     */
    public ConversionException(Class<?> sourceType, Class<?> targetType, Object sourceValue, Throwable cause) {
        super(String.format("Failed to convert from type [%s] to type [%s] for value [%s]",
                sourceType != null ? sourceType.getName() : "null",
                targetType != null ? targetType.getName() : "null",
                sourceValue), cause);
        this.sourceType = sourceType;
        this.targetType = targetType;
        this.sourceValue = sourceValue;
    }

    public Class<?> getSourceType() {
        return sourceType;
    }

    public Class<?> getTargetType() {
        return targetType;
    }

    public Object getSourceValue() {
        return sourceValue;
    }
}
