# AuthorizationManagerFactory 配置选项详解

## 概述

`AuthorizationManagerFactory` 是 Spring Security 7.0 引入的统一授权管理工厂接口，用于创建各种 `AuthorizationManager` 实例。通过配置工厂，可以统一管理角色前缀、角色层次结构、信任解析器等授权相关的全局设置。

---

## 核心接口

```java
public interface AuthorizationManagerFactory<T> {
    // 创建允许所有的管理器
    AuthorizationManager<T> permitAll();
    
    // 创建拒绝所有的管理器
    AuthorizationManager<T> denyAll();
    
    // 创建检查角色的管理器
    AuthorizationManager<T> hasRole(String role);
    
    // 创建检查任一角色的管理器
    AuthorizationManager<T> hasAnyRole(String... roles);
    
    // 创建检查所有角色的管理器
    AuthorizationManager<T> hasAllRoles(String... roles);
    
    // 创建检查权限的管理器
    AuthorizationManager<T> hasAuthority(String authority);
    
    // 创建检查任一权限的管理器
    AuthorizationManager<T> hasAnyAuthority(String... authorities);
    
    // 创建检查所有权限的管理器
    AuthorizationManager<T> hasAllAuthorities(String... authorities);
    
    // 创建检查已认证的管理器
    AuthorizationManager<T> authenticated();
    
    // 创建检查完全认证的管理器
    AuthorizationManager<T> fullyAuthenticated();
    
    // 创建检查 remember-me 的管理器
    AuthorizationManager<T> rememberMe();
    
    // 创建检查匿名的管理器
    AuthorizationManager<T> anonymous();
}
```

---

## 可配置项详解

### 1. 角色前缀（Role Prefix）

**作用：** 配置角色的前缀，默认为 `ROLE_`

**配置方法：**
```java
factory.setRolePrefix("ROLE_");
```

**示例：**
```java
@Configuration
public class AuthorizationManagerFactoryConfig {
    
    @Bean
    public AuthorizationManagerFactory<Object> authorizationManagerFactory() {
        DefaultAuthorizationManagerFactory<Object> factory = 
            new DefaultAuthorizationManagerFactory<>();
        
        // 使用默认前缀 ROLE_
        factory.setRolePrefix("ROLE_");
        
        // 或者自定义前缀
        // factory.setRolePrefix("MY_ROLE_");
        
        // 或者不使用前缀
        // factory.setRolePrefix("");
        
        return factory;
    }
}
```

**影响范围：**
- `hasRole("ADMIN")` → 实际检查 `ROLE_ADMIN`（使用默认前缀）
- `hasRole("ADMIN")` → 实际检查 `MY_ROLE_ADMIN`（使用自定义前缀）
- `hasRole("ADMIN")` → 实际检查 `ADMIN`（无前缀）

**最佳实践：**
```java
// ✅ 推荐：保持默认 ROLE_ 前缀
factory.setRolePrefix("ROLE_");

// ✅ 推荐：统一使用空字符串前缀（如果项目中没有 ROLE_ 前缀）
factory.setRolePrefix("");

// ❌ 不推荐：使用非标准前缀，增加理解成本
factory.setRolePrefix("CUSTOM_PREFIX_");
```

---

### 2. 角色层次结构（Role Hierarchy）

**作用：** 配置角色之间的继承关系，简化授权配置

**配置方法：**
```java
factory.setRoleHierarchy(roleHierarchy);
```

**示例：**
```java
@Configuration
public class AuthorizationManagerFactoryConfig {
    
    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withDefaultRolePrefix()
            .role("ADMIN").implies("MANAGER")    // ADMIN 包含 MANAGER
            .role("MANAGER").implies("USER")     // MANAGER 包含 USER
            .role("USER").implies("GUEST")       // USER 包含 GUEST
            .build();
    }
    
    @Bean
    public AuthorizationManagerFactory<Object> authorizationManagerFactory(
            RoleHierarchy roleHierarchy) {
        DefaultAuthorizationManagerFactory<Object> factory = 
            new DefaultAuthorizationManagerFactory<>();
        
        // 配置角色层次结构
        factory.setRoleHierarchy(roleHierarchy);
        
        return factory;
    }
}
```

**效果：**
- 拥有 `ROLE_ADMIN` 的用户自动拥有 `ROLE_MANAGER`、`ROLE_USER`、`ROLE_GUEST` 权限
- `hasRole("MANAGER")` 对 ADMIN 用户返回 `true`
- `hasRole("USER")` 对 ADMIN 和 MANAGER 用户都返回 `true`

