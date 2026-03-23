package com.linsir.spring.framework.spring_core.reflection.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 基础实体类
 * 用于测试继承链中的字段反射
 */
@Data
public abstract class BaseEntity {

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 是否删除标记
     */
    private Boolean deleted = false;

    /**
     * 版本号（乐观锁）
     */
    private Integer version = 0;

    /**
     * 初始化基础字段
     */
    public void preInsert() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
        this.deleted = false;
        this.version = 0;
    }

    /**
     * 更新基础字段
     */
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
        if (this.version != null) {
            this.version++;
        }
    }
}
