# AuthenticationManager 架构

> **版本说明**：本文档基于 **Spring Security 7.0** 编写。
> 
> **7.0 主要变化**：
> - ProviderManager 和 AuthenticationProvider 接口保持稳定
> - 需要 Spring Framework 7.0+ 支持

## 概述

`AuthenticationManager` 是 Spring Security 中定义过滤器如何执行认证的 API。它接收一个 `Authentication` 对象作为输入，如果认证成功则返回一个已认证的 `Authentication` 对象，如果失败则抛出 `AuthenticationException`。

```
┌─────────────────────────────────────────────────────────────┐
│                  AuthenticationManager                       │
│                     (认证管理器接口)                          │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│   Authentication authenticate(Authentication authentication)│
│        throws AuthenticationException;                      │
│                                                             │
│   输入：包含用户凭证的 Authentication 对象                    │
│   输出：已认证的 Authentication 对象                          │
│   异常：AuthenticationException（认证失败）                  │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

## 核心实现类

### ProviderManager

`ProviderManager` 是 `AuthenticationManager` 最常用的实现。它维护一个 `AuthenticationProvider` 列表，依次尝试每个 provider 进行认证。

```
┌─────────────────────────────────────────────────────────────┐
│                    ProviderManager                           │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│   ┌─────────────────────────────────────────────────────┐   │
│   │              List<AuthenticationProvider>            │   │
│   │                                                      │   │
│   │   ┌─────────────────────────────────────────────┐   │   │
│   │   │  DaoAuthenticationProvider                  │   │   │
│   │   │  (用户名/密码认证)                           │   │   │
│   │   └─────────────────────────────────────────────┘   │   │
│   │                                                      │   │
│   │   ┌─────────────────────────────────────────────┐   │   │
│   │   │  JwtAuthenticationProvider                  │   │   │
│   │   │  (JWT Token 认证)                           │   │   │
│   │   └─────────────────────────────────────────────┘   │   │
│   │                                                      │   │
│   │   ┌─────────────────────────────────────────────┐   │   │
│   │   │  RememberMeAuthenticationProvider           │   │   │
│   │   │  (记住我认证)                               │   │   │
│   │   └─────────────────────────────────────────────┘   │   │
│   │                                                      │   │
│   │   ┌─────────────────────────────────────────────┐   │   │
│   │   │  AnonymousAuthenticationProvider            │   │   │
│   │   │  (匿名用户认证)                             │   │   │
│   │   └─────────────────────────────────────────────┘   │   │
│   │                                                      │   │
│   └─────────────────────────────────────────────────────┘   │
│                                                             │
│   ┌─────────────────────────────────────────────────────┐   │
│   │  可选的 parent AuthenticationManager                │   │
│   │  (所有 provider 都失败时的后备)                      │   │
│   └─────────────────────────────────────────────────────┘   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 认证流程

```
┌─────────────┐     ┌──────────────────┐     ┌─────────────────────┐
│  认证请求    │     │  ProviderManager │     │ AuthenticationProvider│
│             │────>│                  │────>│                     │
│  Username   │     │  遍历 providers  │     │  supports()?        │
│  Password   │     │  列表            │     │  是否支持该认证类型   │
└─────────────┘     └──────────────────┘     └─────────────────────┘
                                                        │
                              ┌─────────────────────────┘
                              │ 不支持，尝试下一个 provider
                              ↓
                        ┌─────────────┐
                        │   支持      │
                        │ authenticate()│
                        │ 执行认证    │
                        └──────┬──────┘
                               │
              ┌────────────────┼────────────────┐
              │                │                │
              ↓                ↓                ↓
        ┌─────────┐     ┌─────────┐     ┌─────────┐
        │ 认证成功 │     │ 认证失败 │     │ 不支持  │
        │ 返回    │     │ 抛异常  │     │ 下一个  │
        │Authentication│ │         │     │ provider│
        └─────────┘     └─────────┘     └─────────┘
```

## AuthenticationProvider

`AuthenticationProvider` 是执行特定类型认证的组件接口。

### 接口定义

```java
public interface AuthenticationProvider {
    
    // 执行认证
    Authentication authenticate(Authentication authentication) 
        throws AuthenticationException;
    
    // 判断是否支持该认证类型
    boolean supports(Class<?> authentication);
}
```

### 主要实现类

