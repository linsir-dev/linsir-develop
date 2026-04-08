package com.linsir.system.modules.rbac.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linsir.system.modules.rbac.entity.Permission;
import com.linsir.system.modules.rbac.mapper.PermissionMapper;
import com.linsir.system.modules.rbac.service.PermissionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 权限 Service 实现类
 *
 * @author linsir
 * @version 1.0.0
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

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
        // 通过关联表查询，这里先返回空列表，后续实现
        return List.of();
    }
}
