# 如何自定义Spring Boot Starter？

## 1. 自定义Starter概述

自定义Spring Boot Starter可以将常用的功能模块化，提高代码复用性，简化项目配置。本文将详细介绍如何创建自定义Starter。

## 2. 项目结构

### 2.1 标准项目结构
```
my-spring-boot-starter/
├── pom.xml
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── example/
        │           └── mystarter/
        │               ├── MyStarterAutoConfiguration.java
        │               ├── MyStarterProperties.java
        │               └── MyService.java
        └── resources/
            └── META-INF/
                └── spring.factories
```

### 2.2 多模块项目结构
```
my-spring-boot-starter/
├── my-spring-boot-starter/          # Starter模块
│   ├── pom.xml
│   └── src/
│       └── main/
│           ├── java/
│           └── resources/
└── my-spring-boot-starter-autoconfigure/  # 自动配置模块
    ├── pom.xml
    └── src/
        └── main/
            ├── java/
            └── resources/
```

## 3. 创建Starter项目

### 3.1 创建父POM
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>my-spring-boot-starter</artifactId>
    <version>1.0.0</version>
    <packaging>pom</packaging>

    <name>My Spring Boot Starter</name>
    <description>Custom Spring Boot Starter</description>

    <modules>
        <module>my-spring-boot-starter-autoconfigure</module>
        <module>my-spring-boot-starter</module>
    </modules>

    <properties>
        <java.version>17</java.version>
        <spring-boot.version>3.2.0</spring-boot.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>${spring-boot.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>
```

### 3.2 创建自动配置模块POM
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.example</groupId>
        <artifactId>my-spring-boot-starter</artifactId>
        <version>1.0.0</version>
    </parent>

    <artifactId>my-spring-boot-starter-autoconfigure</artifactId>
    <name>My Spring Boot Starter Autoconfigure</name>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>
    </dependencies>
</project>
```

### 3.3 创建Starter模块POM
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.example</groupId>
        <artifactId>my-spring-boot-starter</artifactId>
        <version>1.0.0</version>
    </parent>

    <artifactId>my-spring-boot-starter</artifactId>
    <name>My Spring Boot Starter</name>

    <dependencies>
        <dependency>
            <groupId>com.example</groupId>
            <artifactId>my-spring-boot-starter-autoconfigure</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>
</project>
```

## 4. 创建自动配置类

### 4.1 创建配置属性类
```java
package com.example.mystarter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mystarter")
public class MyStarterProperties {
    private String name = "default";
    private boolean enabled = true;
    private int timeout = 3000;
    private String url;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
```

### 4.2 创建服务类
```java
package com.example.mystarter;

public class MyService {
    private final MyStarterProperties properties;

    public MyService(MyStarterProperties properties) {
        this.properties = properties;
    }

    public String sayHello() {
        return "Hello, " + properties.getName() + "!";
    }

    public boolean isEnabled() {
        return properties.isEnabled();
    }

    public int getTimeout() {
        return properties.getTimeout();
    }

    public String getUrl() {
        return properties.getUrl();
    }
}
```

### 4.3 创建自动配置类
```java
package com.example.mystarter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MyStarterProperties.class)
@ConditionalOnProperty(prefix = "mystarter", name = "enabled", havingValue = "true", matchIfMissing = true)
public class MyStarterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MyService myService(MyStarterProperties properties) {
        return new MyService(properties);
    }
}
```

## 5. 注册自动配置

### 5.1 创建spring.factories文件
在`src/main/resources/META-INF/`目录下创建`spring.factories`文件：

```properties
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.example.mystarter.MyStarterAutoConfiguration
```

### 5.2 Spring Boot 2.7+使用spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports文件
在`src/main/resources/META-INF/spring/`目录下创建`org.springframework.boot.autoconfigure.AutoConfiguration.imports`文件：

```
com.example.mystarter.MyStarterAutoConfiguration
```

## 6. 条件注解的使用

### 6.1 @ConditionalOnClass
当类路径中存在指定的类时生效。

```java
@Configuration
@ConditionalOnClass(MyService.class)
public class MyStarterAutoConfiguration {
}
```

### 6.2 @ConditionalOnMissingBean
当Spring容器中不存在指定的Bean时生效。

```java
@Bean
@ConditionalOnMissingBean
public MyService myService(MyStarterProperties properties) {
    return new MyService(properties);
}
```

### 6.3 @ConditionalOnProperty
当配置文件中存在指定的属性时生效。

```java
@Configuration
@ConditionalOnProperty(prefix = "mystarter", name = "enabled", havingValue = "true", matchIfMissing = true)
public class MyStarterAutoConfiguration {
}
```

### 6.4 @ConditionalOnWebApplication
当应用是Web应用时生效。

```java
@Configuration
@ConditionalOnWebApplication
public class MyWebAutoConfiguration {
}
```

### 6.5 @ConditionalOnMissingClass
当类路径中不存在指定的类时生效。

```java
@Configuration
@ConditionalOnMissingClass("com.example.MyService")
public class MyAlternativeAutoConfiguration {
}
```

## 7. 使用自定义Starter

### 7.1 安装到本地仓库
```bash
mvn clean install
```

### 7.2 在项目中使用
```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>my-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 7.3 配置属性
在`application.properties`或`application.yml`中配置：

