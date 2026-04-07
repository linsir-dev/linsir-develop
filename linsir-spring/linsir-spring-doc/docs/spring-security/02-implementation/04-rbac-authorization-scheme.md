# RBAC 授权方案

## 概述

本方案基于 Spring Security 的授权架构，实现完整的基于角色的访问控制（Role-Based Access Control，RBAC）系统。方案涵盖角色定义、权限配置、角色层次结构、动态权限管理等内容。

---

## 一、RBAC 核心概念

### 1.1 角色模型

```
用户 (User) → 角色 (Role) → 权限 (Permission/Authority)
```

**核心组件：**
- **用户（User）**：系统的使用者
- **角色（Role）**：权限的集合，代表一类用户的访问权限
- **权限（Permission）**：对资源的操作权限，对应 `GrantedAuthority`

### 1.2 权限类型

#### 角色权限（Role Authority）
以 `ROLE_` 为前缀的权限：
```java
ROLE_ADMIN      // 管理员角色
ROLE_MANAGER    // 经理角色
ROLE_USER       // 普通用户角色
ROLE_GUEST      // 访客角色
```

#### 功能权限（Function Authority）
具体的功能操作权限：
```java
USER_CREATE     // 创建用户
USER_DELETE     // 删除用户
USER_UPDATE     // 更新用户
USER_VIEW       // 查看用户
ORDER_CREATE    // 创建订单
ORDER_APPROVE   // 审批订单
```

#### 数据权限（Data Authority）
数据范围的权限：
```java
DATA_ALL        // 所有数据
DATA_DEPT       // 部门数据
DATA_SELF       // 个人数据
```

---

## 二、角色层次结构设计

### 2.1 角色层次配置

```java
package com.linsir.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;

/**
 * 角色层次结构配置
 */
@Configuration
public class RoleHierarchyConfig {

    /**
     * 配置角色层次结构
     * 
     * 层次关系：
     * ROLE_ADMIN > ROLE_MANAGER > ROLE_USER > ROLE_GUEST
     * 
     * 含义：
     * - ADMIN 自动拥有 MANAGER、USER、GUEST 的所有权限
     * - MANAGER 自动拥有 USER、GUEST 的所有权限
     * - USER 自动拥有 GUEST 的所有权限
     */
    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withDefaultRolePrefix()
            .role("ADMIN").implies("MANAGER")    // 管理员包含经理权限
            .role("MANAGER").implies("USER")     // 经理包含用户权限
            .role("USER").implies("GUEST")       // 用户包含访客权限
            .build();
    }
}
```

### 2.2 角色层次图

```
┌─────────────┐
│ ROLE_ADMIN  │
│ (管理员)     │
└──────┬──────┘
       │ implies
       ▼
┌─────────────┐
│ ROLE_MANAGER│
│ (经理)       │
└──────┬──────┘
       │ implies
       ▼
┌─────────────┐
│ ROLE_USER   │
│ (普通用户)   │
└──────┬──────┘
       │ implies
       ▼
┌─────────────┐
│ ROLE_GUEST  │
│ (访客)       │
└─────────────┘
```

### 2.3 角色权限矩阵

| 角色 | 隐含角色 | 访问资源 |
|------|----------|----------|
| `ROLE_ADMIN` | MANAGER, USER, GUEST | 所有资源 |
| `ROLE_MANAGER` | USER, GUEST | 管理资源 + 用户资源 |
| `ROLE_USER` | GUEST | 用户资源 |
| `ROLE_GUEST` | - | 公开资源 |

---

## 三、AuthorizationManager 配置

### 3.1 工厂配置

```java
package com.linsir.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationManagerFactory;
import org.springframework.security.authorization.DefaultAuthorizationManagerFactory;

/**
 * AuthorizationManagerFactory 配置
 */
@Configuration
public class AuthorizationManagerFactoryConfig {

    /**
     * 配置 AuthorizationManagerFactory
     */
    @Bean
    public AuthorizationManagerFactory<Object> authorizationManagerFactory() {
        DefaultAuthorizationManagerFactory<Object> factory = 
            new DefaultAuthorizationManagerFactory<>();
        
        // 配置角色前缀为 "ROLE_"
        factory.setRolePrefix("ROLE_");
        
        // 配置角色层次结构（可选，如果已配置 RoleHierarchy Bean 会自动注入）
        // factory.setRoleHierarchy(roleHierarchy());
        
        return factory;
    }
}
```

