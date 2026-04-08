package com.linsir.system.modules.rbac.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.linsir.system.modules.rbac.entity.RolePermission;

import java.util.List;

/**
 * 角色权限关联 Service 接口
 *
 * @author linsir
 * @version 1.0.0
 */
public interface RolePermissionService extends IService<RolePermission> {

    /**
     * 根据角色ID查询权限ID列表
     *
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    List<Long> listPermissionIdsByRoleId(Long roleId);

    /**
     * 根据权限ID查询角色ID列表
     *
     * @param permissionId 权限ID
     * @return 角色ID列表
     */
    List<Long> listRoleIdsByPermissionId(Long permissionId);

    /**
     * 分配角色权限
     *
     * @param roleId        角色ID
     * @param permissionIds 权限ID列表
     * @return 是否成功
     */
    boolean assignPermissions(Long roleId, List<Long> permissionIds);
}
