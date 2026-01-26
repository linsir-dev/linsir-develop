# Spring Boot中的监视器是什么？

## 1. 监视器概述

Spring Boot Actuator提供了生产级的监视器（Monitor）功能，可以监控应用的运行状态、性能指标、健康检查等。Actuator通过HTTP端点或JMX提供监视功能。

## 2. Actuator简介

### 2.1 添加依赖
在pom.xml中添加`spring-boot-starter-actuator`依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

### 2.2 基本配置
在application.properties或application.yml中配置：

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,env
      base-path: /actuator
  endpoint:
    health:
      show-details: always
```

## 3. 常用监视端点

### 3.1 /actuator/health
健康检查端点，用于监控应用和依赖服务的健康状态。

**默认响应：**
```json
{
  "status": "UP"
}
```

**详细响应：**
```json
{
  "status": "UP",
  "components": {
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 500000000000,
        "free": 250000000000,
        "threshold": 10485760,
        "path": "C:\\"
      }
    },
    "ping": {
      "status": "UP"
    },
    "db": {
      "status": "UP",
      "details": {
        "database": "MySQL",
        "validationQuery": "isValid()"
      }
    }
  }
}
```

**自定义健康指示器：**
```java
@Component
public class CustomHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        if (checkService()) {
            return Health.up()
                    .withDetail("service", "running")
                    .build();
        }
        return Health.down()
                .withDetail("service", "stopped")
                .build();
    }

    private boolean checkService() {
        return true;
    }
}
```

### 3.2 /actuator/info
应用信息端点，用于显示应用的基本信息。

**配置信息：**
```yaml
info:
  app:
    name: My Application
    version: 1.0.0
    description: My Spring Boot Application
  build:
    artifact: myapp
    group: com.example
    version: 1.0.0
```

**响应：**
```json
{
  "app": {
    "name": "My Application",
    "version": "1.0.0",
    "description": "My Spring Boot Application"
  },
  "build": {
    "artifact": "myapp",
    "group": "com.example",
    "version": "1.0.0"
  }
}
```

### 3.3 /actuator/metrics
应用指标端点，用于监控应用的性能指标。

**响应：**
```json
{
  "names": [
    "jvm.memory.max",
    "jvm.memory.used",
    "jvm.gc.pause",
    "system.cpu.count",
    "system.cpu.usage",
    "http.server.requests"
  ]
}
```

**获取具体指标：**
```bash
GET /actuator/metrics/jvm.memory.used
```

**响应：**
```json
{
  "name": "jvm.memory.used",
  "description": "The amount of used memory",
  "baseUnit": "bytes",
  "measurements": [
    {
      "statistic": "VALUE",
      "value": 123456789
    }
  ],
  "availableTags": [
    {
      "tag": "area",
      "values": ["heap", "nonheap"]
    },
    {
      "tag": "id",
      "values": ["G1 Eden Space", "G1 Old Gen"]
    }
  ]
}
```

### 3.4 /actuator/env
环境信息端点，用于查看应用的环境配置。

**响应：**
```json
{
  "activeProfiles": ["dev"],
  "propertySources": [
    {
      "name": "applicationConfig: [classpath:/application-dev.yml]",
      "properties": {
        "server.port": {
          "value": "8080"
        },
        "spring.datasource.url": {
          "value": "jdbc:mysql://localhost:3306/mydb"
        }
      }
    }
  ]
}
```

### 3.5 /actuator/loggers
日志管理端点，用于查看和修改日志级别。

**查看日志配置：**
```bash
GET /actuator/loggers
```

**响应：**
```json
{
  "levels": ["OFF", "ERROR", "WARN", "INFO", "DEBUG", "TRACE"],
  "loggers": {
    "ROOT": {
      "configuredLevel": "INFO",
      "effectiveLevel": "INFO"
    },
    "com.example": {
      "configuredLevel": "DEBUG",
      "effectiveLevel": "DEBUG"
    }
  }
}
```

**修改日志级别：**
```bash
POST /actuator/loggers/com.example
Content-Type: application/json

