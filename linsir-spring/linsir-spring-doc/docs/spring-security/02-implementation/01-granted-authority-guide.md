# GrantedAuthority 实现与配置指南

## 概述

`GrantedAuthority` 是 Spring Security 中表示权限的核心接口。它定义了授予用户（principal）的权限，由 `AuthenticationManager` 插入到 `Authentication` 对象中，并由 `AuthorizationManager` 在授权决策时读取。

---

## 接口定义

```java
package org.springframework.security.core;

public interface GrantedAuthority extends Serializable {
    /**
     * 获取权限的字符串表示
     * @return 权限名称，如果无法用字符串表示则返回 null
     */
    String getAuthority();
}
```

---

## 实现方式

### 1. SimpleGrantedAuthority（标准实现）

Spring Security 提供的唯一具体实现，适用于绝大多数场景。

#### 基本使用

```java
// 创建简单权限
GrantedAuthority roleUser = new SimpleGrantedAuthority("ROLE_USER");
GrantedAuthority roleAdmin = new SimpleGrantedAuthority("ROLE_ADMIN");

// 获取权限名称
String authority = roleUser.getAuthority(); // 返回 "ROLE_USER"
```

#### 在 UserDetailsService 中使用

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Override
    public UserDetails loadUserByUsername(String username) {
        // 方式 1：使用 roles() 方法（会自动添加 ROLE_ 前缀）
        return User.builder()
            .username("admin")
            .password("{bcrypt}$2a$10$...")
            .roles("USER", "ADMIN")  // 自动转换为 ROLE_USER, ROLE_ADMIN
            .build();
        
        // 方式 2：使用 authorities() 方法（需要手动添加 ROLE_ 前缀）
        return User.builder()
            .username("admin")
            .password("{bcrypt}$2a$10$...")
            .authorities("ROLE_USER", "ROLE_ADMIN")
            .build();
    }
}
```

#### 从数据库加载权限

```java
@Service
public class DatabaseUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PermissionRepository permissionRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) {
        UserEntity userEntity = userRepository.findByUsername(username);
        
        // 查询用户的权限列表
        List<PermissionEntity> permissions = 
            permissionRepository.findByUserId(userEntity.getId());
        
        // 转换为 GrantedAuthority 列表
        List<GrantedAuthority> authorities = permissions.stream()
            .map(permission -> new SimpleGrantedAuthority(permission.getCode()))
            .collect(Collectors.toList());
        
        return User.builder()
            .username(userEntity.getUsername())
            .password(userEntity.getPassword())
            .authorities(authorities)
            .accountExpired(!userEntity.isAccountExpired())
            .accountLocked(!userEntity.isLocked())
            .credentialsExpired(!userEntity.isCredentialsExpired())
            .disabled(!userEntity.isEnabled())
            .build();
    }
}
```

---

### 2. 自定义 GrantedAuthority 实现

当简单字符串无法完整表示权限时，需要实现自定义的 `GrantedAuthority`。

#### 场景：复杂权限（包含操作和资源）

```java
/**
 * 复杂权限实现
 * 包含操作类型和资源 ID
 */
public class ResourcePermissionAuthority implements GrantedAuthority {
    
    private final String operation;  // 操作：READ, WRITE, DELETE
    private final Long resourceId;   // 资源 ID
    private final String resourceType; // 资源类型：USER, ORDER, PRODUCT
    
    public ResourcePermissionAuthority(String operation, Long resourceId, String resourceType) {
        this.operation = operation;
        this.resourceId = resourceId;
        this.resourceType = resourceType;
    }
    