```yaml
mystarter:
  name: My Custom Starter
  enabled: true
  timeout: 5000
  url: http://example.com
```

### 7.4 使用服务
```java
@RestController
public class MyController {
    @Autowired
    private MyService myService;

    @GetMapping("/hello")
    public String hello() {
        return myService.sayHello();
    }
}
```

## 8. 高级特性

### 8.1 多个自动配置类
```java
@Configuration
@Import({MyStarterAutoConfiguration.class, MyWebAutoConfiguration.class})
public class MyStarterConfiguration {
}
```

### 8.2 自动配置顺序
使用`@AutoConfigureBefore`和`@AutoConfigureAfter`控制自动配置顺序。

```java
@Configuration
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
public class MyStarterAutoConfiguration {
}

@Configuration
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class MyStarterAutoConfiguration {
}
```

### 8.3 自定义条件注解
```java
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Conditional(OnMyCondition.class)
public @interface ConditionalOnMyProperty {
    String value();
}

public class OnMyCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String value = (String) metadata.getAnnotationAttributes(ConditionalOnMyProperty.class.getName()).get("value");
        return "true".equals(value);
    }
}
```

## 9. 测试Starter

### 9.1 创建测试项目
```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>my-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

### 9.2 编写测试用例
```java
@SpringBootTest
class MyStarterTest {
    @Autowired
    private MyService myService;

    @Test
    void testMyService() {
        assertNotNull(myService);
        assertEquals("Hello, default!", myService.sayHello());
    }
}
```

## 10. 发布Starter

### 10.1 配置仓库
在`pom.xml`中配置仓库：

```xml
<distributionManagement>
    <repository>
        <id>my-repo</id>
        <url>https://repo.example.com/maven2</url>
    </repository>
    <snapshotRepository>
        <id>my-snapshot-repo</id>
        <url>https://repo.example.com/maven2-snapshot</url>
    </snapshotRepository>
</distributionManagement>
```

### 10.2 部署到仓库
```bash
mvn deploy
```

### 10.3 发布到Maven中央仓库
参考Maven中央仓库的发布流程，配置GPG签名等。

## 11. 最佳实践

### 11.1 命名规范
- 官方Starter：`spring-boot-starter-*`
- 第三方Starter：`*-spring-boot-starter`

### 11.2 提供合理的默认值
```java
@ConfigurationProperties(prefix = "mystarter")
public class MyStarterProperties {
    private String name = "default";
    private boolean enabled = true;
    private int timeout = 3000;
}
```

### 11.3 使用条件注解
确保Starter只在合适的条件下生效。

### 11.4 提供清晰的文档
为Starter提供详细的使用文档和示例。

### 11.5 版本管理
合理管理Starter的版本，遵循语义化版本规范。

## 12. 总结

自定义Spring Boot Starter的步骤：

1. **创建项目结构**：按照标准结构创建项目
2. **创建配置属性类**：使用`@ConfigurationProperties`注解
3. **创建服务类**：实现具体的功能
4. **创建自动配置类**：使用`@Configuration`和条件注解
5. **注册自动配置**：在`spring.factories`或`AutoConfiguration.imports`中注册
6. **测试Starter**：编写测试用例验证功能
7. **发布Starter**：部署到Maven仓库

自定义Starter可以大大提高代码复用性，简化项目配置，是Spring Boot开发中的重要技能。