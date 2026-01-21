# Java 异常示例

本目录包含 Java 异常的示例代码，展示了 `Error` 和 `Exception` 的功能、区别和使用方法。

## 目录结构
```
exceptiondemo/
├── CustomException.java         // 自定义检查型异常
├── CustomRuntimeException.java   // 自定义运行时异常
├── ExceptionDemo.java            // 异常演示类
└── ExceptionTest.java            // 测试类
```

## 示例代码功能

### 1. 自定义异常

#### 检查型异常 (`CustomException.java`)
- 继承自 `Exception` 的检查型异常
- 包含多个构造方法，支持异常消息和异常链
- 需要显式捕获或声明抛出

#### 运行时异常 (`CustomRuntimeException.java`)
- 继承自 `RuntimeException` 的非检查型异常
- 包含多个构造方法，支持异常消息和异常链
- 无需显式捕获或声明抛出

### 2. 异常演示类 (`ExceptionDemo.java`)

#### 演示检查型异常
- 展示如何定义和抛出检查型异常
- 方法声明中使用 `throws` 关键字

#### 演示非检查型异常
- 展示如何定义和抛出运行时异常
- 无需在方法声明中声明

#### 演示系统异常
- 展示常见的系统异常，如数组越界异常
- 系统异常属于非检查型异常

#### 演示错误（Error）
- 展示如何触发和捕获错误
- 如尝试创建超大数组导致的 `OutOfMemoryError`

#### 演示异常链
- 展示如何包装和传递原始异常
- 通过 `getCause()` 方法获取原始异常

#### 演示 try-catch-finally
- 展示异常处理的基本结构
- 演示 finally 块的执行特性

#### 演示多重 catch
- 展示如何处理多种类型的异常
- 异常捕获的顺序问题

### 3. 测试类 (`ExceptionTest.java`)
- 完整演示所有异常类型的使用
- 包括异常捕获、异常链处理
- 展示 Error 和 Exception 的区别
- 提供详细的运行结果和总结

## 异常体系总结

### Exception（异常）
- **检查型异常**：需要显式捕获或声明抛出
  - 如 `IOException`、`SQLException` 等
  - 继承自 `Exception`，但不是 `RuntimeException` 的子类
- **非检查型异常**：无需显式处理
  - 如 `NullPointerException`、`ArrayIndexOutOfBoundsException` 等
  - 继承自 `RuntimeException`

### Error（错误）
- 严重问题，程序一般无法处理
- 如 `OutOfMemoryError`、`StackOverflowError` 等
- 继承自 `Error` 类
- 通常不应该捕获，而是通过程序设计避免

## 异常处理最佳实践
1. **只捕获你能处理的异常**
2. **使用具体的异常类型**，而不是通用的 `Exception`
3. **合理使用异常链**，保留原始异常信息
4. **不要忽略异常**，至少记录日志
5. **使用 try-with-resources 处理资源**
6. **避免在 finally 块中抛出异常**

## 运行示例
执行 `ExceptionTest.java` 类的 `main` 方法，即可看到完整的异常示例运行结果，包括各种异常的触发、捕获和处理过程。