### 3.2 角色 AuthorizationManager 配置

每个角色创建独立的 AuthorizationManager Bean：

```java
package com.linsir.security.config.authorization;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationManagerFactory;

/**
 * Admin Role AuthorizationManager 配置
 */
@Configuration
public class AdminRoleAuthorizationManagerConfig {

    @Bean
    public AuthorizationManager<Object> adminRoleAuthorizationManager(
            AuthorizationManagerFactory<Object> factory) {
        return factory.hasRole("ADMIN");
    }
}
```

```java
package com.linsir.security.config.authorization;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationManagerFactory;

/**
 * Manager Role AuthorizationManager 配置
 */
@Configuration
public class ManagerRoleAuthorizationManagerConfig {

    @Bean
    public AuthorizationManager<Object> managerRoleAuthorizationManager(
            AuthorizationManagerFactory<Object> factory) {
        return factory.hasRole("MANAGER");
    }
}
```

```java
package com.linsir.security.config.authorization;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationManagerFactory;

/**
 * User Role AuthorizationManager 配置
 */
@Configuration
public class UserRoleAuthorizationManagerConfig {

    @Bean
    public AuthorizationManager<Object> userRoleAuthorizationManager(
            AuthorizationManagerFactory<Object> factory) {
        return factory.hasRole("USER");
    }
}
```

### 3.3 认证状态 AuthorizationManager 配置

```java
package com.linsir.security.config.authorization;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationManagerFactory;

/**
 * Authenticated AuthorizationManager 配置
 */
@Configuration
public class AuthenticatedAuthorizationManagerConfig {

    /**
     * 已认证（包括 remember-me）
     */
    @Bean
    public AuthorizationManager<Object> authenticatedAuthorizationManager(
            AuthorizationManagerFactory<Object> factory) {
        return factory.authenticated();
    }

    /**
     * 完全认证（不包括 remember-me）
     */
    @Bean
    public AuthorizationManager<Object> fullyAuthenticatedAuthorizationManager(
            AuthorizationManagerFactory<Object> factory) {
        return factory.fullyAuthenticated();
    }

    /**
     * 允许所有（公开资源）
     */
    @Bean
    public AuthorizationManager<Object> permitAllAuthorizationManager(
            AuthorizationManagerFactory<Object> factory) {
        return factory.permitAll();
    }
}
```

---

## 四、SecurityConfig 集成

### 4.1 Web 请求授权配置

```java
package com.linsir.security.config;

import com.linsir.security.config.authorization.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security 配置
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private PermitAllAuthorizationManagerConfig permitAllConfig;
    
    @Autowired
    private AuthenticatedAuthorizationManagerConfig authenticatedConfig;
    
    @Autowired
    private UserRoleAuthorizationManagerConfig userRoleConfig;
    
    @Autowired
    private ManagerRoleAuthorizationManagerConfig managerRoleConfig;
    
    @Autowired
    private AdminRoleAuthorizationManagerConfig adminRoleConfig;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 授权配置
            .authorizeHttpRequests(auth -> auth
                // 公开资源（允许所有访问）
                .requestMatchers("/public/**", "/static/**", "/api/auth/**")
                    .manager(permitAllConfig.permitAllAuthorizationManager(null))
                
                // 访客资源（需要登录）
                .requestMatchers("/guest/**")
                    .manager(authenticatedConfig.authenticatedAuthorizationManager(null))
                
                // 用户资源（需要 USER 角色）
                .requestMatchers("/user/**", "/api/user/**")
                    .manager(userRoleConfig.userRoleAuthorizationManager(null))
                
                // 经理资源（需要 MANAGER 角色）
                .requestMatchers("/manager/**", "/api/manager/**")
                    .manager(managerRoleConfig.managerRoleAuthorizationManager(null))
                
                // 管理员资源（需要 ADMIN 角色）
                .requestMatchers("/admin/**", "/api/admin/**")
                    .manager(adminRoleConfig.adminRoleAuthorizationManager(null))
                
                // 其他请求需要认证
                .anyRequest()
                    .manager(authenticatedConfig.authenticatedAuthorizationManager(null))
            )
            
            // 其他配置
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form
                .loginProcessingUrl("/api/auth/login")
                .successHandler(customLoginSuccessHandler)
                .failureHandler(customLoginFailureHandler)
            )
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .logoutSuccessHandler(customLogoutSuccessHandler)
            );
        
        return http.build();
    }
}
```

