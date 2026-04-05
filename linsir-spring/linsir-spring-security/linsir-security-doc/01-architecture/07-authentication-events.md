# Spring Security 认证事件配置指南

## 概述

Spring Security 提供了完善的认证事件机制，通过 `AuthenticationEventPublisher` 和相关的 Event Listener，可以在认证成功或失败时执行自定义逻辑，如审计日志、监控告警等。

## 核心组件

### 1. AuthenticationEventPublisher

负责发布认证事件的发布器，Spring Security 默认使用 `DefaultAuthenticationEventPublisher`。

### 2. AuthenticationEvent

认证事件的基类，分为成功事件和失败事件：
- `AuthenticationSuccessEvent` - 认证成功事件
- `AbstractAuthenticationFailureEvent` - 认证失败事件基类

### 3. Event Listener

监听并处理认证事件的组件，使用 `@EventListener` 注解。

## 默认异常映射

Spring Security 默认将以下异常映射到对应的事件：

| 异常类型 | 事件类型 |
|---------|---------|
| BadCredentialsException | AuthenticationFailureBadCredentialsEvent |
| UsernameNotFoundException | AuthenticationFailureBadCredentialsEvent |
| AccountExpiredException | AuthenticationFailureExpiredEvent |
| DisabledException | AuthenticationFailureDisabledEvent |
| LockedException | AuthenticationFailureLockedEvent |
| AuthenticationServiceException | AuthenticationFailureServiceExceptionEvent |
| CredentialsExpiredException | AuthenticationFailureCredentialsExpiredEvent |
| InvalidBearerTokenException | AuthenticationFailureBadCredentialsEvent |

## 项目配置

### 1. 认证事件发布器配置

```java
@Configuration
public class AuthenticationEventConfig {
    
    @Bean
    public AuthenticationEventPublisher authenticationEventPublisher(
            ApplicationEventPublisher applicationEventPublisher) {
        
        DefaultAuthenticationEventPublisher publisher = 
            new DefaultAuthenticationEventPublisher(applicationEventPublisher);
        
        // 配置额外的异常映射（如需要）
        // Map<Class<? extends AuthenticationException, 
        //     Class<? extends AbstractAuthenticationFailureEvent>> additionalMappings = 
        //         new HashMap<>();
        // additionalMappings.put(CustomException.class, CustomFailureEvent.class);
        // publisher.setAdditionalExceptionMappings(additionalMappings);
        
        return publisher;
    }
}
```

### 2. 认证事件监听器

```java
@Component
public class AuthenticationEventListener {
    
    private final AuthenticationAuditService auditService;
    
    public AuthenticationEventListener(AuthenticationAuditService auditService) {
        this.auditService = auditService;
    }
    
    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        logger.info("认证成功 - 用户名：{}", event.getAuthentication().getName());
        auditService.logSuccess(event.getAuthentication());
    }
    
    @EventListener
    public void onBadCredentials(AuthenticationFailureBadCredentialsEvent event) {
        logger.warn("用户名或密码错误 - 用户名：{}", event.getAuthentication().getName());
        auditService.logFailure(event.getAuthentication().getName(), "用户名或密码错误");
    }
    
    // 其他事件监听方法...
}
```

### 3. 认证审计服务

```java
@Service
public class AuthenticationAuditService {
    
    public void logSuccess(Authentication authentication) {
        String username = authentication.getName();
        String remoteAddress = getRemoteAddress();
        String sessionId = getSessionId();
        
        logger.info("认证成功审计 - 用户名：{}, IP: {}, SessionId: {}, 权限：{}", 
            username, remoteAddress, sessionId, authentication.getAuthorities());
    }
    
    public void logFailure(String username, String reason) {
        String remoteAddress = getRemoteAddress();
        logger.warn("认证失败审计 - 用户名：{}, IP: {}, 原因：{}", 
            username, remoteAddress, reason);
    }
    
    // 其他审计方法...
}
```

## 使用场景

### 1. 安全审计

记录所有认证成功和失败事件，用于安全审计和合规要求。

### 2. 账户保护

监听连续的认证失败事件，自动锁定可疑账户：

