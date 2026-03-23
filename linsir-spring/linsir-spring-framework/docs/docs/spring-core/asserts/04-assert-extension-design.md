# 断言工具模块功能扩展设计文档

## 1. 概述

本文档针对断言工具模块提出深入的设计与封装建议，包括高级功能扩展、性能优化、架构改进等方面。

## 2. 当前架构分析

### 2.1 现有架构优势

- **接口简洁**: 静态方法调用，使用简单
- **覆盖全面**: 支持对象、字符串、集合、类型等多种断言
- **性能良好**: 基础实现高效，支持延迟计算
- **异常标准**: 使用 Java 标准异常体系

### 2.2 待改进点

- 缺少链式断言支持
- 缺少批量断言支持
- 缺少断言结果收集
- 缺少自定义异常支持
- 缺少断言模板和复用机制

## 3. 功能扩展设计

### 3.1 链式断言 (AssertChain)

#### 3.1.1 设计意图

支持多个断言链式调用，统一收集错误信息，避免逐个检查。

#### 3.1.2 核心类设计

```java
/**
 * 链式断言构建器
 */
public class AssertChain {
    
    private final List<String> errors = new ArrayList<>();
    private boolean failFast = false;
    
    /**
     * 创建链式断言
     */
    public static AssertChain start() {
        return new AssertChain();
    }
    
    /**
     * 启用快速失败模式
     */
    public AssertChain failFast() {
        this.failFast = true;
        return this;
    }
    
    /**
     * 添加 notNull 断言
     */
    public AssertChain notNull(Object object, String message) {
        if (object == null) {
            errors.add(message);
            if (failFast) {
                throw new IllegalArgumentException(message);
            }
        }
        return this;
    }
    
    /**
     * 添加 hasText 断言
     */
    public AssertChain hasText(String text, String message) {
        if (text == null || text.trim().isEmpty()) {
            errors.add(message);
            if (failFast) {
                throw new IllegalArgumentException(message);
            }
        }
        return this;
    }
    
    /**
     * 添加 isTrue 断言
     */
    public AssertChain isTrue(boolean expression, String message) {
        if (!expression) {
            errors.add(message);
            if (failFast) {
                throw new IllegalArgumentException(message);
            }
        }
        return this;
    }
    
    /**
     * 验证所有断言
     * 
     * @throws MultiAssertException 如果有任何断言失败
     */
    public void verify() {
        if (!errors.isEmpty()) {
            throw new MultiAssertException(errors);
        }
    }
    
    /**
     * 验证并返回结果
     */
    public AssertResult verifyAndGet() {
        return new AssertResult(errors.isEmpty(), errors);
    }
}

/**
 * 多断言异常
 */
public class MultiAssertException extends IllegalArgumentException {
    
    private final List<String> errors;
    
    public MultiAssertException(List<String> errors) {
        super("Validation failed with " + errors.size() + " error(s): " + String.join(", ", errors));
        this.errors = new ArrayList<>(errors);
    }
    
    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }
}

/**
 * 断言结果
 */
public class AssertResult {
    
    private final boolean success;
    private final List<String> errors;
    
    public AssertResult(boolean success, List<String> errors) {
        this.success = success;
        this.errors = new ArrayList<>(errors);
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }
}
```

#### 3.1.3 使用示例

```java
// 传统方式 - 逐个检查，遇到第一个错误就停止
public void createUser(String username, String email, Integer age) {
    Assert.hasText(username, "Username must not be empty");
    Assert.hasText(email, "Email must not be empty");
    Assert.notNull(age, "Age must not be null");
    Assert.isTrue(age >= 0 && age <= 150, "Age must be between 0 and 150");
}

// 链式断言 - 收集所有错误
public void createUser(String username, String email, Integer age) {
    AssertChain.start()
        .hasText(username, "Username must not be empty")
        .hasText(email, "Email must not be empty")
        .notNull(age, "Age must not be null")
        .isTrue(age != null && age >= 0 && age <= 150, "Age must be between 0 and 150")
        .verify();
}

// 快速失败模式
public void createUser(String username, String email, Integer age) {
    AssertChain.start()
        .failFast()
        .hasText(username, "Username must not be empty")
        .hasText(email, "Email must not be empty")
        .verify();
}

// 获取详细结果
AssertResult result = AssertChain.start()
    .hasText(username, "Username must not be empty")
    .hasText(email, "Email must not be empty")
    .verifyAndGet();

if (result.hasErrors()) {
    for (String error : result.getErrors()) {
        System.out.println("Error: " + error);
    }
}
```