    @Override
    public String getAuthority() {
        // 无法用简单字符串表示，返回 null
        // 这表示需要自定义 AuthorizationManager 来理解此权限
        return null;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public Long getResourceId() {
        return resourceId;
    }
    
    public String getResourceType() {
        return resourceType;
    }
    
    /**
     * 判断是否包含某个权限
     */
    public boolean implies(String targetOperation, Long targetResourceId, String targetResourceType) {
        // 同类型资源
        if (!this.resourceType.equals(targetResourceType)) {
            return false;
        }
        
        // 资源 ID 匹配（-1 表示所有资源）
        if (this.resourceId != -1 && !this.resourceId.equals(targetResourceId)) {
            return false;
        }
        
        // 操作权限判断
        return this.operation.equals(targetOperation) || 
               this.operation.equals("ALL");
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResourcePermissionAuthority that = (ResourcePermissionAuthority) o;
        return Objects.equals(operation, that.operation) &&
               Objects.equals(resourceId, that.resourceId) &&
               Objects.equals(resourceType, that.resourceType);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(operation, resourceId, resourceType);
    }
}
```

#### 配合自定义 AuthorizationManager 使用

```java
@Component
public class ResourcePermissionAuthorizationManager 
        implements AuthorizationManager<MethodInvocation> {
    
    @Override
    public AuthorizationResult authorize(Supplier<Authentication> authentication, 
                                         MethodInvocation invocation) {
        Collection<? extends GrantedAuthority> authorities = 
            authentication.get().getAuthorities();
        
        // 提取方法参数中的资源信息
        ResourceTarget target = extractResourceTarget(invocation);
        
        // 检查是否有匹配的权限
        for (GrantedAuthority authority : authorities) {
            if (authority instanceof ResourcePermissionAuthority) {
                ResourcePermissionPermission = (ResourcePermissionAuthority) authority;
                if (permission.implies(
                        target.getOperation(), 
                        target.getResourceId(), 
                        target.getResourceType())) {
                    return AuthorizationResult.granted();
                }
            }
        }
        
        return AuthorizationResult.denied();
    }
    
    private ResourceTarget extractResourceTarget(MethodInvocation invocation) {
        // 从方法参数中提取资源目标信息
        // ...
        return new ResourceTarget("READ", 123L, "USER");
    }
}
```

---

### 3. 基于表达式的权限实现

```java
/**
 * 基于 SpEL 表达式的权限
 */
public class ExpressionAuthority implements GrantedAuthority {
    
    private final String expression;
    private final String description;
    
    public ExpressionAuthority(String expression, String description) {
        this.expression = expression;
        this.description = description;
    }
    
    @Override
    public String getAuthority() {
        // 返回表达式本身作为标识
        return "EXPRESSION:" + expression;
    }
    
    public String getExpression() {
        return expression;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 评估表达式
     */
    public boolean evaluate(SecurityContext context) {
        // 使用 Spring Expression 评估
        // ...
        return true;
    }
}
```

---

## 配置方式

### 1. 内存配置（测试用）

```java
@Configuration
public class InMemoryUserConfig {
    
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.builder()
            .username("admin")
            .password("{noop}admin123")
            .roles("ADMIN", "USER")
            .build();
        
        UserDetails user = User.builder()
            .username("user")
            .password("{noop}user123")
            .roles("USER")
            .build();
        
        return new InMemoryUserDetailsManager(admin, user);
    }
}
```

### 2. JDBC 配置（数据库）

```java
@Configuration
@EnableWebSecurity
public class JdbcSecurityConfig {
    
    @Autowired
    private DataSource dataSource;
    
    @Bean
    public JdbcUserDetailsManager userDetailsManager() {
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
        
        // 配置查询语句
        manager.setUsersByUsernameQuery(
            "select username, password, enabled from users where username = ?"
        );
        
        manager.setAuthoritiesByUsernameQuery(
            "select u.username, r.role_name " +
            "from users u " +
            "join user_roles ur on u.id = ur.user_id " +
            "join roles r on ur.role_id = r.id " +
            "where u.username = ?"
        );
        
        return manager;
    }
}
```

**数据库表结构：**

```sql
-- 用户表
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    account_expired BOOLEAN DEFAULT FALSE,
    account_locked BOOLEAN DEFAULT FALSE,
    credentials_expired BOOLEAN DEFAULT FALSE
);

-- 角色表
CREATE TABLE roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) UNIQUE NOT NULL
);

-- 用户角色关联表
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- 插入测试数据
INSERT INTO users (username, password, enabled) 
VALUES ('admin', '{bcrypt}$2a$10$...', TRUE);

INSERT INTO roles (role_name) VALUES ('ROLE_ADMIN'), ('ROLE_USER');

INSERT INTO user_roles (user_id, role_id) VALUES (1, 1), (1, 2);
```

### 3. 自定义 UserDetailsService（推荐）

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) 
            throws UsernameNotFoundException {
        
        // 1. 查询用户
        UserEntity user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("用户不存在：" + username));
        
        // 2. 查询用户角色
        List<RoleEntity> roles = roleRepository.findByUserId(user.getId());
        
        // 3. 转换为 GrantedAuthority
        List<GrantedAuthority> authorities = roles.stream()
            .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
            .collect(Collectors.toList());
        
        // 4. 构建 UserDetails
        return org.springframework.security.core.userdetails.User
            .withUsername(user.getUsername())
            .password(user.getPassword())
            .authorities(authorities)
            .disabled(!user.isEnabled())
            .accountExpired(user.isAccountExpired())
            .accountLocked(user.isAccountLocked())
            .credentialsExpired(user.isCredentialsExpired())
            .build();
    }
}
```

### 4. 从 OAuth2 加载权限