### 4.2 方法级授权配置

```java
package com.linsir.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authorization.method.AuthorizationManagerBeforeMethodInterceptor;
import org.springframework.security.authorization.method.PreAuthorizeAuthorizationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.context.SecurityContextThreadLocalStrategy;

/**
 * 方法安全配置
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig {

    /**
     * 配置方法级授权拦截器
     */
    @Bean
    public AuthorizationManagerBeforeMethodInterceptor preAuthorizeAuthorizationManager(
            RoleHierarchy roleHierarchy) {
        
        PreAuthorizeAuthorizationManager manager = 
            new PreAuthorizeAuthorizationManager();
        
        // 设置角色层次结构
        manager.setRoleHierarchy(roleHierarchy);
        
        // 配置 SecurityContextHolder 策略
        SecurityContextHolderStrategy strategy = 
            new SecurityContextThreadLocalStrategy();
        manager.setSecurityContextHolderStrategy(strategy);
        
        return AuthorizationManagerBeforeMethodInterceptor.preAuthorize(manager);
    }
}
```

### 4.3 方法级授权使用示例

```java
package com.linsir.security.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    /**
     * 创建用户（需要 USER_CREATE 权限）
     */
    @PreAuthorize("hasAuthority('USER_CREATE')")
    public void createUser(String username, String password) {
        // 创建用户逻辑
    }

    /**
     * 删除用户（需要 ADMIN 角色）
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(Long userId) {
        // 删除用户逻辑
    }

    /**
     * 查看用户详情（需要 USER 角色，且返回对象的所有者必须是当前用户）
     */
    @PreAuthorize("hasRole('USER')")
    @PostAuthorize("returnObject.owner == authentication.name")
    public User getUser(Long userId) {
        // 查询用户逻辑
        return new User(userId, "owner");
    }

    /**
     * 审批订单（需要 MANAGER 角色）
     */
    @PreAuthorize("hasRole('MANAGER')")
    public void approveOrder(Long orderId) {
        // 审批订单逻辑
    }

    /**
     * 查看所有订单（需要 ADMIN 角色）
     */
    @PreAuthorize("hasRole('ADMIN')")
    public List<Order> getAllOrders() {
        // 查询所有订单逻辑
        return orderRepository.findAll();
    }
}
```

---

## 五、动态权限管理

### 5.1 自定义 AuthorizationManager

```java
package com.linsir.security.authorization;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

/**
 * 动态权限 AuthorizationManager
 * 
 * 支持动态配置权限，可以从数据库或配置中心加载权限规则
 */
@Component
public class DynamicPermissionAuthorizationManager implements AuthorizationManager<Object> {

    private final PermissionService permissionService;

    public DynamicPermissionAuthorizationManager(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Override
    public AuthorizationDecision authorize(Supplier<Authentication> authentication,
                                           Object object) {
        Authentication auth = authentication.get();
        if (auth == null || !auth.isAuthenticated()) {
            return new AuthorizationDecision(false);
        }

        // 获取请求路径
        String requestPath = extractRequestPath(object);
        
        // 获取用户权限
        boolean hasPermission = checkPermission(auth, requestPath);
        
        return new AuthorizationDecision(hasPermission);
    }

    /**
     * 检查用户是否有权限
     */
    private boolean checkPermission(Authentication authentication, String requestPath) {
        // 1. 检查是否拥有直接匹配的权限
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String authorityName = authority.getAuthority();
            
            // 检查权限是否匹配请求路径
            if (permissionService.matchPermission(authorityName, requestPath)) {
                return true;
            }
        }
        
        // 2. 检查角色层次
        // （如果配置了 RoleHierarchy，会自动处理）
        
        return false;
    }

    /**
     * 提取请求路径
     */
    private String extractRequestPath(Object object) {
        // 根据 object 类型提取请求路径
        // 可能是 HttpServletRequest、MethodInvocation 等
        if (object instanceof String) {
            return (String) object;
        }
        // 其他类型处理...
        return "";
    }
}
```

