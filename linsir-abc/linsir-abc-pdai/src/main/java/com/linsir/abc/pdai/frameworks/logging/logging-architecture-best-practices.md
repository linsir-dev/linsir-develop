# 对日志架构使用比较好的实践？

## 1. 日志架构概述

良好的日志架构是系统可观测性的基础，它不仅能够帮助开发者快速定位问题，还能为系统监控、性能分析和业务决策提供重要数据支持。本文将介绍日志架构的最佳实践。

## 2. 日志分层架构

### 2.1 三层日志架构

```
┌─────────────────────────────────────────┐
│         应用层日志                        │
│  - 业务日志                              │
│  - 异常日志                              │
│  - 性能日志                              │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│         中间件层日志                       │
│  - 框架日志                              │
│  - 中间件日志                            │
│  - 系统日志                              │
└──────────────┬──────────────────────────┘
               │
               ▼
┌─────────────────────────────────────────┐
│         基础设施层日志                     │
│  - 容器日志                              │
│  - 网络日志                              │
│  - 系统日志                              │
└─────────────────────────────────────────┘
```

### 2.2 分层实践

**应用层日志**：

```java
@Service
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    
    public void createOrder(Order order) {
        MDC.put("orderId", order.getId());
        MDC.put("userId", order.getUserId());
        
        try {
            logger.info("开始创建订单");
            
            orderRepository.save(order);
            
            logger.info("订单创建成功");
        } catch (Exception e) {
            logger.error("订单创建失败", e);
            throw e;
        } finally {
            MDC.clear();
        }
    }
}
```

**中间件层日志**：

```xml
<!-- Spring Boot配置 -->
logging:
  level:
    org.springframework.web: INFO
    org.springframework.security: INFO
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

**基础设施层日志**：

```yaml
# Docker Compose配置
version: '3'
services:
  app:
    logging:
      driver: "json-file"
      options:
        max-size: "10m"
        max-file: "3"
        labels: "app,production"
```

## 3. 日志分类实践

### 3.1 按日志级别分类

| 级别 | 使用场景 | 示例 |
|-----|---------|------|
| TRACE | 详细的调试信息 | 方法入参、出参、中间状态 |
| DEBUG | 调试信息 | 关键业务逻辑、状态变化 |
| INFO | 重要信息 | 业务流程、系统状态 |
| WARN | 警告信息 | 潜在问题、降级处理 |
| ERROR | 错误信息 | 异常、失败操作 |
| FATAL | 致命错误 | 系统崩溃、无法恢复 |

**实践示例**：

```java
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    public User getUser(String userId) {
        logger.trace("获取用户，userId: {}", userId);
        
        try {
            User user = userRepository.findById(userId);
            
            if (user == null) {
                logger.warn("用户不存在，userId: {}", userId);
                return null;
            }
            
            logger.debug("用户信息: {}", user);
            logger.info("获取用户成功，userId: {}", userId);
            
            return user;
        } catch (Exception e) {
            logger.error("获取用户失败，userId: {}", userId, e);
            throw new RuntimeException("获取用户失败", e);
        }
    }
}
```

### 3.2 按日志类型分类

**业务日志**：

```java
@Service
public class PaymentService {
    private static final Logger businessLogger = LoggerFactory.getLogger("BUSINESS_LOGGER");
    
    public void processPayment(Payment payment) {
        businessLogger.info("支付处理开始，paymentId: {}, amount: {}", 
                           payment.getId(), payment.getAmount());
        
        try {
            paymentGateway.process(payment);
            
            businessLogger.info("支付处理成功，paymentId: {}", payment.getId());
        } catch (Exception e) {
            businessLogger.error("支付处理失败，paymentId: {}, error: {}", 
                                payment.getId(), e.getMessage());
            throw e;
        }
    }
}
```

**审计日志**：

```java
@Service
public class AuditService {
    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT_LOGGER");
    
