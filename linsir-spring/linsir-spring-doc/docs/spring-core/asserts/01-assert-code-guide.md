# 断言工具模块代码说明文档

## 1. 代码结构

```
src/main/java/com/linsir/spring/framework/spring_core/asserts/
│
└── Assert.java    # 断言工具核心类
```

## 2. Assert 类详解

### 2.1 类定义

```java
/**
 * 断言工具类
 * 
 * 提供一系列静态方法用于参数校验和状态检查，帮助消除样板代码，
 * 统一异常类型，提高代码可读性。
 * 
 * 设计特点：
 * 1. 所有方法都是静态的，无需实例化
 * 2. 类被声明为 final，防止被继承
 * 3. 私有构造器强制使用静态方法
 * 4. 条件不满足时立即抛出标准异常
 */
public final class Assert {
    
    private Assert() {
        throw new AssertionError("Assert 类不能实例化");
    }
    // ...
}
```

**设计要点**：
- `final` 修饰符防止类被继承
- 私有构造器防止实例化
- 构造器中抛出 AssertionError 防止反射创建实例

### 2.2 对象断言方法

#### notNull - 断言对象不为 null

```java
/**
 * 断言对象不为 null
 * 
 * @param object 要检查的对象
 * @param message 断言失败时的错误消息
 * @throws IllegalArgumentException 如果对象为 null
 */
public static void notNull(Object object, String message) {
    if (object == null) {
        throw new IllegalArgumentException(message);
    }
}

/**
 * 断言对象不为 null（延迟消息计算版本）
 * 
 * @param object 要检查的对象
 * @param messageSupplier 错误消息提供者
 * @throws IllegalArgumentException 如果对象为 null
 */
public static void notNull(Object object, Supplier<String> messageSupplier) {
    if (object == null) {
        throw new IllegalArgumentException(nullSafeGet(messageSupplier));
    }
}
```

**使用示例**：

```java
// 基础版本
Assert.notNull(user, "User must not be null");

// 延迟计算版本（推荐用于复杂消息）
Assert.notNull(user, () -> "User " + userId + " not found in database");
```

**性能对比**：

```java
// 方式1：直接字符串拼接（每次调用都执行）
Assert.notNull(user, "User " + userId + " not found in " + databaseName);
// 即使 user 不为 null，字符串拼接也会执行

// 方式2：使用 Supplier（仅在断言失败时执行）✓ 推荐
Assert.notNull(user, () -> "User " + userId + " not found in " + databaseName);
// 只有 user 为 null 时，Supplier 才会被调用
```

#### isNull - 断言对象为 null

```java
public static void isNull(Object object, String message) {
    if (object != null) {
        throw new IllegalArgumentException(message);
    }
}
```

**使用场景**：验证某个对象应该为 null

```java
// 验证新创建的对象还没有被赋值
Assert.isNull(user.getId(), "New user should not have an ID");
```

### 2.3 字符串断言方法

#### hasText - 断言字符串包含非空白字符

```java
public static void hasText(String text, String message) {
    if (!hasText(text)) {
        throw new IllegalArgumentException(message);
    }
}

private static boolean hasText(String text) {
    if (text == null || text.isEmpty()) {
        return false;
    }
    for (int i = 0; i < text.length(); i++) {
        if (!Character.isWhitespace(text.charAt(i))) {
            return true;
        }
    }
    return false;
}
```

**验证规则**：
- null → 失败
- "" (空字符串) → 失败
- "   " (仅空白字符) → 失败
- "hello" → 通过
- "  hello  " → 通过

**使用示例**：

```java
// 验证用户名不为空
Assert.hasText(username, "Username must not be empty");

// 验证邮箱不为空
Assert.hasText(email, "Email must not be empty");
```

#### hasLength - 断言字符串有长度

```java
public static void hasLength(String text, String message) {
    if (text == null || text.isEmpty()) {
        throw new IllegalArgumentException(message);
    }
}
```

**与 hasText 的区别**：

| 输入 | hasText | hasLength |
|------|---------|-----------|
| null | ❌ 失败 | ❌ 失败 |
| "" | ❌ 失败 | ❌ 失败 |
| "   " | ❌ 失败 | ✅ 通过 |
| "hello" | ✅ 通过 | ✅ 通过 |

#### doesNotContain - 断言字符串不包含子串

```java
public static void doesNotContain(String textToSearch, String substring, String message) {
    if (hasText(textToSearch) && hasText(substring) && textToSearch.contains(substring)) {
        throw new IllegalArgumentException(message);
    }
}
```

**使用示例**：

```java
// 验证用户名不包含特殊字符
Assert.doesNotContain(username, "<script>", "Username must not contain script tags");

// 验证密码不包含空格
Assert.doesNotContain(password, " ", "Password must not contain spaces");
```

### 2.4 布尔断言方法

#### isTrue / isFalse

```java
public static void isTrue(boolean expression, String message) {
    if (!expression) {
        throw new IllegalArgumentException(message);
    }
}

public static void isFalse(boolean expression, String message) {
    if (expression) {
        throw new IllegalArgumentException(message);
    }
}
```

**使用示例**：

```java
// 验证年龄范围
Assert.isTrue(age >= 0 && age <= 150, "Age must be between 0 and 150");

// 验证数值为正
Assert.isTrue(amount > 0, "Amount must be positive");

// 验证状态为 false
Assert.isFalse(isDeleted, "Record is already deleted");
```

### 2.5 数组断言方法

#### notEmpty (数组)

