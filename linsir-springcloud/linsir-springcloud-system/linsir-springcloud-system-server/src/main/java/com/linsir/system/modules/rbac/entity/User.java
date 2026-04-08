package com.linsir.system.modules.rbac.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.linsir.system.core.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户实体类
 *
 * @author linsir
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class User extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户账号
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 头像地址
     */
    private String avatar;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 用户性别（0未知 1男 2女）
     */
    private Integer sex;

    /**
     * 帐号状态（0正常 1停用）
     */
    private Integer status;

    /**
     * 用户类型（0系统用户 1普通用户）
     */
    @TableField("user_type")
    private Integer userType;

    /**
     * 帐户是否未过期（0已过期 1未过期）
     */
    @TableField("account_non_expired")
    private Integer accountNonExpired;

    /**
     * 帐户是否未锁定（0已锁定 1未锁定）
     */
    @TableField("account_non_locked")
    private Integer accountNonLocked;

    /**
     * 凭证是否未过期（0已过期 1未过期）
     */
    @TableField("credentials_non_expired")
    private Integer credentialsNonExpired;

    /**
     * 最后登录IP
     */
    @TableField("login_ip")
    private String loginIp;

    /**
     * 最后登录时间
     */
    @TableField("login_date")
    private LocalDateTime loginDate;

    /**
     * 备注
     */
    private String remark;
}
