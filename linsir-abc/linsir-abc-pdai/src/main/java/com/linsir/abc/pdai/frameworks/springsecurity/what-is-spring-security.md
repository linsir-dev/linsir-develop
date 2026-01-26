# 什么是Spring Security？核心功能？

## 1. Spring Security概述

Spring Security是一个功能强大且高度可定制的身份验证和访问控制框架，它是保护基于Spring的应用程序的事实标准。Spring Security专注于为Java应用程序提供身份验证和授权。

## 2. 核心概念

### 2.1 认证（Authentication）
认证是验证用户身份的过程，确认"你是谁"。

**示例：**
```java
Authentication authentication = new UsernamePasswordAuthenticationToken(
    username, 
    password
);
```

### 2.2 授权（Authorization）
授权是验证用户权限的过程，确认"你能做什么"。

**示例：**
```java
@PreAuthorize("hasRole('ADMIN')")
public void adminOperation() {
}
```

### 2.3 安全上下文（SecurityContext）
安全上下文存储当前认证用户的信息。

**示例：**
```java
Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
String username = authentication.getName();
```

## 3. 核心功能

### 3.1 身份验证
Spring Security支持多种身份验证方式：

**表单登录：**
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/home")
                .permitAll()
            );
        return http.build();
    }
}
```

**HTTP Basic认证：**
```java
http
    .httpBasic(Customizer.withDefaults())
    .authorizeHttpRequests(auth -> auth
        .anyRequest().authenticated()
    );
```

**JWT认证：**
```java
http
    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
    .authorizeHttpRequests(auth -> auth
        .anyRequest().authenticated()
    );
```

### 3.2 授权
Spring Security提供多种授权方式：

**基于角色的访问控制：**
```java
@PreAuthorize("hasRole('ADMIN')")
public void adminMethod() {
}

@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public void adminOrUserMethod() {
}
```

**基于权限的访问控制：**
```java
@PreAuthorize("hasAuthority('READ_PRIVILEGE')")
public void readMethod() {
}

@PreAuthorize("hasAuthority('WRITE_PRIVILEGE')")
public void writeMethod() {
}
```

**方法级安全：**
```java
@Secured("ROLE_ADMIN")
public void securedMethod() {
}

@RolesAllowed("ROLE_ADMIN")
public void rolesAllowedMethod() {
}
```

### 3.3 CSRF保护
跨站请求伪造（CSRF）保护。

**配置：**
```java
http
    .csrf(csrf -> csrf
        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        .ignoringRequestMatchers("/api/public/**")
    );
```

### 3.4 CORS支持
跨源资源共享（CORS）支持。

**配置：**
```java
http
    .cors(cors -> cors
        .configurationSource(corsConfigurationSource())
    );

