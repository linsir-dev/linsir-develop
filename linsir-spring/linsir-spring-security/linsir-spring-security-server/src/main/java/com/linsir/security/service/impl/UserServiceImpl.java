package com.linsir.security.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.linsir.security.entity.Permission;
import com.linsir.security.entity.Role;
import com.linsir.security.entity.User;
import com.linsir.security.entity.UserRole;
import com.linsir.security.mapper.UserMapper;
import com.linsir.security.service.RoleService;
import com.linsir.security.service.UserRoleService;
import com.linsir.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户 Service 实现类
 * 继承 ServiceImpl 获得通用 Service 实现
 *
 * @author linsir
 * @version 1.0.0
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 获取用户的角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    @Override
    public List<Role> getUserRoles(Long userId) {
        // 查询用户的角色关联
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userId);
        List<UserRole> userRoles = userRoleService.list(wrapper);

        // 获取角色ID列表
        List<Long> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());

        // 查询角色详情
        return roleService.listByIds(roleIds);
    }

    /**
     * 给用户分配角色
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     */
    @Override
    @Transactional
    public void assignRoles(Long userId, List<Long> roleIds) {
        // 先删除该用户的所有角色关联
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userId);
        userRoleService.remove(wrapper);

        // 添加新的角色关联
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                UserRole userRole = new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRoleService.save(userRole);
            }
        }
    }

    /**
     * 获取用户的权限列表（通过角色关联）
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    @Override
    public List<Permission> getUserPermissions(Long userId) {
        // 获取用户的角色列表
        List<Role> roles = getUserRoles(userId);

        if (roles.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取所有角色的权限（使用 Stream API 合并并去重）
        return roles.stream()
                .flatMap(role -> roleService.getRolePermissions(role.getId()).stream())
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 根据用户名获取用户
     *
     * @param username 用户名
     * @return 用户对象
     */
    @Override
    public User getUserByUsername(String username) {
        // 使用 LambdaQueryWrapper 根据用户名查询
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return getOne(wrapper);
    }

    /**
     * 创建用户
     *
     * @param user 用户对象
     * @return 是否创建成功
     */
    @Override
    public boolean createUser(User user) {
        // 加密密码
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return save(user);
    }

    /**
     * 修改用户密码
     *
     * @param userId   用户ID
     * @param password 新密码
     * @return 是否修改成功
     */
    @Override
    public boolean updatePassword(Long userId, String password) {
        // 加密密码
        String encodedPassword = passwordEncoder.encode(password);
        // 更新密码
        return update().eq("id", userId).set("password", encodedPassword).update();
    }
}
