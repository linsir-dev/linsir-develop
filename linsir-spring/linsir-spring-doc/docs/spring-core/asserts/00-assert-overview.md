# 断言工具 (Assert) 模块概述

## 1. 模块定位

### 1.1 核心能力矩阵定位

| 能力 | 核心类 | 解决的问题 | 使用频率 | 学习优先级 |
|------|--------|-----------|----------|-----------|
| **断言工具** | `Assert` | 参数校验样板代码 | 高 | 高 |

### 1.2 模块价值

断言工具是 Spring Framework 中最基础、最常用的工具类之一，它提供了一系列静态方法用于参数校验和状态检查，帮助开发者：

- **消除样板代码**: 避免重复编写 `if (obj == null) throw new IllegalArgumentException(...)` 这样的代码
- **统一异常类型**: 使用标准的 Spring 异常体系（`IllegalArgumentException`、`IllegalStateException` 等）
- **提高代码可读性**: 通过语义化的方法名（`notNull`、`hasText`、`isTrue` 等）使代码意图更清晰
- **简化调试**: 提供详细的错误消息，便于快速定位问题

### 1.3 解决的问题

```java
// 传统方式 - 样板代码多
public void processUser(User user) {
    if (user == null) {
        throw new IllegalArgumentException("User must not be null");
    }
    if (user.getName() == null || user.getName().trim().isEmpty()) {
        throw new IllegalArgumentException("User name must not be empty");
    }
    if (user.getAge() < 0 || user.getAge() > 150) {
        throw new IllegalArgumentException("User age must be between 0 and 150");
    }
    // 业务逻辑...
}

// 使用 Assert 工具 - 简洁清晰
public void processUser(User user) {
    Assert.notNull(user, "User must not be null");
    Assert.hasText(user.getName(), "User name must not be empty");
    Assert.isTrue(user.getAge() >= 0 && user.getAge() <= 150, 
        "User age must be between 0 and 150");
    // 业务逻辑...
}
```

## 2. 核心概念

### 2.1 断言的本质

断言是一种**防御式编程**技术，用于在代码执行前验证前置条件。如果条件不满足，立即抛出异常终止执行，防止错误扩散。

```
┌─────────────────────────────────────────────────────────────┐
│                      断言执行流程                            │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│   调用断言方法 ──→ 条件检查 ──→ 条件满足?                    │
│                        │                                    │
│                        ↓                                    │
│                    ┌─────────┐                              │
│              是 ←──│  判断   │──→ 否                        │
│                    └─────────┘                              │
│                        │                                    │
│                        ↓                                    │
│              ┌─────────────────┐                            │
│              │ 抛出异常终止执行 │                            │
│              │ (带详细错误信息) │                            │
│              └─────────────────┘                            │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 2.2 断言分类

Spring 的 `Assert` 类按照校验目标分为以下几类：

| 分类 | 方法示例 | 用途 |
|------|----------|------|
| **对象断言** | `notNull`, `isNull` | 验证对象是否为 null |
| **字符串断言** | `hasText`, `hasLength`, `doesNotContain` | 验证字符串内容 |
| **布尔断言** | `isTrue`, `isFalse` | 验证布尔条件 |
| **集合断言** | `notEmpty`, `noNullElements` | 验证集合状态 |
| **类型断言** | `isInstanceOf`, `isAssignable` | 验证类型关系 |
| **状态断言** | `state` | 验证对象状态 |

### 2.3 异常体系

```
┌─────────────────────────────────────────────────────────────┐
│                     断言异常体系                             │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  RuntimeException (Java 标准)                               │
│       │                                                     │
│       ├── IllegalArgumentException  ←── 参数校验失败        │
│       │       └── Assert.notNull() 等                      │
│       │                                                     │
│       └── IllegalStateException     ←── 状态校验失败        │
│               └── Assert.state()                           │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

## 3. 架构设计

### 3.1 类结构

