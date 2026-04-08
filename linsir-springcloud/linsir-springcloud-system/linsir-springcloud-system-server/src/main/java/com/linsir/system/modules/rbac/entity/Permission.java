package com.linsir.system.modules.rbac.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linsir.system.core.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限实体类
 *
 * @author linsir
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_permission")
public class Permission extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 权限名称
     */
    @TableField("permission_name")
    private String permissionName;

    /**
     * 权限标识（如：system:user:create）
     */
    @TableField("permission_code")
    private String permissionCode;

    /**
     * 权限类型（1模块 2菜单 3按钮 4操作）
     */
    @TableField("permission_type")
    private Integer permissionType;

    /**
     * 父权限ID
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 图标（菜单类型时使用）
     */
    private String icon;

    /**
     * 路由路径（菜单类型时使用）
     */
    private String path;

    /**
     * 组件路径（菜单类型时使用）
     */
    private String component;

    /**
     * 显示顺序
     */
    @TableField("permission_sort")
    private Integer permissionSort;

    /**
     * 状态（0正常 1停用）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
