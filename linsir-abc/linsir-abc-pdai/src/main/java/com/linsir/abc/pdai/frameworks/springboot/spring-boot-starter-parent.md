# spring-boot-starter-parent有什么作用？

## 1. spring-boot-starter-parent概述

`spring-boot-starter-parent`是Spring Boot项目的父POM，它提供了统一的依赖管理和构建配置，大大简化了Maven项目的配置。

## 2. 基本使用

### 2.1 引入方式
在pom.xml中继承`spring-boot-starter-parent`：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.0</version>
    <relativePath/>
</parent>
```

### 2.2 完整示例
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
        <relativePath/>
    </parent>

    <groupId>com.example</groupId>
    <artifactId>myapp</artifactId>
    <version>1.0.0</version>
    <name>My Application</name>
    <description>My Spring Boot Application</description>

    <properties>
        <java.version>17</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>
</project>
```

## 3. 主要作用

### 3.1 依赖版本管理
`spring-boot-starter-parent`统一管理了Spring Boot及其相关依赖的版本，避免版本冲突。

**无需指定版本号：**
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
    </dependency>
</dependencies>
```

**传统方式需要指定版本：**
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <version>3.2.0</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
        <version>3.2.0</version>
    </dependency>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>8.0.33</version>
    </dependency>
</dependencies>
```

### 3.2 Java版本配置
默认配置Java编译版本。

```xml
<properties>
    <java.version>17</java.version>
</properties>
```

### 3.3 编码配置
默认配置UTF-8编码。

```xml
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
</properties>
```

### 3.4 资源过滤
默认开启资源过滤，可以在配置文件中使用占位符。

**application.properties：**
```properties
app.name=@project.name@
app.version=@project.version@
```

**pom.xml：**
```xml
<build>
    <resources>
        <resource>
            <directory>src/main/resources</directory>
            <filtering>true</filtering>
        </resource>
    </resources>
</build>
```

### 3.5 插件管理
`spring-boot-starter-parent`管理了常用插件的版本。

**spring-boot-maven-plugin：**
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

### 3.6 默认配置
提供了合理的默认配置，如：
- 源代码目录：`src/main/java`
- 测试代码目录：`src/test/java`
- 资源目录：`src/main/resources`
- 测试资源目录：`src/test/resources`

## 4. 覆盖默认配置

### 4.1 覆盖依赖版本
如果需要使用特定版本的依赖，可以在properties中覆盖：

```xml
<properties>
    <java.version>17</java.version>
    <mysql.version>8.0.33</mysql.version>
    <jackson.version>2.15.2</jackson.version>
</properties>
```

### 4.2 覆盖插件配置
可以覆盖插件的默认配置：

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <excludes>
                    <exclude>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                    </exclude>
                </excludes>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### 4.3 覆盖资源过滤配置
可以自定义资源过滤配置：

```xml
<build>
    <resources>
        <resource>
            <directory>src/main/resources</directory>
            <filtering>true</filtering>
            <includes>
                <include>**/*.properties</include>
                <include>**/*.yml</include>
                <include>**/*.yaml</include>
            </includes>
        </resource>
        <resource>
            <directory>src/main/resources</directory>
            <filtering>false</filtering>
            <excludes>
                <exclude>**/*.properties</exclude>
                <exclude>**/*.yml</exclude>
                <exclude>**/*.yaml</exclude>
            </excludes>
        </resource>
    </resources>
</build>
```

## 5. 不使用spring-boot-starter-parent

如果项目需要继承其他父POM，可以使用`dependencyManagement`引入Spring Boot的依赖管理。

### 5.1 使用import scope
```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>3.2.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### 5.2 完整示例
```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.example</groupId>
        <artifactId>my-parent</artifactId>
        <version>1.0.0</version>
    </parent>

    <groupId>com.example</groupId>
    <artifactId>myapp</artifactId>
    <version>1.0.0</version>

    <properties>
        <java.version>17</java.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>3.2.0</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

## 6. 查看依赖版本

### 6.1 查看所有依赖版本
可以使用Maven命令查看所有依赖的版本：

```bash
mvn dependency:tree
```

### 6.2 查看特定依赖版本
```bash
mvn dependency:tree -Dincludes=org.springframework.boot
```

### 6.3 查看有效POM
```bash
mvn help:effective-pom
```

## 7. 最佳实践

### 7.1 使用spring-boot-starter-parent
大多数情况下，建议使用`spring-boot-starter-parent`作为父POM。

### 7.2 合理覆盖版本
只在必要时覆盖依赖版本，避免版本冲突。

```xml
<properties>
    <mysql.version>8.0.33</mysql.version>
</properties>
```

### 7.3 统一Java版本
在properties中统一配置Java版本。

```xml
<properties>
    <java.version>17</java.version>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
</properties>
```

### 7.4 使用profiles管理不同环境
使用profiles管理不同环境的配置。

```xml
<profiles>
    <profile>
        <id>dev</id>
        <properties>
            <spring.profiles.active>dev</spring.profiles.active>
        </properties>
    </profile>
    <profile>
        <id>prod</id>
        <properties>
            <spring.profiles.active>prod</spring.profiles.active>
        </properties>
    </profile>
</profiles>
```

## 8. 常见问题

### 8.1 如何查看spring-boot-starter-parent管理的所有依赖？
可以查看Spring Boot官方文档或使用Maven命令：

```bash
mvn help:effective-pom | grep -A 100 "dependencyManagement"
```

### 8.2 如何升级Spring Boot版本？
只需修改parent的version即可：

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.0</version>
</parent>
```

### 8.3 如何排除某个依赖？
可以在dependency中排除：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

## 9. 总结

`spring-boot-starter-parent`的主要作用包括：

1. **依赖版本管理**：统一管理Spring Boot及其相关依赖的版本
2. **Java版本配置**：默认配置Java编译版本
3. **编码配置**：默认配置UTF-8编码
4. **资源过滤**：默认开启资源过滤
5. **插件管理**：管理常用插件的版本
6. **默认配置**：提供合理的默认配置

使用`spring-boot-starter-parent`可以大大简化Maven项目的配置，提高开发效率。如果项目需要继承其他父POM，可以使用`dependencyManagement`引入Spring Boot的依赖管理。