```
org.springframework.util
│
└── Assert (final class)
    │
    ├── 对象断言方法
    │   ├── notNull(Object object, String message)
    │   ├── notNull(Object object, Supplier<String> messageSupplier)
    │   └── isNull(Object object, String message)
    │
    ├── 字符串断言方法
    │   ├── hasText(String text, String message)
    │   ├── hasLength(String text, String message)
    │   └── doesNotContain(String textToSearch, String substring, String message)
    │
    ├── 布尔断言方法
    │   ├── isTrue(boolean expression, String message)
    │   └── isFalse(boolean expression, String message)
    │
    ├── 集合断言方法
    │   ├── notEmpty(Object[] array, String message)
    │   ├── notEmpty(Collection<?> collection, String message)
    │   ├── notEmpty(Map<?, ?> map, String message)
    │   └── noNullElements(Object[] array, String message)
    │
    ├── 类型断言方法
    │   ├── isInstanceOf(Class<?> type, Object obj, String message)
    │   └── isAssignable(Class<?> superType, Class<?> subType, String message)
    │
    └── 状态断言方法
        └── state(boolean expression, String message)
```

### 3.2 方法签名设计

每个断言方法都遵循统一的设计模式：

```java
// 基础版本 - 直接传入消息字符串
public static void notNull(Object object, String message)

// 延迟计算版本 - 使用 Supplier 避免不必要的字符串拼接
public static void notNull(Object object, Supplier<String> messageSupplier)

// 实际使用对比
Assert.notNull(user, "User " + userId + " not found");  // 总是执行字符串拼接
Assert.notNull(user, () -> "User " + userId + " not found");  // 仅失败时执行
```

### 3.3 设计原则

1. **静态方法**: 所有方法都是静态的，无需实例化
2. **final 类**: 防止被继承和篡改
3. **私有构造器**: 强制使用静态方法
4. **立即失败**: 条件不满足时立即抛出异常
5. **信息丰富**: 错误消息包含足够上下文信息

## 4. 使用场景

### 4.1 公共 API 参数校验

```java
@Service
public class UserService {
    
    public User createUser(String name, String email, Integer age) {
        // 参数前置校验
        Assert.hasText(name, "Name must not be empty");
        Assert.hasText(email, "Email must not be empty");
        Assert.notNull(age, "Age must not be null");
        Assert.isTrue(age >= 0 && age <= 150, "Age must be between 0 and 150");
        
        // 业务逻辑
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setAge(age);
        return userRepository.save(user);
    }
}
```

### 4.2 配置属性校验

```java
@Configuration
public class AppConfig {
    
    @Value("${app.name}")
    private String appName;
    
    @Value("${app.max-connections:100}")
    private int maxConnections;
    
    @PostConstruct
    public void validate() {
        Assert.hasText(appName, "app.name must be configured");
        Assert.isTrue(maxConnections > 0, "app.max-connections must be positive");
    }
}
```

### 4.3 对象状态校验

```java
public class Order {
    private OrderStatus status;
    private List<OrderItem> items;
    
    public void submit() {
        // 状态校验
        Assert.state(status == OrderStatus.CREATED, 
            "Order can only be submitted from CREATED status");
        
        // 数据完整性校验
        Assert.notEmpty(items, "Order must have at least one item");
        Assert.noNullElements(items.toArray(), "Order items must not contain null");
        
        this.status = OrderStatus.SUBMITTED;
    }
}
```

### 4.4 类型安全检查

```java
public void processComponent(Object component) {
    // 类型断言
    Assert.isInstanceOf(Service.class, component, 
        "Component must be a Service");
    
    Service service = (Service) component;
    service.execute();
}
```

## 5. 最佳实践

### 5.1 错误消息规范

