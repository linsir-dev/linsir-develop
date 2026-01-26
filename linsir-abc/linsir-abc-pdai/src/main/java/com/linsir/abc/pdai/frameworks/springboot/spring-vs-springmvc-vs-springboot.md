# Spring、Spring MVC和SpringBoot有什么区别？

## 1. 概述

Spring、Spring MVC和Spring Boot是Spring生态系统中的三个重要组件，它们各自有不同的职责和定位。

## 2. Spring框架

### 2.1 定义
Spring是一个轻量级的Java开发框架，为应用开发提供全面的基础设施支持。

### 2.2 核心特性
- **IOC容器**：控制反转，管理对象的生命周期
- **AOP支持**：面向切面编程，实现横切关注点
- **事务管理**：声明式事务管理
- **数据访问**：统一的数据访问抽象
- **Web支持**：Spring MVC框架

### 2.3 应用场景
```java
@Configuration
public class AppConfig {
    @Bean
    public UserService userService() {
        return new UserService();
    }

    @Bean
    public OrderService orderService(UserService userService) {
        return new OrderService(userService);
    }
}
```

## 3. Spring MVC

### 3.1 定义
Spring MVC是Spring框架的一部分，专门用于构建Web应用程序。

### 3.2 核心组件
- **DispatcherServlet**：前端控制器
- **HandlerMapping**：处理器映射器
- **Controller**：处理器
- **ViewResolver**：视图解析器
- **Model**：数据模型

### 3.3 应用场景
```java
@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public String getUser(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "user/detail";
    }

    @PostMapping
    @ResponseBody
    public User createUser(@RequestBody User user) {
        return userService.save(user);
    }
}
```

## 4. Spring Boot

### 4.1 定义
Spring Boot是基于Spring框架的快速开发框架，简化了Spring应用的配置和部署。

### 4.2 核心特性
- **自动配置**：根据依赖自动配置Spring应用
- **Starter依赖**：提供开箱即用的依赖集合
- **内嵌容器**：内嵌Tomcat、Jetty等容器
- **生产就绪**：提供监控、健康检查等特性

### 4.3 应用场景
```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.findById(id);
    }
}
```

## 5. 三者关系

### 5.1 层次关系
```
Spring Boot
    ├── Spring
    │   ├── IOC容器
    │   ├── AOP
    │   └── 事务管理
    └── Spring MVC
        ├── DispatcherServlet
        ├── Controller
        └── ViewResolver
```

### 5.2 包含关系
- Spring Boot包含了Spring和Spring MVC
- Spring MVC是Spring框架的一部分
- Spring是基础框架，Spring MVC和Spring Boot都是基于Spring构建的

## 6. 详细对比

### 6.1 配置方式对比

**Spring传统配置：**
```xml
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd
       http://www.springframework.org/schema/context
       http://www.springframework.org/schema/context/spring-context.xsd
       http://www.springframework.org/schema/mvc
       http://www.springframework.org/schema/mvc/spring-mvc.xsd">

    <context:component-scan base-package="com.example"/>
    <mvc:annotation-driven/>

    <bean id="dataSource" class="org.apache.commons.dbcp2.BasicDataSource">
        <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
        <property name="url" value="jdbc:mysql://localhost:3306/mydb"/>
        <property name="username" value="root"/>
        <property name="password" value="password"/>
    </bean>

    <bean id="viewResolver" class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="prefix" value="/WEB-INF/views/"/>
        <property name="suffix" value=".jsp"/>
    </bean>
</beans>
```

**Spring Boot配置：**
```properties
# application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/mydb
spring.datasource.username=root
spring.datasource.password=password
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
```

### 6.2 项目结构对比

**Spring传统项目结构：**
```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── example/
│   │           ├── controller/
│   │           ├── service/
│   │           ├── dao/
│   │           └── config/
│   ├── resources/
│   │   ├── applicationContext.xml
│   │   ├── spring-mvc.xml
│   │   └── jdbc.properties
│   └── webapp/
│       ├── WEB-INF/
│       │   ├── web.xml
│       │   └── views/
│       └── static/
└── test/
```

**Spring Boot项目结构：**
```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── example/
│   │           ├── controller/
│   │           ├── service/
│   │           └── Application.java
│   └── resources/
│       ├── application.properties
│       ├── application-dev.properties
│       └── static/
└── test/
```

### 6.3 部署方式对比

**Spring传统部署：**
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-war-plugin</artifactId>
            <version>3.3.1</version>
        </plugin>
    </plugins>
</build>
```
需要部署到外部Servlet容器（Tomcat、Jetty等）

**Spring Boot部署：**
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
打包成可执行jar，内嵌容器

## 7. 功能对比表

| 特性 | Spring | Spring MVC | Spring Boot |
|------|--------|------------|-------------|
| **定位** | 基础框架 | Web框架 | 快速开发框架 |
| **IOC容器** | ✓ | ✓ | ✓ |
| **AOP支持** | ✓ | ✓ | ✓ |
| **Web支持** | - | ✓ | ✓ |
| **自动配置** | - | - | ✓ |
| **Starter依赖** | - | - | ✓ |
| **内嵌容器** | - | - | ✓ |
| **配置方式** | XML/注解 | XML/注解 | 注解/properties/yml |
| **部署方式** | WAR | WAR | 可执行JAR |
| **启动方式** | 容器启动 | 容器启动 | main方法启动 |
| **监控功能** | - | - | ✓ |
| **开发效率** | 中 | 中 | 高 |

## 8. 使用场景对比

### 8.1 使用Spring的场景
- 需要精细控制配置
- 遗留系统维护
- 需要与其他框架深度集成
- 对性能有极致要求

### 8.2 使用Spring MVC的场景
- 构建传统的Web应用
- 需要复杂的视图渲染
- 与Spring框架深度集成
- 需要灵活的配置

### 8.3 使用Spring Boot的场景
- 快速开发新项目
- 微服务架构
- 云原生应用
- RESTful API开发
- 需要快速迭代

## 9. 实际案例对比

### 9.1 创建REST API

**Spring MVC方式：**
```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        User user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }
}
```

**Spring Boot方式：**
```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.findById(id);
    }
}
```

### 9.2 数据源配置

**Spring方式：**
```java
@Configuration
public class DataSourceConfig {
    @Bean
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/mydb");
        dataSource.setUsername("root");
        dataSource.setPassword("password");
        return dataSource;
    }
}
```

**Spring Boot方式：**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/mydb
spring.datasource.username=root
spring.datasource.password=password
```

## 10. 总结

### 10.1 Spring
- 是基础框架，提供IOC、AOP等核心功能
- 适合需要精细控制配置的场景
- 学习曲线较陡，配置复杂

### 10.2 Spring MVC
- 是Spring框架的Web模块
- 专注于Web应用开发
- 提供了强大的MVC支持

### 10.3 Spring Boot
- 是基于Spring的快速开发框架
- 简化了配置和部署
- 提高了开发效率
- 适合现代应用开发

### 10.4 选择建议
- **新项目**：优先选择Spring Boot
- **微服务**：使用Spring Boot
- **传统Web应用**：可以考虑Spring MVC
- **遗留系统**：继续使用Spring

Spring Boot是Spring生态系统的演进方向，它简化了Spring和Spring MVC的使用，是现代Java应用开发的首选。但理解Spring和Spring MVC的原理对于深入掌握Spring Boot仍然非常重要。