# 什么是Eureka？

## 一、Eureka概述

### 1.1 Eureka的定义

Eureka是Netflix开源的服务注册与发现组件，是Spring Cloud Netflix的核心组件之一。Eureka提供了服务注册与发现功能，实现了服务提供者和服务消费者的解耦。

### 1.2 Eureka的架构

**Eureka Server**
- 服务注册中心
- 接收服务提供者的注册请求
- 接收服务消费者的查询请求
- 管理服务实例的信息

**Eureka Client**
- 服务客户端
- 服务提供者：向Eureka Server注册自己的信息
- 服务消费者：从Eureka Server获取服务列表

**服务提供者**
- 提供服务的应用
- 向Eureka Server注册自己的信息
- 定期向Eureka Server发送心跳

**服务消费者**
- 消费服务的应用
- 从Eureka Server获取服务列表
- 根据负载均衡策略选择服务实例

### 1.3 Eureka的特点

**AP系统**
- Eureka是基于AP（可用性和分区容错性）的系统
- 优先保证可用性，而不是一致性
- 适用于服务注册与发现场景

**自我保护模式**
- 当Eureka Server在短时间内丢失大量服务实例的心跳时，进入自我保护模式
- 自我保护模式下，Eureka Server不会剔除服务实例
- 保证在Eureka Server故障时，服务消费者仍然可以获取服务列表

**服务剔除**
- Eureka Server定期检查服务实例的心跳
- 如果服务实例在一定时间内没有发送心跳，Eureka Server会剔除该服务实例

## 二、Eureka的工作原理

### 2.1 服务注册

**注册流程**
1. 服务提供者启动
2. 服务提供者向Eureka Server发送注册请求
3. Eureka Server保存服务提供者的信息
4. Eureka Server将服务提供者的信息同步到其他Eureka Server

**注册信息**
- 服务名称
- 服务实例ID
- 服务实例的IP地址
- 服务实例的端口号
- 服务实例的健康检查URL
- 服务实例的元数据

### 2.2 服务发现

**发现流程**
1. 服务消费者启动
2. 服务消费者向Eureka Server发送查询请求
3. Eureka Server返回服务提供者的信息
4. 服务消费者缓存服务提供者的信息
5. 服务消费者根据负载均衡策略选择一个服务实例

**查询方式**
- 全量查询：获取所有服务实例的信息
- 增量查询：获取变化的服务实例的信息

### 2.3 心跳检测

**心跳流程**
1. 服务提供者定期向Eureka Server发送心跳
2. Eureka Server更新服务实例的最后心跳时间
3. Eureka Server定期检查服务实例的最后心跳时间
4. 如果服务实例在一定时间内没有发送心跳，Eureka Server剔除该服务实例

**心跳间隔**
- 默认30秒发送一次心跳
- 默认90秒没有收到心跳则剔除服务实例

### 2.4 服务剔除

**剔除流程**
1. Eureka Server定期检查服务实例的最后心跳时间
2. 如果服务实例的最后心跳时间超过阈值，Eureka Server剔除该服务实例
3. Eureka Server将剔除的信息同步到其他Eureka Server
4. 服务消费者刷新服务列表

## 三、Eureka的配置

### 3.1 Eureka Server配置

**基本配置**
```yaml
server:
  port: 8761

eureka:
  instance:
    hostname: localhost
  client:
    register-with-eureka: false
    fetch-registry: false
    service-url:
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
```

**集群配置**
```yaml
eureka:
  client:
    service-url:
      defaultZone: http://eureka1:8761/eureka/,http://eureka2:8761/eureka/,http://eureka3:8761/eureka/
```

**自我保护配置**
```yaml
eureka:
  server:
    enable-self-preservation: true
    renewal-percent-threshold: 0.85
```

### 3.2 Eureka Client配置

**基本配置**
```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true
    instance-id: ${spring.application.name}:${spring.cloud.client.ip-address}:${server.port}
```

**心跳配置**
```yaml
eureka:
  instance:
    lease-renewal-interval-in-seconds: 30
    lease-expiration-duration-in-seconds: 90
```

