package com.linsir.spring.framework.spring_core.type_system.metadata.scanner;

import java.util.Set;

/**
 * 类元数据信息
 * 封装类的结构信息和注解信息
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024-01-01
 */
public class ClassMetadataInfo {

    /**
     * 类名
     */
    private String className;

    /**
     * 父类名
     */
    private String superClassName;

    /**
     * 实现的接口名数组
     */
    private String[] interfaceNames;

    /**
     * 是否为抽象类
     */
    private boolean isAbstract;

    /**
     * 是否为接口
     */
    private boolean isInterface;

    /**
     * 是否为注解
     */
    private boolean isAnnotation;

    /**
     * 是否为枚举
     */
    private boolean isEnum;

    /**
     * 是否为final类
     */
    private boolean isFinal;

    /**
     * 内部类名数组
     */
    private String[] memberClassNames;

    /**
     * 类上的注解类型集合
     */
    private Set<String> annotationTypes;

    /**
     * 标注特定注解的方法集合
     */
    private Set<org.springframework.core.type.MethodMetadata> annotatedMethods;

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

    public boolean isAbstract() {
        return isAbstract;
    }

    public void setAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
    }

    public boolean isInterface() {
        return isInterface;
    }

    public void setInterface(boolean isInterface) {
        this.isInterface = isInterface;
    }

    public boolean isAnnotation() {
        return isAnnotation;
    }

    public void setAnnotation(boolean isAnnotation) {
        this.isAnnotation = isAnnotation;
    }

    public boolean isEnum() {
        return isEnum;
    }

    public void setEnum(boolean isEnum) {
        this.isEnum = isEnum;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }

    public String[] getMemberClassNames() {
        return memberClassNames;
    }

    public void setMemberClassNames(String[] memberClassNames) {
        this.memberClassNames = memberClassNames;
    }

    public Set<String> getAnnotationTypes() {
        return annotationTypes;
    }

    public void setAnnotationTypes(Set<String> annotationTypes) {
        this.annotationTypes = annotationTypes;
    }

    public Set<org.springframework.core.type.MethodMetadata> getAnnotatedMethods() {
        return annotatedMethods;
    }

    public void setAnnotatedMethods(Set<org.springframework.core.type.MethodMetadata> annotatedMethods) {
        this.annotatedMethods = annotatedMethods;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ClassMetadataInfo{");
        sb.append("className='").append(className).append('\'');
        sb.append(", superClassName='").append(superClassName).append('\'');
        sb.append(", isAbstract=").append(isAbstract);
        sb.append(", isInterface=").append(isInterface);
        sb.append(", isAnnotation=").append(isAnnotation);
        sb.append(", isEnum=").append(isEnum);
        sb.append(", isFinal=").append(isFinal);
        sb.append('}');
        return sb.toString();
    }
}