### 5.2 权限服务接口

```java
package com.linsir.security.service;

import org.springframework.stereotype.Service;

/**
 * 权限服务
 */
@Service
public class PermissionService {

    /**
     * 检查权限是否匹配请求路径
     */
    public boolean matchPermission(String permission, String requestPath) {
        // 从数据库或配置中加载权限规则
        // 示例：USER_CREATE 权限对应 /api/user/create 路径
        
        // 实现权限匹配逻辑
        return switch (permission) {
            case "USER_CREATE" -> requestPath.matches(".*/user/create.*");
            case "USER_DELETE" -> requestPath.matches(".*/user/delete.*");
            case "USER_UPDATE" -> requestPath.matches(".*/user/update.*");
            case "USER_VIEW" -> requestPath.matches(".*/user/view.*");
            case "ORDER_CREATE" -> requestPath.matches(".*/order/create.*");
            case "ORDER_APPROVE" -> requestPath.matches(".*/order/approve.*");
            default -> false;
        };
    }

    /**
     * 动态加载用户权限
     */
    public Collection<GrantedAuthority> loadUserPermissions(String username) {
        // 从数据库加载用户权限
        // 返回 GrantedAuthority 集合
        return permissionRepository.findByUsername(username);
    }
}
```

### 5.3 DynamicPermissionAuthorizationManager 使用方式

#### 方式 1：在 SecurityConfig 中注入使用

```java
package com.linsir.security.config;

import com.linsir.security.authorization.DynamicPermissionAuthorizationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security 配置 - 使用动态权限管理器
 */
@Configuration
public class SecurityConfig {

    @Autowired
    private DynamicPermissionAuthorizationManager dynamicPermissionManager;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // 公开资源
                .requestMatchers("/public/**", "/api/auth/**").permitAll()
                
                // 使用动态权限管理器（根据用户权限动态判断）
                .requestMatchers("/api/**").manager(dynamicPermissionManager)
                
                // 其他请求需要认证
                .anyRequest().authenticated()
            );
        
        return http.build();
    }
}
```

#### 方式 2：在方法级授权中使用

```java
package com.linsir.security.config;

import com.linsir.security.authorization.DynamicPermissionAuthorizationManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.method.AuthorizationManagerBeforeMethodInterceptor;

/**
 * 方法安全配置 - 使用动态权限管理器
 */
@Configuration
public class DynamicMethodSecurityConfig {

    @Bean
    public AuthorizationManagerBeforeMethodInterceptor dynamicPermissionInterceptor(
            DynamicPermissionAuthorizationManager dynamicPermissionManager) {
        
        // 创建前置拦截器，使用动态权限管理器
        return AuthorizationManagerBeforeMethodInterceptor.preAuthorize(
            dynamicPermissionManager
        );
    }
}
```

#### 方式 3：组合使用

```java
package com.linsir.security.config;

import com.linsir.security.authorization.DynamicPermissionAuthorizationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationManagers;
import org.springframework.security.authorization.AuthorityAuthorizationManager;

/**
 * 组合授权配置 - 动态权限 + 角色
 */
@Configuration
public class CompositeAuthorizationConfig {

    @Autowired
    private DynamicPermissionAuthorizationManager dynamicPermissionManager;

    /**
     * 需要同时满足：
     * 1. 动态权限检查
     * 2. 拥有 ADMIN 角色
     */
    @Bean
    public AuthorizationManager<Object> dynamicAndRoleManager() {
        return AuthorizationManagers.allOf(
            dynamicPermissionManager,  // 动态权限检查
            AuthorityAuthorizationManager.hasRole("ADMIN")  // 角色检查
        );
    }

    /**
     * 满足任一即可：
     * 1. 动态权限检查通过
     * 2. 拥有 ADMIN 或 MANAGER 角色
     */
    @Bean
    public AuthorizationManager<Object> dynamicOrRoleManager() {
        return AuthorizationManagers.anyOf(
            dynamicPermissionManager,
            AuthorityAuthorizationManager.hasAnyRole("ADMIN", "MANAGER")
        );
    }
}
```

