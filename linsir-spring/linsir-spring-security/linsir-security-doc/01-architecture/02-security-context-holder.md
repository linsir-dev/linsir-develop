# SecurityContextHolder 详解

> **版本说明**：本文档基于 **Spring Security 7.0** 编写。
> 
> **7.0 主要变化**：
> - `SecurityContextPersistenceFilter` 已被 `SecurityContextHolderFilter` 取代
> - `requireExplicitSave(false)` 配置已移除，默认自动保存 SecurityContext
> - 过滤器链顺序有所调整

## 概述

`SecurityContextHolder` 是 Spring Security 认证模型的核心，它负责存储当前已认证用户的详细信息。

```
┌─────────────────────────────────────────────────────────────┐
│                   SecurityContextHolder                      │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  ThreadLocal<SecurityContext>  (默认策略)              │  │
│  │                                                       │  │
│  │  存储当前线程的安全上下文                               │  │
│  │  每个线程独立，互不干扰                                 │  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

## 核心职责

1. **存储安全上下文**：包含当前用户的 `SecurityContext`
2. **线程隔离**：默认使用 `ThreadLocal` 存储，确保线程安全
3. **全局访问**：在任何地方都可以通过静态方法获取当前认证信息

## 存储策略

SecurityContextHolder 支持三种存储策略：

| 策略 | 模式 | 适用场景 |
|------|------|----------|
| **ThreadLocal** | `MODE_THREADLOCAL` | Web 应用（默认）|
| **InheritableThreadLocal** | `MODE_INHERITABLETHREADLOCAL` | 子线程继承父线程安全上下文 |
| **Global** | `MODE_GLOBAL` | 独立应用（如 Swing 客户端）|

### 策略配置方式

```java
// 方式1：系统属性
System.setProperty("spring.security.strategy", "MODE_GLOBAL");

// 方式2：静态方法
SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_GLOBAL);
```

## 基本操作

### 设置认证信息

```java
// 创建空的安全上下文
SecurityContext context = SecurityContextHolder.createEmptyContext();

// 创建认证对象
Authentication authentication = new UsernamePasswordAuthenticationToken(
    userDetails,           // 主体（用户信息）
    password,              // 凭证（密码）
    AuthorityUtils.createAuthorityList("ROLE_USER")  // 权限
);

// 设置认证信息到上下文
context.setAuthentication(authentication);

// 设置上下文到 SecurityContextHolder
SecurityContextHolder.setContext(context);
```

### Web 应用中安全上下文的产生

在 Web 应用中，安全上下文不是手动创建的，而是由 Spring Security 的过滤器链自动管理：

> **Spring Security 7.0 变化**：`SecurityContextPersistenceFilter` 已被 `SecurityContextHolderFilter` 取代。
> 新过滤器默认自动保存 SecurityContext，无需显式配置 `requireExplicitSave(false)`。

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    Web 应用中 SecurityContext 产生流程                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  1. 用户请求进入                                                             │
│       │                                                                      │
│       ▼                                                                      │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │ 2. SecurityContextHolderFilter  ★ 7.0 新过滤器                      │    │
│  │    - 检查 Session 中是否存在 SecurityContext                         │    │
│  │    - 如果存在：从 Session 恢复并设置到 SecurityContextHolder         │    │
│  │    - 如果不存在：调用 createEmptyContext() 创建新的空上下文          │    │
│  │                                                                     │    │
│  │    SecurityContext context = SecurityContextHolder.createEmptyContext();│  │
│  │    SecurityContextHolder.setContext(context);                       │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│       │                                                                      │
│       ▼                                                                      │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │ 3. 认证过滤器（如 UsernamePasswordAuthenticationFilter）             │    │
│  │    - 如果是登录请求，执行认证                                        │    │
│  │    - 认证成功后，创建已认证的 Authentication 对象                    │    │
│  │    - 设置到 SecurityContext                                          │    │
│  │                                                                     │    │
│  │    context.setAuthentication(authentication);                       │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│       │                                                                      │
│       ▼                                                                      │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │ 4. 请求处理完成，返回响应                                            │    │
│  │                                                                     │    │
│  │    SecurityContextHolderFilter 自动保存：  ★ 7.0 自动保存           │    │
│  │    - 将 SecurityContext 保存到 Session（如果是新的或已修改）         │    │
│  │    - 调用 SecurityContextHolder.clearContext() 清理当前线程         │    │
│  └─────────────────────────────────────────────────────────────────────┘    │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

#### 核心组件说明

**SecurityContextPersistenceFilter** - 负责上下文的加载和保存：

```java
public class SecurityContextPersistenceFilter extends GenericFilterBean {
    
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        
        // 1. 从 Session 加载 SecurityContext（如果不存在则创建空的）
        SecurityContext contextBeforeChainExecution = repo.loadContext(holder);
        
