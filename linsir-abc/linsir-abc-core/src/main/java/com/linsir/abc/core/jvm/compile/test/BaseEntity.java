package com.linsir.abc.core.jvm.compile.test;

import java.time.LocalDateTime;

/**
 * 基础实体类 - 用于测试父类字段包含功能
 * <p>
 * 该类作为Product等实体的父类，包含通用的ID和时间戳字段。
 * 当子类使用@AutoToString(includeSuper=true)时，这些字段会被包含在toString输出中。
 * </p>
 *
 * @author linsir
 * @version 1.0
 * @since 2026-03-28
 * @see AutoToString
 */
public class BaseEntity {

    /**
     * 实体ID
     */
    private Long id;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 默认构造方法
     */
    public BaseEntity() {
    }

    /**
     * 构造方法
     *
     * @param id         实体ID
     * @param createTime 创建时间
     * @param updateTime 更新时间
     */
    public BaseEntity(Long id, LocalDateTime createTime, LocalDateTime updateTime) {
        this.id = id;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    /**
     * 获取实体ID
     *
     * @return 实体ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置实体ID
     *
     * @param id 实体ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取创建时间
     *
     * @return 创建时间
     */
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取更新时间
     *
     * @return 更新时间
     */
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置更新时间
     *
     * @param updateTime 更新时间
     */
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
