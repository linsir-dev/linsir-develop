package com.linsir.abc.mysql.chapter01.architecture.client.auth;

import com.linsir.abc.mysql.chapter01.architecture.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 认证管理器
 * 管理用户权限和会话权限验证
 *
 * 职责：
 * 1. 管理用户权限
 * 2. 验证操作权限
 * 3. 权限缓存
 *
 * @author linsir
 * @since 1.0.0
 */
@Slf4j
@Component
public class AuthManager {

    /**
     * 权限缓存
     * key: role, value: 权限集合
     */
    private final Map<String, Set<String>> permissionCache;

    public AuthManager() {
        this.permissionCache = new ConcurrentHashMap<>();
        initPermissions();
    }

    /**
     * 初始化权限配置
     */
    private void initPermissions() {
        // ADMIN 拥有所有权限
        permissionCache.put("ADMIN", Set.of("*"));

        // USER 拥有基本操作权限
        permissionCache.put("USER", Set.of(
                "SELECT", "INSERT", "UPDATE",
                "user:read", "user:update",
                "order:read", "order:create", "order:update"
        ));

        // GUEST 只有只读权限
        permissionCache.put("GUEST", Set.of(
                "SELECT",
                "user:read",
                "order:read"
        ));
    }

    /**
     * 检查用户是否有指定权限
     *
     * @param user       用户
     * @param permission 权限
     * @return true-有权限
     */
    public boolean hasPermission(User user, String permission) {
        if (user == null || !user.isValid()) {
            return false;
        }

        Set<String> permissions = permissionCache.get(user.getRole());
        if (permissions == null) {
            return false;
        }

        // ADMIN拥有所有权限
        if (permissions.contains("*")) {
            return true;
        }

        return permissions.contains(permission);
    }

    /**
     * 检查用户是否可以执行SQL操作
     *
     * @param user    用户
     * @param sqlType SQL类型 (SELECT, INSERT, UPDATE, DELETE)
     * @return true-可以执行
     */
    public boolean canExecuteSql(User user, String sqlType) {
        return hasPermission(user, sqlType);
    }

    /**
     * 获取用户角色权限列表
     *
     * @param role 角色
     * @return 权限集合
     */
    public Set<String> getRolePermissions(String role) {
        return permissionCache.getOrDefault(role, Set.of());
    }
}