{
  "configuredLevel": "TRACE"
}
```

### 3.6 /actuator/threaddump
线程转储端点，用于获取应用的线程信息。

**响应：**
```json
{
  "threads": [
    {
      "threadName": "http-nio-8080-exec-1",
      "threadId": 25,
      "blockedTime": -1,
      "blockedCount": 0,
      "waitedTime": 0,
      "waitedCount": 2,
      "lockName": null,
      "lockOwnerId": -1,
      "lockOwnerName": null,
      "inNative": false,
      "suspended": false,
      "threadState": "RUNNABLE",
      "stackTrace": []
    }
  ]
}
```

### 3.7 /actuator/heapdump
堆转储端点，用于获取应用的堆转储文件。

**获取堆转储：**
```bash
GET /actuator/heapdump
```

### 3.8 /actuator/mappings
端点映射端点，用于查看所有的URL映射。

**响应：**
```json
{
  "contexts": {
    "application": {
      "mappings": {
        "dispatcherServlets": {
          "dispatcherServlet": [
            {
              "predicate": "{GET /actuator/health}",
              "handler": "org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping"
            }
          ]
        }
      }
    }
  }
}
```

## 4. 自定义端点

### 4.1 创建自定义端点
```java
@Component
@Endpoint(id = "custom")
public class CustomEndpoint {

    @ReadOperation
    public Map<String, Object> customInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("message", "Custom endpoint");
        info.put("timestamp", System.currentTimeMillis());
        return info;
    }

    @WriteOperation
    public void updateInfo(String message) {
        System.out.println("Update info: " + message);
    }

    @DeleteOperation
    public void deleteInfo() {
        System.out.println("Delete info");
    }
}
```

### 4.2 访问自定义端点
```bash
GET /actuator/custom
POST /actuator/custom
DELETE /actuator/custom
```

## 5. 安全配置

### 5.1 启用Spring Security
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

### 5.2 配置端点安全
```java
@Configuration
public class ActuatorSecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/actuator/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults());
        return http.build();
    }
}
```

### 5.3 配置用户
```yaml
spring:
  security:
    user:
      name: admin
      password: admin123
      roles: ADMIN
```

## 6. 集成监控系统

### 6.1 集成Prometheus
添加依赖：

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

配置：

```yaml
management:
  endpoints:
    web:
      exposure:
        include: prometheus
  metrics:
    export:
      prometheus:
        enabled: true
```

访问：

```bash
GET /actuator/prometheus
```

### 6.2 集成Grafana
1. 配置Prometheus数据源
2. 创建仪表板
3. 监控应用指标

### 6.3 集成InfluxDB
添加依赖：

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-influx</artifactId>
</dependency>
```

配置：

```yaml
management:
  metrics:
    export:
      influx:
        uri: http://localhost:8086
        db: mydb
        user: admin
        password: password
```

## 7. 自定义指标

### 7.1 使用Counter
```java
@Component
public class CustomMetrics {
    private final Counter requestCounter;

    public CustomMetrics(MeterRegistry registry) {
        this.requestCounter = Counter.builder("custom.requests")
                .description("Number of custom requests")
                .tag("type", "api")
                .register(registry);
    }

    public void incrementRequest() {
        requestCounter.increment();
    }
}
```

### 7.2 使用Gauge
```java
@Component
public class CustomMetrics {
    public CustomMetrics(MeterRegistry registry) {
        Gauge.builder("custom.queue.size", queue, Queue::size)
                .description("Size of the custom queue")
                .register(registry);
    }
}
```

### 7.3 使用Timer
```java
@Component
public class CustomMetrics {
    private final Timer requestTimer;

    public CustomMetrics(MeterRegistry registry) {
        this.requestTimer = Timer.builder("custom.request.timer")
                .description("Time taken to process request")
                .register(registry);
    }

    public void processRequest() {
        requestTimer.record(() -> {
        });
    }
}
```

## 8. 最佳实践

### 8.1 生产环境配置
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
      base-path: /actuator
  endpoint:
    health:
      show-details: when-authorized
  metrics:
    export:
      prometheus:
        enabled: true
```

### 8.2 开发环境配置
```yaml
management:
  endpoints:
    web:
      exposure:
        include: "*"
  endpoint:
    health:
      show-details: always
```

### 8.3 安全配置
- 生产环境启用端点安全
- 使用HTTPS
- 定期更新密码

## 9. 总结

Spring Boot Actuator提供了强大的监视功能：

1. **健康检查**：监控应用和依赖服务的健康状态
2. **应用信息**：显示应用的基本信息
3. **性能指标**：监控应用的性能指标
4. **环境信息**：查看应用的环境配置
5. **日志管理**：查看和修改日志级别
6. **线程转储**：获取应用的线程信息
7. **堆转储**：获取应用的堆转储文件
8. **自定义端点**：创建自定义的监视端点
9. **集成监控系统**：集成Prometheus、Grafana等监控系统
10. **自定义指标**：创建自定义的性能指标

Actuator是Spring Boot生产环境监控的重要工具，建议在生产环境中使用。