package com.linsir.system.modules.rbac.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linsir.system.modules.rbac.entity.Permission;
import com.linsir.system.modules.rbac.mapper.PermissionMapper;
import com.linsir.system.modules.rbac.service.PermissionService;
import com.linsir.system.modules.rbac.service.RolePermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 权限 Service 实现类
 *
 * @author linsir
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    private final RolePermissionService rolePermissionService;

    @Override
    public Permission getByPermissionCode(String permissionCode) {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getPermissionCode, permissionCode);
        return getOne(wrapper);
    }

    @Override
    public List<Permission> listByParentId(Long parentId) {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getParentId, parentId);
        wrapper.orderByAsc(Permission::getPermissionSort);
        return list(wrapper);
    }

    @Override
    public List<Permission> listByRoleId(Long roleId) {
        // 1. 获取角色的权限ID列表
        List<Long> permissionIds = rolePermissionService.listPermissionIdsByRoleId(roleId);
        if (permissionIds == null || permissionIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 根据权限ID查询权限信息
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Permission::getId, permissionIds);
        return list(wrapper);
    }
}