    public void logUserAction(String userId, String action, String resource) {
        auditLogger.info("用户操作，userId: {}, action: {}, resource: {}, timestamp: {}", 
                        userId, action, resource, System.currentTimeMillis());
    }
}
```

**性能日志**：

```java
@Aspect
@Component
public class PerformanceAspect {
    private static final Logger perfLogger = LoggerFactory.getLogger("PERFORMANCE_LOGGER");
    
    @Around("@annotation(PerformanceMonitor)")
    public Object monitorPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        
        long startTime = System.currentTimeMillis();
        
        try {
            Object result = joinPoint.proceed();
            
            long duration = System.currentTimeMillis() - startTime;
            perfLogger.info("方法执行，class: {}, method: {}, duration: {}ms", 
                           className, methodName, duration);
            
            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            perfLogger.error("方法执行异常，class: {}, method: {}, duration: {}ms, error: {}", 
                            className, methodName, duration, e.getMessage());
            throw e;
        }
    }
}
```

## 4. 日志格式化实践

### 4.1 统一日志格式

**推荐格式**：

```
时间戳 | 线程 | 日志级别 | Logger名称 | MDC上下文 | 消息内容 | 异常堆栈
```

**Logback配置**：

```xml
<encoder>
    <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} | [%thread] | %-5level | %logger{36} | %X{traceId},%X{userId} | %msg | %ex%n</pattern>
</encoder>
```

**输出示例**：

```
2024-01-26 10:30:45.123 | [http-nio-8080-exec-1] | INFO  | c.l.a.c.OrderController | abc123,user001 | 创建订单成功 |
2024-01-26 10:30:45.456 | [http-nio-8080-exec-1] | ERROR | c.l.a.c.OrderController | abc123,user001 | 订单创建失败 | java.lang.RuntimeException: 库存不足
```

### 4.2 JSON格式化

**适用场景**：
- 日志聚合系统（ELK、Splunk）
- 结构化日志分析
- 微服务架构

**Logback配置**：

```xml
<dependency>
    <groupId>net.logstash.logback</groupId>
    <artifactId>logstash-logback-encoder</artifactId>
    <version>7.4</version>
</dependency>

<appender name="JSON_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>logs/application.json</file>
    <encoder class="net.logstash.logback.encoder.LogstashEncoder">
        <fieldNames>
            <timestamp>timestamp</timestamp>
            <level>level</level>
            <logger>logger</logger>
            <message>message</message>
            <stackTrace>stack_trace</stackTrace>
        </fieldNames>
        <includeMdc>true</includeMdc>
        <includeContext>true</includeContext>
        <customFields>{"service":"order-service","version":"1.0.0"}</customFields>
    </encoder>
    <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
        <fileNamePattern>logs/application-%d{yyyy-MM-dd}.json</fileNamePattern>
        <maxHistory>7</maxHistory>
    </rollingPolicy>
</appender>
```

**输出示例**：

```json
{
  "@timestamp": "2024-01-26T10:30:45.123+08:00",
  "@version": "1",
  "message": "创建订单成功",
  "logger_name": "com.linsir.abc.controller.OrderController",
  "thread_name": "http-nio-8080-exec-1",
  "level": "INFO",
  "level_value": 20000,
  "service": "order-service",
  "version": "1.0.0",
  "traceId": "abc123",
  "userId": "user001"
}
```

## 5. 日志上下文实践

### 5.1 使用MDC传递上下文

**拦截器配置**：

```java
@Component
public class RequestInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String traceId = request.getHeader("X-Trace-Id");
        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
        }
        
        String userId = request.getHeader("X-User-Id");
        String sessionId = request.getSession().getId();
        
        MDC.put("traceId", traceId);
        MDC.put("userId", userId);
        MDC.put("sessionId", sessionId);
        MDC.put("requestUri", request.getRequestURI());
        MDC.put("clientIp", getClientIp(request));
        
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                               Object handler, Exception ex) {
        MDC.clear();
    }
    
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
```

**使用示例**：

```java
@Service
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    
    public void createOrder(Order order) {
        logger.info("开始创建订单，orderId: {}", order.getId());
        
        orderRepository.save(order);
        
        logger.info("订单创建成功，orderId: {}", order.getId());
    }
}
```

### 5.2 分布式追踪

**Spring Cloud Sleuth集成**：

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-sleuth</artifactId>
</dependency>
```