#### 方式 4：在 SecurityConfig 中完整集成

```java
package com.linsir.security.config;

import com.linsir.security.authorization.DynamicPermissionAuthorizationManager;
import com.linsir.security.config.authorization.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security 配置 - 完整集成动态权限
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private DynamicPermissionAuthorizationManager dynamicPermissionManager;
    
    @Autowired
    private PermitAllAuthorizationManagerConfig permitAllConfig;
    
    @Autowired
    private AuthenticatedAuthorizationManagerConfig authenticatedConfig;
    
    @Autowired
    private UserRoleAuthorizationManagerConfig userRoleConfig;
    
    @Autowired
    private AdminRoleAuthorizationManagerConfig adminRoleConfig;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // 公开资源（允许所有访问）
                .requestMatchers("/public/**", "/static/**", "/api/auth/**")
                    .manager(permitAllConfig.permitAllAuthorizationManager(null))
                
                // 用户资源（需要 USER 角色）
                .requestMatchers("/user/**", "/api/user/**")
                    .manager(userRoleConfig.userRoleAuthorizationManager(null))
                
                // 管理员资源（需要 ADMIN 角色）
                .requestMatchers("/admin/**", "/api/admin/**")
                    .manager(adminRoleConfig.adminRoleAuthorizationManager(null))
                
                // API 资源（使用动态权限管理）
                // 根据用户的权限动态判断是否允许访问
                .requestMatchers("/api/**")
                    .manager(dynamicPermissionManager)
                
                // 其他请求需要认证
                .anyRequest()
                    .manager(authenticatedConfig.authenticatedAuthorizationManager(null))
            )
            
            // 其他配置
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form
                .loginProcessingUrl("/api/auth/login")
                .successHandler(customLoginSuccessHandler)
                .failureHandler(customLoginFailureHandler)
            )
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .logoutSuccessHandler(customLogoutSuccessHandler)
            );
        
        return http.build();
    }
}
```

---

## 六、组合授权策略

### 6.1 使用 AuthorizationManagers 组合

```java
package com.linsir.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationManagers;
import org.springframework.security.authorization.AuthorityAuthorizationManager;

/**
 * 组合授权配置
 */
@Configuration
public class CompositeAuthorizationConfig {

    /**
     * 需要同时满足多个条件
     * ADMIN 角色 + 内网 IP
     */
    @Bean
    public AuthorizationManager<Object> adminAndInternalIpManager() {
        return AuthorizationManagers.allOf(
            AuthorityAuthorizationManager.hasRole("ADMIN"),
            new IpAddressAuthorizationManager("192.168.1.0/24")
        );
    }

    /**
     * 满足任一条件即可
     * ADMIN 或 MANAGER 角色
     */
    @Bean
    public AuthorizationManager<Object> adminOrManagerManager() {
        return AuthorizationManagers.anyOf(
            AuthorityAuthorizationManager.hasRole("ADMIN"),
            AuthorityAuthorizationManager.hasRole("MANAGER")
        );
    }

    /**
     * 排除特定条件
     * 非禁用用户
     */
    @Bean
    public AuthorizationManager<Object> notDisabledManager() {
        return AuthorizationManagers.noneOf(
            new DisabledUserAuthorizationManager()
        );
    }
}
```

### 6.2 IP 地址授权管理器

```java
package com.linsir.security.config;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

/**
 * IP 地址授权管理器
 */
@Component
public class IpAddressAuthorizationManager implements AuthorizationManager<Object> {

    private final String allowedSubnet;

    public IpAddressAuthorizationManager(String allowedSubnet) {
        this.allowedSubnet = allowedSubnet;
    }

    @Override
    public AuthorizationDecision authorize(Supplier<Authentication> authentication,
                                           Object object) {
        // 获取客户端 IP
        String clientIp = getClientIp();
        
        // 检查 IP 是否在允许的网段内
        boolean allowed = isIpInSubnet(clientIp, allowedSubnet);
        
        return new AuthorizationDecision(allowed);
    }

    private String getClientIp() {
        // 从 RequestContextHolder 获取客户端 IP
        // 实现略
        return "192.168.1.100";
    }

    private boolean isIpInSubnet(String ip, String subnet) {
        // IP 网段匹配逻辑
        // 实现略
        return true;
    }
}
```

