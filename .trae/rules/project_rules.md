# Linsir 项目规则

## Java 版本配置

### 编译和运行版本
- **Java 版本**: 21
- **JDK 路径**: `D:\dev\Java\jdk-21.0.8`
- **Maven 编译配置**:
  ```xml
  <properties>
      <java.version>21</java.version>
      <maven.compiler.source>21</maven.compiler.source>
      <maven.compiler.target>21</maven.compiler.target>
  </properties>
  ```

### 编译器插件配置
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.11.0</version>
    <configuration>
        <source>${java.version}</source>
        <target>${java.version}</target>
        <encoding>${project.build.sourceEncoding}</encoding>
        <compilerArgs>
            <arg>-parameters</arg>
        </compilerArgs>
        <fork>true</fork>
        <executable>D:/dev/Java/jdk-21.0.8/bin/javac.exe</executable>
    </configuration>
</plugin>
```

## 技术栈版本

### Spring Boot
- **版本**: 4.0.5

### MyBatis-Plus
- **版本**: 3.5.15
- **Starter**: mybatis-plus-spring-boot4-starter

### 数据库连接池
- **Druid 版本**: 1.2.20

## 项目结构规范

### 模块结构
```
linsir-springcloud-system-server/
├── src/main/java/com/linsir/system/
│   ├── config/          # 配置类
│   ├── core/            # 核心模块
│   │   ├── base/        # 基础实体
│   │   ├── constants/   # 常量
│   │   └── result/      # 统一响应
│   ├── handler/         # 处理器
│   └── modules/         # 业务模块
│       └── rbac/        # 权限管理
│           ├── controller/
│           ├── entity/
│           ├── mapper/
│           ├── service/
│           └── service/impl/
└── src/main/resources/
    ├── mapper/          # MyBatis XML
    ├── sql/             # SQL脚本
    └── application.yml
```

## 开发规范

### 代码规范
1. 使用 Lombok 简化代码
2. 使用 `@RequiredArgsConstructor` 进行构造器注入
3. 实体类继承 `BaseEntity` 获取通用字段
4. Controller 返回 `CommonResult<T>` 统一响应格式

### 数据库规范
1. 主键使用雪花算法生成（Long 类型）
2. 所有表必须包含：create_time, update_time, create_by, update_by, deleted, version
3. 关联表使用物理删除避免唯一索引冲突
4. 使用逻辑删除（deleted 字段）标记数据状态

### API 规范
1. RESTful API 设计风格
2. 分页接口使用 `/page` 路径
3. 列表接口使用 `/list` 路径
4. 分配关联使用 `/{id}/roles` 或 `/{id}/permissions` 路径