        // 2. 设置到 SecurityContextHolder
        SecurityContextHolder.setContext(contextBeforeChainExecution);
        
        try {
            // 3. 继续执行过滤器链
            chain.doFilter(request, response);
        } finally {
            // 4. 请求结束后，保存到 Session 并清理
            SecurityContext contextAfterChainExecution = SecurityContextHolder.getContext();
            repo.saveContext(contextAfterChainExecution, holder);
            SecurityContextHolder.clearContext();
        }
    }
}
```

**HttpSessionSecurityContextRepository** - 负责与 Session 交互：

```java
public class HttpSessionSecurityContextRepository implements SecurityContextRepository {
    
    public static final String SPRING_SECURITY_CONTEXT_KEY = "SPRING_SECURITY_CONTEXT";
    
    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        HttpServletRequest request = requestResponseHolder.getRequest();
        
        // 从 Session 获取
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object contextFromSession = session.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
            if (contextFromSession != null) {
                return (SecurityContext) contextFromSession;  // 恢复已有上下文
            }
        }
        
        // 没有则创建空的
        return generateNewContext();  // 内部调用 createEmptyContext()
    }
    
    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, 
                           HttpServletResponse response) {
        // 保存到 Session
        HttpSession session = request.getSession();
        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, context);
    }
}
```

**认证成功后的处理** - AbstractAuthenticationProcessingFilter：

```java
protected void successfulAuthentication(HttpServletRequest request, 
                                       HttpServletResponse response,
                                       FilterChain chain, 
                                       Authentication authResult) {
    
    // 1. 将认证信息存储到 SecurityContext
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(authResult);
    SecurityContextHolder.setContext(context);
    
    // 2. 存储到 Session（通过 SecurityContextPersistenceFilter 完成）
    
    // 3. 调用成功处理器
    successHandler.onAuthenticationSuccess(request, response, authResult);
}
```

#### Web 应用上下文生命周期总结

| 阶段 | 操作 | 说明 |
|------|------|------|
| **请求开始** | `createEmptyContext()` | 创建空上下文或从 Session 恢复 |
| **认证成功** | `setAuthentication()` | 将认证信息设置到上下文 |
| **请求结束** | 保存到 Session | 下次请求可以恢复 |
| **线程清理** | `clearContext()` | 防止内存泄漏 |

#### SecurityContextHolderFilter 是默认过滤器吗？

> **Spring Security 7.0 变化**：
> - `SecurityContextPersistenceFilter` 已更名为 `SecurityContextHolderFilter`
> - 在 Spring Security 7.0 中，默认会自动保存 SecurityContext，无需配置 `requireExplicitSave(false)`

**是的**，`SecurityContextHolderFilter` 是 Spring Security 7.0 的**默认过滤器之一**：

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    Spring Security 7.0 默认过滤器链顺序                      │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  1. DisableEncodeUrlFilter                                                  │
│  2. ForceEagerSessionCreationFilter                                         │
│  3. ChannelProcessingFilter ★                                             │
│  4. WebAsyncManagerIntegrationFilter                                        │
│  5. SecurityContextHolderFilter  ◄── ★ 7.0 已更名（原 SecurityContextPersistenceFilter）│
│  6. HeaderWriterFilter                                                      │
│  7. CorsFilter  ★                                                         │
│  8. CsrfFilter ★                                                          │
│  9. LogoutFilter                                                            │
│  10. UsernamePasswordAuthenticationFilter                                    │
│  11. DefaultLoginPageGeneratingFilter                                       │
│  12. DefaultLogoutPageGeneratingFilter                                      │
│  13. BasicAuthenticationFilter                                              │
│  14. RequestCacheAwareFilter                                                │
│  15. SecurityContextHolderAwareRequestFilter                                │
│  16. RememberMeAuthenticationFilter                                         │
│  17. AnonymousAuthenticationFilter                                          │
│  18. ExceptionTranslationFilter                                              │
│  19. AuthorizationFilter ★                                                  │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

**为什么是默认过滤器？**

1. **自动配置**：引入 `spring-boot-starter-security` 后自动添加
2. **核心作用**：负责 SecurityContext 的加载和保存
3. **不可缺失**：没有它，认证信息无法在请求间保持

**查看已注册的过滤器：**

```java
@Component
public class FilterInspector implements CommandLineRunner {
    