**日志输出**：

```
2024-01-26 10:30:45.123 INFO [order-service,abc123,def456] --- [http-nio-8080-exec-1] c.l.a.c.OrderController : 创建订单成功
```

**字段说明**：
- `order-service`：应用名称
- `abc123`：Trace ID（整个请求链路的唯一标识）
- `def456`：Span ID（单个服务调用的唯一标识）

## 6. 日志性能实践

### 6.1 异步日志

**Logback异步配置**：

```xml
<appender name="ASYNC_FILE" class="ch.qos.logback.classic.AsyncAppender">
    <queueSize>512</queueSize>
    <discardingThreshold>0</discardingThreshold>
    <neverBlock>true</neverBlock>
    <appender-ref ref="ROLLING_FILE"/>
</appender>
```

**参数说明**：

| 参数 | 说明 | 推荐值 |
|-----|------|-------|
| queueSize | 队列大小 | 512-1024 |
| discardingThreshold | 丢弃阈值 | 0（不丢弃） |
| neverBlock | 是否阻塞 | true（不阻塞） |

**Log4j 2异步配置**：

```xml
<Configuration status="WARN">
    <Appenders>
        <RollingFile name="RollingFile" fileName="logs/application.log"
                     filePattern="logs/application-%d{yyyy-MM-dd}-%i.log">
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
            <Policies>
                <TimeBasedTriggeringPolicy/>
                <SizeBasedTriggeringPolicy size="100MB"/>
            </Policies>
        </RollingFile>
        
        <Async name="Async">
            <AppenderRef ref="RollingFile"/>
        </Async>
    </Appenders>
    
    <Loggers>
        <AsyncLogger name="com.linsir.abc" level="info" additivity="false">
            <AppenderRef ref="Async"/>
        </AsyncLogger>
        
        <Root level="info">
            <AppenderRef ref="Async"/>
        </Root>
    </Loggers>
</Configuration>
```

### 6.2 参数化日志

**推荐做法**：

```java
// 推荐：参数化日志
logger.debug("用户ID: {}, 用户名: {}", userId, username);

// 不推荐：字符串拼接
logger.debug("用户ID: " + userId + ", 用户名: " + username);

// 不推荐：条件判断
if (logger.isDebugEnabled()) {
    logger.debug("用户ID: " + userId + ", 用户名: " + username);
}
```

### 6.3 条件日志

**复杂对象日志**：

```java
public void processOrder(Order order) {
    if (logger.isDebugEnabled()) {
        logger.debug("订单详情: {}", JsonUtils.toJson(order));
    }
    
    orderRepository.save(order);
}
```

## 7. 日志安全实践

### 7.1 敏感信息脱敏

**自定义Converter**：

```java
public class MaskingConverter extends ClassicConverter {
    private static final Pattern[] PATTERNS = {
        Pattern.compile("(password|pwd)=[^&\\s]+", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(token|access_token)=[^&\\s]+", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(creditCard|cardNumber)=\\d+", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(phone|mobile)=\\d{11}", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(idCard|idNumber)=\\d{18}", Pattern.CASE_INSENSITIVE)
    };
    
    @Override
    public String convert(ILoggingEvent event) {
        String message = event.getFormattedMessage();
        
        for (Pattern pattern : PATTERNS) {
            message = pattern.matcher(message).replaceAll("$1=***");
        }
        
        return message;
    }
}
```

**配置示例**：

```xml
<conversionRule conversionWord="mask" converterClass="com.linsir.abc.logging.MaskingConverter"/>

<encoder>
    <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %mask%n</pattern>
</encoder>
```

### 7.2 日志权限控制

**文件权限设置**：

```bash
# 设置日志文件权限（仅所有者可读写，组用户可读）
chmod 640 logs/application.log

# 设置日志目录权限
chmod 750 logs
```

**Spring Boot配置**：