### 3.2 断言模板 (AssertTemplate)

#### 3.2.1 设计意图

预定义常用的断言组合，提高代码复用性。

#### 3.2.2 核心类设计

```java
/**
 * 断言模板
 */
public class AssertTemplate {
    
    /**
     * 用户创建参数模板
     */
    public static void validUserCreate(String username, String email, Integer age) {
        AssertChain.start()
            .hasText(username, "Username must not be empty")
            .hasText(email, "Email must not be empty")
            .notNull(age, "Age must not be null")
            .isTrue(age >= 0 && age <= 150, "Age must be between 0 and 150")
            .verify();
    }
    
    /**
     * 订单提交参数模板
     */
    public static void validOrderSubmit(List<OrderItem> items, String orderStatus) {
        AssertChain.start()
            .notEmpty(items, "Order must have at least one item")
            .hasText(orderStatus, "Order status must not be empty")
            .verify();
    }
    
    /**
     * 分页查询参数模板
     */
    public static void validPageQuery(Integer pageNum, Integer pageSize) {
        AssertChain.start()
            .notNull(pageNum, "Page number must not be null")
            .notNull(pageSize, "Page size must not be null")
            .isTrue(pageNum > 0, "Page number must be positive")
            .isTrue(pageSize > 0 && pageSize <= 1000, "Page size must be between 1 and 1000")
            .verify();
    }
}
```

#### 3.2.3 使用示例

```java
@Service
public class UserService {
    
    public User createUser(String username, String email, Integer age) {
        // 使用模板进行参数校验
        AssertTemplate.validUserCreate(username, email, age);
        
        // 业务逻辑...
    }
}

@Service
public class OrderService {
    
    public void submitOrder(Order order) {
        // 使用模板进行参数校验
        AssertTemplate.validOrderSubmit(order.getItems(), order.getStatus());
        
        // 业务逻辑...
    }
}
```

### 3.3 自定义异常支持

#### 3.3.1 设计意图

支持抛出自定义异常，便于上层业务处理。

#### 3.3.2 核心类设计

```java
/**
 * 可定制异常的断言
 */
public class CustomAssert {
    
    /**
     * 断言对象不为 null，失败时抛出指定异常
     */
    public static <T extends RuntimeException> void notNull(
            Object object, 
            Supplier<T> exceptionSupplier) {
        if (object == null) {
            throw exceptionSupplier.get();
        }
    }
    
    /**
     * 断言字符串有文本，失败时抛出指定异常
     */
    public static <T extends RuntimeException> void hasText(
            String text, 
            Supplier<T> exceptionSupplier) {
        if (text == null || text.trim().isEmpty()) {
            throw exceptionSupplier.get();
        }
    }
    
    /**
     * 断言表达式为 true，失败时抛出指定异常
     */
    public static <T extends RuntimeException> void isTrue(
            boolean expression, 
            Supplier<T> exceptionSupplier) {
        if (!expression) {
            throw exceptionSupplier.get();
        }
    }
}

/**
 * 业务异常
 */
public class BusinessException extends RuntimeException {
    private final String errorCode;
    
    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}
```

#### 3.3.3 使用示例

