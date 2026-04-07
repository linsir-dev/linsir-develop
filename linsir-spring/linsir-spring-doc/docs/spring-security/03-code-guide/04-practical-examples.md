# Spring Security 实战案例

## 一、用户认证案例

### 1.1 表单登录实现

#### 后端配置

```java
@Configuration
@EnableWebSecurity
public class FormLoginConfig {
    
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/register").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")                    // 自定义登录页
                .loginProcessingUrl("/api/auth/login")  // 登录处理URL
                .usernameParameter("username")          // 用户名参数
                .passwordParameter("password")          // 密码参数
                .successHandler(loginSuccessHandler())  // 成功处理器
                .failureHandler(loginFailureHandler())  // 失败处理器
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .logoutSuccessHandler(logoutSuccessHandler())
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            );
        
        return http.build();
    }
    
    @Bean
    public AuthenticationSuccessHandler loginSuccessHandler() {
        return (request, response, authentication) -> {
            response.setContentType("application/json;charset=UTF-8");
            
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "登录成功");
            result.put("username", authentication.getName());
            result.put("authorities", authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));
            
            response.getWriter().write(new ObjectMapper().writeValueAsString(result));
        };
    }
    
    @Bean
    public AuthenticationFailureHandler loginFailureHandler() {
        return (request, response, exception) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            
            String message = "登录失败";
            if (exception instanceof BadCredentialsException) {
                message = "用户名或密码错误";
            } else if (exception instanceof LockedException) {
                message = "账户已被锁定";
            } else if (exception instanceof DisabledException) {
                message = "账户已被禁用";
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("code", 401);
            result.put("message", message);
            
            response.getWriter().write(new ObjectMapper().writeValueAsString(result));
        };
    }
}
```

#### 前端实现

```javascript
// 登录表单提交
async function handleLogin(username, password) {
    const formData = new URLSearchParams();
    formData.append('username', username);
    formData.append('password', password);
    
    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: formData,
            credentials: 'include'  // 包含 Cookie
        });
        
        const data = await response.json();
        
        if (data.code === 200) {
            // 保存用户信息
            localStorage.setItem('user', JSON.stringify(data));
            // 跳转到首页
            window.location.href = '/';
        } else {
            alert(data.message);
        }
    } catch (error) {
        console.error('登录失败:', error);
    }
}
```

### 1.2 记住我功能

```java
@Configuration
public class RememberMeConfig {
    
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .rememberMe(remember -> remember
                // 记住我参数名
                .rememberMeParameter("rememberMe")
                // Cookie 名称
                .rememberMeCookieName("remember-me")
                // Cookie 有效期（7天）
                .tokenValiditySeconds(604800)
                // 加密密钥
                .key("uniqueAndSecretKey")
                // 用户详情服务
                .userDetailsService(userDetailsService)
                // 持久化令牌（可选）
                .tokenRepository(persistentTokenRepository())
            );
        
        return http.build();
    }
    
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        // 首次运行创建表
        // tokenRepository.setCreateTableOnStartup(true);
        return tokenRepository;
    }
}
```

## 二、权限控制案例

### 2.1 基于 URL 的权限控制

```java
@Configuration
@EnableWebSecurity
public class UrlAuthorizationConfig {
    
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // 公共资源
                .requestMatchers("/", "/home", "/about").permitAll()
                
                // 用户资源 - 需要 USER 角色
                .requestMatchers("/user/**").hasRole("USER")
                
                // 管理员资源 - 需要 ADMIN 角色
                .requestMatchers("/admin/**").hasRole("ADMIN")
                
                // API 接口 - 需要特定权限
                .requestMatchers(HttpMethod.GET, "/api/users").hasAuthority("user:read")
                .requestMatchers(HttpMethod.POST, "/api/users").hasAuthority("user:create")
                .requestMatchers(HttpMethod.PUT, "/api/users/**").hasAuthority("user:update")
                .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasAuthority("user:delete")
                
                // 其他请求需要认证
                .anyRequest().authenticated()
            );
        
        return http.build();
    }
}
```

### 2.2 基于方法的权限控制

```java
@Service
@EnableMethodSecurity(prePostEnabled = true)
public class UserService {
    
    /**
     * 查询用户 - 需要 user:read 权限
     */
    @PreAuthorize("hasAuthority('user:read')")
    public User getUser(Long id) {
        return userMapper.selectById(id);
    }
    
    /**
     * 创建用户 - 需要 user:create 权限
     */
    @PreAuthorize("hasAuthority('user:create')")
    public void createUser(User user) {
        userMapper.insert(user);
    }
    
    /**
     * 更新用户 - 需要 user:update 权限或只能更新自己
     */
    @PreAuthorize("hasAuthority('user:update') or #user.id == authentication.principal.id")
    public void updateUser(User user) {
        userMapper.updateById(user);
    }
    
    /**
     * 删除用户 - 需要 ADMIN 角色
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(Long id) {
        userMapper.deleteById(id);
    }
    
    /**
     * 批量删除 - 需要 ADMIN 角色，且记录操作日志
     */
    @PreAuthorize("hasRole('ADMIN')")
    @LogOperation(operation = "批量删除用户")
    public void batchDeleteUsers(List<Long> ids) {
        userMapper.deleteBatchIds(ids);
    }
}
```

