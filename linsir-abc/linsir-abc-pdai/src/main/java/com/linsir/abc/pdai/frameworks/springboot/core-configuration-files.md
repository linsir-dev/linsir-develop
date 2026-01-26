# SpringBoot的核心配置文件有哪几个？他们的区别是什么？

## 1. SpringBoot配置文件概述

Spring Boot支持多种配置文件格式，主要包括properties和YAML两种格式。配置文件用于配置应用的各种属性，如数据库连接、服务器端口、日志配置等。

## 2. 配置文件的类型

### 2.1 application.properties
properties格式是传统的配置文件格式，使用键值对的方式配置。

**示例：**
```properties
# 服务器配置
server.port=8080
server.servlet.context-path=/api

# 数据源配置
spring.datasource.url=jdbc:mysql://localhost:3306/mydb
spring.datasource.username=root
spring.datasource.password=password
spring.datasource.driver-class-name=com.mysql.jdbc.Driver

# JPA配置
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# 日志配置
logging.level.root=INFO
logging.level.com.example=DEBUG

# 应用配置
spring.application.name=myapp
spring.profiles.active=dev
```

### 2.2 application.yml
YAML格式是一种人类可读的数据序列化格式，支持层级结构。

**示例：**
```yaml
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  application:
    name: myapp
  profiles:
    active: dev
  datasource:
    url: jdbc:mysql://localhost:3306/mydb
    username: root
    password: password
    driver-class-name: com.mysql.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true

logging:
  level:
    root: INFO
    com:
      example: DEBUG
```

## 3. 两种格式的区别

### 3.1 语法格式

**properties格式：**
- 使用键值对
- 使用点号(.)表示层级
- 每个配置项占一行

**YAML格式：**
- 使用缩进表示层级
- 支持列表、映射等复杂数据结构
- 更加简洁易读

### 3.2 复杂配置对比

**properties格式：**
```properties
# 数据源配置
spring.datasource.primary.url=jdbc:mysql://localhost:3306/mydb
spring.datasource.primary.username=root
spring.datasource.primary.password=password

spring.datasource.secondary.url=jdbc:mysql://localhost:3306/mydb2
spring.datasource.secondary.username=root
spring.datasource.secondary.password=password

# 邮件配置
spring.mail.host=smtp.example.com
spring.mail.port=587
spring.mail.username=user@example.com
spring.mail.password=password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

**YAML格式：**
```yaml
spring:
  datasource:
    primary:
      url: jdbc:mysql://localhost:3306/mydb
      username: root
      password: password
    secondary:
      url: jdbc:mysql://localhost:3306/mydb2
      username: root
      password: password
  mail:
    host: smtp.example.com
    port: 587
    username: user@example.com
    password: password
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
```

### 3.3 列表配置对比

**properties格式：**
```properties
# 列表配置（使用索引）
app.servers[0]=server1.example.com
app.servers[1]=server2.example.com
app.servers[2]=server3.example.com
```

**YAML格式：**
```yaml
app:
  servers:
    - server1.example.com
    - server2.example.com
    - server3.example.com
```

## 4. 配置文件优先级

### 4.1 加载顺序
Spring Boot按照以下顺序加载配置文件（优先级从高到低）：

1. 命令行参数
2. SPRING_APPLICATION_JSON（环境变量）
3. JNDI属性
4. Java系统属性
5. 操作系统环境变量
6. RandomValuePropertySource
7. jar包外的application-{profile}.properties
8. jar包内的application-{profile}.properties
9. jar包外的application.properties
10. jar包内的application.properties
11. @PropertySource注解
12. 默认属性

### 4.2 多环境配置

**开发环境配置（application-dev.properties）：**
```properties
server.port=8080
spring.datasource.url=jdbc:mysql://localhost:3306/devdb
spring.datasource.username=root
spring.datasource.password=devpassword
logging.level.root=DEBUG
```

**生产环境配置（application-prod.properties）：**
```properties
server.port=80
spring.datasource.url=jdbc:mysql://prod-server:3306/proddb
spring.datasource.username=produser
spring.datasource.password=prodpassword
logging.level.root=INFO
```

**测试环境配置（application-test.properties）：**
```properties
server.port=8081
spring.datasource.url=jdbc:mysql://localhost:3306/testdb
spring.datasource.username=testuser
spring.datasource.password=testpassword
logging.level.root=WARN
```

**激活环境：**
```properties
# application.properties
spring.profiles.active=dev
```

或使用命令行参数：
```bash
java -jar myapp.jar --spring.profiles.active=prod
```

## 5. 配置文件的最佳实践

### 5.1 分层配置
将配置按环境分层，便于管理。

```
src/main/resources/
├── application.yml              # 公共配置
├── application-dev.yml          # 开发环境
├── application-test.yml         # 测试环境
└── application-prod.yml         # 生产环境
```

### 5.2 配置文件组织
```yaml
# application.yml（公共配置）
spring:
  application:
    name: myapp
  jackson:
    time-zone: GMT+8
    date-format: yyyy-MM-dd HH:mm:ss