    @Autowired
    private FilterChainProxy filterChainProxy;
    
    @Override
    public void run(String... args) throws Exception {
        List<SecurityFilterChain> filterChains = filterChainProxy.getFilterChains();
        
        for (int i = 0; i < filterChains.size(); i++) {
            System.out.println("Filter Chain " + i + ":");
            List<Filter> filters = filterChains.get(i).getFilters();
            
            for (int j = 0; j < filters.size(); j++) {
                System.out.println("  " + (j + 1) + ". " + 
                    filters.get(j).getClass().getSimpleName());
            }
        }
    }
}
```

**特殊情况：禁用或替换**

> **Spring Security 7.0 配置方式**：移除了 `requireExplicitSave(false)` 配置，SecurityContext 默认自动保存。

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 使用无状态模式（禁用 Session 存储）★ 7.0 配置方式
            .securityContext(context -> context
                .securityContextRepository(new NullSecurityContextRepository())
            )
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
```

| 属性 | 说明 |
|------|------|
| **是否默认** | ✅ 是，自动配置 |
| **顺序** | 第5个（非常靠前）|
| **作用** | 加载/保存 SecurityContext 到 Session |
| **可禁用** | ✅ 可以，用于无状态应用 |
| **7.0 变化** | 默认自动保存，无需 `requireExplicitSave(false)` |

### 获取当前认证信息

```java
// 获取 SecurityContext
SecurityContext context = SecurityContextHolder.getContext();

// 获取 Authentication
Authentication authentication = context.getAuthentication();

// 获取用户名
String username = authentication.getName();

// 获取主体
Object principal = authentication.getPrincipal();

// 获取权限集合
Collection<? extends GrantedAuthority> authorities = 
    authentication.getAuthorities();
```

### 清除认证信息

```java
// 清除当前线程的安全上下文
SecurityContextHolder.clearContext();
```

## 在 Spring MVC 中使用

### 使用 @AuthenticationPrincipal 注解

```java
@GetMapping("/profile")
public String getProfile(@AuthenticationPrincipal UserDetails userDetails) {
    // 直接获取当前登录用户
    String username = userDetails.getUsername();
    return "profile";
}
```

### 使用 @CurrentSecurityContext 注解

```java
@GetMapping("/context")
public String getContext(@CurrentSecurityContext SecurityContext context) {
    Authentication authentication = context.getAuthentication();
    return "context";
}
```

## 在 Servlet API 中使用

```java
// 在 Servlet 中获取当前用户
String username = request.getRemoteUser();

// 或
Principal principal = request.getUserPrincipal();
```

