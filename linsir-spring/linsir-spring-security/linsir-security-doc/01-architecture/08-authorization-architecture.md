# Spring Security 授权架构指南

## 概述

本文档详细介绍了 Spring Security 的授权（Authorization）架构，包括核心组件、工作原理以及如何使用这些组件来控制对应用程序资源的访问。

---

## 核心概念

### 1. GrantedAuthority（授权权限）

**定义：**
`GrantedAuthority` 表示授予主体（principal）的权限。所有 `Authentication` 实现都存储一个 `GrantedAuthority` 对象列表。

**特点：**
- 由 `AuthenticationManager` 插入到 `Authentication` 对象中
- 由 `AuthorizationManager` 在做出授权决策时读取
- 接口只有一个方法：

```java
String getAuthority();
```

**实现类型：**

#### 简单权限 - SimpleGrantedAuthority
```java
// Spring Security 提供的唯一具体实现
GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
String authorityName = authority.getAuthority(); // 返回 "ROLE_USER"
```

#### 复杂权限
如果 `GrantedAuthority` 无法精确表示为 String，`getAuthority()` 应返回 `null`：

```java
public class ComplexGrantedAuthority implements GrantedAuthority {
    private List<String> operations;
    private Map<String, Integer> thresholds;
    
    @Override
    public String getAuthority() {
        // 无法用简单字符串表示，返回 null
        return null;
    }
    
    // 需要自定义 AuthorizationManager 来理解其内容
}
```

---

### 2. 角色前缀配置

默认情况下，基于角色的授权规则使用 `ROLE_` 作为前缀：

```java
// 如果授权规则要求角色 "USER"
// Spring Security 会查找返回 "ROLE_USER" 的 GrantedAuthority
```

**自定义前缀：**

```java
@Configuration
public class SecurityConfig {
    
    @Bean
    static GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("MYPREFIX_");
    }
}
```

**注意：** 使用 `static` 方法确保 Spring 在初始化方法安全配置类之前发布此 Bean。

---

## AuthorizationManager（授权管理器）

### 核心接口

`AuthorizationManager` 取代了旧的 `AccessDecisionManager` 和 `AccessDecisionVoter`，负责做出最终的访问控制决策。

**接口方法：**

```java
public interface AuthorizationManager<T> {
    
    // 授权决策
    AuthorizationResult authorize(Supplier<Authentication> authentication, Object secureObject);
    
    // 验证（授权失败时抛出 AccessDeniedException）
    default void verify(Supplier<Authentication> authentication, Object secureObject)
            throws AccessDeniedException {
        AuthorizationResult result = authorize(authentication, secureObject);
        if (Boolean.FALSE.equals(result.getDecision())) {
            throw new AccessDeniedException("Access Denied");
        }
    }
}
```

**授权决策结果：**

| 返回值 | 含义 |
|--------|------|
| `AuthorizationDecision(true)` | 允许访问 |
| `AuthorizationDecision(false)` | 拒绝访问 |
| `null` | 弃权（不做决策） |

### 使用示例

```java
@Component
public class CustomerAuthorizationManager implements AuthorizationManager<MethodInvocation> {
    
    @Override
    public AuthorizationResult authorize(Supplier<Authentication> authentication, 
                                         MethodInvocation methodInvocation) {
        // 获取方法参数中的 Customer 对象
        Object[] arguments = methodInvocation.getArguments();
        Customer customer = findCustomerArgument(arguments);
        
        if (customer == null) {
            return AuthorizationResult.abstain();
        }
        
        // 检查用户是否有权限操作该客户
        boolean hasPermission = checkPermission(
            authentication.get().getName(), 
            customer.getId()
        );
        
        return hasPermission 
            ? AuthorizationResult.granted() 
            : AuthorizationResult.denied();
    }
    
    private Customer findCustomerArgument(Object[] arguments) {
        for (Object arg : arguments) {
            if (arg instanceof Customer) {
                return (Customer) arg;
            }
        }
        return null;
    }
}
```

---

## 内置 AuthorizationManager 实现

### 1. AuthorityAuthorizationManager（最常用）

检查当前 `Authentication` 是否包含指定的权限：

```java
// 配置
AuthorityAuthorizationManager<Object> manager = 
    AuthorityAuthorizationManager.hasRole("ADMIN");

// 或者多个权限（满足任一即可）
AuthorityAuthorizationManager<Object> manager = 
    AuthorityAuthorizationManager.hasAnyRole("ADMIN", "MANAGER");

// 决策逻辑
// - 如果 Authentication 包含任何配置的权限 → 返回 positive AuthorizationDecision
// - 否则 → 返回 negative AuthorizationDecision
```