```java
@Service
public class OAuth2UserDetailsService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = userRequest.loadUser();
        
        // 从 OAuth2 提供商获取用户信息
        String email = oAuth2User.getAttribute("email");
        
        // 查询或创建本地用户
        UserEntity user = userRepository.findByEmail(email)
            .orElseGet(() -> createUserFromOAuth2(oAuth2User));
        
        // 构建权限列表
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        
        // 根据 OAuth2 属性添加额外权限
        if (oAuth2User.getAttribute("isAdmin")) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        
        return new DefaultOAuth2User(
            authorities,
            oAuth2User.getAttributes(),
            "sub" // name attribute
        );
    }
}
```

---

## 角色前缀配置

### 默认行为

Spring Security 默认使用 `ROLE_` 作为角色前缀：

```java
// 配置
.roles("USER", "ADMIN")

// 实际存储
// - ROLE_USER
// - ROLE_ADMIN

// 授权检查
@PreAuthorize("hasRole('ADMIN')")
// 会查找 GrantedAuthority 中包含 "ROLE_ADMIN" 的权限
```

### 自定义前缀

```java
@Configuration
public class SecurityConfig {
    
    @Bean
    static GrantedAuthorityDefaults grantedAuthorityDefaults() {
        // 使用自定义前缀，例如 "MYPREFIX_"
        return new GrantedAuthorityDefaults("MYPREFIX_");
    }
    
    // 或者移除前缀
    @Bean
    static GrantedAuthorityDefaults noPrefixDefaults() {
        return new GrantedAuthorityDefaults("");
    }
}
```

### 不使用前缀

```java
@Configuration
public class SecurityConfig {
    
    @Bean
    static GrantedAuthorityDefaults noPrefix() {
        return new GrantedAuthorityDefaults(""); // 空字符串
    }
}

// 使用
@Bean
SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(auth -> auth
        // 直接检查 "ADMIN"，而不是 "ROLE_ADMIN"
        .requestMatchers("/admin/**").hasAuthority("ADMIN")
        .anyRequest().authenticated()
    );
}
```

---

## 权限层次结构配置

### 配置角色继承

```java
@Configuration
public class RoleHierarchyConfig {
    
    @Bean
    static RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withDefaultRolePrefix()
            .role("ADMIN").implies("MANAGER")     // ADMIN 包含 MANAGER
            .role("MANAGER").implies("USER")      // MANAGER 包含 USER
            .role("USER").implies("GUEST")        // USER 包含 GUEST
            .build();
    }
}
```

### 方法安全中的角色层次

```java
@Configuration
@EnableMethodSecurity
public class MethodSecurityConfig {
    
    @Bean
    static RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withDefaultRolePrefix()
            .role("ADMIN").implies("USER")
            .build();
    }
    
    @Bean
    static MethodSecurityExpressionHandler methodSecurityExpressionHandler(
            RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler expressionHandler = 
            new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy);
        return expressionHandler;
    }
}
```

---

## 实战示例

### 示例 1：基于数据库的动态权限

```java
@Service
public class DynamicPermissionService {
    
    @Autowired
    private PermissionRepository permissionRepository;
    
    /**
     * 为用户动态添加权限
     */
    @Transactional
    public void grantPermission(Long userId, String permissionCode) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new UsernameNotFoundException("用户不存在"));
        
        PermissionEntity permission = permissionRepository.findByCode(permissionCode)
            .orElseGet(() -> {
                PermissionEntity newPerm = new PermissionEntity();
                newPerm.setCode(permissionCode);
                return permissionRepository.save(newPerm);
            });
        
        user.addPermission(permission);
        userRepository.save(user);
    }
    
    /**
     * 移除用户权限
     */
    @Transactional
    public void revokePermission(Long userId, String permissionCode) {
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new UsernameNotFoundException("用户不存在"));
        
        PermissionEntity permission = permissionRepository.findByCode(permissionCode)
            .orElseThrow(() -> new IllegalArgumentException("权限不存在"));
        
        user.removePermission(permission);
        userRepository.save(user);
    }
}

// 使用
@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private DynamicPermissionService permissionService;
    
    @Override
    public UserDetails loadUserByUsername(String username) {
        UserEntity user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("用户不存在"));
        
        // 动态加载所有权限
        List<GrantedAuthority> authorities = user.getPermissions().stream()
            .map(permission -> new SimpleGrantedAuthority(permission.getCode()))
            .collect(Collectors.toList());
        
        return User.builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .authorities(authorities)
            .build();
    }
}
```

### 示例 2：权限缓存优化

```java
@Service
public class CachedUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
    private CacheManager cacheManager;
    
    @Cacheable(value = "users", key = "#username")
    @Override
    public UserDetails loadUserByUsername(String username) 
            throws UsernameNotFoundException {
        return userDetailsService.loadUserByUsername(username);
    }
    
    /**
     * 清除用户缓存（当权限变更时调用）
     */
    @CacheEvict(value = "users", key = "#username")
    public void evictUserCache(String username) {
        // 缓存会自动清除
    }
}

// 配置缓存
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        cacheManager.setCacheNames(Arrays.asList("users"));
        return cacheManager;
    }
}
```

