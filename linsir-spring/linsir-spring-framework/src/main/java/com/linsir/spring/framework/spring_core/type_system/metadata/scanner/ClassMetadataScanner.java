package com.linsir.spring.framework.spring_core.type_system.metadata.scanner;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 类元数据扫描器
 * 用于扫描包路径下的类元数据信息
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024-01-01
 */
public class ClassMetadataScanner {

    /**
     * 元数据读取器工厂
     */
    private final MetadataReaderFactory metadataReaderFactory;

    /**
     * 资源解析器
     */
    private final PathMatchingResourcePatternResolver resourceResolver;

    public ClassMetadataScanner() {
        this.metadataReaderFactory = new CachingMetadataReaderFactory();
        this.resourceResolver = new PathMatchingResourcePatternResolver();
    }

    /**
     * 扫描指定包下的所有类
     *
     * @param basePackage 基础包路径，例如：com.example.service
     * @return 类元数据信息列表
     * @throws IOException 当资源读取失败时抛出
     */
    public List<ClassMetadataInfo> scanPackage(String basePackage) throws IOException {
        List<ClassMetadataInfo> result = new ArrayList<>();

        // 将包路径转换为资源路径
        String packageSearchPath = "classpath*:" + basePackage.replace('.', '/') + "/**/*.class";

        // 获取所有资源
        Resource[] resources = resourceResolver.getResources(packageSearchPath);

        for (Resource resource : resources) {
            if (resource.isReadable()) {
                try {
                    MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                    ClassMetadataInfo info = extractMetadataInfo(metadataReader);
                    result.add(info);
                } catch (Exception e) {
                    // 跳过无法读取的类
                    System.err.println("无法读取类: " + resource + ", 错误: " + e.getMessage());
                }
            }
        }

        return result;
    }

    /**
     * 从MetadataReader提取类元数据信息
     *
     * @param metadataReader 元数据读取器
     * @return 类元数据信息
     */
    private ClassMetadataInfo extractMetadataInfo(MetadataReader metadataReader) {
        ClassMetadataInfo info = new ClassMetadataInfo();

        // 获取类元数据
        org.springframework.core.type.ClassMetadata classMetadata = metadataReader.getClassMetadata();

        info.setClassName(classMetadata.getClassName());
        info.setSuperClassName(classMetadata.getSuperClassName());
        info.setInterfaceNames(classMetadata.getInterfaceNames());
        info.setAbstract(classMetadata.isAbstract());
        info.setInterface(classMetadata.isInterface());
        info.setAnnotation(classMetadata.isAnnotation());
        info.setFinal(classMetadata.isFinal());
        info.setMemberClassNames(classMetadata.getMemberClassNames());

        // 获取注解元数据
        org.springframework.core.type.AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
        info.setAnnotationTypes(annotationMetadata.getAnnotationTypes());
        info.setAnnotatedMethods(annotationMetadata.getAnnotatedMethods("com.linsir.spring.framework.spring_core.type_system.metadata.model.Service"));

        return info;
    }

    /**
     * 获取单个类的元数据信息
     *
     * @param className 完整类名
     * @return 类元数据信息
     * @throws IOException 当类不存在或无法读取时抛出
     */
    public ClassMetadataInfo getClassMetadata(String className) throws IOException {
        MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(className);
        return extractMetadataInfo(metadataReader);
    }

    /**
     * 扫描带有指定注解的类
     *
     * @param basePackage      基础包路径
     * @param annotationClass  注解类
     * @return 带有指定注解的类列表
     * @throws IOException 当资源读取失败时抛出
     */
    public List<ClassMetadataInfo> scanByAnnotation(String basePackage, Class<?> annotationClass) throws IOException {
        List<ClassMetadataInfo> allClasses = scanPackage(basePackage);
        List<ClassMetadataInfo> annotatedClasses = new ArrayList<>();

        String annotationName = annotationClass.getName();

        for (ClassMetadataInfo info : allClasses) {
            if (info.getAnnotationTypes().contains(annotationName)) {
                annotatedClasses.add(info);
            }
        }

        return annotatedClasses;
    }
}