### 2. AuthenticatedAuthorizationManager

区分匿名、完全认证和记住我认证的用户：

```java
// 检查是否已认证（包括 remember-me）
AuthenticatedAuthorizationManager.authenticated()

// 检查是否完全认证（不包括 remember-me）
AuthenticatedAuthorizationManager.fullyAuthenticated()

// 检查是否是 remember-me 认证
AuthenticatedAuthorizationManager.rememberMe()

// 检查是否是匿名
AuthenticatedAuthorizationManager.anonymous()
```

**使用场景：**
- 允许记住我用户有限访问
- 要求完全认证才能执行敏感操作

---

## AuthorizationManagerFactory（工厂模式）

Spring Security 7.0 引入，用于创建通用的 `AuthorizationManager`：

```java
public interface AuthorizationManagerFactory<T> {
    AuthorizationManager<T> permitAll();
    AuthorizationManager<T> denyAll();
    AuthorizationManager<T> hasRole(String role);
    AuthorizationManager<T> hasAnyRole(String... roles);
    AuthorizationManager<T> hasAllRoles(String... roles);
    AuthorizationManager<T> hasAuthority(String authority);
    AuthorizationManager<T> hasAnyAuthority(String... authorities);
    AuthorizationManager<T> hasAllAuthorities(String... authorities);
    AuthorizationManager<T> authenticated();
    AuthorizationManager<T> fullyAuthenticated();
    AuthorizationManager<T> rememberMe();
    AuthorizationManager<T> anonymous();
}
```

**默认实现：** `DefaultAuthorizationManagerFactory`

**自定义工厂：**

```java
@Bean
<T> AuthorizationManagerFactory<T> authorizationManagerFactory() {
    DefaultAuthorizationManagerFactory<T> factory = 
        new DefaultAuthorizationManagerFactory<>();
    factory.setTrustResolver(getAuthenticationTrustResolver());
    factory.setRoleHierarchy(getRoleHierarchy());
    factory.setRolePrefix("role_");
    return factory;
}
```

---

## 角色层次结构（Hierarchical Roles）

### 概念

角色层次结构允许配置角色之间的包含关系。例如：`ROLE_ADMIN` 自动包含 `ROLE_USER` 的所有权限。

### 配置示例

```java
@Configuration
public class SecurityConfig {
    
    @Bean
    static RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withDefaultRolePrefix()
            .role("ADMIN").implies("STAFF")      // ADMIN 包含 STAFF
            .role("STAFF").implies("USER")       // STAFF 包含 USER
            .role("USER").implies("GUEST")       // USER 包含 GUEST
            .build();
    }
    
    // 如果使用方法安全，还需要配置
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

### 层次关系

```
ROLE_ADMIN ⇒ ROLE_STAFF ⇒ ROLE_USER ⇒ ROLE_GUEST
```

**效果：**
- 拥有 `ROLE_ADMIN` 的用户在安全约束评估时表现得好像拥有所有四个角色
- 简化访问控制配置
- 减少需要分配给用户的权限数量

---

## 组合 AuthorizationManager

使用 `AuthorizationManagers` 工具类组合多个管理器：

```java
// 需要同时满足多个条件
AuthorizationManager<Object> manager = AuthorizationManagers.allOf(
    AuthorityAuthorizationManager.hasRole("ADMIN"),
    IpAddressAuthorizationManager.isInSubnet("192.168.1.0/24")
);

// 满足任一条件即可
AuthorizationManager<Object> manager = AuthorizationManagers.anyOf(
    AuthorityAuthorizationManager.hasRole("ADMIN"),
    AuthorityAuthorizationManager.hasRole("SUPER_USER")
);

