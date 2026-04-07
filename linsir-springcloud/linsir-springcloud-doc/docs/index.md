---
layout: home

hero:
  name: "Linsir Spring Cloud"
  text: "微服务开发指南"
  tagline: 基于 Spring Cloud Alibaba 的微服务架构实战
  image:
    src: /images/logo.png
    alt: Linsir Spring Cloud
  actions:
    - theme: brand
      text: 快速开始
      link: /guide/getting-started
    - theme: alt
      text: 项目介绍
      link: /guide/

features:
  - icon: 🚀
    title: 快速搭建
    details: 提供完整的微服务脚手架，一键生成项目结构，快速启动微服务开发。
  - icon: 🏗️
    title: 组件丰富
    details: 整合 Nacos、Gateway、Feign、Sentinel 等主流组件，开箱即用。
  - icon: 📊
    title: 监控完善
    details: 集成链路追踪、服务监控、日志收集，全方位掌控服务运行状态。
  - icon: 🛡️
    title: 高可用设计
    details: 熔断降级、限流控制、负载均衡，保障服务稳定运行。
---

## 技术栈

<div style="display: flex; flex-wrap: wrap; gap: 10px; margin-top: 20px;">
  <span style="background: #42b883; color: white; padding: 4px 12px; border-radius: 4px;">Spring Boot 3.x</span>
  <span style="background: #6db33f; color: white; padding: 4px 12px; border-radius: 4px;">Spring Cloud 2023</span>
  <span style="background: #ff6a00; color: white; padding: 4px 12px; border-radius: 4px;">Spring Cloud Alibaba</span>
  <span style="background: #00b4e6; color: white; padding: 4px 12px; border-radius: 4px;">Nacos</span>
  <span style="background: #e60012; color: white; padding: 4px 12px; border-radius: 4px;">Sentinel</span>
  <span style="background: #2c3e50; color: white; padding: 4px 12px; border-radius: 4px;">Gateway</span>
  <span style="background: #f7df1e; color: black; padding: 4px 12px; border-radius: 4px;">Seata</span>
</div>

## 快速开始

```bash
# 克隆项目
git clone https://github.com/linsir-dev/linsir-develop.git

# 进入目录
cd linsir-springcloud

# 启动 Nacos
# 访问 http://localhost:8848/nacos

# 编译项目
mvn clean install

# 启动服务
cd linsir-gateway && mvn spring-boot:run
cd linsir-user-service && mvn spring-boot:run
```

## 项目结构

```
linsir-springcloud/
├── linsir-springcloud-doc/       # 文档项目
├── linsir-gateway/               # 网关服务
├── linsir-user-service/          # 用户服务
├── linsir-order-service/         # 订单服务
├── linsir-payment-service/       # 支付服务
└── pom.xml                       # 父工程
```

## 参与贡献

欢迎提交 Issue 和 Pull Request 来帮助改进项目。

## 许可证

[MIT](https://opensource.org/licenses/MIT)