---
# application-dev.yml（开发环境）
server:
  port: 8080
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/devdb
logging:
  level:
    root: DEBUG

---
# application-prod.yml（生产环境）
server:
  port: 80
spring:
  datasource:
    url: jdbc:mysql://prod-server:3306/proddb
logging:
  level:
    root: INFO
```

### 5.3 敏感信息处理
不要将敏感信息直接写在配置文件中，使用环境变量或配置中心。

**使用环境变量：**
```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:mysql://localhost:3306/mydb}
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:password}
```

**使用配置中心：**
```yaml
spring:
  cloud:
    config:
      uri: http://config-server:8888
      name: myapp
      profile: ${spring.profiles.active}
```

## 6. 配置属性绑定

### 6.1 @ConfigurationProperties
使用`@ConfigurationProperties`注解绑定配置属性。

**配置文件：**
```yaml
app:
  name: My Application
  version: 1.0.0
  author:
    name: John Doe
    email: john@example.com
  features:
    - feature1
    - feature2
    - feature3
```

**Java类：**
```java
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private String name;
    private String version;
    private Author author;
    private List<String> features;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }

    public static class Author {
        private String name;
        private String email;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}
```

### 6.2 @Value注解
使用`@Value`注解注入单个配置值。

```java
@Component
public class MyComponent {
    @Value("${app.name}")
    private String appName;

    @Value("${app.version}")
    private String appVersion;

    @Value("${server.port:8080}")
    private int serverPort;
}
```

## 7. 配置文件加密

### 7.1 Jasypt加密
使用Jasypt对敏感配置进行加密。

**添加依赖：**
```xml
<dependency>
    <groupId>com.github.ulisesbocchio</groupId>
    <artifactId>jasypt-spring-boot-starter</artifactId>
    <version>3.0.5</version>
</dependency>
```

**配置文件：**
```yaml
jasypt:
  encryptor:
    password: ${JASYPT_PASSWORD:my-secret-key}

spring:
  datasource:
    password: ENC(encrypted_password_here)
```

## 8. 配置文件热更新

### 8.1 Spring Cloud Config
使用Spring Cloud Config实现配置文件的集中管理和热更新。

**配置中心：**
```yaml
spring:
  cloud:
    config:
      uri: http://config-server:8888
      name: myapp
      profile: ${spring.profiles.active}
      label: master
```

### 8.2 Actuator刷新
使用Actuator的`/refresh`端点刷新配置。

**添加依赖：**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

**配置：**
```yaml
management:
  endpoints:
    web:
      exposure:
        include: refresh,health,info
```

**刷新配置：**
```bash
curl -X POST http://localhost:8080/actuator/refresh
```

## 9. 总结

### 9.1 properties vs YAML

| 特性 | properties | YAML |
|------|-------------|------|
| **语法** | 键值对 | 层级结构 |
| **可读性** | 一般 | 好 |
| **复杂配置** | 冗长 | 简洁 |
| **列表支持** | 需要索引 | 原生支持 |
| **注释** | # | # |
| **性能** | 略快 | 略慢 |

### 9.2 选择建议

**使用properties的场景：**
- 简单的键值对配置
- 需要更好的性能
- 团队熟悉properties格式

**使用YAML的场景：**
- 复杂的层级配置
- 需要更好的可读性
- 需要配置列表、映射等复杂数据结构

### 9.3 最佳实践

1. **统一格式**：项目中选择一种格式，不要混用
2. **环境分离**：使用profile区分不同环境
3. **敏感信息**：不要将敏感信息直接写在配置文件中
4. **配置绑定**：使用`@ConfigurationProperties`绑定配置
5. **文档化**：为配置项添加注释说明
6. **版本控制**：配置文件纳入版本控制，但敏感信息除外

Spring Boot的配置文件机制非常灵活，合理使用可以大大简化应用配置管理。