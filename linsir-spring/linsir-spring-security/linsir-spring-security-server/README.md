# Spring Security 项目结构说明

## 目录结构

```
linsir-spring-security-server/
├── src/main/java/com/linsir/security/
│   ├── config/                          # 配置类
│   │   ├── SecurityConfig.java          # 安全过滤器链配置
│   │   └── AuthenticationConfig.java    # 认证配置（AuthenticationManager、PasswordEncoder）
│   ├── controller/                      # 控制器层
│   │   ├── AuthTestController.java      # 认证测试接口
│   │   ├── HelloController.java         # Hello 测试接口
│   │   └── SecurityContextTestController.java # SecurityContext 测试接口
│   ├── service/                         # 服务层
│   │   ├── CustomUserDetailsService.java # 自定义用户详情服务
│   │   └── impl/                        # 服务实现类（预留）
│   ├── provider/                        # 认证 Provider
│   │   ├── DaoAuthenticationProviderConfig.java # 用户名密码认证
│   │   └── JwtAuthenticationProvider.java # JWT 令牌认证（示例）
│   ├── dto/                             # 数据传输对象
│   │   ├── AuthResponse.java            # 认证响应 DTO
│   │   └── UserResponse.java            # 用户信息响应 DTO
│   ├── enums/                           # 枚举类
│   │   └── UserRole.java                # 用户角色枚举
│   └── SecurityServerApplication.java   # Spring Boot 启动类
└── src/main/resources/
    └── application.yml                  # 应用配置文件
```

## 各层职责

### config（配置层）
- **SecurityConfig.java**: 安全过滤器链配置
  - 配置 HTTP 安全策略
  - 配置请求授权规则
  - 配置表单登录和登出
  - 配置 CSRF 保护策略

- **AuthenticationConfig.java**: 认证配置
  - 配置 AuthenticationManager
  - 配置 PasswordEncoder（BCrypt）
  - 自动发现并注册 provider 包中的所有 Provider 组件

### controller（控制器层）
- **AuthTestController.java**: 认证相关测试接口
  - `/api/auth/current`: 获取当前登录用户信息
  - `/api/auth/protected`: 受保护的资源接口
  
- **SecurityContextTestController.java**: SecurityContext 操作测试
  - `/api/security-context/create-authentication`: 创建认证
  - `/api/security-context/get-current-user`: 获取当前用户
  - `/api/security-context/clear-context`: 清除认证
  - `/api/security-context/check-status`: 检查认证状态

- **HelloController.java**: Hello 测试接口
  - `/api/hello`: 简单的 Hello 接口

### service（服务层）
- **CustomUserDetailsService.java**: 实现 UserDetailsService 接口
  - 从数据源加载用户信息
  - 当前为内存模拟，后续可替换为数据库查询

- **impl/**: 预留的服务实现类目录

### provider（认证 Provider 层）
- **DaoAuthenticationProviderConfig.java**: 基于用户名密码的认证 Provider
  - 继承 DaoAuthenticationProvider
  - 使用 UserDetailsService 加载用户信息
  - 使用 PasswordEncoder 验证密码
  
- **JwtAuthenticationProvider.java**: JWT 令牌认证 Provider（示例）
  - 实现 AuthenticationProvider 接口
  - 验证 JWT Token 的有效性
  - 解析 Token 中的用户信息
  
**扩展说明**：
- 可以添加 LDAP Provider（需添加 spring-security-ldap 依赖）
- 可以添加 OAuth2 Provider（需添加 spring-security-oauth2 依赖）
- 可以添加自定义 Provider 实现特殊认证逻辑

### dto（数据传输对象）
- **AuthResponse.java**: 认证操作响应
  - success: 是否成功
  - message: 响应消息
  - username: 用户名
  - roles: 角色列表
  - sessionId: 会话 ID

- **UserResponse.java**: 用户信息响应
  - authenticated: 是否已认证
  - username: 用户名
  - principal: 主体信息
  - roles: 角色列表
  - authenticationClass: 认证类型

### enums（枚举类）
- **UserRole.java**: 用户角色定义
  - ROLE_ADMIN: 管理员角色
  - ROLE_USER: 普通用户角色

## 后续扩展计划

### 1. 认证相关
- [ ] 添加 JWT 认证支持
- [ ] 添加 OAuth2 认证支持
- [ ] 添加 Remember-Me 功能
- [ ] 添加多因素认证（MFA）

### 2. 授权相关
- [ ] 添加基于角色的访问控制（RBAC）
- [ ] 添加基于权限的访问控制
- [ ] 添加方法级安全注解示例

### 3. 数据持久化
- [ ] 集成 MyBatis/JPA
- [ ] 添加用户表、角色表、权限表
- [ ] 实现完整的用户管理功能

### 4. 功能增强
- [ ] 添加密码重置功能
- [ ] 添加用户注册功能
- [ ] 添加账号锁定/解锁功能
- [ ] 添加登录失败次数限制

### 5. 监控和日志
- [ ] 添加认证事件日志
- [ ] 添加安全审计功能
- [ ] 集成 Actuator 监控

## 技术栈

- **Spring Boot**: 4.0.0
- **Spring Framework**: 7.0.1
- **Spring Security**: 7.0.1
- **Java**: 17+
- **Tomcat**: 11.0.14

## 快速开始

### 1. 启动应用
```bash
cd linsir-spring-security-server
mvn spring-boot:run
```

### 2. 访问登录页面
```
http://localhost:8080/login
```

### 3. 测试用户
- 用户名：`admin` / 密码：`admin123`（角色：ADMIN, USER）
- 用户名：`user` / 密码：`user123`（角色：USER）

### 4. 测试接口
```bash
# 获取当前用户信息
curl http://localhost:8080/api/auth/current

# 访问受保护资源
curl http://localhost:8080/api/auth/protected
```

## 注意事项

1. **密码安全**: 当前使用 `{noop}` 明文密码仅用于测试，生产环境必须使用 `{bcrypt}` 加密
2. **CSRF 保护**: 当前禁用 CSRF 仅用于测试，生产环境应启用
3. **HTTPS**: 生产环境必须使用 HTTPS 传输
4. **会话管理**: 建议配置 Session 超时时间和并发控制
