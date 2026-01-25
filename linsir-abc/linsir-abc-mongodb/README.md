# linsir-abc-mongodb

## 项目简介

`linsir-abc-mongodb` 是一个基于 Spring Boot 3.2.0 和 MongoDB 的用户管理系统，提供了完整的用户 CRUD 操作 API 接口。本项目旨在演示如何使用 Spring Data MongoDB 进行 MongoDB 数据库的基本操作，包括用户的创建、查询、更新和删除等功能。

## 技术栈

- **Spring Boot 3.2.0**：提供自动配置和快速开发能力
- **Spring Data MongoDB**：简化 MongoDB 数据访问操作
- **Spring Web**：提供 RESTful API 支持
- **Lombok**：简化 Java 代码，减少样板代码
- **MongoDB**：NoSQL 数据库，用于存储用户数据

## 项目结构

```
linsir-abc-mongodb/
├── docs/                      # 文档目录
│   ├── API测试文档.md          # API 测试文档
│   └── MongoDB-API-测试.postman_collection.json # Postman 测试集合
├── src/                       # 源代码目录
│   └── main/
│       ├── java/com/linsir/mongodb/
│       │   ├── controller/    # 控制器层
│       │   │   └── UserController.java  # 用户控制器
│       │   ├── entity/        # 实体层
│       │   │   └── User.java  # 用户实体类
│       │   ├── repository/    # 数据访问层
│       │   │   └── UserRepository.java  # 用户数据访问接口
│       │   ├── service/       # 服务层
│       │   │   ├── UserService.java     # 用户服务接口
│       │   │   └── impl/      # 服务实现
│       │   │       └── UserServiceImpl.java  # 用户服务实现
│       │   └── MongoDbApplication.java  # 应用主类
│       └── resources/
│           ├── application.yaml         # 主配置文件
│           └── application-dev.yaml     # 开发环境配置
├── pom.xml                    # Maven 依赖配置
└── README.md                  # 项目说明文档
```

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- MongoDB 4.0+

### 安装依赖

```bash
mvn clean install
```

### 启动项目

```bash
mvn spring-boot:run
```

项目将在 `http://localhost:6080` 启动。

## API 接口

### 用户管理接口

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/api/users` | 创建用户 |
| GET | `/api/users/{id}` | 根据ID查询用户 |
| GET | `/api/users` | 查询所有用户 |
| GET | `/api/users/name/{name}` | 根据名称查询用户 |
| GET | `/api/users/age-range` | 根据年龄范围查询用户 |
| PUT | `/api/users/{id}` | 更新用户 |
| DELETE | `/api/users/{id}` | 删除用户 |

### 请求示例

#### 创建用户

```json
POST /api/users
Content-Type: application/json

{
  "name": "张三",
  "age": 25,
  "email": "zhangsan@example.com",
  "address": "北京市朝阳区"
}
```

#### 更新用户

```json
PUT /api/users/{id}
Content-Type: application/json

{
  "name": "张三",
  "age": 26,
  "email": "zhangsan@example.com",
  "address": "北京市海淀区"
}
```

## 配置说明

项目使用 YAML 格式的配置文件，主要配置文件如下：

### application.yaml

```yaml
# 应用配置
spring:
  application:
    name: linsir-abc-mongodb
  # 环境配置
  profiles:
    active: dev
```

### application-dev.yaml

```yaml
# 应用配置
spring:
  application:
    name: linsir-abc-mongodb
  # MongoDB配置
  data:
    mongodb:
      uri: mongodb://localhost:27017/linsir-abc-mongodb
      database: linsir-abc-mongodb

# 服务器配置
server:
  port: 6080
```

## 测试

项目提供了 Postman 测试集合，位于 `docs/MongoDB-API-测试.postman_collection.json`，可以导入到 Postman 中进行 API 测试。

## 端口规划

| 项目 | 端口 |
|------|------|
| linsir-abc-mongodb | 6080 |
| linsir-abc-redis | 6081 |
| linsir-abc-kafka | 6082 |

## 许可证

本项目采用 MIT 许可证。详见 [LICENSE](LICENSE) 文件。