```java
// 使用自定义异常
CustomAssert.notNull(user, 
    () -> new BusinessException("USER_NOT_FOUND", "User not found"));

CustomAssert.hasText(email, 
    () -> new BusinessException("INVALID_EMAIL", "Email must not be empty"));

// 在业务层统一处理
@ExceptionHandler(BusinessException.class)
public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
    ErrorResponse response = new ErrorResponse(e.getErrorCode(), e.getMessage());
    return ResponseEntity.badRequest().body(response);
}
```

### 3.4 注解驱动断言

#### 3.4.1 设计意图

通过注解实现声明式参数校验。

#### 3.4.2 注解设计

```java
/**
 * 非空注解
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotNull {
    String message() default "Parameter must not be null";
}

/**
 * 非空文本注解
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotEmpty {
    String message() default "Parameter must not be empty";
}

/**
 * 范围注解
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Range {
    int min() default Integer.MIN_VALUE;
    int max() default Integer.MAX_VALUE;
    String message() default "Parameter out of range";
}

/**
 * 断言切面
 */
@Aspect
@Component
public class AssertAspect {
    
    @Around("@within(assertValidated)")
    public Object around(ProceedingJoinPoint point, AssertValidated assertValidated) throws Throwable {
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        Object[] args = point.getArgs();
        
        // 检查参数注解
        for (int i = 0; i < method.getParameters().length; i++) {
            Parameter param = method.getParameters()[i];
            Object value = args[i];
            
            if (param.isAnnotationPresent(NotNull.class)) {
                NotNull annotation = param.getAnnotation(NotNull.class);
                Assert.notNull(value, annotation.message());
            }
            
            if (param.isAnnotationPresent(NotEmpty.class)) {
                NotEmpty annotation = param.getAnnotation(NotEmpty.class);
                if (value instanceof String) {
                    Assert.hasText((String) value, annotation.message());
                }
            }
            
            if (param.isAnnotationPresent(Range.class)) {
                Range annotation = param.getAnnotation(Range.class);
                if (value instanceof Number) {
                    int num = ((Number) value).intValue();
                    Assert.isTrue(num >= annotation.min() && num <= annotation.max(), 
                        annotation.message());
                }
            }
        }
        
        return point.proceed();
    }
}

/**
 * 启用断言校验
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AssertValidated {
}
```

#### 3.4.3 使用示例

```java
@Service
public class UserService {
    
    @AssertValidated
    public User createUser(
            @NotEmpty(message = "Username must not be empty") String username,
            @NotEmpty(message = "Email must not be empty") String email,
            @Range(min = 0, max = 150, message = "Age must be between 0 and 150") Integer age) {
        
        // 参数已通过注解校验，直接执行业务逻辑
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setAge(age);
        return userRepository.save(user);
    }
}
```

### 3.5 断言结果缓存

#### 3.5.1 设计意图

对于重复的断言，缓存结果以提高性能。

#### 3.5.2 核心类设计

```java
/**
 * 缓存断言
 */
public class CachedAssert {
    
    private final Map<String, Boolean> cache = new ConcurrentHashMap<>();
    
    /**
     * 带缓存的断言
     */
    public boolean check(String key, Supplier<Boolean> checker) {
        return cache.computeIfAbsent(key, k -> checker.get());
    }
    
    /**
     * 清除缓存
     */
    public void clear() {
        cache.clear();
    }
    
    /**
     * 清除指定 key 的缓存
     */
    public void clear(String key) {
        cache.remove(key);
    }
}
```

## 4. 性能优化设计

### 4.1 延迟计算优化

```java
/**
 * 优化的延迟计算
 */
public class OptimizedAssert {
    
    /**
     * 使用 Supplier 避免不必要的字符串拼接
     */
    public static void notNull(Object object, Supplier<String> messageSupplier) {
        if (object == null) {
            throw new IllegalArgumentException(messageSupplier.get());
        }
    }
    
    /**
     * 使用格式化字符串延迟计算
     */
    public static void notNull(Object object, String format, Object... args) {
        if (object == null) {
            throw new IllegalArgumentException(String.format(format, args));
        }
    }
}

// 使用示例
OptimizedAssert.notNull(user, "User %d not found in %s", userId, databaseName);
// 只有 user 为 null 时，才会执行 String.format
```

