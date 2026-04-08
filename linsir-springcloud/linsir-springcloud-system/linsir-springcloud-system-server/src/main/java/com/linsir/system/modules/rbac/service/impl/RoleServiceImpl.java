package com.linsir.system.modules.rbac.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linsir.system.modules.rbac.entity.Role;
import com.linsir.system.modules.rbac.entity.UserRole;
import com.linsir.system.modules.rbac.mapper.RoleMapper;
import com.linsir.system.modules.rbac.service.RoleService;
import com.linsir.system.modules.rbac.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色 Service 实现类
 *
 * @author linsir
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    private final UserRoleService userRoleService;

    @Override
    public Role getByRoleCode(String roleCode) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getRoleCode, roleCode);
        return getOne(wrapper);
    }

    @Override
    public List<Role> listByUserId(Long userId) {
        // 1. 获取用户的角色ID列表
        List<Long> roleIds = userRoleService.listRoleIdsByUserId(userId);
        if (roleIds == null || roleIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 根据角色ID查询角色信息
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Role::getId, roleIds);
        return list(wrapper);
    }
}