```java
public static void notEmpty(Object[] array, String message) {
    if (array == null || array.length == 0) {
        throw new IllegalArgumentException(message);
    }
}
```

#### noNullElements

```java
public static void noNullElements(Object[] array, String message) {
    if (array != null) {
        for (Object element : array) {
            if (element == null) {
                throw new IllegalArgumentException(message);
            }
        }
    }
}
```

**注意**：`noNullElements` 对 null 数组不抛出异常，这与 `notEmpty` 不同。

**使用示例**：

```java
// 验证数组不为空
Assert.notEmpty(roles, "User must have at least one role");

// 验证数组不包含 null
Assert.noNullElements(roles, "Roles must not contain null");
```

### 2.6 集合断言方法

#### notEmpty (Collection)

```java
public static void notEmpty(Collection<?> collection, String message) {
    if (collection == null || collection.isEmpty()) {
        throw new IllegalArgumentException(message);
    }
}
```

**使用示例**：

```java
List<OrderItem> items = order.getItems();
Assert.notEmpty(items, "Order must have at least one item");
```

### 2.7 Map 断言方法

#### notEmpty (Map)

```java
public static void notEmpty(Map<?, ?> map, String message) {
    if (map == null || map.isEmpty()) {
        throw new IllegalArgumentException(message);
    }
}
```

**使用示例**：

```java
Map<String, Object> params = request.getParams();
Assert.notEmpty(params, "Request params must not be empty");
```

### 2.8 类型断言方法

#### isInstanceOf

```java
public static void isInstanceOf(Class<?> type, Object obj, String message) {
    notNull(type, "Type to check against must not be null");
    if (!type.isInstance(obj)) {
        throw new IllegalArgumentException(message);
    }
}
```

**使用示例**：

```java
// 验证组件类型
Assert.isInstanceOf(Service.class, component, "Component must be a Service");

// 安全地进行类型转换
Assert.isInstanceOf(String.class, value, "Value must be a string");
String str = (String) value;
```

#### isAssignable

```java
public static void isAssignable(Class<?> superType, Class<?> subType, String message) {
    notNull(superType, "Super type to check against must not be null");
    if (subType == null || !superType.isAssignableFrom(subType)) {
        throw new IllegalArgumentException(message);
    }
}
```

**使用示例**：

```java
// 验证类继承关系
Assert.isAssignable(Number.class, Integer.class, "Integer must extend Number");

// 验证接口实现
Assert.isAssignable(Serializable.class, MyClass.class, "MyClass must implement Serializable");
```

### 2.9 状态断言方法

#### state

```java
public static void state(boolean expression, String message) {
    if (!expression) {
        throw new IllegalStateException(message);
    }
}
```

**与 isTrue 的区别**：

| 特性 | isTrue | state |
|------|--------|-------|
| 异常类型 | IllegalArgumentException | IllegalStateException |
| 使用场景 | 参数校验 | 状态校验 |
| 语义 | "参数错误" | "状态错误" |

**使用示例**：

```java
public class Order {
    private OrderStatus status;
    
    public void submit() {
        // 状态校验（不是参数校验）
        Assert.state(status == OrderStatus.CREATED, 
            "Order can only be submitted from CREATED status");
        
        this.status = OrderStatus.SUBMITTED;
    }
}
```

## 3. 工具方法

### nullSafeGet

```java
private static String nullSafeGet(Supplier<String> messageSupplier) {
    return messageSupplier != null ? messageSupplier.get() : null;
}
```

**作用**：安全地从 Supplier 获取消息，防止 NPE。

## 4. 使用最佳实践

### 4.1 错误消息规范

```java
// ✅ 好的错误消息 - 清晰、具体、可操作
Assert.notNull(userId, "User ID must not be null");
Assert.hasText(email, "Email must not be empty; please provide a valid email address");
Assert.isTrue(age >= 18, "User must be at least 18 years old; current age: " + age);

// ❌ 避免的错误消息 - 模糊、无意义
Assert.notNull(userId, "error");  // 太模糊
Assert.notNull(userId, "userId is null");  // 只是重复显而易见的事实
```

### 4.2 性能优化

```java
// ✅ 推荐：使用 Supplier 延迟计算
Assert.notNull(user, () -> "User " + expensiveOperation() + " not found");

// ❌ 避免：直接调用昂贵操作
Assert.notNull(user, "User " + expensiveOperation() + " not found");
```

### 4.3 组合使用

```java
public void createUser(String username, String email, Integer age, String[] roles) {
    // 参数校验
    Assert.hasText(username, "Username must not be empty");
    Assert.hasText(email, "Email must not be empty");
    Assert.notNull(age, "Age must not be null");
    Assert.isTrue(age >= 0 && age <= 150, "Age must be between 0 and 150");
    Assert.notEmpty(roles, "User must have at least one role");
    Assert.noNullElements(roles, "Roles must not contain null");
    
    // 业务逻辑...
}
```

## 5. 扩展建议

### 5.1 业务断言类

```java
public final class BusinessAssert {
    
    private BusinessAssert() {}
    
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
}
```

### 5.2 自定义异常

```java
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}

public final class BusinessAssert {
    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new BusinessException(message);
        }
    }
}
```

## 6. 总结

Assert 类通过提供语义化的静态方法，帮助开发者：

1. **消除样板代码**：避免重复的 if-throw 代码
2. **统一异常类型**：使用标准的 Java 异常
3. **提高可读性**：方法名清晰表达意图
4. **支持延迟计算**：使用 Supplier 优化性能

在实际项目中，建议根据业务需求封装业务特定的断言方法，形成统一的参数校验规范。