---

## 七、用户 - 角色 - 权限数据模型

### 7.1 数据库表结构

```sql
-- 用户表
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE
);

-- 角色表
CREATE TABLE sys_role (
    id BIGINT PRIMARY KEY,
    role_code VARCHAR(50) UNIQUE NOT NULL,  -- 如：ADMIN, MANAGER, USER
    role_name VARCHAR(100) NOT NULL,         -- 如：管理员，经理，用户
    description VARCHAR(200)
);

-- 权限表
CREATE TABLE sys_permission (
    id BIGINT PRIMARY KEY,
    permission_code VARCHAR(50) UNIQUE NOT NULL,  -- 如：USER_CREATE, USER_DELETE
    permission_name VARCHAR(100) NOT NULL,         -- 如：创建用户，删除用户
    resource_type VARCHAR(20),                     -- MENU, BUTTON, API
    parent_id BIGINT
);

-- 用户 - 角色关联表
CREATE TABLE sys_user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id)
);

-- 角色 - 权限关联表
CREATE TABLE sys_role_permission (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id)
);
```

### 7.2 实体类

```java
package com.linsir.security.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户实体
 */
@Entity
@Table(name = "sys_user")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String username;
    private String password;
    private Boolean enabled = true;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "sys_user_role",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
    
    // Getters and Setters
}
```

```java
package com.linsir.security.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * 角色实体
 */
@Entity
@Table(name = "sys_role")
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "role_code")
    private String roleCode;  // ADMIN, MANAGER, USER
    
    @Column(name = "role_name")
    private String roleName;  // 管理员，经理，用户
    
    private String description;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "sys_role_permission",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();
    
    // Getters and Setters
}
```

```java
package com.linsir.security.entity;

import jakarta.persistence.*;

/**
 * 权限实体
 */
@Entity
@Table(name = "sys_permission")
public class Permission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "permission_code")
    private String permissionCode;  // USER_CREATE, USER_DELETE
    
    @Column(name = "permission_name")
    private String permissionName;  // 创建用户，删除用户
    
    @Column(name = "resource_type")
    private String resourceType;    // MENU, BUTTON, API
    
    // Getters and Setters
}
```

### 7.3 UserDetailsService 实现

```java
package com.linsir.security.service;

import com.linsir.security.entity.User;
import com.linsir.security.entity.Role;
import com.linsir.security.entity.Permission;
import com.linsir.security.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义 UserDetailsService
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) 
            throws UsernameNotFoundException {
        
        // 从数据库加载用户
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("用户不存在：" + username));
        
        // 构建权限列表
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        
        // 添加角色权限
        for (Role role : user.getRoles()) {
            // 添加角色（自动添加 ROLE_ 前缀）
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleCode()));
            
            // 添加功能权限
            for (Permission permission : role.getPermissions()) {
                authorities.add(new SimpleGrantedAuthority(permission.getPermissionCode()));
            }
        }
        
        // 构建 Spring Security UserDetails
        return org.springframework.security.core.userdetails.User
            .withUsername(user.getUsername())
            .password(user.getPassword())
            .authorities(authorities)
            .disabled(!user.getEnabled())
            .build();
    }
}
```

---

## 八、最佳实践

### 8.1 角色命名规范

```java
// ✅ 推荐：使用大写字母 + 下划线
ROLE_ADMIN
ROLE_MANAGER
ROLE_USER

// ❌ 不推荐：小写或混合大小写
role_admin
Role_Admin
```

### 8.2 权限命名规范

```java
// ✅ 推荐：资源_操作 格式
USER_CREATE
USER_DELETE
USER_UPDATE
USER_VIEW
ORDER_CREATE
ORDER_APPROVE

// ❌ 不推荐：模糊命名
CREATE
DELETE
DO_SOMETHING
```

### 8.3 使用角色层次简化配置

```java
// ❌ 不好的做法：显式列出所有角色
@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('USER')")

// ✅ 好的做法：利用角色层次
@PreAuthorize("hasRole('ADMIN')")  // ADMIN 自动包含 MANAGER 和 USER
```

### 8.4 最小权限原则

