# MySQL 学习指南

欢迎来到 MySQL 学习指南！本指南旨在帮助开发者深入理解 MySQL 数据库的核心概念、架构设计和实践应用。

## 内容概览

### 第一章：MySQL架构与历史

- [MySQL逻辑架构](./chapter-01-architecture/01-mysql-logical-architecture.md) - 了解 MySQL 的三层架构设计
- [代码设计文档](./chapter-01-architecture/01-mysql-logical-architecture-code-design.md) - 架构实现的设计文档
- [代码指南](./chapter-01-architecture/01-mysql-logical-architecture-code-guide.md) - 详细代码说明和使用指南
- [测试报告](./chapter-01-architecture/01-mysql-logical-architecture-test-results.md) - 单元测试报告
- [面试题总结](./chapter-01-architecture/01-mysql-logical-architecture-interview.md) - 常见面试题和解答
- [并发控制](./chapter-01-architecture/02-concurrency-control.md) - MySQL 并发控制机制

## 学习路径

1. **理解架构** - 首先了解 MySQL 的逻辑架构，包括客户端层、服务层和存储引擎层
2. **实践代码** - 通过代码示例加深理解
3. **阅读测试** - 查看测试用例了解功能验证方法
4. **准备面试** - 复习面试题，巩固知识点

## 技术栈

- **Java 17** - 主要开发语言
- **Spring Boot 3.2** - 应用框架
- **MyBatis** - ORM 框架
- **H2 Database** - 测试数据库
- **Maven** - 构建工具

## 项目结构

```
linsir-abc-mysql/
├── src/main/java/com/linsir/abc/mysql/
│   ├── chapter01/architecture/          # 第一章：架构实现
│   │   ├── client/                      # 客户端层
│   │   │   ├── auth/                    # 认证模块
│   │   │   ├── connection/              # 连接管理
│   │   │   └── session/                 # 会话管理
│   │   ├── server/                      # 服务层
│   │   │   ├── parser/                  # SQL解析器
│   │   │   ├── optimizer/               # 查询优化器
│   │   │   └── executor/                # 查询执行器
│   │   ├── engine/                      # 存储引擎层
│   │   │   ├── StorageEngine.java       # 存储引擎接口
│   │   │   ├── InnoDBEngine.java        # InnoDB实现
│   │   │   └── TransactionManager.java  # 事务管理器
│   │   ├── entity/                      # 实体类
│   │   └── mapper/                      # MyBatis映射器
│   └── config/                          # 配置类
├── src/test/java/                       # 测试代码
└── src/main/resources/                  # 配置文件和SQL脚本
```

## 快速开始

### 运行测试

```bash
cd linsir-abc-mysql
mvn clean test
```

### 查看测试报告

测试报告位于：`target/surefire-reports/`

## 贡献

欢迎提交 Issue 和 Pull Request 来改进本指南。

## 许可证

[MIT License](https://github.com/linsir-dev/linsir-abc/blob/main/LICENSE)
