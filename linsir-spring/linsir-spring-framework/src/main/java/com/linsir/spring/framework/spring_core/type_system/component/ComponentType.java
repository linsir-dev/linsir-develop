package com.linsir.spring.framework.spring_core.type_system.component;

/**
 * 组件类型枚举
 * 定义Spring中常见的组件类型
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024-01-01
 */
public enum ComponentType {

    /**
     * 服务层组件
     */
    SERVICE("Service", "业务逻辑层组件"),

    /**
     * 数据访问层组件
     */
    REPOSITORY("Repository", "数据访问层组件"),

    /**
     * 控制器层组件
     */
    CONTROLLER("Controller", "控制器层组件"),

    /**
     * 通用组件
     */
    COMPONENT("Component", "通用组件"),

    /**
     * 配置类
     */
    CONFIGURATION("Configuration", "配置类"),

    /**
     * 未知类型
     */
    UNKNOWN("Unknown", "未知类型");

    /**
     * 类型名称
     */
    private final String name;

    /**
     * 类型描述
     */
    private final String description;

    ComponentType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
