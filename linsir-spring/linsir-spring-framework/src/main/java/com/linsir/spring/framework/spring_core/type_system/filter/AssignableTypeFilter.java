package com.linsir.spring.framework.spring_core.type_system.filter;

import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.io.IOException;

/**
 * 可分配类型过滤器
 * 筛选继承或实现了指定类型的类
 * 包括直接继承和间接继承（通过父类或接口）
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024-01-01
 */
public class AssignableTypeFilter implements TypeFilter {

    /**
     * 目标类型
     */
    private final Class<?> targetType;

    public AssignableTypeFilter(Class<?> targetType) {
        this.targetType = targetType;
    }

    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
        String targetTypeName = targetType.getName();
        String className = metadataReader.getClassMetadata().getClassName();

        // 检查是否是目标类型本身
        if (className.equals(targetTypeName)) {
            return true;
        }

        // 检查父类
        String superClassName = metadataReader.getClassMetadata().getSuperClassName();
        if (targetTypeName.equals(superClassName)) {
            return true;
        }

        // 检查实现的接口
        for (String interfaceName : metadataReader.getClassMetadata().getInterfaceNames()) {
            if (targetTypeName.equals(interfaceName)) {
                return true;
            }
        }

        // 递归检查父类（需要加载类）
        try {
            Class<?> clazz = Class.forName(className);
            return targetType.isAssignableFrom(clazz);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public Class<?> getTargetType() {
        return targetType;
    }
}