```java
// ✅ 推荐：只授予必要的权限
@PreAuthorize("hasAuthority('USER_VIEW')")  // 只能查看

// ❌ 不推荐：过度授权
@PreAuthorize("hasRole('ADMIN')")  // 授予了过多权限
```

### 8.5 组合使用角色和权限

```java
// ✅ 推荐：角色 + 权限双重验证
@PreAuthorize("hasRole('USER') and hasAuthority('USER_CREATE')")

// 含义：必须是 USER 角色，且拥有 USER_CREATE 权限
```

---

## 九、测试验证

### 9.1 单元测试

```java
package com.linsir.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RBAC 授权测试
 */
@SpringBootTest
public class RBACAuthorizationTest {

    @Autowired
    private RoleHierarchy roleHierarchy;

    @Test
    public void testRoleHierarchy() {
        // 创建 ADMIN 角色的认证
        List<SimpleGrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_ADMIN")
        );
        
        Authentication authentication = 
            new UsernamePasswordAuthenticationToken("admin", "password", authorities);
        
        // 获取扩展后的权限（包含隐含角色）
        Collection<? extends GrantedAuthority> reachableAuthorities = 
            roleHierarchy.getReachableGrantedAuthorities(authorities);
        
        // 验证 ADMIN 包含所有下级角色
        assertTrue(reachableAuthorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
        assertTrue(reachableAuthorities.contains(new SimpleGrantedAuthority("ROLE_MANAGER")));
        assertTrue(reachableAuthorities.contains(new SimpleGrantedAuthority("ROLE_USER")));
        assertTrue(reachableAuthorities.contains(new SimpleGrantedAuthority("ROLE_GUEST")));
    }
}
```

### 9.2 集成测试

```java
package com.linsir.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web 授权集成测试
 */
@SpringBootTest
@AutoConfigureMockMvc
public class WebAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testAdminAccess() throws Exception {
        // ADMIN 用户可以访问所有资源
        mockMvc.perform(get("/admin/dashboard")
                .with(user("admin").roles("ADMIN")))
            .andExpect(status().isOk());
        
        mockMvc.perform(get("/user/profile")
                .with(user("admin").roles("ADMIN")))
            .andExpect(status().isOk());
    }

    @Test
    public void testUserAccess() throws Exception {
        // USER 用户只能访问用户资源
        mockMvc.perform(get("/user/profile")
                .with(user("user").roles("USER")))
            .andExpect(status().isOk());
        
        // 不能访问管理员资源
        mockMvc.perform(get("/admin/dashboard")
                .with(user("user").roles("USER")))
            .andExpect(status().isForbidden());
    }

    @Test
    public void testGuestAccess() throws Exception {
        // GUEST 用户只能访问公开资源
        mockMvc.perform(get("/public/info"))
            .andExpect(status().isOk());
        
        // 不能访问需要认证的资源
        mockMvc.perform(get("/user/profile")
                .with(user("guest").roles("GUEST")))
            .andExpect(status().isForbidden());
    }
}
```

---

## 十、总结

### 10.1 方案优势

1. **清晰的层次结构**：角色层次简化了权限配置
2. **灵活的扩展性**：支持动态权限和自定义授权逻辑
3. **细粒度控制**：支持角色和功能权限的组合
4. **易于维护**：配置集中，职责清晰

### 10.2 实施步骤

1. 配置角色层次结构（`RoleHierarchyConfig`）
2. 配置 AuthorizationManager 工厂（`AuthorizationManagerFactoryConfig`）
3. 创建各角色的 AuthorizationManager（`authorization` 包）
4. 配置 SecurityFilterChain（`SecurityConfig`）
5. 配置方法级授权（`MethodSecurityConfig`）
6. 实现 UserDetailsService 加载用户权限
7. 测试验证

### 10.3 注意事项

- 角色前缀统一使用 `ROLE_`
- 角色层次避免循环依赖
- 遵循最小权限原则
- 定期审计权限配置

---

## 参考资料

- [Spring Security 官方文档 - Authorization Architecture](https://docs.spring.io/spring-security/reference/servlet/authorization/architecture.html)
- [Spring Security 官方文档 - Method Security](https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html)
- [RBAC 模型介绍](https://en.wikipedia.org/wiki/Role-based_access_control)

---

**版本信息：**
- Spring Security 7.0+
- 最后更新：2026-04-05
