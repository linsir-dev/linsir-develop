# Linsir Spring Security 文档

## 项目概述

本项目是 Linsir Spring Security 认证服务器的文档中心，包含 Spring Security 的核心概念、架构设计和实现指南。

## 文档结构

```
linsir-security-doc/
├── README.md                          # 本文档
├── 01-architecture/                   # 认证架构文档
│   ├── 01-authentication-overview.md  # 认证架构概览
│   ├── 02-security-context-holder.md  # SecurityContextHolder 详解
│   ├── 03-authentication.md           # Authentication 核心组件
│   ├── 04-authentication-manager.md   # AuthenticationManager 架构
│   └── 05-authentication-flow.md      # 认证流程详解
└── ...                                # 后续补充其他模块
```

## 核心概念速览

### 认证架构组件

| 组件 | 说明 |
|------|------|
| **SecurityContextHolder** | 存储当前认证用户的安全上下文 |
| **SecurityContext** | 包含当前用户的 Authentication 对象 |
| **Authentication** | 表示用户的认证信息（主体、凭证、权限）|
| **GrantedAuthority** | 用户被授予的权限（角色、作用域等）|
| **AuthenticationManager** | 定义过滤器如何执行认证的 API |
| **ProviderManager** | AuthenticationManager 的最常见实现 |
| **AuthenticationProvider** | 执行特定类型认证的组件 |
| **UserDetailsService** | 加载用户信息的接口 |
| **PasswordEncoder** | 密码加密和验证 |

### 认证流程

```
用户请求 → Filter Chain → AuthenticationManager → AuthenticationProvider
                                              ↓
                    SecurityContextHolder ← 认证成功
                                              ↓
                                         访问资源
```

## 快速开始

1. [认证架构概览](./01-architecture/01-authentication-overview.md) - 了解整体架构
2. [SecurityContextHolder](./01-architecture/02-security-context-holder.md) - 理解安全上下文存储
3. [Authentication](./01-architecture/03-authentication.md) - 掌握认证对象
4. [AuthenticationManager](./01-architecture/04-authentication-manager.md) - 学习认证管理
5. [认证流程](./01-architecture/05-authentication-flow.md) - 深入认证流程

## 参考文档

- [Spring Security 官方文档](https://docs.spring.io/spring-security/reference/)
- [Spring Security Servlet 认证架构](https://docs.spring.io/spring-security/reference/servlet/authentication/architecture.html)
