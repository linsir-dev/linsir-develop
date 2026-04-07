# Authentication 核心组件

> **版本说明**：本文档基于 **Spring Security 7.0** 编写。
> 
> **7.0 主要变化**：
> - 核心接口和实现类保持稳定，无重大变化
> - 需要 Spring Framework 7.0+ 支持

## 概述

`Authentication` 是 Spring Security 中表示认证信息的核心接口。它既可以是用户提供的凭证（用于认证），也可以是当前已认证用户的信息。

```
┌─────────────────────────────────────────────────────────────┐
│                    Authentication 接口                       │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌───────────────────────────────────────────────────────┐ │
│  │  Principal (主体)                                      │ │
│  │  - 用户标识（用户名、UserDetails 对象等）               │ │
│  └───────────────────────────────────────────────────────┘ │
│                                                             │
│  ┌───────────────────────────────────────────────────────┐ │
│  │  Credentials (凭证)                                    │ │
│  │  - 密码、Token 等敏感信息                               │ │
│  │  - 认证成功后通常被清除                                 │ │
│  └───────────────────────────────────────────────────────┘ │
│                                                             │
│  ┌───────────────────────────────────────────────────────┐ │
│  │  Authorities (权限)                                    │ │
│  │  - GrantedAuthority 集合                               │ │
│  │  - 角色、权限标识                                       │ │
│  └───────────────────────────────────────────────────────┘ │
│                                                             │
│  ┌───────────────────────────────────────────────────────┐ │
│  │  authenticated (认证状态)                              │ │
│  │  - true: 已认证                                         │ │
│  │  - false: 未认证或认证中                                │ │
│  └───────────────────────────────────────────────────────┘ │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

## 接口定义

```java
public interface Authentication extends Principal, Serializable {
    
    // 获取权限集合
    Collection<? extends GrantedAuthority> getAuthorities();
    
    // 获取凭证（密码、Token）
    Object getCredentials();
    
    // 获取认证详情（IP地址、SessionId等）
    Object getDetails();
    
    // 获取主体（用户名、UserDetails）
    Object getPrincipal();
    
    // 是否已认证
    boolean isAuthenticated();
    
    // 设置认证状态
    void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException;
}
```

## 主要实现类

### 1. UsernamePasswordAuthenticationToken

最常用的认证令牌，用于用户名/密码认证。

```java
// 创建未认证的令牌（提交给 AuthenticationManager 前）
UsernamePasswordAuthenticationToken token = 
    new UsernamePasswordAuthenticationToken(
        "username",     // principal
        "password"      // credentials
    );

// 创建已认证的令牌（认证成功后）
UsernamePasswordAuthenticationToken authenticatedToken = 
    new UsernamePasswordAuthenticationToken(
        userDetails,                    // principal
        null,                           // credentials（已清除）
        AuthorityUtils.createAuthorityList("ROLE_USER")  // authorities
    );
```

### 2. AnonymousAuthenticationToken

匿名用户认证令牌。

```java
AnonymousAuthenticationToken anonymousToken = 
    new AnonymousAuthenticationToken(
        "anonymousKey",                 // key
        "anonymousUser",                // principal
        AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")
    );
```

### 3. RememberMeAuthenticationToken

记住我功能使用的认证令牌。

```java
RememberMeAuthenticationToken rememberMeToken = 
    new RememberMeAuthenticationToken(
        "rememberMeKey",                // key
        userDetails,                    // principal
        AuthorityUtils.createAuthorityList("ROLE_USER")
    );
```

### 4. PreAuthenticatedAuthenticationToken

预认证场景使用的令牌（如 SSO、X.509）。

```java
PreAuthenticatedAuthenticationToken preAuthToken = 
    new PreAuthenticatedAuthenticationToken(
        userDetails,                    // principal
        credentials,                    // credentials
        AuthorityUtils.createAuthorityList("ROLE_USER")
    );
```

## GrantedAuthority 权限

### 接口定义

```java
public interface GrantedAuthority extends Serializable {
    String getAuthority();  // 返回权限字符串，如 "ROLE_USER"
}
```

### 创建方式

```java
// 方式1：使用 SimpleGrantedAuthority
GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_ADMIN");

// 方式2：使用 AuthorityUtils
List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(
    "ROLE_USER", "ROLE_ADMIN", "read", "write"
);

// 方式3：从逗号分隔的字符串创建
List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(
    "ROLE_USER,ROLE_ADMIN,read,write"
);
```

### 角色与权限

| 类型 | 示例 | 说明 |
|------|------|------|
| **角色** | `ROLE_USER`, `ROLE_ADMIN` | 通常以 ROLE_ 前缀开头 |
| **权限** | `read`, `write`, `delete` | 细粒度的操作权限 |

## 在代码中获取 Authentication

### 方式1：通过 SecurityContextHolder

```java
Authentication authentication = SecurityContextHolder
    .getContext()
    .getAuthentication();

String username = authentication.getName();
Collection<? extends GrantedAuthority> authorities = 
    authentication.getAuthorities();
```

### 方式2：通过方法参数注入

```java
@GetMapping("/profile")
public String profile(Authentication authentication) {
    String username = authentication.getName();
    return "profile";
}
```

### 方式3：通过 @AuthenticationPrincipal

```java
@GetMapping("/profile")
public String profile(@AuthenticationPrincipal UserDetails userDetails) {
    String username = userDetails.getUsername();
    return "profile";
}
```

## 自定义 Authentication

```java
public class SmsCodeAuthenticationToken extends AbstractAuthenticationToken {
    
    private final Object principal;  // 手机号
    private String smsCode;          // 短信验证码
    
    // 未认证状态
    public SmsCodeAuthenticationToken(String phone, String smsCode) {
        super(null);
        this.principal = phone;
        this.smsCode = smsCode;
        setAuthenticated(false);
    }
    
    // 已认证状态
    public SmsCodeAuthenticationToken(String phone, 
            Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = phone;
        this.smsCode = null;
        super.setAuthenticated(true);
    }
    
    @Override
    public Object getCredentials() {
        return smsCode;
    }
    
    @Override
    public Object getPrincipal() {
        return principal;
    }
}
```

## 认证状态流转

```
┌─────────────────────────────────────────────────────────────────┐
│                     认证状态流转图                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   ┌──────────────────┐                                          │
│   │   未认证状态      │                                          │
│   │  isAuthenticated │                                          │
│   │      = false     │                                          │
│   └────────┬─────────┘                                          │
│            │                                                    │
│            │ 提交给 AuthenticationManager                       │
│            ↓                                                    │
│   ┌──────────────────┐                                          │
│   │   认证处理中      │                                          │
│   │  Authentication  │                                          │
│   │    Provider      │                                          │
│   └────────┬─────────┘                                          │
│            │                                                    │
│            │ 认证成功                                            │
│            ↓                                                    │
│   ┌──────────────────┐                                          │
│   │   已认证状态      │                                          │
│   │  isAuthenticated │                                          │
│   │      = true      │                                          │
│   │                  │                                          │
│   │  credentials = null (清除敏感信息)                           │
│   │  authorities = [权限列表]                                    │
│   └──────────────────┘                                          │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```
