# Spring Security 常见问题与解决方案

## 一、配置类问题

### 1.1 配置不生效

**问题描述**：配置了 SecurityFilterChain，但访问控制没有生效。

**可能原因**：

1. **缺少 `@Configuration` 注解**
```java
// ❌ 错误
public class SecurityConfig {
    
// ✅ 正确
@Configuration
public class SecurityConfig {
```

2. **缺少 `@EnableWebSecurity` 注解**
```java
// ❌ 错误
@Configuration
public class SecurityConfig {

// ✅ 正确
@Configuration
@EnableWebSecurity
public class SecurityConfig {
```

3. **多个 SecurityFilterChain 冲突**
```java
@Configuration
public class SecurityConfig {
    
    // 如果有多个 SecurityFilterChain，需要使用 @Order 指定优先级
    @Bean
    @Order(1)  // 数字越小优先级越高
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/api/**");
        // ...
        return http.build();
    }
    
    @Bean
    @Order(2)
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        // ...
        return http.build();
    }
}
```

### 1.2 循环依赖问题

**问题描述**：启动时报 `BeanCurrentlyInCreationException` 循环依赖错误。

**解决方案**：

```java
// ❌ 错误 - 字段注入可能导致循环依赖
@Configuration
public class SecurityConfig {
    @Autowired
    private UserService userService;
}

// ✅ 正确 - 使用构造器注入
@Configuration
public class SecurityConfig {
    private final UserService userService;
    
    public SecurityConfig(UserService userService) {
        this.userService = userService;
    }
}

// ✅ 或者使用 @Lazy 延迟加载
@Configuration
public class SecurityConfig {
    private final UserService userService;
    
    public SecurityConfig(@Lazy UserService userService) {
        this.userService = userService;
    }
}
```

### 1.3 过滤器顺序问题

**问题描述**：自定义过滤器没有按预期执行。

**解决方案**：

```java
@Configuration
public class SecurityConfig {
    
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 在指定过滤器之前添加
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            
            // 在指定过滤器之后添加
            .addFilterAfter(loggingFilter, UsernamePasswordAuthenticationFilter.class)
            
            // 在指定过滤器位置添加（替换原有过滤器）
            .addFilterAt(customFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
```

Spring Security 过滤器链顺序：
```
1. ChannelProcessingFilter
2. WebAsyncManagerIntegrationFilter
3. SecurityContextPersistenceFilter
4. HeaderWriterFilter
5. CorsFilter
6. CsrfFilter
7. LogoutFilter
8. UsernamePasswordAuthenticationFilter
9. DefaultLoginPageGeneratingFilter
10. DefaultLogoutPageGeneratingFilter
11. BasicAuthenticationFilter
12. RequestCacheAwareFilter
13. SecurityContextHolderAwareRequestFilter
14. AnonymousAuthenticationFilter
15. SessionManagementFilter
16. ExceptionTranslationFilter
17. FilterSecurityInterceptor
18. AuthorizationFilter
```

## 二、认证问题

### 2.1 密码验证失败

**问题描述**：用户名密码正确，但登录失败。

**可能原因**：

1. **密码未加密存储**
```java
// ❌ 错误 - 明文存储密码
user.setPassword("123456");

// ✅ 正确 - 使用 BCrypt 加密
@Autowired
private PasswordEncoder passwordEncoder;

user.setPassword(passwordEncoder.encode("123456"));
```

2. **PasswordEncoder 配置错误**
```java
// ❌ 错误 - 没有配置 PasswordEncoder
@Bean
public PasswordEncoder passwordEncoder() {
    return NoOpPasswordEncoder.getInstance();  // 不安全，仅用于测试
}

// ✅ 正确 - 使用 BCrypt
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}

// ✅ 或者使用 DelegatingPasswordEncoder 支持多种编码
@Bean
public PasswordEncoder passwordEncoder() {
    String encodingId = "bcrypt";
    Map<String, PasswordEncoder> encoders = new HashMap<>();
    encoders.put(encodingId, new BCryptPasswordEncoder());
    encoders.put("noop", NoOpPasswordEncoder.getInstance());
    encoders.put("pbkdf2", Pbkdf2PasswordEncoder.defaultsForSpringSecurity_v5_8());
    return new DelegatingPasswordEncoder(encodingId, encoders);
}
```

3. **密码前缀问题**
```java
// 如果使用 DelegatingPasswordEncoder，密码需要带前缀
// {bcrypt}$2a$10$...
// {noop}plainTextPassword
```

### 2.2 用户不存在异常

**问题描述**：`UsernameNotFoundException` 没有被正确处理。

