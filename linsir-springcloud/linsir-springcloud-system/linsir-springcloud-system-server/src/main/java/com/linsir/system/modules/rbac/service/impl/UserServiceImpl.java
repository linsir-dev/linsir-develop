package com.linsir.system.modules.rbac.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linsir.system.modules.rbac.entity.Permission;
import com.linsir.system.modules.rbac.entity.Role;
import com.linsir.system.modules.rbac.entity.User;
import com.linsir.system.modules.rbac.mapper.UserMapper;
import com.linsir.system.modules.rbac.service.PermissionService;
import com.linsir.system.modules.rbac.service.RoleService;
import com.linsir.system.modules.rbac.service.UserRoleService;
import com.linsir.system.modules.rbac.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户 Service 实现类
 *
 * @author linsir
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserRoleService userRoleService;
    private final RoleService roleService;
    private final PermissionService permissionService;

    @Override
    public User getByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return getOne(wrapper);
    }

    @Override
    public User getByMobile(String mobile) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getMobile, mobile);
        return getOne(wrapper);
    }

    @Override
    public List<Permission> getPermissionsByUserId(Long userId) {
        // 1. 获取用户的角色列表
        List<Role> roles = roleService.listByUserId(userId);
        if (roles == null || roles.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 遍历角色，获取每个角色的权限
        List<Permission> allPermissions = new ArrayList<>();
        for (Role role : roles) {
            List<Permission> permissions = permissionService.listByRoleId(role.getId());
            if (permissions != null && !permissions.isEmpty()) {
                allPermissions.addAll(permissions);
            }
        }

        // 3. 去重（一个用户可能有多个角色，角色间可能有重复权限）
        return allPermissions.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getPermissionCodesByUserId(Long userId) {
        // 获取权限列表，提取权限标识
        return getPermissionsByUserId(userId).stream()
                .map(Permission::getPermissionCode)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<Role> getRolesByUserId(Long userId) {
        // 通过 RoleService 获取用户的角色列表
        return roleService.listByUserId(userId);
    }

    @Override
    public List<String> getRoleCodesByUserId(Long userId) {
        // 获取角色列表，提取角色编码
        List<Role> roles = getRolesByUserId(userId);
        if (roles == null || roles.isEmpty()) {
            return new ArrayList<>();
        }
        return roles.stream()
                .map(Role::getRoleCode)
                .distinct()
                .collect(Collectors.toList());
    }
}