## 线程安全注意事项

```
┌─────────────────────────────────────────────────────────────┐
│                      线程安全机制                            │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  默认策略：ThreadLocal                                       │
│  ┌───────────────────────────────────────────────────────┐ │
│  │  Thread 1: SecurityContext (User A)                   │ │
│  │  Thread 2: SecurityContext (User B)                   │ │
│  │  Thread 3: SecurityContext (User C)                   │ │
│  │  ...                                                  │ │
│  │                                                       │
│  │  每个线程独立存储，互不干扰                            │ │
│  └───────────────────────────────────────────────────────┘ │
│                                                             │
│  FilterChainProxy 保证：                                     │
│  - 请求结束后自动清理 SecurityContext                        │
│  - 防止内存泄漏和线程污染                                    │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 线程池场景注意事项

如果使用线程池（如 @Async），需要注意：

```java
// 问题：子线程无法获取父线程的 SecurityContext
@Async
public void asyncMethod() {
    // 这里 SecurityContextHolder.getContext() 为空！
}

// 解决方案1：使用 InheritableThreadLocal
SecurityContextHolder.setStrategyName(
    SecurityContextHolder.MODE_INHERITABLETHREADLOCAL
);

// 解决方案2：手动传递 SecurityContext
@Async
public void asyncMethod(SecurityContext context) {
    SecurityContextHolder.setContext(context);
    try {
        // 执行业务逻辑
    } finally {
        SecurityContextHolder.clearContext();
    }
}

// 解决方案3：使用 DelegatingSecurityContextRunnable
Runnable task = new DelegatingSecurityContextRunnable(() -> {
    // 自动继承父线程的 SecurityContext
});
executor.execute(task);
```

## 总结

| 特性 | 说明 |
|------|------|
| **核心作用** | 存储当前认证用户的安全上下文 |
| **默认策略** | ThreadLocal（线程隔离）|
| **获取方式** | `SecurityContextHolder.getContext()` |
| **设置方式** | `SecurityContextHolder.setContext(context)` |
| **清理方式** | `SecurityContextHolder.clearContext()` |
| **线程安全** | FilterChainProxy 自动清理，无需手动处理 |

## 最佳实践

1. **不要手动创建 SecurityContext 实例**
   ```java
   // 错误
   SecurityContext context = new SecurityContextImpl();
   
   // 正确
   SecurityContext context = SecurityContextHolder.createEmptyContext();
   ```

2. **避免直接操作 SecurityContextHolder**
   ```java
   // 推荐：使用注解
   @AuthenticationPrincipal UserDetails userDetails
   
   // 不推荐：直接获取
   SecurityContextHolder.getContext().getAuthentication()
   ```

3. **异步任务中注意上下文传递**
   ```java
   // 使用 DelegatingSecurityContextExecutor
   Executor executor = new DelegatingSecurityContextExecutor(
       Executors.newFixedThreadPool(10)
   );
   ```

## 分布式系统方案

在分布式系统中，SecurityContextHolder 的 ThreadLocal 策略无法满足需求，因为每个服务都是独立的 JVM 进程。需要采用以下方案：

### 方案一：JWT Token（推荐）

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          JWT 分布式认证方案                                   │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌──────────┐                    ┌──────────┐                    ┌─────────┐│
│  │  用户     │───── 登录请求 ────>│ 认证服务  │                    │ 业务服务 ││
│  │ (浏览器)  │                    │ (Auth)   │                    │ (API)   ││
│  └────┬─────┘                    └────┬─────┘                    └────┬────┘│
│       │                               │                               │     │
│       │<──────── JWT Token ───────────┘                               │     │
│       │    {userId, roles, exp}                                       │     │
│       │                                                               │     │
│       │───────────────── 请求业务 API ─────────────────────────────────>│     │
│       │                   Authorization: Bearer <JWT>                 │     │
│       │                                                               │     │
│       │<──────────────── 业务数据 ──────────────────────────────────────┘     │
│       │                    服务间无状态，Token 自包含用户信息                 │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

**实现方式：**
- 用户登录成功后，服务端生成 JWT Token
- 客户端存储 Token（localStorage / Cookie）
- 每次请求携带 Token（Header: `Authorization: Bearer <token>`）
- 服务端验证 Token 签名和有效期，从中提取用户信息

### 方案二：OAuth2 + Token 自省

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        OAuth2 分布式认证方案                                 │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌──────────┐     ┌──────────────┐     ┌──────────────┐     ┌──────────┐  │
│  │  用户     │────>│  OAuth2      │────>│  资源服务    │────>│ 认证服务  │  │
│  │ (浏览器)  │     │  授权服务器   │     │  (API)       │     │ (Token)   │  │
│  └──────────┘     └──────────────┘     └──────┬───────┘     └────┬─────┘  │
│                                               │                  │        │
│                                               │  Token 自省请求   │        │
│                                               │ ────────────────>│        │
│                                               │                  │        │
│                                               │<─ 用户有效/权限 ──┘        │
│                                               │                            │
└─────────────────────────────────────────────────────────────────────────────┘
```