**解决方案**：

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        
        // ❌ 错误 - 返回 null
        if (user == null) {
            return null;
        }
        
        // ✅ 正确 - 抛出异常
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        
        // ✅ 或者使用 Optional
        return userRepository.findByUsername(username)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));
    }
}
```

### 2.3 Session 问题

**问题描述**：登录后 Session 丢失或无法保持登录状态。

**解决方案**：

```java
@Configuration
public class SessionConfig {
    
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .sessionManagement(session -> session
                // Session 创建策略
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                
                // 最大并发 Session 数
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
                
                // Session 失效处理
                .expiredUrl("/login?expired=true")
                
                // Session 固定保护
                .sessionFixation().migrateSession()
            );
        
        return http.build();
    }
}
```

前端请求需要携带 Cookie：
```javascript
fetch('/api/protected', {
    credentials: 'include'  // 关键：包含 Cookie
});
```

## 三、授权问题

### 3.1 权限不生效

**问题描述**：配置了权限，但用户仍能访问受限资源。

**可能原因**：

1. **权限格式错误**
```java
// ❌ 错误 - hasRole 会自动添加 ROLE_ 前缀
@PreAuthorize("hasRole('ROLE_ADMIN')")  // 实际是 ROLE_ROLE_ADMIN

// ✅ 正确
@PreAuthorize("hasRole('ADMIN')")  // 实际是 ROLE_ADMIN

// ✅ 或者使用 hasAuthority
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
```

2. **方法安全未启用**
```java
// ❌ 错误 - 缺少注解
@SpringBootApplication
public class Application {}

// ✅ 正确
@SpringBootApplication
@EnableMethodSecurity(prePostEnabled = true)
public class Application {}
```

3. **注解在私有方法上**
```java
@Service
public class UserService {
    
    // ❌ 错误 - 私有方法上的注解无效
    @PreAuthorize("hasRole('ADMIN')")
    private void privateMethod() {}
    
    // ✅ 正确 - 必须是公共方法
    @PreAuthorize("hasRole('ADMIN')")
    public void publicMethod() {}
}
```

### 3.2 URL 匹配问题

**问题描述**：URL 权限规则没有正确匹配。

**解决方案**：

```java
@Configuration
public class UrlSecurityConfig {
    
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // ❌ 错误 - ** 只能用于路径末尾
                .requestMatchers("/api/**/users").authenticated()
                
                // ✅ 正确 - 使用 * 匹配单级路径
                .requestMatchers("/api/*/users").authenticated()
                
                // ✅ 正确 - ** 匹配多级路径
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                // ✅ 正确 - 多个路径
                .requestMatchers("/api/users", "/api/roles").authenticated()
                
                // ✅ 正确 - 使用 mvcMatchers（支持 Ant 风格）
                .requestMatchers("/api/users/{id}").authenticated()
            );
        
        return http.build();
    }
}
```

### 3.3 动态授权问题

**问题描述**：自定义 AuthorizationManager 不生效。

**解决方案**：

```java
@Component
public class CustomAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
    
    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, 
                                       RequestAuthorizationContext context) {
        // 注意：这里必须使用 Supplier 获取认证信息
        Authentication auth = authentication.get();
        
        if (auth == null || !auth.isAuthenticated()) {
            return new AuthorizationDecision(false);
        }
        
        // 权限检查逻辑
        // ...
        
        return new AuthorizationDecision(true);
    }
}

@Configuration
public class SecurityConfig {
    
    @Autowired
    private CustomAuthorizationManager authorizationManager;
    
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .anyRequest().access(authorizationManager)
            );
        
        return http.build();
    }
}
```

## 四、前后端分离问题

### 4.1 CORS 问题

**问题描述**：前端跨域请求被阻止。

**解决方案**：

```java
@Configuration
public class CorsConfig {
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 允许的源
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "http://localhost:8080"
        ));
        
        // 允许的方法
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));
        
        // 允许的请求头
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Requested-With"
        ));
        
        // 允许携带凭证（Cookie）
        configuration.setAllowCredentials(true);
        
        // 预检请求缓存时间
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}

@Configuration
public class SecurityConfig {
    
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 注意：cors() 必须在 csrf() 之前
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable());
        
        return http.build();
    }
}
```

### 4.2 JWT 验证失败

**问题描述**：JWT Token 验证失败。

**可能原因**：

1. **Token 格式错误**
```java
// ❌ 错误 - 缺少 Bearer 前缀
headers: { 'Authorization': token }