```yaml
logging:
  file:
    name: /var/log/app/application.log
    max-size: 100MB
    max-history: 30
```

**Docker配置**：

```yaml
version: '3'
services:
  app:
    volumes:
      - ./logs:/var/log/app
    logging:
      driver: "json-file"
      options:
        max-size: "10m"
        max-file: "3"
```

## 8. 日志监控实践

### 8.1 日志聚合

**ELK Stack集成**：

```xml
<dependency>
    <groupId>net.logstash.logback</groupId>
    <artifactId>logstash-logback-encoder</artifactId>
    <version>7.4</version>
</dependency>

<appender name="LOGSTASH" class="ch.qos.logback.core.net.SocketAppender">
    <remoteHost>logstash-server</remoteHost>
    <port>5000</port>
    <encoder class="net.logstash.logback.encoder.LogstashEncoder"/>
</appender>

<root level="INFO">
    <appender-ref ref="LOGSTASH"/>
</root>
```

### 8.2 错误日志告警

**自定义Appender**：

```java
public class ErrorAlertAppender extends AppenderBase<ILoggingEvent> {
    private AlertService alertService;
    private String alertLevel = "ERROR";
    
    @Override
    protected void append(ILoggingEvent event) {
        if (event.getLevel().levelInt >= Level.ERROR_INT) {
            AlertMessage message = AlertMessage.builder()
                .level(event.getLevel().toString())
                .logger(event.getLoggerName())
                .message(event.getFormattedMessage())
                .timestamp(new Date(event.getTimeStamp()))
                .build();
            
            alertService.sendAlert(message);
        }
    }
    
    public void setAlertService(AlertService alertService) {
        this.alertService = alertService;
    }
    
    public void setAlertLevel(String alertLevel) {
        this.alertLevel = alertLevel;
    }
}
```

**配置示例**：

```xml
<appender name="ERROR_ALERT" class="com.linsir.abc.logging.ErrorAlertAppender">
    <alertService>
        <host>alert-server</host>
        <port>8080</port>
        <endpoint>/api/alerts</endpoint>
    </alertService>
    <alertLevel>ERROR</alertLevel>
</appender>

<root level="INFO">
    <appender-ref ref="FILE"/>
    <appender-ref ref="ERROR_ALERT"/>
</root>
```

### 8.3 日志指标监控

**Prometheus集成**：

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-core</artifactId>
</dependency>

<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

**自定义Metrics**：

```java
@Component
public class LoggingMetrics {
    private final Counter errorCounter;
    private final Counter warnCounter;
    
    public LoggingMetrics(MeterRegistry registry) {
        this.errorCounter = Counter.builder("logging.errors")
            .description("Number of error logs")
            .register(registry);
        
        this.warnCounter = Counter.builder("logging.warnings")
            .description("Number of warning logs")
            .register(registry);
    }
    
    public void recordError(String logger) {
        errorCounter.increment(Tags.of("logger", logger));
    }
    
    public void recordWarning(String logger) {
        warnCounter.increment(Tags.of("logger", logger));
    }
}
```

## 9. 日志管理实践

### 9.1 日志轮转策略

**基于时间的轮转**：

```xml
<rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
    <fileNamePattern>logs/application-%d{yyyy-MM-dd}.log</fileNamePattern>
    <maxHistory>30</maxHistory>
    <totalSizeCap>10GB</totalSizeCap>
</rollingPolicy>
```

**基于大小的轮转**：

```xml
<rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
    <fileNamePattern>logs/application-%d{yyyy-MM-dd}-%i.log</fileNamePattern>
    <maxFileSize>100MB</maxFileSize>
    <maxHistory>30</maxHistory>
    <totalSizeCap>10GB</totalSizeCap>
</rollingPolicy>
```

### 9.2 日志清理策略

**Logback配置**：

```xml
<rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
    <fileNamePattern>logs/application-%d{yyyy-MM-dd}.log</fileNamePattern>
    <maxHistory>30</maxHistory>
    <totalSizeCap>10GB</totalSizeCap>
    <cleanHistoryOnStart>true</cleanHistoryOnStart>
</rollingPolicy>
```

