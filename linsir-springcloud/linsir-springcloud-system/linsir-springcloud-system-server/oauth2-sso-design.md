# Linsir 统一认证与单点登录（SSO）方案设计文档

## 版本信息

| 项目 | 内容 |
|------|------|
| 文档版本 | v1.0 |
| 创建日期 | 2026-04-08 |
| 作者 | Linsir Team |
| 状态 | 已评审 |

---

## 目录

1. [概述](#一概述)
2. [术语定义](#二术语定义)
3. [系统架构](#三系统架构)
4. [技术选型](#四技术选型)
5. [数据库设计](#五数据库设计)
6. [核心流程](#六核心流程)
7. [配置设计](#七配置设计)
8. [接口设计](#八接口设计)
9. [安全设计](#九安全设计)
10. [部署方案](#十部署方案)
11. [开发计划](#十一开发计划)

---

## 一、概述

### 1.1 项目背景

Linsir 平台需要构建统一的认证中心，为多个业务系统提供：
- 统一用户认证服务
- 单点登录（SSO）能力
- OAuth2/OIDC 标准授权服务
- 多客户端支持（Web、移动端）

### 1.2 设计目标

| 目标 | 说明 |
|------|------|
| 统一认证 | 所有系统共享一套用户认证体系 |
| 单点登录 | 一次登录，多系统无感知访问 |
| 标准协议 | 基于 OAuth2.1 + OIDC 标准协议 |
| 多客户端 | 支持 Web、iOS、Android 等多端接入 |
| 可扩展 | 支持动态客户端注册、多种登录方式 |

### 1.3 适用范围

- **认证中心**：linsir-springcloud-system-server
- **管理前端**：system-server-web（Vue）
- **业务系统**：应用Web服务A、应用Web服务B（Vue）
- **移动应用**：App服务A、App服务B（iOS/Android）

---

## 二、术语定义

| 术语 | 英文 | 说明 |
|------|------|------|
| 认证中心 | Authorization Server | OAuth2 授权服务器，负责颁发 Token |
| 单点登录 | SSO | Single Sign-On，一次登录多系统访问 |
| 资源服务器 | Resource Server | 提供业务 API 的后端服务 |
| 客户端 | Client | 请求授权的应用（Web、App） |
| 授权码 | Authorization Code | OAuth2 授权流程中的临时凭证 |
| Access Token | 访问令牌 | 用于访问资源的凭证（JWT格式） |
| Refresh Token | 刷新令牌 | 用于获取新的 Access Token |
| PKCE | Proof Key for Code Exchange | 授权码交换证明密钥，增强安全性 |

---

## 三、系统架构

### 3.1 整体架构图

```
┌─────────────────────────────────────────────────────────────────────────┐
│                              统一认证架构                                │
└─────────────────────────────────────────────────────────────────────────┘

                              ┌─────────────────────────┐
                              │   统一认证中心 (OAuth2)  │
                              │ linsir-springcloud-     │
                              │   system-server         │
                              │   (Java后端)            │
                              │                         │
                              │  ┌─────────────────┐    │
                              │  │  OAuth2端点     │    │
                              │  │  /oauth2/*      │    │
                              │  └─────────────────┘    │
                              │  ┌─────────────────┐    │
                              │  │  登录API        │    │
                              │  │  /api/auth/*    │    │
                              │  └─────────────────┘    │
                              │  ┌─────────────────┐    │
                              │  │  客户端管理API   │    │
                              │  │  /api/oauth2/*  │    │
                              │  └─────────────────┘    │
                              └───────────┬─────────────┘
                                          │
                    ┌─────────────────────┼─────────────────────┐
                    │                     │                     │
                    ▼                     ▼                     ▼
           ┌─────────────────┐   ┌─────────────────┐   ┌─────────────────┐
           │ system-server-web│   │    应用Web服务A  │   │    应用Web服务B  │
           │   (Vue前端)      │   │   (Vue前端)      │   │   (Vue前端)      │
           │  认证中心管理端   │   │  业务系统A前端   │   │  业务系统B前端   │
           └─────────────────┘   └─────────────────┘   └─────────────────┘
                    │                     │                     │
                    │                     │                     │
                    ▼                     ▼                     ▼
           ┌─────────────────┐   ┌─────────────────┐   ┌─────────────────┐
           │    App服务A      │   │    App服务B      │   │    (更多应用...) │
           │  (iOS/Android)   │   │  (iOS/Android)   │   │                 │
           └─────────────────┘   └─────────────────┘   └─────────────────┘
```

### 3.2 各系统职责

| 系统 | 类型 | 职责 | 认证方式 |
|------|------|------|---------|
| **system-server** | Java后端 | 统一认证中心、OAuth2授权服务器、用户管理、客户端管理 | - |
| **system-server-web** | Vue前端 | 认证中心管理界面、用户登录界面、客户端管理界面 | 直接登录 |
| **应用Web服务A** | Vue前端 | 业务系统A | SSO跳转登录 |
| **应用Web服务B** | Vue前端 | 业务系统B | SSO跳转登录 |
| **App服务A** | 移动端 | 移动端业务A | OAuth2授权码 + PKCE |
| **App服务B** | 移动端 | 移动端业务B | OAuth2授权码 + PKCE |

---

## 四、技术选型

### 4.1 核心技术栈

| 组件 | 技术 | 版本 | 说明 |
|------|------|------|------|
| 基础框架 | Spring Boot | 4.0.5 | 基础开发框架 |
| 安全框架 | Spring Security | 7.0.4 | 安全认证框架 |
| 授权服务器 | Spring Authorization Server | 1.4.x | OAuth2/OIDC 实现 |
| Token格式 | JWT (RSA) | - | 非对称密钥签名 |
| 数据存储 | MySQL + JDBC | 8.x | 客户端和Token存储 |
| 缓存/会话 | Redis | 7.x | SSO会话共享 |
| 密码加密 | BCrypt | - | 客户端密钥和用户密码 |

### 4.2 选型理由

1. **Spring Authorization Server**：Spring官方OAuth2授权服务器实现，标准、稳定、可扩展
2. **JWT + RSA**：非对称密钥签名，其他服务可用公钥验证，适合微服务架构
3. **JDBC存储**：数据库存储客户端和Token信息，便于管理和审计
4. **Redis会话**：实现跨应用的SSO会话共享

---

## 五、数据库设计

### 5.1 表结构说明

采用 Spring Authorization Server 官方标准表结构，确保兼容性。

#### 5.1.1 客户端注册表（oauth2_registered_client）

存储 OAuth2 客户端注册信息。

```sql
CREATE TABLE oauth2_registered_client (
    id varchar(100) NOT NULL COMMENT '主键ID',
    client_id varchar(100) NOT NULL COMMENT '客户端ID',
    client_id_issued_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '客户端ID签发时间',
    client_secret varchar(200) DEFAULT NULL COMMENT '客户端密钥（BCrypt加密）',
    client_secret_expires_at timestamp DEFAULT NULL COMMENT '客户端密钥过期时间',
    client_name varchar(200) NOT NULL COMMENT '客户端名称',
    client_authentication_methods varchar(1000) NOT NULL COMMENT '客户端认证方法（JSON数组）',
    authorization_grant_types varchar(1000) NOT NULL COMMENT '授权类型（JSON数组）',
    redirect_uris varchar(1000) DEFAULT NULL COMMENT '重定向URI（JSON数组）',
    post_logout_redirect_uris varchar(1000) DEFAULT NULL COMMENT '登出后重定向URI（JSON数组）',
    scopes varchar(1000) NOT NULL COMMENT '授权范围（JSON数组）',
    client_settings varchar(2000) NOT NULL COMMENT '客户端设置（JSON）',
    token_settings varchar(2000) NOT NULL COMMENT '令牌设置（JSON）',
    PRIMARY KEY (id),
    UNIQUE KEY uk_client_id (client_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OAuth2客户端注册信息表';
```

**字段说明**：

| 字段 | 说明 | 示例 |
|------|------|------|
| client_authentication_methods | 认证方式 | `["client_secret_basic", "client_secret_post"]` |
| authorization_grant_types | 授权类型 | `["authorization_code", "refresh_token"]` |
| redirect_uris | 回调地址 | `["http://app-a.linsir.com/callback"]` |
| scopes | 权限范围 | `["openid", "profile", "api"]` |
| client_settings | 客户端设置 | `{"requireAuthorizationConsent": true}` |
| token_settings | Token设置 | `{"accessTokenTimeToLive": 1800}` |

#### 5.1.2 授权信息表（oauth2_authorization）

存储授权过程中的 Token 和状态信息。

```sql
CREATE TABLE oauth2_authorization (
    id varchar(100) NOT NULL COMMENT '主键ID',
    registered_client_id varchar(100) NOT NULL COMMENT '客户端ID',
    principal_name varchar(200) NOT NULL COMMENT '用户主体名称（用户名）',
    authorization_grant_type varchar(100) NOT NULL COMMENT '授权类型',
    authorized_scopes varchar(1000) DEFAULT NULL COMMENT '已授权范围',
    attributes blob DEFAULT NULL COMMENT '属性（序列化）',
    state varchar(500) DEFAULT NULL COMMENT 'OAuth2 state参数',
    
    -- 授权码
    authorization_code_value blob DEFAULT NULL COMMENT '授权码值',
    authorization_code_issued_at timestamp DEFAULT NULL COMMENT '授权码签发时间',
    authorization_code_expires_at timestamp DEFAULT NULL COMMENT '授权码过期时间',
    authorization_code_metadata blob DEFAULT NULL COMMENT '授权码元数据',
    
    -- Access Token
    access_token_value blob DEFAULT NULL COMMENT 'Access Token值',
    access_token_issued_at timestamp DEFAULT NULL COMMENT 'Access Token签发时间',
    access_token_expires_at timestamp DEFAULT NULL COMMENT 'Access Token过期时间',
    access_token_metadata blob DEFAULT NULL COMMENT 'Access Token元数据',
    access_token_type varchar(100) DEFAULT NULL COMMENT 'Token类型（Bearer）',
    access_token_scopes varchar(1000) DEFAULT NULL COMMENT 'Token权限范围',
    
    -- OIDC ID Token
    oidc_id_token_value blob DEFAULT NULL COMMENT 'ID Token值',
    oidc_id_token_issued_at timestamp DEFAULT NULL COMMENT 'ID Token签发时间',
    oidc_id_token_expires_at timestamp DEFAULT NULL COMMENT 'ID Token过期时间',
    oidc_id_token_metadata blob DEFAULT NULL COMMENT 'ID Token元数据',
    
    -- Refresh Token
    refresh_token_value blob DEFAULT NULL COMMENT 'Refresh Token值',
    refresh_token_issued_at timestamp DEFAULT NULL COMMENT 'Refresh Token签发时间',
    refresh_token_expires_at timestamp DEFAULT NULL COMMENT 'Refresh Token过期时间',
    refresh_token_metadata blob DEFAULT NULL COMMENT 'Refresh Token元数据',
    
    -- 设备码授权（Device Code Grant）
    user_code_value blob DEFAULT NULL COMMENT '用户码值',
    user_code_issued_at timestamp DEFAULT NULL COMMENT '用户码签发时间',
    user_code_expires_at timestamp DEFAULT NULL COMMENT '用户码过期时间',
    user_code_metadata blob DEFAULT NULL COMMENT '用户码元数据',
    device_code_value blob DEFAULT NULL COMMENT '设备码值',
    device_code_issued_at timestamp DEFAULT NULL COMMENT '设备码签发时间',
    device_code_expires_at timestamp DEFAULT NULL COMMENT '设备码过期时间',
    device_code_metadata blob DEFAULT NULL COMMENT '设备码元数据',
    
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OAuth2授权信息表';
```

#### 5.1.3 授权确认表（oauth2_authorization_consent）

存储用户对客户端的授权同意记录。

```sql
CREATE TABLE oauth2_authorization_consent (
    registered_client_id varchar(100) NOT NULL COMMENT '客户端ID',
    principal_name varchar(200) NOT NULL COMMENT '用户主体名称',
    authorities varchar(1000) NOT NULL COMMENT '用户授权的权限范围',
    PRIMARY KEY (registered_client_id, principal_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OAuth2授权确认表';
```

**用途**：
- 记录用户已同意的权限，避免重复询问
- 支持用户查看和管理已授权的应用
- 用户撤销授权时删除记录

### 5.2 客户端初始化数据

```sql
-- 1. system-server-web（认证中心管理端）
INSERT INTO oauth2_registered_client (
    id, client_id, client_id_issued_at, client_secret,
    client_name, client_authentication_methods,
    authorization_grant_types, redirect_uris, post_logout_redirect_uris,
    scopes, client_settings, token_settings
) VALUES (
    'system-web-id',
    'system-web',
    CURRENT_TIMESTAMP,
    '{bcrypt}$2a$10$...',
    '认证中心管理端',
    '["client_secret_basic", "client_secret_post"]',
    '["authorization_code", "refresh_token"]',
    '["http://system.linsir.com/callback"]',
    '["http://system.linsir.com"]',
    '["openid", "profile", "admin"]',
    '{"@class":"org.springframework.security.oauth2.server.authorization.settings.ClientSettings","requireAuthorizationConsent":false}',
    '{"@class":"org.springframework.security.oauth2.server.authorization.settings.TokenSettings","authorizationCodeTimeToLive":300,"accessTokenTimeToLive":1800,"refreshTokenTimeToLive":604800,"reuseRefreshTokens":false}'
);

-- 2. 应用Web服务A
INSERT INTO oauth2_registered_client (...) VALUES (
    'app-a-web-id',
    'app-a-web',
    ...,
    '应用A Web端',
    ...,
    '["http://app-a.linsir.com/callback"]',
    '["openid", "profile", "api"]',
    '{"requireAuthorizationConsent":true}',
    ...
);

-- 3. 应用Web服务B
INSERT INTO oauth2_registered_client (...) VALUES (
    'app-b-web-id',
    'app-b-web',
    ...,
    '应用B Web端',
    ...,
    '["http://app-b.linsir.com/callback"]',
    ...
);

-- 4. App服务A（需要PKCE）
INSERT INTO oauth2_registered_client (...) VALUES (
    'app-a-mobile-id',
    'app-a-mobile',
    ...,
    '应用A移动端',
    '{noop}null',  -- 移动端不需要client_secret
    '["none"]',    -- 无认证方式，使用PKCE
    ...,
    '["com.linsir.app.a://callback"]',
    '["openid", "profile"]',
    '{"requireAuthorizationConsent":true,"requireProofKey":true}',
    ...
);

-- 5. App服务B（需要PKCE）
INSERT INTO oauth2_registered_client (...) VALUES (
    'app-b-mobile-id',
    'app-b-mobile',
    ...,
    '应用B移动端',
    '{noop}null',
    '["none"]',
    ...,
    '["com.linsir.app.b://callback"]',
    ...,
    '{"requireAuthorizationConsent":true,"requireProofKey":true}',
    ...
);
```

---

## 六、核心流程

### 6.1 用户名密码登录流程

适用于：system-server-web 管理端直接登录

```
┌─────────┐     ┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│  Vue前端 │────▶│ /api/auth/  │────▶│ Password    │────▶│  SysUser    │
│         │     │   login     │     │LoginHandler │     │DetailsService│
└─────────┘     └─────────────┘     └─────────────┘     └─────────────┘
     │                │                    │                    │
     │ 1.提交登录信息  │                    │                    │
     │ {username,      │                    │                    │
     │  password,      │                    │                    │
     │  deviceId}      │                    │                    │
     │────────────────▶│                    │                    │
     │                 │ 2.调用认证管理器    │                    │
     │                 │ (AuthenticationManager)
     │                 │                    │ 3.查询用户信息      │
     │                 │                    │ (sys_user表)       │
     │                 │                    │◀───────────────────│
     │                 │ 4.BCrypt验证密码    │                    │
     │                 │ (passwordEncoder.matches)
     │                 │                    │                    │
     │                 │ 5.认证成功          │                    │
     │                 │ (Authentication对象)                   │
     │                 │                    │                    │
     │                 │ 6.生成JWT Token     │                    │
     │                 │ (包含userId, username, roles, deviceId) │
     │◀────────────────│                    │                    │
     │ 7.返回Token     │                    │                    │
     │ {accessToken,   │                    │                    │
     │  refreshToken}  │                    │                    │
     │                 │                    │                    │
     │ 8.存储Token     │                    │                    │
     │ (localStorage)  │                    │                    │
```

**Token内容**：

```json
{
  "sub": "1001",
  "username": "zhangsan",
  "roles": ["admin", "user"],
  "login_type": "password",
  "device_id": "web-admin-001",
  "iat": 1704067200,
  "exp": 1704069000,
  "iss": "http://system.linsir.com"
}
```

### 6.2 OAuth2 授权码模式（SSO）

适用于：应用Web服务A/B 单点登录

```
用户访问 应用Web服务A (http://app-a.linsir.com)
    │
    ▼
应用A检查本地无Token
    │
    ▼
重定向到认证中心
/oauth2/authorize?client_id=app-a-web&redirect_uri=...&scope=openid
    │
    ▼
认证中心检查SSO Session（Redis）
    │
    ├── 未登录 ──▶ 显示登录页面 ──▶ 用户输入密码
    │                              创建SSO Session
    │                              写入Redis + Cookie
    │
    └── 已登录 ──▶ 直接继续
         │
         ▼
检查是否需要授权确认（oauth2_authorization_consent表）
    │
    ├── 首次 ──▶ 显示授权确认页面
    │            用户点击"同意"
    │            保存授权确认记录
    │
    └── 已授权 ──▶ 直接继续
         │
         ▼
生成授权码，保存到oauth2_authorization表
    │
    ▼
重定向回应用A回调地址
http://app-a.linsir.com/callback?code=xxx&state=xxx
    │
    ▼
应用A用授权码换取Token
POST /oauth2/token
    │
    ▼
认证中心验证授权码
生成Access Token + Refresh Token
更新oauth2_authorization表
    │
    ▼
返回Token给应用A
    │
    ▼
应用A存储Token，用户登录成功
```

### 6.3 Token 刷新流程（轮换机制）

```
应用A发现Access Token即将过期
    │
    ▼
调用 /api/auth/refresh
{refreshToken: "xxx"}
    │
    ▼
认证中心验证Refresh Token
查询oauth2_authorization表
    │
    ▼
验证通过
    │
    ▼
生成新的Token对（新的Access Token + 新的Refresh Token）
    │
    ▼
更新数据库记录（旧Refresh Token失效）
    │
    ▼
返回新Token
{accessToken: "new-xxx", refreshToken: "new-yyy"}
    │
    ▼
应用A更新本地存储
```

**轮换机制说明**：
- 每次刷新都生成新的 Refresh Token
- 旧的 Refresh Token 立即失效
- 防止 Refresh Token 被盗用后长期有效

### 6.4 单点登出（SLO）流程

```
用户在应用A点击登出
    │
    ▼
应用A调用 /api/auth/logout
携带Access Token
    │
    ▼
认证中心验证Token
    │
    ▼
撤销Token（从oauth2_authorization表删除或标记）
    │
    ▼
销毁SSO Session（从Redis删除）
    │
    ▼
查询用户的所有会话
    │
    ▼
通知所有已登录应用（通过Redis Pub/Sub）
    │
    ├─────────────┬─────────────┐
    ▼             ▼             ▼
应用A          应用B          应用C
清除本地Token  清除本地Token   清除本地Token
    │             │             │
    ▼             ▼             ▼
用户在所有应用都被登出
```

### 6.5 移动端登录流程（PKCE）

适用于：App服务A/B

```
用户打开App服务A
    │
    ▼
点击"登录"
    │
    ▼
App生成PKCE参数
- code_verifier（随机字符串）
- code_challenge = BASE64URL(SHA256(code_verifier))
    │
    ▼
打开系统浏览器或WebView
访问认证中心 /oauth2/authorize
?client_id=app-a-mobile
&response_type=code
&redirect_uri=com.linsir.app.a://callback
&code_challenge=xxx
&code_challenge_method=S256
&scope=openid profile
    │
    ▼
用户输入用户名密码（或SSO自动登录）
    │
    ▼
授权确认（如需要）
    │
    ▼
重定向到 App Scheme
com.linsir.app.a://callback?code=xxx
    │
    ▼
App捕获授权码
    │
    ▼
App调用 /oauth2/token
grant_type=authorization_code
code=xxx
code_verifier=xxx（PKCE验证）
    │
    ▼
认证中心验证code_verifier
生成Token
    │
    ▼
返回Access Token + Refresh Token
    │
    ▼
App存储Token（Keychain/Keystore）
登录成功
```

---

## 七、配置设计

### 7.1 配置方式

**全部采用 Java 配置**，不写在 `application.yml` 中，便于版本管理和代码审查。

### 7.2 配置类结构

```
config/
├── AuthorizationServerConfig.java      # 授权服务器核心配置
├── SecurityConfig.java                 # Spring Security 配置
├── JwtConfig.java                      # JWT 密钥和Token配置
├── ClientConfig.java                   # 客户端配置
├── LoginConfig.java                    # 登录方式配置
├── SsoConfig.java                      # SSO会话配置
└── CorsConfig.java                     # 跨域配置
```

### 7.3 核心配置说明

#### 7.3.1 Token 配置（JwtConfig.java）

```java
@Bean
public TokenSettings tokenSettings() {
    return TokenSettings.builder()
        // Access Token: 30分钟
        .accessTokenTimeToLive(Duration.ofMinutes(30))
        // Refresh Token: 7天
        .refreshTokenTimeToLive(Duration.ofDays(7))
        // 授权码: 5分钟
        .authorizationCodeTimeToLive(Duration.ofMinutes(5))
        // 刷新Token轮换: 每次刷新都生成新的
        .reuseRefreshTokens(false)
        // Access Token格式: JWT
        .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
        // ID Token签名算法: RS256
        .idTokenSignatureAlgorithm(SignatureAlgorithm.RS256)
        .build();
}
```

#### 7.3.2 JWT 密钥配置

```java
@Bean
public JWKSource<SecurityContext> jwkSource() {
    RSAKey rsaKey = generateRsaKey();
    JWKSet jwkSet = new JWKSet(rsaKey);
    return (jwkSelector, context) -> jwkSelector.select(jwkSet);
}

private RSAKey generateRsaKey() {
    // 方式1: 每次启动生成新的密钥对（开发环境）
    // KeyPair keyPair = generateNewRsaKey();
    
    // 方式2: 从文件加载固定密钥（生产环境推荐）
    KeyPair keyPair = loadRsaKeyFromFile();
    
    RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
    RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
    
    return new RSAKey.Builder(publicKey)
        .privateKey(privateKey)
        .keyID(UUID.randomUUID().toString())
        .build();
}
```

#### 7.3.3 Token 自定义内容

```java
@Bean
public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
    return context -> {
        if (context.getTokenType().equals(OAuth2TokenType.ACCESS_TOKEN)) {
            Authentication principal = context.getPrincipal();
            
            // 用户ID
            context.getClaims().claim("sub", getUserId(principal));
            // 用户名
            context.getClaims().claim("username", principal.getName());
            // 角色
            Set<String> roles = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
            context.getClaims().claim("roles", roles);
            // 登录方式
            context.getClaims().claim("login_type", 
                context.getAuthorizationGrantType().getValue());
            // 设备ID
            String deviceId = getDeviceId(context);
            if (deviceId != null) {
                context.getClaims().claim("device_id", deviceId);
            }
        }
    };
}
```

#### 7.3.4 授权服务器端点配置

```java
@Bean
public AuthorizationServerSettings authorizationServerSettings() {
    return AuthorizationServerSettings.builder()
        .issuer("http://system.linsir.com")
        .authorizationEndpoint("/oauth2/authorize")
        .tokenEndpoint("/oauth2/token")
        .tokenIntrospectionEndpoint("/oauth2/introspect")
        .tokenRevocationEndpoint("/oauth2/revoke")
        .jwkSetEndpoint("/oauth2/jwks")
        .oidcUserInfoEndpoint("/userinfo")
        .oidcClientRegistrationEndpoint("/connect/register")
        .build();
}
```

#### 7.3.5 跨域配置

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    
    // 允许的域
    configuration.setAllowedOrigins(Arrays.asList(
        "http://system.linsir.com",
        "http://app-a.linsir.com",
        "http://app-b.linsir.com",
        "http://localhost:5173",
        "http://localhost:5174",
        "http://localhost:5175"
    ));
    
    // 允许的方法
    configuration.setAllowedMethods(Arrays.asList(
        "GET", "POST", "PUT", "DELETE", "OPTIONS"
    ));
    
    // 允许的头部
    configuration.setAllowedHeaders(Arrays.asList(
        "Authorization", "Content-Type", "X-Requested-With"
    ));
    
    // 允许携带凭证
    configuration.setAllowCredentials(true);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    
    return source;
}
```

---

## 八、接口设计

### 8.1 认证接口

#### 8.1.1 用户名密码登录

```http
POST /api/auth/login
Content-Type: application/json

{
    "loginType": "password",
    "username": "zhangsan",
    "password": "123456",
    "deviceId": "web-admin-001",
    "deviceName": "Chrome on Windows"
}
```

**响应**：

```json
{
    "code": 200,
    "message": "登录成功",
    "data": {
        "accessToken": "eyJhbGciOiJSUzI1NiIs...",
        "refreshToken": "eyJhbGciOiJSUzI1NiIs...",
        "tokenType": "Bearer",
        "expiresIn": 1800,
        "userInfo": {
            "userId": "1001",
            "username": "zhangsan",
            "nickname": "张三",
            "avatar": "https://...",
            "roles": ["admin", "user"]
        }
    }
}
```

#### 8.1.2 刷新Token

```http
POST /api/auth/refresh
Content-Type: application/json

{
    "refreshToken": "eyJhbGciOiJSUzI1NiIs..."
}
```

**响应**：

```json
{
    "code": 200,
    "message": "刷新成功",
    "data": {
        "accessToken": "eyJhbGciOiJSUzI1NiIs...(new)",
        "refreshToken": "eyJhbGciOiJSUzI1NiIs...(new)",
        "tokenType": "Bearer",
        "expiresIn": 1800
    }
}
```

#### 8.1.3 登出

```http
POST /api/auth/logout
Authorization: Bearer eyJhbGciOiJSUzI1NiIs...

{
    "logoutAll": false  // true: 登出所有设备
}
```

**响应**：

```json
{
    "code": 200,
    "message": "登出成功"
}
```

### 8.2 OAuth2 端点

| 端点 | 说明 | 文档 |
|------|------|------|
| `GET /oauth2/authorize` | 授权端点 | OAuth2标准 |
| `POST /oauth2/token` | Token端点 | OAuth2标准 |
| `POST /oauth2/introspect` | Token验证 | OAuth2标准 |
| `POST /oauth2/revoke` | Token撤销 | OAuth2标准 |
| `GET /oauth2/jwks` | 公钥端点 | JWK标准 |
| `GET /.well-known/openid-configuration` | 服务发现 | OIDC标准 |
| `GET /userinfo` | 用户信息 | OIDC标准 |

### 8.3 客户端管理接口（预留）

```http
# 分页查询客户端
GET /api/oauth2/clients/page?page=1&size=10

# 注册客户端
POST /api/oauth2/clients

# 更新客户端
PUT /api/oauth2/clients/{id}

# 删除客户端
DELETE /api/oauth2/clients/{id}

# 重置密钥
POST /api/oauth2/clients/{id}/reset-secret
```

---

## 九、安全设计

### 9.1 密码安全

- 用户密码使用 BCrypt 加密存储（强度10）
- 客户端密钥使用 BCrypt 加密存储
- 支持密码过期策略（预留）

### 9.2 Token 安全

- Access Token 有效期 30 分钟
- Refresh Token 有效期 7 天，支持轮换
- Token 使用 RSA 私钥签名，公钥验证
- 支持 Token 撤销（登出时失效）

### 9.3 传输安全

- 强制 HTTPS（生产环境）
- Cookie 设置 HttpOnly、Secure、SameSite
- 支持 PKCE 防止授权码拦截攻击

### 9.4 防护机制

- 登录失败次数限制（防暴力破解）
- CSRF 防护（前后端分离场景）
- XSS 防护（前端处理）

---

## 十、部署方案

### 10.1 域名规划

| 系统 | 域名 | 说明 |
|------|------|------|
| 认证中心 | system.linsir.com | 统一认证服务 |
| 管理端 | system.linsir.com | 与认证中心同域 |
| 应用A | app-a.linsir.com | 业务系统A |
| 应用B | app-b.linsir.com | 业务系统B |

### 10.2 部署要求

- Java 21
- MySQL 8.0+
- Redis 7.0+
- Nginx（反向代理、SSL）

---

## 十一、开发计划

### 11.1 第一阶段：核心功能（2周）

| 任务 | 说明 | 优先级 |
|------|------|--------|
| 创建OAuth2表结构 | 执行SQL脚本 | 高 |
| 配置Authorization Server | Java配置类 | 高 |
| 集成sys_user认证 | UserDetailsService | 高 |
| 配置JWT（RSA密钥） | JwtConfig | 高 |
| 初始化客户端SQL | 5个客户端 | 高 |
| 测试授权流程 | Postman测试 | 高 |

### 11.2 第二阶段：SSO功能（1周）

| 任务 | 说明 | 优先级 |
|------|------|--------|
| Redis会话共享 | SsoSessionManager | 中 |
| SSO Cookie配置 | 跨子域共享 | 中 |
| 单点登出 | SLO实现 | 中 |
| 多端登录管理 | 设备管理 | 低 |

### 11.3 第三阶段：前端集成（1周）

| 任务 | 说明 | 优先级 |
|------|------|--------|
| system-server-web登录 | 直接API调用 | 中 |
| 应用A/B SSO集成 | 跳转登录 | 中 |
| App PKCE登录 | 移动端 | 低 |

### 11.4 第四阶段：管理功能（预留）

| 任务 | 说明 | 优先级 |
|------|------|--------|
| 客户端管理界面 | 动态注册 | 低 |
| 在线用户管理 | 会话管理 | 低 |
| 登录日志审计 | 日志记录 | 低 |

---

## 附录

### A. 参考文档

- [Spring Authorization Server 文档](https://docs.spring.io/spring-authorization-server/docs/current/reference/html/)
- [OAuth 2.1 规范](https://datatracker.ietf.org/doc/html/draft-ietf-oauth-v2-1-09)
- [OpenID Connect 规范](https://openid.net/specs/openid-connect-core-1_0.html)

### B. 相关文件

- SQL脚本：`src/main/resources/sql/oauth2_tables.sql`
- 配置文件：`config/` 目录

---

**文档结束**
