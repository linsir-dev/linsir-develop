package com.linsir.security.service;

import com.linsir.security.entity.User;
import com.linsir.security.entity.Role;
import com.linsir.security.entity.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义 UserDetailsService 实现
 * 用于从数据库或其他数据源加载用户信息
 * 
 * @author linsir
 * @version 1.0.0
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    /**
     * 根据用户名加载用户信息
     * 
     * @param username 用户名
     * @return 用户详细信息
     * @throws UsernameNotFoundException 用户不存在时抛出
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 从数据库查询用户
        // 注意：数据库中存储的是加密后的密码，不需要解密
        // Spring Security 会在认证时自动处理密码验证
        User user = userService.getUserByUsername(username);
        
        // 如果用户不存在，抛出异常
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在：" + username);
        }
        
        // 获取用户的角色列表
        List<Role> roles = userService.getUserRoles(user.getId());
        
        // 将角色列表转换为角色名称数组
        String[] roleNames = roles.stream()
                .map(Role::getRoleName)
                .toArray(String[]::new);
        
        // 获取用户的权限列表
        List<Permission> permissions = userService.getUserPermissions(user.getId());
        
        // 构建权限列表
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        
        // 添加功能权限
        for (Permission permission : permissions) {
            authorities.add(new SimpleGrantedAuthority(permission.getPermissionCode()));
        }
        
        // 构建 UserDetails 对象
        // 注意：直接使用数据库中存储的加密密码
        // Spring Security 会在认证时使用相同的 PasswordEncoder 来验证密码
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword()) // 直接使用加密后的密码
                .roles(roleNames)
                .authorities(authorities)
                .build();
    }
}
