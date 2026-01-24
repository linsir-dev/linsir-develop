# MongoDB 用户管理 API 测试文档

## 1. 项目概述

本项目是一个基于 Spring Boot 和 MongoDB 的用户管理系统，提供了完整的 RESTful API 接口，用于管理用户数据。

## 2. 技术栈

- Spring Boot 3.2.0
- Spring Data MongoDB
- MongoDB 4.11.1
- Maven

## 3. API 接口列表

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /api/users | 创建用户 |
| GET | /api/users/{id} | 根据 ID 查询用户 |
| GET | /api/users | 查询所有用户 |
| GET | /api/users/name/{name} | 根据名称查询用户 |
| GET | /api/users/age-range | 根据年龄范围查询用户 |
| PUT | /api/users/{id} | 更新用户 |
| DELETE | /api/users/{id} | 删除用户 |

## 4. API 接口详细说明

### 4.1 创建用户

**请求 URL:** `POST /api/users`

**请求体:**
```json
{
  "name": "张三",
  "age": 25,
  "email": "zhangsan@example.com",
  "address": "北京市朝阳区"
}
```

**响应示例:**
```json
{
  "id": "60c72b2f9b1e8c001f8e4d56",
  "name": "张三",
  "age": 25,
  "email": "zhangsan@example.com",
  "address": "北京市朝阳区",
  "createdAt": "2026-01-25T04:00:00.000+00:00",
  "updatedAt": "2026-01-25T04:00:00.000+00:00"
}
```

### 4.2 根据 ID 查询用户

**请求 URL:** `GET /api/users/{id}`

**路径参数:**
- `id`: 用户 ID

**响应示例:**
```json
{
  "id": "60c72b2f9b1e8c001f8e4d56",
  "name": "张三",
  "age": 25,
  "email": "zhangsan@example.com",
  "address": "北京市朝阳区",
  "createdAt": "2026-01-25T04:00:00.000+00:00",
  "updatedAt": "2026-01-25T04:00:00.000+00:00"
}
```

### 4.3 查询所有用户

**请求 URL:** `GET /api/users`

**响应示例:**
```json
[
  {
    "id": "60c72b2f9b1e8c001f8e4d56",
    "name": "张三",
    "age": 25,
    "email": "zhangsan@example.com",
    "address": "北京市朝阳区",
    "createdAt": "2026-01-25T04:00:00.000+00:00",
    "updatedAt": "2026-01-25T04:00:00.000+00:00"
  },
  {
    "id": "60c72b3f9b1e8c001f8e4d57",
    "name": "李四",
    "age": 30,
    "email": "lisi@example.com",
    "address": "上海市浦东新区",
    "createdAt": "2026-01-25T04:00:00.000+00:00",
    "updatedAt": "2026-01-25T04:00:00.000+00:00"
  }
]
```

### 4.4 根据名称查询用户

**请求 URL:** `GET /api/users/name/{name}`

**路径参数:**
- `name`: 用户名称

**响应示例:**
```json
[
  {
    "id": "60c72b2f9b1e8c001f8e4d56",
    "name": "张三",
    "age": 25,
    "email": "zhangsan@example.com",
    "address": "北京市朝阳区",
    "createdAt": "2026-01-25T04:00:00.000+00:00",
    "updatedAt": "2026-01-25T04:00:00.000+00:00"
  }
]
```

### 4.5 根据年龄范围查询用户

**请求 URL:** `GET /api/users/age-range`

**查询参数:**
- `minAge`: 最小年龄
- `maxAge`: 最大年龄

**响应示例:**
```json
[
  {
    "id": "60c72b2f9b1e8c001f8e4d56",
    "name": "张三",
    "age": 25,
    "email": "zhangsan@example.com",
    "address": "北京市朝阳区",
    "createdAt": "2026-01-25T04:00:00.000+00:00",
    "updatedAt": "2026-01-25T04:00:00.000+00:00"
  }
]
```

### 4.6 更新用户

**请求 URL:** `PUT /api/users/{id}`

**路径参数:**
- `id`: 用户 ID

**请求体:**
```json
{
  "name": "张三",
  "age": 26,
  "email": "zhangsan@example.com",
  "address": "北京市海淀区"
}
```

**响应示例:**
```json
{
  "id": "60c72b2f9b1e8c001f8e4d56",
  "name": "张三",
  "age": 26,
  "email": "zhangsan@example.com",
  "address": "北京市海淀区",
  "createdAt": "2026-01-25T04:00:00.000+00:00",
  "updatedAt": "2026-01-25T04:01:00.000+00:00"
}
```

### 4.7 删除用户

**请求 URL:** `DELETE /api/users/{id}`

**路径参数:**
- `id`: 用户 ID

**响应:** 204 No Content

## 5. 测试环境

- **应用地址:** http://localhost:6080
- **MongoDB 地址:** mongodb://localhost:27017/linsir-abc-mongodb

## 6. 测试步骤

1. 启动 MongoDB 服务
2. 启动应用服务
3. 使用 Postman 或其他 API 测试工具发送请求
4. 查看响应结果

## 7. 注意事项

- 确保 MongoDB 服务已启动
- 确保应用服务已启动
- 所有 API 请求都需要使用正确的 URL 和请求格式
- 创建用户时，name、age、email 和 address 字段为必填项
