package com.linsir.abc.mysql.chapter01.architecture.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户实体类
 * 对应数据库表：users
 * 用于存储系统用户信息，支持认证授权功能
 *
 * @author linsir
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * 用户ID
     * 主键，自增
     */
    private Long id;

    /**
     * 用户名
     * 唯一标识，用于登录
     */
    private String username;

    /**
     * 加密密码
     * 使用BCrypt算法加密存储
     */
    private String password;

    /**
     * 邮箱
     * 可选，用于找回密码
     */
    private String email;

    /**
     * 手机号
     * 可选，用于通知
     */
    private String phone;

    /**
     * 状态
     * 0-禁用，1-启用
     */
    private Integer status;

    /**
     * 角色
     * ADMIN-管理员, USER-普通用户, GUEST-访客
     */
    private String role;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 最后登录IP
     */
    private String lastLoginIp;

    /**
     * 登录次数
     * 用于统计和安全审计
     */
    private Integer loginCount;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 检查用户是否有效
     *
     * @return true-有效，false-无效
     */
    public boolean isValid() {
        return status != null && status == 1;
    }

    /**
     * 检查是否为管理员
     *
     * @return true-是管理员
     */
    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    /**
     * 检查是否有指定角色权限
     *
     * @param requiredRole 需要的角色
     * @return true-有权限
     */
    public boolean hasRole(String requiredRole) {
        if ("ADMIN".equals(role)) {
            return true;
        }
        return requiredRole.equals(role);
    }
}