// ✅ 正确
headers: { 'Authorization': 'Bearer ' + token }
```

2. **密钥不匹配**
```java
// 生成 Token 和验证 Token 必须使用相同的密钥
@Component
public class JwtTokenProvider {
    
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    
    public String generateToken(Authentication authentication) {
        return Jwts.builder()
                // ...
                .signWith(key)  // 使用相同的 key
                .compact();
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)  // 使用相同的 key
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
```

3. **Token 过期**
```java
// 检查 Token 过期时间
public boolean isTokenExpired(String token) {
    Date expiration = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getExpiration();
    
    return expiration.before(new Date());
}
```

### 4.3 401/403 响应处理

**问题描述**：前端无法正确区分 401 和 403 错误。

**解决方案**：

```java
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 401);
        result.put("message", "未认证，请先登录");
        result.put("path", request.getRequestURI());
        
        response.getWriter().write(new ObjectMapper().writeValueAsString(result));
    }
}

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 403);
        result.put("message", "无权限访问该资源");
        result.put("path", request.getRequestURI());
        
        response.getWriter().write(new ObjectMapper().writeValueAsString(result));
    }
}
```

前端处理：
```javascript
axios.interceptors.response.use(
    response => response,
    error => {
        if (error.response) {
            switch (error.response.status) {
                case 401:
                    // 未认证，跳转到登录页
                    localStorage.removeItem('token');
                    window.location.href = '/login';
                    break;
                case 403:
                    // 无权限，显示提示
                    message.error('您没有权限执行此操作');
                    break;
                default:
                    message.error(error.response.data.message || '请求失败');
            }
        }
        return Promise.reject(error);
    }
);
```

## 五、性能问题

### 5.1 权限查询慢

**问题描述**：每次请求都查询数据库，性能差。

**解决方案**：

```java
@Service
public class CachedUserService {
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @Autowired
    private UserMapper userMapper;
    
    private static final String USER_PERMISSIONS_KEY = "user:permissions:";
    private static final long CACHE_TTL = 30; // 30分钟
    
    public List<Permission> getUserPermissions(Long userId) {
        String key = USER_PERMISSIONS_KEY + userId;
        
        // 从缓存获取
        String cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return JSON.parseArray(cached, Permission.class);
        }
        
        // 从数据库获取
        List<Permission> permissions = userMapper.selectPermissionsByUserId(userId);
        
        // 存入缓存
        redisTemplate.opsForValue().set(
            key,
            JSON.toJSONString(permissions),
            CACHE_TTL,
            TimeUnit.MINUTES
        );
        
        return permissions;
    }
    
    // 权限变更时清除缓存
    public void clearPermissionCache(Long userId) {
        redisTemplate.delete(USER_PERMISSIONS_KEY + userId);
    }
}
```

### 5.2 Session 存储问题

**问题描述**：集群环境下 Session 不共享。

**解决方案**：

```java
@Configuration
public class SessionClusterConfig {
    
    @Bean
    public LettuceConnectionFactory connectionFactory() {
        return new LettuceConnectionFactory();
    }
    
    @Bean
    public HttpSessionIdResolver httpSessionIdResolver() {
        // 使用 Cookie 传递 Session ID
        return CookieHttpSessionIdResolver.builder()
                .cookieName("SESSION")
                .build();
    }
    
    @Bean
    public ReactiveSessionRepository<?> sessionRepository() {
        // 使用 Redis 存储 Session
        return new ReactiveRedisSessionRepository(connectionFactory());
    }
}

// 依赖
// implementation 'org.springframework.session:spring-session-data-redis'
```

## 六、调试技巧

### 6.1 开启调试日志

```yaml
# application.yml
logging:
  level:
    org.springframework.security: DEBUG
    org.springframework.security.web.FilterChainProxy: DEBUG
```

### 6.2 打印过滤器链

```java
@Component
public class FilterChainDebugger {
    
    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        FilterChainProxy filterChainProxy = 
            event.getApplicationContext().getBean(FilterChainProxy.class);
        
        System.out.println("=== Security Filter Chain ===");
        for (SecurityFilterChain chain : filterChainProxy.getFilterChains()) {
            System.out.println("Chain: " + chain);
            for (Filter filter : chain.getFilters()) {
                System.out.println("  Filter: " + filter.getClass().getName());
            }
        }
        System.out.println("============================");
    }
}
```

### 6.3 打印当前认证信息

```java
@RestController
public class DebugController {
    
    @GetMapping("/debug/auth")
    public Map<String, Object> debugAuth(Authentication authentication) {
        Map<String, Object> result = new HashMap<>();
        
        if (authentication != null) {
            result.put("authenticated", authentication.isAuthenticated());
            result.put("principal", authentication.getPrincipal());
            result.put("authorities", authentication.getAuthorities());
            result.put("details", authentication.getDetails());
        } else {
            result.put("authenticated", false);
        }
        
        return result;
    }
}
```

## 七、总结

Spring Security 常见问题分类：

| 类别 | 常见问题 | 关键解决思路 |
|------|----------|--------------|
| 配置 | 配置不生效、循环依赖 | 检查注解、使用构造器注入 |
| 认证 | 密码验证失败、Session 丢失 | 检查密码编码器、Session 策略 |
| 授权 | 权限不生效、URL 匹配失败 | 检查权限格式、URL 匹配规则 |
| 前后端分离 | CORS、JWT 验证失败 | 正确配置 CORS、检查 Token 格式 |
| 性能 | 权限查询慢 | 使用 Redis 缓存 |

遇到问题时，建议：
1. 开启 DEBUG 日志查看详细错误信息
2. 检查配置类注解是否完整
3. 确认过滤器顺序是否正确
4. 验证权限格式和 URL 匹配规则