// 所有条件都不满足时才允许
AuthorizationManager<Object> manager = AuthorizationManagers.noneOf(
    AuthorityAuthorizationManager.hasRole("BANNED")
);
```

---

## 自定义 AuthorizationManager

实现业务特定的授权逻辑：

```java
@Component
public class BusinessLogicAuthorizationManager 
        implements AuthorizationManager<MethodInvocation> {
    
    private final PermissionRepository permissionRepository;
    
    @Override
    public AuthorizationResult authorize(Supplier<Authentication> authentication, 
                                         MethodInvocation invocation) {
        String username = authentication.get().getName();
        String operation = invocation.getMethod().getName();
        Object target = invocation.getThis();
        
        // 查询业务权限数据库
        boolean hasPermission = permissionRepository.hasPermission(
            username, 
            target.getClass().getSimpleName(), 
            operation
        );
        
        return hasPermission 
            ? AuthorizationResult.granted() 
            : AuthorizationResult.denied();
    }
}
```

---

## 适配器模式（迁移旧代码）

### 适配 AccessDecisionManager

```java
@Component
public class AccessDecisionManagerAuthorizationManagerAdapter 
        implements AuthorizationManager<Object> {
    
    private final AccessDecisionManager accessDecisionManager;
    private final SecurityMetadataSource securityMetadataSource;
    
    @Override
    public AuthorizationResult authorize(Supplier<Authentication> authentication, 
                                         Object object) {
        try {
            Collection<ConfigAttribute> attributes = 
                securityMetadataSource.getAttributes(object);
            accessDecisionManager.decide(authentication.get(), object, attributes);
            return AuthorizationResult.granted();
        } catch (AccessDeniedException ex) {
            return AuthorizationResult.denied();
        }
    }
    
    @Override
    public void verify(Supplier<Authentication> authentication, Object object) {
        Collection<ConfigAttribute> attributes = 
            securityMetadataSource.getAttributes(object);
        accessDecisionManager.decide(authentication.get(), object, attributes);
    }
}
```

### 适配 AccessDecisionVoter

```java
@Component
public class AccessDecisionVoterAuthorizationManagerAdapter 
        implements AuthorizationManager<Object> {
    
    private final AccessDecisionVoter accessDecisionVoter;
    private final SecurityMetadataSource securityMetadataSource;
    
    @Override
    public AuthorizationResult authorize(Supplier<Authentication> authentication, 
                                         Object object) {
        Collection<ConfigAttribute> attributes = 
            securityMetadataSource.getAttributes(object);
        int decision = accessDecisionVoter.vote(authentication.get(), object, attributes);
        
        switch (decision) {
            case AccessDecisionVoter.ACCESS_GRANTED:
                return AuthorizationResult.granted();
            case AccessDecisionVoter.ACCESS_DENIED:
                return AuthorizationResult.denied();
            default:
                return null; // 弃权
        }
    }
}
```

---

## 拦截器处理（Invocation Handling）

### 前置拦截（Before Invocation）

在方法执行前进行授权决策：

```java
// 方法安全
AuthorizationManagerBeforeMethodInterceptor.preAuthorize()
    .manager(myAuthorizationManager);

// Web 请求
http.authorizeHttpRequests(auth -> auth
    .requestMatchers("/admin/**").hasRole("ADMIN")
    .anyRequest().authenticated()
);
```

### 后置拦截（After Invocation）

在方法执行后决定返回值是否可以返回：

```java
AuthorizationManagerAfterMethodInterceptor.postFilter()
    .manager(myAuthorizationManager);