**复杂层次结构示例：**
```java
@Bean
public RoleHierarchy roleHierarchy() {
    return RoleHierarchyImpl.withDefaultRolePrefix()
        // 管理员层级
        .role("SUPER_ADMIN").implies("ADMIN")
        .role("ADMIN").implies("MODERATOR")
        
        // 用户层级
        .role("MODERATOR").implies("USER")
        .role("USER").implies("GUEST")
        
        // 特殊角色
        .role("ADMIN").implies("EDITOR")
        .role("EDITOR").implies("WRITER")
        .build();
}
```

**层次结构图：**
```
SUPER_ADMIN
    └── ADMIN
            ├── MODERATOR
            │       └── USER
            │               └── GUEST
            └── EDITOR
                    └── WRITER
```

---

### 3. 认证信任解析器（AuthenticationTrustResolver）

**作用：** 配置认证信任解析器，用于判断认证状态（匿名、完全认证、remember-me）

**配置方法：**
```java
factory.setTrustResolver(authenticationTrustResolver);
```

**示例：**
```java
@Configuration
public class AuthorizationManagerFactoryConfig {
    
    @Bean
    public AuthorizationManagerFactory<Object> authorizationManagerFactory() {
        DefaultAuthorizationManagerFactory<Object> factory = 
            new DefaultAuthorizationManagerFactory<>();
        
        // 配置认证信任解析器
        factory.setTrustResolver(new AuthenticationTrustResolverImpl());
        
        return factory;
    }
}
```

**信任解析器类型：**

#### AuthenticationTrustResolverImpl（标准实现）
```java
@Bean
public AuthenticationTrustResolver trustResolver() {
    return new AuthenticationTrustResolverImpl(
        AnonymousAuthenticationToken.class,
        RememberMeAuthenticationToken.class
    );
}
```

**判断逻辑：**
- `anonymous()` - 检查是否是 `AnonymousAuthenticationToken`
- `rememberMe()` - 检查是否是 `RememberMeAuthenticationToken`
- `authenticated()` - 检查是否不是匿名
- `fullyAuthenticated()` - 检查是否既不是匿名也不是 remember-me

**自定义信任解析器：**
```java
public class CustomTrustResolver implements AuthenticationTrustResolver {
    
    @Override
    public boolean isAnonymous(Class<? extends Authentication> authentication) {
        // 自定义匿名判断逻辑
        return authentication.equals(CustomAnonymousToken.class);
    }
    
    @Override
    public boolean isRememberMe(Class<? extends Authentication> authentication) {
        // 自定义 remember-me 判断逻辑
        return authentication.equals(CustomRememberMeToken.class);
    }
    
    @Override
    public boolean isFullyAuthenticated(Class<? extends Authentication> authentication) {
        // 自定义完全认证判断逻辑
        return !isAnonymous(authentication) && !isRememberMe(authentication);
    }
}
```

---

### 4. 组合配置（推荐方式）

**完整配置示例：**
```java
@Configuration
public class AuthorizationManagerFactoryConfig {
    
    /**
     * 配置角色层次结构
     */
    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withDefaultRolePrefix()
            .role("SUPER_ADMIN").implies("ADMIN")
            .role("ADMIN").implies("MODERATOR")
            .role("MODERATOR").implies("USER")
            .role("USER").implies("GUEST")
            .build();
    }
    
    /**
     * 配置认证信任解析器
     */
    @Bean
    public AuthenticationTrustResolver trustResolver() {
        return new AuthenticationTrustResolverImpl(
            AnonymousAuthenticationToken.class,
            RememberMeAuthenticationToken.class
        );
    }
    
    /**
     * 配置 AuthorizationManagerFactory
     */
    @Bean
    public AuthorizationManagerFactory<Object> authorizationManagerFactory(
            RoleHierarchy roleHierarchy,
            AuthenticationTrustResolver trustResolver) {
        
        DefaultAuthorizationManagerFactory<Object> factory = 
            new DefaultAuthorizationManagerFactory<>();
        
        // 1. 设置角色前缀
        factory.setRolePrefix("ROLE_");
        
        // 2. 设置角色层次结构
        factory.setRoleHierarchy(roleHierarchy);
        
        // 3. 设置认证信任解析器
        factory.setTrustResolver(trustResolver);
        
        return factory;
    }
}
```

---

## 使用场景

### 场景 1：Web 请求授权

```java
@Configuration
public class SecurityConfig {
    
    @Autowired
    private AuthorizationManagerFactory<Object> factory;
    
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
            // 允许所有
            .requestMatchers("/public/**").manager(factory.permitAll())
            
            // 角色检查（使用角色层次）
            .requestMatchers("/admin/**").manager(factory.hasRole("ADMIN"))
            .requestMatchers("/moderator/**").manager(factory.hasRole("MODERATOR"))
            
            // 权限检查
            .requestMatchers("/api/user/create").manager(factory.hasAuthority("USER_CREATE"))
            
            // 认证状态检查
            .requestMatchers("/user/**").manager(factory.authenticated())
            .requestMatchers("/premium/**").manager(factory.fullyAuthenticated())
            
            .anyRequest().authenticated()
        );
        
        return http.build();
    }
}
```