@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("*"));
    configuration.setAllowedMethods(Arrays.asList("*"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

### 3.5 会话管理
会话创建、固定和过期管理。

**配置：**
```java
http
    .sessionManagement(session -> session
        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
        .maximumSessions(1)
        .maxSessionsPreventsLogin(false)
        .sessionFixation().migrateSession()
    );
```

### 3.6 密码编码
密码加密和验证。

**配置：**
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

**使用：**
```java
String encodedPassword = passwordEncoder.encode("password");
boolean matches = passwordEncoder.matches("password", encodedPassword);
```

### 3.7 记住我功能
记住用户登录状态。

**配置：**
```java
http
    .rememberMe(remember -> remember
        .key("uniqueAndSecret")
        .tokenValiditySeconds(86400)
        .rememberMeCookieName("remember-me-cookie")
        .rememberMeParameter("remember-me")
        .userDetailsService(userDetailsService)
    );
```

### 3.8 登出
用户登出功能。

**配置：**
```java
http
    .logout(logout -> logout
        .logoutUrl("/logout")
        .logoutSuccessUrl("/login?logout")
        .invalidateHttpSession(true)
        .deleteCookies("JSESSIONID", "remember-me-cookie")
        .addLogoutHandler(new CustomLogoutHandler())
    );
```

## 4. 核心组件

### 4.1 SecurityFilterChain
安全过滤器链，处理所有安全相关的请求。

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/public/**").permitAll()
            .anyRequest().authenticated()
        )
        .formLogin(Customizer.withDefaults())
        .httpBasic(Customizer.withDefaults());
    return http.build();
}
```

### 4.2 UserDetailsService
加载用户特定数据的核心接口。

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRoles().toArray(new String[0]))
                .build();
    }
}
```

### 4.3 PasswordEncoder
密码编码接口。

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

### 4.4 AuthenticationProvider
认证提供者，处理认证逻辑。

```java
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (passwordEncoder.matches(password, userDetails.getPassword())) {
            return new UsernamePasswordAuthenticationToken(
                username, 
                password, 
                userDetails.getAuthorities()
            );
        }

        throw new BadCredentialsException("Invalid credentials");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
```

### 4.5 AccessDecisionManager
访问决策管理器，决定用户是否有权限访问资源。

```java
@Bean
public AccessDecisionManager accessDecisionManager() {
    List<AccessDecisionVoter<?>> decisionVoters = new ArrayList<>();
    decisionVoters.add(new RoleVoter());
    decisionVoters.add(new AuthenticatedVoter());
    decisionVoters.add(new WebExpressionVoter());
    return new AffirmativeBased(decisionVoters);
}
```

## 5. 安全注解

### 5.1 @PreAuthorize
方法执行前进行权限检查。

```java
@PreAuthorize("hasRole('ADMIN')")
public void adminMethod() {
}

@PreAuthorize("#username == authentication.name")
public void updateUser(String username, User user) {
}
```

### 5.2 @PostAuthorize
方法执行后进行权限检查。

```java
@PostAuthorize("returnObject.owner == authentication.name")
public User getUser(Long id) {
    return userRepository.findById(id);
}
```

### 5.3 @PreFilter
方法执行前过滤返回值。

```java
@PreFilter("filterObject.owner == authentication.name")
public List<User> getUsers() {
    return userRepository.findAll();
}
```

### 5.4 @PostFilter
方法执行后过滤返回值。

```java
@PostFilter("filterObject.owner == authentication.name")
public List<User> getUsers() {
    return userRepository.findAll();
}
```

### 5.5 @Secured
使用角色进行方法级安全。

```java
@Secured("ROLE_ADMIN")
public void adminMethod() {
}
```

### 5.6 @RolesAllowed
JSR-250注解，指定允许的角色。

```java
@RolesAllowed("ROLE_ADMIN")
public void adminMethod() {
}
```

## 6. 完整配置示例

### 6.1 基本配置
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/home", "/register", "/login").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/user/**").hasAnyRole("ADMIN", "USER")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/home")
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .exceptionHandling(exception -> exception
                .accessDeniedPage("/403")
            );
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### 6.2 JWT配置
```java
@Configuration
@EnableWebSecurity
public class JwtSecurityConfig {
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
```

## 7. 最佳实践

### 7.1 使用强密码编码器
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
}
```

### 7.2 最小权限原则
只授予用户完成工作所需的最小权限。

### 7.3 定期更新依赖
保持Spring Security和相关依赖的最新版本。

### 7.4 启用CSRF保护
对于Web应用，启用CSRF保护。

### 7.5 使用HTTPS
在生产环境中使用HTTPS保护通信。

### 7.6 记录安全事件
记录认证和授权事件。

```java
@Component
public class SecurityEventListener {
    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        log.info("User {} logged in successfully", event.getAuthentication().getName());
    }

    @EventListener
    public void onAuthenticationFailure(AuthenticationFailureEvent event) {
        log.warn("Authentication failed for user {}", event.getAuthentication().getName());
    }
}
```

## 8. 总结

Spring Security是一个功能强大且高度可定制的安全框架，核心功能包括：

1. **身份验证**：支持多种认证方式
2. **授权**：基于角色和权限的访问控制
3. **CSRF保护**：防止跨站请求伪造攻击
4. **CORS支持**：支持跨源资源共享
5. **会话管理**：管理用户会话
6. **密码编码**：安全的密码存储和验证
7. **记住我功能**：记住用户登录状态
8. **登出功能**：安全的用户登出

Spring Security是保护Spring应用程序的标准选择，提供了全面的安全解决方案。