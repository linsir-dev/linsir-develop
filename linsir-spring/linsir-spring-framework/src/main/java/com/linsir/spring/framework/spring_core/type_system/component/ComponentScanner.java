package com.linsir.spring.framework.spring_core.type_system.component;

import com.linsir.spring.framework.spring_core.type_system.filter.TypeFilter;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 组件扫描器
 * 综合使用类型系统的各个组件进行组件扫描
 * 演示ResolvableType、ClassMetadata、AnnotationMetadata、TypeFilter的协作
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024-01-01
 */
public class ComponentScanner {

    /**
     * 元数据读取器工厂
     */
    private final MetadataReaderFactory metadataReaderFactory;

    /**
     * 资源解析器
     */
    private final PathMatchingResourcePatternResolver resourceResolver;

    /**
     * 包含过滤器列表
     */
    private final List<TypeFilter> includeFilters;

    /**
     * 排除过滤器列表
     */
    private final List<TypeFilter> excludeFilters;

    public ComponentScanner() {
        this.metadataReaderFactory = new CachingMetadataReaderFactory();
        this.resourceResolver = new PathMatchingResourcePatternResolver();
        this.includeFilters = new ArrayList<>();
        this.excludeFilters = new ArrayList<>();
    }

    /**
     * 添加包含过滤器
     *
     * @param filter 过滤器
     */
    public void addIncludeFilter(TypeFilter filter) {
        this.includeFilters.add(filter);
    }

    /**
     * 添加排除过滤器
     *
     * @param filter 过滤器
     */
    public void addExcludeFilter(TypeFilter filter) {
        this.excludeFilters.add(filter);
    }

    /**
     * 扫描指定包下的组件
     *
     * @param basePackage 基础包路径
     * @return 扫描结果
     * @throws IOException 当资源读取失败时抛出
     */
    public ScanResult scan(String basePackage) throws IOException {
        ScanResult result = new ScanResult();
        List<ComponentInfo> components = new ArrayList<>();

        // 将包路径转换为资源路径
        String packageSearchPath = "classpath*:" + basePackage.replace('.', '/') + "/**/*.class";

        // 获取所有资源
        Resource[] resources = resourceResolver.getResources(packageSearchPath);

        for (Resource resource : resources) {
            if (!resource.isReadable()) {
                continue;
            }

            try {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);

                // 检查排除过滤器
                if (matchesExcludeFilters(metadataReader)) {
                    result.addExcludedClass(metadataReader.getClassMetadata().getClassName());
                    continue;
                }

                // 检查包含过滤器
                if (includeFilters.isEmpty() || matchesIncludeFilters(metadataReader)) {
                    ComponentInfo info = createComponentInfo(metadataReader);
                    components.add(info);
                    result.addScannedClass(metadataReader.getClassMetadata().getClassName());
                }

            } catch (Exception e) {
                result.addError("无法读取类: " + resource + ", 错误: " + e.getMessage());
            }
        }

        result.setComponents(components);
        return result;
    }

    /**
     * 检查是否匹配包含过滤器
     *
     * @param metadataReader 元数据读取器
     * @return 是否匹配
     */
    private boolean matchesIncludeFilters(MetadataReader metadataReader) throws IOException {
        for (TypeFilter filter : includeFilters) {
            if (filter.match(metadataReader, metadataReaderFactory)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查是否匹配排除过滤器
     *
     * @param metadataReader 元数据读取器
     * @return 是否匹配
     */
    private boolean matchesExcludeFilters(MetadataReader metadataReader) throws IOException {
        for (TypeFilter filter : excludeFilters) {
            if (filter.match(metadataReader, metadataReaderFactory)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 创建组件信息
     *
     * @param metadataReader 元数据读取器
     * @return 组件信息
     */
    private ComponentInfo createComponentInfo(MetadataReader metadataReader) {
        ComponentInfo info = new ComponentInfo();

        // 设置类元数据
        info.setClassName(metadataReader.getClassMetadata().getClassName());
        info.setSuperClassName(metadataReader.getClassMetadata().getSuperClassName());
        info.setInterfaceNames(metadataReader.getClassMetadata().getInterfaceNames());

        // 设置注解元数据
        info.setAnnotationTypes(metadataReader.getAnnotationMetadata().getAnnotationTypes());

        // 判断组件类型
        if (metadataReader.getAnnotationMetadata().hasAnnotation("org.springframework.stereotype.Service")) {
            info.setComponentType(ComponentType.SERVICE);
        } else if (metadataReader.getAnnotationMetadata().hasAnnotation("org.springframework.stereotype.Repository")) {
            info.setComponentType(ComponentType.REPOSITORY);
        } else if (metadataReader.getAnnotationMetadata().hasAnnotation("org.springframework.stereotype.Component")) {
            info.setComponentType(ComponentType.COMPONENT);
        } else {
            info.setComponentType(ComponentType.UNKNOWN);
        }

        return info;
    }
}