**实现方式：**
- 使用 OAuth2 授权服务器颁发 Access Token
- 资源服务器通过 `/oauth2/introspect` 端点验证 Token
- 或使用 JWT 自验证（减少授权服务器压力）

### 方案三：共享 Session（不推荐）

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        共享 Session 方案                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌──────────┐     ┌──────────┐     ┌──────────┐     ┌──────────────────┐  │
│  │  服务A    │<───>│  服务B    │<───>│  服务C    │<───>│  Redis (Session) │  │
│  │          │     │          │     │          │     │                  │  │
│  │  Spring  │     │  Spring  │     │  Spring  │     │  - sessionId     │  │
│  │  Session │     │  Session │     │  Session │     │  - user info     │  │
│  │          │     │          │     │          │     │  - authorities   │  │
│  └──────────┘     └──────────┘     └──────────┘     └──────────────────┘  │
│                                                                             │
│  所有服务共享同一个 Redis Session 存储                                       │
│  通过 sessionId 获取用户信息                                                │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

**实现方式：**
- 使用 Spring Session + Redis
- 所有服务共享 Session 存储
- 通过 Cookie 中的 sessionId 获取用户信息

### 方案对比

| 方案 | 优点 | 缺点 | 适用场景 |
|------|------|------|----------|
| **JWT** | 无状态、性能好、易于扩展 | Token 无法主动失效、信息量大 | 微服务、移动端、SPA |
| **OAuth2** | 标准化、安全性高、支持多种授权模式 | 复杂度高、需要维护授权服务器 | 第三方接入、企业级应用 |
| **共享 Session** | 实现简单、可主动失效 | 有状态、Redis 单点、扩展性差 | 小型集群、传统应用 |

### 推荐方案：JWT + Spring Security

```java
@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        
        // 1. 从请求头获取 Token
        String token = extractTokenFromRequest(request);
        
        if (token != null && jwtTokenUtil.validateToken(token)) {
            // 2. 从 Token 解析用户信息
            String username = jwtTokenUtil.getUsernameFromToken(token);
            List<GrantedAuthority> authorities = jwtTokenUtil.getAuthoritiesFromToken(token);
            
            // 3. 创建 Authentication 对象
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                    username, null, authorities);
            
            authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request));
            
            // 4. 设置到 SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        
        chain.doFilter(request, response);
    }
    
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    private JwtTokenFilter jwtTokenFilter;
    
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // JWT 不需要 CSRF
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // 无状态
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
```

### 分布式系统总结

分布式系统中：
- **放弃 ThreadLocal 存储用户状态**
- **采用无状态的 Token 机制（JWT）**
- **每个请求独立验证 Token，不依赖服务器端存储**
- **服务间调用也携带 Token，保持用户上下文传递**