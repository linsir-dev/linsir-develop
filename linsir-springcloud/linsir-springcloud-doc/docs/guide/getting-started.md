# 快速开始

## 环境准备

在开始之前，请确保你的开发环境满足以下要求：

### 必需环境

| 工具 | 版本 | 说明 |
|------|------|------|
| JDK | 17+ | Java 开发工具包 |
| Maven | 3.8+ | 项目构建工具 |
| MySQL | 8.0+ | 数据库 |
| Nacos | 2.2+ | 服务注册与配置中心 |

### 可选环境

| 工具 | 版本 | 说明 |
|------|------|------|
| Redis | 6.0+ | 缓存服务 |
| RocketMQ | 4.9+ | 消息队列 |
| Seata | 1.6+ | 分布式事务 |
| Sentinel | 1.8+ | 流量控制 |

## 安装 Nacos

### 方式一：Docker 安装（推荐）

```bash
# 拉取镜像
docker pull nacos/nacos-server:v2.2.3

# 启动容器
docker run --name nacos \
  -p 8848:8848 \
  -p 9848:9848 \
  -e MODE=standalone \
  -e SPRING_DATASOURCE_PLATFORM=mysql \
  -e MYSQL_SERVICE_HOST=mysql \
  -e MYSQL_SERVICE_DB_NAME=nacos \
  -e MYSQL_SERVICE_PORT=3306 \
  -e MYSQL_SERVICE_USER=nacos \
  -e MYSQL_SERVICE_PASSWORD=nacos \
  nacos/nacos-server:v2.2.3
```

### 方式二：本地安装

```bash
# 下载 Nacos
wget https://github.com/alibaba/nacos/releases/download/2.2.3/nacos-server-2.2.3.tar.gz

# 解压
tar -zxvf nacos-server-2.2.3.tar.gz
cd nacos/bin

# 启动（单机模式）
startup.cmd -m standalone  # Windows
./startup.sh -m standalone # Linux/Mac
```

访问 Nacos 控制台：http://localhost:8848/nacos
- 默认账号：nacos
- 默认密码：nacos

## 创建项目

### 1. 创建父工程

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
                             http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.linsir</groupId>
    <artifactId>linsir-springcloud</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>pom</packaging>

    <name>linsir-springcloud</name>
    <description>Linsir Spring Cloud 微服务项目</description>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        
        <!-- Spring Boot -->
        <spring-boot.version>3.2.0</spring-boot.version>
        
        <!-- Spring Cloud -->
        <spring-cloud.version>2023.0.0</spring-cloud.version>
        
        <!-- Spring Cloud Alibaba -->
        <spring-cloud-alibaba.version>2022.0.0.0</spring-cloud-alibaba.version>
    </properties>

    <modules>
        <module>linsir-gateway</module>
        <module>linsir-user-service</module>
    </modules>

    <dependencyManagement>
        <dependencies>
            <!-- Spring Boot -->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>${spring-boot.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            
            <!-- Spring Cloud -->
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            
            <!-- Spring Cloud Alibaba -->
            <dependency>
                <groupId>com.alibaba.cloud</groupId>
                <artifactId>spring-cloud-alibaba-dependencies</artifactId>
                <version>${spring-cloud-alibaba.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <version>${spring-boot.version}</version>
            </plugin>
        </plugins>
    </build>
</project>
```

### 2. 创建 Gateway 服务

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
                             http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>com.linsir</groupId>
        <artifactId>linsir-springcloud</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>linsir-gateway</artifactId>
    <name>linsir-gateway</name>
    <description>API 网关服务</description>

    <dependencies>
        <!-- Gateway -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-gateway</artifactId>
        </dependency>
        
        <!-- Nacos Discovery -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>
        
        <!-- Nacos Config -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
        </dependency>
        
        <!-- LoadBalancer -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-loadbalancer</artifactId>
        </dependency>
    </dependencies>
</project>
```

### 3. 创建 User Service

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
                             http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>com.linsir</groupId>
        <artifactId>linsir-springcloud</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>linsir-user-service</artifactId>
    <name>linsir-user-service</name>
    <description>用户服务</description>

    <dependencies>
        <!-- Web -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <!-- Nacos Discovery -->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>
        
        <!-- MySQL -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.33</version>
        </dependency>
        
        <!-- MyBatis Plus -->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>3.5.5</version>
        </dependency>
    </dependencies>
</project>
```

## 配置服务

### Gateway 配置

```yaml
# bootstrap.yml
spring:
  application:
    name: linsir-gateway
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
      config:
        server-addr: localhost:8848
        file-extension: yaml

---

# application.yml
server:
  port: 8080

spring:
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true  # 开启服务发现
      routes:
        - id: user-service
          uri: lb://linsir-user-service
          predicates:
            - Path=/api/user/**
          filters:
            - StripPrefix=1
```

### User Service 配置

```yaml
# bootstrap.yml
spring:
  application:
    name: linsir-user-service
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
      config:
        server-addr: localhost:8848
        file-extension: yaml

---

# application.yml
server:
  port: 8081

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/linsir_user?useUnicode=true&characterEncoding=utf-8
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver

mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  mapper-locations: classpath*:/mapper/**/*.xml
```

## 启动服务

### 1. 启动 Nacos

确保 Nacos 服务已启动并正常运行。

### 2. 启动 Gateway

```bash
cd linsir-gateway
mvn spring-boot:run
```

### 3. 启动 User Service

```bash
cd linsir-user-service
mvn spring-boot:run
```

## 验证服务

### 1. 查看 Nacos 服务列表

访问 http://localhost:8848/nacos，在服务列表中应该能看到：
- linsir-gateway
- linsir-user-service

### 2. 测试接口

```bash
# 直接访问用户服务
curl http://localhost:8081/user/list

# 通过网关访问用户服务
curl http://localhost:8080/api/user/list
```

## 下一步

- [项目结构](./project-structure) - 了解项目组织方式
- [服务注册与发现](./service-discovery) - 深入理解 Nacos
- [服务网关](./api-gateway) - 学习 Gateway 高级用法
