# linsir-develop

## 项目介绍

linsir-develop 是一个为企业大学准备的研究型课题代码仓库，包含多个子项目，涵盖Java基础、前端、Spring框架等多个技术领域。

## 目录结构

```
linsir-develop/
├── linsir-abc/          # Java基础课程和特定组件的基本实践
│   ├── linsir-abc-core/     # Java核心基础代码
│   ├── linsir-abc-effective/ # 设计模式和Effective Java实践
│   ├── linsir-abc-heima/    # 黑马程序员课程相关代码
│   └── linsir-abc-pdai/     # 综合Java实践项目
├── linsir-f-abc/        # 前端相关内容
├── linsir-f-subject/    # 前端课题研究
├── linsir-jdk/          # JDK相关研究
├── linsir-spring/       # Spring框架相关内容研究
├── linsir-subject/      # 项目研究
├── .gitignore
├── LICENSE
├── pom.xml              # 项目Maven配置文件
└── README.md            # 本文档
```

## 主要模块说明

### 1. linsir-abc

linsir-abc 是Java基础学习的核心模块，包含多个子项目：

#### 1.1 linsir-abc-core
- Java核心基础代码
- 包含基本语法、面向对象、线程、网络编程等示例

#### 1.2 linsir-abc-effective
- 设计模式实现
- Effective Java实践
- 包含23种设计模式的完整实现

#### 1.3 linsir-abc-pdai
- 综合Java实践项目
- 包含以下主要内容：
  - **基础模块**：注解、异常、泛型、反射、SPI等
  - **集合模块**：List、Map、Set、Queue等集合的使用示例
  - **数据库模块**：MySQL数据库操作、分表实现、数据库原理分析
  - **IO模块**：字节流、字符流、NIO、零拷贝等
  - **JVM模块**：类加载器、内存结构、垃圾回收等
  - **数据结构与算法**：各种数据结构实现、排序算法等
  - **多线程模块**：线程基础、锁、JMM等

## 最近更新内容

### 数据库模块（linsir-abc-pdai/database/mysql）

最近在数据库模块添加了详细的MySQL相关内容：

#### 1. 数据库连接与操作
- `DatabaseConnection.java`：MySQL数据库连接示例
- `DatabaseConnectionTest.java`：数据库连接测试
- `ShardingTableExample.java`：MySQL水平分表示例

#### 2. 数据库原理文档
- `b_plus_tree.md`：B+树原理及应用
- `covering_index.md`：覆盖索引和回表
- `lock_types.md`：MySQL锁类型
- `mvcc.md`：MySQL MVCC实现原理
- `myisam_vs_innodb.md`：MyISAM和InnoDB的区别
- `replication.md`：MySQL主从复制
- `sharding.md`：分库分表实现

#### 3. 使用说明文档
- `README.md`：MySQL代码使用详细说明

### MongoDB模块（linsir-abc-mongodb）

最近添加了完整的MongoDB用户管理系统：

#### 1. 核心功能
- 用户的创建、查询、更新、删除操作
- 根据名称和年龄范围查询用户
- RESTful API接口设计

#### 2. 技术栈
- Spring Boot 3.2.0
- Spring Data MongoDB
- MongoDB 4.11.1

#### 3. API接口
- `POST /api/users`：创建用户
- `GET /api/users/{id}`：根据ID查询用户
- `GET /api/users`：查询所有用户
- `GET /api/users/name/{name}`：根据名称查询用户
- `GET /api/users/age-range`：根据年龄范围查询用户
- `PUT /api/users/{id}`：更新用户
- `DELETE /api/users/{id}`：删除用户

## 子项目端口规划

为了确保各子项目能够正常运行且不发生端口冲突，以下是各子项目的端口规划：

| 子项目名称 | 端口号 | 说明 |
|-----------|-------|------|
| linsir-abc-mongodb | 6080 | MongoDB用户管理系统 |
| linsir-abc-kafka | 6081 | Kafka消息队列示例 |
| linsir-abc-pdai | 6082 | 综合Java实践项目 |
| linsir-spring | 6083 | Spring框架相关内容 |

### 端口使用说明

1. **MongoDB用户管理系统**（linsir-abc-mongodb）：
   - 端口：6080
   - 访问地址：http://localhost:6080/api/users

2. **Kafka消息队列示例**（linsir-abc-kafka）：
   - 端口：6081
   - 访问地址：http://localhost:6081

3. **综合Java实践项目**（linsir-abc-pdai）：
   - 端口：6082
   - 访问地址：http://localhost:6082

4. **Spring框架相关内容**（linsir-spring）：
   - 端口：6083
   - 访问地址：http://localhost:6083

### 注意事项

- 确保在启动子项目前，对应的端口没有被其他程序占用
- 如果需要修改端口，可以在各子项目的配置文件中进行修改
- 对于需要数据库的子项目，确保数据库服务已启动且配置正确

## 技术栈

- **核心技术**：Java 8+
- **构建工具**：Maven
- **数据库**：MySQL 8.0+
- **测试框架**：JUnit 4/5
- **日志框架**：SLF4J + Logback
- **加密库**：Bouncy Castle、Google Tink、Jasypt

## 项目用途

1. **Java学习**：提供从基础到高级的Java学习示例
2. **设计模式参考**：包含23种设计模式的完整实现
3. **数据库学习**：提供MySQL数据库操作和原理分析
4. **企业培训**：为企业大学提供培训教材和示例代码
5. **技术研究**：用于各种Java相关技术的研究和实践

## 如何使用

1. **克隆项目**：
   ```bash
   git clone <项目地址>
   ```

2. **构建项目**：
   ```bash
   cd linsir-develop
   mvn clean install
   ```

3. **运行示例**：
   - 直接运行各模块中的main方法
   - 运行JUnit测试用例

4. **学习文档**：
   - 阅读各模块下的README.md文件
   - 查看数据库模块下的原理文档

## 贡献

欢迎对项目进行贡献，包括：
- 修复bug
- 添加新功能
- 完善文档
- 提供更好的实现方式

## 许可证

本项目采用MIT许可证，详见LICENSE文件。

## 联系方式

如有问题或建议，欢迎联系项目维护者。

---

**更新时间**：2026-01-25
**版本**：1.0.0