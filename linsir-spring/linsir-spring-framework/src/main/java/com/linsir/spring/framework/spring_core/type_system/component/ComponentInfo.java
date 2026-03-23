package com.linsir.spring.framework.spring_core.type_system.component;

import java.util.Set;

/**
 * 组件信息
 * 封装扫描到的组件的完整信息
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024-01-01
 */
public class ComponentInfo {

    /**
     * 类名
     */
    private String className;

    /**
     * 父类名
     */
    private String superClassName;

    /**
     * 实现的接口
     */
    private String[] interfaceNames;

    /**
     * 组件类型
     */
    private ComponentType componentType;

    /**
     * 类上的注解类型
     */
    private Set<String> annotationTypes;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSuperClassName() {
        return superClassName;
    }

    public void setSuperClassName(String superClassName) {
        this.superClassName = superClassName;
    }

    public String[] getInterfaceNames() {
        return interfaceNames;
    }

    public void setInterfaceNames(String[] interfaceNames) {
        this.interfaceNames = interfaceNames;
    }

    public ComponentType getComponentType() {
        return componentType;
    }

    public void setComponentType(ComponentType componentType) {
        this.componentType = componentType;
    }

    public Set<String> getAnnotationTypes() {
        return annotationTypes;
    }

    public void setAnnotationTypes(Set<String> annotationTypes) {
        this.annotationTypes = annotationTypes;
    }

    @Override
    public String toString() {
        return "ComponentInfo{" +
                "className='" + className + '\'' +
                ", componentType=" + componentType +
                ", annotationTypes=" + annotationTypes +
                '}';
    }
}
