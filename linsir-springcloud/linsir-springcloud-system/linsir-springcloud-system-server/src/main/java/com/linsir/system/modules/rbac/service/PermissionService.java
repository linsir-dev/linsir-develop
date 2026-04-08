package com.linsir.system.modules.rbac.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linsir.system.modules.rbac.entity.Permission;

import java.util.List;

/**
 * 权限 Service 接口
 *
 * @author linsir
 * @version 1.0.0
 */
public interface PermissionService extends IService<Permission> {

    /**
     * 根据权限标识查询权限
     *
     * @param permissionCode 权限标识
     * @return 权限信息
     */
    Permission getByPermissionCode(String permissionCode);

    /**
     * 根据父权限ID查询子权限列表
     *
     * @param parentId 父权限ID
     * @return 权限列表
     */
    List<Permission> listByParentId(Long parentId);

    /**
     * 根据角色ID查询权限列表
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<Permission> listByRoleId(Long roleId);
}
