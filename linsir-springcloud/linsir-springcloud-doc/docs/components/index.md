# 核心组件

Linsir Spring Cloud 基于 Spring Cloud Alibaba 构建，整合了微服务架构中的核心组件。

## 组件概览

| 组件 | 功能 | 版本 |
|------|------|------|
| Nacos | 服务注册与配置中心 | 2.2.x |
| Gateway | API 网关 | 4.x |
| Feign | 服务调用 | 4.x |
| Ribbon | 负载均衡 | 4.x |
| Sentinel | 流量控制与熔断 | 1.8.x |
| Seata | 分布式事务 | 1.7.x |
| Sleuth | 链路追踪 | 3.x |
| Zipkin | 链路追踪 UI | 2.x |

## 组件关系图

```
                    ┌─────────────────┐
                    │   API Gateway   │
                    └────────┬────────┘
                             │
              ┌──────────────┼──────────────┐
              │              │              │
              ▼              ▼              ▼
       ┌────────────┐ ┌────────────┐ ┌────────────┐
       │User Service│ │Order Service│ │Pay Service │
       └─────┬──────┘ └─────┬──────┘ └─────┬──────┘
             │              │              │
             └──────────────┼──────────────┘
                            │
              ┌─────────────┼─────────────┐
              │             │             │
              ▼             ▼             ▼
       ┌──────────┐ ┌──────────┐ ┌──────────┐
       │  Nacos   │ │  MySQL   │ │  Redis   │
       │(注册/配置)│ │(数据存储) │ │ (缓存)   │
       └──────────┘ └──────────┘ └──────────┘
```

## 快速导航

### 服务治理
- [Eureka](./eureka) - Netflix 服务注册中心
- [Nacos](./nacos) - 阿里巴巴服务注册与配置中心

### 服务网关
- [Gateway](./gateway) - Spring Cloud 网关

### 服务调用
- [Feign](./feign) - 声明式 HTTP 客户端
- [Ribbon](./ribbon) - 客户端负载均衡

### 服务保护
- [Hystrix](./hystrix) - 熔断器（已停止维护）
- [Sentinel](./sentinel) - 阿里巴巴流量控制

### 可观测性
- [Sleuth](./sleuth) - 链路追踪
- [Zipkin](./zipkin) - 链路追踪可视化
- [Actuator](./actuator) - 服务监控
