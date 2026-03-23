package com.linsir.spring.framework.spring_core.conversion.service;

/**
 * 可配置的类型转换服务接口
 * 继承 ConversionService 和 ConverterRegistry
 *
 * <p>该接口将类型转换服务和转换器注册能力合并，
 * 允许在运行时动态添加和移除转换器。</p>
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-23
 */
public interface ConfigurableConversionService extends ConversionService, ConverterRegistry {
}
