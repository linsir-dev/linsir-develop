package com.linsir.system.modules.rbac.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linsir.system.modules.rbac.entity.Permission;
import com.linsir.system.modules.rbac.entity.Role;
import com.linsir.system.modules.rbac.entity.User;

import java.util.List;

/**
 * 用户 Service 接口
 *
 * @author linsir
 * @version 1.0.0
 */
public interface UserService extends IService<User> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    User getByUsername(String username);

    /**
     * 根据手机号查询用户
     *
     * @param mobile 手机号
     * @return 用户信息
     */
    User getByMobile(String mobile);

    /**
     * 根据用户ID查询权限列表
     * 通过 RBAC 模型：用户 -> 角色 -> 权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    List<Permission> getPermissionsByUserId(Long userId);

    /**
     * 根据用户ID查询权限标识列表
     *
     * @param userId 用户ID
     * @return 权限标识列表（如：system:user:create）
     */
    List<String> getPermissionCodesByUserId(Long userId);

    /**
     * 根据用户ID查询角色列表
     * 通过 RBAC 模型：用户 -> 角色
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<Role> getRolesByUserId(Long userId);

    /**
     * 根据用户ID查询角色编码列表
     *
     * @param userId 用户ID
     * @return 角色编码列表（如：super_admin）
     */
    List<String> getRoleCodesByUserId(Long userId);
}
