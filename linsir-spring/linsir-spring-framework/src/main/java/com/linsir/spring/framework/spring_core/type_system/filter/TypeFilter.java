package com.linsir.spring.framework.spring_core.type_system.filter;

import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.io.IOException;

/**
 * 类型过滤器接口
 * 用于在组件扫描时筛选符合条件的类
 * 对应Spring的org.springframework.core.type.filter.TypeFilter
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024-01-01
 */
@FunctionalInterface
public interface TypeFilter {

    /**
     * 判断给定的类元数据是否匹配过滤条件
     *
     * @param metadataReader     当前类的元数据读取器
     * @param metadataReaderFactory 元数据读取器工厂，用于获取其他类的元数据
     * @return 如果匹配返回true，否则返回false
     * @throws IOException 当读取元数据失败时抛出
     */
    boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException;
}
