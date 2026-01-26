# Spring Security的原理？

## 1. Spring Security架构概述

Spring Security是一个基于Servlet过滤器链的安全框架，通过一系列过滤器来处理请求的安全检查。理解Spring Security的原理对于深入使用和定制Spring Security非常重要。

## 2. 核心架构

### 2.1 过滤器链（Filter Chain）
Spring Security的核心是一系列Servlet过滤器，按顺序处理每个请求。

```
请求 → Filter1 → Filter2 → Filter3 → ... → FilterN → 应用
```

### 2.2 安全上下文（SecurityContext）
存储当前认证用户的信息。

```java
SecurityContext context = SecurityContextHolder.getContext();
Authentication authentication = context.getAuthentication();
```

### 2.3 认证管理器（AuthenticationManager）
处理认证请求的核心接口。

```java
public interface AuthenticationManager {
    Authentication authenticate(Authentication authentication) 
        throws AuthenticationException;
}
```

## 3. 认证流程

### 3.1 认证流程图
```
用户提交认证信息
    ↓
UsernamePasswordAuthenticationFilter
    ↓
AuthenticationManager
    ↓
AuthenticationProvider
    ↓
UserDetailsService
    ↓
PasswordEncoder
    ↓
认证成功/失败
```

### 3.2 认证步骤详解

#### 步骤1：用户提交认证信息
用户通过登录表单或HTTP Basic等方式提交用户名和密码。

```java
POST /login
{
    "username": "admin",
    "password": "password"
}
```

#### 步骤2：过滤器拦截请求
`UsernamePasswordAuthenticationFilter`拦截登录请求，创建认证令牌。

```java
public class UsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request) {
        String username = obtainUsername(request);
        String password = obtainPassword(request);

        UsernamePasswordAuthenticationToken authRequest = 
            new UsernamePasswordAuthenticationToken(username, password);
        
        return getAuthenticationManager().authenticate(authRequest);
    }
}
```

#### 步骤3：AuthenticationManager处理认证
`AuthenticationManager`委托给`AuthenticationProvider`处理认证。

```java
public class ProviderManager implements AuthenticationManager {
    private List<AuthenticationProvider> providers;

    @Override
    public Authentication authenticate(Authentication authentication) {
        for (AuthenticationProvider provider : providers) {
            if (provider.supports(authentication.getClass())) {
                return provider.authenticate(authentication);
            }
        }
        throw new ProviderNotFoundException("No AuthenticationProvider found");
    }
}
```

#### 步骤4：AuthenticationProvider验证凭证
`AuthenticationProvider`调用`UserDetailsService`加载用户信息，并验证密码。

```java
public class DaoAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) {
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
}
```