**Log4j 2配置**：

```xml
<DefaultRolloverStrategy max="30">
    <Delete basePath="logs" maxDepth="1">
        <IfFileName glob="application-*.log"/>
        <IfLastModified age="30d"/>
    </Delete>
</DefaultRolloverStrategy>
```

### 9.3 日志归档策略

**压缩归档**：

```xml
<rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
    <fileNamePattern>logs/application-%d{yyyy-MM-dd}.log.gz</fileNamePattern>
    <maxHistory>30</maxHistory>
</rollingPolicy>
```

**归档脚本**：

```bash
#!/bin/bash
# archive_logs.sh

LOG_DIR="/var/log/app"
ARCHIVE_DIR="/var/log/app/archive"
DATE=$(date +%Y%m)

# 创建归档目录
mkdir -p $ARCHIVE_DIR

# 归档上个月的日志
find $LOG_DIR -name "application-$(date -d '1 month ago' +%Y-%m)*.log" -exec mv {} $ARCHIVE_DIR/ \;

# 压缩归档日志
gzip $ARCHIVE_DIR/*.log
```

## 10. 最佳实践总结

### 10.1 日志记录原则

1. **适度记录**：记录关键信息，避免过度日志
2. **结构化日志**：使用JSON格式，便于解析和分析
3. **上下文完整**：包含Trace ID、用户ID等关键上下文
4. **异常完整**：记录完整的异常堆栈信息
5. **性能优先**：使用异步日志，参数化日志

### 10.2 日志配置检查清单

- [ ] 配置合适的日志级别
- [ ] 使用异步日志提高性能
- [ ] 配置日志轮转策略
- [ ] 设置日志文件大小和保留时间
- [ ] 使用MDC传递上下文信息
- [ ] 过滤敏感信息
- [ ] 配置日志格式（推荐JSON格式）
- [ ] 集成日志监控系统
- [ ] 配置错误日志告警
- [ ] 定期清理过期日志

### 10.3 日志架构模板

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <property name="LOG_PATH" value="${LOG_PATH:-logs}"/>
    <property name="APP_NAME" value="${APP_NAME:-application}"/>
    <property name="LOG_PATTERN" value="%d{yyyy-MM-dd HH:mm:ss.SSS} | [%thread] | %-5level | %logger{36} | %X{traceId},%X{userId} | %msg | %ex%n"/>
    
    <conversionRule conversionWord="mask" converterClass="com.linsir.abc.logging.MaskingConverter"/>
    
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>${LOG_PATTERN}</pattern>
        </encoder>
    </appender>
    
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_PATH}/${APP_NAME}.log</file>
        <encoder>
            <pattern>${LOG_PATTERN}</pattern>
        </encoder>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>${LOG_PATH}/${APP_NAME}-%d{yyyy-MM-dd}-%i.log</fileNamePattern>
            <maxFileSize>100MB</maxFileSize>
            <maxHistory>30</maxHistory>
            <totalSizeCap>10GB</totalSizeCap>
        </rollingPolicy>
    </appender>
    
    <appender name="ASYNC_FILE" class="ch.qos.logback.classic.AsyncAppender">
        <queueSize>512</queueSize>
        <discardingThreshold>0</discardingThreshold>
        <neverBlock>true</neverBlock>
        <appender-ref ref="FILE"/>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="ASYNC_FILE"/>
    </root>
</configuration>
```

## 11. 总结

良好的日志架构实践包括：

1. **分层架构**：应用层、中间件层、基础设施层
2. **日志分类**：按级别、按类型分类记录
3. **统一格式**：使用结构化格式（JSON）
4. **上下文传递**：使用MDC传递关键上下文
5. **性能优化**：异步日志、参数化日志
6. **安全保护**：敏感信息脱敏、权限控制
7. **监控告警**：集成日志监控系统、错误告警
8. **日志管理**：轮转、清理、归档策略

通过遵循这些最佳实践，可以构建一个高效、可靠、可维护的日志架构，为系统的可观测性和问题诊断提供有力支持。