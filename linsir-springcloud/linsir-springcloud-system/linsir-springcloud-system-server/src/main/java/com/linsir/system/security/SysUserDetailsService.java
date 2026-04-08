package com.linsir.system.security;

import com.linsir.system.modules.rbac.entity.Permission;
import com.linsir.system.modules.rbac.entity.Role;
import com.linsir.system.modules.rbac.entity.User;
import com.linsir.system.modules.rbac.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统用户详情服务
 * 实现 Spring Security 的 UserDetailsService 接口
 * 用于从数据库加载用户信息
 *
 * @author linsir
 * @version 1.0.0
 */
@Service
@RequiredArgsConstructor
public class SysUserDetailsService implements UserDetailsService {

    private final UserService userService;

    /**
     * 根据用户名加载用户信息
     *
     * @param username 用户名
     * @return UserDetails 用户详情
     * @throws UsernameNotFoundException 用户不存在时抛出
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 从数据库查询用户
        User user = userService.getByUsername(username);

        // 2. 检查用户是否存在
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        // 3. 检查用户是否被删除
        if (user.getDeleted() != null && user.getDeleted() == 1) {
            throw new UsernameNotFoundException("用户已被删除: " + username);
        }

        // 4. 构建权限列表（通过 RBAC 模型从数据库获取）
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        // 4.1 获取角色并添加 ROLE_ 前缀
        List<Role> roles = userService.getRolesByUserId(user.getId());
        if (roles != null && !roles.isEmpty()) {
            roles.forEach(role -> authorities.add(
                    new SimpleGrantedAuthority("ROLE_" + role.getRoleCode())
            ));
        }

        // 4.2 获取权限
        List<Permission> permissions = userService.getPermissionsByUserId(user.getId());
        if (permissions != null && !permissions.isEmpty()) {
            permissions.forEach(permission -> authorities.add(
                    new SimpleGrantedAuthority(permission.getPermissionCode())
            ));
        }

        // 4.3 如果没有角色和权限，默认添加 ROLE_USER
        if (authorities.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        // 5. 构建 Spring Security 的 UserDetails 对象
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                // 帐户是否未过期
                .accountExpired(user.getAccountNonExpired() != null && user.getAccountNonExpired() == 0)
                // 帐户是否未锁定
                .accountLocked(user.getAccountNonLocked() != null && user.getAccountNonLocked() == 0)
                // 凭证是否未过期
                .credentialsExpired(user.getCredentialsNonExpired() != null && user.getCredentialsNonExpired() == 0)
                // 是否启用（status: 0正常 1停用）
                .disabled(user.getStatus() != null && user.getStatus() == 1)
                .build();
    }
}