### 4.2 批量断言优化

```java
/**
 * 批量断言优化
 */
public class BatchAssert {
    
    /**
     * 批量检查集合元素
     */
    public static <T> void allMatch(Collection<T> collection, 
            Predicate<T> predicate, 
            String message) {
        if (collection != null) {
            for (T element : collection) {
                if (!predicate.test(element)) {
                    throw new IllegalArgumentException(message);
                }
            }
        }
    }
    
    /**
     * 批量检查并返回失败的元素
     */
    public static <T> List<T> findAllNotMatch(Collection<T> collection, 
            Predicate<T> predicate) {
        List<T> failures = new ArrayList<>();
        if (collection != null) {
            for (T element : collection) {
                if (!predicate.test(element)) {
                    failures.add(element);
                }
            }
        }
        return failures;
    }
}
```

## 5. 架构改进建议

### 5.1 模块化设计

```
asserts/
├── core/           # 核心断言
│   └── Assert.java
├── chain/          # 链式断言
│   ├── AssertChain.java
│   ├── AssertResult.java
│   └── MultiAssertException.java
├── template/       # 断言模板
│   └── AssertTemplate.java
├── custom/         # 自定义异常支持
│   └── CustomAssert.java
├── annotation/     # 注解驱动
│   ├── NotNull.java
│   ├── NotEmpty.java
│   ├── Range.java
│   └── AssertAspect.java
└── cache/          # 缓存支持
    └── CachedAssert.java
```

### 5.2 插件化架构

```java
/**
 * 断言插件接口
 */
public interface AssertPlugin {
    
    /**
     * 插件名称
     */
    String getName();
    
    /**
     * 初始化插件
     */
    void initialize();
    
    /**
     * 注册断言方法
     */
    void registerMethods(AssertRegistry registry);
}

/**
 * 断言注册表
 */
public class AssertRegistry {
    
    private final Map<String, AssertFunction> methods = new HashMap<>();
    
    public void register(String name, AssertFunction function) {
        methods.put(name, function);
    }
    
    public AssertFunction get(String name) {
        return methods.get(name);
    }
}
```

## 6. 监控与度量

### 6.1 断言使用统计

```java
/**
 * 断言统计
 */
public class AssertMetrics {
    
    private final Map<String, AtomicLong> successCount = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> failureCount = new ConcurrentHashMap<>();
    
    /**
     * 记录断言结果
     */
    public void record(String assertType, boolean success) {
        if (success) {
            successCount.computeIfAbsent(assertType, k -> new AtomicLong()).incrementAndGet();
        } else {
            failureCount.computeIfAbsent(assertType, k -> new AtomicLong()).incrementAndGet();
        }
    }
    
    /**
     * 获取统计报告
     */
    public AssertStatistics getStatistics() {
        return new AssertStatistics(successCount, failureCount);
    }
}
```

## 7. 实施路线图

### 7.1 短期（1-2 周）

- [ ] 实现 AssertChain 链式断言
- [ ] 实现 AssertTemplate 断言模板
- [ ] 添加更多边界条件测试

### 7.2 中期（1 个月）

- [ ] 实现 CustomAssert 自定义异常支持
- [ ] 实现注解驱动断言
- [ ] 添加性能测试

### 7.3 长期（2-3 个月）

- [ ] 实现断言缓存机制
- [ ] 实现断言插件化架构
- [ ] 添加监控与度量系统

## 8. 总结

本文档提出了断言工具模块的深入设计与封装建议，包括：

1. **功能扩展**: 链式断言、断言模板、自定义异常、注解驱动、缓存机制
2. **性能优化**: 延迟计算、批量断言优化
3. **架构改进**: 模块化设计、插件化架构
4. **监控度量**: 断言使用统计

建议根据实际业务需求，分阶段实施这些扩展功能。
