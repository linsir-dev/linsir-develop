package com.linsir.spring.framework.spring_core.type_system.conversion;

import java.util.Objects;

/**
 * 可转换类型对
 */
public class ConvertiblePair {
    private final Class<?> sourceType;
    private final Class<?> targetType;

    public ConvertiblePair(Class<?> sourceType, Class<?> targetType) {
        this.sourceType = sourceType;
        this.targetType = targetType;
    }

    public Class<?> getSourceType() { return sourceType; }
    public Class<?> getTargetType() { return targetType; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConvertiblePair that = (ConvertiblePair) o;
        return Objects.equals(sourceType, that.sourceType) &&
               Objects.equals(targetType, that.targetType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceType, targetType);
    }
}
