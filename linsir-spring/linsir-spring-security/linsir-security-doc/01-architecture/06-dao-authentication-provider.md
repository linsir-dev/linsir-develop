# DaoAuthenticationProvider 配置指南

> **版本说明**：本文档基于 **Spring Security 7.0** 编写。

## 概述

`DaoAuthenticationProvider` 是 Spring Security 中最常用的 AuthenticationProvider 实现，用于基于用户名和密码的认证。它从 `UserDetailsService` 加载用户数据，并使用 `PasswordEncoder` 验证密码。

## 核心组件

```
┌─────────────────────────────────────────────────────────────┐
│              DaoAuthenticationProvider 架构                  │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌──────────────────┐                                      │
│  │  Authentication  │  输入：UsernamePasswordAuthenticationToken
│  │    (请求)        │  包含：username, password            │
│  └────────┬─────────┘                                      │
│           │                                                 │
│           ▼                                                 │
│  ┌──────────────────────────────────────────────────────┐  │
│  │        DaoAuthenticationProvider                     │  │
│  │  ┌────────────────────────────────────────────────┐  │  │
│  │  │  1. 从 UserDetailsService 加载 UserDetails     │  │  │
│  │  │  2. 使用 PasswordEncoder 验证密码              │  │  │
│  │  │  3. 创建已认证的 Authentication 对象           │  │  │
│  │  └────────────────────────────────────────────────┘  │  │
│  └──────────────────────────────────────────────────────┘  │
│           │                                                 │
│           ├───▶ UserDetailsService                          │
│           │     └───▶ 从数据库/内存加载用户信息             │
│           │                                                 │
│           └───▶ PasswordEncoder (BCrypt)                    │
│                 └───▶ 密码哈希验证                          │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

## 配置步骤

### 1. 创建 UserDetailsService 实现

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Override
    public UserDetails loadUserByUsername(String username) 
            throws UsernameNotFoundException {
        // 从数据库查询用户
        if ("admin".equals(username)) {
            return User.builder()
                    .username("admin")
                    .password("{bcrypt}" + encodedPassword)  // BCrypt 加密
                    .roles("ADMIN", "USER")
                    .build();
        }
        throw new UsernameNotFoundException("用户不存在：" + username);
    }
}
```

### 2. 配置 DaoAuthenticationProvider

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    private final CustomUserDetailsService userDetailsService;
    
    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
    
    @Bean
    DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
    
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### 3. 配置表单登录

```java
@Bean
SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/public/**").permitAll()
            .anyRequest().authenticated()
        )
        .formLogin(form -> form
            .loginPage("/login")
            .defaultSuccessUrl("/home", true)
            .failureUrl("/login?error=true")
            .permitAll()
        );
    
    return http.build();
}
```

## 用户数据说明

### 密码格式

Spring Security 支持多种密码编码格式，通过前缀标识：

- `{bcrypt}` - BCrypt 强哈希（推荐）
- `{noop}` - 明文密码（仅用于测试）
- `{sha256}` - SHA-256 哈希
- `{pbkdf2}` - PBKDF2 哈希

### 示例用户

```java
// 使用 BCrypt 加密（推荐）
String encodedPassword = "{bcrypt}$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKTm8QjJpKlL9h6jF0F5F5F5F5F5";

// 使用明文密码（仅测试用）
String rawPassword = "{noop}password123";
```

## 测试方法

### 1. 表单登录测试

访问 `http://localhost:8080/login`

- 用户名：`admin`
- 密码：`admin123`

### 2. HTTP Basic 认证测试

```bash
curl -u admin:admin123 http://localhost:8080/api/auth/current
```

### 3. 使用测试接口

```bash
# 检查当前用户
Invoke-WebRequest -Uri http://localhost:8080/api/auth/current -UseBasicParsing | Select-Object -ExpandProperty Content

# 访问受保护资源
Invoke-WebRequest -Uri http://localhost:8080/api/auth/protected -UseBasicParsing | Select-Object -ExpandProperty Content
```

## 工作流程

```
1. 用户提交登录表单（username, password）
         │
         ▼
2. UsernamePasswordAuthenticationFilter 拦截请求
         │
         ▼
3. ProviderManager 调用 DaoAuthenticationProvider
         │
         ├─▶ 从 UserDetailsService 加载 UserDetails
         │        │
         │        └─▶ 查询数据库获取用户信息
         │
         └─▶ 使用 PasswordEncoder 验证密码
                  │
                  └─▶ BCrypt.checkpw(plain, encoded)
         │
         ▼
4. 认证成功 → 创建 UsernamePasswordAuthenticationToken
         │
         ▼
5. SecurityContextPersistenceFilter 保存 SecurityContext 到 Session
         │
         ▼
6. 重定向到成功页面
```

## 完整示例代码

### CustomUserDetailsService.java

```java
package com.linsir.security.config;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) 
            throws UsernameNotFoundException {
        
        if ("admin".equals(username)) {
            return User.builder()
                    .username("admin")
                    .password("{bcrypt}$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKTm8QjJpKlL9h6jF0F5F5F5F5F5")
                    .roles("ADMIN", "USER")
                    .build();
        } else if ("user".equals(username)) {
            return User.builder()
                    .username("user")
                    .password("{bcrypt}$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKTm8QjJpKlL9h6jF0F5F5F5F5F5")
                    .roles("USER")
                    .build();
        } else {
            throw new UsernameNotFoundException("用户不存在：" + username);
        }
    }
}
```

### SecurityConfig.java

```java
package com.linsir.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/api/hello", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .httpBasic(basic -> {});
        
        return http.build();
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService() {
        return userDetailsService;
    }
}
```

## 注意事项

1. **密码加密**：生产环境必须使用 BCrypt 等强哈希算法
2. **HTTPS**：登录接口必须使用 HTTPS 传输
3. **CSRF 保护**：生产环境应启用 CSRF 保护
4. **会话管理**：配置适当的 Session 超时时间
5. **错误处理**：统一处理认证异常，避免信息泄露

## 相关文档

- [Authentication 核心组件](03-authentication.md)
- [AuthenticationManager 架构](04-authentication-manager.md)
- [认证流程详解](05-authentication-flow.md)
