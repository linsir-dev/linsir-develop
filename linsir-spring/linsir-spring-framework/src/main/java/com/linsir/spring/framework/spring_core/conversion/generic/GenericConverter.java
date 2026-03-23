package com.linsir.spring.framework.spring_core.conversion.generic;

import com.linsir.spring.framework.spring_core.conversion.descriptor.TypeDescriptor;

import java.util.Set;

/**
 * 通用转换器接口
 * 支持复杂的类型转换场景，特别是需要访问泛型信息的情况
 *
 * <p>与 Converter 接口相比，GenericConverter 可以：</p>
 * <ul>
 *   <li>同时支持多种源类型到目标类型的转换</li>
 *   <li>访问源类型和目标类型的泛型信息</li>
 *   <li>处理集合、Map 等复杂类型的转换</li>
 * </ul>
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-23
 */
public interface GenericConverter {

    /**
     * 获取支持的转换类型对
     *
     * @return 可转换类型对的集合
     */
    Set<ConvertiblePair> getConvertibleTypes();

    /**
     * 执行类型转换
     *
     * @param source 源对象
     * @param sourceType 源类型描述符
     * @param targetType 目标类型描述符
     * @return 转换后的对象
     */
    Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType);

    /**
     * 可转换类型对
     * 用于描述一种源类型到目标类型的转换关系
     */
    final class ConvertiblePair {
        private final Class<?> sourceType;
        private final Class<?> targetType;

        /**
         * 构造可转换类型对
         *
         * @param sourceType 源类型
         * @param targetType 目标类型
         */
        public ConvertiblePair(Class<?> sourceType, Class<?> targetType) {
            this.sourceType = sourceType;
            this.targetType = targetType;
        }

        /**
         * 获取源类型
         *
         * @return 源类型
         */
        public Class<?> getSourceType() {
            return sourceType;
        }

        /**
         * 获取目标类型
         *
         * @return 目标类型
         */
        public Class<?> getTargetType() {
            return targetType;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            ConvertiblePair other = (ConvertiblePair) obj;
            return sourceType.equals(other.sourceType) && targetType.equals(other.targetType);
        }

        @Override
        public int hashCode() {
            return sourceType.hashCode() * 31 + targetType.hashCode();
        }

        @Override
        public String toString() {
            return sourceType.getName() + " -> " + targetType.getName();
        }
    }
}