```

---

## 投票机制（Voting Mechanism）

### AccessDecisionVoter 投票结果

| 常量值 | 含义 |
|--------|------|
| `ACCESS_GRANTED` (1) | 赞成 |
| `ACCESS_DENIED` (-1) | 反对 |
| `ACCESS_ABSTAIN` (0) | 弃权 |

### AccessDecisionManager 实现策略

#### 1. AffirmativeBased（肯定策略）

```java
// 只要有一票赞成即通过（忽略反对票）
// 适用于：宽松策略，只要有人同意就行
```

#### 2. ConsensusBased（共识策略）

```java
// 根据多数票决定
// 适用于：民主决策
```

#### 3. UnanimousBased（一致策略）

```java
// 必须全票通过（有一票反对即拒绝）
// 适用于：严格策略，安全第一
```

### RoleVoter（角色投票器）

最常用的投票器，处理以 `ROLE_` 开头的配置属性：

```java
// 配置属性：ROLE_ADMIN
// 如果用户有 GrantedAuthority("ROLE_ADMIN") → 投赞成票
// 如果用户没有 → 投反对票
// 如果没有 ROLE_ 开头的属性 → 弃权
```

### AuthenticatedVoter（认证投票器）

处理认证状态相关的属性：

```java
// 配置属性：
// - IS_AUTHENTICATED_ANONYMOUSLY → 允许匿名访问
// - IS_AUTHENTICATED_REMEMBERED → 需要记住我或更好认证
// - IS_AUTHENTICATED_FULLY → 需要完全认证
```

---

## 实战示例

### 示例 1：基于 IP 地址的授权

```java
@Component
public class IpAddressAuthorizationManager 
        implements AuthorizationManager<HttpServletRequest> {
    
    @Override
    public AuthorizationResult authorize(Supplier<Authentication> authentication, 
                                         HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        
        // 只允许内网访问
        if (remoteAddr.startsWith("192.168.") || 
            remoteAddr.startsWith("10.")) {
            return AuthorizationResult.granted();
        }
        
        return AuthorizationResult.denied();
    }
}
```

### 示例 2：基于时间的授权

```java
@Component
public class TimeBasedAuthorizationManager 
        implements AuthorizationManager<MethodInvocation> {
    
    @Override
    public AuthorizationResult authorize(Supplier<Authentication> authentication, 
                                         MethodInvocation invocation) {
        LocalTime now = LocalTime.now();
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(18, 0);
        
        if (now.isAfter(startTime) && now.isBefore(endTime)) {
            return AuthorizationResult.granted();
        }
        
        return AuthorizationResult.denied();
    }
}
```

### 示例 3：基于数据所有权的授权

```java
@Component
public class DataOwnershipAuthorizationManager 
        implements AuthorizationManager<MethodInvocation> {
    
    private final UserRepository userRepository;
    
    @Override
    public AuthorizationResult authorize(Supplier<Authentication> authentication, 
                                         MethodInvocation invocation) {
        String username = authentication.get().getName();
        User currentUser = userRepository.findByUsername(username);
        
        // 获取方法参数中的资源 ID
        Long resourceId = extractResourceId(invocation.getArguments());
        Resource resource = resourceRepository.findById(resourceId);
        
        // 检查资源是否属于当前用户
        if (resource.getOwnerId().equals(currentUser.getId())) {
            return AuthorizationResult.granted();
        }
        
        return AuthorizationResult.denied();
    }
}
```

---

## 最佳实践

### 1. 使用角色层次结构简化配置

```java
// ❌ 不好的做法：每个权限都要配置多个角色
@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('SUPER_USER')")

// ✅ 好的做法：使用角色层次
// ROLE_ADMIN 自动包含 ROLE_MANAGER 和 ROLE_SUPER_USER
@PreAuthorize("hasRole('ADMIN')")
```

### 2. 组合使用多个 AuthorizationManager

```java
// 多重保护
AuthorizationManager<Object> manager = AuthorizationManagers.allOf(
    AuthorityAuthorizationManager.hasRole("ADMIN"),
    IpAddressAuthorizationManager.isInSubnet("192.168.1.0/24"),
    TimeBasedAuthorizationManager.isBusinessHours()
);
```

### 3. 自定义复杂权限判断

```java
// 对于复杂业务场景，实现自定义 AuthorizationManager
@Component
public class CustomAuthorizationManager 
        implements AuthorizationManager<MethodInvocation> {
    
    @Override
    public AuthorizationResult authorize(Supplier<Authentication> authentication, 
                                         MethodInvocation invocation) {
        // 实现复杂的业务授权逻辑
        // ...
    }
}
```

### 4. 使用工厂模式创建管理器

```java
// 利用 AuthorizationManagerFactory 创建标准管理器
@Autowired
private AuthorizationManagerFactory factory;

public void configureSecurity() {
    AuthorizationManager<Object> manager = factory.hasRole("ADMIN");
    // ...
}
```

---

## 迁移指南

### 从 AccessDecisionManager 迁移到 AuthorizationManager

```java
// 旧代码
public class MyAccessDecisionManager implements AccessDecisionManager {
    @Override
    public void decide(Authentication authentication, Object object,
                       Collection<ConfigAttribute> attrs) {
        // ...
    }
}

// 新代码
public class MyAuthorizationManager implements AuthorizationManager<Object> {
    @Override
    public AuthorizationResult authorize(Supplier<Authentication> authentication, 
                                         Object object) {
        // ...
        return AuthorizationResult.granted();
    }
}
```

---

## 参考资料

- [Spring Security 官方文档 - Authorization Architecture](https://docs.spring.io/spring-security/reference/servlet/authorization/architecture.html)
- [Spring Security 官方文档 - Method Security](https://docs.spring.io/spring-security/reference/servlet/authorization/method-security.html)
- [Spring Security 官方文档 - Web Security](https://docs.spring.io/spring-security/reference/servlet/authorization/web.html)

---

**版本信息：**
- Spring Security 7.0+
- 最后更新：2026-04-05
