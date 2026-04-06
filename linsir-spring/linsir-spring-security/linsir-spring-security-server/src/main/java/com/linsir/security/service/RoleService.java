package com.linsir.security.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linsir.security.entity.Permission;
import com.linsir.security.entity.Role;

import java.util.List;

/**
 * 角色 Service 接口
 * 继承 IService 获得通用 Service 方法
 *
 * @author linsir
 * @version 1.0.0
 */
public interface RoleService extends IService<Role> {

    /**
     * 获取角色的权限列表
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<Permission> getRolePermissions(Long roleId);

    /**
     * 给角色分配权限
     *
     * @param roleId        角色ID
     * @param permissionIds 权限ID列表
     */
    void assignPermissions(Long roleId, List<Long> permissionIds);
}