**服务列表缓存配置**
```yaml
eureka:
  client:
    registry-fetch-interval-seconds: 30
```

## 四、Eureka的高可用

### 4.1 Eureka Server集群

**集群架构**
```
┌─────────────────────────────────────────────────────────────┐
│                   服务消费者                             │
└─────────────────────────────────────────────────────────────┘
                              │
              ┌───────────────┼───────────────┐
              │               │               │
┌─────────────┴─────┐ ┌─────┴─────┐ ┌─────┴─────┐
│   Eureka Server1  │ │Eureka Server2│ │Eureka Server3│
│  (8761)          │ │  (8762)    │ │  (8763)    │
└─────────────────────┘ └───────────┘ └───────────┘
         │                    │                    │
         └────────────────────┼────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                   服务提供者                             │
└─────────────────────────────────────────────────────────────┘
```

**集群配置**
```yaml
eureka:
  client:
    service-url:
      defaultZone: http://eureka1:8761/eureka/,http://eureka2:8761/eureka/,http://eureka3:8761/eureka/
```

### 4.2 Eureka Server的高可用

**高可用方案**
- 部署多个Eureka Server实例
- Eureka Server实例之间相互注册
- 使用负载均衡器分发请求

**负载均衡配置**
```nginx
upstream eureka {
    server eureka1:8761;
    server eureka2:8761;
    server eureka3:8761;
}

server {
    listen 80;
    server_name eureka.example.com;
    
    location / {
        proxy_pass http://eureka;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

## 五、Eureka的自我保护模式

### 5.1 自我保护模式的触发条件

**触发条件**
- Eureka Server在15分钟内，丢失的心跳比例低于85%
- Eureka Server进入自我保护模式

**自我保护模式下的行为**
- Eureka Server不会剔除服务实例
- Eureka Server仍然可以接收服务注册和查询请求
- 服务消费者可以获取到不健康的服务实例

### 5.2 自我保护模式的配置

**开启自我保护模式**
```yaml
eureka:
  server:
    enable-self-preservation: true
    renewal-percent-threshold: 0.85
```

**关闭自我保护模式**
```yaml
eureka:
  server:
    enable-self-preservation: false
```

### 5.3 自我保护模式的优缺点

**优点**
- 保证在Eureka Server故障时，服务消费者仍然可以获取服务列表
- 避免因网络分区导致的服务实例误剔除

**缺点**
- 服务消费者可能获取到不健康的服务实例
- 服务消费者需要自己处理服务实例的故障

## 六、Eureka的最佳实践

### 6.1 高可用部署

**部署多个Eureka Server实例**
- 部署3个或更多Eureka Server实例
- Eureka Server实例之间相互注册
- 保证Eureka Server的高可用

### 6.2 服务健康检查

**配置健康检查**
```yaml
eureka:
  instance:
    health-check-url: /actuator/health
    health-check-interval: 30
```

### 6.3 服务列表缓存

**配置服务列表缓存**
```yaml
eureka:
  client:
    registry-fetch-interval-seconds: 30
```

## 七、总结

Eureka是Netflix开源的服务注册与发现组件，是Spring Cloud Netflix的核心组件之一。Eureka提供了服务注册与发现功能，实现了服务提供者和服务消费者的解耦。

### 核心要点

1. **Eureka架构**：Eureka Server、Eureka Client、服务提供者、服务消费者
2. **Eureka特点**：AP系统、自我保护模式、服务剔除
3. **Eureka工作原理**：服务注册、服务发现、心跳检测、服务剔除
4. **Eureka配置**：Eureka Server配置、Eureka Client配置
5. **Eureka高可用**：Eureka Server集群、负载均衡
6. **Eureka自我保护模式**：触发条件、配置、优缺点

### 最佳实践

1. **高可用部署**：部署多个Eureka Server实例
2. **服务健康检查**：配置健康检查URL
3. **服务列表缓存**：配置服务列表缓存间隔

Eureka是Spring Cloud Netflix的核心组件，适用于Spring Cloud项目。但随着Netflix组件的维护模式，建议新项目使用Nacos等服务注册与发现组件。
