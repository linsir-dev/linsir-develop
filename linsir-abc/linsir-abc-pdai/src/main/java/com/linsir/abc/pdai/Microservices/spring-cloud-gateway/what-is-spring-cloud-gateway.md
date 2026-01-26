# 什么是Spring Cloud Gateway？

## 一、Spring Cloud Gateway概述

### 1.1 什么是Spring Cloud Gateway

**定义**
Spring Cloud Gateway是Spring Cloud的第二代服务网关，基于Spring 5.0、Spring Boot 2.0和Project Reactor等技术构建。

**特点**
- 基于Reactor
- 性能高
- 非阻塞
- 支持动态路由

### 1.2 Spring Cloud Gateway的架构

**架构图**
```
┌─────────────────────────────────────────────────────┐
│                   客户端                           │
└─────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────┐
│            Spring Cloud Gateway                   │
│                                                         │
│  1. 接收客户端请求                                      │
│  2. 路由请求到内部服务                                  │
│  3. 统一鉴权、限流、熔断                                 │
│  4. 记录日志、监控状态                                    │
└─────────────────────────────────────────────────────┘
                              │
              ┌───────────────┼───────────────┐
              │               │               │
┌─────────────┴─────┐ ┌─────┴─────┐ ┌─────┴─────┐
│   服务A           │ │  服务B    │ │  服务C    │
│  (ServiceA)        │ │ (ServiceB) │ │ (ServiceC) │
└─────────────────────┘ └───────────┘ └───────────┘
```

## 二、Spring Cloud Gateway的核心概念

### 2.1 Route（路由）

**Route定义**
Route是网关的基本构建块，由ID、目标URI、断言和过滤器组成。

**Route示例**
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/user/**
          filters:
            - StripPrefix=1
```

### 2.2 Predicate（断言）

**Predicate定义**
Predicate是Java 8的Predicate函数，用于匹配HTTP请求的任何内容。

**Predicate示例**
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/user/**
            - Method=GET
            - Header=X-Request-Id, \d+
            - Query=foo, ba.
            - Cookie=chocolate, ch.p
            - Host=**.somehost.org
            - RemoteAddr=192.168.1.1/24
            - Before=2026-01-01T00:00:00+08:00[Asia/Shanghai]
            - After=2026-01-01T00:00:00+08:00[Asia/Shanghai]
            - Between=2026-01-01T00:00:00+08:00[Asia/Shanghai], 2026-12-31T23:59:59+08:00[Asia/Shanghai]
```

### 2.3 Filter（过滤器）

**Filter定义**
Filter是GatewayFilter的实例，用于修改请求和响应。

**Filter示例**
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/user/**
          filters:
            - StripPrefix=1
            - AddRequestHeader=X-Request-Id, 123456
            - AddResponseHeader=X-Response-Id, 123456
            - RewritePath=/user/(?<segment>.*), /$\{segment}
```

## 三、Spring Cloud Gateway的配置

### 3.1 基本配置

**依赖配置**
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
```

**启动类配置**
```java
@SpringBootApplication
public class GatewayApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
```

**配置文件**
```yaml
server:
  port: 8080

spring:
  application:
    name: gateway
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/user/**
        - id: order-service
          uri: lb://order-service
          predicates:
            - Path=/order/**
```

### 3.2 路由配置

**路由配置**
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/user/**
          filters:
            - StripPrefix=1
        - id: order-service
          uri: lb://order-service
          predicates:
            - Path=/order/**
          filters:
            - StripPrefix=1
```

### 3.3 断言配置

**断言配置**
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/user/**
            - Method=GET
            - Header=X-Request-Id, \d+
            - Query=foo, ba.
            - Cookie=chocolate, ch.p
            - Host=**.somehost.org
            - RemoteAddr=192.168.1.1/24
            - Before=2026-01-01T00:00:00+08:00[Asia/Shanghai]
            - After=2026-01-01T00:00:00+08:00[Asia/Shanghai]
            - Between=2026-01-01T00:00:00+08:00[Asia/Shanghai], 2026-12-31T23:59:59+08:00[Asia/Shanghai]
```

### 3.4 过滤器配置

**过滤器配置**
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/user/**
          filters:
            - StripPrefix=1
            - AddRequestHeader=X-Request-Id, 123456
            - AddResponseHeader=X-Response-Id, 123456
            - RewritePath=/user/(?<segment>.*), /$\{segment}
```

## 四、Spring Cloud Gateway的使用

### 4.1 使用Route

**Route使用**
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/user/**
```

### 4.2 使用Predicate

**Predicate使用**
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/user/**
            - Method=GET
            - Header=X-Request-Id, \d+
```

### 4.3 使用Filter

**Filter使用**
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/user/**
          filters:
            - StripPrefix=1
            - AddRequestHeader=X-Request-Id, 123456
```

## 五、Spring Cloud Gateway的高级功能

### 5.1 自定义Filter

**自定义Filter**
```java
@Component
public class CustomFilter implements GlobalFilter {
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        System.out.println("Custom Filter: " + request.getURI());
        
        return chain.filter(exchange);
    }
}
```

### 5.2 限流

**限流配置**
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/user/**
          filters:
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 100
                redis-rate-limiter.burstCapacity: 200
```

### 5.3 熔断

**熔断配置**
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/user/**
          filters:
            - name: CircuitBreaker
              args:
                name: userServiceCircuitBreaker
                fallbackUri: forward:/fallback/user
```

## 六、Spring Cloud Gateway的最佳实践

### 6.1 高可用部署

**部署建议**
- 部署多个Spring Cloud Gateway实例
- 使用负载均衡器分发请求
- 保证Spring Cloud Gateway的高可用

### 6.2 统一鉴权

**统一鉴权**
- 在Spring Cloud Gateway层统一处理身份认证和授权
- 后端服务不需要重复处理鉴权
- 提高开发效率

### 6.3 限流熔断

**限流熔断**
- 在Spring Cloud Gateway层实现限流和熔断
- 保护后端服务
- 提高系统可用性

### 6.4 日志监控

**日志监控**
- 在Spring Cloud Gateway层记录请求日志
- 在Spring Cloud Gateway层监控请求状态
- 及时发现和处理问题

## 七、总结

Spring Cloud Gateway是Spring Cloud的第二代服务网关，基于Spring 5.0、Spring Boot 2.0和Project Reactor等技术构建。Spring Cloud Gateway提供了请求路由、负载均衡、统一鉴权、限流熔断、日志监控等功能。

### 核心要点

1. **Spring Cloud Gateway定义**：Spring Cloud的第二代服务网关，基于Spring 5.0、Spring Boot 2.0和Project Reactor等技术构建
2. **Spring Cloud Gateway特点**：基于Reactor、性能高、非阻塞、支持动态路由
3. **Spring Cloud Gateway核心概念**：Route、Predicate、Filter
4. **Spring Cloud Gateway配置**：基本配置、路由配置、断言配置、过滤器配置
5. **Spring Cloud Gateway使用**：使用Route、使用Predicate、使用Filter
6. **Spring Cloud Gateway高级功能**：自定义Filter、限流、熔断
7. **Spring Cloud Gateway最佳实践**：高可用部署、统一鉴权、限流熔断、日志监控

### 使用建议

1. **Spring Cloud项目**：选择Spring Cloud Gateway作为服务网关
2. **高性能场景**：选择Spring Cloud Gateway
3. **需要动态路由**：选择Spring Cloud Gateway
4. **团队熟悉Spring Cloud**：选择Spring Cloud Gateway

Spring Cloud Gateway是Spring Cloud的核心组件，适用于Spring Cloud项目。
