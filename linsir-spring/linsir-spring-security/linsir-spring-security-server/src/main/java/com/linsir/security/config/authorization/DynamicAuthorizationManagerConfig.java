package com.linsir.security.config.authorization;

import com.linsir.security.entity.Permission;
import com.linsir.security.entity.User;
import com.linsir.security.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.util.List;
import java.util.function.Supplier;

/**
 * 动态授权 AuthorizationManager 配置类
 *
 * 基于 RBAC 模型进行动态授权
 * 获取登录用户，通过用户的角色获取权限列表，进行动态授权判断
 *
 * @author linsir
 * @version 1.0.0
 */
@Configuration
public class DynamicAuthorizationManagerConfig {

    private final UserService userService;

    public DynamicAuthorizationManagerConfig(UserService userService) {
        this.userService = userService;
    }

    /**
     * 配置动态授权 AuthorizationManager
     *
     * @return 动态授权 AuthorizationManager 实例
     */
    @Bean
    public AuthorizationManager<RequestAuthorizationContext> dynamicAuthorizationManager() {
        return new AuthorizationManager<RequestAuthorizationContext>() {
            @Override
            public AuthorizationDecision authorize(Supplier<? extends Authentication> authenticationSupplier,
                                                   RequestAuthorizationContext context) {
                // 获取当前认证信息
                Authentication authentication = authenticationSupplier.get();

                // 未认证用户，拒绝访问
                if (authentication == null || !authentication.isAuthenticated()) {
                    return new AuthorizationDecision(false);
                }

                // 获取请求信息
                String requestUri = context.getRequest().getRequestURI();
                String requestMethod = context.getRequest().getMethod();

                // 获取当前用户名
                String username = authentication.getName();

                // 从数据库获取用户信息
                User user = userService.getUserByUsername(username);
                if (user == null) {
                    return new AuthorizationDecision(false);
                }

                // 1. 检查是否是超级管理员（拥有所有权限）
                boolean isAdmin = authentication.getAuthorities().stream()
                        .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN") ||
                                         auth.getAuthority().equals("ROLE_SUPER_ADMIN"));
                if (isAdmin) {
                    return new AuthorizationDecision(true);
                }

                // 2. 通过 RBAC 模型获取用户的权限列表
                List<Permission> userPermissions = userService.getUserPermissions(user.getId());

                // 3. 检查用户是否拥有访问该 URL 和方法的权限
                // 匹配规则：URL 匹配且方法匹配（或方法为 * 表示所有方法）
                boolean hasPermission = userPermissions.stream()
                        .anyMatch(permission -> matchPermission(permission, requestUri, requestMethod));

                // 4. 同时检查 Spring Security 上下文中的权限（兼容已有权限）
                if (!hasPermission) {
                    String requiredPermission = buildPermissionKey(requestMethod, requestUri);
                    hasPermission = authentication.getAuthorities().stream()
                            .anyMatch(auth -> auth.getAuthority().equals(requiredPermission) ||
                                             auth.getAuthority().equals("ROLE_" + requiredPermission));
                }

                return new AuthorizationDecision(hasPermission);
            }
        };
    }

    /**
     * 匹配权限
     * 检查 Permission 是否匹配请求的 URI 和方法
     *
     * @param permission 权限对象
     * @param requestUri 请求 URI
     * @param requestMethod 请求方法
     * @return true: 匹配, false: 不匹配
     */
    private boolean matchPermission(Permission permission, String requestUri, String requestMethod) {
        // 获取权限的 URL 和方法
        String permissionUrl = permission.getUrl();
        String permissionMethod = permission.getMethod();

        // URL 为空，不匹配
        if (permissionUrl == null || permissionUrl.isEmpty()) {
            return false;
        }

        // 检查 URL 是否匹配
        // 支持 Ant 风格的路径匹配，如 /api/user/* 匹配 /api/user/1
        boolean urlMatch = matchUrl(permissionUrl, requestUri);

        // 检查方法是否匹配
        // permissionMethod 为 null 或 * 表示匹配所有方法
        boolean methodMatch = permissionMethod == null ||
                              permissionMethod.isEmpty() ||
                              permissionMethod.equals("*") ||
                              permissionMethod.equalsIgnoreCase(requestMethod);

        return urlMatch && methodMatch;
    }

    /**
     * URL 匹配
     * 支持精确匹配和 Ant 风格通配符匹配
     *
     * @param pattern URL 模式
     * @param requestUri 请求 URI
     * @return true: 匹配, false: 不匹配
     */
    private boolean matchUrl(String pattern, String requestUri) {
        // 精确匹配
        if (pattern.equals(requestUri)) {
            return true;
        }

        // Ant 风格匹配
        // /api/user/* 匹配 /api/user/1, /api/user/2
        // /api/user/** 匹配 /api/user/1/role, /api/user/1/role/2
        if (pattern.contains("*")) {
            String regex = pattern.replace("**", ".*")
                                  .replace("*", "[^/]*");
            return requestUri.matches(regex);
        }

        // 前缀匹配
        // /api/user 匹配 /api/user, /api/user/1, /api/user/list
        if (requestUri.startsWith(pattern)) {
            return true;
        }

        return false;
    }

    /**
     * 构建权限键
     * 将 HTTP 方法和请求路径转换为权限编码格式
     *
     * @param method HTTP 方法
     * @param uri    请求路径
     * @return 权限编码
     */
    private String buildPermissionKey(String method, String uri) {
        // 将 /api/user/list 转换为 api:user:list
        // 将 GET /api/user/1 转换为 api:user:get
        String normalizedUri = uri.replace("/", ":");
        if (normalizedUri.startsWith(":")) {
            normalizedUri = normalizedUri.substring(1);
        }

        // 如果路径包含数字 ID，去掉 ID 部分，使用方法名
        // 例如：/api/user/1 -> api:user:get
        normalizedUri = normalizedUri.replaceAll(":\\d+", "");

        // 返回格式：api:user:list 或 get:api:user
        return method.toLowerCase() + ":" + normalizedUri;
    }
}
