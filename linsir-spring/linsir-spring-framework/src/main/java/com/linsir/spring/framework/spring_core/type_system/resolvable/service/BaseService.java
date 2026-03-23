package com.linsir.spring.framework.spring_core.type_system.resolvable.service;

import java.util.List;

/**
 * 基础服务接口
 * 定义通用的CRUD操作，使用泛型参数
 *
 * @param <T>  实体类型
 * @param <ID> 主键类型
 * @author linsir
 * @version 1.0.0
 * @since 2024-01-01
 */
public interface BaseService<T, ID> {

    /**
     * 根据ID查询实体
     *
     * @param id 主键
     * @return 实体对象
     */
    T findById(ID id);

    /**
     * 查询所有实体
     *
     * @return 实体列表
     */
    List<T> findAll();

    /**
     * 保存实体
     *
     * @param entity 实体对象
     * @return 保存后的实体
     */
    T save(T entity);

    /**
     * 根据ID删除实体
     *
     * @param id 主键
     */
    void deleteById(ID id);

    /**
     * 更新实体
     *
     * @param entity 实体对象
     * @return 更新后的实体
     */
    T update(T entity);
}