### 2.3 动态权限控制

```java
@Component
public class DynamicPermissionEvaluator implements PermissionEvaluator {
    
    @Autowired
    private UserService userService;
    
    @Override
    public boolean hasPermission(Authentication authentication, 
                                  Object targetDomainObject, 
                                  Object permission) {
        // 获取当前用户
        String username = authentication.getName();
        User user = userService.getUserByUsername(username);
        
        // 获取目标对象类型
        String targetType = targetDomainObject.getClass().getSimpleName().toLowerCase();
        String requiredPermission = targetType + ":" + permission;
        
        // 检查权限
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(requiredPermission));
    }
    
    @Override
    public boolean hasPermission(Authentication authentication,
                                  Serializable targetId,
                                  String targetType,
                                  Object permission) {
        // 基于 ID 的权限检查
        String requiredPermission = targetType.toLowerCase() + ":" + permission;
        
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(requiredPermission));
    }
}

// 使用
@Service
public class DocumentService {
    
    @PreAuthorize("hasPermission(#document, 'read')")
    public Document readDocument(Document document) {
        return document;
    }
    
    @PreAuthorize("hasPermission(#id, 'document', 'write')")
    public void updateDocument(Long id, Document document) {
        // 更新文档
    }
}
```

## 三、前后端分离案例

### 3.1 JWT 认证方案

#### JWT 工具类

```java
@Component
public class JwtTokenProvider {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private long jwtExpiration;
    
    private final Key key;
    
    public JwtTokenProvider() {
        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }
    
    /**
     * 生成 JWT Token
     */
    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        Date expiryDate = new Date(System.currentTimeMillis() + jwtExpiration);
        
        return Jwts.builder()
                .setSubject(userPrincipal.getId().toString())
                .claim("username", userPrincipal.getUsername())
                .claim("authorities", userPrincipal.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }
    
    /**
     * 从 Token 获取用户 ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        return Long.parseLong(claims.getSubject());
    }
    
    /**
     * 验证 Token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
```

#### JWT 过滤器

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        
        try {
            // 获取 JWT
            String jwt = getJwtFromRequest(request);
            
            // 验证 JWT
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                Long userId = tokenProvider.getUserIdFromToken(jwt);
                
                // 加载用户信息
                UserDetails userDetails = userDetailsService.loadUserById(userId);
                
                // 创建认证对象
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                
                authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));
                
                // 设置安全上下文
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        return null;
    }
}
```

#### 安全配置

```java
@Configuration
@EnableWebSecurity
public class JwtSecurityConfig {
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, 
                UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
```

### 3.2 前端 Axios 拦截器

```javascript
// axios 配置
import axios from 'axios';

const instance = axios.create({
    baseURL: '/api',
    timeout: 10000
});

// 请求拦截器 - 添加 Token
instance.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    error => Promise.reject(error)
);

// 响应拦截器 - 处理错误
instance.interceptors.response.use(
    response => response,
    error => {
        if (error.response) {
            switch (error.response.status) {
                case 401:
                    // Token 过期，清除并跳转到登录
                    localStorage.removeItem('token');
                    window.location.href = '/login';
                    break;
                case 403:
                    alert('没有权限执行此操作');
                    break;
                default:
                    console.error('请求错误:', error.response.data);
            }
        }
        return Promise.reject(error);
    }
);

export default instance;
```

## 四、OAuth2 集成案例

### 4.1 GitHub OAuth2 登录

```java
@Configuration
@EnableWebSecurity
public class OAuth2LoginConfig {
    
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(oAuth2UserService())
                )
                .successHandler(oAuth2SuccessHandler())
            );
        
        return http.build();
    }
    
    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        
        return request -> {
            OAuth2User oauth2User = delegate.loadUser(request);
            
            // 获取 GitHub 用户信息
            String githubId = oauth2User.getAttribute("id").toString();
            String login = oauth2User.getAttribute("login");
            String email = oauth2User.getAttribute("email");
            
            // 查找或创建本地用户
            User user = userService.findByGithubId(githubId);
            if (user == null) {
                user = new User();
                user.setGithubId(githubId);
                user.setUsername(login);
                user.setEmail(email);
                userService.save(user);
            }
            
            return new CustomOAuth2User(oauth2User, user);
        };
    }
}
```

### 4.2 配置 application.yml

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          github:
            client-id: your-github-client-id
            client-secret: your-github-client-secret
            scope:
              - read:user
              - user:email
        provider:
          github:
            authorization-uri: https://github.com/login/oauth/authorize
            token-uri: https://github.com/login/oauth/access_token
            user-info-uri: https://api.github.com/user
            user-name-attribute: id
```

## 五、安全防护案例

### 5.1 防止 CSRF 攻击

