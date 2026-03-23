package com.linsir.spring.framework.spring_core.reflection.service;

import java.util.List;

/**
 * 基础服务类
 * 用于测试继承链反射
 *
 * @param <T> 实体类型
 */
public abstract class BaseService<T> {

    /**
     * 实体类型
     */
    private final Class<T> entityClass;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 受保护字段
     */
    protected boolean initialized = false;

    /**
     * 构造方法
     */
    public BaseService(Class<T> entityClass) {
        this.entityClass = entityClass;
        this.serviceName = entityClass.getSimpleName() + "Service";
    }

    /**
     * 获取实体类型
     */
    public Class<T> getEntityClass() {
        return entityClass;
    }

    /**
     * 获取服务名称
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * 设置服务名称
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * 初始化方法
     */
    protected void initialize() {
        this.initialized = true;
        System.out.println("Service [" + serviceName + "] initialized");
    }

    /**
     * 抽象方法：保存实体
     */
    public abstract T save(T entity);

    /**
     * 抽象方法：根据ID查询
     */
    public abstract T findById(Long id);

    /**
     * 受保护方法：验证实体
     */
    protected boolean validate(T entity) {
        return entity != null;
    }

    /**
     * 私有方法：获取日志前缀
     */
    private String getLogPrefix() {
        return "[" + serviceName + "] ";
    }

    /**
     * 静态方法：获取服务版本
     */
    public static String getVersion() {
        return "1.0.0";
    }
}