```java
@EventListener
public void onBadCredentials(AuthenticationFailureBadCredentialsEvent event) {
    String username = event.getAuthentication().getName();
    int failedAttempts = loginAttemptService.getFailedAttempts(username);
    
    if (failedAttempts >= MAX_ATTEMPTS) {
        userService.lockAccount(username);
        logger.warn("账户已被自动锁定：{}", username);
    }
}
```

### 3. 实时监控

将认证事件发送到消息队列，用于实时监控和告警：

```java
@EventListener
public void onSuccess(AuthenticationSuccessEvent event) {
    Authentication auth = event.getAuthentication();
    kafkaTemplate.send("auth-success-topic", auth.getName());
}
```

### 4. 登录统计

统计用户登录信息，用于分析用户行为：

```java
@EventListener
public void onSuccess(AuthenticationSuccessEvent event) {
    String username = event.getAuthentication().getName();
    String ip = getRemoteAddress();
    
    loginStatisticsService.recordLogin(username, ip);
}
```

## 最佳实践

### 1. 异步处理

认证事件监听器应该异步执行，避免阻塞主线程：

```java
@Async
@EventListener
public void onSuccess(AuthenticationSuccessEvent event) {
    // 异步处理
}
```

### 2. 异常处理

在事件监听器中捕获异常，避免影响正常认证流程：

```java
@EventListener
public void onSuccess(AuthenticationSuccessEvent event) {
    try {
        // 处理逻辑
    } catch (Exception e) {
        logger.error("处理认证事件失败", e);
    }
}
```

### 3. 敏感信息保护

不要在日志中记录密码等敏感信息：

```java
// ❌ 错误做法
logger.info("登录尝试 - 用户名：{}, 密码：{}", username, password);

// ✅ 正确做法
logger.info("登录尝试 - 用户名：{}, IP: {}", username, ip);
```

### 4. 性能考虑

对于高频事件，考虑使用批量处理或采样：

```java
@EventListener
public void onFailure(AbstractAuthenticationFailureEvent event) {
    // 只记录失败的摘要信息，不记录详细堆栈
    logger.warn("认证失败 - 用户名：{}, 类型：{}", 
        event.getAuthentication().getName(),
        event.getException().getClass().getSimpleName());
}
```

## 测试验证

### 1. 测试认证成功

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -d "username=admin&password=admin123"
```

查看日志：
```
✅ 认证成功 - 用户名：admin
🔐 认证成功审计 - 用户名：admin, IP: 0:0:0:0:0:0:0:1, SessionId: N/A
```

### 2. 测试认证失败

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -d "username=admin&password=wrong"
```

查看日志：
```
🔐 用户名或密码错误 - 用户名：admin
❌ 认证失败 - 用户名：admin, 异常类型：BadCredentialsException
🔐 认证失败审计 - 用户名：admin, IP: 0:0:0:0:0:0:0:1, 原因：用户名或密码错误
```

### 3. 测试账户锁定

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -d "username=locked&password=locked123"
```

查看日志：
```
🔒 账户被锁定 - 用户名：locked
🔐 账户锁定审计 - 用户名：locked, IP: 0:0:0:0:0:0:0:1
```

## 扩展配置

### 1. 自定义异常映射

```java
@Bean
public AuthenticationEventPublisher authenticationEventPublisher(
        ApplicationEventPublisher applicationEventPublisher) {
    
    DefaultAuthenticationEventPublisher publisher = 
        new DefaultAuthenticationEventPublisher(applicationEventPublisher);
    
    Map<Class<? extends AuthenticationException, 
        Class<? extends AbstractAuthenticationFailureEvent>> mappings = 
            new HashMap<>();
    
    // 添加自定义异常映射
    mappings.put(CustomAuthenticationException.class, 
                 CustomAuthenticationFailureEvent.class);
    
    publisher.setAdditionalExceptionMappings(mappings);
    return publisher;
}
```

### 2. 自定义事件

```java
public class CustomAuthenticationFailureEvent 
        extends AbstractAuthenticationFailureEvent {
    
    public CustomAuthenticationFailureEvent(Authentication authentication,
                                            AuthenticationException exception) {
        super(authentication, exception);
    }
}
```

## 参考资料

- [Spring Security 官方文档 - Authentication Events](https://docs.spring.io/spring-security/reference/servlet/authentication/events.html)
- [Spring Framework 文档 - Application Events](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#context-functionality-events)