```java
@Configuration
public class CsrfConfig {
    
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                // 忽略某些路径
                .ignoringRequestMatchers("/api/webhook/**")
                // 使用 Cookie 存储 CSRF Token
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                // 或存储在 Session 中
                // .csrfTokenRepository(new HttpSessionCsrfTokenRepository())
            );
        
        return http.build();
    }
}
```

前端获取 CSRF Token：

```javascript
// 从 Cookie 获取 CSRF Token
function getCsrfToken() {
    const match = document.cookie.match(/XSRF-TOKEN=([^;]+)/);
    return match ? decodeURIComponent(match[1]) : null;
}

// 发送请求时添加 CSRF Token
fetch('/api/users', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json',
        'X-XSRF-TOKEN': getCsrfToken()
    },
    body: JSON.stringify(data)
});
```

### 5.2 防止点击劫持

```java
@Configuration
public class ClickjackingConfig {
    
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .headers(headers -> headers
                // DENY: 拒绝所有 iframe
                // .frameOptions(frameOptions -> frameOptions.deny())
                
                // SAMEORIGIN: 只允许同源 iframe
                .frameOptions(frameOptions -> frameOptions.sameOrigin())
                
                // 允许特定域名
                // .addHeaderWriter(new XFrameOptionsHeaderWriter(
                //     XFrameOptionsMode.ALLOW_FROM, "https://trusted.com"))
            );
        
        return http.build();
    }
}
```

### 5.3 安全响应头

```java
@Configuration
public class SecurityHeadersConfig {
    
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .headers(headers -> headers
                // X-Content-Type-Options: 防止 MIME 嗅探
                .contentTypeOptions(contentTypeOptions -> {})
                
                // X-XSS-Protection: XSS 过滤
                .xssProtection(xss -> xss
                    .headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK)
                )
                
                // Strict-Transport-Security: 强制 HTTPS
                .httpStrictTransportSecurity(hsts -> hsts
                    .includeSubDomains(true)
                    .maxAgeInSeconds(31536000)
                )
                
                // Content-Security-Policy
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives(
                        "default-src 'self'; " +
                        "script-src 'self' 'unsafe-inline'; " +
                        "style-src 'self' 'unsafe-inline'; " +
                        "img-src 'self' data: https:;"
                    )
                )
                
                // Referrer-Policy
                .referrerPolicy(referrer -> referrer
                    .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                )
            );
        
        return http.build();
    }
}
```

## 六、测试案例

### 6.1 单元测试

```java
@ExtendWith(MockitoExtension.class)
public class SecurityServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    void testLoadUserByUsername() {
        // Given
        User user = new User();
        user.setUsername("admin");
        user.setPassword("encodedPassword");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        
        // When
        UserDetails userDetails = userService.loadUserByUsername("admin");
        
        // Then
        assertThat(userDetails.getUsername()).isEqualTo("admin");
        verify(userRepository).findByUsername("admin");
    }
    
    @Test
    void testLoadUserByUsername_NotFound() {
        // Given
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("unknown");
        });
    }
}
```

### 6.2 集成测试

```java
@SpringBootTest
@AutoConfigureMockMvc
public class SecurityIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @BeforeEach
    void setUp() {
        // 创建测试用户
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole("USER");
        userRepository.save(user);
    }
    
    @Test
    void testLoginSuccess() throws Exception {
        mockMvc.perform(formLogin("/api/auth/login")
                .user("testuser")
                .password("password"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }
    
    @Test
    void testLoginFailure() throws Exception {
        mockMvc.perform(formLogin("/api/auth/login")
                .user("testuser")
                .password("wrongpassword"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value(401));
    }
    
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
}
```

### 6.3 端到端测试

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SecurityE2ETest {
    
    @LocalServerPort
    private int port;
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void testFullAuthenticationFlow() {
        // 1. 访问受保护资源 - 应该返回 401
        ResponseEntity<String> unauthorizedResponse = restTemplate.getForEntity(
            "http://localhost:" + port + "/api/protected",
            String.class
        );
        assertThat(unauthorizedResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        
        // 2. 登录
        MultiValueMap<String, String> loginParams = new LinkedMultiValueMap<>();
        loginParams.add("username", "admin");
        loginParams.add("password", "admin123");
        
        ResponseEntity<String> loginResponse = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/auth/login",
            loginParams,
            String.class
        );
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        // 3. 获取 Session Cookie
        String cookie = loginResponse.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        
        // 4. 使用 Cookie 访问受保护资源
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.COOKIE, cookie);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        ResponseEntity<String> authorizedResponse = restTemplate.exchange(
            "http://localhost:" + port + "/api/protected",
            HttpMethod.GET,
            entity,
            String.class
        );
        assertThat(authorizedResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
```

## 七、总结

本章节涵盖了 Spring Security 的常见实战场景：

1. **用户认证**：表单登录、记住我、JWT
2. **权限控制**：URL 权限、方法权限、动态权限
3. **前后端分离**：JWT 认证、Axios 拦截器
4. **OAuth2**：第三方登录集成
5. **安全防护**：CSRF、点击劫持、安全头
6. **测试**：单元测试、集成测试、端到端测试

通过这些案例，可以快速搭建安全的 Web 应用。