### 场景 2：方法级别授权

```java
@Service
public class UserService {
    
    // 使用 @PreAuthorize 注解（需要 @EnableMethodSecurity）
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(Long userId) {
        // 删除用户
    }
    
    @PreAuthorize("hasAuthority('USER_CREATE')")
    public void createUser(User user) {
        // 创建用户
    }
    
    @PostAuthorize("returnObject.owner == authentication.name")
    public Document getDocument(Long id) {
        // 获取文档
        return new Document(id, "owner");
    }
}
```

### 场景 3：动态权限检查

```java
@Service
public class AuthorizationService {
    
    @Autowired
    private AuthorizationManagerFactory<Object> factory;
    
    /**
     * 检查用户是否有指定角色
     */
    public boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthorizationManager<Object> manager = factory.hasRole(role);
        return manager.authorize(() -> authentication, new Object()).isGranted();
    }
    
    /**
     * 检查用户是否有指定权限
     */
    public boolean hasAuthority(String authority) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthorizationManager<Object> manager = factory.hasAuthority(authority);
        return manager.authorize(() -> authentication, new Object()).isGranted();
    }
    
    /**
     * 检查用户是否完全认证
     */
    public boolean isFullyAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthorizationManager<Object> manager = factory.fullyAuthenticated();
        return manager.authorize(() -> authentication, new Object()).isGranted();
    }
}
```

---

## 配置对比表

| 配置项 | 方法 | 默认值 | 影响范围 | 推荐配置 |
|--------|------|--------|----------|----------|
| 角色前缀 | `setRolePrefix(String)` | `ROLE_` | `hasRole()`, `hasAnyRole()`, `hasAllRoles()` | 保持默认或设置为空字符串 |
| 角色层次结构 | `setRoleHierarchy(RoleHierarchy)` | 无 | 所有角色检查方法 | 根据业务需求配置 |
| 认证信任解析器 | `setTrustResolver(AuthenticationTrustResolver)` | `AuthenticationTrustResolverImpl` | `authenticated()`, `fullyAuthenticated()`, `rememberMe()`, `anonymous()` | 使用默认实现即可 |

---

## 配置示例集合

### 最小化配置（仅使用默认值）

```java
@Configuration
public class MinimalConfig {
    
    @Bean
    public AuthorizationManagerFactory<Object> authorizationManagerFactory() {
        return new DefaultAuthorizationManagerFactory<>();
    }
}
```

### 标准配置（推荐）

```java
@Configuration
public class StandardConfig {
    
    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withDefaultRolePrefix()
            .role("ADMIN").implies("USER")
            .build();
    }
    
    @Bean
    public AuthorizationManagerFactory<Object> authorizationManagerFactory(
            RoleHierarchy roleHierarchy) {
        DefaultAuthorizationManagerFactory<Object> factory = 
            new DefaultAuthorizationManagerFactory<>();
        factory.setRoleHierarchy(roleHierarchy);
        return factory;
    }
}
```

### 完整配置（所有选项）

```java
@Configuration
public class FullConfig {
    
    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withDefaultRolePrefix()
            .role("SUPER_ADMIN").implies("ADMIN")
            .role("ADMIN").implies("MODERATOR")
            .role("MODERATOR").implies("USER")
            .role("USER").implies("GUEST")
            .build();
    }
    
    @Bean
    public AuthenticationTrustResolver trustResolver() {
        return new AuthenticationTrustResolverImpl(
            AnonymousAuthenticationToken.class,
            RememberMeAuthenticationToken.class
        );
    }
    
    @Bean
    public AuthorizationManagerFactory<Object> authorizationManagerFactory(
            RoleHierarchy roleHierarchy,
            AuthenticationTrustResolver trustResolver) {
        
        DefaultAuthorizationManagerFactory<Object> factory = 
            new DefaultAuthorizationManagerFactory<>();
        
        factory.setRolePrefix("ROLE_");
        factory.setRoleHierarchy(roleHierarchy);
        factory.setTrustResolver(trustResolver);
        
        return factory;
    }
}
```

### 自定义前缀配置

```java
@Configuration
public class CustomPrefixConfig {
    
    @Bean
    public AuthorizationManagerFactory<Object> authorizationManagerFactory() {
        DefaultAuthorizationManagerFactory<Object> factory = 
            new DefaultAuthorizationManagerFactory<>();
        
        // 不使用前缀
        factory.setRolePrefix("");
        
        return factory;
    }
}
```

**使用效果：**
```java
// 配置前缀为空
factory.setRolePrefix("");

// 检查角色
.hasRole("ADMIN")  // 实际检查 "ADMIN"，而不是 "ROLE_ADMIN"
```

---

## 最佳实践

