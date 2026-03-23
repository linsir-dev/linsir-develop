package com.linsir.spring.framework.spring_core.type_system.filter;

import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.io.IOException;

/**
 * 注解类型过滤器
 * 筛选标注了指定注解的类
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024-01-01
 */
public class AnnotationTypeFilter implements TypeFilter {

    /**
     * 目标注解类型
     */
    private final Class<? extends java.lang.annotation.Annotation> annotationType;

    /**
     * 是否考虑元注解
     */
    private final boolean considerMetaAnnotations;

    public AnnotationTypeFilter(Class<? extends java.lang.annotation.Annotation> annotationType) {
        this(annotationType, true);
    }

    public AnnotationTypeFilter(Class<? extends java.lang.annotation.Annotation> annotationType, boolean considerMetaAnnotations) {
        this.annotationType = annotationType;
        this.considerMetaAnnotations = considerMetaAnnotations;
    }

    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
        String annotationName = annotationType.getName();

        if (considerMetaAnnotations) {
            // 检查是否标注了该注解（包括元注解）
            return metadataReader.getAnnotationMetadata().hasAnnotation(annotationName) ||
                   metadataReader.getAnnotationMetadata().hasMetaAnnotation(annotationName);
        } else {
            // 只检查直接注解
            return metadataReader.getAnnotationMetadata().hasAnnotation(annotationName);
        }
    }

    public Class<? extends java.lang.annotation.Annotation> getAnnotationType() {
        return annotationType;
    }
}