```java
// 好的错误消息 - 清晰、具体、可操作
Assert.notNull(userId, "User ID must not be null");
Assert.hasText(email, "Email must not be empty; please provide a valid email address");
Assert.isTrue(age >= 18, "User must be at least 18 years old; current age: " + age);

// 避免的错误消息 - 模糊、无意义
Assert.notNull(userId, "error");  // ❌ 太模糊
Assert.notNull(userId, "userId is null");  // ❌ 只是重复显而易见的事实
```

### 5.2 性能优化

```java
// 方式1：直接字符串拼接（每次调用都执行）
Assert.notNull(user, "User " + userId + " not found in database");

// 方式2：使用 Supplier（仅在断言失败时执行）✓ 推荐
Assert.notNull(user, () -> "User " + userId + " not found in database");

// 方式3：复杂逻辑使用独立方法
Assert.notNull(user, () -> buildUserNotFoundMessage(userId));

private String buildUserNotFoundMessage(Long userId) {
    // 复杂的消息构建逻辑
    return String.format("User [%d] not found in %s", userId, databaseName);
}
```

### 5.3 与 Bean Validation 的对比

| 特性 | Assert | Bean Validation (JSR-303) |
|------|--------|---------------------------|
| 使用场景 | 编程式校验 | 声明式校验 |
| 侵入性 | 代码侵入 | 注解驱动 |
| 灵活性 | 高（任意逻辑） | 中（预定义约束）|
| 适用层级 | 方法内部 | 方法参数、字段 |
| 错误处理 | 立即抛出异常 | 收集所有错误 |
| 典型使用 | 公共 API、内部校验 | DTO、实体类 |

```java
// Assert - 编程式校验
public void processOrder(Order order) {
    Assert.notNull(order, "Order must not be null");
    Assert.hasText(order.getOrderNo(), "Order number must not be empty");
    Assert.notEmpty(order.getItems(), "Order must have items");
}

// Bean Validation - 声明式校验
public void processOrder(@Valid @NotNull Order order) {
    // 校验通过后才执行
}

public class Order {
    @NotBlank
    private String orderNo;
    
    @NotEmpty
    private List<OrderItem> items;
}
```

## 6. 扩展思考

### 6.1 自定义断言

虽然 Spring 的 `Assert` 类是 final 的，但可以通过组合方式扩展：

```java
public final class BusinessAssert {
    
    private BusinessAssert() {}
    
    // 业务特定的断言
    public static void validEmail(String email) {
        Assert.hasText(email, "Email must not be empty");
        Assert.isTrue(email.matches("^[A-Za-z0-9+_.-]+@(.+)$"), 
            "Invalid email format: " + email);
    }
    
    public static void validPhone(String phone) {
        Assert.hasText(phone, "Phone must not be empty");
        Assert.isTrue(phone.matches("^1[3-9]\\d{9}$"), 
            "Invalid phone format: " + phone);
    }
    
    public static void validIdCard(String idCard) {
        Assert.hasText(idCard, "ID card must not be empty");
        Assert.isTrue(idCard.length() == 18, 
            "ID card must be 18 characters");
    }
}
```

### 6.2 与日志结合

```java
public class LoggingAssert {
    
    private static final Logger logger = LoggerFactory.getLogger(LoggingAssert.class);
    
    public static void notNull(Object object, String message) {
        if (object == null) {
            logger.error("Assertion failed: {}", message);
            throw new IllegalArgumentException(message);
        }
    }
}
```

## 7. 参考资源

- [Spring Framework - Assert 源码](https://github.com/spring-projects/spring-framework/blob/main/spring-core/src/main/java/org/springframework/util/Assert.java)
- [Spring Framework 文档 - Core Utilities](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#core-util)
- [Effective Java - 第 49 条：检查参数有效性](https://www.oracle.com/java/technologies/effective-java.html)

## 8. 下一步

接下来将基于本文档实现断言工具的核心代码：

1. **Assert 核心类实现** - 实现所有断言方法
2. **自定义异常类** - 可选的自定义断言异常
3. **扩展断言工具** - 业务特定的断言方法
4. **完整测试覆盖** - 验证所有断言场景