### 1. 使用角色层次结构简化配置

```java
// ✅ 推荐：使用角色层次
@Bean
public RoleHierarchy roleHierarchy() {
    return RoleHierarchyImpl.withDefaultRolePrefix()
        .role("ADMIN").implies("USER")
        .build();
}

// ❌ 不推荐：在每个检查中重复配置
.hasRole("ADMIN") or hasRole("USER")
```

### 2. 保持默认角色前缀

```java
// ✅ 推荐：保持默认
factory.setRolePrefix("ROLE_");

// ❌ 不推荐：使用非标准前缀
factory.setRolePrefix("CUSTOM_");
```

### 3. 使用 Bean 依赖注入

```java
// ✅ 推荐：通过 Bean 依赖注入
@Bean
public AuthorizationManagerFactory<Object> authorizationManagerFactory(
        RoleHierarchy roleHierarchy) {
    DefaultAuthorizationManagerFactory<Object> factory = 
        new DefaultAuthorizationManagerFactory<>();
    factory.setRoleHierarchy(roleHierarchy);
    return factory;
}

// ❌ 不推荐：在配置类内部创建
@Configuration
public class Config {
    private RoleHierarchy roleHierarchy = RoleHierarchyImpl.withDefaultRolePrefix()...;
}
```

### 4. 分离配置职责

```java
// ✅ 推荐：分离配置
@Configuration
public class RoleHierarchyConfig {
    @Bean
    public RoleHierarchy roleHierarchy() { ... }
}

@Configuration
public class AuthorizationManagerFactoryConfig {
    @Bean
    public AuthorizationManagerFactory<Object> authorizationManagerFactory(
            RoleHierarchy roleHierarchy) { ... }
}

// ❌ 不推荐：所有配置在一个类
@Configuration
public class AllInOneConfig {
    // 所有配置都在这里
}
```

### 5. 使用 Builder 模式（如果支持）

```java
// ✅ 推荐：使用 Builder 模式
@Bean
public RoleHierarchy roleHierarchy() {
    return RoleHierarchyImpl.withDefaultRolePrefix()
        .role("ADMIN").implies("USER")
        .build();
}

// ❌ 不推荐：手动构建
@Bean
public RoleHierarchy roleHierarchy() {
    Map<Role, List<Role>> hierarchy = new HashMap<>();
    // ... 手动构建层次结构
    return new RoleHierarchyImpl(hierarchy);
}
```

---

## 常见问题

### Q1: 如何禁用角色前缀？

```java
@Bean
public AuthorizationManagerFactory<Object> authorizationManagerFactory() {
    DefaultAuthorizationManagerFactory<Object> factory = 
        new DefaultAuthorizationManagerFactory<>();
    factory.setRolePrefix("");
    return factory;
}
```

### Q2: 角色层次结构是否支持循环依赖？

**不支持**。角色层次结构不能形成循环，否则会导致无限递归。

```java
// ❌ 错误：循环依赖
.role("A").implies("B")
.role("B").implies("A")  // 会导致异常

// ✅ 正确：树形结构
.role("A").implies("B")
.role("B").implies("C")
```

### Q3: 如何自定义认证信任解析器？

```java
@Bean
public AuthenticationTrustResolver trustResolver() {
    return new AuthenticationTrustResolverImpl(
        CustomAnonymousToken.class,  // 自定义匿名 Token
        CustomRememberMeToken.class   // 自定义 RememberMe Token
    );
}
```

### Q4: 配置何时生效？

配置在 `AuthorizationManagerFactory` Bean 创建时生效，所有通过该工厂创建的 `AuthorizationManager` 都会使用这些配置。

```java
// 配置工厂
@Bean
public AuthorizationManagerFactory<Object> authorizationManagerFactory() {
    DefaultAuthorizationManagerFactory<Object> factory = 
        new DefaultAuthorizationManagerFactory<>();
    factory.setRolePrefix("CUSTOM_");  // 配置立即生效
    return factory;
}

// 使用时，配置已生效
@Autowired
private AuthorizationManagerFactory<Object> factory;

public void check() {
    AuthorizationManager<Object> manager = factory.hasRole("ADMIN");
    // 实际检查 "CUSTOM_ADMIN"
}
```

---

## 参考资料

- [Spring Security 官方文档 - AuthorizationManagerFactory](https://docs.spring.io/spring-security/reference/servlet/authorization/architecture.html#authorization-manager-factory)
- [Spring Security 官方文档 - Role Hierarchy](https://docs.spring.io/spring-security/reference/servlet/authorization/architecture.html#role-hierarchy)
- [Spring Security 官方文档 - Method Security](https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html)
- [DefaultAuthorizationManagerFactory API](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/authorization/DefaultAuthorizationManagerFactory.html)

---

**版本信息：**
- Spring Security 7.0+
- 最后更新：2026-04-05
