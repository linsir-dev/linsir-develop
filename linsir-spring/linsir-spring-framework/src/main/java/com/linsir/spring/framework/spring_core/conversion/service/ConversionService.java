package com.linsir.spring.framework.spring_core.conversion.service;

import com.linsir.spring.framework.spring_core.conversion.descriptor.TypeDescriptor;

/**
 * 类型转换服务接口
 * 提供统一的类型转换入口
 *
 * <p>该接口定义了类型转换的核心能力，包括：</p>
 * <ul>
 *   <li>判断是否可以进行类型转换</li>
 *   <li>执行具体的类型转换操作</li>
 *   <li>支持泛型类型的转换</li>
 * </ul>
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-23
 */
public interface ConversionService {

    /**
     * 判断是否可以将源类型转换为目标类型
     *
     * @param sourceType 源类型
     * @param targetType 目标类型
     * @return 如果可以转换返回 true，否则返回 false
     */
    boolean canConvert(Class<?> sourceType, Class<?> targetType);

    /**
     * 判断是否可以将源类型转换为目标类型（支持泛型）
     *
     * @param sourceType 源类型描述符
     * @param targetType 目标类型描述符
     * @return 如果可以转换返回 true，否则返回 false
     */
    boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType);

    /**
     * 执行类型转换
     *
     * @param source 源对象
     * @param targetType 目标类型
     * @param <T> 目标类型泛型参数
     * @return 转换后的目标类型对象
     * @throws ConversionException 转换失败时抛出
     */
    <T> T convert(Object source, Class<T> targetType);

    /**
     * 执行类型转换（支持泛型）
     *
     * @param source 源对象
     * @param sourceType 源类型描述符
     * @param targetType 目标类型描述符
     * @return 转换后的对象
     * @throws ConversionException 转换失败时抛出
     */
    Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType);
}