#### 步骤5：UserDetailsService加载用户信息
`UserDetailsService`从数据源加载用户详细信息。

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) 
            throws UsernameNotFoundException {
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

#### 步骤6：密码验证
`PasswordEncoder`验证用户输入的密码是否正确。

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

#### 步骤7：认证成功/失败处理
认证成功后，将认证信息存储到`SecurityContext`。

```java
public class UsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    @Override
    protected void successfulAuthentication(HttpServletRequest request, 
            HttpServletResponse response, Authentication authResult) {
        SecurityContextHolder.getContext().setAuthentication(authResult);
        getRememberMeServices().loginSuccess(request, response, authResult);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, 
            HttpServletResponse response, AuthenticationException failed) {
        SecurityContextHolder.clearContext();
        getRememberMeServices().loginFail(request, response);
    }
}
```

## 4. 授权流程

### 4.1 授权流程图
```
请求到达
    ↓
FilterSecurityInterceptor
    ↓
AccessDecisionManager
    ↓
AccessDecisionVoter
    ↓
允许/拒绝访问
```

### 4.2 授权步骤详解

#### 步骤1：请求到达过滤器
`FilterSecurityInterceptor`拦截请求，进行安全检查。

```java
public class FilterSecurityInterceptor extends FilterSecurityInterceptor {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
            FilterChain chain) throws IOException, ServletException {
        FilterInvocation fi = new FilterInvocation(request, response, chain);
        invoke(fi);
    }

    protected void invoke(FilterInvocation fi) throws IOException, ServletException {
        InterceptorStatusToken token = super.beforeInvocation(fi);
        try {
            fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
        } finally {
            super.finallyInvocation(token);
        }
    }
}
```

#### 步骤2：AccessDecisionManager决策
`AccessDecisionManager`委托给`AccessDecisionVoter`进行决策。

```java
public class AffirmativeBased implements AccessDecisionManager {
    private List<AccessDecisionVoter<?>> decisionVoters;

    @Override
    public void decide(Authentication authentication, Object object,
            Collection<ConfigAttribute> configAttributes) throws AccessDeniedException {
        int deny = 0;
        for (AccessDecisionVoter voter : decisionVoters) {
            int result = voter.vote(authentication, object, configAttributes);
            if (result == AccessDecisionVoter.ACCESS_GRANTED) {
                return;
            } else if (result == AccessDecisionVoter.ACCESS_DENIED) {
                deny++;
            }
        }
        if (deny > 0) {
            throw new AccessDeniedException("Access denied");
        }
    }
}
```

#### 步骤3：AccessDecisionVoter投票
`AccessDecisionVoter`根据配置决定是否允许访问。

```java
public class RoleVoter implements AccessDecisionVoter<ConfigAttribute> {
    @Override
    public int vote(Authentication authentication, Object object, 
            Collection<ConfigAttribute> attributes) {
        if (authentication == null) {
            return ACCESS_DENIED;
        }

        for (ConfigAttribute attribute : attributes) {
            if (this.supports(attribute)) {
                String requiredRole = attribute.getAttribute();
                for (GrantedAuthority authority : authentication.getAuthorities()) {
                    if (requiredRole.equals(authority.getAuthority())) {
                        return ACCESS_GRANTED;
                    }
                }
                return ACCESS_DENIED;
            }
        }
        return ACCESS_ABSTAIN;
    }
}
```

## 5. 过滤器链详解

### 5.1 常用过滤器
| 过滤器 | 作用 |
|--------|------|
| `SecurityContextPersistenceFilter` | 维护SecurityContext |
| `LogoutFilter` | 处理登出请求 |
| `UsernamePasswordAuthenticationFilter` | 处理用户名密码认证 |
| `BasicAuthenticationFilter` | 处理HTTP Basic认证 |
| `RequestCacheAwareFilter` | 缓存请求 |
| `SecurityContextHolderAwareRequestFilter` | 设置请求的安全上下文 |
| `AnonymousAuthenticationFilter` | 匿名用户认证 |
| `SessionManagementFilter` | 会话管理 |
| `ExceptionTranslationFilter` | 处理安全异常 |
| `FilterSecurityInterceptor` | 访问决策 |

### 5.2 过滤器执行顺序
```java
public enum FilterOrderRegistration {
    CHANNEL_FILTER,
    CONCURRENT_SESSION_FILTER,
    SECURITY_CONTEXT_FILTER,
    LOGOUT_FILTER,
    X509_FILTER,
    PRE_AUTH_FILTER,
    CAS_FILTER,
    FORM_LOGIN_FILTER,
    BASIC_AUTH_FILTER,
    SERVLET_API_SUPPORT_FILTER,
    JAAS_API_SUPPORT_FILTER,
    REMEMBER_ME_FILTER,
    ANONYMOUS_FILTER,
    SESSION_MANAGEMENT_FILTER,
    EXCEPTION_TRANSLATION_FILTER,
    FILTER_SECURITY_INTERCEPTOR,
    SWITCH_USER_FILTER,
}
```

## 6. 安全表达式

### 6.1 常用表达式
```java
hasRole('ADMIN')              // 拥有ADMIN角色
hasAnyRole('ADMIN', 'USER')   // 拥有ADMIN或USER角色
hasAuthority('READ')            // 拥有READ权限
hasAnyAuthority('READ', 'WRITE') // 拥有READ或WRITE权限
permitAll()                     // 允许所有访问
denyAll()                       // 拒绝所有访问
isAnonymous()                    // 匿名用户
isAuthenticated()                 // 已认证用户
isRememberMe()                   // 记住我用户
isFullyAuthenticated()           // 完全认证用户（非记住我）
hasIpAddress('192.168.1.0/24')  // 指定IP地址
```

### 6.2 表达式使用
```java
@PreAuthorize("hasRole('ADMIN')")
public void adminMethod() {
}

@PreAuthorize("#username == authentication.name")
public void updateUser(String username, User user) {
}

@PreAuthorize("hasPermission(#id, 'READ')")
public User getUser(Long id) {
}
```

## 7. 事件机制

### 7.1 认证事件
```java
public class AuthenticationSuccessEvent extends ApplicationEvent {
    private final Authentication authentication;
}

public class AuthenticationFailureEvent extends ApplicationEvent {
    private final AuthenticationException exception;
    private final Authentication authentication;
}
```

### 7.2 授权事件
```java
public class AuthorizationSuccessEvent extends ApplicationEvent {
    private final Authentication authentication;
    private final ConfigAttribute configAttribute;
    private final Object object;
}

public class AuthorizationFailureEvent extends ApplicationEvent {
    private final Authentication authentication;
    private final ConfigAttribute configAttribute;
    private final Object object;
    private final AccessDeniedException accessDeniedException;
}
```

### 7.3 事件监听
```java
@Component
public class SecurityEventListener {
    @EventListener
    public void handleAuthenticationSuccess(AuthenticationSuccessEvent event) {
        log.info("User {} logged in successfully", 
                event.getAuthentication().getName());
    }

    @EventListener
    public void handleAuthenticationFailure(AuthenticationFailureEvent event) {
        log.warn("Authentication failed for user {}", 
                event.getAuthentication().getName());
    }

    @EventListener
    public void handleAuthorizationFailure(AuthorizationFailureEvent event) {
        log.warn("Authorization failed for user {}", 
                event.getAuthentication().getName());
    }
}
```

## 8. 自定义扩展

### 8.1 自定义过滤器
```java
public class CustomFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
            HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        if (token != null) {
            Authentication auth = new CustomAuthenticationToken(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(request, response);
    }
}
```

### 8.2 自定义AuthenticationProvider
```java
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) 
            throws AuthenticationException {
        String username = authentication.getName();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        
        return new CustomAuthenticationToken(userDetails);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return CustomAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
```

### 8.3 自定义AccessDecisionVoter
```java
public class CustomAccessDecisionVoter implements AccessDecisionVoter<ConfigAttribute> {
    @Override
    public int vote(Authentication authentication, Object object, 
            Collection<ConfigAttribute> attributes) {
        if (authentication == null) {
            return ACCESS_DENIED;
        }

        for (ConfigAttribute attribute : attributes) {
            if (attribute.getAttribute().equals("CUSTOM_CHECK")) {
                return customVote(authentication, object);
            }
        }
        return ACCESS_ABSTAIN;
    }

    private int customVote(Authentication authentication, Object object) {
        return ACCESS_GRANTED;
    }
}
```

## 9. 总结

Spring Security的原理可以总结为以下几点：

1. **过滤器链架构**：通过一系列Servlet过滤器处理请求
2. **认证流程**：用户提交认证信息 → 过滤器拦截 → AuthenticationManager → AuthenticationProvider → UserDetailsService → 密码验证
3. **授权流程**：请求到达 → FilterSecurityInterceptor → AccessDecisionManager → AccessDecisionVoter → 允许/拒绝
4. **安全上下文**：存储当前认证用户的信息
5. **事件机制**：发布认证和授权事件
6. **自定义扩展**：支持自定义过滤器、AuthenticationProvider、AccessDecisionVoter等

理解Spring Security的原理对于深入使用和定制Spring Security非常重要。通过理解其架构和流程，可以更好地满足项目的安全需求。