# MySQL代码使用说明文档

本文档详细介绍了MySQL相关代码的使用方法，包括数据库连接示例和分表示例。

更多的MySQL相关代码示例和说明，请参考项目linsir-abc-mysql

## 目录结构

```
mysql/
├── DatabaseConnection.java        # 数据库连接示例
├── DatabaseConnectionTest.java    # 数据库连接测试
├── ShardingTableExample.java      # 分表示例
└── README.md                      # 本文档
```

## 1. DatabaseConnection 使用说明

### 1.1 功能介绍

`DatabaseConnection` 类提供了MySQL数据库的基本连接和操作功能，包括：

- 驱动加载
- 获取数据库连接
- 关闭数据库连接
- 测试数据库操作（创建表、插入数据、查询数据、更新数据、删除数据）

### 1.2 配置参数

在使用前，需要修改以下配置参数：

```java
// 数据库连接参数
private static final String URL = "jdbc:mysql://localhost:3306/linsir-abc-pdai?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC";
private static final String USER = "root";
private static final String PASSWORD = "your_password";  // 修改为你的MySQL密码
```

### 1.3 使用方法

#### 1.3.1 基本使用

```java
// 获取数据库连接
Connection connection = DatabaseConnection.getConnection();

// 执行数据库操作...

// 关闭数据库连接
DatabaseConnection.closeConnection(connection, statement, resultSet);
```

#### 1.3.2 快速测试

运行 `main()` 方法进行快速测试：

```java
public static void main(String[] args) {
    testConnection();
}
```

测试内容包括：
1. 创建 `test_user` 表
2. 插入一条测试数据
3. 查询测试数据
4. 更新测试数据
5. 再次查询测试数据
6. 删除测试数据

### 1.4 注意事项

- 确保MySQL服务已启动
- 确保 `linsir-abc-pdai` 数据库已创建
- 确保MySQL驱动已正确引入（pom.xml中已添加）
- 测试完成后，表和数据会被自动清理

## 2. ShardingTableExample 使用说明

### 2.1 功能介绍

`ShardingTableExample` 类演示了MySQL水平分表的实现，包括：

- 创建分表
- 基于用户ID的分表路由
- 分表数据的增删改查操作
- 多表数据查询

### 2.2 配置参数

在使用前，需要修改以下配置参数：

```java
// 数据库连接参数
private static final String URL = "jdbc:mysql://localhost:3306/linsir-abc-pdai?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC";
private static final String USER = "root";
private static final String PASSWORD = "your_password";  // 修改为你的MySQL密码

// 分表数量
private static final int TABLE_COUNT = 4;  // 可根据需要调整
```

### 2.3 分表设计

- **分表策略**：基于用户ID取模
- **分表数量**：默认4个（可配置）
- **表名格式**：`user_info_{index}`，其中index为0-3
- **分表路由**：`userId % TABLE_COUNT`

### 2.4 使用方法

#### 2.4.1 创建分表

```java
// 创建所有分表
ShardingTableExample.createShardingTables();
```

#### 2.4.2 插入数据

```java
// 插入用户数据，会自动路由到对应分表
ShardingTableExample.insertUser(1, "张三", "zhangsan@example.com", "13800138001");
```

#### 2.4.3 查询数据

```java
// 查询用户数据，会自动路由到对应分表
ShardingTableExample.queryUser(1);
```

#### 2.4.4 更新数据

```java
// 更新用户数据，会自动路由到对应分表
ShardingTableExample.updateUser(1, "zhangsan_new@example.com", "13900139001");
```

#### 2.4.5 删除数据

```java
// 删除用户数据，会自动路由到对应分表
ShardingTableExample.deleteUser(1);
```

#### 2.4.6 查询所有数据

```java
// 查询所有分表中的数据
ShardingTableExample.queryAllUsers();
```

#### 2.4.7 快速测试

运行 `main()` 方法进行快速测试：

```java
public static void main(String[] args) {
    testShardingTable();
}
```

测试内容包括：
1. 创建4个分表
2. 插入8条测试数据（分布在不同分表）
3. 查询指定用户数据
4. 更新用户数据
5. 删除用户数据
6. 查询所有分表中的数据

### 2.5 注意事项

- 确保MySQL服务已启动
- 确保 `linsir-abc-pdai` 数据库已创建
- 分表数量应根据实际数据量和服务器性能进行调整
- 生产环境中建议使用专业的分库分表中间件（如ShardingSphere）

## 3. 依赖管理

### 3.1 Maven依赖

在 `pom.xml` 文件中已添加以下依赖：

```xml
<!-- MySQL驱动 -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>
```

### 3.2 驱动版本说明

- `mysql-connector-java 8.0.33` 兼容MySQL 8.0及以上版本
- 若使用MySQL 5.7版本，建议使用 `mysql-connector-java 5.1.x` 版本

## 4. 测试方法

### 4.1 运行单元测试

使用JUnit运行 `DatabaseConnectionTest` 中的测试用例：

```bash
# 使用Maven运行测试
mvn test -Dtest=DatabaseConnectionTest
```

### 4.2 直接运行主方法

- 运行 `DatabaseConnection.main()` 测试数据库连接
- 运行 `ShardingTableExample.main()` 测试分表功能

## 5. 常见问题及解决方案

### 5.1 数据库连接失败

**问题**：`数据库连接失败: Access denied for user 'root'@'localhost'`

**解决方案**：
- 检查MySQL服务是否启动
- 检查用户名和密码是否正确
- 检查数据库 `linsir-abc-pdai` 是否已创建
- 检查网络连接是否正常

### 5.2 驱动加载失败

**问题**：`MySQL驱动加载失败: com.mysql.cj.jdbc.Driver`

**解决方案**：
- 确保Maven依赖已正确添加
- 确保MySQL驱动版本与MySQL服务器版本兼容
- 检查classpath是否包含MySQL驱动

### 5.3 分表操作失败

**问题**：`创建分表失败: Table 'linsir-abc-pdai.user_info_0' doesn't exist`

**解决方案**：
- 确保已先调用 `createShardingTables()` 创建分表
- 检查数据库权限是否足够
- 检查SQL语句是否正确

## 6. 性能优化建议

### 6.1 连接池使用

生产环境中建议使用连接池管理数据库连接，如：

- HikariCP
- Apache DBCP
- C3P0

### 6.2 索引优化

- 为经常查询的字段创建索引
- 避免在索引列上使用函数
- 合理设计复合索引

### 6.3 SQL优化

- 使用PreparedStatement避免SQL注入
- 批量操作减少网络开销
- 合理使用事务

### 6.4 分表优化

- 根据数据增长趋势合理设置分表数量
- 考虑使用一致性哈希算法提高扩展性
- 定期维护分表（如分区合并、数据清理）

## 7. 总结

本文档介绍的代码示例提供了MySQL数据库操作的基本功能，包括：

- **DatabaseConnection**：适合学习MySQL基本操作和测试
- **ShardingTableExample**：适合学习分表概念和原理

在实际生产环境中，建议结合具体业务需求和性能要求，选择合适的数据库访问框架和分库分表方案。

---

**文档更新时间**：2026-01-23
**版本**：1.0
