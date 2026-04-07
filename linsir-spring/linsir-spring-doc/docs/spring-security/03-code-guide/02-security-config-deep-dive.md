# Spring Security 配置深度解析

## 一、SecurityFilterChain 详解

### 1.1 过滤器链概述

Spring Security 的核心是过滤器链（Filter Chain），每个请求都会经过一系列过滤器的处理：

```
请求 → IPFilter → UsernamePasswordAuthenticationFilter → 
      AuthorizationFilter → ExceptionTranslationFilter → 
      业务逻辑
```

### 1.2 自定义过滤器配置

```java
// 在 SecurityConfig 中添加过滤器
http.addFilterBefore(ipFilter, UsernamePasswordAuthenticationFilter.class)
```

过滤器执行顺序：
1. `IPFilter` - 自定义 IP 过滤
2. `UsernamePasswordAuthenticationFilter` - 用户名密码认证
3. `AuthorizationFilter` - 授权检查
4. `ExceptionTranslationFilter` - 异常转换

## 二、授权配置详解

### 2.1 请求匹配规则

```java
.authorizeHttpRequests(auth -> auth
    // 精确匹配
    .requestMatchers("/api/hello").permitAll()
    
    // Ant 风格匹配
    .requestMatchers("/api/user/*").authenticated()
    .requestMatchers("/api/admin/**").hasRole("ADMIN")
    
    // 多路径匹配
    .requestMatchers("/login", "/register", "/forgot-password").permitAll()
    
    // HTTP 方法匹配
    .requestMatchers(HttpMethod.GET, "/api/public/**").permitAll()
    .requestMatchers(HttpMethod.POST, "/api/admin/**").hasRole("ADMIN")
    
    // 动态授权
    .anyRequest().access(dynamicAuthorizationManager)
)
```

### 2.2 内置授权规则

| 方法 | 说明 | 示例 |
|------|------|------|
| `permitAll()` | 允许所有访问 | 登录页面、静态资源 |
| `denyAll()` | 拒绝所有访问 | 敏感接口 |
| `authenticated()` | 需要认证 | 普通业务接口 |
| `anonymous()` | 仅匿名用户 | 注册接口 |
| `hasRole("ADMIN")` | 需要指定角色 | 管理接口 |
| `hasAuthority("user:read")` | 需要指定权限 | 细粒度控制 |
| `hasAnyRole("ADMIN", "USER")` | 多个角色任一 | 通用接口 |

## 三、认证配置详解

### 3.1 表单登录配置

```java
.formLogin(form -> form
    // 登录处理 URL
    .loginProcessingUrl("/api/auth/login")
    
    // 登录成功处理器
    .successHandler((request, response, authentication) -> {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":200,\"message\":\"登录成功\"}");
    })
    
    // 登录失败处理器
    .failureHandler((request, response, exception) -> {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"message\":\"登录失败\"}");
    })
    
    // 允许所有访问登录接口
    .permitAll()
)
```

### 3.2 Session 管理

```java
.sessionManagement(session -> session
    // Session 创建策略
    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
    
    // 最大并发 Session 数
    .maximumSessions(1)
    .maxSessionsPreventsLogin(false)  // true: 禁止新登录, false: 踢掉旧登录
    
    // Session 失效处理
    .expiredUrl("/login?expired")
)
```

Session 创建策略：
- `ALWAYS` - 总是创建 Session
- `NEVER` - 不创建 Session，但会使用已有的
- `IF_REQUIRED` - 需要时创建（默认）
- `STATELESS` - 无状态，不创建 Session（JWT 场景）

## 四、异常处理配置

### 4.1 认证异常 vs 授权异常

```
认证异常 (401 Unauthorized)
├── 未登录访问受保护资源
├── Session 过期
└── Token 无效

授权异常 (403 Forbidden)
├── 已登录但无权限
├── 角色不匹配
└── 权限不足
```

### 4.2 自定义异常处理器

```java
// 认证异常处理器 - 未登录
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        
        // 判断请求类型
        String acceptHeader = request.getHeader("Accept");
        boolean isAjax = acceptHeader != null && acceptHeader.contains("application/json");
        
        if (isAjax) {
            // AJAX 请求返回 JSON
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"请先登录\"}");
        } else {
            // 普通请求重定向
            response.sendRedirect("/login");
        }
    }
}

// 授权异常处理器 - 无权限
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
        result.put("message", "访问被拒绝");
        result.put("path", request.getRequestURI());
        result.put("timestamp", System.currentTimeMillis());
        
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(result));
    }
}
```

## 五、高级配置

### 5.1 记住我功能

```java
.rememberMe(remember -> remember
    // 记住我参数名
    .rememberMeParameter("rememberMe")
    
    // Cookie 名称
    .rememberMeCookieName("remember-me")
    
    // Token 有效期（秒）
    .tokenValiditySeconds(604800)  // 7天
    
    // 密钥（用于加密）
    .key("uniqueAndSecretKey")
    
    // 用户详情服务
    .userDetailsService(userDetailsService)
)
```

### 5.2 CORS 配置