### 示例 3：权限变更时刷新 SecurityContext

```java
@Service
public class PermissionRefreshService {
    
    @Autowired
    private SecurityContextRepository securityContextRepository;
    
    /**
     * 当用户权限变更时，刷新其 SecurityContext
     */
    public void refreshUserAuthorities(String username) {
        // 1. 重新加载用户权限
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        
        // 2. 创建新的 Authentication
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
            userDetails,
            userDetails.getPassword(),
            userDetails.getAuthorities()
        );
        
        // 3. 更新 SecurityContext
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(newAuth);
        
        // 4. 保存回存储（如果是分布式 session）
        securityContextRepository.saveContext(context, null);
    }
}
```

---

## 最佳实践

### 1. 使用简单权限优先

```java
// ✅ 推荐：使用 SimpleGrantedAuthority
.authorities(new SimpleGrantedAuthority("ROLE_USER"))

// ❌ 不推荐：过度复杂的权限实现
.authorities(new ComplexCustomAuthority(...))
```

### 2. 统一权限命名规范

```java
// 角色权限：ROLE_ 前缀
"ROLE_ADMIN", "ROLE_USER", "ROLE_MANAGER"

// 功能权限：功能模块前缀
"USER_CREATE", "USER_EDIT", "USER_DELETE"
"ORDER_VIEW", "ORDER_APPROVE"

// 数据权限：资源类型前缀
"DATA_DEPT_1", "DATA_DEPT_2"
```

### 3. 权限与角色分离

```java
// 角色：表示用户在组织中的位置
GrantedAuthority role = new SimpleGrantedAuthority("ROLE_MANAGER");

// 权限：表示具体操作能力
GrantedAuthority permission = new SimpleGrantedAuthority("USER_DELETE");

// 组合使用
User.builder()
    .roles("MANAGER")  // 角色
    .authorities("USER_DELETE", "ORDER_APPROVE")  // 权限
    .build();
```

### 4. 使用枚举定义权限

```java
public enum AppPermission {
    USER_CREATE("user:create"),
    USER_EDIT("user:edit"),
    USER_DELETE("user:delete"),
    ORDER_VIEW("order:view"),
    ORDER_APPROVE("order:approve");
    
    private final String code;
    
    AppPermission(String code) {
        this.code = code;
    }
    
    public String getCode() {
        return code;
    }
}

// 使用
.authorities(
    new SimpleGrantedAuthority(AppPermission.USER_CREATE.getCode()),
    new SimpleGrantedAuthority(AppPermission.USER_EDIT.getCode())
)
```

### 5. 权限变更监听

```java
@Component
public class PermissionChangeListener {
    
    @EventListener
    public void onPermissionChanged(PermissionChangedEvent event) {
        // 清除缓存
        userDetailsService.evictUserCache(event.getUsername());
        
        // 通知其他服务
        eventPublisher.publishEvent(new AuthorityRefreshEvent(event.getUserId()));
    }
}
```

---

## 常见问题

### Q1: `hasRole()` 和 `hasAuthority()` 的区别？

```java
// hasRole("ADMIN") 
// 会自动添加 ROLE_ 前缀，实际检查 "ROLE_ADMIN"

// hasAuthority("ADMIN")
// 直接检查 "ADMIN"，不添加前缀

// 推荐：
// - 使用 roles() 配置角色，配合 hasRole() 检查
// - 使用 authorities() 配置权限，配合 hasAuthority() 检查
```

### Q2: 如何动态添加/删除权限？

```java
// 1. 更新数据库
permissionRepository.save(newPermission);

// 2. 清除缓存
userDetailsService.evictUserCache(username);

// 3. 刷新 SecurityContext（如果用户已登录）
permissionRefreshService.refreshUserAuthorities(username);
```

### Q3: 如何实现数据级权限？

```java
// 使用复杂权限实现
public class DataScopeAuthority implements GrantedAuthority {
    private final Long deptId;
    private final List<Long> projectIds;
    
    @Override
    public String getAuthority() {
        return null; // 复杂权限，返回 null
    }
    
    // 需要自定义 AuthorizationManager 来检查
}
```

---

## 参考资料

- [Spring Security 官方文档 - GrantedAuthority](https://docs.spring.io/spring-security/reference/servlet/authorization/architecture.html#objects-grantedauthority)
- [Spring Security 官方文档 - UserDetailsService](https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/core.html#userdetails-service)
- [Spring Security 官方文档 - Role Hierarchy](https://docs.spring.io/spring-security/reference/servlet/authorization/architecture.html#role-hierarchy)

---

**版本信息：**
- Spring Security 7.0+
- 最后更新：2026-04-05