| Provider | 说明 | 支持的 Authentication |
|----------|------|----------------------|
| **DaoAuthenticationProvider** | 基于数据库的认证 | UsernamePasswordAuthenticationToken |
| **JwtAuthenticationProvider** | JWT Token 认证 | BearerTokenAuthenticationToken |
| **RememberMeAuthenticationProvider** | 记住我认证 | RememberMeAuthenticationToken |
| **AnonymousAuthenticationProvider** | 匿名用户认证 | AnonymousAuthenticationToken |
| **PreAuthenticatedAuthenticationProvider** | 预认证 | PreAuthenticatedAuthenticationToken |
| **LdapAuthenticationProvider** | LDAP 认证 | UsernamePasswordAuthenticationToken |

### DaoAuthenticationProvider 详解

```java
public class DaoAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
    
    private UserDetailsService userDetailsService;
    private PasswordEncoder passwordEncoder;
    
    @Override
    protected UserDetails retrieveUser(String username, 
            UsernamePasswordAuthenticationToken authentication) 
            throws AuthenticationException {
        // 1. 通过 UserDetailsService 加载用户
        return userDetailsService.loadUserByUsername(username);
    }
    
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
            UsernamePasswordAuthenticationToken authentication) 
            throws AuthenticationException {
        // 2. 验证密码
        String presentedPassword = authentication.getCredentials().toString();
        if (!passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
            throw new BadCredentialsException("密码错误");
        }
    }
}
```

## 配置 ProviderManager

### 方式1：使用 HttpSecurity 配置（推荐）

```java
@Bean
SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authenticationProvider(customAuthenticationProvider())
        .authorizeHttpRequests(auth -> auth
            .anyRequest().authenticated()
        )
        .formLogin(Customizer.withDefaults());
    
    return http.build();
}

@Bean
AuthenticationProvider customAuthenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(userDetailsService());
    provider.setPasswordEncoder(passwordEncoder());
    return provider;
}
```

### 方式2：手动构建 ProviderManager

```java
@Bean
AuthenticationManager authenticationManager() {
    List<AuthenticationProvider> providers = Arrays.asList(
        daoAuthenticationProvider(),
        jwtAuthenticationProvider(),
        rememberMeAuthenticationProvider()
    );
    
    return new ProviderManager(providers);
}
```

### 方式3：使用 AuthenticationManagerBuilder

```java
@Autowired
void configure(AuthenticationManagerBuilder builder) throws Exception {
    builder
        .userDetailsService(userDetailsService())
        .passwordEncoder(passwordEncoder())
        .and()
        .authenticationProvider(customAuthenticationProvider());
}
```

## 自定义 AuthenticationProvider

```java
@Component
public class SmsCodeAuthenticationProvider implements AuthenticationProvider {
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
    private SmsCodeService smsCodeService;
    
    @Override
    public Authentication authenticate(Authentication authentication) 
            throws AuthenticationException {
        String phone = authentication.getPrincipal().toString();
        String code = authentication.getCredentials().toString();
        
        // 1. 验证短信验证码
        if (!smsCodeService.verify(phone, code)) {
            throw new BadCredentialsException("短信验证码错误");
        }
        
        // 2. 加载用户信息
        UserDetails userDetails = userDetailsService.loadUserByUsername(phone);
        
        // 3. 创建已认证的 Authentication
        return new SmsCodeAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
        );
    }
    
    @Override
    public boolean supports(Class<?> authentication) {
        return SmsCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
```

## 异常处理

| 异常类型 | 说明 |
|----------|------|
| **BadCredentialsException** | 凭证错误（用户名或密码错误）|
| **UsernameNotFoundException** | 用户不存在 |
| **AccountExpiredException** | 账户过期 |
| **LockedException** | 账户被锁定 |
| **DisabledException** | 账户被禁用 |
| **CredentialsExpiredException** | 凭证过期 |
| **InsufficientAuthenticationException** | 权限不足 |
| **AuthenticationServiceException** | 认证服务异常 |

## 总结

```
┌─────────────────────────────────────────────────────────────┐
│                  认证管理器架构总结                           │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  AuthenticationManager (接口)                               │
│       │                                                     │
│       ▼                                                     │
│  ProviderManager (实现) ──▶ 维护 AuthenticationProvider 列表 │
│       │                                                     │
│       ├──▶ DaoAuthenticationProvider (用户名/密码)          │
│       ├──▶ JwtAuthenticationProvider (JWT Token)            │
│       ├──▶ RememberMeAuthenticationProvider (记住我)        │
│       └──▶ ... 其他 Provider                                │
│                                                             │
│  每个 Provider:                                             │
│  - supports(): 判断是否支持该认证类型                        │
│  - authenticate(): 执行具体认证逻辑                          │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```