```java
.cors(cors -> cors
    .configurationSource(request -> {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);
        return config;
    })
)
```

### 5.3 请求头安全

```java
.headers(headers -> headers
    // X-Frame-Options 防止点击劫持
    .frameOptions(frameOptions -> frameOptions
        .sameOrigin()  // 只允许同源 iframe
    )
    
    // X-Content-Type-Options 防止 MIME 嗅探
    .contentTypeOptions(contentTypeOptions -> {})
    
    // X-XSS-Protection XSS 过滤
    .xssProtection(xss -> xss
        .headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK)
    )
    
    // Strict-Transport-Security 强制 HTTPS
    .httpStrictTransportSecurity(hsts -> hsts
        .includeSubDomains(true)
        .maxAgeInSeconds(31536000)
    )
    
    // Content-Security-Policy
    .contentSecurityPolicy(csp -> csp
        .policyDirectives("default-src 'self'")
    )
)
```

## 六、方法级安全

### 6.1 启用方法安全

```java
@EnableMethodSecurity(
    prePostEnabled = true,   // 启用 @PreAuthorize, @PostAuthorize
    securedEnabled = true,   // 启用 @Secured
    jsr250Enabled = true     // 启用 @RolesAllowed
)
```

### 6.2 常用注解

```java
@Service
public class UserService {
    
    // 需要特定角色
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(Long userId) {
        // 只有 ADMIN 角色可以删除用户
    }
    
    // 需要特定权限
    @PreAuthorize("hasAuthority('user:update')")
    public void updateUser(User user) {
        // 需要 user:update 权限
    }
    
    // 复杂表达式
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #userId == authentication.principal.id)")
    public User getUser(Long userId) {
        // ADMIN 可以查看任何用户，USER 只能查看自己
        return userMapper.selectById(userId);
    }
    
    // 返回值检查
    @PostAuthorize("returnObject.owner == authentication.name")
    public Document getDocument(Long id) {
        // 只能获取属于自己的文档
        return documentMapper.selectById(id);
    }
    
    // 过滤集合
    @PreFilter("filterObject.owner == authentication.name")
    public List<Document> deleteDocuments(List<Document> documents) {
        // 只能删除属于自己的文档
        return documents;
    }
}
```

## 七、测试配置

### 7.1 测试类配置

```java
@SpringBootTest
@AutoConfigureMockMvc
public class SecurityTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void testAdminAccess() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
               .andExpect(status().isOk());
    }
    
    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testUserAccessDenied() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
               .andExpect(status().isForbidden());
    }
    
    @Test
    void testAnonymousAccess() throws Exception {
        mockMvc.perform(get("/api/user/profile"))
               .andExpect(status().isUnauthorized());
    }
}
```

### 7.2 安全测试工具

```java
@Test
void testLogin() throws Exception {
    mockMvc.perform(formLogin("/api/auth/login")
            .user("admin")
            .password("123456"))
           .andExpect(authenticated());
}

@Test
void testLogout() throws Exception {
    mockMvc.perform(logout("/api/auth/logout"))
           .andExpect(unauthenticated());
}
```

## 八、常见问题排查

### 8.1 配置不生效

检查清单：
1. 确保配置类上有 `@Configuration` 注解
2. 确保启用了 `@EnableWebSecurity`
3. 检查过滤器顺序是否正确
4. 确认没有多个 SecurityFilterChain 冲突

### 8.2 循环依赖问题

```java
// 错误示例 - 循环依赖
@Configuration
public class SecurityConfig {
    @Autowired
    private UserService userService;  // UserService 依赖 SecurityConfig
}

// 正确做法 - 使用构造器注入
@Configuration
public class SecurityConfig {
    private final UserService userService;
    
    public SecurityConfig(UserService userService) {
        this.userService = userService;
    }
}
```

### 8.3 权限表达式不生效

```java
// 确保启用了方法安全
@EnableMethodSecurity(prePostEnabled = true)

// 确保注解在公共方法上
@Service
public class UserService {
    @PreAuthorize("hasRole('ADMIN')")  // ✅ 有效
    public void publicMethod() {}
    
    @PreAuthorize("hasRole('ADMIN')")  // ❌ 无效 - 私有方法
    private void privateMethod() {}
}
```

## 九、完整配置示例

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class CompleteSecurityConfig {
    
    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            // CSRF
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/auth/**")
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            )
            
            // CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Session
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // 授权
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            
            // 认证
            .authenticationProvider(authenticationProvider())
            
            // 过滤器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            
            // 异常
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(authenticationEntryPoint())
                .accessDeniedHandler(accessDeniedHandler())
            )
            
            // 头部
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin())
            )
            
            .build();
    }
    
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

## 十、总结

Spring Security 配置的核心要点：

1. **过滤器链**：理解过滤器的执行顺序和作用
2. **授权规则**：合理配置请求匹配和权限规则
3. **认证方式**：选择合适的认证方式（Session/JWT）
4. **异常处理**：区分认证异常和授权异常
5. **方法安全**：使用注解实现细粒度控制

通过合理配置，可以构建安全、灵活的权限管理系统。